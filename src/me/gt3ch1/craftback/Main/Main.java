package me.gt3ch1.craftback.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import me.gt3ch1.craftback.http.CraftBackHttp;
import me.gt3ch1.craftback.listeners.PlayerEventListener;
import me.gt3ch1.craftback.sql.MainSQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin {
	/*
	 * TODO: Add JavaDoc to all the functions. Clean up uneccessary spaghetti code.
	 * Add catch in CraftBackHttp for a message of "?" Add versioning info about
	 * plugin for SQL. Make branch for BungeeCord proxies.
	 * 
	 */
	static String ss;

	static final double VERSION = 1.4;

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

	public String serverName = "";
	public String dataAddress = "";
	public String database = "";
	public String dataUsername = "";
	public String dataPassword = "";
	public String fingerprint = "";
	public String serverHostName = "";
	public int port = 8080;
	public boolean useSQL = false;
	static public ArrayList<ProxiedPlayer> playerArrayList = new ArrayList<ProxiedPlayer>();
	static public ArrayList<String> playerNameArrayList = new ArrayList<String>();
	static public ArrayList<String> playerUUIDArrayList = new ArrayList<String>();

	Thread t;

	public static void setCommand(String s) {
		ss = s;
	}

	public Main getPluginHere() {
		return this;
	}

	@Override
	public void onEnable() {

		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		File file = new File(getDataFolder(), "config.yml");
		Configuration configuration = null;

		if (!file.exists()) {
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(new File(getDataFolder(), "config.yml"));
				configuration.set("fingerprint", Math.floor(100000 + Math.random() * 900000));
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,
						new File(getDataFolder(), "config.yml"));

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(new File(getDataFolder(), "config.yml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		port = configuration.getInt("port");
		useSQL = configuration.getBoolean("useSQL");
		getLogger().info(ChatColor.YELLOW + "[[CraftBack]] Using SQL: " + useSQL);

		if (useSQL) {

			serverName = configuration.getString("name");
			dataAddress = configuration.getString("data.address");
			database = configuration.getString("data.database");
			dataUsername = configuration.getString("data.username");
			dataPassword = configuration.getString("data.password");
			fingerprint = configuration.getString("fingerprint");
			serverHostName = configuration.getString("serverHostName");
			new MainSQL(getDataAddress(), getDatabase(), getFingerprint(), getDataUsername(), getDataPassword(),
					getServerName(), getPort(), getServerHostName(), VERSION);
		}

		getLogger().info(ChatColor.GREEN + "[[CraftBack]] Enabled");

		t = new Thread(new Runnable() {

			public void run() {
				try {

					CraftBackHttp.start(port);

				} catch (IOException e) {

					e.printStackTrace();

				}
			}
		});

		getLogger().info(ChatColor.GREEN + "[[CraftBack]] Starting webserver on port " + ChatColor.BLUE + port
				+ ChatColor.DARK_GREEN + "...");
		t.start();
		getLogger().info(ChatColor.GREEN + "[[CraftBack]] Started webserver.");
//		TaskScheduler task = new Commander(getPluginHere()).runTaskTimer(getPluginHere(), 5, 20);
		startCommandListener();
		getProxy().getPluginManager().registerListener(this, new PlayerEventListener());
	}

	@Override
	public void onDisable() {

		t.interrupt();
		getLogger().info(ChatColor.AQUA + "[[CraftBack]] Disabled");
	}

	public void startCommandListener() {
		
		System.out.println("STARTING LISTENER");
		
		getProxy().getScheduler().schedule(this, new Runnable() {
			
			@Override
			public void run() {
				System.out.println("IN LOOP --> Command: " + Main.ss);
				if (Main.ss != null) {

					BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
							Main.ss);
					Main.ss = null;
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}


}
