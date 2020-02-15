package me.gt3ch1.craftback.Main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Commander extends BukkitRunnable {
	private final JavaPlugin plugin;
	public Commander(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(this.plugin, () -> {
			if(Main.ss != null)
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Main.ss);
			Main.ss = null;
        });
	}

}
