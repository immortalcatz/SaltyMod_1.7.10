package ru.liahim.saltmod.inventory.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ru.liahim.saltmod.common.CommonProxy;
import ru.liahim.saltmod.inventory.container.ContainerExtractor;
import ru.liahim.saltmod.network.ExtractorButtonMessage;
import ru.liahim.saltmod.tileentity.TileEntityExtractor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExtractor extends GuiContainer {
	private static final ResourceLocation guiTextures = new ResourceLocation("saltmod:textures/gui/container/extractor.png");
	private TileEntityExtractor te;
	private GuiExtractorButton button;

	public GuiExtractor(InventoryPlayer player, TileEntityExtractor tileEntity) {
		super(new ContainerExtractor(player, tileEntity));
		this.te = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(this.button = new GuiExtractorButton(GuiExtractor.guiTextures, 1, this.guiLeft + 97, this.guiTop + 16));
		this.button.enabled = false;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.button.enabled = this.te.liquidLevel > 0;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		CommonProxy.network.sendToServer(new ExtractorButtonMessage(this.te.xCoord, this.te.yCoord, this.te.zCoord));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par_1, int par_2) {
		String s = this.te.hasCustomInventoryName() ? this.te.getInventoryName() : I18n.format(this.te.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2 - 10, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        /*if (this.te.liquidLevel > 0)
		{
			s = String.valueOf(this.te.liquidLevel);
			this.fontRendererObj.drawStringWithShadow(s, this.xSize - 81 - this.fontRendererObj.getStringWidth(s), this.ySize - 124, 0xFFFFFF);
		}*/
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par_1, int par_2, int par_3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (this.te.isBurning()) {
			int i1 = this.te.getBurnTimeRemainingScaled(13);
			this.drawTexturedModalRect(k + 71, l + 54 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
		}

		if (this.te.liquidLevel > 0) {
			int i1 = this.te.getExtractProgressScaled(17);
			this.drawTexturedModalRect(k + 96, l + 36, 176, 14, i1 + 1, 10);
		}

		if (this.te.pressure > 0) {
			this.drawTexturedModalRect(k + 59, l + 32 - this.te.pressure, 176, 40 - this.te.pressure, 1, this.te.pressure);
		}

		if (this.te.getFluidAmountScaledClient(32) > 0) {
			drawTank(k, l, 62, 17, 32, this.te.getFluidAmountScaledClient(32), new FluidStack(this.te.liquidID, this.te.liquidLevel));
			this.mc.getTextureManager().bindTexture(guiTextures);
		}
	}

	protected void drawTank(int w, int h, int wp, int hp, int width, int amount, FluidStack fluidstack) {
		if (fluidstack == null) {
			return;
		}

		IIcon icon = null;
		Fluid fluid = fluidstack.getFluid();
		int color = fluid.getColor();
		if (fluid.getStillIcon() != null) {
			icon = fluid.getStillIcon();
		}
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GL11.glColor4f(r, g, b, 1.0f);
		this.drawTexturedModelRectFromIcon(w + wp, h + hp + 32 - amount, icon, width, amount);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		int w = (this.width - this.xSize) / 2;
		int h = (this.height - this.ySize) / 2;

		if ((mouseX >= w + 62) && (mouseY >= h + 17) && (mouseX < w + 62 + 32) && (mouseY < h + 17 + 32)) {
			ArrayList toolTip = new ArrayList();

			if (this.te.liquidLevel > 0) {
				toolTip.add(new FluidStack(this.te.liquidID, this.te.liquidLevel).getLocalizedName());
			}

			drawText(toolTip, mouseX, mouseY, this.fontRendererObj);
		}

		if ((mouseX >= w + 97) && (mouseY >= h + 16) && (mouseX < w + 97 + 3) && (mouseY < h + 16 + 3)) {
			ArrayList toolTip = new ArrayList();

			if (this.te.liquidLevel > 0) {
				toolTip.add(I18n.format("container.discard"));
			}

			drawText(toolTip, mouseX, mouseY, this.fontRendererObj);
		}
	}

	protected void drawText(List list, int par2, int par3, FontRenderer font) {
		if (!list.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;

			for (Object aList : list) {
				String s = (String) aList;
				int l = font.getStringWidth(s);

				if (l > k) {
					k = l;
				}
			}

			int i1 = par2 + 12;
			int j1 = par3 - 12;
			int k1 = 8;

			if (list.size() > 1) {
				k1 += 2 + (list.size() - 1) * 10;
			}

			if (i1 + k > this.width) {
				i1 -= 28 + k;
			}

			if (j1 + k1 + 6 > this.height) {
				j1 = this.height - k1 - 6;
			}

			this.zLevel = 300.0F;
			itemRender.zLevel = 300.0F;
			int l1 = -267386864;
			this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
			this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
			this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
			int i2 = 1347420415;
			int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
			this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
			this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
			this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

			for (int k2 = 0; k2 < list.size(); ++k2) {
				String s1 = (String) list.get(k2);
				font.drawStringWithShadow(s1, i1, j1, -1);

				if (k2 == 0) {
					j1 += 2;
				}

				j1 += 10;
			}

			this.zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
}