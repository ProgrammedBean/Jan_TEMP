package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL_Connection {
	
	private Connection conn;
	private Statement st;
	
	public static final String RED = "\u001B[31m";
	public static final String DEFAULT = "\u001B[0m";
	
	public SQL_Connection(Statement st) {
		this.st = st;
		if(register_driver() == 0) {
			this.conn = connect();
			if(this.conn != null) {
				System.out.println("Connected to database... Taking commands.");
				try {
					this.st = this.conn.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static int register_driver() {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
          System.out.println("JDBC NOT registered :(");
          return -1;
        }
		System.out.println("JDBC Registered!");
		return 0;
	}
	
	private static Connection connect() {
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "jan", "Mariachi65293!!");
		}
		catch(SQLException e) {
			System.out.println(RED + e + DEFAULT);
			return null;
		}
	}
	
	public static void close_connection(Connection conn) {
		try {
			conn.close();
		}
		catch(SQLException e) {
			System.out.println(RED + e + DEFAULT);
		}
	}

	public String read_one_sql_cmd(String cmd) {
		ResultSet rs;
		try {
			rs = this.st.executeQuery(cmd);
			boolean res = rs.next();
			if(res) {
				String result = rs.getString(1);
				return result;
			}
			return "";
		} catch (SQLException e) {
			System.out.println("Could not get result from command in 'read_sql_cmd'.");
			return null;
		}
	}
	
	public String[] read_col_sql_cmd(String cmd) {
		ResultSet[] rs = {null, null};
		String[] res_arr;
		int rs_cnt = 0;
		try {
			
			// run cmd once to get the col count
			rs[0] = this.st.executeQuery(cmd);
			while(rs[0].next())
				rs_cnt++;
			res_arr = new String[rs_cnt];
			
			// run cmd again to store cols in array
			rs[1] = this.st.executeQuery(cmd);
			for(int res_idx = 0; rs[1].next(); res_idx++)
				res_arr[res_idx] = rs[1].getString(1);
			
			// return the column array
			return res_arr;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] read_row_sql_cmd(String cmd, String[] labels) {
		ResultSet rs = null;
		String[] res_arr;
		try {
			// run cmd
			rs = this.st.executeQuery(cmd);
			rs.next();
			
			// get column values with each label
			res_arr = new String[labels.length];
			for(int lbl = 0; lbl < labels.length; lbl++)
				res_arr[lbl] = rs.getString(labels[lbl]);
			
			// return the row array
			return res_arr;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void write_sql_cmd(String cmd) {
		try {
			this.st.execute(cmd);
		}
		catch(SQLException e) {
			System.out.println("Could not write to server using '" + cmd + "'.");
		}
	}
}
