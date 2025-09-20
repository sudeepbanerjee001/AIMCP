package com.mcp.comms.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class OllamaLlmAdapter implements LlmAdapter {

    @Value("${mcp.ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${mcp.ollama.model:mistral:7b-instruct-v0.3-q8_0}")
    private String defaultModel;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(String model) {
        return model != null && model.toLowerCase().contains("mistral");
    }

    @Override
    public String generate(String model, String prompt, Map<String,Object> options) throws Exception {
        if(model==null || model.isEmpty()) model = defaultModel;

        String endpoint = ollamaUrl;
        if(!endpoint.endsWith("/")) endpoint += "/";
        endpoint += "v1/completions";

        Map<String,Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        if(options!=null){
            if(options.containsKey("max_tokens")) body.put("max_tokens", options.get("max_tokens"));
            if(options.containsKey("temperature")) body.put("temperature", options.get("temperature"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(mapper.writeValueAsString(body), headers);

        try{
            ResponseEntity<String> response = rest.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new RuntimeException("Ollama server returned error: "+response.getStatusCode()+" / "+response.getBody());
            }

            System.out.println("Ollama response: "+response.getBody());

            // Parse choices[0].text
            JsonNode root = mapper.readTree(response.getBody());
            if(root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0){
                JsonNode first = root.get("choices").get(0);
                if(first.has("text")) return first.get("text").asText();
            }

            return "Ollama returned no text output.";

        }catch(RestClientException ex){
            throw new RuntimeException("Cannot reach Ollama server at "+endpoint+". Is it running? "+ex.getMessage());
        }
    }
}
