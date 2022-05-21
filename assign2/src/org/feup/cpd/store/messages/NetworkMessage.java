package org.feup.cpd.store.messages;

public interface NetworkMessage<T extends Message> {
    String serialize();
    T deserialize(String message) throws IllegalArgumentException;
}
