package com.redhat.demo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Path("/secret")
public class SecretResource {
    @Inject
    SecretsManagerClient secretsManagerClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String fetchSecret() {
        var getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId("DatabaseProdSecrets")
                .build();
        return secretsManagerClient.getSecretValue(getSecretValueRequest).secretString();
    }


}
