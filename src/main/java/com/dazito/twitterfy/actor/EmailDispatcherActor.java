package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.mail.EmailClient;

/**
 * Created by daz on 16/04/2017.
 */
public class EmailDispatcherActor extends UntypedActor {

    private boolean isActive;
    private EmailClient emailClient;

    public static Props props() {
        return Props.create(EmailDispatcherActor.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        isActive = TwitterfyConfiguration.getConfiguration().isEmailActive();
        emailClient = new EmailClient();
    }

    @Override
    public void onReceive(Object message) throws Throwable {

    }
}
