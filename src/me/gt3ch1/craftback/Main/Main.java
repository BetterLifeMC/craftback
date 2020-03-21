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
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	/*
	 * TODO: Add JavaDoc to all the functions. TODO: Clean up uneccessary spaghetti
	 * code.
	 */
	static String ss;

	static final String VERSION = "1.5-Bungee";

	/**
	 * @return Server name specified in config.yml
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @return The server address specified in config.yml
	 */
	public String getDataAddress() {
		return dataAddress;
	}

	/**
	 * @return What SQL Database we will be working on as specified in config.yml.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @return The hostname of the server, specified in config.yml
	 */
	public String getServerHostName() {
		return serverHostName;
	}

	/**
	 * @return Get the user name that we are going to log 
	 * into the SQL database as, as specified in config.yml
	 */
	public String getDataUsername() {
		return dataUsername;
	}

	/**
	 * @return Get the password that we need to log into the
	 * SQL database, as specified in config.yml
	 */
	public String getDataPassword() {
		return dataPassword;
	}

	/**
	 * @return The randomly created fingerprint from config.yml
	 */
	public String getFingerprint() {
		return fingerprint;
	}

	/**
	 * @return Returns the port as specified in config.yml
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return Returns the boolean if we are going to use
	 * SQL, as specified in config.yml
	 */
	public boolean isUseSQL() {
		return useSQL;
	}
	/* Initialize all the strings and variables */
	public String serverName = "";
	public String dataAddress = "";
	public String database = "";
	public String dataUsername = "";
	public String dataPassword = "";
	public String fingerprint = "";
	public String serverHostName = "";
	public int port = 8080;
	public boolean useSQL = false;
	public static ArrayList<ProxiedPlayer> playerArrayList = new ArrayList<ProxiedPlayer>();
	public static ArrayList<String> playerNameArrayList = new ArrayList<String>();
	public static ArrayList<String> playerUUIDArrayList = new ArrayList<String>();
	public static boolean canDoHTTP = true;
	Thread t;

	/**
	 * Sets the command we are going to execute
	 * @param s
	 */
	public static void setCommand(String s) {
		ss = s;
	}

	/**
	 * @return The instance of the plugin
	 */
	public Main getPluginHere() {
		return this;
	}

	@Override
	public void onEnable() {
		// Enabled the HTTP loop 
		canDoHTTP = true;
		
	    // Creates the "CraftBack" folder if it doesn't exist.
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		// Gets the config.yml
		File file = new File(getDataFolder(), "config.yml");
		Configuration configuration = null;
		
		// If the file doesn't exist.
		if (!file.exists()) {

			try (InputStream in = getResourceAsStream("config.yml")) {
				
				//Copy the internal config.yml to the directory
				Files.copy(in, file.toPath());
				
				// Set the configuration variable
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(new File(getDataFolder(), "config.yml"));
				// Creates the fingerprint
				configuration.set("fingerprint", (int) Math.floor(100000 + Math.random() * 900000));
				
				// Saves the new config.yml
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,
						new File(getDataFolder(), "config.yml"));

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			try {
				// If the folder/file exists, use it!
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(new File(getDataFolder(), "config.yml"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Get the port
		port = configuration.getInt("port");
		// Get the useSQL boolean
		useSQL = configuration.getBoolean("useSQL");
		// Log some info
		BungeeCord.getInstance().getLogger().info(ChatColor.YELLOW + "[[CraftBack]] Using SQL: " + useSQL);
		
		// If we are using SQL
		if (useSQL) {
			
			// Set the necessary variables to their respective values
			serverName = configuration.getString("name");
			dataAddress = configuration.getString("data.address");
			database = configuration.getString("data.database");
			dataUsername = configuration.getString("data.username");
			dataPassword = configuration.getString("data.password");
			fingerprint = configuration.getString("fingerprint");
			serverHostName = configuration.getString("serverHostName");
			
			// Start the SQL insert/update
			new MainSQL(getDataAddress(), getDatabase(), getFingerprint(), getDataUsername(), getDataPassword(),
					getServerName(), getPort(), getServerHostName(), VERSION);

		}
		// Log that it has been enabled
		BungeeCord.getInstance().getLogger().info(ChatColor.GREEN + "[[CraftBack]] Enabled");
		
		// Create a new thread for CraftBackHttp
		t = new Thread(new Runnable() {

			public void run() {

				try {

					CraftBackHttp.start(port);

				} catch (IOException e) {

					e.printStackTrace();

				}
			}
		});
		
		// Log some info
		BungeeCord.getInstance().getLogger().info(ChatColor.GREEN + "[[CraftBack]] Starting webserver on port "
				+ ChatColor.BLUE + port + ChatColor.DARK_GREEN + "...");
		// Launch the thread
		t.start();
		
		// Log some info
		BungeeCord.getInstance().getLogger().info(ChatColor.GREEN + "[[CraftBack]] Started webserver.");
		
		// Start the command listener
		startCommandListener();
		
		// Register an event
		getProxy().getPluginManager().registerListener(this, new PlayerEventListener());
	}

	@Override
	public void onDisable() {
		canDoHTTP = false;
		try {
			CraftBackHttp.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		t.interrupt();
		BungeeCord.getInstance().getLogger().info(ChatColor.AQUA + "[[CraftBack]] Disabled");
	}

	/**
	 * Starts the command listener, which allows commands to be
	 * sent from the web interface.  It attempts to run the command sent
	 * once a second.
	 */
	public void startCommandListener() {

		getProxy().getScheduler().schedule(this, new Runnable() {

			@Override
			public void run() {

				if (Main.ss != null) {

					BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
							ss.toString());

					Main.ss = null;

				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

}
