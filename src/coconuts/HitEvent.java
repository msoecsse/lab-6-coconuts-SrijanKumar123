package coconuts;

import javafx.scene.image.Image;

import java.util.Collection;
import java.util.LinkedList;

// An abstraction of all objects that can be hit by another object
// This captures the Subject side of the Observer pattern; observers of the hit event will take action
//   to process that event
// This is a domain class; do not introduce JavaFX or other GUI components here
public class HitEvent {
    private final HitEventType type;
    private final IslandObject hitter;
    private final IslandObject target;
    public HitEvent(HitEventType type, IslandObject hitter, IslandObject target) {
        this.type = type; this.hitter = hitter; this.target = target;
    }
    public HitEventType getType() { return type; }
    public IslandObject getHitter() { return hitter; }
    public IslandObject getTarget() { return target; }
}



