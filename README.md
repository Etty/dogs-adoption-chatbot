# Dog Adoptions Chatbot
## Objective
Chatbot for a dog adoption agency
## Features
1. Create, update and delete dogs in vector database
2. CRUD repositories for dogs and appointments
3.  Chatbot with memory per session, enhanced by RAG and Tooling, which can:
   - tell about agency;
   - tell about available dogs; 
   - suggest dogs to adopt; 
   - schedule an appointment, extracting date from natural language, like "tomorrow at 6pm;
   - correct data in appointment, and even choose another dog;
   - cancel (delete) appointment.
4. React frontend.

## Technologies
- Java
- Spring Boot
- Spring AI
- MySQL
- Anthropic Chat Model https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html
- Elasticsearch Vector Database https://docs.spring.io/spring-ai/reference/api/vectordbs/elasticsearch.html
- ONNX Transformers https://docs.spring.io/spring-ai/reference/api/embeddings/onnx.html
- JUnit
- TypeScript
- React
- HTML, CSS

## How It Works/Technical Details
`Dog` and `Appointment` entities have `DogListener` and `DogListener` respectively. These listeners track entity changes
and update vector database accordingly. This way, new dogs are added to vector database, booked dogs gets removed.
<br>
`Dog` and `Appointment` entities have `OneToOne` relationship, `Appointment` depends on `Dog`. 
This way, currently only one appointment per each dog is supported.
<br><br>
`DogAdoptionScheduler` is responsible for appointments processing.
<br>
`DataTimeAware` is responsible for cumulating exact dates from natural language.
<br><br>
`AdoptionsController` is responsible for handling chat requests. It takes `user` path parameter, 
which identifies user session, and `question` request parameter, which contains natural language question.
Session ID is generated in `App.tsx` with `uuidv4()` function.
<br><br>
`GetConfigController` is responsible for retrieving agency info.
<br><br>
`StoreConfig` is responsible for settings retrieval, such as agency info. `AdoptionsController` and `GetConfigController`
use `StoreConfig` use it.
Requests to controllers are performed via React's `useQuery`.
<br><br>
In `application.properties' you can configure agency details (agency.*)

## Environment Dependencies
- Java 11+
- MySQL 8.0
- Elasticsearch >=7.10.2
- onnx
- NodeJs >=16
 
## Quick Start
1. Clone the repository
2. Copy `application.properties.template` to `application.properties` and update database credentials
3. Obtain Anthropic API key and copy your model ID https://console.anthropic.com/dashboard
4. Paste API key into `spring.ai.anthropic.api-key` and model ID into `spring.ai.anthropic.chat.options.model` in `application.properties`
5. Make sure to complete prerequisites https://docs.spring.io/spring-ai/reference/api/embeddings/onnx.html#_prerequisites
6. Run `mvn clean install` from 'backend' folder
7. Run `mvn spring-boot:run` from 'backend' folder
8. If you need dummy data for dogs, it is into `src/main/resources/data.sql`. Also execute `SyncDogsController` 
(needs to be removed for production)
9. Run `npm install` from 'frontend' folder
10. Run `npm start` from 'frontend' folder
11. Open `http://localhost:3000/` in browser

## Video Demo
[Watch the video](https://www.youtube.com/watch?v=cfq8XHfIKpA)

## Author
https://www.linkedin.com/in/anastasiia-yermolik-b39140b9/