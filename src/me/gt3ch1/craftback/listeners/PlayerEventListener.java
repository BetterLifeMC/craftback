package me.gt3ch1.craftback.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gt3ch1.craftback.Main.Main;

public class PlayerEventListener implements Listener{
	
	public static Main main;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		event.setJoinMessage("This is a test");
		p.sendMessage("I am testing this.");
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		
	}
}
