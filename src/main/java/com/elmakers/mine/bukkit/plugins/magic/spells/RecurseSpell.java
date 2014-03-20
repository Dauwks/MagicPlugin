package com.elmakers.mine.bukkit.plugins.magic.spells;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.elmakers.mine.bukkit.blocks.BlockRecurse;
import com.elmakers.mine.bukkit.blocks.MaterialBrush;
import com.elmakers.mine.bukkit.blocks.ReplaceMaterialAction;
import com.elmakers.mine.bukkit.plugins.magic.BrushSpell;
import com.elmakers.mine.bukkit.plugins.magic.SpellResult;
import com.elmakers.mine.bukkit.utilities.borrowed.ConfigurationNode;

public class RecurseSpell extends BrushSpell 
{
	private final BlockRecurse blockRecurse = new BlockRecurse();

	@Override
	public SpellResult onCast(ConfigurationNode parameters) 
	{
		Block targetBlock = getTargetBlock();
		
		if (targetBlock == null) 
		{
			return SpellResult.NO_TARGET;
		}
		if (!hasBuildPermission(targetBlock)) 
		{
			return SpellResult.INSUFFICIENT_PERMISSION;
		}
		
		MaterialBrush buildWith = getMaterialBrush();

		int size = parameters.getInt("size", 8);
		size = (int)(mage.getRadiusMultiplier() * size);
		blockRecurse.setMaxRecursion(size);

		Material targetMaterial = targetBlock.getType();
		ReplaceMaterialAction action = new ReplaceMaterialAction(mage, targetBlock, buildWith);

		// A bit hacky, but is very handy!
		if (targetMaterial == Material.STATIONARY_WATER)
		{
			action.addReplaceable(Material.WATER);
		}
		else if (targetMaterial == Material.WATER)
		{
			action.addReplaceable(Material.STATIONARY_WATER);
		}
		else if (targetMaterial == Material.STATIONARY_LAVA)
		{
			action.addReplaceable(Material.LAVA);
		}
		else if (targetMaterial == Material.LAVA)
		{
			action.addReplaceable(Material.STATIONARY_LAVA);
		}
		blockRecurse.recurse(targetBlock, action);
		mage.registerForUndo(action.getBlocks());
		controller.updateBlock(targetBlock);
		
		return SpellResult.CAST;
	}
}
