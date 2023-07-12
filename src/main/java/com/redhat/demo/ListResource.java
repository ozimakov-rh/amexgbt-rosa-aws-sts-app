package com.redhat.demo;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Path("/list")
public class ListResource {

    private static final Logger log = LoggerFactory.getLogger(ListResource.class);

    @Inject
    StsClient sts;

    @Inject
    S3Client s3;

    @ConfigProperty(name = "bucket.name", defaultValue = "default")
    String bucketName;

    @ConfigProperty(name = "role.arn", defaultValue = "")
    String roleArn;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> list() {
        var assumeRoleRequest = AssumeRoleRequest.builder()
                .roleSessionName("secure-access-user-s3")
                .roleArn(roleArn)
                .build();

        var stsCredentials = sts.assumeRole(assumeRoleRequest).credentials();

        s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(
                        stsCredentials.accessKeyId(),
                        stsCredentials.secretAccessKey(),
                        stsCredentials.sessionToken())))
                .build();

        // ⬇ the actual code ⬇

        var listObjectRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        return Multi.createFrom().items(s3.listObjects(listObjectRequest).contents().stream())
                .map(S3Object::key);
    }
}
