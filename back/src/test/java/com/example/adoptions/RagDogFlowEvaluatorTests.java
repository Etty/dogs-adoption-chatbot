/**
 * At the moment of writing this test, spring-ai-evaluation artifact was not available in Maven repo
 */
//package com.example.adoptions;
//
//import com.example.adoptions.entity.Dog;
//import com.example.adoptions.repository.DogRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.evaluation.ResponseEvaluationResult;
//import org.springframework.ai.evaluation.accuracy.ResponseAccuracyEvaluator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//public class RagDogFlowEvaluatorTests {
//    @Autowired
//    private DogRepository dogRepository;
//
//    /**
//     * ChatClient, used by app
//     */
//    @Autowired
//    private ChatClient ai;
//
//    /**
//     * Evaluator that checks whether the model answer matches the
//     * expected ground-truth text with a good enough score.
//     * (LLM-based evaluator from Spring AI testing support.)
//     */
//    @Autowired
//    private ResponseAccuracyEvaluator responseAccuracyEvaluator;
//
//    @Test
//    @Transactional
//    void addDog_makesItSearchableThroughRag() {
//        // Arrange: create and persist a dog;
//        Dog dog = new Dog();
//        dog.setName("EvaluatorRex");
//        dog.setDescription("A friendly evaluator dog used for RAG tests");
//        // Add dog – this should trigger indexing logic.
//        Dog savedDog = dogRepository.save(dog);
//
//        String question = "Do you have any dog named EvaluatorRex available for adoption?";
//        String expectedAnswerFragment =
//                "Yes, we have a dog named EvaluatorRex"; // ground truth expectation
//
//        String answer = ai
//                .prompt()
//                .user(question)
//                .call()
//                .content();
//
//        // Evaluate: use an Evaluator instead of naive string contains.
//        ResponseEvaluationResult result =
//                responseAccuracyEvaluator.evaluate(question, expectedAnswerFragment, answer);
//
//        // Assert:
//        // - evaluator score is above a threshold, meaning the RAG+LLM
//        //   could “see” the dog and answer correctly.
//        assertThat(result.getScore())
//                .as("Dog should be discoverable via RAG after being added")
//                .isGreaterThanOrEqualTo(0.7);
//
//        // Remove dog – this should trigger de-indexing logic.
//        dogRepository.delete(savedDog);
//        String question1 = "Do you have a dog named EvaluatorRex?";
//        String expectedAnswerFragment1 =
//                "We do not have a dog named EvaluatorRex"; // expected ground truth
//
//        // Act: query assistant again
//        String answer1 = ai
//                .prompt()
//                .user(question)
//                .call()
//                .content();
//        // Evaluate: again, use the evaluator to check correctness.
//        ResponseEvaluationResult result1 =
//                responseAccuracyEvaluator.evaluate(question1, expectedAnswerFragment1, answer1);
//
//        // Assert:
//        // - evaluator score is high for the *negative* ground truth,
//        //   meaning the answer correctly reflects that the dog is no longer available.
//        assertThat(result1.getScore())
//                .as("Dog should no longer be discoverable via RAG after removal")
//                .isGreaterThanOrEqualTo(0.7);
//    }
//}
