package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

// Courtesy of CustomNPCs+
public class EntityAIAttackTarget extends EntityAIBase
{
    World worldObj;
    EntityGeneric attacker;
    EntityLivingBase entityTarget;

    /**
     * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
     */
    int attackTick;

    /** The PathEntity of our entity. */
    PathEntity entityPathEntity;
    private int field_75445_i;
	private boolean navOverride = false;
    
    public EntityAIAttackTarget(EntityGeneric par1EntityLiving){
        this.attackTick = 0;
        this.attacker = par1EntityLiving;
        this.worldObj = par1EntityLiving.worldObj;
        this.setMutexBits(this.navOverride ? 4 : 3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute(){
    	EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null){
            return false;
        }
        else if (!entitylivingbase.isEntityAlive()){
            return false;
        }
        int mindistance = Integer.parseInt(this.attacker.getValue("miDi"));
        if (this.attacker.invvalues.get("proj") != null && mindistance == 0){
     	   return false;
        }
        
        double var2 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
        double var3 = mindistance * mindistance;
        
        if (this.attacker.getValue("ranged") != null && var2 > var3){
        	return false;
        }
        else{
        	this.entityTarget = entitylivingbase;
            this.attacker.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Double.valueOf(this.attacker.getValue("aggR")));
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            this.attacker.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Double.valueOf(this.attacker.getValue("navR")));
            return this.entityPathEntity != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	this.entityTarget = this.attacker.getAttackTarget();
    	
		if(entityTarget == null || !entityTarget.isEntityAlive())
			return false;
		if(attacker.getDistanceToEntity(entityTarget) > Double.valueOf(this.attacker.getValue("aggR")))
			return false;
        int mindistance = Integer.parseInt(this.attacker.getValue("miDi"));
		if (this.attacker.getValue("ranged") != null && attacker.getDistanceSqToEntity(entityTarget) > (mindistance * mindistance))
			return false;
		
		return this.attacker.isWithinHomeDistance(MathHelper.floor_double(entityTarget.posX), MathHelper.floor_double(entityTarget.posY), MathHelper.floor_double(entityTarget.posZ));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting(){
    	if(!navOverride)
    		this.attacker.getNavigator().setPath(this.entityPathEntity, 1.3D);
        this.field_75445_i = 0;
        if (this.attacker.getRangedTask() != null && this.attacker.preferMelee)
        {
        	this.attacker.getRangedTask().navOverride(true);
        }
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
    	this.entityPathEntity = null;
    	this.entityTarget = null;
    	this.attacker.setAttackTarget(null);
        this.attacker.getNavigator().clearPathEntity();
        if (this.attacker.getRangedTask() != null && this.attacker.preferMelee)
        {
        	this.attacker.getRangedTask().navOverride(false);
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.attacker.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);
        if (!navOverride && --this.field_75445_i <= 0)
        {
            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
            this.attacker.getNavigator().tryMoveToEntityLiving(this.entityTarget, 1.3f);
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double distance = this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ);
        double range = Double.parseDouble(attacker.getValue("MattR")) * Double.parseDouble(attacker.getValue("MattR")) + entityTarget.width;
        double minRange = this.attacker.width * 2.0F * this.attacker.width * 2.0F + entityTarget.width;
        if(minRange > range)
        	range = minRange;
        if (distance <= range && (attacker.canSee(this.entityTarget) || distance < minRange))
        {
            if (this.attackTick <= 0)
            {
                this.attackTick = Integer.parseInt(attacker.getValue("MattM")) + this.attacker.worldObj.rand.nextInt(Integer.parseInt(attacker.getValue("MattMX")));
                this.attacker.swingItem();
                this.attacker.attackEntityAsMob(this.entityTarget);
            }
        }
    }
    
    public void navOverride(boolean nav){
    	this.navOverride = nav;
        this.setMutexBits(this.navOverride ? 4 : 3);
    }
}
