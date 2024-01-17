package am2.interop;

import am2.EnervatorRecipeHelper;
import am2.blocks.BlocksCommonProxy;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeHandlerEnervator  extends TemplateRecipeHandler{

	public class CachedEvervatorRecipe extends CachedRecipe {

		public List<PositionedStack> inputs = new ArrayList<PositionedStack>();
		public PositionedStack output;

		public CachedEvervatorRecipe(ItemStack from, ItemStack to) {
			setIngredients(from);
			output = new PositionedStack(to, 82, 4);
		}

		public void setIngredients(ItemStack input) {
			this.inputs.add(new PositionedStack(input, 74, 42));
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, inputs);
		}

		@Override
		public PositionedStack getResult() {
			return output;
		}

	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("am2.gui.EntropicEnervator");
	}

	@Override
	public String getOverlayIdentifier() {
		return "arsmagica2.entropic_enervator";
	}

	@Override
	public String getGuiTexture() {
		return "arsmagica2:textures/guis/enervatorGUINEI.png";
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(54, 3, 22, 18), getOverlayIdentifier()));
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(this.getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 5, 166, 70);
	}

	public void drawForeground(int recipe) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(2896);
		this.drawExtras(recipe);
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	public HashMap getRecipes() {
//		if (!GuiRecipeTab.handlerMap.containsKey(getHandlerId())) {
//			HandlerInfo info = new HandlerInfo.Builder(getHandlerId(), "Ars Magica 2", "arsmagica2")
//					.setHeight(HandlerInfo.DEFAULT_HEIGHT)
//					.setWidth(HandlerInfo.DEFAULT_WIDTH)
//					.setMaxRecipesPerPage(this.recipiesPerPage())
//					.setDisplayStack(new ItemStack(BlocksCommonProxy.entropicEnervator))
//					.build();
//			GuiRecipeTab.handlerMap.put(getHandlerId(), info);
//		}

		return EnervatorRecipeHelper.instance.getRecipes();
	}

	public RecipeHandlerEnervator.CachedEvervatorRecipe getCachedRecipe(ItemStack from, ItemStack to) {
		return new RecipeHandlerEnervator.CachedEvervatorRecipe(from, to);
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if(outputId.equals(getOverlayIdentifier())) {
			for (Object key : getRecipes().keySet()) {
				ItemStack from = (ItemStack)key;
				arecipes.add(getCachedRecipe(from, (ItemStack)getRecipes().get(from)));
			}
		} else super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (Object key : getRecipes().keySet()) {
			if(key == null)
				continue;

			if(getRecipes().get(key) != null && NEIServerUtils.areStacksSameType((ItemStack)getRecipes().get(key), result) || ((ItemStack)getRecipes().get(key)).stackTagCompound == null && NEIServerUtils.areStacksSameTypeCrafting(((ItemStack)getRecipes().get(key)), result) && ((ItemStack)getRecipes().get(key)).getItem() != Items.skull)
				arecipes.add(getCachedRecipe((ItemStack)key, ((ItemStack)getRecipes().get(key))));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (Object key : getRecipes().keySet()) {
			if(key == null)
				continue;

			RecipeHandlerEnervator.CachedEvervatorRecipe crecipe = getCachedRecipe((ItemStack)key, ((ItemStack)getRecipes().get(key)));
			if(crecipe.contains(crecipe.inputs, ingredient)) arecipes.add(crecipe);
		}
	}
}
