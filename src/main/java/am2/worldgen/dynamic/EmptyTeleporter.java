package am2.worldgen.dynamic;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class EmptyTeleporter extends Teleporter {
    public EmptyTeleporter(WorldServer par1WorldServer) {
        super(par1WorldServer);
    }

    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
    }
}
