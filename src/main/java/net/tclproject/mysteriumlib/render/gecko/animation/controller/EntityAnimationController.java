package net.tclproject.mysteriumlib.render.gecko.animation.controller;

import net.minecraft.client.audio.SoundList;
import net.minecraft.entity.Entity;
import net.tclproject.mysteriumlib.render.gecko.GeckoLib;
import net.tclproject.mysteriumlib.render.gecko.animation.AnimationState;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.Animation;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;
import net.tclproject.mysteriumlib.render.gecko.easing.EasingType;
import net.tclproject.mysteriumlib.render.gecko.entity.IAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.util.AnimationUtils;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityAnimationController<T extends Entity & IAnimatedEntity> extends AnimationController<T>
{
	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	private IEntityAnimationPredicate<T> animationPredicate;

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 */
	@FunctionalInterface
	public interface IEntityAnimationPredicate<E extends Entity>
	{
		<E extends Entity> boolean test(AnimationTestEvent<E> event);
	}


	public EntityAnimationController(T entity, String name, float transitionLengthTicks, IEntityAnimationPredicate<T> animationPredicate)
	{
		super(entity, name, transitionLengthTicks);
		this.animationPredicate = animationPredicate;
		this.soundPlayer = this::playSound;
	}

	public EntityAnimationController(T entity, String name, float transitionLengthTicks, IEntityAnimationPredicate<T> animationPredicate, EasingType easingtype)
	{
		super(entity, name, transitionLengthTicks, easingtype);
		this.animationPredicate = animationPredicate;
	}

	public EntityAnimationController(T entity, String name, float transitionLengthTicks, IEntityAnimationPredicate<T> animationPredicate, Function<Double, Double> customEasingMethod)
	{
		super(entity, name, transitionLengthTicks, customEasingMethod);
		this.animationPredicate = animationPredicate;
	}

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(@Nullable AnimationBuilder builder)
	{
		AnimatedEntityModel model = AnimationUtils.getModelForEntity(entity);
		if (model != null)
		{
			if (builder == null || builder.getRawAnimationList().size() == 0)
			{
				animationState = AnimationState.Stopped;
			}
			else if (!builder.getRawAnimationList().equals(currentAnimationBuilder.getRawAnimationList()) || needsAnimationReload)
			{
				AtomicBoolean encounteredError = new AtomicBoolean(false);
				// Convert the list of animation names to the actual list, keeping track of the loop boolean along the way
				LinkedList<Animation> animations = new LinkedList<>(
						builder.getRawAnimationList().stream().map((rawAnimation) ->
						{
							Animation animation = model.getAnimation(rawAnimation.animationName);
							if (animation == null)
							{
								GeckoLib.LOGGER.error(
										"Could not load animation: " + rawAnimation.animationName + ". Is it missing?");
								encounteredError.set(true);
							}
							if (animation != null && rawAnimation.loop != null)
							{
								animation.loop = rawAnimation.loop;
							}
							return animation;
						}).collect(Collectors.toList()));

				if(encounteredError.get())
				{
					return;
				}
				else {
					animationQueue = animations;
				}
				currentAnimationBuilder = builder;

				// Reset the adjusted tick to 0 on next animation process call
				shouldResetTick = true;
				this.animationState = AnimationState.Transitioning;
				justStartedTransition = true;
				needsAnimationReload = false;
			}
		}
	}

	@Override
	protected boolean testAnimationPredicate(AnimationTestEvent<T> event)
	{
		return this.animationPredicate.test(event);
	}

	public void playSound(String name)
	{
		entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, name, volume, pitch, distanceSoundDelay);
	}
}