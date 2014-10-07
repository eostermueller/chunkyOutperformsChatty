package com.github.eostermueller.pgbench;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PgBenchUtil {

	public static final int UN_INIT = -1;
	public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));    


	public static Date getDate(ResultSet rs, int colNum) throws SQLException {
		Timestamp ts = rs.getTimestamp(colNum,tzUTC); // column is TIMESTAMPTZ
		return  ts !=null ? new Date(ts.getTime())  : null; 		
	}
	public static void closeQuietly(Connection con) {
		try {
			if (con!=null)
				con.close();
		} catch (SQLException e) {
			//ignore
		} 
	}
	public static void closeQuietly(ResultSet rs) {
		try {
			if (rs!=null)
				rs.close();
		} catch (SQLException e) {
			//ignore
		} 
	}
	public static void closeQuietly(Statement stmt) {
		try {
			if (stmt!=null)
				stmt.close();
		} catch (SQLException e) {
			//ignore
		} 
	}
	
}
