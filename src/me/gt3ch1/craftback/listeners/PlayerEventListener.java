package me.gt3ch1.craftback.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gt3ch1.craftback.Main.Main;

public class PlayerEventListener implements Listener{
	Main craftback;
	public PlayerEventListener(Main m) {
		this.craftback = m;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
//		p.sendMessage("I am testing this.");
		craftback.addPlayerToArrayLists(p);
		System.out.println(Main.playerUUIDAndNameList.toString());

	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		craftback.removePlayerFromArrayLists(p);
		
	}
}
