/*
 * Copyright (c) 2018 Thermo Fisher Scientific
 * All rights reserved.
 */


package consumer.utilities;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;


/**
 * Client HTTP request configuration methods
 */
public class ClientHttpRequestConfiguration
{
    /**
     * configure client HTTP request
     *
     * @return a {@link ClientHttpRequestFactory}
     */
    public static ClientHttpRequestFactory getClientHttpRequestFactory()
    {
        int timeout = 5000;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}

