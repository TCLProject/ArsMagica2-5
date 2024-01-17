package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ModelFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ColladaAsset {
   public String xAxis;
   public String zAxis;
   public String yAxis;
   private Element root;
   private XPath xpath = XPathFactory.newInstance().newXPath();

   public ColladaAsset(Document doc) {
      this.root = doc.getDocumentElement();
      String upAxis = this.GetXPathString("asset/up_axis");
      if (upAxis.equals("X_UP")) {
         this.xAxis = "Z";
         this.yAxis = "X";
         this.zAxis = "Y";
      } else if (upAxis.equals("Y_UP")) {
         this.xAxis = "X";
         this.yAxis = "Y";
         this.zAxis = "Z";
      } else {
         if (!upAxis.equals("Z_UP")) {
            throw new ModelFormatException("Invalid up axis configuration");
         }

         this.xAxis = "Y";
         this.yAxis = "Z";
         this.zAxis = "X";
      }

   }

   private String GetXPathString(String path) {
      return this.GetXPathString(this.root, path);
   }

   private String GetXPathString(Element node, String path) {
      try {
         return (String)this.xpath.evaluate(path, node, XPathConstants.STRING);
      } catch (XPathExpressionException var4) {
         throw new ModelFormatException("Could not get the string for the path '" + path + "'", var4);
      }
   }

   private Element GetXPathElement(String path) {
      return this.GetXPathElement(this.root, path);
   }

   private Element GetXPathElement(Element node, String path) {
      try {
         return (Element)this.xpath.evaluate(path, node, XPathConstants.NODE);
      } catch (XPathExpressionException var4) {
         throw new ModelFormatException("Could not get the element for the path '" + path + "'", var4);
      }
   }

   private Collection GetXPathElementList(String path) {
      return this.GetXPathElementList(this.root, path);
   }

   private Collection GetXPathElementList(Element node, String path) {
      try {
         LinkedList result = new LinkedList();
         NodeList nodes = (NodeList)this.xpath.evaluate(path, node, XPathConstants.NODESET);

         for(int i = 0; i < nodes.getLength(); ++i) {
            result.add((Element)nodes.item(i));
         }

         return result;
      } catch (XPathExpressionException var6) {
         throw new ModelFormatException("Could not get the node list for the path '" + path + "'", var6);
      }
   }

   private Collection GetXmlChildren(Element node) {
      LinkedList result = new LinkedList();
      NodeList nodes = node.getChildNodes();

      for(int i = 0; i < nodes.getLength(); ++i) {
         if (nodes.item(i).getNodeType() == 1) {
            result.add((Element)nodes.item(i));
         }
      }

      return result;
   }

   private String parseURL(String url) {
      return url.substring(1);
   }

   private String[] splitData(String data) {
      return data.trim().split("\\s+");
   }

   private int[] splitDataInt(String data) {
      String[] dataSplit = this.splitData(data);
      int[] ret = new int[dataSplit.length];

      for(int i = 0; i < ret.length; ++i) {
         ret[i] = Integer.parseInt(dataSplit[i]);
      }

      return ret;
   }

   private double[] splitDataDouble(String data) {
      String[] dataSplit = this.splitData(data);
      double[] ret = new double[dataSplit.length];

      for(int i = 0; i < ret.length; ++i) {
         ret[i] = Double.parseDouble(dataSplit[i]);
      }

      return ret;
   }

   public String getRootSceneId() {
      return this.parseURL(this.GetXPathString("scene/instance_visual_scene/@url"));
   }

   public Model getModel(String id) {
      Element sceneElem = this.GetXPathElement(String.format("library_visual_scenes/visual_scene[@id='%s']", id));
      return this.parseScene(sceneElem);
   }

   private Model parseScene(Element sceneElem) {
      Model model = new Model();
      Iterator i$ = this.GetXPathElementList(sceneElem, "node").iterator();

      Element animElem;
      while(i$.hasNext()) {
         animElem = (Element)i$.next();
         Geometry geom = this.parseSceneNode(animElem);
         if (geom != null) {
            model.addGeometry(geom);
         }
      }

      i$ = this.GetXPathElementList("library_animations/animation").iterator();

      while(i$.hasNext()) {
         animElem = (Element)i$.next();
         this.parseAnimation(model, animElem);
      }

      return model;
   }

   private Geometry parseSceneNode(Element nodeElem) {
      String geomURL = this.GetXPathString(nodeElem, "instance_geometry/@url");
      if (geomURL == "") {
         return null;
      } else {
         Geometry geom = this.getGeometry(this.parseURL(geomURL));
         String nodeId = nodeElem.getAttribute("id");
         geom.setName(nodeId);
         Iterator i$ = this.GetXmlChildren(nodeElem).iterator();

         while(i$.hasNext()) {
            Element child = (Element)i$.next();
            Transform trans = null;
            String transId = null;
            if (child.getTagName() == "translate") {
               trans = this.parseTranslation(child);
               transId = child.getAttribute("sid");
            } else if (child.getTagName() == "rotate") {
               trans = this.parseRotation(child);
               transId = child.getAttribute("sid");
            } else if (child.getTagName() == "scale") {
               trans = this.parseScale(child);
               transId = child.getAttribute("sid");
            } else if (child.getTagName() == "matrix") {
               trans = this.parseMatrix(child);
               transId = child.getAttribute("sid");
            }

            if (trans != null) {
               geom.addTransform(transId, (Transform)trans);
            }
         }

         return geom;
      }
   }

   private Translation parseTranslation(Element transElem) {
      double[] transData = this.splitDataDouble(transElem.getTextContent());
      if (transData.length != 3) {
         throw new ModelFormatException("Invalid translate data");
      } else {
         return new Translation(this.toMinecraftCoords(transData[0], transData[1], transData[2]));
      }
   }

   private Rotation parseRotation(Element rotElem) {
      double[] rotData = this.splitDataDouble(rotElem.getTextContent());
      if (rotData.length != 4) {
         throw new ModelFormatException("Invalid rotate data");
      } else {
         return new Rotation(this.toMinecraftCoords(rotData[0], rotData[1], rotData[2]), rotData[3]);
      }
   }

   private Scale parseScale(Element scaleElem) {
      double[] scaleData = this.splitDataDouble(scaleElem.getTextContent());
      if (scaleData.length != 3) {
         throw new ModelFormatException("Invalid scale data");
      } else {
         return new Scale(this.toMinecraftCoords(scaleData[0], scaleData[1], scaleData[2]));
      }
   }

   private Matrix parseMatrix(Element matrixElem) {
      double[] matrixData = this.splitDataDouble(matrixElem.getTextContent());
      if (matrixData.length != 16) {
         throw new ModelFormatException("Invalid matrix data");
      } else {
         double tmp = matrixData[7];
         matrixData[7] = matrixData[11];
         matrixData[11] = -tmp;
         ByteBuffer matrixBytes = ByteBuffer.allocateDirect(128);
         matrixBytes.order(ByteOrder.nativeOrder());
         matrixBytes.clear();
         DoubleBuffer matrix = matrixBytes.asDoubleBuffer();
         matrix.clear();

         for(int j = 0; j < 4; ++j) {
            matrix.put(matrixData[j]);
            matrix.put(matrixData[j + 4]);
            matrix.put(matrixData[j + 8]);
            matrix.put(matrixData[j + 12]);
         }

         return new Matrix(this.toMinecraftCoords(matrix));
      }
   }

   public Geometry getGeometry(String id) {
      Element geomElem = this.GetXPathElement(String.format("library_geometries/geometry[@id='%s']", id));
      return this.parseGeometry(geomElem);
   }

   private Geometry parseGeometry(Element geomElem) {
      Geometry geom = new Geometry();
      Iterator i$ = this.GetXPathElementList(geomElem, "mesh").iterator();

      while(i$.hasNext()) {
         Element meshElem = (Element)i$.next();
         this.parseMesh(geom, meshElem);
      }

      return geom;
   }

   private void parseMesh(Geometry geom, Element meshElem) {
      Iterator i$ = this.GetXmlChildren(meshElem).iterator();

      while(i$.hasNext()) {
         Element child = (Element)i$.next();
         if (child.getTagName() == "triangles") {
            this.parseMeshTriangles(geom, meshElem, child);
         } else if (child.getNodeName() == "polylist") {
            this.parseMeshPolylist(geom, meshElem, child);
         } else if (child.getNodeName() == "polygons") {
            this.parseMeshPolygons(geom, meshElem, child);
         }
      }

   }

   private void parseMeshTriangles(Geometry geom, Element meshElem, Element triElem) {
      ColladaSource[] dataSrcs = this.parseMeshInputSources(meshElem, triElem);
      int count = Integer.parseInt(triElem.getAttribute("count"));
      int[] refs = this.splitDataInt(this.GetXPathElement(triElem, "p").getTextContent());
      if (refs.length != count * 9) {
         throw new ModelFormatException("Wrong number of data elements");
      } else {
         for(int q = 0; q < count; ++q) {
            Vec3[] vertex = new Vec3[3];
            Vec3[] normal = new Vec3[3];
            Vec3[] texCoords = new Vec3[3];

            for(int r = 0; r < 3; ++r) {
               vertex[r] = this.toMinecraftCoords(dataSrcs[0].getVec3(refs[q * 9 + r * 3], "X", "Y", "Z"));
               normal[r] = this.toMinecraftCoords(dataSrcs[1].getVec3(refs[q * 9 + r * 3 + 1], "X", "Y", "Z"));
               texCoords[r] = dataSrcs[2].getVec2(refs[q * 9 + r * 3 + 2], "S", "T");
            }

            Face poly = new Face();
            poly.setVertex(vertex, normal, texCoords);
            geom.addFace(poly);
         }

      }
   }

   private ColladaSource[] parseMeshInputSources(Element meshElem, Element defElem) {
      ColladaSource[] dataSrcs = new ColladaSource[3];
      String verticesId = this.parseURL(this.GetXPathString(defElem, "input[@semantic='VERTEX']/@source"));
      String srcId = this.parseURL(this.GetXPathString(meshElem, String.format("vertices[@id='%s']/input/@source", verticesId)));
      dataSrcs[0] = this.parseSource(this.GetXPathElement(meshElem, String.format("source[@id='%s']", srcId)));
      String normalsId = this.parseURL(this.GetXPathString(defElem, "input[@semantic='NORMAL']/@source"));
      dataSrcs[1] = this.parseSource(this.GetXPathElement(meshElem, String.format("source[@id='%s']", normalsId)));
      String texcoordId = this.parseURL(this.GetXPathString(defElem, "input[@semantic='TEXCOORD']/@source"));
      dataSrcs[2] = this.parseSource(this.GetXPathElement(meshElem, String.format("source[@id='%s']", texcoordId)));
      return dataSrcs;
   }

   private void parseMeshPolylist(Geometry geom, Element meshElem, Element polylistElem) {
      ColladaSource[] dataSrcs = this.parseMeshInputSources(meshElem, polylistElem);
      int count = Integer.parseInt(polylistElem.getAttribute("count"));
      int[] vcount = this.splitDataInt(this.GetXPathElement(polylistElem, "vcount").getTextContent());
      int[] refs = this.splitDataInt(this.GetXPathElement(polylistElem, "p").getTextContent());
      if (vcount.length != count) {
         throw new ModelFormatException("Wrong number of data elements");
      } else {
         int p = 0;

         for(int q = 0; q < vcount.length; ++q) {
            Vec3[] vertex = new Vec3[vcount[q]];
            Vec3[] normal = new Vec3[vcount[q]];
            Vec3[] texCoords = new Vec3[vcount[q]];

            for(int r = 0; r < vcount[q]; ++r) {
               vertex[r] = this.toMinecraftCoords(dataSrcs[0].getVec3(refs[p * 3], "X", "Y", "Z"));
               normal[r] = this.toMinecraftCoords(dataSrcs[1].getVec3(refs[p * 3 + 1], "X", "Y", "Z"));
               texCoords[r] = dataSrcs[2].getVec2(refs[p * 3 + 2], "S", "T");
               ++p;
            }

            Face poly = new Face();
            poly.setVertex(vertex, normal, texCoords);
            geom.addFace(poly);
         }

      }
   }

   private void parseMeshPolygons(Geometry geom, Element meshElem, Element polyElem) {
      ColladaSource[] dataSrcs = this.parseMeshInputSources(meshElem, polyElem);
      int count = Integer.parseInt(polyElem.getAttribute("count"));
      Collection polysData = this.GetXPathElementList(polyElem, "p");
      if (polysData.size() != count) {
         throw new ModelFormatException("Wrong number of data elements");
      } else {
         Iterator i$ = polysData.iterator();

         while(i$.hasNext()) {
            Element pElem = (Element)i$.next();
            int[] refs = this.splitDataInt(pElem.getTextContent());
            Vec3[] vertex = new Vec3[refs.length / 3];
            Vec3[] normal = new Vec3[refs.length / 3];
            Vec3[] texCoords = new Vec3[refs.length / 3];

            for(int r = 0; r < refs.length / 3; ++r) {
               vertex[r] = this.toMinecraftCoords(dataSrcs[0].getVec3(refs[r * 3], "X", "Y", "Z"));
               normal[r] = this.toMinecraftCoords(dataSrcs[1].getVec3(refs[r * 3 + 1], "X", "Y", "Z"));
               texCoords[r] = dataSrcs[2].getVec2(refs[r * 3 + 2], "S", "T");
            }

            Face poly = new Face();
            poly.setVertex(vertex, normal, texCoords);
            geom.addFace(poly);
         }

      }
   }

   private ColladaSource parseSource(Element srcElem) {
      ColladaSource src = new ColladaSource();
      Element data_array = this.GetXPathElement(srcElem, "float_array");
      if (data_array == null) {
         data_array = this.GetXPathElement(srcElem, "Name_array");
      }

      if (data_array == null) {
         throw new ModelFormatException("Could not find the data array for the source");
      } else {
         int data_count;
         try {
            data_count = Integer.parseInt(data_array.getAttribute("count"));
         } catch (NumberFormatException var15) {
            throw new ModelFormatException("Could not parse the count attribute of the <float_array>", var15);
         }

         float[] float_data = null;
         String[] name_data = null;
         if (data_array.getNodeName() == "float_array") {
            float_data = new float[data_count];
         } else if (data_array.getNodeName() == "Name_array") {
            name_data = new String[data_count];
         }

         int i = 0;
         String data_string = data_array.getTextContent();
         String[] arr$ = this.splitData(data_string);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String val = arr$[i$];
            if (data_array.getNodeName() == "float_array") {
               float_data[i] = Float.parseFloat(val);
            } else if (data_array.getNodeName() == "Name_array") {
               name_data[i] = val;
            }

            ++i;
            if (i > data_count) {
               throw new ModelFormatException("Too many values in the data");
            }
         }

         if (i < data_count - 1) {
            throw new ModelFormatException("Not enough values in the data");
         } else {
            if (data_array.getNodeName() == "float_array") {
               src.setData(float_data);
            } else if (data_array.getNodeName() == "Name_array") {
               src.setData(name_data);
            }

            Element accessorNode = this.GetXPathElement(srcElem, "technique_common/accessor");

            try {
               src.setCount(Integer.parseInt(accessorNode.getAttribute("count")));
               if (accessorNode.getAttribute("stride") != "") {
                  src.setStride(Integer.parseInt(accessorNode.getAttribute("stride")));
               } else {
                  src.setStride(1);
               }
            } catch (NumberFormatException var14) {
               throw new ModelFormatException("Could not parse the count attribute of the <float_array>", var14);
            }

            Collection paramElems = this.GetXPathElementList(accessorNode, "param");
            String[] params = new String[paramElems.size()];
            i = 0;

            Element paramElem;
            for(Iterator i$ = paramElems.iterator(); i$.hasNext(); params[i++] = paramElem.getAttribute("name")) {
               paramElem = (Element)i$.next();
            }

            src.setParams(params);
            return src;
         }
      }
   }

   private void parseAnimation(Model model, Element animElem) {
      Element channelNode = this.GetXPathElement(animElem, "channel");
      Element subAnimationElem;
      if (channelNode != null) {
         String samplerId = this.parseURL(channelNode.getAttribute("source"));
         subAnimationElem = this.GetXPathElement(animElem, String.format("sampler[@id='%s']", samplerId));
         HashMap sources = this.parseAnimationInputSources(animElem, subAnimationElem);
         Animation animation = new Animation();

         for(int i = 0; i < ((ColladaSource)sources.get("INPUT")).getCount(); ++i) {
            int frame = (int)Math.floor((double)((ColladaSource)sources.get("INPUT")).getDouble("TIME", i));
            String interpName = ((ColladaSource)sources.get("INTERPOLATION")).getString(0, i);
            Interpolation interp = null;
            if (interpName.equals("LINEAR")) {
               interp = new LinearInterpolation();
            } else {
               if (!interpName.equals("BEZIER")) {
                  throw new ModelFormatException(String.format("Invalid interpolation method %s", interpName));
               }

               if (i + 1 < ((ColladaSource)sources.get("INPUT")).getCount()) {
                  interp = new BezierInterpolation((double)((ColladaSource)sources.get("OUT_TANGENT")).getDouble("Y", i), (double)((ColladaSource)sources.get("IN_TANGENT")).getDouble("Y", i + 1));
               } else {
                  interp = new LinearInterpolation();
               }
            }

            KeyFrame keyFrame = new KeyFrame((double)frame, (double)((ColladaSource)sources.get("OUTPUT")).getDouble(0, i), (Interpolation)interp);
            animation.addKeyFrame(keyFrame);
         }

         String[] targetParts = channelNode.getAttribute("target").split("[/.]");
         Transform trans = model.getGeometry(targetParts[0]).getTransform(targetParts[1]);
         if (targetParts.length == 3) {
            trans.setAnimation(this.toMinecraftParam(targetParts[2]), animation);
         } else {
            trans.setAnimation((String)null, animation);
         }
      }

      Iterator i$ = this.GetXPathElementList(animElem, "animation").iterator();

      while(i$.hasNext()) {
         subAnimationElem = (Element)i$.next();
         this.parseAnimation(model, subAnimationElem);
      }

   }

   private HashMap parseAnimationInputSources(Element animElem, Element samplerElem) {
      HashMap sources = new HashMap();
      Iterator i$ = this.GetXPathElementList(samplerElem, "input").iterator();

      while(i$.hasNext()) {
         Element inputElem = (Element)i$.next();
         sources.put(inputElem.getAttribute("semantic"), this.parseSource(this.GetXPathElement(animElem, String.format("source[@id='%s']", this.parseURL(inputElem.getAttribute("source"))))));
      }

      return sources;
   }

   private String toMinecraftParam(String param) {
      if (param.equals(this.xAxis)) {
         return "X";
      } else if (param.equals(this.yAxis)) {
         return "Y";
      } else {
         return param.equals(this.zAxis) ? "Z" : param;
      }
   }

   public Vec3 toMinecraftCoords(double x, double y, double z) {
      return this.toMinecraftCoords(Vec3.createVectorHelper(x, y, z));
   }

   public Vec3 toMinecraftCoords(Vec3 vec) {
      if (this.yAxis.equals("X")) {
         return Vec3.createVectorHelper(vec.zCoord, vec.xCoord, vec.yCoord);
      } else if (this.yAxis.equals("Y")) {
         return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
      } else {
         return this.yAxis.equals("Z") ? Vec3.createVectorHelper(vec.yCoord, vec.zCoord, vec.xCoord) : null;
      }
   }

   public DoubleBuffer toMinecraftCoords(DoubleBuffer matrix) {
      return matrix;
   }
}
