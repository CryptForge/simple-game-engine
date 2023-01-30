package me.cryptforge.engine.ui;

import me.cryptforge.engine.ui.component.Component;

import java.util.Collection;

public interface ComponentBundle extends Component {

    Collection<Component> components();

    void add(Component component);

    boolean remove(Component component);

    @Override
    default void registerInput() {
        for (Component component : components()) {
            component.registerInput();
        }
    }

    @Override
    default void unregisterInput() {
        for (Component component : components()) {
            component.unregisterInput();
        }
    }

}
