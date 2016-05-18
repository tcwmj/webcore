package org.yiwan.webcore.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.yiwan.webcore.test.pojo.ApplicationServer;
import org.yiwan.webcore.test.pojo.DatabaseServer;
import org.yiwan.webcore.test.pojo.Server;
import org.yiwan.webcore.test.pojo.TestEnvironment;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Kenny Wang on 5/12/2016.
 */
public class TestEnvironmentTest {
    private static final Logger logger = LoggerFactory.getLogger(TestEnvironmentTest.class);

    @Test
    public void testJsonToTestEnviroment() throws Exception {
        String json = "[{\"applicationServers\":[{\"name\":\"default\",\"url\":\"http://localhost:8080/\"}],\"databaseServers\":[{\"name\":\"default\",\"dump\":\"data/system/default.xml\"}]}]";
        List<TestEnvironment> testEnvironments = (new ObjectMapper()).readValue(json, new TypeReference<List<TestEnvironment>>() {
        });
        assertThat(testEnvironments.get(0).getApplicationServer("default").getUrl()).isEqualTo("http://localhost:8080/");
    }

    @Test
    public void testTestEnviromentToJson() {
        TestEnvironment a = new TestEnvironment();
        ApplicationServer b = new ApplicationServer();
        DatabaseServer d = new DatabaseServer();
        Server c = new Server();
        b.setConfiguration(c);
        d.setConfiguration(c);
        a.setApplicationServers(Arrays.asList(b));
        a.setDatabaseServers(Arrays.asList(d));
        logger.info(a.toString());
    }
}
