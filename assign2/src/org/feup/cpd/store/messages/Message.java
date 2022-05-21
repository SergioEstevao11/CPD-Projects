package org.feup.cpd.store.messages;


public abstract class Message {

    private static final String CRLF = "\r\n";

    private final StringBuilder header;
    private final StringBuilder body;

    protected Message() {
        this.header = new StringBuilder();
        this.body = new StringBuilder();
    }

    protected void addHeaderLine(String line) {
        header.append(line).append(CRLF);
    }

    @Override
    public String toString() {
        return header + CRLF + body;
    }
}
