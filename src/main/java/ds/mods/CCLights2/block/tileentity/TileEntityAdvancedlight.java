package ds.mods.CCLights2.block.tileentity;

import java.io.IOException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import ds.mods.CCLights2.CCLights2;
import ds.mods.CCLights2.network.PacketChunker;
import ds.mods.CCLights2.network.PacketHandler.PacketMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAdvancedlight  extends TileEntity implements IPeripheral {
	    public float r = 255;
	    public float g = 255;
	    public float b = 255;
		private int ticksSinceSync;

	    @Override
		public synchronized void detach(IComputerAccess icomputeraccess) // tried sync and without
	    {
	    }
	    
		@Override
		public void attach(IComputerAccess computer) {}

	    @Override
		public String getType()
	    {
	        return "LightAdv";
	    }

	    @Override
		public String[] getMethodNames()
	    {
	        return (new String[] { "setColorRGB", "getColorRGB"});
	    }
        @Override
        public Object[] callMethod(IComputerAccess computer,ILuaContext context, int method, Object[] arguments) throws LuaException {
	    	if (method == 0)
	        {
	    		int r = ((Double) arguments[0]).intValue();
	    		int g = ((Double) arguments[1]).intValue();
	    		int b = ((Double) arguments[2]).intValue();
	    		if (r > 255 || r < 0 || g > 255 || g < 0 || b > 255 || b < 0)
	            {
	            	throw new LuaException("Invalid RGB!");
	            }
	        	this.r = r;
	            this.g = g;
	            this.b = b;
	            colorChange();
	            return null;
	        }
	        else if (method == 1)
	        {
	        	return (new Object[]{this.r,this.g,this.b});
	        }

	        return null;
	    }
        
	    public void colorChange()
	    {
	    	ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
	    	try {
	    		outputStream.writeInt(xCoord);
	    		outputStream.writeInt(yCoord);
	    		outputStream.writeInt(zCoord);
	    		outputStream.writeFloat(this.r);
	    		outputStream.writeFloat(this.g);
	    		outputStream.writeFloat(this.b);
	    		PacketMessage[] packets = PacketChunker.instance.createPackets("CCLights2", outputStream.toByteArray());
	    		CCLights2.network.sendToAllAround(packets[0], new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 4096.0D));
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }
	    @Override
	    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	    	super.readFromNBT(par1NBTTagCompound);
	        this.r = par1NBTTagCompound.getFloat("r");
	        this.g = par1NBTTagCompound.getFloat("g");
	        this.b = par1NBTTagCompound.getFloat("b");
	        colorChange();
	    }
	    @Override
	    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	    	super.writeToNBT(par1NBTTagCompound);
	    	par1NBTTagCompound.setFloat("r",this.r);
	    	par1NBTTagCompound.setFloat("g",this.g);
	    	par1NBTTagCompound.setFloat("b",this.b);
	    }
	    @Override
	    public void updateEntity() {
	    	if ((++ticksSinceSync % 20) == 0) {
	    		colorChange();
	    	}
	    }

		@Override
		public boolean equals(IPeripheral other) {
			if(other.getType() == getType()){return true;}
			else return false;
		}
}
