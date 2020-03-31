package me.gt3ch1.craftback.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.gt3ch1.craftback.http.CraftBackHttp;
import me.gt3ch1.craftback.listeners.PlayerEventListener;
import me.gt3ch1.craftback.sql.MainSQL;

public class Main extends JavaPlugin {
	/*
	 * TODO:
	 * 		Add JavaDoc to all the functions.
	 * TODO:
	 * 		Clean up uneccessary spaghetti code.
	 * 
	 */
	static String ss;
	private Listener l;
	
	static final String VERSION = "1.4-Spigot";
	
	public String getServerName() {
		return serverName;
	}
	
	public String getDataAddress() {
		return dataAddress;
	}

	public String getDatabase() {
		return database;
	}

	public String getServerHostName() {
		return serverHostName;
	}

	public String getDataUsername() {
		return dataUsername;
	}

	public String getDataPassword() {
		return dataPassword;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public int getPort() {
		return port;
	}

	public boolean isUseSQL() {
		return useSQL;
	}
	public static boolean isServingHTTP() {
		return isServingHTTP;
	}
	public String serverName = "";
	public String dataAddress = "";
	public String database = "";
	public String dataUsername = "";
	public String dataPassword = "";
	public String fingerprint = "";
	public String serverHostName = "";
	public int port = 8080;
	public boolean useSQL = false;
	public static boolean isServingHTTP = true;
	public ArrayList<Player> playerArrayList = new ArrayList<Player>();
	static public ArrayList<String> playerNameArrayList = new ArrayList<String>();
	static public ArrayList<String> playerUUIDArrayList = new ArrayList<String>();

	Thread t;

	public static void setCommand(String s) {
		ss = s;
	}

	public Main getPluginHere() {
		return this;
	}

	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		isServingHTTP = true;
		File f = new File("plugins/CraftBack/config.yml");
		if (!f.exists()) {
			
			this.saveDefaultConfig();
			this.getConfig().set("fingerprint", RandomStringUtils.randomAlphanumeric(10));
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
			
		}

		this.saveDefaultConfig();

		port = this.getConfig().getInt("port");
		useSQL = this.getConfig().getBoolean("useSQL");
		Bukkit.getLogger().info(ChatColor.YELLOW + "[[CraftBack]] Using SQL: " + useSQL);

		if (useSQL) {

			serverName = this.getConfig().getString("name");
			dataAddress = this.getConfig().getString("data.address");
			database = this.getConfig().getString("data.database");
			dataUsername = this.getConfig().getString("data.username");
			dataPassword = this.getConfig().getString("data.password");
			fingerprint = this.getConfig().getString("fingerprint");
			serverHostName = this.getConfig().getString("serverHostName");
			new MainSQL(getDataAddress(), getDatabase(), getFingerprint(), getDataUsername(), getDataPassword(),
					getServerName(), getPort(), getServerHostName(), VERSION);
			
		}
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			addPlayerToArrayLists(p);
		}
		Bukkit.getLogger().info(ChatColor.GREEN + "[[CraftBack]] Enabled");

		t = new Thread(new Runnable() {

			public void run() {
				try {

					CraftBackHttp.start(port);

				} catch (IOException e) {

					e.printStackTrace();

				}
			}
		});

		Bukkit.getLogger().info(ChatColor.GREEN + "[[CraftBack]] Starting webserver on port " + ChatColor.BLUE + port
				+ ChatColor.DARK_GREEN + "...");
		t.start();
		Bukkit.getLogger().info(ChatColor.GREEN + "[[CraftBack]] Started webserver.");
		BukkitTask task = new Commander(getPluginHere()).runTaskTimer(getPluginHere(), 5, 20);
		l = new PlayerEventListener(getPluginHere());
		Bukkit.getPluginManager().registerEvents(l, this);

	}

	@Override
	public void onDisable() {
		isServingHTTP = false;
		l = null;
		t.interrupt();
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			removePlayerFromArrayLists(p);
		}
		try {
			CraftBackHttp.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bukkit.getLogger().info(ChatColor.AQUA + "[[CraftBack]] Disabled");
		
	}

	public void addPlayerToArrayLists(Player p) {

		playerArrayList.add(p);
		playerNameArrayList.add(p.getName());
		playerUUIDArrayList.add(p.getUniqueId().toString());

	}

	public void removePlayerFromArrayLists(Player p) {

		int playerIndex = playerArrayList.indexOf(p);
		playerArrayList.remove(playerIndex);
		playerNameArrayList.remove(playerIndex);
		playerUUIDArrayList.remove(playerIndex);

	}

}
