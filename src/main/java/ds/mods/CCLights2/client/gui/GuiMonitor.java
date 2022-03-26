package ds.mods.CCLights2.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import ds.mods.CCLights2.block.tileentity.TileEntityMonitor;
import ds.mods.CCLights2.client.render.TabletRenderer;
import ds.mods.CCLights2.gpu.Monitor;
import ds.mods.CCLights2.gpu.Texture;
import ds.mods.CCLights2.network.PacketSenders;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;


//DONE: Don't fire events when mouse is outside area, and apply correct offsets.
public class GuiMonitor extends GuiScreen {
	//private static final ResourceLocation corners = new ResourceLocation("cclights", "textures/gui/corners.png");
	public Monitor mon;
	public TileEntityMonitor tile;
	public boolean isMouseDown = false;
	public int mouseButton = 0;
	public int mlx;  // mouselast x
	public int mly;
	public int mx;   // mouse x
	public int my;
	
	public GuiMonitor(TileEntityMonitor mon)
	{
		this.mon = mon.mon;
		tile = mon;
	}
	
	@Override
	public void initGui()
	{
		Texture tex = mon.tex;
		if (tex == null)
			throw new RuntimeException("OpenGL texture setup failed!");
		Keyboard.enableRepeatEvents(true);
	}
	
	public int applyXOffset(int x)
	{
		return x-((width/4)-mon.getWidth()/4)*2;
	}
	
	public int applyYOffset(int y)
	{
		return y-((height/4)-mon.getHeight()/4)*2;
	}
	
	public int unapplyXOffset(int x)
	{
		return x+((width/4)-mon.getWidth()/4)*2;
	}
	
	public int unapplyYOffset(int y)
	{
		return y+((height/4)-mon.getHeight()/4)*2;
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		par1 = applyXOffset(par1);
		par2 = applyYOffset(par2);
		
		int wheel = Mouse.getDWheel();
		if (wheel != 0)
		{
			PacketSenders.GPUEvent(par1,par2,tile,wheel);
		}
		if (isMouseDown)
		{
			if (par1 > -1 & par2 > -1 & par1 < mon.getWidth()+1 & par2 < mon.getHeight()+1)
			{
				mx = par1;
				my = par2;
				if (mlx != mx | mly != my)
				{
					PacketSenders.mouseEventMove(mx,mly,tile);
				}
				mlx = mx;
				mly = my;
			}
			else
			{
				mouseMovedOrUp(unapplyXOffset(par1)/2, unapplyYOffset(par2)/2, mouseButton);
			}
		}
		drawWorldBackground(0);
		Texture tex = mon.tex;
		synchronized (tex)
		{
			try {
				if (tex.renderLock) {tex.wait(1L);}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TextureUtil.uploadTexture(TabletRenderer.dyntex.getGlTextureId(), tex.rgbCache, 16*32, 9*32);
		this.drawTexturedModalRect(unapplyXOffset(0), unapplyYOffset(0), mon.getWidth(), mon.getHeight());
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void drawTexturedModalRect(int x, int y, int w, int h)
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator var2 = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var3 = 256.0F;
		GL11.glPushMatrix();
		GL11.glScaled(1D, 1D, 1D);
		var2.startDrawingQuads();
		//var2.setColorOpaque_I(4210752);
		var2.addVertexWithUV(x, y, this.zLevel, 0.0D, 0D);
		var2.addVertexWithUV(x, (double)h+y, this.zLevel, 0.0D, h/(9*32D));
		var2.addVertexWithUV((double)w+x, (double)h+y, this.zLevel, w/(16*32D), h/(9*32D));
		var2.addVertexWithUV((double)w+x, y, this.zLevel, w/(16*32D), 0D);
		var2.draw();
		GL11.glPopMatrix();
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		par1 = applyXOffset(par1);
		par2 = applyYOffset(par2);
		if (par1 > -1 & par2 > -1 & par1 < mon.getWidth()+1 & par2 < mon.getHeight()+1)
		{
			isMouseDown = true;
			mouseButton = par3;
			mlx = par1;
			mx = par1;
			mly = par2;
			my = par2;
			PacketSenders.mouseEvent(mx,my,par3,tile);
		}
	}
	
	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3)
	{
		par1 = applyXOffset(par1);
		par2 = applyYOffset(par2);
		if (isMouseDown)
		{
			if (par3 == mouseButton)
			{
				isMouseDown = false;
				PacketSenders.mouseEventUp(tile);
			}
		}
	}
	
	@Override
	public void handleKeyboardInput() {
		super.handleKeyboardInput();
		if (!Keyboard.getEventKeyState()) {
			keyRelease(Keyboard.getEventCharacter(), Keyboard.getEventKey()); // TODO: Character is 0?
		}
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		if (par2 != 1)
		{
			PacketSenders.sendKeyEvent(par1, par2, Keyboard.isRepeatEvent(), tile);
		}
	}
	
	protected void keyRelease(char par1, int par2)
	{
		if (par2 != 1)
		{
			PacketSenders.sendKeyEventUp(par1, par2, tile);
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
