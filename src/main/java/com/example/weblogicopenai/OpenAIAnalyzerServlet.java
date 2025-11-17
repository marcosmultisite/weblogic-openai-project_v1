package com.example.weblogicopenai;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClients;
import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebServlet(name = "OpenAIAnalyzerServlet", urlPatterns = {"/analyze"})
public class OpenAIAnalyzerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    // Use environment variable OPENAI_API_KEY in WebLogic domain config or startup script

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("OpenAI API key not configured. Set OPENAI_API_KEY env var.");
            return;
        }

        String body = new BufferedReader(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        if (body == null || body.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("No input provided. POST raw text (logs) to this endpoint.");
            return;
        }

        // Build request payload for Chat Completions
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", "gpt-4o-mini"); // substitua conforme disponibilidade

        // messages array
        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", "Analyze the following WebLogic log and return a diagnosis, possible causes and remediation steps:\n\n" + body);

        payload.putArray("messages").add(message);

        String payloadStr = mapper.writeValueAsString(payload);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OPENAI_API_URL);
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(payloadStr, StandardCharsets.UTF_8));

            ClassicHttpResponse openAiResp = client.executeOpen(null, post, null);
            int status = openAiResp.getCode();
            HttpEntity entity = openAiResp.getEntity();

            resp.setStatus(status);
            if (entity != null) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
                    String result = r.lines().collect(Collectors.joining("\n"));
                    // Forward AI response to client
                    resp.setContentType("application/json; charset=utf-8");
                    resp.getWriter().write(result);
                }
            } else {
                resp.getWriter().write("{}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error calling OpenAI: " + e.getMessage());
        }
    }
}
