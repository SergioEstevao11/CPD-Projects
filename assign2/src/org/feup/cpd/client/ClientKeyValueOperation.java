package org.feup.cpd.client;

import org.feup.cpd.interfaces.KeyValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientKeyValueOperation implements KeyValue {
    private final String nodeAccessPoint;

    public ClientKeyValueOperation(String nodeAccessPoint) {
        this.nodeAccessPoint = nodeAccessPoint;
    }

    public String putClientSetup(String fileName) {
        StringBuilder keyToBigEndian = new StringBuilder(64);
        String value = "";

        try {
            Path filePath = Path.of(fileName);
            value = Files.readString(filePath);

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

            for (byte b : hash)
                keyToBigEndian.append(String.format("%02x", b));

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String key = keyToBigEndian.toString();
        put(key, value);

        return key;
    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public File get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException();
    }
}
