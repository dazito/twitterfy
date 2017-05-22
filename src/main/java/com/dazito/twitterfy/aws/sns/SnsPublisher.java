package com.dazito.twitterfy.aws.sns;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.dazito.twitterfy.util.AwsUtil;
import com.google.gson.Gson;

/**
 * Created by daz on 12/04/2017.
 */
public class SnsPublisher implements Publisher {

    private AmazonSNSAsync snsClient;
    private final String region = TwitterfyConfiguration.getConfiguration().getAwsSnsRegion();
    private final String topicArn = TwitterfyConfiguration.getConfiguration().getAwsSnsTopicArn();
    private final AWSCredentialsProvider awsCredentialsProvider;
    private final Gson gson;

    public SnsPublisher() {
        awsCredentialsProvider = AwsUtil.awsCredentialsProvider();
        snsClient = AmazonSNSAsyncClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();

        gson = new Gson();
    }

    @Override
    public void publish(TweetModel tweetModel) {
        final String message = gson.toJson(tweetModel, TweetModel.class);
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        snsClient.publish(publishRequest);
    }
}
