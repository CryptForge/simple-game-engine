package me.cryptforge.engine.render;

import static org.lwjgl.opengl.GL32.*;

public final class GLFence {

    private long fence;

    public int waitBuffer() {
        int timeWaited = 0;
        if (fence == 0) {
            return timeWaited;
        }
        int sync = GL_UNSIGNALED;
        while (sync != GL_ALREADY_SIGNALED && sync != GL_CONDITION_SATISFIED) {
            timeWaited++;
            sync = glClientWaitSync(fence, GL_SYNC_FLUSH_COMMANDS_BIT, 1);
        }
        return timeWaited;
    }

    public void lockBuffer() {
        if (fence != 0) {
            glDeleteSync(fence);
        }
        fence = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

}
