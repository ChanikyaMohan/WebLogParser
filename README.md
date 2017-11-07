# WebLogParser

    Windows 32/64 bit MySQL installer https://dev.mysql.com/downloads/file/?id=473605
    

# Database schemacreate database weblog;

    use weblog;

    show tables;

    drop table ipaddresses;
    drop table comments;
    drop table log_filtered;
    drop table log_dates;

    CREATE TABLE `ipaddresses` (
        `ip_id` INT NOT NULL AUTO_INCREMENT,
        `ip_address` VARCHAR(255) NOT NULL,
        PRIMARY KEY (`ip_id`)
    );

    CREATE TABLE `comments` (
        `comment_id` INT NOT NULL AUTO_INCREMENT,
        `comment_code` INT NOT NULL DEFAULT '0',
        `comment` VARCHAR(255) NOT NULL,
        PRIMARY KEY (`comment_id`)
    );

    CREATE TABLE `log_filtered` (
        `log_id` INT NOT NULL AUTO_INCREMENT,
        `ip_address` INT NOT NULL,
        `comment` INT NOT NULL,
        PRIMARY KEY (`log_id`)
    );

    CREATE TABLE `log_dates` (
        `log_date_id` INT NOT NULL AUTO_INCREMENT,
        `log_id` INT NOT NULL,
        `date_time` DATETIME,
        PRIMARY KEY (`log_date_id`)
    );

    ALTER TABLE `log_filtered` ADD CONSTRAINT `log_filtered_fk0` FOREIGN KEY (`ip_address`) REFERENCES `ipaddresses`(`ip_id`);

    ALTER TABLE `log_filtered` ADD CONSTRAINT `log_filtered_fk1` FOREIGN KEY (`comment`) REFERENCES `comments`(`comment_id`);

    ALTER TABLE `log_dates` ADD CONSTRAINT `log_dates_fk0` FOREIGN KEY (`log_id`) REFERENCES `log_filtered`(`log_id`);



# Using JDBC for database connection

    Steps: https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database
   
   1) Download and install the MySQL server. Just do it the usual way. Remember the port number whenever you've changed it. It's by default 3306.
   2) Download the JDBC driver and put in classpath, extract the ZIP file and put the containing JAR file in the classpath. The vendor-specific JDBC driver is a concrete implementation of the JDBC API (tutorial here).

If you're using an IDE like Eclipse or Netbeans, then you can add it to the classpath by adding the JAR file as Library to the Build Path in project's properties.

   3) Create a database in MySQL. Let's create a database javabase. You of course want World Domination, so let's use UTF-8 as well.
CREATE DATABASE javabase DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

   4) Determine the JDBC URL. To connect the MySQL database using Java you need an JDBC URL in the following syntax:

    jdbc:mysql://hostname:port/databasename
    hostname: The hostname where MySQL server is installed.
    
If it's installed at the same machine where you run the Java code, then you can just use localhost. It can also be an IP address like 127.0.0.1.
If you encounter connectivity problems and using 127.0.0.1 instead of localhost solved it, then you've a problem in your network/DNS/hosts config.

    port: The TCP/IP port where MySQL server listens on. This is by default 3306.
    databasename: The name of the database you'd like to connect to. That's javabase.
So the final URL should look like:
    jdbc:mysql://localhost:3306/javabase

   5) Test the connection to MySQL using Java. Create a simple Java class with a main() method to test the connection.

    String url = "jdbc:mysql://localhost:3306/javabase";
    String username = "java";
    String password = "password";

    System.out.println("Connecting database...");

    try (Connection connection = DriverManager.getConnection(url, username, password)) {
        System.out.println("Database connected!");
    } catch (SQLException e) {
        throw new IllegalStateException("Cannot connect the database!", e);
    }

If you get a SQLException: No suitable driver, then it means that either the JDBC driver wasn't autoloaded at all or that the JDBC URL is wrong (i.e. it wasn't recognized by any of the loaded drivers). Normally, a JDBC 4.0 driver should be autoloaded when you just drop it in runtime classpath. To exclude one and other, you can always manually load it as below:

    System.out.println("Loading driver...");

    try {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Driver loaded!");
    } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Cannot find the driver in the classpath!", e);
    }
    
Note that the newInstance() call is not needed here. It's just to fix the old and buggy org.gjt.mm.mysql.Driver. Explanation here. If this line throws ClassNotFoundException, then the JAR file containing the JDBC driver class is simply not been placed in the classpath.

Note that you don't need to load the driver everytime before connecting. Just only once during application startup is enough.

If you get a SQLException: Connection refused or Connection timed out or a MySQL specific CommunicationsException: 
Communications link failure, then it means that the DB isn't reachable at all. This can have one or more of the following causes:

IP address or hostname in JDBC URL is wrong.
Hostname in JDBC URL is not recognized by local DNS server.
Port number is missing or wrong in JDBC URL.
DB server is down.
DB server doesn't accept TCP/IP connections.
DB server has run out of connections.
Something in between Java and DB is blocking connections, e.g. a firewall or proxy. 

To solve the one or the other, follow the following advices:

Verify and test them with ping.
Refresh DNS or use IP address in JDBC URL instead.
Verify it based on my.cnf of MySQL DB.
Start the DB.
Verify if mysqld is started without the --skip-networking option.
Restart the DB and fix your code accordingly that it closes connections in finally.
Disable firewall and/or configure firewall/proxy to allow/forward the port. 

Note that closing the Connection is extremely important. If you don't close connections and keep getting a lot of them in a short time, then the database may run out of connections and your application may break. Always acquire the Connection in a try-with-resources statement. Or if you're not on Java 7 yet, explicitly close it in finally of a try-finally block. Closing in finally is just to ensure that it get closed as well in case of an exception. This also applies to Statement, PreparedStatement and ResultSet.
That was it as far the connectivity concerns. You can find here a more advanced tutorial how to load and store fullworthy Java model objects in a database with help of a basic DAO class.
