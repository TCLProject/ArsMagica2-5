package am2.entities.renderers;

import am2.entities.EntityGeneric;
import am2.entities.models.ModelBipedGeneric;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

// large courtesy of CNPCs+
public class RenderEntityGeneric extends RenderLiving {

    public ModelBase originalModel;
    private ModelBipedGeneric modelBipedMain;
    protected ModelBipedGeneric modelArmorChestplate;
    protected ModelBipedGeneric modelArmor;
    protected final ModelBipedGeneric steve = new ModelBipedGeneric(0, false);
    protected final ModelBipedGeneric alex = new ModelBipedGeneric(0, true);

    private RendererLivingEntity renderEntity;
    private EntityLivingBase entity;

    public RenderEntityGeneric(ModelBase model, float f){
        super(model, f);
        this.originalModel = model;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        EntityGeneric generic = (EntityGeneric) entity;
        if (generic.resourceLocationTexture == null) generic.resourceLocationTexture = new ResourceLocation(generic.textureLoc);
        return generic.resourceLocationTexture;
    }

    protected void renderName(EntityGeneric generic, double d, double d1, double d2) {
        if (!this.func_110813_b(generic))
            return;
        float f2 = generic.getDistanceToEntity(renderManager.livingPlayer);
        float f3 = generic.isSneaking() ? 32F : 64F;

        if (f2 > f3)
            return;
        double scale = (1.8f / 5f) * generic.getSize();
        if (generic.showName()) {
            String s = generic.getCommandSenderName();
            renderLivingLabel(generic, d, d1 + generic.height - 0.06f * scale, d2, 64, s, 1f);
        }
    }

    public void doRenderShadowAndFire(Entity par1Entity, double par2, double par4, double par6, float par8, float par9){
        EntityGeneric npc = (EntityGeneric) par1Entity;
        if(!npc.isDead && !npc.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer))
            super.doRenderShadowAndFire(par1Entity, par2, par4, par6, par8, par9);
    }

    protected void renderLivingLabel(EntityGeneric generic, double d, double d1, double d2, int i, Object... objects){
        FontRenderer fontrenderer = getFontRendererFromRenderManager();

        i = generic.getBrightnessForRender(0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);

        double f1 = (1.8f / 5f) * generic.getSize(); // 1.8f is baseHeight
        double f2 = 0.01666667F * f1;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.0F, (float)d1, (float)d2);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        Tessellator tessellator = Tessellator.instance;
        double height = f1 / 6.5f;
        for(j = 0; j < objects.length; j += 2){
            float scale = (Float) objects[j + 1];
            height += f1 / 6.5f * scale;
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            String s = objects[j].toString();
            GL11.glTranslated(0, height, 0);
            GL11.glScaled(-f2 * scale, -f2 * scale, f2 * scale);
            tessellator.startDrawingQuads();
            int size = fontrenderer.getStringWidth(s) / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            tessellator.addVertex(-size - 1, -1, 0.0D);
            tessellator.addVertex(-size - 1, 8 , 0.0D);
            tessellator.addVertex(size + 1, 8 , 0.0D);
            tessellator.addVertex(size + 1, -1 , 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int color = generic.getNametagColor();
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, color);
            GL11.glPopMatrix();
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    protected void renderPlayerScale(EntityGeneric gen, float f){
        double scaleFactor = 0.9375f / 5;
        GL11.glScaled(scaleFactor * gen.getSize(), scaleFactor * gen.getSize(), scaleFactor * gen.getSize());
    }

    protected void renderGenericEntityLiving(EntityGeneric generic, double d, double d1, double d2){
        shadowSize = (float)generic.getSize() / 10f;
        renderLiving(generic, d, d1, d2, 0, 0, 0);
    }
    private void renderLiving(EntityGeneric generic, double d, double d1, double d2, double xoffset, double yoffset, double zoffset){
        xoffset = (xoffset/ 5f) * generic.getSize();
        yoffset = (yoffset/ 5f) * generic.getSize();
        zoffset = (zoffset/ 5f) * generic.getSize();
        super.renderLivingAt(generic, d+xoffset, d1+yoffset, d2 + zoffset);
    }

    public static ModelBase getMainModel(RendererLivingEntity render){
        return render.mainModel;
    }

    @Override
    protected void passSpecialRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6){
        renderName((EntityGeneric) par1EntityLivingBase, par2, par4, par6);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityliving, float f){
        if(renderEntity != null){
            EntityGeneric generic = (EntityGeneric)entityliving;
            double size = generic.getSize();
            generic.size = 5;
            ProtectedRenderHelper.preRenderCallback(entity, f, renderEntity);
            generic.size = size;
            GL11.glScaled(0.2f * generic.getSize(), 0.2f * generic.getSize(), 0.2f * generic.getSize());
        }
        else renderPlayerScale((EntityGeneric)entityliving, f);
    }

    @Override
    protected void renderLivingAt(EntityLivingBase entityliving, double d, double d1, double d2){
        renderGenericEntityLiving((EntityGeneric) entityliving, d, d1, d2);
    }

    public void doRenderNotPlayer(EntityLiving entityliving, double d, double d1, double d2, float f, float f1){
        EntityGeneric gen = (EntityGeneric) entityliving;

        if(gen.isDead && gen.deathTime > 20){
            return;
        }
        if((gen.getBossBarVisiblity() == 1 || gen.getBossBarVisiblity() == 2 && gen.isAttacking()) && !gen.isDead && gen.deathTime <= 20 && gen.canSee(Minecraft.getMinecraft().thePlayer))
            BossStatus.setBossStatus(gen, true);

//        if(Boolean.parseBoolean(gen.getValue("loAr")) && !gen.isWalking()){
//            gen.prevRenderYawOffset = gen.renderYawOffset = gen.ai.orientation;
//        } // if something goes wrong with rotation, attempt uncommenting and adding the 'orientation' tomfoolery
        super.doRender(entityliving, d, d1, d2, f, f1);
    }

    protected void renderModel(EntityLivingBase entityliving, float par2, float par3, float par4, float par5, float par6, float par7){
        EntityGeneric generic = (EntityGeneric) entityliving;
        if (generic.model == null) {
            if (generic.isHuman) {
                generic.model = new ModelBipedGeneric(0);
                modelBipedMain = new ModelBipedGeneric(0);
                modelArmorChestplate = new ModelBipedGeneric(1);
                modelArmor = new ModelBipedGeneric(0.5f);
            } else {
                Object model = null;
                try {
                   model  = Class.forName(generic.modelLoc).getConstructor().newInstance();
                } catch (Exception e) {
                    try {
                        model = Class.forName(generic.modelLoc).getConstructor(float.class).newInstance(0f);
                    } catch (Exception e2) {
                        throw new RuntimeException("The specified model " + generic.modelLoc + " is either not a model or does not have a valid constructor!", e2);
                    }
                }
                generic.model = (ModelBase) model;
            }
        }
        this.mainModel = generic.model;
        super.renderModel(entityliving, par2, par3, par4, par5, par6, par7);
        if (generic.hasGlowTexture)
        {
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            if(generic.resourceLocationGlowTexture == null){
                generic.resourceLocationGlowTexture = new ResourceLocation(generic.getValue("gLoc"));
            }
            bindTexture(generic.resourceLocationGlowTexture);
            float f1 = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GL11.glDisable(GL11.GL_LIGHTING);
            if (generic.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPushMatrix();
            GL11.glScalef(1.001f, 1.001f, 1.001f);
            mainModel.render(entityliving, par2, par3, par4, par5, par6, par7);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f1);


            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
//
//    public static String getTexture(RendererLivingEntity render, Entity entity){
//        ResourceLocation location = render.getEntityTexture(entity);
//        return location.toString();
//    }
//
//    public static int shouldRenderPass(EntityLivingBase entity, int par2, float par3, RendererLivingEntity renderEntity) {
//        return renderEntity.shouldRenderPass(entity, par2, par3);
//    }
//
//    public static void preRenderCallback(EntityLivingBase entity, float f,
//                                         RendererLivingEntity renderEntity) {
//        renderEntity.preRenderCallback(entity, f);
//    }
//
//    public static ModelBase getPassModel(RendererLivingEntity render) {
//        return render.renderPassModel;
//    }
//
    @Override
    protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2){
        if (renderEntity != null) return ProtectedRenderHelper.handleRotationFloat(entity, par2, renderEntity);
        return super.handleRotationFloat(par1EntityLivingBase, par2);
    }
//
//    public static void renderEquippedItems(EntityLivingBase entity, float f,
//                                           RendererLivingEntity renderEntity) {
//        renderEntity.renderEquippedItems(entity, f);
//    }

    // HUMAN ONLY PART //

    protected int customShouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack itemstack = par1EntityLiving.func_130225_q(3 - par2);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                this.bindTexture(RenderBiped.getArmorResource(par1EntityLiving, itemstack, par2, null));
                ModelBiped modelbiped = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
                modelbiped.bipedHead.showModel = par2 == 0;
                modelbiped.bipedHeadwear.showModel = par2 == 0;
                modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
                modelbiped.bipedRightArm.showModel = par2 == 1;
                modelbiped.bipedLeftArm.showModel = par2 == 1;
                modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped = ForgeHooksClient.getArmorModel(par1EntityLiving, itemstack, par2, modelbiped);
                this.setRenderPassModel(modelbiped);
                modelbiped.onGround = this.mainModel.onGround;
                modelbiped.isRiding = this.mainModel.isRiding;
                modelbiped.isChild = this.mainModel.isChild;
                float f1 = 1.0F;

                //Move out of if to allow for more then just CLOTH to have color
                int j = itemarmor.getColor(itemstack);
                if (j != -1)
                {
                    float f2 = (float)(j >> 16 & 255) / 255.0F;
                    float f3 = (float)(j >> 8 & 255) / 255.0F;
                    float f4 = (float)(j & 255) / 255.0F;
                    GL11.glColor3f(f1 * f2, f1 * f3, f1 * f4);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(f1, f1, f1);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3){
        if (renderEntity != null) return ProtectedRenderHelper.shouldRenderPass(entity, par2, par3, renderEntity);
        else if (((EntityGeneric)par1EntityLivingBase).isHuman) return this.customShouldRenderPass((EntityLiving)par1EntityLivingBase, par2, par3);
        else return super.shouldRenderPass(par1EntityLivingBase, par2, par3);
    }

    public void renderPlayer(EntityGeneric human, double d, double d1, double d2,
                             float f, float f1)
    {
        if (human.copyRenderPasses) {
            if (renderEntity == null) { // may need to remove this 'if'
                Object entityObject = null;
                try {
                    entityObject = Class.forName(human.getValue("copRPF")).getConstructor(World.class).newInstance(human.worldObj);
                } catch (Exception e) {
                    throw new RuntimeException("Failed fetching entity renderer to copy render passes from! This is an error!", e);
                }
                entity = (EntityLivingBase) entityObject;
                renderEntity = null;
                if (entity != null) {
                    renderEntity = (RendererLivingEntity) RenderManager.instance.getEntityRenderObject(entity);
                }
            }
            doRenderNotPlayer(human, d, d1, d2, f, f1);
            return;
        }
        if (human.isHuman) {
            if (!human.isAlex) {
                this.mainModel = steve;
                this.modelBipedMain = steve;
            } else {
                this.mainModel = alex;
                this.modelBipedMain = alex;
            }
        } else{ // ???
            this.mainModel = modelBipedMain;
        }

        ItemStack itemstack = human.getHeldItem();
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight =
                itemstack == null ? 0 : human.hurtResistantTime > 0 ? 3 : 1;

        modelArmorChestplate.heldItemLeft = modelArmor.heldItemLeft = modelBipedMain.heldItemLeft =
                human.getOffHand() == null ? 0 : human.hurtResistantTime > 0 ? 3 : 1;

        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = human.isSneaking();

        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = human.isAiming();

        modelArmorChestplate.isRiding = modelArmor.isRiding = modelBipedMain.isRiding = human.isRiding();

        double d3 = d1 - (double)human.yOffset;
        if(human.isSneaking())
        {
            d3 -= 0.125D;
        }
//        super.doRender(human, d, d3, d2, f, f1);
        doRenderNotPlayer(human, d, d3, d2, f, f1); // this was the 'super'
        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;
        modelArmorChestplate.heldItemLeft = modelArmor.heldItemLeft = modelBipedMain.heldItemLeft = 0;
    }

    protected void renderSpecials(EntityGeneric gen, float f)
    {
        super.renderEquippedItems(gen, f);
        GL11.glColor3f(1,1,1);
        int i = gen.getBrightnessForRender(f);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        if (gen.hasCloak)
        {
            if(gen.resourceLocationCloakTexture == null){
                gen.resourceLocationCloakTexture = new ResourceLocation(gen.getValue("cLoc"));
            }
            bindTexture(gen.resourceLocationCloakTexture);
            //AbstractClientPlayer.func_110307_b(, null);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double d = (gen.field_20066_r + (gen.field_20063_u - gen.field_20066_r) * (double)f) - (gen.prevPosX + (gen.posX - gen.prevPosX) * (double)f);
            double d1 = (gen.field_20065_s + (gen.field_20062_v - gen.field_20065_s) * (double)f) - (gen.prevPosY + (gen.posY - gen.prevPosY) * (double)f);
            double d2 = (gen.field_20064_t + (gen.field_20061_w - gen.field_20064_t) * (double)f) - (gen.prevPosZ + (gen.posZ - gen.prevPosZ) * (double)f);
            float f11 = gen.prevRenderYawOffset + (gen.renderYawOffset - gen.prevRenderYawOffset) * f;
            double d3 = MathHelper.sin((f11 * 3.141593F) / 180F);
            double d4 = -MathHelper.cos((f11 * 3.141593F) / 180F);
            float f14 = (float)(d * d3 + d2 * d4) * 100F;
            float f15 = (float)(d * d4 - d2 * d3) * 100F;
            if (f14 < 0.0F)
            {
                f14 = 0.0F;
            }
            float f16 = gen.prevRotationYaw + (gen.rotationYaw - gen.prevRotationYaw) * f;
            //f13 += MathHelper.sin((entityplayer.prevDistanceWalkedModified + (entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified) * f) * 6F) * 32F * f16;
            float f13 = 5f;
            if (gen.isSneaking())
            {
                f13 += 25F;
            }
            //System.out.println(entityplayer.prevDistanceWalkedModified);
            GL11.glRotatef(6F + f14 / 2.0F + f13, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f15 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f15 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            modelBipedMain.renderCloak(0.0625F);

            GL11.glPopMatrix();
        }
        GL11.glColor3f(1,1,1);
        ItemStack itemstack = gen.getEquipmentInSlot(0);
        if(itemstack != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

            if (itemstack.getItem() instanceof ItemBlock)
            {
                if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
                {
                    float var6 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, -var6);
                }

                this.renderManager.itemRenderer.renderItem(gen, itemstack, 0);
            }

            GL11.glPopMatrix();
        }
        GL11.glColor3f(1,1,1);
        ItemStack itemstack2 = gen.getHeldItem();
        if(itemstack2 != null)
        {
            float var6;
            GL11.glPushMatrix();
            float y = 0;
            float x = 0;
            this.modelBipedMain.bipedRightArm.postRender(0.0625F);

            if(gen.isAlex){
                GL11.glTranslatef(-0.0125F, 0.4375F + y, 0.0625F);
            }
            else {
                GL11.glTranslatef(-0.0625F, 0.4375F + y, 0.0625F);
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack2, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack2, BLOCK_3D));

            if (itemstack2.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack2.getItem()).getRenderType())))
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var6, -var6, var6);
            }
            else if (itemstack2.getItem() instanceof ItemBow && customRenderer == null)
            {
                var6 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (itemstack2.getItem().isFull3D())
            {
                var6 = 0.625F;

                if (itemstack2.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                if (gen.hurtResistantTime > 0 && gen.fullyMeleeResistant)
                {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            if (itemstack2.getItem().requiresMultipleRenderPasses())
            {
                for (int var25 = 0; var25 < itemstack2.getItem().getRenderPasses(itemstack2.getItemDamage()); ++var25)
                {
                    int var24 = itemstack2.getItem().getColorFromItemStack(itemstack2, var25);
                    float var26 = (float)(var24 >> 16 & 255) / 255.0F;
                    float var9 = (float)(var24 >> 8 & 255) / 255.0F;
                    float var10 = (float)(var24 & 255) / 255.0F;
                    GL11.glColor4f(var26, var9, var10, 1.0F);
                    this.renderManager.itemRenderer.renderItem(gen, itemstack2, var25);
                }
            }
            else
                renderManager.itemRenderer.renderItem(gen, itemstack2, 0);

            GL11.glPopMatrix();
        }
        GL11.glColor4f(1, 1, 1, 1.0F);
        itemstack2 = gen.getOffHand();
        if(itemstack2 != null)
        {
            GL11.glPushMatrix();
            float y = 0;
            float x = 0;
            this.modelBipedMain.bipedLeftArm.postRender(0.0625F);

            if(gen.isAlex){
                GL11.glTranslatef(0.0125F, 0.4375F + y, 0.0625F);
            }
            else {
                GL11.glTranslatef(0.0625F, 0.4375F + y, 0.0625F);
            }

            float var6;

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack2, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack2, BLOCK_3D));

            Class<?> clazz = itemstack2.getItem().getClass();
            if(clazz.getSimpleName().equals("ItemShield") || clazz.getSimpleName().equals("ItemRotatedShield") || clazz.getSimpleName().equals("ItemClaw"))
                GL11.glTranslatef(0.30f, 0, 0f);

            if (itemstack2.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack2.getItem()).getRenderType())))
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
            }
            else if (itemstack2.getItem() instanceof ItemBow && customRenderer == null)
            {
                var6 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (itemstack2.getItem().isFull3D())
            {
                var6 = 0.625F;

                if (itemstack2.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                if (gen.hurtResistantTime > 0 && gen.fullyRangedResistant)
                {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            if (itemstack2.getItem().requiresMultipleRenderPasses())
            {
                for (int var25 = 0; var25 < itemstack2.getItem().getRenderPasses(itemstack2.getItemDamage()); ++var25)
                {
                    int var24 = itemstack2.getItem().getColorFromItemStack(itemstack2, var25);
                    float var26 = (float)(var24 >> 16 & 255) / 255.0F;
                    float var9 = (float)(var24 >> 8 & 255) / 255.0F;
                    float var10 = (float)(var24 & 255) / 255.0F;
                    GL11.glColor4f(var26, var9, var10, 1.0F);
                    this.renderManager.itemRenderer.renderItem(gen, itemstack2, var25);
                }
            }else
            {
                renderManager.itemRenderer.renderItem(gen, itemstack2, 0);
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase entityliving, float f){
        if (renderEntity != null) ProtectedRenderHelper.renderEquippedItems(entity, f, renderEntity);
        else if (((EntityGeneric)entityliving).isHuman) renderSpecials((EntityGeneric) entityliving, f);
        else super.renderEquippedItems(entityliving, f);
    }

    @Override
    public void doRender(EntityLiving entityliving, double d, double d1, double d2, float f, float f1){
        if (((EntityGeneric)entityliving).isHuman) renderPlayer((EntityGeneric)entityliving, d, d1, d2, f, f1);
        else doRenderNotPlayer(entityliving, d, d1, d2, f, f1);
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1){
        if (((EntityGeneric)entity).isHuman) renderPlayer((EntityGeneric)entity, d, d1, d2, f, f1);
        else doRenderNotPlayer((EntityLiving) entity, d, d1, d2, f, f1);
    }


}
