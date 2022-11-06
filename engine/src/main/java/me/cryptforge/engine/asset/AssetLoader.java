package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;

abstract class AssetLoader<T,D> {

    abstract protected @Nullable T get(String id);
    abstract protected @NotNull T load(String id, AssetPathType pathType, String path, D data) throws FileNotFoundException;

}
