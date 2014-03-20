package com.elmakers.mine.bukkit.plugins.magic.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.elmakers.mine.bukkit.blocks.BlockList;
import com.elmakers.mine.bukkit.plugins.magic.BlockSpell;
import com.elmakers.mine.bukkit.plugins.magic.SpellResult;
import com.elmakers.mine.bukkit.utilities.borrowed.ConfigurationNode;

public class TowerSpell extends BlockSpell {

	private int blocksCreated;
	
	@SuppressWarnings("deprecation")
	@Override
	public SpellResult onCast(ConfigurationNode parameters) 
	{
		blocksCreated = 0;
		Block target = getTargetBlock();
		if (target == null) 
		{
			return SpellResult.NO_TARGET;
		}
		if (!hasBuildPermission(target)) {
			return SpellResult.INSUFFICIENT_PERMISSION;
		}
		int MAX_HEIGHT = 255;
		int height = 16;
		int maxHeight = 255;
		int material = 20;
		int midX = target.getX();
		int midY = target.getY();
		int midZ = target.getZ();

		// Check for roof
		for (int i = height; i < maxHeight; i++)
		{
			int y = midY + i;
			if (y > MAX_HEIGHT)
			{
				maxHeight = MAX_HEIGHT - midY;
				height = height > maxHeight ? maxHeight : height;
				break;
			}
			Block block = getBlockAt(midX, y, midZ);
			if (block.getType() != Material.AIR)
			{
				height = i;
				break;
			}
		}

		BlockList towerBlocks = new BlockList();
		for (int i = 0; i < height; i++)
		{
			midY++;
			for (int dx = -1; dx <= 1; dx++)
			{
				for (int dz = -1; dz <= 1; dz++)
				{
					int x = midX + dx;
					int y = midY;
					int z = midZ + dz;
					// Leave the middle empty
					if (dx != 0 || dz != 0)
					{
						Block block = getBlockAt(x, y, z);
						if (isDestructible(block) && hasBuildPermission(block)) {
							blocksCreated++;
							towerBlocks.add(block);
							block.setTypeId(material);
						}
					}					
				}
			}
		}
		registerForUndo(towerBlocks);
		return SpellResult.CAST;
	}
	
	@Override
	public String getMessage(String messageKey, String def) {
		String message = super.getMessage(messageKey, def);
		return message.replace("$count", Integer.toString(blocksCreated));
	}
}
