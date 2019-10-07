package org.pdxfinder.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JwtValidator {


    private String secret = "pdxfinder-neo5j&secret...keyJesusislord";

    public JwtUser validate(String token) {

        JwtUser jwtUser = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            jwtUser = new JwtUser();

            jwtUser.setUserName(body.getSubject());
            jwtUser.setId(Long.parseLong((String) body.get("userId")));
            jwtUser.setRole((String) body.get("role"));
            jwtUser.setOrganization((Map<String, Object>) body.get("organization"));
            jwtUser.setPreviledges((List<Map>) body.get("previledges"));
        } catch (Exception e) {
            System.out.println(e);
        }

        return jwtUser;
    }
}
