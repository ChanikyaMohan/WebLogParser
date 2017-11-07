package jdbc_connection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLConn {
	Connection con;
	public MySQLConn() {
		// TODO Auto-generated constructor stub
		try{
			Class.forName("com.mysql.jdbc.Driver");
			//here weblog is database name, root is username and password is passSQL!
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/weblog?autoReconnect=true","root","passSQL!");
//
//			Statement stmt=con.createStatement();
//			ResultSet rs=stmt.executeQuery("select * from ipadresses");
//			while(rs.next())
//				//System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
//			con.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}

	public void clearAllTables() throws SQLException{
		Statement stmt=con.createStatement();
		List<String> tables = new ArrayList<String>();
		//getting all tables in weblog database
		ResultSet rs=stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA='weblog';");
		while(rs.next()){
			tables.add(rs.getString(1));
		}
		String query;
		tables.clear();
		tables.add("log_dates");
		tables.add("log_filtered");
		tables.add("comments");
		tables.add("ipaddresses");

		for(String table : tables){
			try{
				query = "DELETE FROM "+ table +";";
				PreparedStatement preparedStmt = con.prepareStatement(query);
			    preparedStmt.execute();
				stmt.execute(query);
			}catch (Exception delException){
			      System.err.println("Got an exception! ");
			      System.err.println(delException.getMessage());
			}
		}
	}

	public void closeConnection() throws SQLException{
		con.close();
	}

	public int insertIntoIpaddresses(String ipaddress) throws SQLException{
		Statement stmt=con.createStatement();
		ResultSet rs=stmt.executeQuery("select * from ipaddresses where ip_address='"+ipaddress+"';");
		if(rs.next()){
			//already existing in table
			//id of ip_address tuple
			return rs.getInt(1);
		}
		//not present in table so insert it
		String query = "insert into ipaddresses (ip_address) values ('"+ipaddress +"');";
		PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		return keys.getInt(1);
	}

	public int insertIntoComments(String comment) throws SQLException{
		Statement stmt=con.createStatement();
		ResultSet rs=stmt.executeQuery("select * from comments where comment='"+comment+"';");
		if(rs.next()){
			//already existing in table
			//id of comment tuple
			return rs.getInt(1);
		}
		//not present in table so insert it
		String query = "insert into comments (comment) values ('"+ comment +"');";
		PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		return keys.getInt(1);
	}

	public int insertIntoLogFiltered(int ip_id, int comment_id) throws SQLException{
		String query = "insert into log_filtered (ip_address,comment) values ('"+ ip_id +"','"+ comment_id +"');";
		PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		//reurn log_id
		return keys.getInt(1);
	}

	public void insertIntoLogDates(int log_id, LocalDateTime localDateTime) throws SQLException{
		String query = "insert into log_dates (log_id,date_time) values ('"+ log_id +"','"+ localDateTime.toString() +"');";
		PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		//reurn insert log_datetime_id
		//return keys.getInt(1);
	}

//	public void runConn(){
//	try{
//		Class.forName("com.mysql.jdbc.Driver");
//		//here weblog is database name, root is username and password is passSQL!
//		Connection con=DriverManager.getConnection(
//		"jdbc:mysql://localhost:3306/weblog","root","passSQL!");
//
//		Statement stmt=con.createStatement();
//		ResultSet rs=stmt.executeQuery("select * from ipadresses");
//		while(rs.next())
//			System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
//		con.close();
//	}catch(Exception e){
//		System.out.println(e);
//	}
//}
}
