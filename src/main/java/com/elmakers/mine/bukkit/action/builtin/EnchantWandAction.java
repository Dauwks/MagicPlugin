package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.spell.Spell;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.api.wand.Wand;
import com.elmakers.mine.bukkit.magic.MagicPlugin;
import com.elmakers.mine.bukkit.spell.BaseSpell;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class EnchantWandAction extends BaseSpellAction
{
    private int levels;
    private boolean useXp;
    private ItemStack requireItem = null;

    public void prepare(CastContext context, ConfigurationSection parameters) {
        super.prepare(context, parameters);
        levels = parameters.getInt("levels", 30);
        useXp = parameters.getBoolean("use_xp", false);
        String costKey = parameters.getString("requires");
        if (costKey != null && !costKey.isEmpty())
        {
            MageController controller = context.getController();
            requireItem = controller.createItem(costKey);
            if (requireItem == null) {
                context.getLogger().warning("Invalid required item: " + costKey);
            }
        }
    }

    @Override
    public SpellResult perform(CastContext context) {
        Mage mage = context.getMage();
        Wand wand = mage.getActiveWand();
		Player player = mage.getPlayer();
		if (player == null) {
            return SpellResult.PLAYER_REQUIRED;
        }
        if (levels <= 0) {
            return SpellResult.FAIL;
        }
        if (wand == null) {
            context.sendMessage("no_wand");
            return SpellResult.FAIL;
        }
        if (requireItem != null) {
            MageController controller = context.getController();
            boolean foundItem = false;
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (controller.itemsAreEqual(item, requireItem)) {
                    player.getInventory().setItem(i, null);
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                context.sendMessage("insufficient_resources");
                return SpellResult.INSUFFICIENT_RESOURCES;
            }
        }

        int xpLevels = levels;
        if (useXp) {
            xpLevels = mage.getLevel();
        }
        int levelsUsed = wand.enchant(xpLevels, mage);
        if (levelsUsed == 0) {
            return SpellResult.FAIL;
        }
        if (useXp) {
            mage.setLevel(Math.max(0, mage.getLevel() - levelsUsed));
        }
        return SpellResult.CAST;
	}

    @Override
    public void getParameterNames(Spell spell, Collection<String> parameters)
    {
        super.getParameterNames(spell, parameters);
        parameters.add("levels");
        parameters.add("use_xp");
    }

    @Override
    public void getParameterOptions(Spell spell, String parameterKey, Collection<String> examples)
    {
        if (parameterKey.equals("levels")) {
            examples.addAll(Arrays.asList(BaseSpell.EXAMPLE_SIZES));
        } else if (parameterKey.equals("use_xp")) {
            examples.addAll(Arrays.asList(BaseSpell.EXAMPLE_BOOLEANS));
        } else {
            super.getParameterOptions(spell, parameterKey, examples);
        }
    }

    @Override
    public String transformMessage(String message) {
        MagicAPI api = MagicPlugin.getAPI();
        if (this.requireItem != null) {
            message = message.replace("$requires", api.describeItem(requireItem));
        }
        return message;
    }
}
