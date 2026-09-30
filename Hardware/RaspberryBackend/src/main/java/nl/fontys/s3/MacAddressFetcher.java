package nl.fontys.s3;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class MacAddressFetcher {
    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Skip loopback or virtual interfaces
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }

                byte[] mac = networkInterface.getHardwareAddress();

                if (mac == null) {
                    continue;
                }

                StringBuilder macAddress = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                return macAddress.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
