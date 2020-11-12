package org.ctp.xpbank.database.tables;

import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;

import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;
import org.ctp.crashapi.db.tables.Table;

public class XpTable extends Table {

	public XpTable(SQLite db) {
		super(db, "xpbank", Arrays.asList("player"));
		addColumn("player", "varchar", "\"\"");
		addColumn("xp", "int", "0");
	}

	public <E> boolean hasRecord(String tableName, String key, E value) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + tableName + " WHERE " + key + " LIKE ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setObject(1, value);
			rs = ps.executeQuery();

			if (rs.next()) found = rs.getBoolean(1); // "found" column
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				getDb().getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}

	public Integer getExp(String player) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer integer = 0;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE player = '" + player + "';");

			rs = ps.executeQuery();
			while (rs.next())
				integer = rs.getInt("xp");
		} catch (SQLException ex) {
			getDb().getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				getDb().getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return integer;
	}

	public void setExp(String player, Integer integer) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasRecord(this.getName(), "player", player);
		try {
			conn = getDb().getSQLConnection();
			if (hasRecord) {
				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET xp = ? WHERE player = ?");

				ps.setInt(1, integer);

				ps.setString(2, player);
			} else {
				ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (player, xp) VALUES (?, ?)");

				ps.setInt(2, integer);

				ps.setString(1, player);
			}
			ps.executeUpdate();
		} catch (SQLException ex) {
			getDb().getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				getDb().getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}

}
