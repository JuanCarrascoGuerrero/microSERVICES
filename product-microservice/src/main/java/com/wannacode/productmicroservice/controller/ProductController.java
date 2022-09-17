package com.wannacode.productmicroservice.controller;

import com.wannacode.productmicroservice.entity.ProductEntity;
import com.wannacode.productmicroservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")//Url to access to endpoints
public class ProductController {

    @Autowired //Annotates to Spring that we want to inject repository
    ProductRepository productRepository;

    //ENDPOINTS
//#1 List products
    @GetMapping //Http GET
    @ResponseStatus(HttpStatus.OK) //Useful annotation to define response http status / Response type 200
    public List<ProductEntity> getAllProducts(){
        return  productRepository.findAll();
    }
//without http response status
//    @GetMapping //Http GET
//    public ResponseEntity<List<ProductEntity>> getAllProducts(){
//        List<ProductEntity> productEntities = productRepository.findAll();
//        ResponseEntity<List<ProductEntity>> responseEntity = new ResponseEntity<>(productEntities, HttpStatus.OK);
//        return responseEntity;
//    } Dirtier code

//#2 Create product
    @PostMapping               //Gets values from the request
    @ResponseStatus(HttpStatus.OK)
    public void createProduct (@RequestBody ProductEntity productEntity){
        productRepository.save(productEntity);
    }

}
