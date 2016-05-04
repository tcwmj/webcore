package org.yiwan.webcore.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestBase;
import org.yiwan.webcore.util.PropHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class HttpArchiveObserver extends SampleObserver {
    private final static Logger logger = LoggerFactory.getLogger(HttpArchiveObserver.class);
    private ProxyWrapper proxyWrapper;

    public HttpArchiveObserver(ProxyWrapper proxyWrapper) {
        this.proxyWrapper = proxyWrapper;
    }

    @Override
    public void start(ITestBase testCase) {
        super.start(testCase);
        newHar(testCase.getTransactionName());
    }

    @Override
    public void stop(ITestBase testCase) {
        super.stop(testCase);
        writeHar(testCase.getTransactionName());
    }

    private void newHar() {
        proxyWrapper.getProxy().newHar();
    }

    private void newHar(String initialPageRef) {
        proxyWrapper.getProxy().newHar(initialPageRef);
    }

    private void newHar(String initialPageRef, String initialPageTitle) {
        proxyWrapper.getProxy().newHar(initialPageRef, initialPageTitle);
    }

    /**
     * write har to a file
     *
     * @param filename file name without extension
     */
    private void writeHar(final String filename) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(filename);
            }
        };
        File[] files = new File(PropHelper.HAR_FOLDER).listFiles(filter);
        String filePath = PropHelper.HAR_FOLDER + filename + "_" + (files == null ? 0 : files.length) + ".har";
        new File(PropHelper.HAR_FOLDER).mkdirs();
        try {
            proxyWrapper.getProxy().getHar().writeTo(new FileWriter(filePath));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


}
