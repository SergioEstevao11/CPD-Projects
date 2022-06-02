package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MembershipService {

    private final AccessPoint cluster;
    private final Node node;

    public MembershipService(AccessPoint group, Node node) {
        this.cluster = group;
        this.node = node;
    }

    public void listen() {
        ExecutorService pool = Executors.newFixedThreadPool(6);

        try {
            MembershipOperation operation = new MembershipOperation(pool, cluster, node);
            Membership membership = (Membership) UnicastRemoteObject.exportObject(operation, node.getAccessPoint().getPort());
            Registry registry = LocateRegistry.createRegistry(node.getAccessPoint().getPort());
            registry.rebind("Membership", membership);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
