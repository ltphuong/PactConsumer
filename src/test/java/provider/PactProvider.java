/*
 * Copyright (c) 2018 Thermo Fisher Scientific
 * All rights reserved.
 */


package provider;

import au.com.dius.pact.consumer.PactProviderRuleMk2;
import consumer.utilities.PropertiesResource;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import static consumer.utilities.ClientHttpRequestConfiguration.getClientHttpRequestFactory;

/**
 * PactProvider creates the sandbox "mock" provider for the consumer side tests
 */
public abstract class PactProvider
{
    /** Logger creation */
    private static final Logger LOG = LoggerFactory.getLogger(PactProvider.class);

    /** Host name for the mock provider */
    private static final String SANDBOX_PROVIDER_HOST = PropertiesResource.getProperty("provider/DefaultConfiguration.properties", "mockProviderHost", "localhost");

    /** Port for the mock provider */
    private static final int SANDBOX_PROVIDER_PORT = Integer.parseInt(PropertiesResource.getProperty("provider/DefaultConfiguration.properties", "mockProviderPort", "8080"));

    /**
     * Rule annotates fields that reference rules or methods that return a JUnit test rule. The field must be public, and not static.
     * Creates a mock provider for the consumer side contract
     */
    @Rule
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2(getProviderName(), SANDBOX_PROVIDER_HOST, SANDBOX_PROVIDER_PORT, this);

    /**
     * Obtains the provider's name that is being referenced from the consumer side test.
     *
     * @return the provider's name
     */
    protected abstract String getProviderName();

    /**
     * Obtains the provider's string Url
     *
     * @return the provider's Url
     */
    protected String getProviderBaseUrl()
    {
        return provider.getUrl();
    }

    /**
     * Send the request with HttpStatusCodeException exception expected and handle the response
     *
     * @param path URL path
     * @param query URL query
     * @param method HTTP Method, e.g; POST, GET etc.
     * @param headers request's header
     * @param body request 's body
     * @return response entity
     */
    public ResponseEntity handleRequestWithHttpStatusCodeException(String path, String query, HttpMethod method, HttpHeaders headers, String body)
    {
        try
        {
            ResponseEntity unexpectedResponse = sendRequestWithErrorExpected(path, query, method, headers, body);
            return unexpectedResponse;
        }
        catch (HttpStatusCodeException actualException)
        {
            ResponseEntity<String> response = new ResponseEntity(actualException.getResponseBodyAsString(), actualException.getResponseHeaders(), actualException.getStatusCode());
            return response;
        }
    }

    /**
     * Send the request with HttpStatusCodeException exception expected and handle the response
     *
     * @param path URL path
     * @param query URL query
     * @param method HTTP Method, e.g; POST, GET etc.
     * @param headers request's header
     * @param body request 's body
     * @param responseStatus
     * @return response entity
     */
    public ResponseEntity handleRequestWithInvalidMediaTypeException(String path, String query, HttpMethod method, HttpHeaders headers, String body, HttpStatus responseStatus)
    {
        try
        {
            ResponseEntity unexpectedResponse = sendRequestWithErrorExpected(path, query, method, headers, body);
            return unexpectedResponse;
        }
        catch (InvalidMediaTypeException actualException)
        {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, actualException.getMediaType());

            ResponseEntity<String> response = new ResponseEntity(responseHeaders, responseStatus);
            return response;
        }
    }

    /**
     * Send the request with error expected
     *
     * @param path URL path
     * @param query URL query
     * @param method HTTP Method, e.g; POST, GET etc.
     * @param headers request's header
     * @param body request 's body
     * @return response entity
     */
    public ResponseEntity sendRequestWithErrorExpected(String path, String query, HttpMethod method, HttpHeaders headers, String body)
    {
        HttpEntity request;
        if (body.isEmpty() || body == null)
        {
            request = new HttpEntity<>(headers);
        }
        else
        {
            request = new HttpEntity<>(body, headers);
        }

        ResponseEntity<String> unexpectedResponse;

        if (query.isEmpty() || query == null)
        {
            unexpectedResponse = new RestTemplate(getClientHttpRequestFactory()).exchange(getProviderBaseUrl() + path, method, request, String.class);
        }
        else
        {
            unexpectedResponse = new RestTemplate(getClientHttpRequestFactory()).exchange(getProviderBaseUrl() + path + "?" + query, method, request, String.class);
        }
        LOG.error("Failure expected but passed, recheck the request please!\n");
        return unexpectedResponse;
    }

}

