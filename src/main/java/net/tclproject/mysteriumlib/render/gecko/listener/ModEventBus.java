/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.listener;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity.*;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model.*;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AdvancedNPCEntity;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.*;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityIceDragon;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexLarva;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexPupa;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexSentinel;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.animator.IceDragonTabulaModelAnimator;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.IceAndFireTabulaModel;

public class ModEventBus
{

	public static void registerEntities()
	{ // moved to EntityReg
//		EntityRegistry.registerModEntity(StingrayTestEntity.class, "stingray", 127, AMCore.instance, 160, 2, false);
//		EntityRegistry.registerModEntity(BrownEntity.class, "brown", 128, AMCore.instance, 160, 2, false);
//		EntityRegistry.registerModEntity(RobotEntity.class, "robot", 129, AMCore.instance, 160, 2, false);
//		EntityRegistry.registerModEntity(AngelEntity.class, "angel", 130, AMCore.instance, 160, 2, false);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(StingrayTestEntity.class, new StingrayRenderer());
		RenderingRegistry.registerEntityRenderingHandler(BrownEntity.class, new BrownRenderer());
		RenderingRegistry.registerEntityRenderingHandler(RobotEntity.class, new RobotRenderer());
		RenderingRegistry.registerEntityRenderingHandler(AngelEntity.class, new AngelRenderer());
		RenderingRegistry.registerEntityRenderingHandler(CrystalCrabEntity.class, new CrystalCrabRenderer(new CrystalCrabModel(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(ChickenEntity.class, new ChickenRenderer(new ChickenModel(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(HorseEntity.class, new HorseRenderer(new HorseModel(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedNPCEntity.class, new AdvancedNPCRenderer(new AdvancedNPCModel(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(FairyEntity.class, new FairyRenderer());
		RenderingRegistry.registerEntityRenderingHandler(DragonEntity.class, new DragonRenderer());
		RenderingRegistry.registerEntityRenderingHandler(StonelingEntity.class, new StonelingRenderer(new StonelingModel(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexLarva.class, new RenderLarva());
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexPupa.class, new RenderPupa());
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexSentinel.class, new RenderSentinel());
		RenderingRegistry.registerEntityRenderingHandler(EntEntity.class, new TemplateEntityRenderer(new EntModel(), 0.6F, "ent_texture"));
		RenderingRegistry.registerEntityRenderingHandler(PointerEntity.class, new TemplateEntityRenderer(new PointerModel(), 0.6F, "pointer_texture2"));
		RenderingRegistry.registerEntityRenderingHandler(PortalEntity.class, new TemplateEntityRenderer(new PortalModel(), 0.6F, "portal_texture"));
		RenderingRegistry.registerEntityRenderingHandler(TankEntity.class, new TemplateEntityRenderer(new TankModel(), 0.6F, "tank_texture"));
		RenderingRegistry.registerEntityRenderingHandler(TrapEntity.class, new TemplateEntityRenderer(new TrapModel(), 0.6F, "trap_texture"));
		RenderingRegistry.registerEntityRenderingHandler(DuckEntity.class, new TemplateEntityRenderer(new DuckModel(), 0.6F, "duck"));
		RenderingRegistry.registerEntityRenderingHandler(CrowEntity.class, new TemplateEntityRenderer(new CrowModel(), 0.6F, "crow"));
		RenderingRegistry.registerEntityRenderingHandler(TentacleEntity.class, new TemplateEntityRenderer(new TentacleModel(), 0.6F, "tentacle"));
		RenderingRegistry.registerEntityRenderingHandler(AncientAbyssEntity.class, new TemplateEntityRenderer(new AncientAbyssModel(), 0.6F, "ancient_abyss_texture"));

		try {
			IceAndFireTabulaModel icedragon_model = new IceAndFireTabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/arsmagica2/tabula/icedragon/dragonIceGround"), new IceDragonTabulaModelAnimator());
			RenderingRegistry.registerEntityRenderingHandler(EntityIceDragon.class, new RenderDragonBase(icedragon_model, false));
		} catch (Exception var3) {
		}
	}
}
