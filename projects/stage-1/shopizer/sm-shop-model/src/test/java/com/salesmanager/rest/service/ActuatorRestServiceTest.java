package com.salesmanager.rest.service;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

public class ActuatorRestServiceTest {

    @Test
    public void testPostActuatorShutdown() throws URISyntaxException {
        URI uri = new URI("http://127.0.0.1:8888");
        ActuatorRestService restService = RestClientBuilder.newBuilder()
                .baseUri(uri)
                .build(ActuatorRestService.class);

        ShutdownMessage shutdown = restService.shutdown();
        Assert.assertEquals(shutdown.getMessage(), "Shutting down, bye...");

    }

}