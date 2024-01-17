package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIDodgeShoot extends EntityAIBase
{
    private EntityGeneric entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public EntityAIDodgeShoot(EntityGeneric par1EntityNPCInterface)
    {
        this.entity = par1EntityNPCInterface;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	EntityLivingBase var1 = this.entity.getAttackTarget();

        if (var1 == null || !var1.isEntityAlive())
        {
            return false;
        }
        if (this.entity.invvalues.get("proj") == null)
        {
        	return false;
        }
        else if (this.entity.getRangedTask() == null)
        {
        	return false;
        }
        else
        {

        	Vec3 vec = this.entity.getRangedTask().hasFired() ? RandomPositionGeneratorAlt.findRandomTarget(this.entity, 4, 1) : null;

            if (vec == null)
            {
                return false;
            }
            else
            {
                this.xPosition = vec.xCoord;
                this.yPosition = vec.yCoord;
                this.zPosition = vec.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, 1.2D);
    }
    
    /**
     * Updates the task
     */
    public void updateTask()
    {
    	if (this.entity.getAttackTarget() != null)
    	this.entity.getLookHelper().setLookPositionWithEntity(this.entity.getAttackTarget(), 30.0F, 30.0F);
    }
}
