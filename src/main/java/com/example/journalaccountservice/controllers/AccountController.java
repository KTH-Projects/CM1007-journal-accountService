package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.entity.Message;
import com.example.journalaccountservice.core.service.interfaces.IAccountsService;
import com.example.journalaccountservice.core.service.interfaces.IChatService;
import com.example.journalaccountservice.core.service.interfaces.ICookieService;
import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.view.dto.SignUpRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:8080"})
@RestController
@RequestMapping("/account")
public class AccountController {


    private final IAccountsService accountService;
    private final ICookieService cookieService;
    private final IJournalService journalService;
    private final IChatService chatService;

    @Autowired
    public AccountController(IAccountsService accountService, ICookieService cookieService,
                             IJournalService journalService,
                             IChatService chatService
                             ) {
        this.accountService = accountService;
        this.cookieService = cookieService;
        this.journalService = journalService;
        this.chatService = chatService;
    }


    @GetMapping("")
    public ResponseEntity<List<Account>> getAll(@CookieValue("userCookieID") String userSessionID)
    {
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.findAll());

    }

    @GetMapping("/{email}")
    public ResponseEntity<Account> getByEmail(@PathVariable String email,@CookieValue("userCookieID") String userCookieID) {
        if(!cookieService.isDoctorOrStaff(userCookieID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Account account = accountService.findByEmail(email);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(account);
    }
        /*
    @GetMapping("/chats")
    public ResponseEntity<List<Chat>> getAllChats(HttpSession session,@CookieValue("userSessionID") String userSessionID){
        if(!cookieService.isValidSession(userSessionID)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(chatService.findAll());
    }

    @GetMapping("/chat")
    public ResponseEntity<List<Chat>> getAllAccountChats(HttpSession session,@CookieValue("userSessionID") String userSessionID){
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account fromAccount = cookieService.findAccountByCookie(userSessionID);
        return ResponseEntity.ok(chatService.findByAccountID(fromAccount.getId()));
    }

    @GetMapping("/chatTo")
    public ResponseEntity<List<Chat>> getChatConversation(@RequestParam String toEmail,@CookieValue("userSessionID") String userSessionID){
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account fromAccount = cookieService.findAccountByCookie(userSessionID);
        Account account = accountService.findByEmail(toEmail);
        if(account==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(chatService.findByMyAccount_IDAndToAccount_ID(fromAccount.getId(),account.getId()));
    }

    @PostMapping("/chat")
    public ResponseEntity<Chat> sendChat(@RequestParam String toEmail,@RequestParam String message, HttpSession session,@CookieValue("userSessionID") String userSessionID){
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Account account = cookieService.findAccountByCookie(userSessionID);
        if(account == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        String fromEmail = account.getEmail();
        Account toAccount = accountService.findByEmail(toEmail);
        Account fromAccount = accountService.findByEmail(fromEmail);
        return ResponseEntity.ok(chatService.createByEmail(toAccount,fromAccount,message));
    }

         */
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
    @GetMapping("/message")
    public ResponseEntity<List<Message>> getMyMessages(@CookieValue("userCookieID") String userCookieID){
        Account fromAcc = cookieService.findAccountByCookie(userCookieID);
        List<Message> messages = chatService.getChatsFromAccount(fromAcc);
        if(messages==null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(messages,HttpStatus.CREATED);
    }

    @GetMapping("/message/chat")
    public ResponseEntity<List<Message>> getMessagesFromAccAndToAcc(@RequestParam String toEmail,
                                                                    @CookieValue("userCookieID") String userCookieID){
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
    public ResponseEntity<Account> signUp(@RequestBody SignUpRequest signUpRequest,
                                          HttpServletResponse response) {
        try {
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

    private void setIdForAccount(Account account, String id){
        if(account.getRole().name().equals("doctor") || account.getRole().name().equals( "staff" ) )
            account.setStaffID(id);
        else
            account.setPatientID(id);

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
        return ResponseEntity.ok(accountCore);
    }
}
