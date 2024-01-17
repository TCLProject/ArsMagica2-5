package am2.items;

import am2.texture.ResourceManager;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemBossRecord extends ItemRecord {

    private final String music_file;
    private final String record_file;

    public ItemBossRecord(String record) {
        super("arsmagica2:" + record);
        music_file = "arsmagica2:mob.boss." + record;
        record_file = record;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = ResourceManager.RegisterTexture("record_" + record_file, par1IconRegister);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(music_file);
    }

    @SideOnly(Side.CLIENT)
    public String getRecordNameLocal()
    {
        return StatCollector.translateToLocal("item.arsmagica2:" + record_file + ".desc");
    }
}