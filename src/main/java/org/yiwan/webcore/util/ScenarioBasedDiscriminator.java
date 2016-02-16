package org.yiwan.webcore.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;
import cucumber.api.Scenario;
import cucumber.api.java.Before;

/**
 * Created by Kenny Wang on 2/4/2016.
 */
public class ScenarioBasedDiscriminator implements Discriminator<ILoggingEvent> {

    private static final String KEY = "scenario";

    private boolean started;

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return scenario.getId();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
