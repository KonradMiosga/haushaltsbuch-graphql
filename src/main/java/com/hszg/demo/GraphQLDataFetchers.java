package com.hszg.demo;

import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import com.hszg.demo.service.TranslationService;
import com.hszg.demo.model.MultiLanguageJoke;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class GraphQLDataFetchers {
    private final Random random = new Random();
    
    @Autowired
    private TranslationService translationService;

    public DataFetcher getChuckNorrisJokeDataFetcher() {  // This name should match the schema
        return dataFetchingEnvironment -> {
            try {
                String query = dataFetchingEnvironment.getArgument("query");
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://api.chucknorris.io/jokes/search?query=" + query;

                JsonNode response = restTemplate.getForObject(url, JsonNode.class);

                if (response != null && response.has("result") &&
                        response.get("result").isArray() &&
                        response.get("result").size() > 0) {
                    JsonNode results = response.get("result");
                    int randomIndex = random.nextInt(results.size());
                    String englishJoke = results.get(randomIndex).get("value").asText();

                    MultiLanguageJoke joke = new MultiLanguageJoke();
                    joke.setEnglish(englishJoke);
                    joke.setGerman(translationService.translate(englishJoke, "de"));
                    joke.setPolish(translationService.translate(englishJoke, "pl"));
                    joke.setJapanese(translationService.translate(englishJoke, "ja"));

                    return joke;
                }

                return null;
            } catch (Exception e) {
                return null;
            }
        };
    }
}
