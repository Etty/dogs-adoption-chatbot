package com.example.adoptions.repository;

import org.springframework.data.repository.ListCrudRepository;
import com.example.adoptions.entity.Dog;

public interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

