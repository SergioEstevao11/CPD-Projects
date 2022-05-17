package org.feup.cpd.store;

import org.feup.cpd.interfaces.Membership;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Store {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: java Store <IP_mcast_addr> <IP_mcast_port> <node_id>  <Store_port>");
            System.exit(1);
        }

        NodeAccessPoint clusterId = NodeAccessPoint.buildStoreAddress(args[0], args[1]);
        NodeAccessPoint nodeId = NodeAccessPoint.buildStoreAddress(args[2], args[3]);

        try {
            StoreMembershipOperation membershipOperation = new StoreMembershipOperation(clusterId, nodeId);
            Membership membership = (Membership) UnicastRemoteObject.exportObject(membershipOperation, nodeId.port());

            Registry registry = LocateRegistry.createRegistry(nodeId.port());
            System.err.println("RMI registry created");

            // TODO: probably this should call unbind after
            registry.rebind("Membership", membership);
            System.err.println("Node is ready to receive RMI requests");

        } catch (RemoteException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
