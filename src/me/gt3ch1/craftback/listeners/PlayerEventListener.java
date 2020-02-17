package me.gt3ch1.craftback.listeners;

import me.gt3ch1.craftback.Main.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEventListener  implements Listener {

	@EventHandler
	public void OnLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		Main.playerArrayList.add(p);
		Main.playerNameArrayList.add(p.getName());
		Main.playerUUIDArrayList.add(p.getUniqueId().toString());

	}

//
	@EventHandler
	public void OnQuit(PlayerDisconnectEvent  e) {
		ProxiedPlayer p = e.getPlayer();
		int playerIndex = Main.playerArrayList.indexOf(p);
		Main.playerArrayList.remove(playerIndex);
		Main.playerNameArrayList.remove(playerIndex);
		Main.playerUUIDArrayList.remove(playerIndex);

	}
	
}
