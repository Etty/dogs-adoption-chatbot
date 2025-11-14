package com.example.adoptions.jpa_listener;

import com.example.adoptions.entity.Dog;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DogListener {
    private static final Map<Integer, Dog> dogCache = new HashMap<>();

    private VectorStore vectorStore;

    public DogListener(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostLoad
    public void postLoad(Dog dog) {
        // Cache the current name after loading from DB
        dogCache.put(dog.getId(), dog);
    }

    @PostPersist
    public void addNewDog(Dog dog) {
        var document = new Document("id: %s, name: %s, description: %s".formatted(
                dog.getId(), dog.getName(), dog.getDescription()
        ), Map.of("id", dog.getId(), "name", dog.getName(), "description", dog.getDescription())
        );
        vectorStore.add(List.of(document));
    }

    @PostUpdate
    public void preUpdate(Dog dog) {
        Dog oldDogObg = dogCache.get(dog.getId());
        int newRef = dog.getAppointment() != null ? dog.getAppointment().getId() : 0;
        if (!dog.equals(oldDogObg)) {
            if (newRef == 0) {
                removeDogIndex(dog.getId());
                var document = new Document("id: %s, name: %s, description: %s".formatted(
                        dog.getId(), dog.getName(), dog.getDescription()
                ), Map.of("id", dog.getId(), "name", dog.getName(), "description", dog.getDescription())
                );
                vectorStore.add(List.of(document));
            } else {
                removeDogIndex(dog.getId());
            }
        }
    }

    @PostRemove
    public void removeDogIndex(Dog dog) {
        removeDogIndex(dog.getId());
    }

    private void removeDogIndex(int id) {
        vectorStore.delete("id == %s".formatted(id));
    }
}
