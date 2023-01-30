package me.cryptforge.engine.ui;

import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.ui.component.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FlexBundle implements ComponentBundle {

    private final List<Component> components;

    private FlexDirection direction = FlexDirection.ROW;
    private int gap = 0;
    private float width, height;

    public FlexBundle() {
        components = new ArrayList<>();
    }

    private FlexBundle(List<Component> components) {
        this.components = components;
    }

    public void setDirection(FlexDirection direction) {
        this.direction = direction;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    @Override
    public void add(Component component) {
        components.add(component);
    }

    @Override
    public boolean remove(Component component) {
        return components.remove(component);
    }

    @Override
    public Collection<Component> components() {
        return Collections.unmodifiableCollection(components);
    }

    @Override
    public void render(Renderer renderer, float x, float y) {
        recalculateSize();
        for (Component component : components) {
            component.render(renderer, x, y);
            final float componentWidth = component.width();
            final float componentHeight = component.height();
            if (direction == FlexDirection.ROW) {
                x += componentWidth + gap;
            } else {
                y += componentHeight + gap;
            }
        }
    }

    private void recalculateSize() {
        if (direction == FlexDirection.ROW) {
            width = (float) components.stream().mapToDouble(Component::width).sum();
            height = (float) components.stream().mapToDouble(Component::height).max().orElse(0);
        } else {
            height = (float) components.stream().mapToDouble(Component::height).sum();
            width = (float) components.stream().mapToDouble(Component::width).max().orElse(0);
        }
    }

    public static FlexBundle of(Component... components) {
        return new FlexBundle(new ArrayList<>(List.of(components)));
    }
}
