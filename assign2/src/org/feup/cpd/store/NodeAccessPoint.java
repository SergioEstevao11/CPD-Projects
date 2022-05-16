package org.feup.cpd.store;

import java.net.InetAddress;
import java.net.UnknownHostException;

public record NodeAccessPoint(InetAddress address, int port) {

    @Override
    public String toString() {
        return address().getHostAddress() + ':' + port;
    }

    public static NodeAccessPoint buildStoreAddress(String host, String port) {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            int inetAddressPort = Integer.parseInt(port);

            return new NodeAccessPoint(inetAddress, inetAddressPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        throw new IllegalArgumentException("Cannot build object of type StoreAddress");
    }
}
