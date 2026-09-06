package tz.ac.dit.safarismart.service.ai;

public interface AiClient {
    /**
     * Sends a system+user prompt to the LLM and returns its raw text response.
     * Implementations must throw on any failure (network, auth, malformed
     * response) rather than returning null -- callers treat any exception
     * as "fall back to the template narrative."
     */
    String complete(String systemPrompt, String userPrompt);
}
