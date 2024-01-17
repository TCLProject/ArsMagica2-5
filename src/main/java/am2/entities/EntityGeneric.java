package am2.entities;

import am2.AMEventHandler;
import am2.damage.*;
import am2.entities.ai.generic.*;
import am2.entities.ai.generic.EntityAIPanic;
import am2.entities.ai.generic.EntityAIWander;
import am2.entities.ai.generic.fly.FlyingMoveHelper;
import am2.entities.ai.generic.fly.PathNavigateFlying;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.core.MTargetClassTransformer;

import java.io.IOException;
import java.util.*;

import static am2.AMEventHandler.playerHasMagitech;

/**
 * Henceforth this will be used for all (or, most) generic (non-boss, non-special) entities.
 * Why? Ease of creation, ease of configuration, conserving IDs, automatic syncing where needed.
 * I essentially made this is as an in-code customNPCs-lite type framework to ease the workload on creating entities.
 * */
public class EntityGeneric extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IBossDisplayData {

    public static enum EnumVisibility
    {
        VISIBLE,
        INVISIBLE,
        PARTIALLY_VISIBLE,
    }

    public static enum EnumAttackReaction
    {
        PANIC,
        RUN,
        FIGHT,
        NONE,
    }

    public enum EnumNavType {
        Default("rush"),
        Dodge("stagger"),
        Surround("orbit"),
        HitNRun("hitandrun"),
        Ambush("ambush"),
        Stalk("stalk"),
        None("none");

        String name;
        EnumNavType(String name){
            this.name = name;
        }
        public static String[] names(){
            ArrayList<String> list = new ArrayList<String>();
            for(EnumNavType e : values())
                list.add(e.name);

            return list.toArray(new String[list.size()]);
        }
    }

    public static class BiomeEntitySpawnEntry {
        public GenericEntityBuilder builder;
        public BiomeDictionary.Type[] biomeTypes;
        public float[] biomeWeights;
        public int[] dimensions;
        public boolean spawnOnSurface;
        public boolean spawnInDark;
        public int minYSpawn;
        public int maxYSpawn;

        public BiomeEntitySpawnEntry(GenericEntityBuilder geb, BiomeDictionary.Type[] types, float[] weights, int[] dimensions) {
            this(geb, types, weights, true, false, -1, -1, dimensions);
        }

        public BiomeEntitySpawnEntry(GenericEntityBuilder geb, BiomeDictionary.Type[] types, float[] weights, boolean darkOnly, boolean surfaceOnly, int minY, int maxY, int[] allowedDimensions) {
            builder = geb;
            biomeTypes = types;
            biomeWeights = weights;
            spawnInDark = darkOnly;
            spawnOnSurface = surfaceOnly;
            minYSpawn = minY;
            maxYSpawn = maxY;
            dimensions = allowedDimensions;
        }
    }

    // for convenience (ease of configuration) - no need to remember the exact strings.
    // make sure to call build() once done calling setters - all non-assigned required values will automatically be set to their defaults.
    // IMPORTANT usage detail - have some kind of array of GenericEntityBuilders with all the presets you will need built *before* the player joins the world.
    public class GenericEntityBuilder {

        private HashMap<String, String> values = new HashMap<String, String>();
        private HashMap<String, ItemStack> inventoryValues = new HashMap<String, ItemStack>();
        public boolean built = false;
        private BiomeEntitySpawnEntry spawnEntry = null;

        //d
        /** WARNING: Use with caution */
        public void setManualValue(String key, String value) {values.put(key, value);}

        //d
        public void setMaxHealth(double maxHealth) {
            values.put("maxH", String.valueOf(maxHealth));
        }

        //d
        public void setNavigationRange(double followRange) {
            values.put("navR", String.valueOf(followRange));
        }

        //d
        public void setAggroRange(double range) {
            values.put("aggR", String.valueOf(range));
        }

        //d
        public void setCreatureType(EnumCreatureType ect) {
            values.put("crtT", String.valueOf(ect.ordinal()));
        }

        //d
        public void setCreatureUndeadAnthropod(EnumCreatureAttribute ect) {
            values.put("crtA", String.valueOf(ect.ordinal()));
        }

        //d
        public void setMovementSpeed(double movementSpeed) {
            values.put("movS", String.valueOf(movementSpeed));
        }

        //d
        public void setSightSettings(boolean canSeeInvisible, boolean canSeeIndirect) {
            values.put("invS", String.valueOf(canSeeInvisible));
            values.put("indS", String.valueOf(canSeeIndirect));
        }

        public void setAttackStatsMelee(double attackDamage, double attackRange,
                                         double knockback, int minDelay, int max_delayRandomAddition) {
            values.put("MattD", String.valueOf(attackDamage));
            values.put("MattR", String.valueOf(attackRange));
            values.put("MattK", String.valueOf(knockback));
            values.put("MattM", String.valueOf(minDelay));
            values.put("MattMX", String.valueOf(max_delayRandomAddition));
            values.put("melee", "true");
        }

        //d
        public void setMeleePotionEffect(EntityProjectile.EnumPotionType type, int duration, int amplifier) {
            values.put("potT", String.valueOf(type.ordinal()));
            values.put("potD", String.valueOf(duration));
            values.put("potA", String.valueOf(amplifier));
        }

        //d
        public void setAttackStatsRanged(double attackDamage, int knockback,
                                         double fireRate, double burstCount,
                                         int shotCount, double accuracy,
                                         String fireSound, double minDelay,
                                         double maxDelay, ItemStack projectile,
                                         boolean preferMeleeWhenPossible, boolean aimAnimation) {
            values.put("RattD", String.valueOf(attackDamage));
            values.put("RattK", String.valueOf(knockback));
            values.put("RattF", String.valueOf(fireRate));
            values.put("RattB", String.valueOf(burstCount));
            values.put("RattSc", String.valueOf(shotCount));
            values.put("RattA", String.valueOf(accuracy));
            values.put("RattM", String.valueOf(minDelay));
            values.put("RattX", String.valueOf(maxDelay));
            values.put("PrefM", String.valueOf(preferMeleeWhenPossible));
            values.put("aimA", String.valueOf(aimAnimation));
            values.put("RattSo", fireSound);
            inventoryValues.put("proj", projectile);
            values.put("ranged", "true");
        }

        //d
        public void setRangedProjectileProperties(boolean accelerate, boolean explode, int explodeRadius,
                                                  EntityProjectile.EnumPotionType effect, int duration, int amplify,
                                                  EntityProjectile.EnumParticleType trail, int size, boolean glows,
                                                  int speed, boolean gravity, boolean is3D, boolean spin, boolean stickToWalls) {
            values.put("projAccel", String.valueOf(accelerate));
            values.put("projExpl", String.valueOf(explode));
            values.put("projExplRad", String.valueOf(explodeRadius));
            values.put("projEff", String.valueOf(effect.ordinal()));
            values.put("projDur", String.valueOf(duration));
            values.put("projAmp", String.valueOf(amplify));
            values.put("projTrail", String.valueOf(trail.ordinal()));
            values.put("projSize", String.valueOf(size));
            values.put("projGlows", String.valueOf(glows));
            values.put("projSpeed", String.valueOf(speed));
            values.put("projGrav", String.valueOf(gravity));
            values.put("proj3D", String.valueOf(is3D));
            values.put("projSpin", String.valueOf(spin));
            values.put("projStick", String.valueOf(stickToWalls));
        }

        //d
        // makes passing negative resistances possible
        private float fnv(float value) {
           return value >= 0 ? value : 1 - value; // 1 - -0.5 = 1.5
        }

        //d
        // the float values (except for the regens) are multipliers: the damage/knockback is *'d, e.g. 0 = 100% resistance, 0.5 = 50% resistance, 2 = 100% weakness
        public void setResistancesAndRegens(boolean invincible,
                                            boolean sun, boolean fire, boolean potion,
                                            boolean drown, boolean fall, float knockback,
                                            float ranged, float melee, float explosion,
                                            float regen, float combatRegen) {
            values.put("resP", invincible + "," + sun + ","  + fire + "," + potion + ","
                    + drown + "," + fall + ","  + fnv(knockback) + "," + fnv(ranged) + ","
                    + fnv(melee) + "," + fnv(explosion) + ","  + regen + "," + combatRegen + ",");
        }

        //d
        public void setMagicResistances(float fire, float frost, float wind,
                                        float magic, float lightning, float wither,
                                        float thorns, float cactus, float holy, float special) {
            values.put("resM", fnv(fire) + "," + fnv(frost) + "," + fnv(wind) + "," + fnv(magic)
                    + "," + fnv(lightning) + "," + fnv(wither) + "," + fnv(thorns) + ","
                    + fnv(cactus) + "," + fnv(holy) + "," + fnv(special));
        }

        //d
        public void setModelSize(double size) {
            values.put("modS", String.valueOf(size));
        }

        //d
        // name is the full class name of the model (subclass of ModelBase), copyRenderPassesFrom is the full class name of the entity (subclass of EntityLivingBase)
        public void setModelName(String name, boolean isHuman, boolean isAlex, boolean copyRenderPasses, String copyRenderPassesFrom) {
            values.put("mHum", String.valueOf(isHuman));
            values.put("mAl", String.valueOf(isAlex));
            values.put("copRP", String.valueOf(copyRenderPasses));
            values.put("copRPF", copyRenderPassesFrom);
            values.put("mLoc", name);
        }

        //d
        public void setDisplayName(String name) {
            values.put("name", name);
        }

        //d
        public void setDisplayNameColor(int color) { values.put("namC", String.valueOf(color)); }

        //d
        public void setTextureLocation(String loc) {
            values.put("rLoc", loc);
        }

        //d
        public void setCapeTextureLocation(String loc) {
            values.put("cLoc", loc);
        }

        //d
        public void setGlowTextureLocation(String loc) {
            values.put("gLoc", loc);
        }

        //d
        public void setEntityVisibility(EnumVisibility vis) {
            values.put("visE", String.valueOf(getVisibilityIntFromEnum(vis)));
        }

        //d
        public void setDisplayTagVisibility(EnumVisibility vis) {
            values.put("visD", String.valueOf(getVisibilityIntFromEnum(vis)));
        }

        //d
        public void setBossBarVisibility(EnumVisibility vis) {
            values.put("visB", String.valueOf(getVisibilityIntFromEnum(vis)));
        }

        //d
        public void setSounds(String idle, String hurt, String death, String step, float volume, float pitch) {
            values.put("sndI", idle);
            values.put("sndH", hurt);
            values.put("sndD", death);
            values.put("sndS", step);
            values.put("sndV", String.valueOf(volume));
            values.put("sndP", String.valueOf(pitch));
        }

        // PLEASE NOTE the following highly customizable spawning system only works in the presence of spawn events (some mobs already spawning).
        // Add some basic pigs and spiders to a desolate dimension and you're covered. If needed, you can then cancel their respective spawn events
        // after we process them in AMEventHandler, leaving you with only the custom mobs spawning.

        //d
        /** Should be called *last* before build. Note: Do not call both setSpawnSettings methods, choose one. */
        public void setSpawnSettings(BiomeDictionary.Type[] biomesSpawnsIn, float[] biomesSpawnsInWeights, boolean darkOnly, boolean surfaceOnly, int minY, int maxY, int[] dimensions) {
            if (biomesSpawnsIn.length > biomesSpawnsInWeights.length) throw new RuntimeException("Weights not provided for all biomes entity should spawn in! This is an error!");
            BiomeEntitySpawnEntry spawnEntry = new BiomeEntitySpawnEntry(this, biomesSpawnsIn, biomesSpawnsInWeights, darkOnly, surfaceOnly, minY, maxY, dimensions);
            AMEventHandler.spawnEntries.add(spawnEntry);
            this.spawnEntry = spawnEntry;
        }

        //d
        /** Should be called *last* before build. Note: Do not call both setSpawnSettings methods, choose one. */
        public void setSpawnSettings(BiomeDictionary.Type[] biomesSpawnsIn, float[] biomesSpawnsInWeights, int[] dimensions) {
            if (biomesSpawnsIn.length > biomesSpawnsInWeights.length) throw new RuntimeException("Weights not provided for all biomes entity should spawn in! This is an error!");
            BiomeEntitySpawnEntry spawnEntry = new BiomeEntitySpawnEntry(this, biomesSpawnsIn, biomesSpawnsInWeights, dimensions);
            AMEventHandler.spawnEntries.add(spawnEntry);
            this.spawnEntry = spawnEntry;
        }

        //d
        // entity classes passed in, e.g. EntityCreature.class (supports superclasses), EntityZombie.class
        public void setAggressionValues(Class[] attackedBy, Class[] aggressiveTo) {
            String[] attackedByStr = new String[attackedBy.length];
            String[] aggressiveToStr = new String[aggressiveTo.length];
            for (int i = 0; i < attackedBy.length; i++) attackedByStr[i] = attackedBy[i].getSimpleName();
            for (int i = 0; i < aggressiveTo.length; i++) aggressiveToStr[i] = aggressiveTo[i].getSimpleName();
            values.put("attBy", String.join(",", attackedByStr));
            values.put("agrTo", String.join(",", aggressiveToStr));
        }

        //d
        /** armor has to be in order which is frustrating to remember manually*/
        public void setInventoryData(ItemStack boots, ItemStack legs, ItemStack chest, ItemStack head, ItemStack hand, ItemStack handWhenRanged, ItemStack offhand, ItemStack offhandWhenRanged, boolean picksUp) {
            inventoryValues.put("boot", boots);
            inventoryValues.put("legs", legs);
            inventoryValues.put("chest", chest);
            inventoryValues.put("head", head);
            inventoryValues.put("hand", hand);
            inventoryValues.put("handR", handWhenRanged);
            inventoryValues.put("handO", offhand);
            inventoryValues.put("handOR", offhandWhenRanged);
            values.put("pckU", String.valueOf(picksUp));
        }

        //d
        public void setAIData(EnumAttackReaction attackReaction, boolean opensDoors,
                              boolean looksForShelter, boolean canSwim, boolean avoidsLiquids, boolean bypassWebs,
                              boolean avoidsSky, boolean sprints, boolean bigJumps, EnumNavType tactics,
                              boolean canFly, boolean movingAround, boolean lookingAround, int maxDistanceForRanged,
                              int minDistanceForRanged, int tacticalRange, int flySpeedXZ, int flySpeedY,
                              boolean hurtAffectsFly, int flyYLimitBlocksAboveGround, boolean canDespawn) {
            values.put("oDor", String.valueOf(opensDoors)); //d
            values.put("lShe", String.valueOf(looksForShelter)); //d
            values.put("caSw", String.valueOf(canSwim)); //d
            values.put("avLi", String.valueOf(avoidsLiquids)); //d
            values.put("byWe", String.valueOf(bypassWebs)); //d
            values.put("avSk", String.valueOf(avoidsSky)); //d

            values.put("spRi", String.valueOf(sprints)); //d
            values.put("biJu", String.valueOf(bigJumps)); //d
            values.put("moAr", String.valueOf(movingAround)); //d
            values.put("loAr", String.valueOf(lookingAround)); //d

            values.put("caFl", String.valueOf(canFly)); //d
            values.put("flXZ", String.valueOf(flySpeedXZ)); //d
            values.put("flY", String.valueOf(flySpeedY)); //d
            values.put("doHF", String.valueOf(hurtAffectsFly)); //d
            values.put("flL", String.valueOf(flyYLimitBlocksAboveGround)); // -1 is no limit //d

            values.put("aRec", String.valueOf(getAttackReactionIntFromEnum(attackReaction))); //d
            values.put("miDi", String.valueOf(minDistanceForRanged)); // d
            values.put("maDi", String.valueOf(maxDistanceForRanged)); // d
            values.put("taRa", String.valueOf(tacticalRange)); // d
            values.put("taCt", String.valueOf(tactics.ordinal())); // d

            values.put("desPa", String.valueOf(canDespawn)); //d
        }

        //d
        public void setLootData(int xpdrops, ItemStack[] drops, int[] chances, boolean playerOnly) {
            if (drops.length > chances.length) throw new RuntimeException("Weights not provided for all drops entity should have! This is an error!");
            values.put("xpdR", String.valueOf(xpdrops));
            for (int i = 0; i < drops.length; i++) {
                inventoryValues.put("drp" + i, drops[i]);
                values.put("drpCh" + i, String.valueOf(chances[i]));
            }
            values.put("drpN", String.valueOf(drops.length));
            values.put("ploN", String.valueOf(playerOnly));
        }

        //d
        private int getVisibilityIntFromEnum(EnumVisibility vis) {
            switch (vis) {
                case INVISIBLE:
                    return 1;
                case PARTIALLY_VISIBLE:
                    return 2;
                case VISIBLE:
                default:
                    return 0;
            }
        }

        //d
        private int getAttackReactionIntFromEnum(EnumAttackReaction ar) {
            switch (ar) {
                case PANIC:
                    return 1;
                case RUN:
                    return 2;
                case NONE:
                    return 3;
                case FIGHT:
                default:
                    return 0;
            }
        }

        //d
        public GenericEntityBuilder build() {
            if (!values.containsKey("maxH")) values.put("maxH", String.valueOf(20));
            if (!values.containsKey("pckU")) values.put("pckU", String.valueOf(false));
            if (!values.containsKey("navR")) values.put("navR", String.valueOf(50));
            if (!values.containsKey("movS")) values.put("movS", String.valueOf(0.3));
            if (!values.containsKey("MattD")) values.put("MattD", String.valueOf(4));
            if (!values.containsKey("modS")) values.put("modS", String.valueOf(5));
            if (!values.containsKey("visE")) values.put("visE", String.valueOf(0));
            if (!values.containsKey("visD")) values.put("visD", String.valueOf(0));
            if (!values.containsKey("visB")) values.put("visB", String.valueOf(1));
            if (!values.containsKey("xpdR")) values.put("xpdR", String.valueOf(1));
            if (!values.containsKey("aggR")) values.put("aggR", String.valueOf(16));
            if (!values.containsKey("resP")) setResistancesAndRegens(false, true, false, false, false, false, 1, 1, 1, 1, 1, 0);
            if (!values.containsKey("resM")) setMagicResistances(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            if (!values.containsKey("name")) values.put("name", "Human");
            if (!values.containsKey("rLoc")) values.put("rLoc", "textures/entity/steve.png");
            built = true;
            return this;
        }

        //d
        public HashMap<String, String> getValues() {
            if (!built) throw new RuntimeException("Attempting to get the values of a generic entity that hasn't been built properly! This is an error; please report it on the issue tracker.");
            return values;
        }

        //d
        public HashMap<String, ItemStack> getInvValues() {
            if (!built) throw new RuntimeException("Attempting to get the inventory values of a generic entity that hasn't been built properly! This is an error; please report it on the issue tracker.");
            return inventoryValues;
        }

        //d
        public BiomeEntitySpawnEntry getSpawnSettings() {
            return spawnEntry;
        }
    }

    private HashMap<String, String> values;
    public HashMap<String, ItemStack> invvalues;

    public boolean burnsInSun = false;
    public boolean potionImmune = false;
    public boolean canBreatheUnderwater = false;
    public float[] regen = new float[2]; // regen, combat regen
    public String name = "Human";
    public String modelLoc = "";
    public boolean isHuman = true;
    public boolean isAlex = false;
    public boolean hasCloak = false;
    public boolean hasGlowTexture = false;
    public boolean fullyMeleeResistant = false;
    public boolean fullyRangedResistant = false;
    public boolean copyRenderPasses = false;
    public boolean preferMelee = false;
    public boolean seesIndirect = false;
    public String textureLoc = "textures/entity/steve.png";
    public double size = 5;
    public int nametagColor = 0xFFFFFF;
    public int bossBarVis = 1;
    public int entityVis = 0;
    public int nametagVis = 0;

    @SideOnly(Side.CLIENT)
    public ResourceLocation resourceLocationTexture;
    @SideOnly(Side.CLIENT)
    public ResourceLocation resourceLocationCloakTexture;
    @SideOnly(Side.CLIENT)
    public ResourceLocation resourceLocationGlowTexture;
    @SideOnly(Side.CLIENT)
    public ModelBase model;

    // all classes that extend EntityGeneric *must* implement this constructor also
    public EntityGeneric(World world, GenericEntityBuilder geb) {
        this(world, geb.getValues(), geb.getInvValues());
    }

    // ONLY use if you know what you're doing
    public EntityGeneric(World world, HashMap<String, String> gebStr, HashMap<String, ItemStack> gebInv) {
        super(world);
        values = gebStr;
        invvalues = gebInv;

        setSize(1, 1);
        this.updateTasks();
        setClassVariablesFromValues();
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public String getValue(String name) { return values.get(name); }

    public void setClassVariablesFromValues() {
        this.experienceValue = Integer.valueOf(values.get("xpdR"));
        this.setCurrentItemOrArmor(0, invvalues.get("hand"));
        this.setCurrentItemOrArmor(1, invvalues.get("boot"));
        this.setCurrentItemOrArmor(2, invvalues.get("legs"));
        this.setCurrentItemOrArmor(3, invvalues.get("chest"));
        this.setCurrentItemOrArmor(4, invvalues.get("head"));
        this.setCanPickUpLoot(Boolean.valueOf(values.get("pckU")));
        this.getNavigator().setAvoidsWater(Boolean.valueOf(values.get("avLi")));
        // values that need to get accessed often are stored locally to reduce overhead
        burnsInSun = !Boolean.valueOf(values.get("resP").split(",")[1]);
        isImmuneToFire = Boolean.valueOf(values.get("resP").split(",")[2]);
        potionImmune = Boolean.valueOf(values.get("resP").split(",")[3]);
        canBreatheUnderwater = Boolean.valueOf(values.get("resP").split(",")[4]);
        regen = new float[]{Float.valueOf(values.get("resP").split(",")[10]), Float.valueOf(values.get("resP").split(",")[11])};
        name = values.get("name");
        size = Double.parseDouble(values.get("modS"));
        nametagColor = Integer.parseInt(values.get("namC"));
        nametagVis = Integer.parseInt(values.get("visD"));
        bossBarVis = Integer.parseInt(values.get("visB"));
        entityVis = Integer.parseInt(values.get("visE"));
        textureLoc = values.get("rLoc");
        modelLoc = values.get("mLoc");
        isHuman = Boolean.parseBoolean(values.get("mHum"));
        isAlex = Boolean.parseBoolean(values.get("mAl"));
        hasCloak = values.containsKey("cLoc");
        hasGlowTexture = values.containsKey("gLoc");
        fullyMeleeResistant = Float.parseFloat(values.get("resP").split(",")[8]) == 0;
        fullyRangedResistant = Float.parseFloat(values.get("resP").split(",")[7]) == 0;
        copyRenderPasses = Boolean.parseBoolean(values.get("copRP"));
        preferMelee = Boolean.parseBoolean(values.get("PrefM"));
        seesIndirect = Boolean.valueOf(values.get("indS"));
    }

    // should NOT be used. Only here for forge
    public EntityGeneric(World world) {
        super(world);
    }

    private int taskCount = 1;
    private EntityAIRangedAttack aiRange;
    private EntityAIBase aiResponse, aiAttackTarget, aiLeap, aiSprint;
    public FlyingMoveHelper flyMoveHelper = new FlyingMoveHelper(this);
    public PathNavigate flyNavigator = new PathNavigateFlying(this, worldObj);

    private void updateTasks() {
        if (worldObj == null || worldObj.isRemote) return;
        aiAttackTarget = aiResponse = aiLeap = aiSprint = null;
        aiRange = null;

        clearTasks(tasks);
        clearTasks(targetTasks);
        IEntitySelector attackEntitySelector = new EntityAttackSelector(this);
        this.targetTasks.addTask(0, new EntityAIClearTarget(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAIClosestTarget(this, EntityLivingBase.class, 4, !this.seesIndirect, false, attackEntitySelector));
//        this.targetTasks.addTask(3, new EntityAIOwnerHurtByTarget(this));
//        this.targetTasks.addTask(4, new EntityAIOwnerHurtTarget(this));

        if (canFly() || Boolean.parseBoolean(values.get("caSw"))) {
            this.getNavigator().setCanSwim(true);
            this.tasks.addTask(0, new EntityAISwimming(this));
        }

        this.taskCount = 1;
        if (Boolean.parseBoolean(values.get("moAr"))) this.tasks.addTask(this.taskCount++, new EntityAIWander(this));
        if (Boolean.parseBoolean(values.get("loAr"))) {
            this.tasks.addTask(this.taskCount++, new EntityAIWatchClosest(this, EntityPlayer.class, 7.0F));
            this.tasks.addTask(this.taskCount++, new EntityAILookIdle(this));
        }
        this.setOpensDoors();
        this.seekShelter();
        this.setResponse();
        this.tasks.addTask(this.taskCount++, new EntityAIAimingAnimation(this));
    }

    protected void updateAITasks()
    {
        try {
            super.updateAITasks();
        } catch (ConcurrentModificationException ignored){
        }

        this.getNavigator().onUpdateNavigation();
        this.getMoveHelper().onUpdateMoveHelper();
    }

    public void setCanLeap() {
        if (Boolean.parseBoolean(values.get("biJu"))) this.tasks.addTask(this.taskCount++, aiLeap = new EntityAILeapAtTarget(this, 0.45F));
    }

    public void setCanSprint() {
        if (Boolean.parseBoolean(values.get("spRi"))) this.tasks.addTask(this.taskCount++, aiSprint = new EntityAISprintToTarget(this));
    }

    public boolean isWalking() {
        return Boolean.parseBoolean(values.get("moAr")) || isAttacking() || !getNavigator().noPath();
    }

    public EnumNavType getTacticalVariant() {
        return EnumNavType.values()[Integer.parseInt(values.get("taCt"))];
    }

    public void setResponse(){
        removeTask(aiLeap);
        removeTask(aiResponse);
        removeTask(aiSprint);
        removeTask(aiAttackTarget);
        removeTask(aiRange);
        aiLeap = aiAttackTarget = aiResponse = aiSprint = aiRange = null;
        int onAttack = Integer.parseInt(values.get("aRec"));

        if (onAttack == 1)
            this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIPanic(this, 1.2F));

        else if (onAttack == 2)  {
            this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIAvoidTarget(this));
            this.setCanSprint();
        }

        else if (onAttack == 0) {
            this.setCanLeap();
            this.setCanSprint();
            int tacticalRadius = getIntegerValue("taRa");
            EnumNavType tacticalVariant = getTacticalVariant();
            if (invvalues.get("proj") == null || this.preferMelee)
            {
                switch(tacticalVariant)
                {
                    case Dodge : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIZigZagTarget(this, 1.0D, tacticalRadius)); break;
                    case Surround : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIOrbitTarget(this, 1.0D, tacticalRadius, true)); break;
                    case HitNRun : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIAvoidTarget(this)); break;
                    case Ambush : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIAmbushTarget(this, 1.2D, tacticalRadius, false)); break;
                    case Stalk : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIStalkTarget(this, tacticalRadius)); break;
                    default :
                }
            }
            else
            {
                switch(tacticalVariant)
                {
                    case Dodge : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIDodgeShoot(this)); break;
                    case Surround : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIOrbitTarget(this, 1.0D, getIntegerValue("maDi"), false)); break;
                    case HitNRun : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIAvoidTarget(this)); break;
                    case Ambush : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIAmbushTarget(this, 1.2D, tacticalRadius, false)); break;
                    case Stalk : this.tasks.addTask(this.taskCount++, aiResponse = new EntityAIStalkTarget(this, tacticalRadius)); break;
                    default :
                }
            }
            this.tasks.addTask(this.taskCount, aiAttackTarget = new EntityAIAttackTarget(this));
            ((EntityAIAttackTarget)aiAttackTarget).navOverride(tacticalVariant == EnumNavType.None);

            if(invvalues.get("proj") != null){
                this.tasks.addTask(this.taskCount++, aiRange = new EntityAIRangedAttack(this));
                aiRange.navOverride(tacticalVariant == EnumNavType.None);
            }
        }
        else if (onAttack == 3) {
            // none
        }
    }

    public EntityAIRangedAttack getRangedTask(){
        return this.aiRange;
    }

    public int getIntegerValue(String name) {
        return Integer.parseInt(values.get(name));
    }

    public PathNavigate getNavigator() {
        if(canFly())
            return this.flyNavigator;
        else {
            return super.getNavigator();
        }
    }

    public EntityMoveHelper getMoveHelper() {
        if(canFly())
            return this.flyMoveHelper;
        else {
            return super.getMoveHelper();
        }
    }

    public void setOpensDoors(){
        if(canFly()) return;
        EntityAIBase aiDoor = null;
        if (Boolean.parseBoolean(values.get("oDor"))) this.tasks.addTask(this.taskCount++, aiDoor = new EntityAIOpenDoor(this, true));
        this.getNavigator().setBreakDoors(aiDoor != null);
    }

    public void seekShelter() {
        if (Boolean.parseBoolean(values.get("lShe"))) {
            this.tasks.addTask(this.taskCount++, new EntityAIMoveIndoors(this));
        }
        else if (Boolean.parseBoolean(values.get("avSk"))) {
            if(!canFly()) this.tasks.addTask(this.taskCount++, new EntityAIRestrictSun(this));
            this.tasks.addTask(this.taskCount++, new EntityAIFindShade(this));
        }
    }

    public boolean canFly() {
        return Boolean.parseBoolean(values.get("caFl"));
    }

    private void clearTasks(EntityAITasks tasks){
        Iterator iterator = tasks.taskEntries.iterator();
        List<EntityAITasks.EntityAITaskEntry> list = new ArrayList(tasks.taskEntries);
        for (EntityAITasks.EntityAITaskEntry entityaitaskentry : list)
        {
            tasks.removeTask(entityaitaskentry.action);
        }
        tasks.taskEntries = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    }

    private void removeTask(EntityAIBase task){
        if(task != null) tasks.removeTask(task);
    }

    protected String getLivingSound()
    {
        return values.get("sndI");
    }

    protected String getHurtSound()
    {
        String sound = values.get("sndH");
        return sound != null ? sound : "game.neutral.hurt";
    }

    protected String getDeathSound()
    {
        String sound = values.get("sndD");
        return sound != null ? sound : "game.neutral.die";
    }

    // play walking sound
    protected void func_145780_a(int x, int y, int z, Block block)
    {
        String sound = values.get("sndS");
        if (sound != null) this.playSound(sound, 1.0F, 1.0F);
        else super.func_145780_a(x, y, z, block);
    }

    protected float getSoundVolume()
    {
        if (values.containsKey("sndV") && Float.parseFloat(values.get("sndV")) >= 0) return Float.parseFloat(values.get("sndV"));
        else return 1.0F;
    }

    protected float getSoundPitch()
    {
        if (values.containsKey("sndP") && Float.parseFloat(values.get("sndP")) >= 0) return Float.parseFloat(values.get("sndP"));
        else return super.getSoundPitch();
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect){
        if(potionImmune) return false;
        if(getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD && effect.getPotionID() == Potion.poison.id) return false;
        return super.isPotionApplicable(effect);
    }

    @Override
    public void setInWeb(){
        if(!Boolean.parseBoolean(values.get("byWe"))) super.setInWeb();
    }

    public boolean updateAI = false;

    public void onLivingUpdate()
    {
        if (potionImmune && getActivePotionEffects().size() > 0) clearActivePotions();
        if(!worldObj.isRemote) {
            if (!isDead && this.ticksExisted % 20 == 0) { // regen
                if (this.getHealth() < this.getMaxHealth()) {
                    if (regen[0] > 0 && !isAttacking()) heal(regen[0]);
                    if (regen[1] > 0 && isAttacking()) heal(regen[1]);
                }
                if(!isAttacking() && this.ticksExisted % 60 == 0){
                    List<EntityCreature> list = this.worldObj.getEntitiesWithinAABB(EntityCreature.class, this.boundingBox.expand(16, 16, 16));
                    for(EntityCreature mob : list){
                        if(mob.getAttackTarget() == null && this.canSee(mob) && this.getsAttackedByEntity(mob)){
                            if(mob instanceof EntityZombie && !mob.getEntityData().hasKey("AttackGeneric")){
                                mob.tasks.addTask(2, new EntityAIAttackOnCollide(mob, EntityLivingBase.class, 1.0D, false));
                                mob.getEntityData().setBoolean("AttackGeneric", true);
                            }
                            mob.setAttackTarget(this);
                        }
                    }
                }
                if(updateAI){
                    updateTasks();
                    updateAI = false;
                }
            }
            if (this.worldObj.isDaytime() && this.burnsInSun) { // sun burning
                float f = this.getBrightness(1.0F);

                if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
                    boolean flag = true;
                    ItemStack itemstack = this.getEquipmentInSlot(4);

                    if (itemstack != null) {
                        if (itemstack.isItemStackDamageable()) {
                            itemstack.setItemDamage(itemstack.getItemDamageForDisplay() + this.rand.nextInt(2));

                            if (itemstack.getItemDamageForDisplay() >= itemstack.getMaxDamage()) {
                                this.renderBrokenItemStack(itemstack);
                                this.setCurrentItemOrArmor(4, (ItemStack) null);
                            }
                        }
                        flag = false;
                    }
                    if (flag) {
                        this.setFire(8);
                    }
                }
            }
        }

        super.onLivingUpdate();

        if (worldObj.isRemote) {
            updateHitbox();
            if (hasCloak) cloakUpdate();
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return canBreatheUnderwater;
    }

    @Override
    protected int decreaseAirSupply(int p_70682_1_)
    {
        if (canBreatheUnderwater) return p_70682_1_;
        return super.decreaseAirSupply(p_70682_1_);
    }

    public void updateHitbox() {
        if (isRiding()){
            width = 0.6f;
            height = 1.8f * 0.77f;
        }
        else{
            width = 0.6f;
            height = 1.8f;
        }
        width = (float)((width / 5f) * getSize());
        height = (float)((height / 5f) * getSize());

        this.setPosition(posX, posY, posZ);
    }

    @Override
    protected void fall(float p_70069_1_) {
        if (!Boolean.valueOf(values.get("resP").split(",")[5]) && !canFly()) super.fall(p_70069_1_); // if !fallDamageResistance
    }

    @Override
    protected void updateFallState(double p_180433_1_, boolean p_180433_3_) {
        if(!canFly()) super.updateFallState(p_180433_1_, p_180433_3_);
    }

    @Override
    public boolean isOnLadder(){
        if (canFly()) return false;
        else return super.isOnLadder();
    }

    @Override
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_){
        if(!this.canFly() || (this.hurtTime != 0 && Boolean.parseBoolean(values.get("doHF")))) {
            super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
            return;
        }

        double heightOffGround = 0;
        int flyLimit = Integer.parseInt(values.get("flL"));
        if(flyLimit != -1) {
            for (int blockY = (int) this.posY; blockY > 0; blockY--) {
                heightOffGround = this.posY - blockY;
                if (this.worldObj.getBlock((int) this.posX, blockY, (int) this.posZ) != Blocks.air || heightOffGround > flyLimit){
                    break;
                }
            }
        }

        if(heightOffGround > flyLimit && (flyLimit != -1)){
            super.moveEntityWithHeading(p_70612_1_,p_70612_2_);
            return;
        }

        double d3 = this.motionY;
        super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
        this.motionY = d3;

        this.fallDistance = 0.0F;
        this.velocityChanged = true;

        if(this.getNavigator().noPath())
            this.motionY = 0.0D;
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        if (values.containsKey("ploN")) {
            if (p_70628_1_ || !Boolean.valueOf(values.get("ploN"))) {
                int luckyNum = this.rand.nextInt(101);
                int noOfDrops = Integer.valueOf(values.get("drpN"));
                for (int i = 0; i < noOfDrops; i++) {
                    if (luckyNum <= Integer.valueOf(values.get("drpCh" + i)))
                        this.entityDropItem(invvalues.get("drp" + i), 0.0F);
                }
            }
        }
    }

    public ItemStack getHeldItem() {
        return attackingRanged() ? invvalues.get("handR") : invvalues.get("hand");
    }

    public ItemStack getOffHand() {
        return attackingRanged() ? invvalues.get("handOR") : invvalues.get("handO");
    }

    public boolean attackingRanged() {
        if (aiRange != null && isAttacking()) {
            int mindistance = getIntegerValue("miDi");
            int maxdistance = getIntegerValue("maDi");
            double distToTarget = this.getDistanceSqToEntity(this.getAttackTarget());
            return (distToTarget > (mindistance * mindistance)) && (distToTarget < (maxdistance * maxdistance));
        }
        return false;
    }

    private boolean isAiming = false;

    public void setAiming(boolean aiming) {isAiming = aiming;}
    public boolean isAiming()
    {
        return isAiming;
    }

    public boolean isAggressiveToEntity(Entity entity) {
        Class matchTo = entity.getClass();
        String[] aggressiveTo = values.get("agrTo").split(",");
        while (matchTo.getSuperclass() != null) {
            if (contains(aggressiveTo, matchTo.getSimpleName())) return true;
            matchTo = matchTo.getSuperclass();
        }
        return false;
    }

    public boolean getsAttackedByEntity(Entity entity) {
        Class matchTo = entity.getClass();
        String[] attackedBy = values.get("attBy").split(",");
        while (matchTo.getSuperclass() != null) {
            if (contains(attackedBy, matchTo.getSimpleName())) return true;
            matchTo = matchTo.getSuperclass();
        }
        return false;
    }

    public static boolean contains(final String[] array, final String key) {
        for(int i = 0; i < array.length; i++) {
            if(array[i]==key) return true;
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (this.worldObj.isRemote || (damagesource.damageType != null && damagesource.damageType.equals("inWall"))){
            return false;
        }

        i = modifyDamageBasedOnResistance(damagesource, i);
        if((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F && i <= this.lastDamage)
            return false;

        Entity entity = damagesource.getEntity();

        EntityLivingBase attackingEntity = null;

        if (entity instanceof EntityLivingBase)
            attackingEntity = (EntityLivingBase) entity;

        if ((entity instanceof EntityArrow) && ((EntityArrow) entity).shootingEntity instanceof EntityLivingBase)
            attackingEntity = (EntityLivingBase) ((EntityArrow) entity).shootingEntity;
        else if ((entity instanceof EntityThrowable))
            attackingEntity = ((EntityThrowable) entity).getThrower();

        if(isDead)
            return false;

        if(attackingEntity == null)
            return super.attackEntityFrom(damagesource, i);

        if (isAttacking()){
            if(getAttackTarget() != null && attackingEntity != null && this.getDistanceSqToEntity(getAttackTarget()) > this.getDistanceSqToEntity(attackingEntity)){
                setAttackTarget(attackingEntity);
            }
            return super.attackEntityFrom(damagesource, i);
        }

        if (i > 0) setAttackTarget(attackingEntity);
        return super.attackEntityFrom(damagesource, i);
    }

    public float modifyDamageBasedOnResistance(DamageSource ds, float damage) {
        String[] resistances = values.get("resP").split(",");
        String[] mResistances = values.get("resM").split(",");
        if (resistances != null) {
            if (Boolean.valueOf(resistances[0])) { // invincible (not a comprehensive invincibility method, will be complemented later with other solutions)
                return 0;
            }

            if (ds.damageType.equals("arrow") || ds.damageType.equals("thrown")) {
                damage *= Float.parseFloat(resistances[7]); // ranged
            } else if (ds.damageType.equals("player") || ds.damageType.equals("mob")) {
                damage *= Float.parseFloat(resistances[8]); // melee
            } else if (ds.damageType.equals("explosion") || ds.damageType.equals("explosion.player")) {
                damage *= Float.parseFloat(resistances[9]); // explosion
            }
        }
        if (mResistances != null) {
            if (ds instanceof DamageSourceFire) {
                damage *= Float.parseFloat(mResistances[0]);
            } else if (ds instanceof DamageSourceFrost) {
                damage *= Float.parseFloat(mResistances[1]);
            } else if (ds instanceof DamageSourceWind) {
                damage *= Float.parseFloat(mResistances[2]);
            } else if (ds.isMagicDamage()) {
                damage *= Float.parseFloat(mResistances[3]);
            } else if (ds instanceof DamageSourceLightning) {
                damage *= Float.parseFloat(mResistances[4]);
            } else if (ds.damageType.equals("wither")) {
                damage *= Float.parseFloat(mResistances[5]);
            } else if (ds.damageType.equals("thorns")) {
                damage *= Float.parseFloat(mResistances[6]);
            } else if (ds.damageType.equals("cactus")) {
                damage *= Float.parseFloat(mResistances[7]);
            } else if (ds instanceof DamageSourceHoly) {
                damage *= Float.parseFloat(mResistances[8]);
            } else if (ds.damageType.equals("grail")) {
                damage *= Float.parseFloat(mResistances[9]);
            }
        }

        return damage;
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5)
    {
        double knockbackResistance = Double.valueOf(values.get("resP").split(",")[6]);

        if(knockbackResistance == 0) // remember, knockbackResistance is a multiplier, 0 = no knockback
            return;
        this.isAirBorne = true;
        float f1 = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
        float f2 = 0.5F * (float)knockbackResistance;
        this.motionX /= 2.0D;
        this.motionY /= 2.0D;
        this.motionZ /= 2.0D;
        this.motionX -= par3 / (double)f1 * (double)f2;
        this.motionY += 0.2 + f2 / 2;
        this.motionZ -= par5 / (double)f1 * (double)f2;

        if (this.motionY > 0.4000000059604645D)
        {
            this.motionY = 0.4000000059604645D;
        }
    }

    public boolean isAttacking() {
        return this.getAttackTarget() != null;
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity){
        double f = Double.valueOf(values.get("MattD"));

        if (Double.valueOf(values.get("MattM")) < 10){
            par1Entity.hurtResistantTime = 0;
        }

        boolean var4 = par1Entity.attackEntityFrom(new EntityDamageSource("mob", this), (float)f);

        if (var4){
            if (Double.valueOf(values.get("MattK")) > 0){
                par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * Double.valueOf(values.get("MattK")) * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * Double.valueOf(values.get("MattK")) * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
        }

        EntityProjectile.EnumPotionType potionType = EntityProjectile.EnumPotionType.values()[Integer.parseInt(values.get("potT"))];
        if (potionType != EntityProjectile.EnumPotionType.None){
            if (potionType != EntityProjectile.EnumPotionType.Fire)
                ((EntityLivingBase)par1Entity).addPotionEffect(new PotionEffect(this.getPotionEffect(potionType), Integer.parseInt(values.get("potD")) * 20, Integer.parseInt(values.get("potA"))));
            else
                par1Entity.setFire(Integer.parseInt(values.get("potD")));
        }
        return var4;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player){
        return entityVis == 1 && !playerHasMagitech(player);
    }

    @Override
    public boolean isInvisible(){
        return entityVis != 0; // != VISIBLE
    }

    public boolean showName() {
        return nametagVis == 0 || (nametagVis == 2 && isAttacking()); // visible or partially visible and attacking
    }

    public boolean getAlwaysRenderNameTagForRender(){
        return true;
    }

    @Override
    public void setInPortal(){
    }

    public double field_20066_r;
    public double field_20065_s;
    public double field_20064_t;
    public double field_20063_u;
    public double field_20062_v;
    public double field_20061_w;

    public void cloakUpdate() {
        field_20066_r = field_20063_u;
        field_20065_s = field_20062_v;
        field_20064_t = field_20061_w;
        double d = posX - field_20063_u;
        double d1 = posY - field_20062_v;
        double d2 = posZ - field_20061_w;
        double d3 = 10D;
        if (d > d3) {
            field_20066_r = field_20063_u = posX;
        }
        if (d2 > d3) {
            field_20064_t = field_20061_w = posZ;
        }
        if (d1 > d3) {
            field_20065_s = field_20062_v = posY;
        }
        if (d < -d3) {
            field_20066_r = field_20063_u = posX;
        }
        if (d2 < -d3) {
            field_20064_t = field_20061_w = posZ;
        }
        if (d1 < -d3) {
            field_20065_s = field_20062_v = posY;
        }
        field_20063_u += d * 0.25D;
        field_20061_w += d2 * 0.25D;
        field_20062_v += d1 * 0.25D;
    }

    @Override
    protected boolean canDespawn() {
        return Boolean.parseBoolean(values.get("desPa"));
    }

    private int getPotionEffect(EntityProjectile.EnumPotionType p) {
        switch(p)
        {
            case Poison : return Potion.poison.id;
            case Hunger : return Potion.hunger.id;
            case Weakness : return Potion.weakness.id;
            case Slowness : return Potion.moveSlowdown.id;
            case Nausea : return Potion.confusion.id;
            case Blindness : return Potion.blindness.id;
            case Wither : return Potion.wither.id;
            default : return 0;
        }
    }

    @Override
    public String getCommandSenderName() {
        return name;
    }

    public double getSize() {
        return size;
    }

    public int getNametagColor() {
        return nametagColor;
    }

    public int getBossBarVisiblity() {
        return bossBarVis;
    }

    public boolean canSee(Entity entity){
        return this.getEntitySenses().canSee(entity);
    }

    // not overwriting any parent method, the plan is to use locally for spawning and some other stuff
    public EnumCreatureType getCreatureType()
    {
        if (values.containsKey("crtT")) return EnumCreatureType.values()[Integer.valueOf(values.get("crtT"))];
        return EnumCreatureType.creature;
    }

    public EnumCreatureAttribute getCreatureAttribute()
    {
        if (values.containsKey("crtA")) return EnumCreatureAttribute.values()[Integer.valueOf(values.get("crtA"))];
        return EnumCreatureAttribute.UNDEFINED;
    }

    protected Entity findPlayerToAttack()
    {
        EntityPlayer entityplayer = this.getClosestVulnerablePlayer(this.posX, this.posY, this.posZ, Double.valueOf(values.get("aggR")));
        return entityplayer != null && (seesIndirect || this.canEntityBeSeen(entityplayer)) ? entityplayer : null;
    }

    public EntityPlayer getClosestVulnerablePlayer(double p_72846_1_, double p_72846_3_, double p_72846_5_, double p_72846_7_)
    {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.worldObj.playerEntities.size(); ++i)
        {
            EntityPlayer entityplayer1 = (EntityPlayer)this.worldObj.playerEntities.get(i);

            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive())
            {
                double d5 = entityplayer1.getDistanceSq(p_72846_1_, p_72846_3_, p_72846_5_);
                double d6 = p_72846_7_;

                if (entityplayer1.isSneaking() && !seesIndirect)
                {
                    d6 = p_72846_7_ * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible() && !Boolean.valueOf(values.get("invS")))
                {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F)
                    {
                        f = 0.1F;
                    }

                    d6 *= (double)(0.7F * f);
                }

                if ((p_72846_7_ < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4))
                {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }


    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Double.valueOf(values.get("maxH")));
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Double.valueOf(values.get("navR")));
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Double.valueOf(values.get("movS")));
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Double.valueOf(values.get("MattD")));
    }

    protected boolean isAIEnabled(){
        return true;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        try {
            writeNBT(buffer, writeSpawnData(new NBTTagCompound()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound writeSpawnData(NBTTagCompound compound) {
        int c = 0;
        for (Object o : values.keySet()) {
            String iS = (String)o;
            String iValue = values.get(iS);
            compound.setString("value" + c, iValue);
            compound.setString("valuename" + c, iS);
            c++;
        }
        compound.setInteger("valuesize", values.size());
        c = 0;
        for (Object o : invvalues.keySet()) {
            String iS = (String)o;
            ItemStack iValue = invvalues.get(iS);
            NBTTagCompound spellCompound = iValue.writeToNBT(new NBTTagCompound());
            compound.setTag("inv_value" + c, spellCompound);
            compound.setString("inv_valuename" + c, iS);
            c++;
        }
        compound.setInteger("inv_valuesize", invvalues.size());
        return compound;
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            readSpawnData(readNBT(additionalData));
        } catch (IOException e) {
        }
    }

    public void readSpawnData(NBTTagCompound compound) {
        values.clear();
        invvalues.clear();
        for (int j = 0; j < compound.getInteger("valuesize"); j++) {
            values.put(compound.getString("valuename" + j), compound.getString("value" + j));
        }
        for (int j = 0; j < compound.getInteger("inv_valuesize"); j++) {
            invvalues.put(compound.getString("inv_valuename" + j), ItemStack.loadItemStackFromNBT(compound.getCompoundTag("inv_value" + j)));
        }
        setClassVariablesFromValues();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        writeSpawnData(p_70014_1_);
        super.writeEntityToNBT(p_70014_1_);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        readSpawnData(p_70037_1_);
        super.readEntityFromNBT(p_70037_1_);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Double.valueOf(values.get("navR")));
        this.updateTasks();
    }

    public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
        byte[] bytes = CompressedStreamTools.compress(compound);
        buffer.writeShort((short)bytes.length);
        buffer.writeBytes(bytes);
    }

    public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
        byte[] bytes = new byte[buffer.readShort()];
        buffer.readBytes(bytes);
        return CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(2097152L));
    }

    @Override
    public void addChatMessage(IChatComponent p_145747_1_) {}

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return new ChunkCoordinates(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
    }

    @Override
    public World getEntityWorld() {
        return this.worldObj;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entity, float f) {
        ItemStack proj = invvalues.get("proj");
        if(proj == null){
            updateTasks();
            return;
        }
        for(int i = 0; i < Integer.parseInt(values.get("RattSc")); i++)
        {
            EntityProjectile projectile = shoot(entity, Double.valueOf(values.get("RattA")), proj, f == 1);
            projectile.damage = Float.valueOf(values.get("RattD"));
            projectile.punch = Integer.parseInt(values.get("RattK"));
        }
        this.playSound(values.get("RattSo"), getSoundVolume(), 1.0f);

    }

    public EntityProjectile shoot(EntityLivingBase entity, double accuracy, ItemStack proj, boolean indirect){
        return shoot(entity.posX, entity.boundingBox.minY + (double)(entity.height / 2.0F), entity.posZ, accuracy, proj, indirect);
    }

    public EntityProjectile shoot(double x, double y, double z, double accuracy, ItemStack proj, boolean indirect){
        EntityProjectile projectile = new EntityProjectile(this.worldObj, this, proj.copy(), true);
        double varX = x - this.posX;
        double varY = y - (this.posY + this.getEyeHeight());
        double varZ = z - this.posZ;
        float varF = projectile.hasGravity() ? MathHelper.sqrt_double(varX * varX + varZ * varZ) : 0.0F;
        float angle = projectile.getAngleForXYZ(varX, varY, varZ, varF, indirect);
        float acc = 20.0F - MathHelper.floor_double(accuracy / 5.0D);
        projectile.setThrowableHeading(varX, varY, varZ, angle, acc);
        worldObj.spawnEntityInWorld(projectile);
        return projectile;
    }
}
