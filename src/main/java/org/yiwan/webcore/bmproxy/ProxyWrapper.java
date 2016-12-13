package org.yiwan.webcore.bmproxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import org.littleshoot.proxy.HttpFiltersSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;

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
        logger.info("starting proxy");
        proxy.start();

//        shutdown hook was added inside the proxy
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                proxy.stop();
//            }
//        });
    }

    public void stop() {
        logger.info("stopping proxy");
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
        logger.info("adding a new response filter to the proxy");
        proxy.addResponseFilter(filter);
    }

    /**
     * Adds a new filter factory (request/response interceptor) to the beginning of the HttpFilters chain.
     * <p/>
     * <b>Usage note:</b> The actual filter (interceptor) instance is created on every request by implementing the
     * {@link HttpFiltersSource#filterRequest(io.netty.handler.codec.http.HttpRequest, io.netty.channel.ChannelHandlerContext)} method and returning an
     * {@link org.littleshoot.proxy.HttpFilters} instance (typically, a subclass of {@link org.littleshoot.proxy.HttpFiltersAdapter}).
     * To disable or bypass a filter on a per-request basis, the filterRequest() method may return null.
     *
     * @param filterFactory factory to generate HttpFilters
     */
    public void addFirstHttpFilterFactory(HttpFiltersSource filterFactory) {
        proxy.addFirstHttpFilterFactory(filterFactory);
    }

    /**
     * Adds a new RequestFilter that can be used to examine and manipulate the
     * request before sending it to the server.
     *
     * @param filter filter instance
     */
    public void addReqeustFilter(RequestFilter filter) {
        logger.info("adding a new request filter to the proxy");
        proxy.addRequestFilter(filter);
    }

    public void setChainedProxy(String hostname, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        proxy.setChainedProxy(inetSocketAddress);
    }

    public void setHarCaptureTypes(Set<CaptureType> captureTypes) {
        proxy.setHarCaptureTypes(captureTypes);
    }

    public void setHarCaptureTypes(CaptureType... captureTypes) {
        proxy.setHarCaptureTypes(captureTypes);
    }

    public void enableHarCaptureTypes(CaptureType... captureTypes) {
        proxy.enableHarCaptureTypes(captureTypes);
    }

    public void whitelistRequests(Collection<String> urlPatterns, int statusCode) {
        proxy.whitelistRequests(urlPatterns, statusCode);
    }
}
