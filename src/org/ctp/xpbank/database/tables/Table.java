package org.ctp.xpbank.database.tables;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.ctp.xpbank.database.columns.Column;
import org.ctp.xpbank.utils.ChatUtils;

public class Table {
	
	private String name, primary;
	private ArrayList<Column> columns = new ArrayList<Column>();
	private HashMap<String, String> conversions = new HashMap<String, String>();

	public Table(String name){
		this.name = name;
		conversions.put("int", "int(11) NOT NULL");
		conversions.put("varchar", "varchar(255) NOT NULL");
	}
	
	public Table(String name, String primary){
		this.name = name;
		conversions.put("int", "int(11) NOT NULL");
		conversions.put("varchar", "varchar(255) NOT NULL");
		this.primary = primary;
	}
	
	private boolean hasPrimaryKeys() {
		return primary != null;
	}
	
	public HashMap<String, String> getConversions(){
		return conversions;
	}

	public ArrayList<Column> getColumns() {
		return columns;
	}

	public void addColumn(String name, String type, String defaultValue) {
		columns.add(new Column(name, type, defaultValue));
	}

	public String getPrimary() {
		return primary;
	}

	public void setPrimary(String primary) {
		this.primary = primary;
	}

	public String getName() {
		return name;
	}
	
	public boolean tableExists(Connection connection) {
		try {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, name, null);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException ex) {

		}
		return false;
	}
	
	public void createTable(Connection connection){
		try{
			PreparedStatement s = connection.prepareStatement("PRAGMA table_info(" + name + ")");
			ResultSet rs = s.executeQuery();
			ArrayList<String> columnsInTable = new ArrayList<String>();
			boolean has_table = tableExists(connection);
			while(rs.next()){
				for(Column column : columns){
					if(column.getName().equalsIgnoreCase(rs.getString(2))){
						columnsInTable.add(rs.getString(2));
					}
				}
			}
			if(has_table) {
				for (Column column : columns) {
					if(!columnsInTable.contains(column.getName())){
						String statement = "ALTER TABLE " + name + " ADD COLUMN `" + column.getName() + "` " + conversions.get(column.getType()) + " DEFAULT " + column.getDefaultValue();
						if(column.getType().equals("autoint")) {
							ChatUtils.sendToConsole("Can't add auto increment value to existing table: skipping.");
							continue;
						}
						ChatUtils.sendToConsole(statement);
						Statement st = connection.createStatement();
						st.executeUpdate(statement);
						st.close();
					}
				}
			}else {
				if(hasPrimaryKeys()) {
					String statement = 
							"CREATE TABLE IF NOT EXISTS " + name + " (";
					for (Column column : columns) {
						statement += "`" + column.getName() + "` " + conversions.get(column.getType()) + " DEFAULT " + column.getDefaultValue() + ",";
					}
					String primaryString = primary;
					if(primaryString.length() > 0) {
						statement += "PRIMARY KEY (" + primaryString + "))";
					}else {
						statement = statement.substring(0, statement.length() - 1) + ")";
					}
					ChatUtils.sendToConsole(statement);
					try{
						Statement st = connection.createStatement();
						st.executeUpdate(statement);
						st.close();
					}catch(SQLException e){
						e.printStackTrace();
					}
				}else {
					ChatUtils.sendToConsole("Failed to add table " + name + ": primary keys undefined.");
				}
			}
		}catch(SQLException ex){
			if(ex.getMessage().equalsIgnoreCase("query does not return results")){
				String statement = 
						"CREATE TABLE IF NOT EXISTS " + name + " (";
				for (Column column : columns) {
					statement += "`" + column.getName() + "` " + conversions.get(column.getType()) + " DEFAULT " + column.getDefaultValue() + ",";
				}
				statement += "PRIMARY KEY (`" + primary + "`))";
				Bukkit.getConsoleSender().sendMessage(statement);
				try{
					Statement st = connection.createStatement();
					st.executeUpdate(statement);
					st.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}else{
				ex.printStackTrace();
			}
		}
	}
}
