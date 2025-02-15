package com.hszg.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TranslationService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String translate(String text, String targetLang) {
        try {
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=" 
                + targetLang + "&dt=t&q=" + text;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            
            // Verarbeite alle Teile der Übersetzung
            String translatedText = "";
            JsonNode translations = response.get(0);
            for (JsonNode part : translations) translatedText += part.get(0).asText();
            return translatedText;
        } catch (Exception e) {
            return "Translation error: " + e.getMessage();
        }
    }
}
