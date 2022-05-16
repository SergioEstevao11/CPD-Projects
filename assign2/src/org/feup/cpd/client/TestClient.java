package org.feup.cpd.client;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.NodeAccessPoint;

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

        NodeAccessPoint nodeAccessPoint = parseNodeAccessPoint(args[0]);
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

    private static void handleMembershipOperation(NodeAccessPoint nodeAccessPoint, String operation) {
        try {
            Registry registry = LocateRegistry.getRegistry(nodeAccessPoint.port());
            Membership remoteCall = (Membership) registry.lookup("Membership");

            if (operation.equals("join"))
                remoteCall.join();
            else if (operation.equals("leave"))
                remoteCall.leave();
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void handleKeyValueOperation(NodeAccessPoint nodeAccessPoint,
                                                String operation, String argument) throws IllegalArgumentException {
        ClientKeyValueOperation keyValueOperation = new ClientKeyValueOperation(nodeAccessPoint.toString());

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

    private static NodeAccessPoint parseNodeAccessPoint(String nodeAccessPoint) {
        String host = nodeAccessPoint.substring(0, nodeAccessPoint.indexOf(':'));
        String port = nodeAccessPoint.substring(nodeAccessPoint.indexOf(':') + 1);

        return NodeAccessPoint.buildStoreAddress(host, port);
    }
}
