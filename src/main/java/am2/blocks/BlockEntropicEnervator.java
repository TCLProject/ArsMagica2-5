package am2.blocks;

import am2.AMCore;
import am2.api.blocks.IKeystoneLockable;
import am2.api.items.KeystoneAccessType;
import am2.blocks.tileentities.TileEntityEntropicEnervator;
import am2.guis.ArsMagicaGuiIdList;
import am2.texture.ResourceManager;
import am2.utility.KeystoneUtilities;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEntropicEnervator extends PoweredBlock{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private String[] textureNames = {"entropic_enervator_top", "entropic_enervator_front_idle", "entropic_enervator_front_active"};

	public BlockEntropicEnervator(){
		super(Material.iron);
		setHardness(3.0f);
	}

	private void setDefaultDirection(World world, int i, int j, int k){
		if (world.isRemote){
			return;
		}
		Block l = world.getBlock(i, j, k - 1);
		Block i1 = world.getBlock(i, j, k + 1);
		Block j1 = world.getBlock(i - 1, j, k);
		Block k1 = world.getBlock(i + 1, j, k);
		byte byte0 = 3;
		if (l.isOpaqueCube() && !i1.isOpaqueCube()){
			byte0 = 3;
		}
		if (i1.isOpaqueCube() && !l.isOpaqueCube()){
			byte0 = 2;
		}
		if (j1.isOpaqueCube() && !k1.isOpaqueCube()){
			byte0 = 5;
		}
		if (k1.isOpaqueCube() && !j1.isOpaqueCube()){
			byte0 = 4;
		}
		world.setBlockMetadataWithNotify(i, j, k, byte0, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		this.icons = new IIcon[textureNames.length];

		for (int i = 0; i < textureNames.length; ++i){
			this.icons[i] = ResourceManager.RegisterTexture(textureNames[i], iconRegister);
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int i, int j, int k, int side){
		if (side == 1 || side == 0) //top or bottom
		{
			return icons[0];
		} else {
			int i1 = iblockaccess.getBlockMetadata(i, j, k);
			boolean flag = (i1 & 0x8) == 0x8;
			if (flag){
				return icons[2];
			}
			return icons[1];
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (side == 1 || side == 0) //top or bottom
		{
			return icons[0];
		} else {
			boolean flag = (meta & 0x8) == 0x8;
			if (flag)
				return icons[2];
			return icons[1];
		}
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if (this.HandleSpecialItems(par1World, par5EntityPlayer, par2, par3, par4)){
			return true;
		}
		super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
		if (!par1World.isRemote){
			FMLNetworkHandler.openGui(par5EntityPlayer, AMCore.instance, ArsMagicaGuiIdList.GUI_ENERVATOR, par1World, par2, par3, par4);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack){

		int l = MathHelper.floor_double((entityliving.rotationYaw * 4F) / 360F + 0.5D) & 3;
		if (l == 0){
			world.setBlockMetadataWithNotify(i, j, k, 2, 2);
		}
		if (l == 1){
			world.setBlockMetadataWithNotify(i, j, k, 5, 2);
		}
		if (l == 2){
			world.setBlockMetadataWithNotify(i, j, k, 3, 2);
		}
		if (l == 3){
			world.setBlockMetadataWithNotify(i, j, k, 4, 2);
		}

		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int metadata){
		TileEntityEntropicEnervator refiner = (TileEntityEntropicEnervator)world.getTileEntity(i, j, k);
		if (refiner == null) return;
		for (int l = 0; l < refiner.getSizeInventory(); l++){
			ItemStack itemstack = refiner.getStackInSlot(l);
			if (itemstack == null){
				continue;
			}
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
			do{
				if (itemstack.stackSize <= 0){
					break;
				}
				int i1 = world.rand.nextInt(21) + 10;
				if (i1 > itemstack.stackSize){
					i1 = itemstack.stackSize;
				}
				itemstack.stackSize -= i1;
				ItemStack newItem = new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage());
				newItem.setTagCompound(itemstack.getTagCompound());
				EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, newItem);
				float f3 = 0.05F;
				entityitem.motionX = (float)world.rand.nextGaussian() * f3;
				entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float)world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}while (true);
		}

		super.breakBlock(world, i, j, k, par5, metadata);
	}
	@Override
	public TileEntity createNewTileEntity(World par1World, int i){
		return new TileEntityEntropicEnervator();
	}
}
