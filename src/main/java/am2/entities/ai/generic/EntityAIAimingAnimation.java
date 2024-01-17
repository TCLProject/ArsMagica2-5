package am2.entities.ai.generic;

import am2.entities.EntityGeneric;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAimingAnimation extends EntityAIBase
{
    private EntityGeneric npc;
    boolean aimWhileShooting;

    public EntityAIAimingAnimation(EntityGeneric npc){
        this.npc = npc;
        aimWhileShooting = Boolean.parseBoolean(npc.getValue("aimA"));
    }

    @Override
    public boolean shouldExecute(){
    	if(aimWhileShooting && npc.attackingRanged())
    		return !npc.isAiming();
    	else return npc.isAiming();
    }

    @Override
    public void updateTask(){
    	if(aimWhileShooting && npc.attackingRanged()){
			setAiming(true);
    		return;
    	}
    	setAiming(false);
    }
    
    private void setAiming(boolean aiming){
    	npc.setAiming(aiming);
    	npc.updateHitbox();
    	npc.setPosition(npc.posX, npc.posY, npc.posZ);
    }
}
