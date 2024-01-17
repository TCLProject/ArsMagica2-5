package am2.entities.ai.generic;

import java.util.List;

import am2.entities.EntityGeneric;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

//courtesy of CustomNPCs
public class EntityAIAvoidTarget extends EntityAIBase
{
    /** The entity we are attached to */
    private EntityGeneric theEntity;
    private Entity closestLivingEntity;
    private double distanceFromEntity;
    
    private float health;

    /** The PathEntity of our entity */
    private PathEntity entityPathEntity;

    /** The PathNavigate of our entity */
    private PathNavigate entityPathNavigate;
    
    /** The class of the entity we should avoid */
    private Class targetEntityClass;

    public EntityAIAvoidTarget(EntityGeneric par1EntityNPC)
    {
        this.theEntity = par1EntityNPC;
        this.distanceFromEntity = Double.valueOf(this.theEntity.getValue("aggR"));
        this.health = this.theEntity.getHealth();
        this.entityPathNavigate = par1EntityNPC.getNavigator();
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	EntityLivingBase target = this.theEntity.getAttackTarget();
    	
    	if (target == null)
        {
            return false;
        }
        
    	targetEntityClass = target.getClass();
    	
        if (this.targetEntityClass == EntityPlayer.class)
        {
            this.closestLivingEntity = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, (double)this.distanceFromEntity);

            if (this.closestLivingEntity == null)
            {
                return false;
            }
        }
        else
        {
            List var1 = this.theEntity.worldObj.getEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity));

            if (var1.isEmpty())
            {
                return false;
            }

            this.closestLivingEntity = (Entity)var1.get(0);
        }

        if (!this.theEntity.getEntitySenses().canSee(this.closestLivingEntity) && !this.theEntity.seesIndirect)
        {
            return false;
        }
        else
        {
            Vec3 var2 = RandomPositionGeneratorAlt.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

            boolean var3 = this.theEntity.invvalues.get("proj") == null || this.theEntity.preferMelee;
            
            boolean var4 = var3 ? this.health == this.theEntity.getHealth() : this.theEntity.getRangedTask() != null && !this.theEntity.getRangedTask().hasFired();
            
            if (var2 == null)
            {
                return false;
            }
            else if (this.closestLivingEntity.getDistanceSq(var2.xCoord, var2.yCoord, var2.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity))
            {
                return false;
            }
            else if (this.theEntity.getTacticalVariant() == EntityGeneric.EnumNavType.HitNRun && var4)
            {
            	return false;
            }
            else
            {
                this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(var2.xCoord, var2.yCoord, var2.zCoord);
                return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(var2);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityPathNavigate.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityPathNavigate.setPath(this.entityPathEntity, 1.0D);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.closestLivingEntity = null;
        this.theEntity.setAttackTarget(null);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigator().setSpeed(1.2D);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(1.0D);
        }
        if (this.theEntity.getTacticalVariant() == EntityGeneric.EnumNavType.HitNRun)
        {
        	float dist = this.theEntity.getDistanceToEntity(this.closestLivingEntity);
        	if (dist > this.distanceFromEntity || dist < this.theEntity.getIntegerValue("taRa"))
        	{
        		this.health = this.theEntity.getHealth();
        	}
        }
    }
}