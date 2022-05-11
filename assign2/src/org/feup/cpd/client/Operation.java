package org.feup.cpd.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Operation {

    public Operation() {}


    public void join() {

    }

    public void leave() {

    }

    /**
     * Puts a file name in the system
     * @param fileName Name of the file to be inserted
     * @return Hash/Key for the file
     * @throws IOException If there are any errors while reading the input file
     */
    public String put(String fileName) throws IOException {
        Path filePath = Path.of(fileName);
        String fileContent = Files.readString(filePath);

        StringBuilder keyToHex = new StringBuilder(64);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] key = messageDigest.digest(fileContent.getBytes(StandardCharsets.UTF_8));

            for (byte b : key)
                keyToHex.append(String.format("%02x", b));

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return keyToHex.toString();
    }

    public void get(String key) {
        throw new RuntimeException();
    }

    public void delete() {
        throw new RuntimeException();
    }
}
