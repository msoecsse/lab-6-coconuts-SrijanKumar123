package coconuts;

import javafx.scene.image.Image;

import java.util.Collection;
import java.util.LinkedList;

// An abstraction of all objects that can be hit by another object
// This captures the Subject side of the Observer pattern; observers of the hit event will take action
//   to process that event
// This is a domain class; do not introduce JavaFX or other GUI components here
public class HitEvent {
    private final IslandObject source;   // e.g., LaserBeam, Coconut
    private final IslandObject target;   // e.g., Coconut, Crab, Beach
    private final String eventType;      // "LASER_HIT", "COCONUT_HIT_BEACH", "CRAB_HIT", etc.

    public HitEvent(IslandObject source, IslandObject target, String eventType) {
        this.source = source;
        this.target = target;
        this.eventType = eventType;
    }

    public IslandObject getSource() {
        return source;
    }

    public IslandObject getTarget() {
        return target;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "HitEvent{" + eventType + ", src=" + safeName(source) + ", tgt=" + safeName(target) + "}";
    }

    private String safeName(Object o) {
        return (o == null) ? "null" : o.getClass().getSimpleName();
    }
}
