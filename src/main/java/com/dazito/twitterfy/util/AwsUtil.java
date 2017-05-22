package com.dazito.twitterfy.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;

/**
 * Created by daz on 22/05/2017.
 */
public final class AwsUtil {

    private AwsUtil() {}

    public static AWSStaticCredentialsProvider awsCredentialsProvider() {
        final String accessKey = TwitterfyConfiguration.getConfiguration().getAwsAccessKey();
        final String secretKey = TwitterfyConfiguration.getConfiguration().getAwsSecretKey();

        final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(credentials);
    }
}
