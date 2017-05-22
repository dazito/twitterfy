package com.dazito.twitterfy.aws.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.dazito.twitterfy.util.AwsUtil;
import com.google.gson.Gson;

import java.util.UUID;

/**
 * Created by daz on 22/05/2017.
 */
public class DynamoDbClient implements Publisher {

    private final AmazonDynamoDB client;
    private final DynamoDB dynamoDB;
    private final Table table;
    private final Regions region;
    private final String tableName;

    public DynamoDbClient() {
        tableName = TwitterfyConfiguration.getConfiguration().getAwsDynamoDbTable();
        region = Regions.fromName(TwitterfyConfiguration.getConfiguration().getAwsDynamoDbRegion());

        AWSStaticCredentialsProvider awsCredentialsProvider = AwsUtil.awsCredentialsProvider();

        client = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();
        dynamoDB = new DynamoDB(client);
        table = new Table(client, tableName);
    }

    @Override
    public void publish(TweetModel tweetModel) {
        final String uuid = UUID.randomUUID().toString();
        final String tweetModelJson = new Gson().toJson(tweetModel, TweetModel.class);

        table.putItem(
                new Item()
                        .withPrimaryKey("id", uuid)
                        .withJSON("tweet", tweetModelJson)
        );
    }
}
