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

