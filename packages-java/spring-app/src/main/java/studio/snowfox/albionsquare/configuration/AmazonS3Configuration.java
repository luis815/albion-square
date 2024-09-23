package studio.snowfox.albionsquare.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Configuration {
    @Value("${aws.s3.endpoint}")
    private String awsS3Endpoint;

    @Value("${aws.s3.region}")
    private String awsS3Region;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsS3Endpoint, awsS3Region))
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey)))
                .build();
    }
}
