package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import am2.AMClientEventHandler;

import net.minecraft.util.ResourceLocation;

public class AnimationState {
    private double frame;
    private boolean lock;
    private boolean repeat;
    private ResourceLocation resource;

    public AnimationState() {
        frame = 0.0d;
    }

    public void setAnimation(ResourceLocation resource) {
        this.setResource(resource);
        frame = 0.0d;
    }

    public double getFrame() {
        return frame;
    }

    public boolean addFrame() {
        if (!lock) {
            frame += 0.05d;
            if (AMClientEventHandler.getModelManager().getModel(getResource())
                    .getAnimationLength() <= frame) {
                setLockFrame(lock);
                return false;
            }
        }
        return true;
    }

    public boolean isLockFrame() {
        return lock;
    }

    public void setLockFrame(boolean lock) {
        this.lock = lock;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setFrame(float newFrame) {
        this.frame = newFrame/20;
    } // 20 per second (+=0.05d)

    public ResourceLocation getResource() {
        return resource;
    }

    public void setResource(ResourceLocation resource) {
        this.resource = resource;
    }

    public void render() {
        AMClientEventHandler.getModelManager().getModel(getResource())
                .renderAnimationAll(frame);
    }
}