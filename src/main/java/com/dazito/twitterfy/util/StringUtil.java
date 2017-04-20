package com.dazito.twitterfy.util;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by daz on 17/04/2017.
 */
public final class StringUtil {

    private StringUtil() {}

    public static String removeNewLine(String str) {
        return str
                // Replace new line by a space - avoid words being glued together
                .replace("\n", " ")
                .replace("\r", " ")
                // Remove double spaces
                .replace("  ", " ");
    }

    public static List<String> fetchUrlFromTweet(String tweet) {
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                .build();

        final Iterable<LinkSpan> links = linkExtractor.extractLinks(tweet);
        final Iterator<LinkSpan> iterator = links.iterator();

        List<String> fetchedLinkList = new ArrayList<>();

        while (iterator.hasNext()) {
            final LinkSpan link = iterator.next();

            link.getType();
            link.getBeginIndex();
            link.getEndIndex();

            final String url = tweet.substring(link.getBeginIndex(), link.getEndIndex());

            fetchedLinkList.add(url);
        }

        return fetchedLinkList;
    }
}
