package am2.entities.renderers;

import am2.entities.models.ModelPlayerSpecial;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.UUID;

import static net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka.playerModelMap;

@SideOnly(Side.CLIENT)
public class RenderPlayerSpecial extends RenderPlayer {

    private boolean cachedAlex;

    private static final ModelPlayerSpecial ALEX = new ModelPlayerSpecial(0.0F, true);

    public RenderPlayerSpecial() {
        renderManager = RenderManager.instance;
        mainModel = modelBipedMain = ALEX;
    }

    @Override
    protected int shouldRenderPass(AbstractClientPlayer player, int pass, float partialTickTime) {
        return super.shouldRenderPass(player, pass, partialTickTime);
    }

    @Override
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float someFloat, float partialTickTime) {
        super.doRender(player, x, y, z, someFloat, partialTickTime);
    }

    @Override
    protected void renderEquippedItems(AbstractClientPlayer player, float partialTickTime) {
        super.renderEquippedItems(player, partialTickTime);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        return new ResourceLocation("arsmagica2", "textures/models/" + playerModelMap.get(((EntityPlayer)player).getCommandSenderName()) + ".png");
    }

    public static ResourceLocation getEntityTextureFromOutside(AbstractClientPlayer player) {
        return new ResourceLocation("arsmagica2", "textures/models/" + playerModelMap.get(((EntityPlayer)player).getCommandSenderName()) + ".png");
    }

    @Override
    protected boolean func_110813_b(EntityLivingBase entity) {
        boolean isGUiEnabled = Minecraft.isGuiEnabled();
        boolean isPlayer = entity != renderManager.livingPlayer;
        boolean isInvisible = !entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
        boolean isBeingRidden = entity.riddenByEntity == null;

        return isGUiEnabled && isPlayer && isInvisible && isBeingRidden;
    }

    @Override
    public void renderFirstPersonArm(EntityPlayer player) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(player));

        super.renderFirstPersonArm(player);

        // This call is not needed at all due to bipedRightArmwear being a Child of bipedRightArm, therefore moving and rendering with it automatically already.
        //((ModelPlayer) modelBipedMain).bipedRightArmwear.render(0.0625F);
    }
}