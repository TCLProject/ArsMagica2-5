package net.tclproject.mysteriumlib.render.gecko;

import am2.AMCore;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AdvancedNPCEntity;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.*;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityIceDragon;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexLarva;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexPupa;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexSentinel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public enum EntityReg {
    INSTANCE;

    private final Map ENTITY_EGGS = new LinkedHashMap();
    private int nextEntityId = 131;

    public void onInit() {
        this.registerEntity(CrystalCrabEntity.class, "CrystalCrab", true, 4735339, 96598220, false, 20, 3, 1, EnumCreatureType.monster, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(ChickenEntity.class, "GoodChicken", true, 16737215, 45436977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(HorseEntity.class, "GoodHorse", true, 0x0, 0xc9bc2f, false, 20, 3, 1, EnumCreatureType.monster, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(AdvancedNPCEntity.class, "AdvancedNPC", false, 35777215, 53776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(AngelEntity.class, "GeckoAngel", false, 23777215, 85776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(StingrayTestEntity.class, "GeckoStingray", false, 53777215, 56776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(BrownEntity.class, "GeckoBrown", true, 23777215, 16776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(RobotEntity.class, "GeckoRobot", true, 74777215, 45776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(FairyEntity.class, "Fairy", false, 45777215, 73776977, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(DragonEntity.class, "Dragon", false, 0x283f89, 0x141f44, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(EntityIceDragon.class, "DragonTabula", false, 0x324fac, 0x5a72bc, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(StonelingEntity.class, "Stoneling", true, 12777215, 5252927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(EntityMyrmexLarva.class, "MyrmexLarva", true, 11777215, 6452927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(EntityMyrmexPupa.class, "MyrmexPupa", true, 23777215, 3452927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(EntityMyrmexSentinel.class, "MyrmexSentinel", true, 85777215, 1052927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(EntEntity.class, "EntEntity", false, 55777215, 2352927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(PortalEntity.class, "PortalEntity", false, 86777215, 1452927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(PointerEntity.class, "PointerEntity", true, 58777215, 2352927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(TankEntity.class, "TankEntity", false, 75777215, 1052927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(TrapEntity.class, "TrapEntity", false, 47777215, 5452927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(DuckEntity.class, "DuckEntity", true, 45777215, 2352927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(AncientAbyssEntity.class, "AncientAbyssEntity", false, 64777215, 4752927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(CrowEntity.class, "CrowEntity", false, 43777215, 4652927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
        this.registerEntity(TentacleEntity.class, "TentacleEntity", true, 34777215, 4352927, false, 20, 3, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills, BiomeGenBase.jungle, BiomeGenBase.jungleEdge);
    }

    public void registerEntity(Class entityClass, String name, boolean addEgg, int mainColor, int subColor, boolean addSpawn, int spawnFrequency, int minGroup, int maxGroup, EnumCreatureType typeOfCreature, BiomeGenBase... biomes) {
        int entityId = this.nextEntityId();
        EntityRegistry.registerModEntity(entityClass, name, entityId, AMCore.instance, 64, 1, true);
        if (addEgg) {
            this.ENTITY_EGGS.put(entityId, new ItemSpawnEgg.EntityEggInfo(entityId, mainColor, subColor));
        }

        if (addSpawn) {
            EntityRegistry.addSpawn(entityClass, spawnFrequency, minGroup, maxGroup, typeOfCreature, biomes);
        }

    }

    private int nextEntityId() {
        return this.nextEntityId++;
    }

    public ItemSpawnEgg.EntityEggInfo getEntityEggInfo(int id) {
        return (ItemSpawnEgg.EntityEggInfo)this.ENTITY_EGGS.get(id);
    }

    public boolean hasEntityEggInfo(int id) {
        return this.ENTITY_EGGS.containsKey(id);
    }

    public Iterator getEntityEggInfoIterator() {
        return this.ENTITY_EGGS.values().iterator();
    }

    public Entity createEntityById(int id, World world) {
        Entity entity = null;

        try {
            EntityRegistry.EntityRegistration reg = EntityRegistry.instance().lookupModSpawn(GeckoLib.getModContainer(), id);
            Class clazz = reg.getEntityClass();
            if (clazz != null) {
                entity = (Entity)clazz.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return entity;
    }

    public String getEntityNameById(int id) {
        String name = "missingno";

        try {
            name = EntityRegistry.instance().lookupModSpawn(GeckoLib.getModContainer(), id).getEntityName();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return name;
    }
}
