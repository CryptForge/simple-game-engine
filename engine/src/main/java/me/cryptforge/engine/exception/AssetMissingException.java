package me.cryptforge.engine.exception;

import me.cryptforge.engine.asset.Asset;

public class AssetMissingException extends RuntimeException {

    public AssetMissingException(Asset asset) {
        super(asset.type() + " asset missing \"" + asset.path() + "\"");
    }
}
