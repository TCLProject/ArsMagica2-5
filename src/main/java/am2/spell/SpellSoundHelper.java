package am2.spell;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SpellSoundHelper {

    static Minecraft mc = Minecraft.getMinecraft();

    public static class LoopingSound extends MovingSound {
        public boolean done = false;
        boolean fadingOut = false;
        public Entity ent = null;
        public float originalPosY;
        public int lastTickSinceUpdate = 0;

        protected LoopingSound(float x, float y, float z, ResourceLocation p_i45104_1_, float vol, float freq) {
            super(p_i45104_1_);
            this.xPosF = x;
            this.yPosF = y;
            this.zPosF = z;
            this.originalPosY = y;
            this.volume = vol;
            this.field_147663_c = freq;
        }

        protected LoopingSound(Entity e, ResourceLocation p_i45104_1_, float vol, float freq) {
            super(p_i45104_1_);
            this.xPosF = (float)e.posX;
            this.yPosF = (float)e.posY;
            this.zPosF = (float)e.posZ;
            this.volume = vol;
            this.field_147663_c = freq;
            this.ent = e;
        }

        public boolean canRepeat()
        {
            return true;
        }

        public int getRepeatDelay()
        {
            return 0;
        }

        @Override
        public void update() {
            if (done) donePlaying = true;
            if (this.ent != null) {
                this.xPosF = (float)this.ent.posX;
                if (!fadingOut) this.yPosF = (float)this.ent.posY;
                this.zPosF = (float)this.ent.posZ;
            }
            if (fadingOut) {
                this.yPosF = this.yPosF - 0.125F;
                if (this.yPosF <= (this.ent != null ? this.ent.posY-17F : this.originalPosY-17F)) donePlaying = true;
            }
            lastTickSinceUpdate = 0;
        }

        public void fadeOut() {
            fadingOut = true;
        }

        public float getVolume()
        {
            return this.volume;
        }

        public float getPitch()
        {
            return this.field_147663_c;
        }

        public float getXPosF()
        {
            return this.xPosF;
        }

        public float getYPosF()
        {
            return this.yPosF;
        }

        public float getZPosF()
        {
            return this.zPosF;
        }
    }

    public static Map<String, LoopingSound> loopingSounds = new HashMap<String, LoopingSound>();
    public static LoopingSound currentlyPlayingMusic;

    public static void playLoopingSound(float x, float y, float z, String sound, float volume, float frequency, String uniqueSoundString) {
        LoopingSound toPlay = new LoopingSound(x, y, z, new ResourceLocation(uniqueSoundString), volume, frequency);
        playUniqueLoopingSound(uniqueSoundString, toPlay);
    }

    public static void playLoopingSound(Entity e, String sound, float volume, float frequency, String uniqueSoundString) {
        LoopingSound toPlay = new LoopingSound(e, new ResourceLocation(uniqueSoundString), volume, frequency);
        playUniqueLoopingSound(uniqueSoundString, toPlay);
    }

    public static void playLoopingMusicSound(Entity e, String sound, float volume, float frequency, String uniqueSoundString) {
        LoopingSound toPlay = new LoopingSound(e, new ResourceLocation(uniqueSoundString), volume, frequency);
        if (currentlyPlayingMusic != null) {
            currentlyPlayingMusic.lastTickSinceUpdate++;
            if (!currentlyPlayingMusic.isDonePlaying() && currentlyPlayingMusic.lastTickSinceUpdate < 60) return;
            else currentlyPlayingMusic = null;
        }
        currentlyPlayingMusic = toPlay;
        mc.getSoundHandler().playSound(toPlay);
    }

    public static void stopPlayingLoopingSound(String uniqueSoundString) {
        if (loopingSounds.containsKey(uniqueSoundString)) {
            loopingSounds.get(uniqueSoundString).done = true;
            loopingSounds.remove(uniqueSoundString);
        }
    }

    private static void playUniqueLoopingSound(String uniqueSoundString, LoopingSound toPlay) {
        if (loopingSounds.containsKey(uniqueSoundString)) {
            if (!loopingSounds.get(uniqueSoundString).isDonePlaying()) return;
            else loopingSounds.remove(uniqueSoundString);
        }
        loopingSounds.put(uniqueSoundString, toPlay);
        mc.getSoundHandler().playSound(toPlay);
    }
}
