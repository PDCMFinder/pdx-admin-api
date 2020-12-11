package org.pdxfinder.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Created by csaba on 01/08/2018.
 */
@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> user(Principal principal) {

        Map<String, Object> map = new LinkedHashMap<>();
         map.put("name", principal);
        return map;
    }
}
