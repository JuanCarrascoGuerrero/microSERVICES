package com.wannacode.productmicroservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope //Enables .properties cloud refresh "on running" when requesting this "/api"
public class DefaultConfiguration {

    @Value("${app.testProp}") //Injects the value of the property defined at our Spring Cloud Server
    private String testProp;

    public String getTestProp() {
        return testProp;
    }

    public void setTestProp(String testProp) {
        this.testProp = testProp;
    }
}
