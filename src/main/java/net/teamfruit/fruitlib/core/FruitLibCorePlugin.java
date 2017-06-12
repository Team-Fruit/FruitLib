package net.teamfruit.fruitlib.core;

import java.util.Map;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.teamfruit.fruitlib.FruitLibClient;
import net.teamfruit.fruitlib.FruitLibCommon;
import net.teamfruit.fruitlib.loader.FruitLoader;
import net.teamfruit.fruitlib.loader.Log;
import net.teamfruit.lib.FruitClient;
import net.teamfruit.lib.FruitCommon;

public class FruitLibCorePlugin implements IFMLLoadingPlugin, IFMLCallHook {
	private final FruitLoader loader = new FruitLoader();

	public FruitLibCorePlugin() {
		FruitCommon.setFruit(new FruitLibCommon());
		FruitClient.setFruit(new FruitLibClient());
		try {
			this.loader.load();
		} catch (final Exception e) {
			Log.log.error("FruitLoader error: ", e);
		}
	}

	@Override
	public @Nullable String[] getASMTransformerClass() {
		return new String[] {
				FruitLibTransformer.class.getName(),
				FruitDelegatedTransformer.class.getName()
		};
	}

	@Override
	public @Nullable String getModContainerClass() {
		return null;
	}

	@Override
	public @Nullable String getSetupClass() {
		return getClass().getName();
	}

	@Override
	public void injectData(final @Nullable Map<String, Object> data) {
	}

	@Override
	public @Nullable String getAccessTransformerClass() {
		return null;
	}

	@Override
	public Void call() throws Exception {
		//		for (final Entry<String, File> transformer : this.loader.discoverer.transformers.entrySet())
		//			FruitTransformer.instance.addTransformerName(transformer.getKey(), transformer.getValue());

		return null;
	}
}