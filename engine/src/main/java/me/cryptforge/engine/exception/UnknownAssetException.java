package me.cryptforge.engine.exception;

public class UnknownAssetException extends RuntimeException {

    public UnknownAssetException(String assetType,String assetId) {
        super(assetType + " \"" + assetId + "\" not loaded");
    }
}
