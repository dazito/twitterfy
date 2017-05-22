package com.dazito.twitterfy.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.dazito.twitterfy.util.AwsUtil;
import com.google.gson.Gson;

/**
 * Created by daz on 10/05/2017.
 */
public class SqsPublisher implements Publisher {

    private AmazonSQSAsync amazonSQSAsync;
    private String region = TwitterfyConfiguration.getConfiguration().getAwsSnsRegion();
    private final String queueUrl;
    private final AWSCredentialsProvider awsCredentialsProvider;
    private final Gson gson;

    public SqsPublisher() {
        gson = new Gson();
        awsCredentialsProvider = AwsUtil.awsCredentialsProvider();

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
