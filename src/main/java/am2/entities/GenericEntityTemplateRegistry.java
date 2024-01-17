package am2.entities;

import am2.AMEventHandler;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class GenericEntityTemplateRegistry<T extends EntityGeneric.GenericEntityBuilder, Y extends Class<? extends EntityGeneric>> {

    public static GenericEntityTemplateRegistry INSTANCE = new GenericEntityTemplateRegistry();
    private Map<String, T> nameToTemplateMap = new HashMap<>();
    private Map<T, Y> templateToEntityClassMap = new HashMap<>();

    public static void init() {
        // all entity template building and registration will be here
    };

    public void registerEntityTemplate(String name, T builder) {
        INSTANCE.nameToTemplateMap.put(name, builder);
    }

    // classes that extend EntityGeneric
    public void registerSpecialEntityBuilder(T builder, Y entityClass) {
        INSTANCE.templateToEntityClassMap.put(builder, entityClass);
    }

    public static void unregisterEntityTemplate(String name) {
        if (INSTANCE.nameToTemplateMap.containsKey(name)) {
            EntityGeneric.BiomeEntitySpawnEntry spawnEntry = ((EntityGeneric.GenericEntityBuilder)INSTANCE.nameToTemplateMap.get(name)).getSpawnSettings();
            if (spawnEntry != null) AMEventHandler.spawnEntries.remove(spawnEntry);
            INSTANCE.nameToTemplateMap.remove(name);
        }
    }

    public T getEntityTemplate(String name) {
        return (T)INSTANCE.nameToTemplateMap.get(name);
    }

    public Y getEntitySpecialClass(T builder) {
        if (INSTANCE.templateToEntityClassMap.get(builder) != null) return (Y)INSTANCE.templateToEntityClassMap.get(builder);
        else return null;
    }

    public static <P extends EntityGeneric> P createBasicEntityInstance(World world, String name) {
        if (INSTANCE.getEntitySpecialClass(INSTANCE.getEntityTemplate(name)) == null) {
            return (P)(new EntityGeneric(world, (EntityGeneric.GenericEntityBuilder) INSTANCE.nameToTemplateMap.get(name)));
        } else {
            try {
                Object specialEntityGeneric = INSTANCE.getEntitySpecialClass(INSTANCE.getEntityTemplate(name)).getDeclaredConstructor(World.class, EntityGeneric.GenericEntityBuilder.class).newInstance(world, (EntityGeneric.GenericEntityBuilder) INSTANCE.nameToTemplateMap.get(name));
                return (P)specialEntityGeneric;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create special generic entity instance! This is most likely due to an unimplemented (World, GenericEntityBuilder) constructor.", e);
            }
        }
    }

    public <P extends EntityGeneric> P createBasicEntityInstance(World world, T builder) {
        if (INSTANCE.getEntitySpecialClass(builder) == null) {
            return (P)(new EntityGeneric(world, builder));
        } else {
            try {
                Object specialEntityGeneric = INSTANCE.getEntitySpecialClass(builder).getDeclaredConstructor(World.class, EntityGeneric.GenericEntityBuilder.class).newInstance(world, builder);
                return (P)specialEntityGeneric;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create special generic entity instance! This is most likely due to an unimplemented (World, GenericEntityBuilder) constructor.", e);
            }
        }
    }
}
