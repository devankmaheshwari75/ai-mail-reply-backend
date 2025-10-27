package com.email.writer.services;

import com.email.writer.entities.EmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service

public class EmailGeneratorService {

  private final WebClient webClient;
  private final String apiKey;



  public EmailGeneratorService(WebClient.Builder webClientbuilder , @org.springframework.beans.factory.annotation.Value("${gemini.api.url}") String baseUrl , @Value("${gemini.api.key}") String geminiApiKey ){
      this.apiKey  = geminiApiKey;
      this.webClient = webClientbuilder.baseUrl(baseUrl).build();


  }
    public String generateEmailReply(EmailRequest emailRequest) {

//        prepare json body

        String  prompt = buildPrompt(emailRequest);

        String requestbody = String.format("""
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      }
                    ]
                  }
                """, prompt);

//         send request

        String response  = webClient.post().uri(uriBuilder -> uriBuilder.path("/v1beta/models/gemini-2.5-flash:generateContent").build()).header("x-goog-api-key" , apiKey).header("Content-Type" , "application/json").bodyValue(requestbody).retrieve().bodyToMono(String.class).block();

        String responseContent  = extractResponseContent(response);


        return  responseContent;

        





    }

    private String extractResponseContent(String response) {

      try
      {
          ObjectMapper mapper   = new ObjectMapper();
          JsonNode root = mapper.readTree(response);
          String text = root.path("candidates").get(0)
                  .path("content")
                  .path("parts").get(0)
                  .path("text")
                  .asText();

          return text;

      }

      catch (JsonProcessingException e){
          throw  new RuntimeException(e);


      }



    }

    private String buildPrompt(EmailRequest emailRequest) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email without the subject line i want the body only  : ");

        if(emailRequest.getTone() != null &&  !emailRequest.getTone().isEmpty()){
            prompt.append("Use a").append(emailRequest.getTone()).append("tone");

        }
        prompt.append("Original Email : \n" ).append(emailRequest.getEmailContent());


        return prompt.toString();



    }
}
