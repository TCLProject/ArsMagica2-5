/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.struct.inventory;

import am2.AMCore;
import am2.worldgen.smartgen.struct.files.FileLoadContext;
import am2.worldgen.smartgen.struct.files.FileTypeHandler;
import ivorius.ivtoolkit.tools.IvFileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 25.05.14.
 */
public class ItemCollectionSaveHandler implements FileTypeHandler
{
    public static final ItemCollectionSaveHandler INSTANCE = new ItemCollectionSaveHandler();

    public static final String FILE_SUFFIX = "rcig";

    public static GenericItemCollection.Component readInventoryGenerator(Path file) throws IOException, InventoryLoadException
    {
        return GenericItemCollectionRegistry.INSTANCE.createComponentFromJSON(new String(Files.readAllBytes(file)));
    }

    @Override
    public boolean loadFile(Path path, FileLoadContext context)
    {
        try
        {
            GenericItemCollection.Component component = readInventoryGenerator(path);

            String name = context.customID != null ? context.customID : FilenameUtils.getBaseName(path.getFileName().toString());

            if (component.inventoryGeneratorID == null || component.inventoryGeneratorID.length() == 0) // Legacy support
                component.inventoryGeneratorID = name;

            GenericItemCollectionRegistry.INSTANCE.register(component, name, context.domain, context.active, context.custom);

            return true;
        }
        catch (IOException | InventoryLoadException e)
        {
            AMCore.logger.warn("Error reading inventory generator", e);
        }

        return false;
    }

    @Override
    public void clearCustomFiles()
    {
        GenericItemCollectionRegistry.INSTANCE.clearCustom();
    }

    public static boolean saveInventoryGenerator(GenericItemCollection.Component info, String name)
    {
        File structuresFile = IvFileHelper.getValidatedFolder(AMCore.proxy.getBaseFolderFile("AM2InventoryCustomFiles"));
        if (structuresFile != null)
        {
            File inventoryGeneratorsFile = IvFileHelper.getValidatedFolder(structuresFile, "active");
            if (inventoryGeneratorsFile != null)
            {
                File newFile = new File(inventoryGeneratorsFile, String.format("%s.%s", name, FILE_SUFFIX));
                String json = GenericItemCollectionRegistry.INSTANCE.createJSONFromComponent(info);

                try
                {
                    FileUtils.writeStringToFile(newFile, json);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                return newFile.exists();
            }
        }

        return false;
    }
}
