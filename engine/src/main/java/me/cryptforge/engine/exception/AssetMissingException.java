package me.cryptforge.engine.exception;

import me.cryptforge.engine.asset.AssetPathType;

public class AssetMissingException extends RuntimeException {

    public AssetMissingException(AssetPathType pathType,String path) {
        super(pathType + " asset missing \"" + path + "\"");
    }
}
