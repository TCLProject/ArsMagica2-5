package am2.entities.ai.generic;

import java.util.Iterator;
import java.util.List;

import am2.entities.EntityGeneric;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase
{
    private EntityGeneric entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    int walkingRange = 15;

    public EntityAIWander(EntityGeneric npc){
        this.entity = npc;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute(){
        if (this.entity.getAge() >= 100 || !entity.getNavigator().noPath() || this.entity.getRNG().nextInt(80) != 0){
            return false;
        }
        Vec3 vec = RandomPositionGenerator.findRandomTarget(this.entity, walkingRange, 7);
        if (vec == null){
            return false;
        } else{
            this.xPosition = vec.xCoord;
            this.yPosition = vec.yCoord;
            // this may not work, needs testing: was initially 'starting position' in place of 'vec.yCoord'
            if(entity.canFly()) this.yPosition = vec.yCoord + (entity.getRNG().nextFloat() * 0.75 * walkingRange);
            this.zPosition = vec.zCoord;
        }
        return true;
    }

    @Override
    public boolean continueExecuting(){
        return !this.entity.getNavigator().noPath() && this.entity.isEntityAlive();
    }

    @Override
    public void startExecuting(){
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, 1);
    }
}
