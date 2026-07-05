package com.crio.starter.repository;

import com.crio.starter.data.MemeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeRepository extends MongoRepository<MemeEntity, String> {

  // Spring Data generates this query automatically from the method name
  boolean existsByNameAndUrlAndCaption(String name, String url, String caption);
}
