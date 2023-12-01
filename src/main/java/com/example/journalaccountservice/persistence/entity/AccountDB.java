package com.example.journalaccountservice.persistence.entity;

import com.example.journalaccountservice.core.entity.Account;
import com.example.journalaccountservice.util.enums.Role;
import jakarta.persistence.*;


@Entity()
@Table(name = "account")
public class AccountDB {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cookie_id", referencedColumnName = "id")
    private CookieDB cookie;
    @Column(nullable = false)
    private Role role;

    private String staffID;
    private String patientID;

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountDB(String id, String email, String password, String name, Role role, String staffID, String patientID) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.staffID = staffID;
        this.patientID = patientID;
    }

    public static AccountDB convertFromCore(Account account){
        return new AccountDB(account.getId(),account.getEmail(),account.getPassword(), account.getName(),account.getRole());
    }
    public static AccountDB convertAllFromCore(Account account){ //TODO there is no cookie here
        return new AccountDB(
                account.getId(),
                account.getEmail(),
                account.getPassword(),
                account.getName(),
                account.getRole(),
                account.getStaffID(),
                account.getPatientID());
    }

    public AccountDB(String id, String email, String password, String name, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public AccountDB(String id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public AccountDB(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public AccountDB() {
        this.email = "email";
        this.password = "password";
        this.name = "name";
    }
    /*
    public boolean isPatient(String patientID) {
        return  !getPatientID().isEmpty() && getPatientID().equals(patientID);
    }

    public boolean isStaff(String staffID) {
        return !getStaffID().isEmpty() && getStaffID().equals(staffID);
    }

     */


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public CookieDB getCookie() {
        return cookie;
    }

    public void setCookie(CookieDB session) {
        this.cookie = session;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
