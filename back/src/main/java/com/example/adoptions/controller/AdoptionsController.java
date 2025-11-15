package com.example.adoptions.controller;

import com.example.adoptions.config.StoreConfig;
import com.example.adoptions.repository.DogRepository;
import com.example.adoptions.tool.DataTimeAware;
import com.example.adoptions.tool.DogAdoptionScheduler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@CrossOrigin("${frontend.origin}")
public class AdoptionsController {
    private final ChatClient ai;
    private final VectorStore vectorStore;
    private final StoreConfig storeConfig;

    AdoptionsController(DogAdoptionScheduler scheduler,
                        DataTimeAware dataTimeAware,
                        PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                        ChatClient.Builder ai,
                        StoreConfig storeConfig,
                        VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.storeConfig = storeConfig;

        String system = """
                You are an AI powered assistant to help people adopt a dog from the adoption agency named "
                +"%s with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, "
                +"New York City, Amsterdam, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. "
                +"Phone number is %s. We meet visitors from %s to %s.
                +"Information about the dogs available will be presented below. You provide actual information about "
                +"available dogs. When telling about specific dog, show it's ID, because it will be necessary"
                +" for appointment scheduling. If there is no information, then return a polite response "
                +"suggesting we don't have any dogs available.
                """.formatted(
                storeConfig.getAgencyName(),
                storeConfig.getAgencyPhone(),
                storeConfig.getAgencyServeFrom(),
                storeConfig.getAgencyServeTo()
        );
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
}
