package me.gt3ch1.craftback.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class MainSQL {

	String dataAddress = "";
	String database = "";
	String fingerprint = "";
	String dataUsername = "";
	String dataPassword = "";
	String serverName = "";
	String serverHostName = "";
	String version = "";
	int port = 8080;

	public MainSQL(String da, String db, String f, String du, String dp, String sn, int p, String sh, String v) {

		this.dataAddress = da;
		this.database = db;
		this.fingerprint = f;
		this.dataUsername = du;
		this.dataPassword = dp;
		this.serverName = sn;
		this.serverHostName = sh;
		this.port = p;
		this.version = v;
		doSQL();

	}
	// This method sucks.
	public void doSQL() {
		Bukkit.getLogger().info(ChatColor.DARK_RED + "[[CraftBack]] Running SQL instances...");

		try {
			// Connect up to an SQL server
			Connection con = DriverManager.getConnection("jdbc:mysql://" + dataAddress + ":3306/" + database,
					dataUsername, dataPassword);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `Servers` WHERE `fingerprint` = '" + fingerprint + "'");
			
			if (rs.next()) {
				
				Bukkit.getLogger().info(ChatColor.DARK_RED + "[[CraftBack]] Updating SQL...");
				stmt.executeUpdate("UPDATE `Servers` SET `name`= '" + serverName + "' ,`port` = " + port +
						" ,`hostname` = '" + serverHostName + "' ,`maxplayers` = "+ Bukkit.getMaxPlayers() + ", `version` = '" + version + "' WHERE "
						+ "`fingerprint` = '" + fingerprint + "'");
				
			}

			else {
				
				Bukkit.getLogger().info(ChatColor.DARK_RED + "[[CraftBack]] Inserting SQL...");
				stmt.executeUpdate("INSERT INTO `Servers` (`name`,`port`,`fingerprint`,`hostname`,`maxplayers`,`version`) VALUES " + "('" + serverName
						+ "'," + port + ",'" + fingerprint + "', '" + serverHostName +"', "+Bukkit.getMaxPlayers() +", '"+version+"')");
				
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
