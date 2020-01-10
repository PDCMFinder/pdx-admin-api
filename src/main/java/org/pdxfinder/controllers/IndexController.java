package org.pdxfinder.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abayomi on 27/06/2017.
 */

@Controller
public class IndexController {

    private final static Logger log = LoggerFactory.getLogger(IndexController.class);

    // Forward to home page so that the angular route is preserved.
    @RequestMapping(value = "/**/{[path:[^\\.]*}")
    public String redirect() {

        return "forward:/";
    }


}
