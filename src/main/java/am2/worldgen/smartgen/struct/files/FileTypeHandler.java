/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.struct.files;

import java.nio.file.Path;

/**
 * Created by lukas on 18.09.15.
 */
public interface FileTypeHandler
{
    boolean loadFile(Path path, FileLoadContext context);

    void clearCustomFiles();
}
