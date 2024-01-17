package am2.illeffect;

import am2.api.illeffect.BadThingTypes;
import am2.api.illeffect.IIllEffect;
import am2.api.illeffect.IllEffectSeverity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.*;

public class IllEffectsManager{
	private TreeMap<IIllEffect, Integer> DarkNexusBadThings;
	private TreeMap<IIllEffect, Integer> AllBadThings;
	public static IllEffectsManager instance = new IllEffectsManager();
	private static Random rand = new Random();

	private IllEffectsManager(){
		DarkNexusBadThings = new TreeMap<IIllEffect, Integer>();
		AllBadThings = new TreeMap<IIllEffect, Integer>();

		RegisterIllEffect(new IllEffectDrainMana(), 15, BadThingTypes.DARKNEXUS);
		RegisterIllEffect(new IllEffectSpark(), 10, BadThingTypes.DARKNEXUS);
		RegisterIllEffect(new IllEffectSparkStorm(), 2, BadThingTypes.DARKNEXUS);

		RegisterIllEffect(new IllEffectDrainMana(), 16, BadThingTypes.ALL);
		RegisterIllEffect(new IllEffectSpark(), 13, BadThingTypes.ALL);
		RegisterIllEffect(new IllEffectSpawnDarkling(), 11, BadThingTypes.ALL);
		RegisterIllEffect(new IllEffectExplode(), 5, BadThingTypes.ALL);
		RegisterIllEffect(new IllEffectSparkStorm(), 2, BadThingTypes.ALL);
	}

	public void ApplyRandomBadThing(TileEntity te, IllEffectSeverity maxSev, BadThingTypes type){
		HashMap<IIllEffect, Integer> possibleBadThings = new HashMap<IIllEffect, Integer>();

		switch (type){
		case ALL:
			for (IIllEffect badThing : AllBadThings.keySet()){
				if (badThing.GetSeverity().ordinal() <= maxSev.ordinal()){
					possibleBadThings.put(badThing, AllBadThings.get(badThing));
				}
			}
			break;
		case DARKNEXUS:
			for (IIllEffect badThing : DarkNexusBadThings.keySet()){
				if (badThing.GetSeverity().ordinal() <= maxSev.ordinal()){
					possibleBadThings.put(badThing, DarkNexusBadThings.get(badThing));
				}
			}
			break;
		default:
			break;
		}
		//nothing found...lucky you!
		if (possibleBadThings.size() == 0){
			return;
		}

		IIllEffect chosenBadThing = getWeightedBadthing(possibleBadThings);
		Map<EntityPlayer, Object> affected = chosenBadThing.ApplyIllEffect(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
	}

	private IIllEffect getWeightedBadthing(HashMap<IIllEffect, Integer> possibilities){

		int totalWeight = 0;

		for (IIllEffect effect : possibilities.keySet()){
			totalWeight += possibilities.get(effect);
		}

		int tWeight = rand.nextInt(totalWeight);

		for (IIllEffect effect : possibilities.keySet()){
			tWeight -= possibilities.get(effect);
			if (tWeight <= 0){
				return effect;
			}
		}

		return possibilities.keySet().iterator().next();
	}

	@Deprecated
	public static void RegisterIllEffect(IIllEffect effect, BadThingTypes type){
		RegisterIllEffect(effect, 10, type);
	}

	public static void RegisterIllEffect(IIllEffect effect, int weight, BadThingTypes type){
		switch (type){
		case ALL:
			instance.AllBadThings.put(effect, weight);
			instance.AllBadThings = entriesSortedByValues(instance.AllBadThings);
			break;
		case DARKNEXUS:
		default:
			instance.DarkNexusBadThings.put(effect, weight);
			instance.DarkNexusBadThings = entriesSortedByValues(instance.DarkNexusBadThings);
			break;
		}
	}

	static <K, V extends Comparable<? super V>> TreeMap<K, V> entriesSortedByValues(Map<K, V> map){
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>(){
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2){
						return e1.getValue().compareTo(e2.getValue());
					}
				}
		);
		TreeMap<K, V> tm = new TreeMap<K, V>();
		for (Map.Entry<K, V> entry : map.entrySet()){
			tm.put(entry.getKey(), entry.getValue());
		}
//		sortedEntries.addAll(map.entrySet());
		return tm;
	}
}
