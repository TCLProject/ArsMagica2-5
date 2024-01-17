package am2.blocks.tileentities;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTickingAir extends TileEntity {

    public boolean noDisappear;
    private int counter;

    public TileEntityTickingAir() {
        counter = 3;
        noDisappear = false;
    }

    @Override
    public void updateEntity(){
        if (!noDisappear) {
            counter--;
            if (counter <= 0) worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
        }
    }
}
