package org.feup.cpd.store.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Message {

    private static final String CRLF = "\r\n";

    private final StringBuilder header;
    private final StringBuilder body;

    protected Message() {
        header = new StringBuilder();
        body = new StringBuilder();
    }

    protected void addHeaderLine(String line) {
        header.append(line).append(CRLF);
    }

    protected int getHeaderNumberOfLines() {
        Pattern pattern = Pattern.compile(CRLF);
        Matcher matcher = pattern.matcher(header.toString());

        int lines = 0;
        while (matcher.find())
            lines++;

        return lines;
    }

    protected void addBodyLine(String line) {
        body.append(line).append(CRLF);
    }

    protected int getBodyNumberOfLines() {
        Pattern pattern = Pattern.compile(CRLF);
        Matcher matcher = pattern.matcher(body.toString());

        int lines = 0;
        while (matcher.find())
            lines++;

        return lines;
    }

    protected String assemble() {
        String headerContent = header.toString();
        String bodyContent = body.toString();

        return headerContent + bodyContent;
    }

    protected void clean() {
        header.delete(0, header.length());
        body.delete(0, body.length());
    }
}
