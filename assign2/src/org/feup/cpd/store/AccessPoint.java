package org.feup.cpd.store;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class AccessPoint {

    private final InetAddress address;
    private final int port;

    public AccessPoint(String host, String port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = Integer.parseUnsignedInt(port);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return address.getHostAddress() + ':' + port;
    }
}
