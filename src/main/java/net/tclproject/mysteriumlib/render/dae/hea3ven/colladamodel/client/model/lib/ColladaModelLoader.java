package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ColladaModelLoader implements IModelCustomLoader {
   private static final String[] types = new String[]{"dae", "DAE"};

   public String getType() {
      return "COLLADA model";
   }

   public String[] getSuffixes() {
      return types;
   }

   public static void init() {
      if (!AdvancedModelLoader.getSupportedSuffixes().contains("dae")) {
         AdvancedModelLoader.registerModelHandler(new ColladaModelLoader());
      }

   }

   public IModelCustom loadInstance(ResourceLocation resource) throws ModelFormatException {
      IResource res;
      try {
         res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
      } catch (IOException var4) {
         throw new ModelFormatException("IO Exception reading model format", var4);
      }

      return this.LoadFromStream(res.getInputStream());
   }

   public IModelAnimationCustom loadAnimationInstance(ResourceLocation resource) throws ModelFormatException {
      IResource res;
      try {
         res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
      } catch (IOException var4) {
         throw new ModelFormatException("IO Exception reading model format", var4);
      }

      return this.LoadFromStream(res.getInputStream());
   }

   private Model LoadFromStream(InputStream stream) {
      try {
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         return this.LoadFromXml(builder.parse(stream));
      } catch (IOException var3) {
         throw new ModelFormatException("IO Exception reading model format", var3);
      } catch (ParserConfigurationException var4) {
         throw new ModelFormatException("Xml Parser Exception reading model format", var4);
      } catch (SAXException var5) {
         throw new ModelFormatException("Xml Parsing Exception reading model format", var5);
      }
   }

   private Model LoadFromXml(Document doc) {
      ColladaAsset asset = new ColladaAsset(doc);
      return asset.getModel(asset.getRootSceneId());
   }
}
