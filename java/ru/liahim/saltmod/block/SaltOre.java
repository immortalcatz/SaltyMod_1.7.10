package ru.liahim.saltmod.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.liahim.saltmod.init.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SaltOre extends Block {

	@SideOnly(Side.CLIENT)
	private IIcon SIDE;

	public SaltOre(String name, CreativeTabs tab) {
		super(Material.rock);
		this.setBlockName(name);
		this.setCreativeTab(tab);
		this.setHardness(2F);
		this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 1 && meta > 0 ? Blocks.stone.getBlockTextureFromSide(side) : (side == 2 && meta % 2 == 1) ||
			(side == 5 && meta % 4 >= 2) || (side == 3 && meta % 8 >= 4) || (side == 4 && meta >= 8) ? this.SIDE : this.blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1) {
		this.blockIcon = par1.registerIcon("saltmod:SaltOre");
		this.SIDE = par1.registerIcon("saltmod:SaltOre_Side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (player.capabilities.isCreativeMode && side > 1) {
			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ModItems.salt) {
				int i = world.getBlockMetadata(x, y, z);
				if (side == 2) {if (i % 2 < 1) i += 1; else i -= 1;}
				if (side == 5) {if (i % 4 < 2) i += 2; else i -= 2;}
				if (side == 3) {if (i % 8 < 4) i += 4; else i -= 4;}
				if (side == 4) {if (i < 8) i += 8; else i -= 8;}
				world.setBlock(x, y, z, this, i, 3);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Item getItemDropped(int par1, Random random, int par2) {
		return ModItems.salt;
	}

	@Override
	public int quantityDropped(Random random) {

		return 1 + random.nextInt(3);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		if (fortune > 0) {
			int j = random.nextInt(fortune + 1);
			if (j > 2) {
				return 2;
			}
			return quantityDropped(random) + j;
		} else {
			return quantityDropped(random);
		}
	}

	@Override
	public int getExpDrop(IBlockAccess par1, int par2, int par3) {
		return 1;
	}
}

