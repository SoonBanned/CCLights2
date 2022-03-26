package ds.mods.CCLights2.block.tileentity;

import java.awt.Color;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import ds.mods.CCLights2.gpu.GPU;
import ds.mods.CCLights2.gpu.Monitor;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMonitor extends TileEntity {
	public Monitor mon;
	
	public TileEntityMonitor()
	{
		mon = new Monitor(256,144,getMonitorObject());
		mon.tex.fill(Color.black);
	}
	
	public void connect(GPU g)
	{
		mon.addGPU(g);
	}

	
	public ILuaObject getMonitorObject()
	{
		return new MonitorObject();
	}
	
	public class MonitorObject implements ILuaObject
	{

		@Override
		public String[] getMethodNames() {
			return new String[]{"getResolution"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method,
				Object[] arguments) {
			switch (method)
			{
			case 0:
			{
				return new Object[]{mon.getWidth(),mon.getHeight()};
			}
			}
			return null;
		}
		
	}
}
