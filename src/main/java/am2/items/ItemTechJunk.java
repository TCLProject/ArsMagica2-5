package am2.items;

import am2.texture.ResourceManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemTechJunk extends ArsMagicaItem{
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public static final int META_TECHNOLOGICALJUNK = 0;
    public static final int META_SCRAPELECTRONICS = 1;
    public static final int META_STRANGEALLOY = 2;
    public static final int META_IRONDUST = 3;
    public static final int META_IRONRODS = 4;

    public ItemTechJunk(){
        super();
        this.setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack){
        int meta = par1ItemStack.getItemDamage();
        switch (meta) {
            case META_TECHNOLOGICALJUNK:
                return StatCollector.translateToLocal("item.arsmagica2:technologicalJunk.name");
            case META_SCRAPELECTRONICS:
                return StatCollector.translateToLocal("item.arsmagica2:scrapElectronics.name");
            case META_STRANGEALLOY:
                return StatCollector.translateToLocal("item.arsmagica2:strangeAlloy.name");
            case META_IRONDUST:
                return StatCollector.translateToLocal("item.arsmagica2:ironDust.name");
            case META_IRONRODS:
                return StatCollector.translateToLocal("item.arsmagica2:ironRods.name");
        }

        return super.getItemStackDisplayName(par1ItemStack);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister){
        icons = new IIcon[5];

        for (int i = 0; i < 5; i++){
            icons[i] = ResourceManager.RegisterTexture("tech_junk" + (i + 1), par1IconRegister);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta){
        return icons[meta % icons.length];
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        for (int i = 0; i < icons.length; ++i){
            par3List.add(new ItemStack(this, 1, i));
        }
    }
}
