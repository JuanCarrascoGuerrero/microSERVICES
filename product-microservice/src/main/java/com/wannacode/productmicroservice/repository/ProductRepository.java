package com.wannacode.productmicroservice.repository;

import com.wannacode.productmicroservice.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository                                            //Methods related to DDBB
public interface ProductRepository extends MongoRepository <ProductEntity, String> {
}
