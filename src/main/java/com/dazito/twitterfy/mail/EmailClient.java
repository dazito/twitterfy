package com.dazito.twitterfy.mail;

import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import com.dazito.twitterfy.util.StringUtil;
import com.dazito.twitterfy.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * Created by daz on 16/04/2017.
 */
public class EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailClient.class);

    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
    private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";
    private static final String JAVAX_NET_SSL_SSLSOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private final int port;
    private final String[] toEmail;
    private final String fromEmail;
    private final String username;
    private final String password;
    private final String emailSmtpHost;
    private final String emailSubject;

    public EmailClient() {
        port = TwitterfyConfiguration.getConfiguration().getEmailPort();
        toEmail = TwitterfyConfiguration.getConfiguration().getEmailTo();
        fromEmail = TwitterfyConfiguration.getConfiguration().getEmailFrom();
        username = TwitterfyConfiguration.getConfiguration().getEmailUsername();
        password = TwitterfyConfiguration.getConfiguration().getEmailPassword();
        emailSmtpHost = TwitterfyConfiguration.getConfiguration().getEmailSmtpHost();
        emailSubject = TwitterfyConfiguration.getConfiguration().getEmailSubject();
    }

    public void createAndSendEmail(List<TweetModel> tweetModelList) {

        Properties props = new Properties();
        props.put(MAIL_SMTP_HOST, emailSmtpHost);
        props.put(MAIL_SMTP_SOCKET_FACTORY_PORT, port);
        props.put(MAIL_SMTP_SOCKET_FACTORY_CLASS,
                JAVAX_NET_SSL_SSLSOCKET_FACTORY);
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_PORT, port);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));

            Address[] addresses = new Address[toEmail.length];

            for(int i = 0; i < toEmail.length; i++) {
                addresses[i] = new InternetAddress(toEmail[i]);
            }

            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(emailSubject);
            message.setText(buildEmailBody(tweetModelList));

            Transport.send(message);
        }
        catch (MessagingException ex) {
            LOGGER.error("Could not send the email notification - reason: {}", ex.getMessage());
        }
    }

    private String buildEmailBody(List<TweetModel> tweetModelList) {
        final StringBuilder emailBody = new StringBuilder();
        final String currentTime = TimeUtil.parseTimestampToDateText(TimeUtil.currentTimestampUtc());

        emailBody
                .append("Date: ")
                .append(currentTime)
                .append("\n\n")
                .append("Tweet List: ")
                .append("\n");

        for(TweetModel tweetModel : tweetModelList) {
            emailBody
                    .append(TimeUtil.parseTimestampToDateText(tweetModel.getTimestamp()))
                    .append(" :: ")
                    .append(StringUtil.remoteNewLine(tweetModel.getTweet()))
                    .append(" :: ")
                    .append(tweetModel.getScreenName())
                    .append("\n----------------------------------------\n");
        }

        emailBody
                .append("\n")
                .append("Stay tunned for the next batch!")
                .append("\n")
                .append("Best,")
                .append("\n")
                .append("Twitterfy");

        return emailBody.toString();
    }
}

