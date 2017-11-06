package jdbc_connection;

import java.sql.*;

public class MySQLConn {
	public void runConn(){
			try{
				Class.forName("com.mysql.jdbc.Driver");
				//here weblog is database name, root is username and password is passSQL!
				Connection con=DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/weblog","root","passSQL!");

				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select * from ipadresses");
				while(rs.next())
					System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
				con.close();
			}catch(Exception e){
				System.out.println(e);
			}
		}
}
