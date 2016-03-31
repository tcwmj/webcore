package org.yiwan.webcore.test;


/**
 * Created by Kenny Wang on 3/30/2016.
 */
public class TestEnvironment {
    private Application application;
    private Database database;
    private Server applicationServer;
    private Server databaseServer;

    public Server getDatabaseServer() {
        return databaseServer;
    }

    public void setDatabaseServer(Server databaseServer) {
        this.databaseServer = databaseServer;
    }

    public Server getApplicationServer() {
        return applicationServer;
    }

    public void setApplicationServer(Server applicationServer) {
        this.applicationServer = applicationServer;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return String.format("application info:\n%s\ndatabase info:\n%s\napplication server info:\n%s\ndatabase server info:\n%s", application, database, applicationServer, databaseServer);
    }

    public class Application {
        private String url;
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return String.format("|_url: %s\n|_version: %s", url, version);
        }
    }

    public class Database {
        private String driver;
        private String url;
        private String user;
        private String password;
        private String version;
        private String dump;

        public String getDump() {
            return dump;
        }

        public void setDump(String dump) {
            this.dump = dump;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return String.format("|_driver: %s\n|_url: %s\n|_user: %s\n|_password: %s\n|_version: %s\n|_dump: %s", driver, url, user, password, version, dump);
        }
    }

    public class Server {
        private String os;
        private String version;
        private String cpu;
        private String memory;

        public String getMemory() {
            return memory;
        }

        public void setMemory(String memory) {
            this.memory = memory;
        }

        public String getCpu() {
            return cpu;
        }

        public void setCpu(String cpu) {
            this.cpu = cpu;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return String.format("|_os: %s\n|_version: %s\n|_cpu: %s\n|_memory: %s", os, version, cpu, memory);
        }
    }
}