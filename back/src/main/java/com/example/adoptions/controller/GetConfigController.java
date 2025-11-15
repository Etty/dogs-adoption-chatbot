package com.example.adoptions.controller;

import com.example.adoptions.config.StoreConfig;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@CrossOrigin("${frontend.origin}")
public class GetConfigController {
    private final StoreConfig storeConfig;

    public GetConfigController(StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }

    @GetMapping("/store/config/agency_name")
    public String getAgencyName() {
        return storeConfig.getAgencyName();
    }
}
