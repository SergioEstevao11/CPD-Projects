package org.feup.cpd.store.message;

public class PutMessage extends Message{
    protected PutMessage(String type) {
        super(type);
    }

    @Override
    public String getContent() {
        return null;
    }
}
