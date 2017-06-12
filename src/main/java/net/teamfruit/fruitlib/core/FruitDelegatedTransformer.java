package net.teamfruit.fruitlib.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.teamfruit.lib.launch.FruitTransformer;

public class FruitDelegatedTransformer implements IClassTransformer {
	@Override
	public byte[] transform(final String name, final String tname, byte[] bytes) {
		if (bytes==null)
			return null;
		for (final IClassTransformer trans : FruitTransformer.instance.delegatedTransformers)
			bytes = trans.transform(name, tname, bytes);
		return bytes;
	}
}