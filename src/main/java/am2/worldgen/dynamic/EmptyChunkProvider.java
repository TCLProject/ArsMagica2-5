package am2.worldgen.dynamic;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.List;

public class EmptyChunkProvider implements IChunkProvider
{
    public World worldObj;

    public EmptyChunkProvider(World worldObj)
    {
        this.worldObj = worldObj;
    }

    @Override
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    @Override
    public Chunk loadChunk(int x, int z)
    {
        return this.provideChunk(x, z);
    }

    @Override
    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = new Chunk(this.worldObj, x, z);
        chunk.generateSkylightMap();
        return chunk;
    }
//
//    @Override
//    public Chunk provideChunk(BlockPos blockPosIn)
//    {
//        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
//    }

    @Override
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
    {
    }

    @Override
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
    {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    @Override
    public boolean canSave()
    {
        return true;
    }

    @Override
    public String makeString()
    {
        return "Guardian Domain";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        return ImmutableList.of();
    }

    @Override
    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_) {
        return null;
    }

    @Override
    public int getLoadedChunkCount()
    {
        return 0;
    }

    @Override
    public void recreateStructures(int p_82695_1_, int p_82695_2_) {
    }

    @Override
    public void saveExtraData()
    {
    }
}
