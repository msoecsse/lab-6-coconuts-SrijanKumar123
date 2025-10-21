package coconuts;

import javafx.scene.image.Image;

// Represents island objects which can be hit
// This is a domain class; do not introduce JavaFX or other GUI components here
public abstract class HittableIslandObject extends IslandObject {

    public HittableIslandObject(OhCoconutsGameManager game, int x, int y, int width, Image image) {
        super(game, x, y, width, image);
    }

    @Override
    public boolean isHittable() {
        return true;
    }

    /**
     * Called when something hits this object.
     * Default behavior: mark this object for deletion. Subclasses may override.
     */
    public void onHit(IslandObject hitter) {
        // 'game' should be a protected field or accessible via a getter in IslandObject.
        containingGame.scheduleForDeletion(this);
    }
}