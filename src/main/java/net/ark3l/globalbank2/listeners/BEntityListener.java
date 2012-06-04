package net.ark3l.globalbank2.listeners;

import net.ark3l.globalbank2.banker.entity.Banker;
import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.util.SqliteDB;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

@SuppressWarnings("deprecation")
public class BEntityListener implements Listener {
	public final GlobalBank b;

	public BEntityListener(GlobalBank b) {
		this.b = b;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		if (b.manager.isNPC(event.getEntity())) {
			Player player = (Player) ((EntityDamageByEntityEvent) event).getDamager();
			if (b.playersDeletingBankers.contains(player)) {
				Banker banker = b.manager.getBanker(b.manager.getNPCIdFromEntity(event.getEntity()));
				SqliteDB.delBanker(banker.bankName);
				b.manager.despawnById(b.manager.getNPCIdFromEntity(banker.getBukkitEntity()));
				player.sendMessage(ChatColor.BLUE + "[GlobalBank2]"
						+ ChatColor.WHITE
						+ " Banker has been removed.");
				b.playersDeletingBankers.remove(player);
			} else if(b.playersChangingBankersDirection.contains(player)) {
				Banker banker = b.manager.getBanker(b.manager.getNPCIdFromEntity(event.getEntity()));
				banker.turnToFace(player.getLocation());
				SqliteDB.delBanker(banker.bankName);
				SqliteDB.newBanker(banker.bankName, banker.getBukkitEntity().getLocation());
				b.playersChangingBankersDirection.remove(player);
			}
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;
		if (b.manager.isNPC(event.getTarget())) {
			event.setCancelled(true);
		}

	}
}
