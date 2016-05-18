package org.yiwan.webcore.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ProxyWrapper {
    public final static String CONTENT_DISPOSITION = "Content-Disposition";
    private final static Logger logger = LoggerFactory.getLogger(ProxyWrapper.class);
    private BrowserMobProxy proxy;

    public ProxyWrapper() {
        this(new BrowserMobProxyServer());
    }

    public ProxyWrapper(BrowserMobProxy proxy) {
        this.proxy = proxy;
    }

    public BrowserMobProxy getProxy() {
        return proxy;
    }

    public void start() {
        proxy.setHarCaptureTypes(CaptureType.getRequestCaptureTypes());
        proxy.start();

//        shutdown hook was added inside the proxy
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                proxy.stop();
//            }
//        });
    }

    public void stop() {
        if (proxy.isStarted()) {
            proxy.stop();
        }
    }

    /**
     * Adds a new ResponseFilter that can be used to examine and manipulate the
     * response before sending it to the client.
     *
     * @param filter filter instance
     */

    public void addResponseFilter(ResponseFilter filter) {
        logger.debug("add a new response filter to the proxy");
        proxy.addResponseFilter(filter);
    }

    /**
     * Adds a new RequestFilter that can be used to examine and manipulate the
     * request before sending it to the server.
     *
     * @param filter filter instance
     */
    public void addReqeustFilter(RequestFilter filter) {
        logger.debug("add a new request filter to the proxy");
        proxy.addRequestFilter(filter);
    }

    public void setChainedProxy(String hostname, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        proxy.setChainedProxy(inetSocketAddress);
    }
}
