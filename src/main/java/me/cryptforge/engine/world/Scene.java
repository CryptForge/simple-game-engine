package me.cryptforge.engine.world;

import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.Renderer;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Scene {

    private final Map<UUID, GameObject> objects = new HashMap<>();

    public abstract void init();

    public abstract void terminate();

    public void update() {
        objects.values().forEach(GameObject::update);
    }

    public void render(Renderer renderer) {
        final Map<Texture,List<GameObject>> grouped = objects.values().stream().collect(Collectors.groupingBy(GameObject::texture));

        for (Map.Entry<Texture, List<GameObject>> entry : grouped.entrySet()) {
            final Texture texture = entry.getKey();
            final List<GameObject> objects = entry.getValue();
            renderer.spriteBatch(texture, batch -> {
                for (GameObject object : objects) {
                    batch.drawSprite(object.transform().matrix(), object.color());
                }
            });
        }
    }

    public void addGameObject(GameObject gameObject) {
        if(objects.containsKey(gameObject.uuid())) {
            throw new RuntimeException("Object already exists in scene!");
        }
        objects.put(gameObject.uuid(),gameObject);
    }

    public void removeGameObject(UUID uuid) {
        if(!objects.containsKey(uuid)) {
            throw new RuntimeException("Cannot remove object with uuid '" + uuid + "' because it doesn't exist in the scene!");
        }
        objects.remove(uuid);
    }

    public void removeGameObject(GameObject gameObject) {
        removeGameObject(gameObject.uuid());
    }

    public Collection<GameObject> gameObjects() {
        return Collections.unmodifiableCollection(objects.values());
    }

}
