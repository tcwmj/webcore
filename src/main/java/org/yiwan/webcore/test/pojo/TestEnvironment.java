package org.yiwan.webcore.test.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.Helper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kenny Wang on 3/30/2016.
 */
public class TestEnvironment {
    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(TestEnvironment.class);
    private List<ApplicationServer> applicationServers;
    private List<DatabaseServer> databaseServers;

    public List<DatabaseServer> getDatabaseServers() {
        return databaseServers;
    }

    public void setDatabaseServers(List<DatabaseServer> databaseServers) {
        this.databaseServers = databaseServers;
    }

    public List<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(List<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }

    public DatabaseServer getDatabaseServer(String name) throws Exception {
        return (DatabaseServer) Helper.filterListByName(databaseServers, name);
    }

    public ApplicationServer getApplicationServer(String name) throws Exception {
        return (ApplicationServer) Helper.filterListByName(applicationServers, name);
    }

    @Override
    public String toString() {
        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }


    public static void main(String[] args) throws Exception {
        String json = "[{\"applicationServers\":[{\"name\":\"default\",\"url\":\"http://localhost:8080/\"}],\"databaseServers\":[{\"name\":\"default\",\"dump\":\"data/system/default.xml\"}]}]";
        List<TestEnvironment> testEnvironments = (new ObjectMapper()).readValue(json, new TypeReference<List<TestEnvironment>>() {
        });
        logger.info(testEnvironments.get(0).getApplicationServer("default").getUrl());

        TestEnvironment a = new TestEnvironment();
        ApplicationServer b = new ApplicationServer();
        Server c = new Server();
        DatabaseServer d = new DatabaseServer();
        b.setConfiguration(c);
        d.setConfiguration(c);
        a.setApplicationServers(Arrays.asList(b));
        a.setDatabaseServers(Arrays.asList(d));
        logger.info(a.toString());
    }
}