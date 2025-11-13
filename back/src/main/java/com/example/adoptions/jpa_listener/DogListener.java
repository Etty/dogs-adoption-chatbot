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
    private static final Map<Integer, Integer> refCache = new HashMap<>();

    private VectorStore vectorStore;

    public DogListener(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostLoad
    public void postLoad(Dog dog) {
        // Cache the current name after loading from DB
        refCache.put(dog.getId(), dog.getAppointment() != null ? dog.getAppointment().getId() : 0);
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
        int oldRef = refCache.get(dog.getId());
        int newRef = dog.getAppointment() != null ? dog.getAppointment().getId() : 0;
        if (oldRef != newRef) {
            if (newRef == 0) {
                var document = new Document("id: %s, name: %s, description: %s".formatted(
                        dog.getId(), dog.getName(), dog.getDescription()
                ), Map.of("id", dog.getId(), "name", dog.getName(), "description", dog.getDescription())
                );
                vectorStore.add(List.of(document));
            } else {
                vectorStore.delete("id == %s".formatted(dog.getId()));
            }
        }
    }

    @PostRemove
    public void removeDogIndex(Dog dog) {
        vectorStore.delete("id == %s".formatted(dog.getId()));
    }
}
