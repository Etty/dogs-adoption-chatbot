package com.example.adoptions.repository;

import org.springframework.data.repository.ListCrudRepository;
import com.example.adoptions.entity.Dog;

import java.util.List;

public interface DogRepository extends ListCrudRepository<Dog, Integer> {
    List<Dog> findAllByAppointmentIsNull();
}

