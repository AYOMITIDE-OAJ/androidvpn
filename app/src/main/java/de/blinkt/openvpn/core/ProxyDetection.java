

package de.blinkt.openvpn.core;

import com.abc.evpnfree.R;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;


import de.blinkt.openvpn.VpnProfile;

public class ProxyDetection {
	static SocketAddress detectProxy(VpnProfile vp) {
		try {
			URL url = new URL(String.format("https://%s:%s",vp.mServerName,vp.mServerPort));
			Proxy proxy = getFirstProxy(url);

			if(proxy==null)
				return null;
			SocketAddress addr = proxy.address();
			if (addr instanceof InetSocketAddress) {
				return addr; 
			}
			
		} catch (MalformedURLException e) {
			VpnStatus.logError(R.string.getproxy_error, e.getLocalizedMessage());
		} catch (URISyntaxException e) {
			VpnStatus.logError(R.string.getproxy_error, e.getLocalizedMessage());
		}
		return null;
	}

	static Proxy getFirstProxy(URL url) throws URISyntaxException {
		System.setProperty("java.net.useSystemProxies", "true");

		List<Proxy> proxylist = ProxySelector.getDefault().select(url.toURI());


		if (proxylist != null) {
			for (Proxy proxy: proxylist) {
				SocketAddress addr = proxy.address();

				if (addr != null) {
					return proxy;
				}
			}

		}
		return null;
	}
}