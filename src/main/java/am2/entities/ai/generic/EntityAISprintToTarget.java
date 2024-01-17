package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISprintToTarget extends EntityAIBase
{
    EntityGeneric runner;
    EntityLivingBase runTarget;

    public EntityAISprintToTarget(EntityGeneric par1EntityLiving)
    {
        this.runner = par1EntityLiving;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.runTarget = this.runner.getAttackTarget();

        if (this.runTarget == null)
        {
            return false;
        }
        else if (this.runner.getNavigator().noPath())
        {
        	return false;
        }
        else
        {
        	switch(Integer.parseInt(this.runner.getValue("aRec")))
        	{       	
        		case 0 : return this.runner.getDistanceSqToEntity(this.runTarget) >= 64.0D ? (!this.runner.onGround ? false : true) : false;
        		case 2 : return this.runner.getDistanceSqToEntity(this.runTarget) <= 49.0D ? (!this.runner.onGround ? false : true) : false;
        		default : return false;
        	}
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.runner.isEntityAlive() && this.runner.onGround && this.runner.hurtTime <= 0 && (this.runner.motionX != 0.0D && this.runner.motionZ != 0.0D);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	this.runner.setSprinting(true);
    }
    
    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.runner.setSprinting(false);
    }
}
