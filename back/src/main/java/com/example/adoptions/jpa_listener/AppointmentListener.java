package com.example.adoptions.jpa_listener;

import com.example.adoptions.entity.Appointment;
import com.example.adoptions.entity.Dog;
import jakarta.persistence.*;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppointmentListener {
    private static final Map<Integer, Dog> dogCache = new HashMap<>();

    private final VectorStore vectorStore;

    public AppointmentListener(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostLoad
    public void postLoad(Appointment appointment) {
        // Cache the current name after loading from DB
        dogCache.put(appointment.getId(), appointment.getDog());
    }

    @PostPersist
    public void newAppointment(Appointment appointment) {
        int dogId = appointment.getDog().getId();
        vectorStore.delete("id == %s".formatted(dogId));
    }

    @PostUpdate
    public void preUpdate(Appointment appointment) {
        Dog prevDog = dogCache.get(appointment.getId());
        int newDogId = appointment.getDog().getId();
        if (prevDog.getId() != newDogId) {
            var document = new Document("id: %s, name: %s, description: %s".formatted(
                    prevDog.getId(), prevDog.getName(), prevDog.getDescription()
            ), Map.of(
                    "id", prevDog.getId(), "name", prevDog.getName(), "description", prevDog.getDescription()
            )
            );
            vectorStore.add(List.of(document));
            vectorStore.delete("id == %s".formatted(newDogId));
        }
    }

    @PostRemove
    public void restoreDogIndex(Appointment appointment) {
        Dog dog = appointment.getDog();
        var document = new Document("id: %s, name: %s, description: %s".formatted(
                dog.getId(), dog.getName(), dog.getDescription()
        ));
        vectorStore.add(List.of(document));
    }
}
