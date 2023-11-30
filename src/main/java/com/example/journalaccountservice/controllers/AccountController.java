package com.example.journalaccountservice.controllers;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.core.service.interfaces.IAccountsService;
import com.example.journalaccountservice.core.service.interfaces.ICookieService;
import com.example.journalaccountservice.core.service.interfaces.IJournalService;
import com.example.journalaccountservice.dto.SignUpDTO;
import com.example.journalaccountservice.dto.SignUpRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController {


    private final IAccountsService accountService;
    private final ICookieService cookieService;
    private final IJournalService journalService;
    //private final IChatService chatService;

    @Autowired
    public AccountController(IAccountsService accountService, ICookieService cookieService,
                             IJournalService journalService
                             ) {
        this.accountService = accountService;
        this.cookieService = cookieService;
        this.journalService = journalService;
        //this.chatService = chatService;
    }


    @GetMapping("")
    public ResponseEntity<List<Account>> getAll(@CookieValue("userSessionID") String userSessionID)
    {
        if(!cookieService.isValidCookie(userSessionID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.findAll());

    }

    @GetMapping("/{email}")
    public ResponseEntity<Account> getByEmail(@PathVariable String email,@CookieValue("userSessionID") String userSessionID) {
        if(!cookieService.isDoctorOrStaff(userSessionID)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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

    @PostMapping("/signup")
    public ResponseEntity<Account> signUp(@RequestBody SignUpRequest signUpRequest,
                                          HttpServletResponse response) {
        // Everyone can create account
        // Defaults to patient
        try {
            if(!journalService.create(signUpRequest.getSignupDTO()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            Account acc = accountService.create(signUpRequest.getAccount());

            String cookieToken = cookieService.createCookie(acc);
            Cookie userIdCookie = new Cookie("userSessionID", cookieToken);
            userIdCookie.setPath("/");
            response.addCookie(userIdCookie);

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

        String sessionToken = cookieService.createCookie(accountCore);
        Cookie userIdCookie = new Cookie("userSessionID", sessionToken);
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);
        return ResponseEntity.ok(accountCore);
    }
}
