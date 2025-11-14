package com.example.adoptions;

import com.example.adoptions.config.TestConfig;
import com.example.adoptions.jpa_listener.AppointmentListener;
import com.example.adoptions.jpa_listener.DogListener;
import com.example.adoptions.repository.DogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DataJpaTest
@Qualifier("mockVectorStore")
@Import({
        DogListener.class,
        AppointmentListener.class,
        TestConfig.class,
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddDogsToVectorTests {
    @Autowired
    private DogRepository dogRepository;

    @Test
    void sync() {
        List<Document> documents = new ArrayList<>();
        dogRepository.saveAll(dogRepository.findAllByAppointmentIsNull());
    }
}
