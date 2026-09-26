package org.intern.personalfinancemanagementsystem.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.net.http.HttpHeaders;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AwsSnsConfig {
    @Value("${aws.sns.access-key}")
    String awsAccessKey;

    @Value("${aws.sns.secret-key}")
    String awsSecretKey;

    @Bean
    public SnsClient snsClient() {
        //  1. Goi 2 chuoi
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

        // 2. Tao client
        return SnsClient.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
