package com.example.adoptions.controller;

import com.example.adoptions.repository.DogRepository;
import com.example.adoptions.tool.DataTimeAware;
import com.example.adoptions.tool.DogAdoptionScheduler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
public class AdoptionsController {
    @Value("${agency.name}")
    private String agencyName;

    private final ChatClient ai;
    private final DogRepository repository;
    private final VectorStore vectorStore;
    private final Environment env;

    AdoptionsController(DogAdoptionScheduler scheduler,
                        DataTimeAware dataTimeAware,
                        PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                        ChatClient.Builder ai,
                        DogRepository repository,
                        Environment env,
                        VectorStore vectorStore) {
        this.repository = repository;
        this.vectorStore = vectorStore;
        this.env = env;

        String system = """
                You are an AI powered assistant to help people adopt a dog from the adoption agency named "
                +"%s with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, "
                +"New York City, Amsterdam, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. Information"
                +" about the dogs available will be presented below. You provide actual information about "
                +"available dogs. When telling about specific dog, show it's ID, because it will be necessary"
                +" for appointment scheduling. If there is no information, then return a polite response "
                +"suggesting we don't have any dogs available.
                """.formatted(env.getProperty("agency.name"));
        this.ai = ai
                .defaultTools(scheduler, dataTimeAware)
                .defaultSystem(system)
                .defaultAdvisors(promptChatMemoryAdvisor
                        , new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }

    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return ai
                .prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }

    @GetMapping("/sync")
    String syncDogs() {
        List<Document> documents = new ArrayList<>();
        repository.findAll().forEach(dog -> {

            var document = new Document("id: %s, name: %s, description: %s".formatted(
                    dog.getId(), dog.getName(), dog.getDescription()
            ), Map.of("id", dog.getId(), "name", dog.getName(), "description", dog.getDescription())
            );
            documents.add(document);

        });
        vectorStore.add(documents);

        return "success!";
    }
}
