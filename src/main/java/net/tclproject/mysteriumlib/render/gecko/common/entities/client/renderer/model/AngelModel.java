package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;
import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.AngelEntity;

public class AngelModel extends AnimatedEntityModel<AngelEntity> {
    private final AnimatedModelRenderer body;
    private final AnimatedModelRenderer WingRight;
    private final AnimatedModelRenderer StartWing;
    private final AnimatedModelRenderer StartBoneWing;
    private final AnimatedModelRenderer featherDeco;
    private final AnimatedModelRenderer BoneWingRight;
    private final AnimatedModelRenderer feather2;
    private final AnimatedModelRenderer feather12;
    private final AnimatedModelRenderer feather3;
    private final AnimatedModelRenderer feather4;
    private final AnimatedModelRenderer feather5;
    private final AnimatedModelRenderer feather15;
    private final AnimatedModelRenderer cube_r1;
    private final AnimatedModelRenderer featheralot;
    private final AnimatedModelRenderer featherDeco2;
    private final AnimatedModelRenderer BoneWingEnd3Right;
    private final AnimatedModelRenderer BoneWingEndRight;
    private final AnimatedModelRenderer BoneWingEnd2;
    private final AnimatedModelRenderer featherDeco3;
    private final AnimatedModelRenderer feather6;
    private final AnimatedModelRenderer cube_r2;
    private final AnimatedModelRenderer feather13;
    private final AnimatedModelRenderer cube_r3;
    private final AnimatedModelRenderer feather7;
    private final AnimatedModelRenderer cube_r4;
    private final AnimatedModelRenderer feather8;
    private final AnimatedModelRenderer cube_r5;
    private final AnimatedModelRenderer feather11;
    private final AnimatedModelRenderer feather9;
    private final AnimatedModelRenderer cube_r6;
    private final AnimatedModelRenderer feather10;
    private final AnimatedModelRenderer cube_r7;
    private final AnimatedModelRenderer feather14;
    private final AnimatedModelRenderer WingOBlEnd;
    private final AnimatedModelRenderer WingLeft;
    private final AnimatedModelRenderer StartWing2;
    private final AnimatedModelRenderer StartBoneWing2;
    private final AnimatedModelRenderer featherDeco4;
    private final AnimatedModelRenderer BoneWingLeft;
    private final AnimatedModelRenderer feather16;
    private final AnimatedModelRenderer feather17;
    private final AnimatedModelRenderer feather18;
    private final AnimatedModelRenderer feather19;
    private final AnimatedModelRenderer feather20;
    private final AnimatedModelRenderer feather21;
    private final AnimatedModelRenderer cube_r8;
    private final AnimatedModelRenderer featheralot2;
    private final AnimatedModelRenderer featherDeco5;
    private final AnimatedModelRenderer BoneWingEndLeft;
    private final AnimatedModelRenderer BoneWingEndLeft2;
    private final AnimatedModelRenderer BoneWingEnd6;
    private final AnimatedModelRenderer featherDeco6;
    private final AnimatedModelRenderer feather22;
    private final AnimatedModelRenderer cube_r9;
    private final AnimatedModelRenderer feather23;
    private final AnimatedModelRenderer cube_r10;
    private final AnimatedModelRenderer feather24;
    private final AnimatedModelRenderer cube_r11;
    private final AnimatedModelRenderer feather25;
    private final AnimatedModelRenderer cube_r12;
    private final AnimatedModelRenderer feather26;
    private final AnimatedModelRenderer feather27;
    private final AnimatedModelRenderer cube_r13;
    private final AnimatedModelRenderer feather28;
    private final AnimatedModelRenderer cube_r14;
    private final AnimatedModelRenderer feather29;
    private final AnimatedModelRenderer WingOBlEnd2;

    public AngelModel() {
        textureWidth = 64;
        textureHeight = 64;

        body = new AnimatedModelRenderer(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.setModelRendererName("body");
        this.registerModelRenderer(body);

        WingRight = new AnimatedModelRenderer(this);
        WingRight.setRotationPoint(-2.6F, 2.8F, 2.0F);
        WingRight.setModelRendererName("WingRight");
        body.addChild(WingRight);
        this.registerModelRenderer(WingRight);

        StartWing = new AnimatedModelRenderer(this);
        StartWing.setRotationPoint(0.0F, 0.0F, 0.0F);
        StartWing.setModelRendererName("StartWing");
        WingRight.addChild(StartWing);
        setRotationAngle(StartWing, -0.6981F, 0.0F, 0.0F);
        this.registerModelRenderer(StartWing);

        StartBoneWing = new AnimatedModelRenderer(this);
        StartBoneWing.setRotationPoint(2.0F, -4.0F, -2.0F);
        StartBoneWing.setModelRendererName("StartBoneWing");
        StartWing.addChild(StartBoneWing);
        StartBoneWing.cubeList.add(new ModelBox(StartBoneWing, 35, 50, -3.0F, 3.0F, 1.0F, 2, 3, 9, 0.0F));
        this.registerModelRenderer(StartBoneWing);

        featherDeco = new AnimatedModelRenderer(this);
        featherDeco.setRotationPoint(-3.1F, 4.0546F, 0.2015F);
        featherDeco.setModelRendererName("featherDeco");
        StartBoneWing.addChild(featherDeco);
        setRotationAngle(featherDeco, 0.6981F, 0.0F, -0.0349F);
        featherDeco.cubeList.add(new ModelBox(featherDeco, 18, 50, 1.0F, -1.4226F, -0.037F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco);

        BoneWingRight = new AnimatedModelRenderer(this);
        BoneWingRight.setRotationPoint(-1.8437F, 3.6371F, 7.8804F);
        BoneWingRight.setModelRendererName("BoneWingRight");
        StartBoneWing.addChild(BoneWingRight);
        setRotationAngle(BoneWingRight, 1.4835F, 0.0F, 0.0F);
        BoneWingRight.cubeList.add(new ModelBox(BoneWingRight, 0, 0, -1.0F, -0.9324F, -0.7728F, 2, 3, 12, 0.0F));
        this.registerModelRenderer(BoneWingRight);

        feather2 = new AnimatedModelRenderer(this);
        feather2.setRotationPoint(-1.4F, -0.2437F, -0.3786F);
        feather2.setModelRendererName("feather2");
        BoneWingRight.addChild(feather2);
        setRotationAngle(feather2, -2.4435F, 0.0F, 0.1396F);
        feather2.cubeList.add(new ModelBox(feather2, 3, 31, 1.0F, -1.4226F, -0.037F, 0, 5, 9, 0.0F));
        this.registerModelRenderer(feather2);

        feather12 = new AnimatedModelRenderer(this);
        feather12.setRotationPoint(-1.4F, -0.2437F, -0.3786F);
        feather12.setModelRendererName("feather12");
        BoneWingRight.addChild(feather12);
        setRotationAngle(feather12, -2.7227F, 0.0F, 0.1396F);
        feather12.cubeList.add(new ModelBox(feather12, 35, 35, 0.4882F, -0.567F, -0.2195F, 0, 7, 6, 0.0F));
        this.registerModelRenderer(feather12);

        feather3 = new AnimatedModelRenderer(this);
        feather3.setRotationPoint(-0.9F, 0.0252F, 0.5603F);
        feather3.setModelRendererName("feather3");
        BoneWingRight.addChild(feather3);
        setRotationAngle(feather3, -2.0944F, 0.0F, 0.1396F);
        feather3.cubeList.add(new ModelBox(feather3, 1, 24, 1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather3);

        feather4 = new AnimatedModelRenderer(this);
        feather4.setRotationPoint(-0.9F, 1.4394F, 2.2456F);
        feather4.setModelRendererName("feather4");
        BoneWingRight.addChild(feather4);
        setRotationAngle(feather4, -1.7453F, 0.0F, 0.1396F);
        feather4.cubeList.add(new ModelBox(feather4, 1, 24, 1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather4);

        feather5 = new AnimatedModelRenderer(this);
        feather5.setRotationPoint(-0.8F, 1.4394F, 2.2456F);
        feather5.setModelRendererName("feather5");
        BoneWingRight.addChild(feather5);
        setRotationAngle(feather5, -1.1345F, 0.0F, 0.1745F);
        feather5.cubeList.add(new ModelBox(feather5, 1, 24, 1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather5);

        feather15 = new AnimatedModelRenderer(this);
        feather15.setRotationPoint(-0.3096F, 2.3332F, 5.8248F);
        feather15.setModelRendererName("feather15");
        BoneWingRight.addChild(feather15);
        setRotationAngle(feather15, -1.1345F, 0.0F, 0.1745F);
        this.registerModelRenderer(feather15);

        cube_r1 = new AnimatedModelRenderer(this);
        cube_r1.setRotationPoint(2.0F, -4.0F, -2.0F);
        cube_r1.setModelRendererName("cube_r1");
        feather15.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.1047F, 0.0F);
        cube_r1.cubeList.add(new ModelBox(cube_r1, 1, 24, -2.044F, 1.8831F, 1.5915F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(cube_r1);

        featheralot = new AnimatedModelRenderer(this);
        featheralot.setRotationPoint(-0.6F, -0.1834F, 8.0192F);
        featheralot.setModelRendererName("featheralot");
        BoneWingRight.addChild(featheralot);
        setRotationAngle(featheralot, -1.5708F, 0.0F, 0.0349F);
        featheralot.cubeList.add(new ModelBox(featheralot, 46, 3, 0.1049F, -4.4566F, -0.0259F, 1, 14, 8, 0.0F));
        this.registerModelRenderer(featheralot);

        featherDeco2 = new AnimatedModelRenderer(this);
        featherDeco2.setRotationPoint(-1.4552F, -0.1491F, 7.2054F);
        featherDeco2.setModelRendererName("featherDeco2");
        BoneWingRight.addChild(featherDeco2);
        setRotationAngle(featherDeco2, 0.5236F, -0.0873F, -0.0349F);
        featherDeco2.cubeList.add(new ModelBox(featherDeco2, 18, 39, 1.8385F, -1.6573F, -0.2648F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco2);

        BoneWingEnd3Right = new AnimatedModelRenderer(this);
        BoneWingEnd3Right.setRotationPoint(0.1355F, -0.8877F, 11.2347F);
        BoneWingEnd3Right.setModelRendererName("BoneWingEnd3Right");
        BoneWingRight.addChild(BoneWingEnd3Right);
        setRotationAngle(BoneWingEnd3Right, -0.7679F, 0.0F, 0.0F);
        BoneWingEnd3Right.cubeList.add(new ModelBox(BoneWingEnd3Right, 30, 0, -1.1355F, -0.027F, -0.0364F, 2, 3, 6, 0.0F));
        this.registerModelRenderer(BoneWingEnd3Right);

        BoneWingEndRight = new AnimatedModelRenderer(this);
        BoneWingEndRight.setRotationPoint(-0.1898F, 0.2569F, 5.9025F);
        BoneWingEndRight.setModelRendererName("BoneWingEndRight");
        BoneWingEnd3Right.addChild(BoneWingEndRight);
        setRotationAngle(BoneWingEndRight, -0.5061F, 0.0F, 0.0F);
        BoneWingEndRight.cubeList.add(new ModelBox(BoneWingEndRight, 40, 25, -0.9457F, -0.2335F, -0.0466F, 2, 3, 10, 0.0F));
        this.registerModelRenderer(BoneWingEndRight);

        BoneWingEnd2 = new AnimatedModelRenderer(this);
        BoneWingEnd2.setRotationPoint(-0.3052F, -0.0218F, 9.5547F);
        BoneWingEnd2.setModelRendererName("BoneWingEnd2");
        BoneWingEndRight.addChild(BoneWingEnd2);
        setRotationAngle(BoneWingEnd2, -0.2618F, 0.0F, 0.0F);
        BoneWingEnd2.cubeList.add(new ModelBox(BoneWingEnd2, 4, 5, -0.1524F, 0.1003F, 0.3694F, 1, 2, 8, 0.0F));
        this.registerModelRenderer(BoneWingEnd2);

        featherDeco3 = new AnimatedModelRenderer(this);
        featherDeco3.setRotationPoint(-0.8616F, 0.4073F, 0.2455F);
        featherDeco3.setModelRendererName("featherDeco3");
        BoneWingEndRight.addChild(featherDeco3);
        setRotationAngle(featherDeco3, 1.0472F, 0.0F, -0.2094F);
        featherDeco3.cubeList.add(new ModelBox(featherDeco3, 18, 44, 0.8499F, -3.294F, -5.5094F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco3);

        feather6 = new AnimatedModelRenderer(this);
        feather6.setRotationPoint(-0.6616F, -0.121F, 3.8959F);
        feather6.setModelRendererName("feather6");
        BoneWingEndRight.addChild(feather6);
        setRotationAngle(feather6, -0.6283F, 0.0F, 0.0F);
        this.registerModelRenderer(feather6);


        cube_r2 = new AnimatedModelRenderer(this);
        cube_r2.setRotationPoint(1.8645F, -3.1125F, -0.9382F);
        cube_r2.setModelRendererName("cube_r2");
        feather6.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.0698F);
        cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -1.2507F, 2.8016F, 0.8622F, 0, 5, 20, 0.0F));
        this.registerModelRenderer(cube_r2);

        feather13 = new AnimatedModelRenderer(this);
        feather13.setRotationPoint(-0.2396F, 0.7502F, 9.0488F);
        feather13.setModelRendererName("feather13");
        BoneWingEndRight.addChild(feather13);
        setRotationAngle(feather13, -0.2793F, 0.0F, 0.0F);
        this.registerModelRenderer(feather13);

        cube_r3 = new AnimatedModelRenderer(this);
        cube_r3.setRotationPoint(1.8645F, -2.8029F, -1.3058F);
        cube_r3.setModelRendererName("cube_r3");
        feather13.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 0.0349F);
        cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -1.3738F, 2.4885F, 0.1106F, 0, 5, 20, 0.0F));
        this.registerModelRenderer(cube_r3);

        feather7 = new AnimatedModelRenderer(this);
        feather7.setRotationPoint(-0.9616F, 2.2392F, 0.8295F);
        feather7.setModelRendererName("feather7");
        BoneWingEndRight.addChild(feather7);
        setRotationAngle(feather7, -0.4363F, 0.0F, 0.0873F);
        this.registerModelRenderer(feather7);

        cube_r4 = new AnimatedModelRenderer(this);
        cube_r4.setRotationPoint(1.982F, -2.9201F, -1.1242F);
        cube_r4.setModelRendererName("cube_r4");
        feather7.addChild(cube_r4);
        setRotationAngle(cube_r4, -0.2793F, 0.0175F, 0.0F);
        cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 7, -1.0F, 2.5774F, 1.963F, 0, 5, 18, 0.0F));
        this.registerModelRenderer(cube_r4);

        feather8 = new AnimatedModelRenderer(this);
        feather8.setRotationPoint(-1.1616F, 4.9287F, 0.5286F);
        feather8.setModelRendererName("feather8");
        BoneWingEndRight.addChild(feather8);
        setRotationAngle(feather8, -0.6807F, 0.0F, 0.0F);
        this.registerModelRenderer(feather8);

        cube_r5 = new AnimatedModelRenderer(this);
        cube_r5.setRotationPoint(1.8645F, -3.1693F, -0.8932F);
        cube_r5.setModelRendererName("cube_r5");
        feather8.addChild(cube_r5);
        setRotationAngle(cube_r5, -0.2967F, 0.0F, 0.0F);
        cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 15, -1.1F, 2.5774F, 1.963F, 0, 5, 15, 0.0F));
        this.registerModelRenderer(cube_r5);

        feather11 = new AnimatedModelRenderer(this);
        feather11.setRotationPoint(-1.081F, 4.4876F, -2.0324F);
        feather11.setModelRendererName("feather11");
        BoneWingEndRight.addChild(feather11);
        setRotationAngle(feather11, -1.3614F, 0.0F, 0.0F);
        feather11.cubeList.add(new ModelBox(feather11, 0, 15, 0.7645F, -1.4736F, 1.3459F, 0, 5, 15, 0.0F));
        this.registerModelRenderer(feather11);

        feather9 = new AnimatedModelRenderer(this);
        feather9.setRotationPoint(-1.4616F, 3.8662F, 0.8133F);
        feather9.setModelRendererName("feather9");
        BoneWingEndRight.addChild(feather9);
        setRotationAngle(feather9, -0.4189F, 0.0F, 0.0F);
        this.registerModelRenderer(feather9);

        cube_r6 = new AnimatedModelRenderer(this);
        cube_r6.setRotationPoint(1.8645F, -2.9111F, -1.146F);
        cube_r6.setModelRendererName("cube_r6");
        feather9.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, -0.0349F, 0.0F);
        cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 23, -0.9048F, 2.6065F, -1.0467F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(cube_r6);

        feather10 = new AnimatedModelRenderer(this);
        feather10.setRotationPoint(-1.4616F, 1.065F, 1.5639F);
        feather10.setModelRendererName("feather10");
        BoneWingEndRight.addChild(feather10);
        setRotationAngle(feather10, -0.0698F, 0.0F, 0.0F);
        this.registerModelRenderer(feather10);

        cube_r7 = new AnimatedModelRenderer(this);
        cube_r7.setRotationPoint(1.8645F, -2.6847F, -1.5699F);
        cube_r7.setModelRendererName("cube_r7");
        feather10.addChild(cube_r7);
        setRotationAngle(cube_r7, 0.0F, -0.0349F, 0.0F);
        cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 23, -1.0006F, 2.5774F, -0.0403F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(cube_r7);

        feather14 = new AnimatedModelRenderer(this);
        feather14.setRotationPoint(-0.7935F, 1.2329F, 1.44F);
        feather14.setModelRendererName("feather14");
        BoneWingEndRight.addChild(feather14);
        setRotationAngle(feather14, 0.0524F, 0.0698F, 0.0873F);
        feather14.cubeList.add(new ModelBox(feather14, 0, 23, 0.9585F, -0.0582F, -1.7728F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(feather14);

        WingOBlEnd = new AnimatedModelRenderer(this);
        WingOBlEnd.setRotationPoint(0.0384F, 0.1648F, -0.1876F);
        WingOBlEnd.setModelRendererName("WingOBlEnd");
        BoneWingEndRight.addChild(WingOBlEnd);
        this.registerModelRenderer(WingOBlEnd);

        WingLeft = new AnimatedModelRenderer(this);
        WingLeft.setRotationPoint(2.6F, 2.8F, 2.0F);
        WingLeft.setModelRendererName("WingLeft");
        body.addChild(WingLeft);
        this.registerModelRenderer(WingLeft);

        // Здесь мы заканчиваем, ага...
        StartWing2 = new AnimatedModelRenderer(this);
        StartWing2.setRotationPoint(0.0F, 0.0F, 0.0F);
        StartWing2.setModelRendererName("StartWing2");
        WingLeft.addChild(StartWing2);
        setRotationAngle(StartWing2, -0.6981F, 0.0F, 0.0F);
        this.registerModelRenderer(StartWing2);

        // Left
        StartBoneWing2 = new AnimatedModelRenderer(this);
        StartBoneWing2.setRotationPoint(-2.0F, -4.0F, -2.0F);
        StartBoneWing2.setModelRendererName("StartBoneWing2");
        StartWing2.addChild(StartBoneWing2);
        StartBoneWing2.mirror = true;
        StartBoneWing2.cubeList.add(new ModelBox(StartBoneWing2, 35, 50, 1.0F, 3.0F, 1.0F, 2, 3, 9, 0.0F));
        this.registerModelRenderer(StartBoneWing2);

        featherDeco4 = new AnimatedModelRenderer(this);
        featherDeco4.setRotationPoint(3.1F, 4.0546F, 0.2015F);
        featherDeco4.setModelRendererName("featherDeco4");
        StartBoneWing2.addChild(featherDeco4);
        setRotationAngle(featherDeco4, 0.6981F, 0.0F, 0.0349F);
        featherDeco4.mirror = true;
        featherDeco4.cubeList.add(new ModelBox(featherDeco4, 18, 50, -1.0F, -1.4226F, -0.037F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco4);

        BoneWingLeft = new AnimatedModelRenderer(this);
        BoneWingLeft.setRotationPoint(1.8437F, 3.6371F, 7.8804F);
        BoneWingLeft.setModelRendererName("BoneWingLeft");
        StartBoneWing2.addChild(BoneWingLeft);
        setRotationAngle(BoneWingLeft, 1.4835F, 0.0F, 0.0F);
        BoneWingLeft.mirror = true;
        BoneWingLeft.cubeList.add(new ModelBox(BoneWingLeft, 0, 0, -1.0F, -0.9324F, -0.7728F, 2, 3, 12, 0.0F));
        this.registerModelRenderer(BoneWingLeft);

        feather16 = new AnimatedModelRenderer(this);
        feather16.setRotationPoint(1.4F, -0.2437F, -0.3786F);
        feather16.setModelRendererName("feather16");
        BoneWingLeft.addChild(feather16);
        setRotationAngle(feather16, -2.4435F, 0.0F, -0.1396F);
        feather16.mirror = true;
        feather16.cubeList.add(new ModelBox(feather16, 3, 31, -1.0F, -1.4226F, -0.037F, 0, 5, 9, 0.0F));
        this.registerModelRenderer(feather16);

        feather17 = new AnimatedModelRenderer(this);
        feather17.setRotationPoint(1.4F, -0.2437F, -0.3786F);
        feather17.setModelRendererName("feather17");
        BoneWingLeft.addChild(feather17);
        setRotationAngle(feather17, -2.7227F, 0.0F, -0.1396F);
        feather17.mirror = true;
        feather17.cubeList.add(new ModelBox(feather17, 35, 35, -0.4882F, -0.567F, -0.2195F, 0, 7, 6, 0.0F));
        this.registerModelRenderer(feather17);

        feather18 = new AnimatedModelRenderer(this);
        feather18.setRotationPoint(0.9F, 0.0252F, 0.5603F);
        feather18.setModelRendererName("feather18");
        BoneWingLeft.addChild(feather18);
        setRotationAngle(feather18, -2.0944F, 0.0F, -0.1396F);
        feather18.mirror = true;
        feather18.cubeList.add(new ModelBox(feather18, 1, 24, -1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather18);

        feather19 = new AnimatedModelRenderer(this);
        feather19.setRotationPoint(0.9F, 1.4394F, 2.2456F);
        feather19.setModelRendererName("feather19");
        BoneWingLeft.addChild(feather19);
        setRotationAngle(feather19, -1.7453F, 0.0F, -0.1396F);
        feather19.mirror = true;
        feather19.cubeList.add(new ModelBox(feather19, 1, 24, -1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather19);

        feather20 = new AnimatedModelRenderer(this);
        feather20.setRotationPoint(0.8F, 1.4394F, 2.2456F);
        feather20.setModelRendererName("feather20");
        BoneWingLeft.addChild(feather20);
        setRotationAngle(feather20, -1.1345F, 0.0F, -0.1745F);
        feather20.mirror = true;
        feather20.cubeList.add(new ModelBox(feather20, 1, 24, -1.0F, -1.4226F, -0.037F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(feather20);

        feather21 = new AnimatedModelRenderer(this);
        feather21.setRotationPoint(0.3096F, 2.3332F, 5.8248F);
        feather21.setModelRendererName("feather21");
        BoneWingLeft.addChild(feather21);
        setRotationAngle(feather21, -1.1345F, 0.0F, -0.1745F);
        this.registerModelRenderer(feather21);

        cube_r8 = new AnimatedModelRenderer(this);
        cube_r8.setRotationPoint(-2.0F, -4.0F, -2.0F);
        cube_r8.setModelRendererName("cube_r8");
        feather21.addChild(cube_r8);
        setRotationAngle(cube_r8, 0.0F, -0.1047F, 0.0F);
        cube_r8.mirror = true;
        cube_r8.cubeList.add(new ModelBox(cube_r8, 1, 24, 2.044F, 1.8831F, 1.5915F, 0, 5, 11, 0.0F));
        this.registerModelRenderer(cube_r8);

        featheralot2 = new AnimatedModelRenderer(this);
        featheralot2.setRotationPoint(0.6F, -0.1834F, 8.0192F);
        featheralot2.setModelRendererName("featheralot2");
        BoneWingLeft.addChild(featheralot2);
        setRotationAngle(featheralot2, -1.5708F, 0.0F, -0.0349F);
        featheralot2.mirror = true;
        featheralot2.cubeList.add(new ModelBox(featheralot2, 46, 3, -1.1049F, -4.4566F, -0.0259F, 1, 14, 8, 0.0F));
        this.registerModelRenderer(featheralot2);

        featherDeco5 = new AnimatedModelRenderer(this);
        featherDeco5.setRotationPoint(1.4552F, -0.1491F, 7.2054F);
        featherDeco5.setModelRendererName("featherDeco5");
        BoneWingLeft.addChild(featherDeco5);
        setRotationAngle(featherDeco5, 0.5236F, 0.0873F, 0.0349F);
        featherDeco5.mirror = true;
        featherDeco5.cubeList.add(new ModelBox(featherDeco5, 18, 39, -1.8385F, -1.6573F, -0.2648F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco5);

        BoneWingEndLeft = new AnimatedModelRenderer(this);
        BoneWingEndLeft.setRotationPoint(-0.1355F, -0.8877F, 11.2347F);
        BoneWingEndLeft.setModelRendererName("BoneWingEndLeft");
        BoneWingLeft.addChild(BoneWingEndLeft);
        setRotationAngle(BoneWingEndLeft, -0.7679F, 0.0F, 0.0F);
        BoneWingEndLeft.mirror = true;
        BoneWingEndLeft.cubeList.add(new ModelBox(BoneWingEndLeft, 30, 0, -0.8645F, -0.027F, -0.0364F, 2, 3, 6, 0.0F));
        this.registerModelRenderer(BoneWingEndLeft);

        BoneWingEndLeft2 = new AnimatedModelRenderer(this);
        BoneWingEndLeft2.setRotationPoint(0.1898F, 0.2569F, 5.9025F);
        BoneWingEndLeft2.setModelRendererName("BoneWingEndLeft2");
        BoneWingEndLeft.addChild(BoneWingEndLeft2);
        setRotationAngle(BoneWingEndLeft2, -0.5061F, 0.0F, 0.0F);
        BoneWingEndLeft2.mirror = true;
        BoneWingEndLeft2.cubeList.add(new ModelBox(BoneWingEndLeft2, 40, 25, -1.0543F, -0.2335F, -0.0466F, 2, 3, 10, 0.0F));
        this.registerModelRenderer(BoneWingEndLeft2);

        BoneWingEnd6 = new AnimatedModelRenderer(this);
        BoneWingEnd6.setRotationPoint(0.3052F, -0.0218F, 9.5547F);
        BoneWingEnd6.setModelRendererName("BoneWingEnd6");
        BoneWingEndLeft2.addChild(BoneWingEnd6);
        setRotationAngle(BoneWingEnd6, -0.2618F, 0.0F, 0.0F);
        BoneWingEnd6.mirror = true;
        BoneWingEnd6.cubeList.add(new ModelBox(BoneWingEnd6, 4, 5, -0.8476F, 0.1003F, 0.3694F, 1, 2, 8, 0.0F));
        this.registerModelRenderer(BoneWingEnd6);

        featherDeco6 = new AnimatedModelRenderer(this);
        featherDeco6.setRotationPoint(0.8616F, 0.4073F, 0.2455F);
        featherDeco6.setModelRendererName("featherDeco6");
        BoneWingEndLeft2.addChild(featherDeco6);
        setRotationAngle(featherDeco6, 1.0472F, 0.0F, 0.2094F);
        featherDeco6.mirror = true;
        featherDeco6.cubeList.add(new ModelBox(featherDeco6, 18, 44, -0.8499F, -3.294F, -5.5094F, 0, 5, 7, 0.0F));
        this.registerModelRenderer(featherDeco6);

        feather22 = new AnimatedModelRenderer(this);
        feather22.setRotationPoint(0.6616F, -0.121F, 3.8959F);
        feather22.setModelRendererName("feather22");
        BoneWingEndLeft2.addChild(feather22);
        setRotationAngle(feather22, -0.6283F, 0.0F, 0.0F);
        this.registerModelRenderer(feather22);

        cube_r9 = new AnimatedModelRenderer(this);
        cube_r9.setRotationPoint(-1.8645F, -3.1125F, -0.9382F);
        cube_r9.setModelRendererName("cube_r9");
        feather22.addChild(cube_r9);
        setRotationAngle(cube_r9, 0.0F, 0.0F, -0.0698F);
        cube_r9.mirror = true;
        cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 0, 1.2507F, 2.8016F, 0.8622F, 0, 5, 20, 0.0F));
        this.registerModelRenderer(cube_r9);

        feather23 = new AnimatedModelRenderer(this);
        feather23.setRotationPoint(0.2396F, 0.7502F, 9.0488F);
        feather23.setModelRendererName("feather23");
        BoneWingEndLeft2.addChild(feather23);
        setRotationAngle(feather23, -0.2793F, 0.0F, 0.0F);
        this.registerModelRenderer(feather23);

        cube_r10 = new AnimatedModelRenderer(this);
        cube_r10.setRotationPoint(-1.8645F, -2.8029F, -1.3058F);
        cube_r10.setModelRendererName("cube_r10");
        feather23.addChild(cube_r10);
        setRotationAngle(cube_r10, 0.0F, 0.0F, -0.0349F);
        cube_r10.mirror = true;
        cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 0, 1.3738F, 2.4885F, 0.1106F, 0, 5, 20, 0.0F));
        this.registerModelRenderer(cube_r10);

        feather24 = new AnimatedModelRenderer(this);
        feather24.setRotationPoint(0.9616F, 2.2392F, 0.8295F);
        feather24.setModelRendererName("feather24");
        BoneWingEndLeft2.addChild(feather24);
        setRotationAngle(feather24, -0.4363F, 0.0F, -0.0873F);
        this.registerModelRenderer(feather24);

        cube_r11 = new AnimatedModelRenderer(this);
        cube_r11.setRotationPoint(-1.982F, -2.9201F, -1.1242F);
        cube_r11.setModelRendererName("cube_r11");
        feather24.addChild(cube_r11);
        setRotationAngle(cube_r11, -0.2793F, -0.0175F, 0.0F);
        cube_r11.mirror = true;
        cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 7, 1.0F, 2.5774F, 1.963F, 0, 5, 18, 0.0F));
        this.registerModelRenderer(cube_r11);

        feather25 = new AnimatedModelRenderer(this);
        feather25.setRotationPoint(1.1616F, 4.9287F, 0.5286F);
        feather25.setModelRendererName("feather25");
        BoneWingEndLeft2.addChild(feather25);
        setRotationAngle(feather25, -0.6807F, 0.0F, 0.0F);
        this.registerModelRenderer(feather25);

        cube_r12 = new AnimatedModelRenderer(this);
        cube_r12.setRotationPoint(-1.8645F, -3.1693F, -0.8932F);
        cube_r12.setModelRendererName("cube_r12");
        feather25.addChild(cube_r12);
        setRotationAngle(cube_r12, -0.2967F, 0.0F, 0.0F);
        cube_r12.mirror = true;
        cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 15, 1.1F, 2.5774F, 1.963F, 0, 5, 15, 0.0F));
        this.registerModelRenderer(cube_r12);

        feather26 = new AnimatedModelRenderer(this);
        feather26.setRotationPoint(1.081F, 4.4876F, -2.0324F);
        feather26.setModelRendererName("feather26");
        BoneWingEndLeft2.addChild(feather26);
        setRotationAngle(feather26, -1.3614F, 0.0F, 0.0F);
        feather26.mirror = true;
        feather26.cubeList.add(new ModelBox(feather26, 0, 15, -0.7645F, -1.4736F, 1.3459F, 0, 5, 15, 0.0F));
        this.registerModelRenderer(feather26);

        feather27 = new AnimatedModelRenderer(this);
        feather27.setRotationPoint(1.4616F, 3.8662F, 0.8133F);
        feather27.setModelRendererName("feather27");
        BoneWingEndLeft2.addChild(feather27);
        setRotationAngle(feather27, -0.4189F, 0.0F, 0.0F);
        this.registerModelRenderer(feather27);

        cube_r13 = new AnimatedModelRenderer(this);
        cube_r13.setRotationPoint(-1.8645F, -2.9111F, -1.146F);
        cube_r13.setModelRendererName("cube_r13");
        feather27.addChild(cube_r13);
        setRotationAngle(cube_r13, 0.0F, 0.0349F, 0.0F);
        cube_r13.mirror = true;
        cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 23, 0.9048F, 2.6065F, -1.0467F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(cube_r13);

        feather28 = new AnimatedModelRenderer(this);
        feather28.setRotationPoint(1.4616F, 1.065F, 1.5639F);
        feather28.setModelRendererName("feather28");
        BoneWingEndLeft2.addChild(feather28);
        setRotationAngle(feather28, -0.0698F, 0.0F, 0.0F);
        this.registerModelRenderer(feather28);

        cube_r14 = new AnimatedModelRenderer(this);
        cube_r14.setRotationPoint(-1.8645F, -2.6847F, -1.5699F);
        cube_r14.setModelRendererName("cube_r14");
        feather28.addChild(cube_r14);
        setRotationAngle(cube_r14, 0.0F, 0.0349F, 0.0F);
        cube_r14.mirror = true;
        cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 23, 1.0006F, 2.5774F, -0.0403F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(cube_r14);

        feather29 = new AnimatedModelRenderer(this);
        feather29.setRotationPoint(0.7935F, 1.2329F, 1.44F);
        feather29.setModelRendererName("feather29");
        BoneWingEndLeft2.addChild(feather29);
        setRotationAngle(feather29, 0.0524F, -0.0698F, -0.0873F);
        feather29.mirror = true;
        feather29.cubeList.add(new ModelBox(feather29, 0, 23, -0.9585F, -0.0582F, -1.7728F, 0, 5, 12, 0.0F));
        this.registerModelRenderer(feather29);

        WingOBlEnd2 = new AnimatedModelRenderer(this);
        WingOBlEnd2.setRotationPoint(-0.0384F, 0.1648F, -0.1876F);
        WingOBlEnd2.setModelRendererName("WingOBlEnd2");
        BoneWingEndLeft2.addChild(WingOBlEnd2);
        this.registerModelRenderer(WingOBlEnd2);

        this.rootBones.add(body);
    }

    @Override
    public ResourceLocation getAnimationFileLocation()
    {
        return new ResourceLocation("arsmagica2", "animations/flyposes.json");
    }
}
