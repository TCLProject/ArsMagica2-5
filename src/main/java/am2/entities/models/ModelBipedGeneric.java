package am2.entities.models;

import am2.entities.EntityGeneric;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelBipedGeneric extends ModelBiped {

    public ModelBipedGeneric(float f) {
        init(f, 0.0F);
    }

    public ModelBipedGeneric(float f, boolean arms) {
        super(f, 0.0F, 64, 64);
        init(f, 0.0F, arms);
    }

    public void init(float f, float f1, boolean arms) {

        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        aimedBow = false;

        bipedCloak = new ModelRenderer(this, 0, 0);
        bipedCloak.setTextureSize(64, 32);
        bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);

        bipedEars = new ModelRenderer(this, 24, 0);
        bipedEars.setTextureSize(64, 32); // NEW LINE
        initCommonBodyparts(f, f1);

        if (arms) { // alex
            bipedRightArm = (new ModelScaleRenderer(this, 40, 16));
            bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
            bipedRightArm.setRotationPoint(-5.0F, 2.5F + f1, 0.0F);

            bipedLeftArm = new ModelScaleRenderer(this, 32, 48);
            bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
            bipedLeftArm.setRotationPoint(5.0F, 2.5F + f1, 0.0F);
        } else { // steve
            bipedRightArm = new ModelRenderer(this, 40, 16);
            bipedRightArm.addBox(-3F, -2F, -2F, 4, 12, 4, f);
            bipedRightArm.setRotationPoint(-5F, 2.0F + f1, 0.0F);

            bipedLeftArm = new ModelRenderer(this, 32, 48);
            bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
            bipedLeftArm.setRotationPoint(5.0F, 2.0F + f1, 0.0F);
        }

        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
        bipedRightLeg.setRotationPoint(-2F, 12F + f1, 0.0F);

        bipedLeftLeg = new ModelRenderer(this, 16, 48);
        bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F + f1, 0.0F);

    }

    public void initCommonBodyparts(float f, float f1) {
        bipedEars.addBox(-3F, -6F, -1F, 6, 6, 1, f);

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-4F, -8F, -4F, 8, 8, 8, f);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

        bipedHeadwear = new ModelRenderer(this, 32, 0);
        bipedHeadwear.addBox(-4F, -8F, -4F, 8, 8, 8, f + 0.5F);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

        bipedBody = new ModelRenderer(this, 16, 16);
        bipedBody.addBox(-4F, 0.0F, -2F, 8, 12, 4, f);
        bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    public void init(float f, float f1) {
        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        aimedBow = false;

        bipedCloak = new ModelRenderer(this, 0, 0);
        bipedCloak.textureHeight = 32;
        bipedCloak.addBox(-5F, 0.0F, -1F, 10, 16, 1, f);

        bipedEars = new ModelRenderer(this, 24, 0);
        initCommonBodyparts(f, f1);
        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightArm.addBox(-3F, -2F, -2F, 4, 12, 4, f);
        bipedRightArm.setRotationPoint(-5F, 2.0F + f1, 0.0F);
        bipedLeftArm = new ModelRenderer(this, 40, 16);
        bipedLeftArm.mirror = true;
        bipedLeftArm.addBox(-1F, -2F, -2F, 4, 12, 4, f);
        bipedLeftArm.setRotationPoint(5F, 2.0F + f1, 0.0F);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
        bipedRightLeg.setRotationPoint(-2F, 12F + f1, 0.0F);
        bipedLeftLeg = new ModelRenderer(this, 0, 16);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
        bipedLeftLeg.setRotationPoint(2.0F, 12F + f1, 0.0F);

    }


    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

        bipedHead.render(par7);
        bipedBody.render(par7);
        bipedRightArm.render(par7);
        bipedLeftArm.render(par7);
        bipedRightLeg.render(par7);
        bipedLeftLeg.render(par7);
        bipedHeadwear.render(par7);
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        EntityGeneric npc = (EntityGeneric) entity;
        isRiding = npc.isRiding();

        bipedHead.rotateAngleY = par4 / (180F / (float) Math.PI);
        bipedHead.rotateAngleX = par5 / (180F / (float) Math.PI);
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 2.0F * par2 * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;

        if (isRiding) {
            bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedRightLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
            bipedLeftLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
            bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
            bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
        }

        if (heldItemLeft != 0) {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F) * (float) heldItemLeft;
        }

        if (heldItemRight != 0) {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F) * (float) heldItemRight;
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;

        if (onGround > -9990F) {
            float f = onGround;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f) * (float) Math.PI * 2.0F) * 0.2F;
            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5F;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5F;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5F;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5F;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f = 1.0F - onGround;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f2 = MathHelper.sin(f * (float) Math.PI);
            float f4 = MathHelper.sin(onGround * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            bipedRightArm.rotateAngleX -= (double) f2 * 1.2D + (double) f4;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            bipedRightArm.rotateAngleZ = MathHelper.sin(onGround * (float) Math.PI) * -0.4F;
        }

        if (isSneak) {
            bipedBody.rotateAngleX = 0.5F;
            bipedRightLeg.rotateAngleX -= 0.0F;
            bipedLeftLeg.rotateAngleX -= 0.0F;
            bipedRightArm.rotateAngleX += 0.4F;
            bipedLeftArm.rotateAngleX += 0.4F;
            bipedRightLeg.rotationPointZ = 4F;
            bipedLeftLeg.rotationPointZ = 4F;
            bipedRightLeg.rotationPointY = 9F;
            bipedLeftLeg.rotationPointY = 9F;
            bipedHead.rotationPointY = 1.0F;
        } else {
            bipedBody.rotateAngleX = 0.0F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;
            bipedRightLeg.rotationPointY = 12F;
            bipedLeftLeg.rotationPointY = 12F;
            bipedHead.rotationPointY = 0.0F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;

        if (aimedBow) {
            float f1 = 0.0F;
            float f3 = 0.0F;
            bipedRightArm.rotateAngleZ = 0.0F;
            bipedLeftArm.rotateAngleZ = 0.0F;
            bipedRightArm.rotateAngleY = -(0.1F - f1 * 0.6F) + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = (0.1F - f1 * 0.6F) + bipedHead.rotateAngleY + 0.4F;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedRightArm.rotateAngleX -= f1 * 1.2F - f3 * 0.4F;
            bipedLeftArm.rotateAngleX -= f1 * 1.2F - f3 * 0.4F;
            bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
        }
    }

    public void renderEars(float f) {
        bipedEars.rotateAngleY = bipedHead.rotateAngleY;
        bipedEars.rotateAngleX = bipedHead.rotateAngleX;
        bipedEars.rotationPointX = 0.0F;
        bipedEars.rotationPointY = 0.0F;
        bipedEars.render(f);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderCloak(float f) {
        bipedCloak.render(f);
    }

    // thank you CNPCs+
    public class ModelScaleRenderer extends ModelRenderer {

        public boolean compiled;

        public int displayList;

        public float x, y, z;

        public ModelScaleRenderer(ModelBase par1ModelBase) {
            super(par1ModelBase);
        }
        public ModelScaleRenderer(ModelBase par1ModelBase, int par2, int par3)
        {
            this(par1ModelBase);
            this.setTextureOffset(par2, par3);
        }

        public void setConfig(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setRotation(ModelRenderer model, float x, float y, float z) {
            model.rotateAngleX = x;
            model.rotateAngleY = y;
            model.rotateAngleZ = z;
        }

        public void render(float par1)
        {
            if(!showModel || isHidden)
                return;
            if(!compiled)
                compileDisplayList(par1);
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, z);
            this.postRender(par1);
            GL11.glCallList(this.displayList);
            if (this.childModels != null)
            {
                for (int i = 0; i < this.childModels.size(); ++i)
                {
                    ((ModelRenderer)this.childModels.get(i)).render(par1);
                }
            }
            GL11.glPopMatrix();
        }

        public void compileDisplayList(float par1)
        {
            this.displayList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.displayList, GL11.GL_COMPILE);
            Tessellator tessellator = Tessellator.instance;

            for (int i = 0; i < this.cubeList.size(); ++i)
            {
                ((ModelBox)this.cubeList.get(i)).render(tessellator, par1);
            }

            GL11.glEndList();
            this.compiled = true;
        }

    }
}
