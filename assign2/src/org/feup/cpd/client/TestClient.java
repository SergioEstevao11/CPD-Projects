package org.feup.cpd.client;

import java.io.IOException;

public class TestClient {

    public static void main(String[] args) {
        Operation operation = new Operation();
        String operationString = args[0];

        try {
            switch (operationString) {
                case "join" -> System.err.println("JOIN Not Yet Implemented");
                case "leave" -> System.err.println("LEAVE Not Yet Implemented");
                case "put" -> System.out.println(operation.put(args[1]));
                case "get" -> System.err.println("GET Not Yet Implemented");
                case "delete" -> System.err.println("DELETE Not Yet Implemented");
            }

        } catch (IOException | RuntimeException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        }
    }
}
