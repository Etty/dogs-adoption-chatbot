package com.example.adoptions.controller;

import com.example.adoptions.repository.DogRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@CrossOrigin("${frontend.origin}")
public class SyncDogsController {
    private final DogRepository dogRepository;
    private final VectorStore vectorStore;

    public SyncDogsController(DogRepository dogRepository, VectorStore vectorStore) {
        this.dogRepository = dogRepository;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/sync")
    String syncDogs() {
        List<Document> documents = new ArrayList<>();
        dogRepository.findAllByAppointmentIsNull().forEach(dog -> {
            vectorStore.delete("id == %s".formatted(dog.getId()));
            var document = new Document("id: %s, name: %s, description: %s".formatted(
                    dog.getId(), dog.getName(), dog.getDescription()
            ), Map.of("id", dog.getId(), "name", dog.getName(), "description", dog.getDescription())
            );
            documents.add(document);
        });
        vectorStore.add(documents);
        return "Synced";
    }
}
