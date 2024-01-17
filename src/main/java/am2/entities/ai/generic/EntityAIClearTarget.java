package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIClearTarget extends EntityAITarget
{
	private EntityGeneric generic;
	private EntityLivingBase target;
    public EntityAIClearTarget(EntityGeneric generic){
    	super(generic, false);
    	this.generic = generic;
    }

    @Override
    public boolean shouldExecute(){
    	target = taskOwner.getAttackTarget();
        if (target == null)
            return false;
        
        if(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.disableDamage)
        	return true;

        double aggroRange = Double.valueOf(generic.getValue("aggR"));
        double distance = aggroRange * 2 * aggroRange;
        
        return generic.getDistanceSqToEntity(target) > distance;
    }

    @Override
    public void startExecuting(){
        this.taskOwner.setAttackTarget(null);
        if(target == taskOwner.getAITarget())
        	this.taskOwner.setRevengeTarget(null);
        super.startExecuting();
    }
}
