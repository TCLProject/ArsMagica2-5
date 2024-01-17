package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;

public class EntityAttackSelector implements IEntitySelector
{
	private EntityGeneric generic;
	
	public EntityAttackSelector(EntityGeneric generic){
		this.generic = generic;
	}
    /**
     * Return whether the specified entity is applicable to this filter.
     */
	@Override
    public boolean isEntityApplicable(Entity entity){
    	if(!entity.isEntityAlive() || entity == generic || generic.getDistanceToEntity(entity) > Double.valueOf(generic.getValue("aggR")) || !(entity instanceof EntityLivingBase) || ((EntityLivingBase)entity).getHealth() < 1)
    		return false;
        if (!this.generic.seesIndirect && !this.generic.getEntitySenses().canSee(entity))
        	return false;
        
        if(!Boolean.valueOf(generic.getValue("invS")) &&((EntityLivingBase)entity).isPotionActive(Potion.invisibility) && generic.getDistanceSqToEntity(entity) < 9)
        	return false;

    	if(entity instanceof EntityPlayerMP){
    		if (!((EntityPlayerMP)entity).capabilities.disableDamage) return generic.isAggressiveToEntity((EntityPlayer) entity);
    		else return false;
    	}
        return generic.isAggressiveToEntity(entity);
    }
}
