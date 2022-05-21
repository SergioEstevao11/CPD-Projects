package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.NodeAccessPoint;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MembershipService {

    private final NodeAccessPoint cluster;
    private final NodeAccessPoint node;

    public MembershipService(NodeAccessPoint group, NodeAccessPoint node) {
        this.cluster = group;
        this.node = node;
    }

    public void listen() {
        ExecutorService pool = Executors.newFixedThreadPool(6);
        MembershipOperation operation = new MembershipOperation(pool, cluster, node);

        try {
            Membership membership = (Membership) UnicastRemoteObject.exportObject(operation, node.port());
            Registry registry = LocateRegistry.createRegistry(node.port());

            registry.rebind("Membership", membership);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
