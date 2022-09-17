package com.wannacode.productmicroservice.controller;

import com.wannacode.productmicroservice.config.DefaultConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/categories")
@Getter
@Setter
public class CategoryController {

    @Autowired
    DefaultConfiguration defaultConfiguration;

    @GetMapping("test-prop")// An endpoint of the categories api that returns the textProp
    private String getTestProp(){
        return this.defaultConfiguration.getTestProp();
    }
}
