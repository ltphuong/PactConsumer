package consumer.convention;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.*;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import provider.BusinessConventionProvider;
import provider.KnownProviders;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;

public class SearchWeatherInYourCity extends  BusinessConventionProvider{
    private static final String API_KEY = "eb4b2aebcef4ddb419fb427604bd4880";
    private static final String SEARCH_WEATHER_PATH = "/data/2.5/weather";
    private static final String SEARCH_WEATHER_QUERY = "q=My Tho, VN&unit=Imperia&appid=" + API_KEY;
    private static final String SEARCH_INVALID_CITY_PATH = "/data/2.5/weather";
    private static final String SEARCH_INVALID_CITY_QUERY = "id=12345784575478&units=metric&appid=" + API_KEY;

    private static final Map<String, String> DEFAULT_HEADERS = ImmutableMap.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    /**
     * Creates pact record for getting Search weather in your city
     *
     * @param builder - pact builder
     * @return pact record
     */
    @Pact(
            provider = KnownProviders.QA_CHALLENGE_CONVENTION,
            consumer = "SearchWeatherInYourCity_Consumer"
    )
    public RequestResponsePact createSearchWeatherInYourCityRecord(PactDslWithProvider builder)
    {
        return builder
            .given("Search weather in your city is implemented!")
            .uponReceiving("An request to search weather in your city")
            .path(SEARCH_WEATHER_PATH)
            .query(SEARCH_WEATHER_QUERY)
            .method(HttpMethod.GET.name())
            .willRespondWith()
            .headers(DEFAULT_HEADERS)
            .status(HttpStatus.OK.value())
            .body(new PactDslJsonBody()
                    .stringType("base", "stations")
                    .numberType("visibility", 10000)
                    .numberType("dt", 1619624200)
                    .numberType("timezone", 25200)
                    .id("id")
                    .stringValue("name", "My Tho")
                    .integerType("cod", 200)

                    .object("coord")
                    .decimalType("lon", 106.35)
                    .decimalType("lat", 10.35)
                    .closeObject()

                    .minArrayLike("weather", 1, 1)
                    .id("id")
                    .stringType("main", "Clouds")
                    .stringType("description", "few clouds")
                    .stringType("icon", "02n")
                    .closeArray()

                    .object("main")
                    .decimalType("temp", 302.15)
                    .decimalType("feels_like", 306.64)
                    .decimalType("temp_min", 302.15)
                    .decimalType("temp_max", 302.15)
                    .numberType("pressure", 1011)
                    .numberType("humidity", 74)
                    .closeObject()

                    .object("wind")
                    .decimalType("speed", 106.35)
                    .numberType("deg", 130)
                    .closeObject()

                    .object("clouds")
                    .numberType("all", 130)
                    .closeObject()

                    .object("sys")
                    .integerType("type", 1)
                    .id("id")
                    .stringValue("country", "VN")
                    .numberType("sunrise", 1619563078)
                    .numberType("sunset", 1619607960)
                    .closeObject()

            )
            .toPact();
    }

    /**
     * Creates pact record for search invalid city
     *
     * @param builder - pact builder
     * @return pact record
     */
    @Pact(
            provider = KnownProviders.QA_CHALLENGE_CONVENTION,
            consumer = "SearchWeatherWithInvalidCity_Consumer"
    )
    public RequestResponsePact createSearchWeatherWithInvalidCityRecord(PactDslWithProvider builder)
    {
        return builder
                .given("Search weather in your city is implemented!")
                .uponReceiving("An request to search weather with invalid city")
                .path(SEARCH_INVALID_CITY_PATH)
                .query(SEARCH_INVALID_CITY_QUERY)
                .method(HttpMethod.GET.name())
                .willRespondWith()
                .headers(DEFAULT_HEADERS)
                .status(HttpStatus.NOT_FOUND.value())
                .body(new PactDslJsonBody()
                        .stringValue("cod", "404")
                        .stringValue("message", "city not found"))
                .toPact();
    }

    /**
     * Verify search weather in your city
     *
     * @throws IOException - when there is an issue reading a value from the response body
     */
    @Test
    @PactVerification(
            value = KnownProviders.QA_CHALLENGE_CONVENTION,
            fragment = "createSearchWeatherInYourCityRecord"
    )
    public void verifySearchWeatherInYourCity() throws IOException
    {
        ResponseEntity<String> response = new RestTemplate().getForEntity(getProviderBaseUrl() + SEARCH_WEATHER_PATH + "?" +  SEARCH_WEATHER_QUERY, String.class);
        Assert.assertTrue(response.getHeaders().getContentType().toString().equals(MediaType.APPLICATION_JSON_VALUE));
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertEquals(new ObjectMapper().readValue(response.getBody(), Map.class).get("name"), "My Tho");
    }

    /**
     * Verify search weather with invalid city
     *
     * @throws IOException - when there is an issue reading a value from the response body
     */
    @Test
    @PactVerification(
            value = KnownProviders.QA_CHALLENGE_CONVENTION,
            fragment = "createSearchWeatherWithInvalidCityRecord"
    )
    public void verifySearchWeatherWithInvalidCity() throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = handleRequestWithHttpStatusCodeException(SEARCH_INVALID_CITY_PATH + "?" +  SEARCH_INVALID_CITY_QUERY, StringUtils.EMPTY, HttpMethod.GET, headers,
                StringUtils.EMPTY);
        Assert.assertTrue(response.getHeaders().getContentType().toString().equals(MediaType.APPLICATION_JSON_VALUE));
        Assert.assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
        Assert.assertEquals(new ObjectMapper().readValue(response.getBody(), Map.class).get("cod"), "404");
        Assert.assertEquals(new ObjectMapper().readValue(response.getBody(), Map.class).get("message"), "city not found");
    }
}

