package com.elmakers.mine.bukkit.plugins.magic.spells;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.plugins.magic.Spell;
import com.elmakers.mine.bukkit.utilities.borrowed.ConfigurationNode;

public class FireballSpell extends Spell 
{
    int defaultSize = 1;
    
	@Override
	public boolean onCast(ConfigurationNode parameters) 
	{
	     int size = parameters.getInt("size", defaultSize);
	     boolean useFire = parameters.getBoolean("fire", true);
	  
        CraftWorld cw = (CraftWorld)player.getWorld();
        WorldServer world = cw.getHandle();
        CraftPlayer craftPlayer = (CraftPlayer)player;
        EntityLiving playerEntity = craftPlayer.getHandle();
        Location playerLoc = player.getLocation();
        Vector aim = getAimVector();
        int fireballX = (int)(playerLoc.getX() + aim.getX() * 2 + 0.5);
        int fireballY = (int)(playerLoc.getY() + aim.getY() * 2 + 2);
        int fireballZ = (int)(playerLoc.getZ() + aim.getZ() * 2 + 0.5);
        double d0 = aim.getX();
        double d1 = aim.getY();
        double d2 = aim.getZ();

        EntityFireball fireball = new EntityFireball(world, playerEntity, aim.getX(), aim.getY(), aim.getZ());
        fireball.setPositionRotation(fireballX, fireballY, fireballZ, playerLoc.getYaw(), playerLoc.getPitch());
        fireball.yield = size;
        fireball.isIncendiary = useFire;
        
        // De-randomize aim vector
        double d3 = (double) MathHelper.a(d0 * d0 + d1 * d1 + d2 * d2);

        fireball.dirX = d0 / d3 * 0.1D;
        fireball.dirY = d1 / d3 * 0.1D;
        fireball.dirZ = d2 / d3 * 0.1D;
        
        world.addEntity(fireball);
		return true;
	}

    @Override
    public void onLoad(ConfigurationNode node)
    {
        disableTargeting();
    }
}
