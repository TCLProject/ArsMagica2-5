package net.tclproject.mysteriumlib.render.gecko;

import java.util.Iterator;
import java.util.List;

import am2.items.ItemsCommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemSpawnEgg extends Item {
    private IIcon overlay;

    public ItemSpawnEgg() {
        this.setUnlocalizedName("monsterPlacer");
        this.setTextureName("spawn_egg");
        this.setHasSubtypes(true);
        this.setCreativeTab(ItemsCommonProxy.summonsTab);
    }

    public static Entity spawnCreature(World world, int id, double x, double y, double z) {
        if (EntityReg.INSTANCE.hasEntityEggInfo(id)) {
            Entity entity = EntityReg.INSTANCE.createEntityById(id, world);
            if (entity instanceof EntityLivingBase) {
                EntityLiving entityLiving = (EntityLiving)entity;
                entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                entityLiving.rotationYawHead = entityLiving.rotationYaw;
                entityLiving.renderYawOffset = entityLiving.rotationYaw;
                entityLiving.onSpawnWithEgg((IEntityLivingData)null);
                world.spawnEntityInWorld(entity);
                entityLiving.playLivingSound();
            }

            return entity;
        } else {
            return null;
        }
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        String name = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String entityName = EntityReg.INSTANCE.getEntityNameById(itemStack.getItemDamage());
        if (entityName != null) {
            name = name + " " + StatCollector.translateToLocal("entity." + entityName + ".name");
        }

        return name;
    }

    public int getColorFromItemStack(ItemStack itemStack, int pass) {
        EntityEggInfo info = EntityReg.INSTANCE.getEntityEggInfo(itemStack.getItemDamage());
        return info == null ? 16777215 : (pass == 0 ? info.primaryColor : info.secondaryColor);
    }

    public boolean onItemUse(ItemStack heldItemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double yOffset = 0.0D;
            if (side == 1 && block.getRenderType() == 11) {
                yOffset = 0.5D;
            }

            Entity entity = spawnCreature(world, heldItemStack.getItemDamage(), (double)x + 0.5D, (double)y + yOffset, (double)z + 0.5D);
            if (entity != null) {
                if (entity instanceof EntityLivingBase && heldItemStack.hasDisplayName()) {
                    ((EntityLiving)entity).setCustomNameTag(heldItemStack.getDisplayName());
                }

                if (!player.capabilities.isCreativeMode) {
                    --heldItemStack.stackSize;
                }
            }

            return true;
        }
    }

    public ItemStack onItemRightClick(ItemStack heldItemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return heldItemStack;
        } else {
            MovingObjectPosition hitVector = this.getMovingObjectPositionFromPlayer(world, player, true);
            if (hitVector == null) {
                return heldItemStack;
            } else {
                if (hitVector.typeOfHit == MovingObjectType.BLOCK) {
                    int x = hitVector.blockX;
                    int y = hitVector.blockY;
                    int z = hitVector.blockZ;
                    if (!world.canMineBlock(player, x, y, z)) {
                        return heldItemStack;
                    }

                    if (!player.canPlayerEdit(x, y, z, hitVector.sideHit, heldItemStack)) {
                        return heldItemStack;
                    }

                    if (world.getBlock(x, y, z) instanceof BlockLiquid) {
                        Entity entity = spawnCreature(world, heldItemStack.getItemDamage(), (double)x, (double)y, (double)z);
                        if (entity instanceof EntityLivingBase && heldItemStack.hasDisplayName()) {
                            ((EntityLiving)entity).setCustomNameTag(heldItemStack.getDisplayName());
                        }

                        if (!player.capabilities.isCreativeMode) {
                            --heldItemStack.stackSize;
                        }
                    }
                }

                return heldItemStack;
            }
        }
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass > 0 ? this.overlay : super.getIconFromDamageForRenderPass(damage, pass);
    }

    public void getSubItems(Item item, CreativeTabs tab, List subItems) {
        Iterator iterator = EntityReg.INSTANCE.getEntityEggInfoIterator();

        while(iterator.hasNext()) {
            EntityEggInfo info = (EntityEggInfo)iterator.next();
            subItems.add(new ItemStack(item, 1, info.entityID));
        }

    }

    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        this.overlay = register.registerIcon(this.getIconString() + "_overlay");
    }

    public static class EntityEggInfo {
        public int entityID;
        public int primaryColor;
        public int secondaryColor;

        public EntityEggInfo(int entityID, int primaryColor, int secondaryColor) {
            this.entityID = entityID;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
        }
    }
}
