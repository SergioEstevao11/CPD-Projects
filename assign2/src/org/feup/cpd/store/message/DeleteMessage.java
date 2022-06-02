package org.feup.cpd.store.message;

public class DeleteMessage extends Message{
    protected DeleteMessage(String type) {
        super(type);
    }

    @Override
    public String getContent() {
        return null;
    }
}
