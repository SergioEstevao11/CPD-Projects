package org.feup.cpd.store.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Message {

    private static final String CRLF = "\r\n";

    private final StringBuilder header;
    private final StringBuilder body;
    private boolean headerClosed = false;

    protected Message() {
        header = new StringBuilder();
        body = new StringBuilder();
    }

    protected void closeHeader() {
        header.append(CRLF);
        headerClosed = true;
    }

    private int getNumberOfLines(String text) {
        Pattern pattern = Pattern.compile(CRLF);
        Matcher matcher = pattern.matcher(text);

        int lines = 0;
        while (matcher.find())
            lines++;

        return lines;
    }

    protected void addHeaderLine(String line) {
        if (!headerClosed)
            header.append(line).append(CRLF);
    }

    protected int getHeaderNumberOfLines() {
        return getNumberOfLines(header.toString());
    }

    protected void addBodyLine(String line) {
        body.append(line).append(CRLF);
    }

    protected int getBodyNumberOfLines() {
        return getNumberOfLines(body.toString());
    }

    protected String assemble() {
        String headerContent = header.toString();
        String bodyContent = body.toString();

        return headerContent + bodyContent;
    }
}
