package me.cryptforge.engine.asset.loader;

import me.cryptforge.engine.asset.Asset;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

@ApiStatus.Internal
abstract class AssetLoader<T,D> {

    public abstract @NotNull T load(Asset asset, D data) throws FileNotFoundException;

}
