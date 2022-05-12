package org.feup.cpd.client;

import org.feup.cpd.interfaces.Membership;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClient {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java TestClient <node_ap> <operation> [<opnd>]");
            System.exit(1);
        }

        String nodeAccessPoint = args[0];
        String operation = args[1];

        switch (operation) {
            case "join", "leave" -> handleMembershipOperation(nodeAccessPoint, operation);
            case "put", "get", "delete" -> {
                String operationArgument = args[2];
                handleKeyValueOperation(nodeAccessPoint, operation, operationArgument);
            }
            default -> throw new IllegalArgumentException("Unexpected operation: " + operation);
        }
    }

    // FIXME: I don't have any idea about what should this method return - Mike
    private static void handleMembershipOperation(String nodeAccessPoint, String operation) {
        String remoteCallReturn = "";

        try {
            Registry registry = LocateRegistry.getRegistry(nodeAccessPoint);
            Membership remoteCall = (Membership) registry.lookup("Membership");

            if (operation.equals("join"))
                remoteCallReturn = remoteCall.join();
            else if (operation.equals("leave"))
                remoteCallReturn = remoteCall.leave();

        } catch (RemoteException | NotBoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // FIXME: printing for now, this should (must) be changed later, idk for what - Mike
        System.out.println(remoteCallReturn);
    }

    private static void handleKeyValueOperation(String nodeAccessPoint, String operation, String argument) throws IllegalArgumentException {
        ClientKeyValueOperation keyValueOperation = new ClientKeyValueOperation(nodeAccessPoint);

        switch (operation) {
            case "put" -> {
                String key = keyValueOperation.putClientSetup(argument);
                System.out.println("File saved with the following key: " + key);
            }
            case "get" -> {
                File file = keyValueOperation.get(argument);
                System.out.println("File saved to the following location: " + file.getPath());
            }
            case "delete" -> {
                keyValueOperation.delete(argument);
                System.out.println("Erased the following key: " + argument);
            }
            default -> throw new IllegalArgumentException("Unexpected argument: " + argument);
        }
    }
}
