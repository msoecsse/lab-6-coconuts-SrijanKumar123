package coconuts;

import javafx.scene.image.Image;

// Represents the beam of light moving from the crab to a coconut; can hit only falling objects
// This is a domain class; do not introduce JavaFX or other GUI components here
public class LaserBeam extends IslandObject {
    private static final Image laserImage = new Image("file:images/laser-1.png");
    private static final int WIDTH  = 6;          // thickness on screen
    private static final int SPEED  = 12;

    public LaserBeam(OhCoconutsGameManager game, int eyeHeight, int crabCenterX) {
        super(game, crabCenterX, eyeHeight, WIDTH, laserImage);

        // ensure the image renders at the thickness we pass as WIDTH
        if (getImageView() != null) {
            getImageView().setPreserveRatio(false);
            getImageView().setFitWidth(WIDTH);
        }
    }

    // Laser is not a target; it hits others
    @Override
    public boolean isHittable() { return false; }

    // Laser does not fall
    @Override
    public boolean isFalling() { return false; }

    // What can this hit? Only coconuts (domain predicate, no instanceof)
    @Override
    public boolean canHit(IslandObject other) { return other.isCoconut(); }

    // Per spec, laser uses its TOP y when checking “touching”
    @Override
    protected int hittable_height() { return y; }

    @Override
    public void step() {
        // move the beam upward
        y -= SPEED;

        // if it goes off the top, schedule removal (manager will clean up)
        if (y < 0) {
            containingGame.scheduleForDeletion(this);
        }
    }

    @Override
    public boolean isLaser() { return true; }
}

