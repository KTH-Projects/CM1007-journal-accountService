package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.entity.Message;
import com.example.journalaccountservice.core.service.KeycloakService;
import com.example.journalaccountservice.core.service.interfaces.IAccountsService;
import com.example.journalaccountservice.core.service.interfaces.IChatService;
import com.example.journalaccountservice.core.service.interfaces.ICookieService;
import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.view.dto.SignUpRequest;
import com.example.journalaccountservice.view.entity.AccountView;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin(origins = {"http://localhost:8080","http://localhost:3000"})
@RestController
@RequestMapping("/account")
public class AccountController {


    private final IAccountsService accountService;
    private final ICookieService cookieService;
    private final IJournalService journalService;
    private final IChatService chatService;
    private final KeycloakService keycloakService;


    @Autowired
    public AccountController(IAccountsService accountService, ICookieService cookieService, IJournalService journalService, IChatService chatService, KeycloakService keycloakService) {
        this.accountService = accountService;
        this.cookieService = cookieService;
        this.journalService = journalService;
        this.chatService = chatService;
        this.keycloakService = keycloakService;
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("")
    public ResponseEntity<List<Account>> getAll(@CookieValue("userCookieID") String userSessionID)
    {
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.findAll());

    }

    @PreAuthorize("hasRole('staff')")
    @GetMapping("/userEmail")
    public ResponseEntity<AccountView> getByEmail(@RequestParam String email, @CookieValue("userCookieID") String userCookieID) {
        if(!cookieService.isDoctorOrStaff(userCookieID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Account account = accountService.findByEmail(email);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AccountView.convert(account));
    }

    @PreAuthorize("hasRole('user')")
    @PostMapping("/send")
    public ResponseEntity<Message> sendChat(@RequestParam String toEmail,@RequestParam String message,@CookieValue("userCookieID") String userCookieID){
        Account fromAcc = cookieService.findAccountByCookie(userCookieID);
        Account toAcc = accountService.findByEmail(toEmail);
        if(toAcc==null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Message msg = chatService.postChat(toAcc,fromAcc,message);
        if(msg==null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(msg,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('user')")
    @GetMapping("/message")
    public ResponseEntity<List<Message>> getMyMessages(@CookieValue("userCookieID") String userCookieID){

        Account fromAcc = cookieService.findAccountByCookie(userCookieID);
        List<Message> messages = chatService.getChatsFromAccount(fromAcc);
        if(messages==null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(messages,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('user')")
    @GetMapping("/message/chat")
    public ResponseEntity<List<Message>> getMessagesFromAccAndToAcc(@RequestParam String toEmail, @CookieValue("userCookieID") String userCookieID){
        Account fromAcc = cookieService.findAccountByCookie(userCookieID);
        Account toAccount = accountService.findByEmail(toEmail);
        if(fromAcc == null || toAccount == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        List<Message> messages = chatService.getChatsFromAccountANDtoAccount(toAccount,fromAcc);
        if(messages==null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(messages,HttpStatus.CREATED);
    }

    @PostMapping("/signup")
    public ResponseEntity<Account> signUp(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        try {
            if(keycloakService.createUser(signUpRequest.getAccount()) == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if(accountService.findByEmail(signUpRequest.getAccount().getEmail()) != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            String id = journalService.create(signUpRequest.getSignupDTO());
            if(id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            setIdForAccount(signUpRequest.getAccount(),id);
            Account acc = accountService.create(signUpRequest.getAccount());

            String cookieToken = cookieService.createCookie(acc);
            Cookie userCookieID = new Cookie("userCookieID", cookieToken);
            userCookieID.setPath("/");
            response.addCookie(userCookieID);

            return new ResponseEntity<>(acc, HttpStatus.CREATED);
        }
        catch (EntityExistsException e)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Account> login(@RequestBody Account accountLogin, HttpServletResponse response) {
        Account accountCore = accountService.findByEmail(accountLogin.getEmail());
        if (accountCore == null || ! accountLogin.getPassword().equals(accountCore.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }



        String cookieToken = cookieService.createCookie(accountCore);
        Cookie userCookieID = new Cookie("userCookieID", cookieToken);
        userCookieID.setPath("/");
        response.addCookie(userCookieID);

        return (ResponseEntity<Account>) keycloakService.authenticateUser(accountCore.getName(), accountLogin.getPassword());

    }

    private void setIdForAccount(Account account, String id){
        if(account.getRole().name().equals("doctor") || account.getRole().name().equals( "staff" ) )
            account.setStaffID(id);
        else
            account.setPatientID(id);

    }
}
