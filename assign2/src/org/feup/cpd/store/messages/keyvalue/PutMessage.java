package org.feup.cpd.store.messages.keyvalue;

import org.feup.cpd.store.messages.Message;
import org.feup.cpd.store.messages.NetworkMessage;

public class PutMessage extends Message implements NetworkMessage<Message> {
    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Message deserialize(String message) throws IllegalArgumentException {
        return null;
    }
}
