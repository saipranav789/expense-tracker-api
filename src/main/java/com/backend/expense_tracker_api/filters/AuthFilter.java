package com.backend.expense_tracker_api.filters;

import com.backend.expense_tracker_api.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;

public class AuthFilter extends GenericFilterBean {

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(Constants.API_SECRET_KEY));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                httpServletRequest.setAttribute("userId", Integer.parseInt(claims.get("userId").toString()));

            } catch (Exception e) {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Invalid or expired token");
                return;
            }
        } else {
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be provided");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
//
//        String authHeader = httpServletRequest.getHeader("Authorization");
//        if(authHeader!=null){
//            String[] authHeaderArr = authHeader.split("Bearer ");
//            if(authHeaderArr.length>1 && authHeaderArr[1] != null){
//                String token = authHeaderArr[1];
//                try{
//                    Claims claims = Jwts.parser().setSigningKey(Constants.API_SECRET_KEY).parseClaimsJws(token).getBody();
//                    HttpRequest.setAttribute("userId",Integer.parseInt(claims.get("userId").toString()));
//
//                }catch (Exception e){
//                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(),"invalid/expired token");
//                    return;
//                }
//            }else{
//                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(),"Authorization token must be Bearer [token]");
//                return;
//            }
//
//        } else {
//            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(),"Authorization token must be provided");
//            return;
//        }
//        filterChain.doFilter(servletRequest,servletResponse);
//    }
}

