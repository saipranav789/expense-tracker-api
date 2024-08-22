package com.backend.expense_tracker_api.resources;

import com.backend.expense_tracker_api.Constants;
import com.backend.expense_tracker_api.domain.User;
import com.backend.expense_tracker_api.services.UserService;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        System.out.println("Before Validation");
        User user = userService.validateUser(email, password);
        System.out.println("User After Validation in controller: ");
//        Map<String,String> map = new HashMap<>();
//        map.put("message", "LoggedIn Successfully");
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> userMap) {
        String firstName = (String) userMap.get("firstName");
        String lastName = (String) userMap.get("lastName");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        System.out.println(firstName + ", " + lastName + ", " + email + ", " + password + " received at controller ");
        User user = userService.registerUser(firstName, lastName, email, password);
//        Map<String, String> map = new HashMap<>();
//        map.put("message", "registered successfully");
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    //    private Map<String,String> generateJWTToken(User user){
//        long timestamp = System.currentTimeMillis();
//
//        //byte[] keyBytes = Decoders.BASE64.decode(Constants.API_SECRET_KEY);
//        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256,Constants.API_SECRET_KEY)
//                .setIssuedAt(new Date(timestamp))
//                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
//                .claim("userId", user.getUserId())
//                .claim("email", user.getEmail())
//                .claim("firstName", user.getFirstName())
//                .claim("lastName", user.getLastName())
//                .compact();
//        Map<String,String> map = new HashMap<>();
//        map.put("token",token);
//        return map;
//    }
//
//    private Key getSignInKey(){
//        byte[] keyBytes = Decoders.BASE64.decode(Constants.API_SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
    //GPT:
    public Map<String, String> generateJWTToken(User user) {
        long timestamp = System.currentTimeMillis();
        Key signingKey = getSignInKey();

        String token = Jwts.builder()
                .signWith(signingKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .compact();

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Constants.API_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//     return Jwts
//             .builder()
//             .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//            .setIssuedAt(new Date(System.currentTimeMillis()))
//            .setExpiration(new Date(System.currentTimeMillis() + expiration))
//            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//            .compact();
}