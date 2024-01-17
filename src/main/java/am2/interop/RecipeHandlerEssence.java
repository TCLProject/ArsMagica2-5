package am2.interop;

import am2.blocks.BlocksCommonProxy;
import am2.blocks.RecipesEssenceRefiner;
import am2.items.RecipeArsMagica;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeHandlerEssence extends TemplateRecipeHandler {

	public class CachedRefinerRecipe extends CachedRecipe {

		public List<PositionedStack> inputs = new ArrayList<PositionedStack>();
		public PositionedStack output;

		public CachedRefinerRecipe(RecipeArsMagica recipe) {
			setIngredients(recipe.getRecipeItems());
			output = new PositionedStack(recipe.getOutput(), 138, 98);
			inputs.add(new PositionedStack(new ItemStack(BlocksCommonProxy.essenceRefiner), 18, 3));
		}

		public void setIngredients(ItemStack[] inputs) {
			float degreePerInput = 360F / inputs.length;
			float currentDegree = -90F;

			for(int i = 0; i < inputs.length; i++) {
				int posX = 0, posY = 0;
				if (i == 0) {
					posX = 75;
					posY = 30;
				} else if (i == 1) {
					posX = 42;
					posY = 63;
				} else if (i == 2) {
					posX = 75;
					posY = 63;
				} else if (i == 3) {
					posX = 107;
					posY = 63;
				} else if (i == 4) {
					posX = 75;
					posY = 95;
				}
				this.inputs.add(new PositionedStack(inputs[i], posX, posY));
				currentDegree += degreePerInput;
			}
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
		return StatCollector.translateToLocal("am2.gui.EssenceRefiner");
	}


	@Override
	public String getOverlayIdentifier() {
		return "arsmagica2.essence_refiner";
	}

	@Override
	public String getGuiTexture() {
		return "arsmagica2:textures/guis/essenceExtractorNEI.png";
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(18, 3, 18, 18), getOverlayIdentifier()));
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(this.getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 128);
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

	public List<? extends RecipeArsMagica> getRecipes() {
//		if (!GuiRecipeTab.handlerMap.containsKey(getHandlerId())) {
//			HandlerInfo info = new HandlerInfo.Builder(getHandlerId(), "Ars Magica 2", "arsmagica2")
//					.setHeight(HandlerInfo.DEFAULT_HEIGHT)
//					.setWidth(HandlerInfo.DEFAULT_WIDTH)
//					.setMaxRecipesPerPage(this.recipiesPerPage())
//					.setDisplayStack(new ItemStack(BlocksCommonProxy.essenceRefiner))
//					.build();
//			GuiRecipeTab.handlerMap.put(getHandlerId(), info);
//		}
		List<RecipeArsMagica> list = new ArrayList();
		for (Object r : RecipesEssenceRefiner.essenceRefinement().GetRecipeList().values()) {
			RecipeArsMagica recipe = (RecipeArsMagica) r;
			list.add(recipe);
		}
		return list;
	}

	public CachedRefinerRecipe getCachedRecipe(RecipeArsMagica recipe) {
		return new CachedRefinerRecipe(recipe);
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if(outputId.equals(getOverlayIdentifier())) {
			for(RecipeArsMagica recipe : getRecipes())
				arecipes.add(getCachedRecipe(recipe));
		} else super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for(RecipeArsMagica recipe : getRecipes()){
			if(recipe == null)
				continue;

			if(recipe.getOutput().stackTagCompound != null && NEIServerUtils.areStacksSameType(recipe.getOutput(), result) || recipe.getOutput().stackTagCompound == null && NEIServerUtils.areStacksSameTypeCrafting(recipe.getOutput(), result) && recipe.getOutput().getItem() != Items.skull)
				arecipes.add(getCachedRecipe(recipe));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for(RecipeArsMagica recipe : getRecipes()) {
			if(recipe == null)
				continue;

			CachedRefinerRecipe crecipe = getCachedRecipe(recipe);
			if(crecipe.contains(crecipe.inputs, ingredient)) arecipes.add(crecipe);
		}
	}
}
