package me.cryptforge.engine.input;

public class InputModifiers {

    private final int mods;

    public InputModifiers(int mods) {
        this.mods = mods;
    }

    public boolean isShiftHeld() {
        return (mods & 0x0001) != 0;
    }

    public boolean isControlHeld() {
        return (mods & 0x0002) != 0;
    }

    public boolean isAltHeld() {
        return (mods & 0x0004) != 0;
    }

    public boolean isSuperHeld() {
        return (mods & 0x0008) != 0;
    }

    public boolean isCapslockEnabled() {
        return (mods & 0x0010) != 0;
    }

    public boolean isNumlockEnabled() {
        return (mods & 0x0020) != 0;
    }
}
