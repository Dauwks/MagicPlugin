package com.elmakers.mine.bukkit.protection;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TownyManager implements PVPManager, BlockBreakManager, BlockBuildManager {
	private boolean enabled = false;
	private TownyAPI towny = null;
    protected boolean wildernessBypass;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled && towny != null;
	}

	public void initialize(Plugin plugin) {
		if (enabled) {
			try {
				Plugin townyPlugin = plugin.getServer().getPluginManager()
						.getPlugin("Towny");
				if (townyPlugin != null) {
					towny = new TownyAPI(this, townyPlugin);
				}
			} catch (Throwable ex) {
			}

			if (towny == null) {
				plugin.getLogger()
						.info("Towny not found, region protection and pvp checks will not be used.");
			} else {
				plugin.getLogger()
						.info("Towny found, will respect build permissions for construction spells");
			}
		} else {
			plugin.getLogger()
					.info("Towny manager disabled, region protection and pvp checks will not be used.");
			towny = null;
		}
	}

    public void setWildernessBypass(boolean bypass) {
        wildernessBypass = bypass;
    }

	public boolean hasBuildPermission(Player player, Block block) {
		if (enabled && block != null && towny != null) {
			return towny.hasBuildPermission(player, block);
		}
		return true;
	}

    public boolean hasBreakPermission(Player player, Block block) {
        if (enabled && block != null && towny != null) {
            return towny.hasBreakPermission(player, block);
        }
        return true;
    }

    public boolean isAlly(Player player, Player other) {
        if (enabled && player != null && towny != null && other != null) {
            return towny.isAlly(player, other);
        }
        return false;
    }

    @Override
    public boolean isPVPAllowed(Player player, Location location) {
        if (!enabled || towny == null || location == null)
            return true;
        return towny.isPVPAllowed(location);
    }
}
