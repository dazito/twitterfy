package com.dazito.twitterfy.aws.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.google.gson.Gson;

/**
 * Created by daz on 10/05/2017.
 */
public class SqsPublisher implements Publisher {

    private AmazonSQSAsync amazonSQSAsync;
    private String region = TwitterfyConfiguration.getConfiguration().getAwsSnsRegion();
    private final String queueUrl;
    private final AWSCredentialsProvider awsCredentialsProvider;
    private final String accessKey;
    private final String secretKey;
    private final Gson gson;

    public SqsPublisher() {
        gson = new Gson();
        accessKey = TwitterfyConfiguration.getConfiguration().getAwsAccessKey();
        secretKey = TwitterfyConfiguration.getConfiguration().getAwsSecretKey();

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);

        amazonSQSAsync = AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();

        queueUrl = TwitterfyConfiguration.getConfiguration().getAwsSqsQueueUrl();
    }

    @Override
    public void publish(TweetModel tweetModel) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(tweetModel, TweetModel.class));

        amazonSQSAsync.sendMessage(sendMessageRequest);
    }
}
