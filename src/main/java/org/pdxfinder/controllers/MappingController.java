package org.pdxfinder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Created by csaba on 01/08/2018.
 */
@RestController
public class MappingController {


    @RequestMapping("/mapping")
    String mapping() {

        return "mapping";
    }


    @GetMapping("/user")
    public Map<String, Object> user(Principal principal) {

        Map<String, Object> map = new LinkedHashMap<>();
         map.put("name", principal);
        return map;
    }
}
