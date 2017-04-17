package com.dazito.twitterfy.gcloud;

import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.spotify.google.cloud.pubsub.client.Message;
import com.spotify.google.cloud.pubsub.client.Publisher;
import com.spotify.google.cloud.pubsub.client.Pubsub;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Created by daz on 16/04/2017.
 */
public class GoogleCloudPubSubClient implements com.dazito.twitterfy.Publisher {

    private final String projectId;
    private final String topic;
    private final GoogleCredential googleCredential;

    private GoogleCloudPubSubClient(String projectId, String topic, GoogleCredential googleCredential) {
        this.projectId = projectId;
        this.topic = topic;
        this.googleCredential = googleCredential;
    }

    public static GoogleCloudPubSubClient defaultClient() throws IOException {
        String credentialsPath = TwitterfyConfiguration.getConfiguration().getGcCredentialsJsonPath();
        String projectId = TwitterfyConfiguration.getConfiguration().getGcPubsubProjectId();
        String topic = TwitterfyConfiguration.getConfiguration().getGcPubsubTopic();
        InputStream inputStream = new FileInputStream(credentialsPath);
        GoogleCredential credential = GoogleCredential.fromStream(inputStream);

        return new GoogleCloudPubSubClient(projectId, topic, credential);
    }

    public void publish(TweetModel tweetModel) {
        final Pubsub pubsub = Pubsub.builder()
                .credential(googleCredential)
                .build();

        final Publisher publisher = Publisher.builder()
                .pubsub(pubsub)
                .project(projectId)
                .build();

        final Message message = Message.of(
                new String(
                        Base64.getEncoder().encode(
                                tweetModel.toString().getBytes()
                        )
                )
        );

        // Publish message
        publisher.publish(topic, message);
    }

}
