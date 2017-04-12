package com.dazito.twitterfy.aws.sns;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.google.gson.Gson;

/**
 * Created by daz on 12/04/2017.
 */
public class SnsPublisher implements Publisher {

    private AmazonSNSAsync snsClient;
    private final String region = TwitterfyConfiguration.getConfiguration().getAwsSnsRegion();
    private final String topicArn = TwitterfyConfiguration.getConfiguration().getAwsSnsTopicArn();
    private final AWSCredentialsProvider awsCredentialsProvider;
    private final String accessKey;
    private final String secretKey;

    public SnsPublisher() {
        accessKey = TwitterfyConfiguration.getConfiguration().getAwsAccessKey();
        secretKey = TwitterfyConfiguration.getConfiguration().getAwsSecretKey();

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);
        snsClient = AmazonSNSAsyncClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();
    }

    @Override
    public void publish(TweetModel tweetModel) {
        final String message = new Gson().toJson(tweetModel, TweetModel.class);
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        snsClient.publish(publishRequest);
    }
}
