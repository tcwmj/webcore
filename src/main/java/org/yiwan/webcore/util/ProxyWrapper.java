package org.yiwan.webcore.util;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyWrapper {
    public final static String CONTENT_DISPOSITION = "Content-Disposition";
    private final static Logger logger = LoggerFactory.getLogger(ProxyWrapper.class);
    private final static BrowserMobProxy proxy = new BrowserMobProxyServer();

    static {
        if (PropHelper.ENABLE_PROXY) {
            logger.debug("start proxy");
            proxy.setHarCaptureTypes(CaptureType.getRequestCaptureTypes());
            proxy.start(0);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.debug("gracefully shutting down proxy");
                    if (proxy.isStarted()) {
                        proxy.stop();
                    }
                }
            });
        }
    }

    public static BrowserMobProxy getProxy() {
        return proxy;
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

}
