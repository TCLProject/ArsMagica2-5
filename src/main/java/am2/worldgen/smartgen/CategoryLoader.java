/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen;

import am2.AMCore;
import am2.worldgen.smartgen.struct.files.FileLoadContext;
import am2.worldgen.smartgen.struct.files.FileTypeHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 29.09.15.
 */
public class CategoryLoader implements FileTypeHandler
{
    public static final String FILE_SUFFIX = "rcnc";

    private Gson gson = createGson();

    public static Gson createGson()
    {
        return new GsonBuilder().create();
    }

    @Override
    public boolean loadFile(Path path, FileLoadContext context)
    {
        StructureSelector.SimpleCategory category = null;
        String name = context.customID != null ? context.customID : FilenameUtils.getBaseName(path.getFileName().toString());

        try
        {
            category = read(new String(Files.readAllBytes(path)));
        }
        catch (IOException e)
        {
            AMCore.logger.warn("Error reading natural spawn category", e);
        }

        if (category != null)
        {
            StructureSelector.registerCategory(name, category, context.custom);

            return true;
        }

        return false;
    }

    @Override
    public void clearCustomFiles()
    {
        StructureSelector.clearCustom();
    }

    public StructureSelector.SimpleCategory read(String file)
    {
        return gson.fromJson(file, StructureSelector.SimpleCategory.class);
    }
}
