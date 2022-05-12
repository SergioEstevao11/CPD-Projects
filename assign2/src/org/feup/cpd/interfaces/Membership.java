package org.feup.cpd.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Membership extends Remote {
    String join() throws RemoteException;
    String leave() throws RemoteException;
}
