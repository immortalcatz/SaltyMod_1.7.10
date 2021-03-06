package ru.liahim.saltmod.item;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.liahim.saltmod.init.AchievSalt;
import ru.liahim.saltmod.init.SaltConfig;

public class FizzyDrink extends Item {

    public FizzyDrink(String name, CreativeTabs tab, String textureName) {
        this.setMaxStackSize(1);
        this.setUnlocalizedName(name);
        this.setCreativeTab(tab);
        this.setTextureName("saltmod:" + textureName);

    }

    @Override
	public void addInformation(ItemStack is, EntityPlayer player, List list, boolean flag) {
        list.add(I18n.format(getUnlocalizedName() + ".tooltip"));
    }

    @Override
	public ItemStack onEaten(ItemStack item, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode)
        {--item.stackSize;}

        if (!world.isRemote) {
        	if (SaltConfig.fizzyEffect) {player.clearActivePotions();}
			else {player.curePotionEffects(new ItemStack(Items.milk_bucket));}
        	if (player.isBurning()) {player.addStat(AchievSalt.fizzyDrink, 1); player.extinguish();}
        }
        
        return item.stackSize <= 0 ? new ItemStack(Items.glass_bottle) : item;
    }

    @Override
	public int getMaxItemUseDuration(ItemStack item) {
        return 32;
    }

    @Override
	public EnumAction getItemUseAction(ItemStack item) {
        return EnumAction.drink;
    }

    @Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
        player.setItemInUse(item, this.getMaxItemUseDuration(item));
        return item;
    }

}