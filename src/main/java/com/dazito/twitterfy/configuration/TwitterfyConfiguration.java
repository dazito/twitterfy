package com.dazito.twitterfy.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by daz on 05/04/2017.
 */
public final class TwitterfyConfiguration {

    private static final String PROPERTIES_FILE_NAME = "configuration.properties";
    private static final String TWITTER_API_KEY = "twitter.key";
    private static final String TWITTER_API_SECRET = "twitter.secret";
    private static final String TWITTER_API_TOKEN = "twitter.token";
    private static final String TWITTER_API_TOKEN_SECRET = "twitter.tokenSecret";
    private static final String TWITTER_KEYWORDS = "twitter.keywords";
    private static final String FILTER_KEYWORDS = "twitter.keywords.filter";
    private static final String DB_ACTIVE = "db.active";
    private static final String DB_JDBC_URL = "db.JdbcUrl";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String AWS_SNS_TOPIC_ARN = "aws.sns.topic-arn";
    private static final String AWS_SNS_ACTIVE = "aws.sns.active";
    private static final String AWS_SNS_REGION = "aws.sns.region";
    private static final String AWS_SQS_ACTIVE = "aws.sqs.active";
    private static final String AWS_SQS_REGION= "aws.sqs.region";
    private static final String AWS_SQS_QUEUE_URL = "aws.sqs.queue-url";
    private static final String AWS_SECRET_KEY = "aws.secret-key";
    private static final String AWS_ACCESS_KEY = "aws.access-key";
    private static final String SCHEDULER_ACTIVE = "scheduler.active";
    private static final String SCHEDULER_FREQUENCY = "scheduler.frequency";
    private static final String SCHEDULER_FREQUENCY_UNIT = "scheduler.frequency.unit";
    private static final String EMAIL_ACTIVE = "email.active";
    private static final String EMAIL_PORT = "email.port";
    private static final String EMAIL_TO = "email.to";
    private static final String EMAIL_FROM = "email.from";
    private static final String EMAIL_USER = "email.user";
    private static final String EMAIL_PASSWORD = "email.password";
    private static final String EMAIL_SMTP_HOST = "email.smtp.host";
    private static final String EMAIL_SUBJECT = "email.subject";
    private static final String GC_CREDENTIALS_JSON_PATH = "gc.credentials.json.path";
    private static final String GC_PUBSUB_ACTIVE = "gc.pubsub.active";
    private static final String GC_PUBSUB_PROJECT_ID = "gc.pubsub.project-id";
    private static final String GC_PUBSUB_TOPIC = "gc.pubsub.topic";

    private String twitterApiKey;
    private String twitterApiSecret;
    private String twitterApiToken;
    private String twitterApiTokenSecret;
    private String[] subscribeKeywords;
    private boolean dbActive;
    private String connectionPoolJdbcUrl;
    private String connectionPoolDbUsername;
    private String connectionPoolDbPassword;
    private Set<String> filterKeywords;
    private String awsSnsTopicArn;
    private String awsSnsRegion;
    private boolean awsSnsActive;
    private String awsSqsRegion;
    private String awsSqsQueueUrl;
    private boolean awsSqsActive;
    private String awsSecretKey;
    private String awsAccessKey;
    private boolean isSchedulerActivated;
    private int schedulerFrequency;
    private String schedulerFrequencyUnit;
    private boolean emailActive;
    private int emailPort;
    private String[] emailTo;
    private String emailFrom;
    private String emailUsername;
    private String emailPassword;
    private String emailSmtpHost;
    private String emailSubject;
    private String gcCredentialsJsonPath;
    private boolean gcPubsubActive;
    private String gcPubsubProjectId;
    private String gcPubsubTopic;

    final Properties properties = new Properties();

    private static TwitterfyConfiguration instance = new TwitterfyConfiguration();
    public static TwitterfyConfiguration getConfiguration() {
        return instance;
    }

    private TwitterfyConfiguration() {
    }

    public void loadConfiguration() throws IOException {
        properties.load(TwitterfyConfiguration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));

        twitterApiKey = properties.getProperty(TWITTER_API_KEY, "");
        twitterApiSecret = properties.getProperty(TWITTER_API_SECRET, "");

        twitterApiToken = properties.getProperty(TWITTER_API_TOKEN, "");
        twitterApiTokenSecret = properties.getProperty(TWITTER_API_TOKEN_SECRET, "");
        dbActive = Boolean.parseBoolean(properties.getProperty(DB_ACTIVE, "false"));
        connectionPoolJdbcUrl = properties.getProperty(DB_JDBC_URL, "");
        connectionPoolDbUsername = properties.getProperty(DB_USERNAME, "");
        connectionPoolDbPassword = properties.getProperty(DB_PASSWORD, "");
        awsSnsTopicArn = properties.getProperty(AWS_SNS_TOPIC_ARN, "");
        awsSnsRegion = properties.getProperty(AWS_SNS_REGION, "");
        awsSnsActive = Boolean.parseBoolean(properties.getProperty(AWS_SNS_ACTIVE, "false"));
        awsSqsRegion = properties.getProperty(AWS_SQS_REGION, "");
        awsSqsQueueUrl = properties.getProperty(AWS_SQS_QUEUE_URL, "");
        awsSqsActive = Boolean.parseBoolean(properties.getProperty(AWS_SQS_ACTIVE, "false"));
        awsAccessKey = properties.getProperty(AWS_ACCESS_KEY, "");
        awsSecretKey = properties.getProperty(AWS_SECRET_KEY, "");
        isSchedulerActivated = Boolean.parseBoolean(properties.getProperty(SCHEDULER_ACTIVE, "false"));
        schedulerFrequency = Integer.parseInt(properties.getProperty(SCHEDULER_FREQUENCY, "1"));

        // toUpperCase to be able to be parsed by TimeUnit as SECONDS/MINUTES/HOURS/DAYS...
        schedulerFrequencyUnit = properties.getProperty(SCHEDULER_FREQUENCY_UNIT, "days").toUpperCase();
        emailActive = Boolean.parseBoolean(properties.getProperty(EMAIL_ACTIVE, "false"));
        emailPort = Integer.parseInt(properties.getProperty(EMAIL_PORT, "465"));
        emailTo = parseCommaSeparatedStringToArray(properties.getProperty(EMAIL_TO, ""));
        emailFrom = properties.getProperty(EMAIL_FROM, "");
        emailUsername = properties.getProperty(EMAIL_USER, "");
        emailPassword = properties.getProperty(EMAIL_PASSWORD, "");
        emailSmtpHost = properties.getProperty(EMAIL_SMTP_HOST, "");
        emailSubject = properties.getProperty(EMAIL_SUBJECT, "");
        subscribeKeywords = parseCommaSeparatedStringToArray(properties.getProperty(TWITTER_KEYWORDS, ""));
        filterKeywords = parseCommaSeparatedStringToSet(properties.getProperty(FILTER_KEYWORDS, ""));
        gcCredentialsJsonPath = properties.getProperty(GC_CREDENTIALS_JSON_PATH, "");
        gcPubsubActive = Boolean.parseBoolean(properties.getProperty(GC_PUBSUB_ACTIVE, "false"));
        gcPubsubProjectId = properties.getProperty(GC_PUBSUB_PROJECT_ID, "");
        gcPubsubTopic = properties.getProperty(GC_PUBSUB_TOPIC, "");
    }

    private String[] parseCommaSeparatedStringToArray(String csvString) {
        final String[] strArray = csvString.toLowerCase().split(",");

        for(int i = 0; i < strArray.length; i++) {
            strArray[i] = strArray[i].trim();
        }

        return strArray;
    }

    private Set<String> parseCommaSeparatedStringToSet(String csvString) {
        final String[] strArray = parseCommaSeparatedStringToArray(csvString.toLowerCase());

        return new HashSet<>(Arrays.asList(strArray));
    }

    public String getTwitterApiKey() {
        return twitterApiKey;
    }

    public String getTwitterApiSecret() {
        return twitterApiSecret;
    }

    public String getTwitterApiToken() {
        return twitterApiToken;
    }

    public String getTwitterApiTokenSecret() {
        return twitterApiTokenSecret;
    }

    public String[] getSubscribeKeywords() {
        return subscribeKeywords;
    }

    public String getConnectionPoolJdbcUrl() {
        return connectionPoolJdbcUrl;
    }

    public String getConnectionPoolDbUsername() {
        return connectionPoolDbUsername;
    }

    public String getConnectionPoolDbPassword() {
        return connectionPoolDbPassword;
    }

    public Set<String> getFilterKeywords() {
        return filterKeywords;
    }

    public String getAwsSnsTopicArn() {
        return awsSnsTopicArn;
    }

    public String getAwsSnsRegion() {
        return awsSnsRegion;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public boolean isSchedulerActivated() {
        return isSchedulerActivated;
    }

    public int getSchedulerFrequency() {
        return schedulerFrequency;
    }

    public String getSchedulerFrequencyUnit() {
        return schedulerFrequencyUnit;
    }

    public int getEmailPort() {
        return emailPort;
    }

    public String[] getEmailTo() {
        return emailTo;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public String getEmailUsername() {
        return emailUsername;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public String getEmailSmtpHost() {
        return emailSmtpHost;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getGcCredentialsJsonPath() {
        return gcCredentialsJsonPath;
    }

    public boolean isGcPubsubActive() {
        return gcPubsubActive;
    }

    public String getGcPubsubProjectId() {
        return gcPubsubProjectId;
    }

    public String getGcPubsubTopic() {
        return gcPubsubTopic;
    }

    public boolean isAwsSnsActive() {
        return awsSnsActive;
    }

    public boolean isDbActive() {
        return dbActive;
    }

    public boolean isEmailActive() {
        return emailActive;
    }

    public String getAwsSqsRegion() {
        return awsSqsRegion;
    }

    public boolean isAwsSqsActive() {
        return awsSqsActive;
    }

    public String getAwsSqsQueueUrl() {
        return awsSqsQueueUrl;
    }
}
