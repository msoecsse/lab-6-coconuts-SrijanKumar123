package coconuts;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// an object in the game, either something coming from the island or falling on it
// Each island object has a location and can determine if it hits another island object
// This is a domain class; do not introduce JavaFX or other GUI components here
public abstract class IslandObject {
    protected final int width;
    protected final OhCoconutsGameManager containingGame;
    protected int x, y;
    ImageView imageView = null;
    private static final int HIT_EPS = 8;

    public IslandObject(OhCoconutsGameManager game, int x, int y, int width, Image image) {
        containingGame = game;
        if (image != null) {
            imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(width);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        display();
        //System.out.println(this + " left " + left() + " right " + right());
    }

    protected ImageView getImageView() {
        return imageView;
    }

    public void display() {
        if (imageView != null) {
            imageView.setLayoutX(x);
            imageView.setLayoutY(y);
        }
    }

    public boolean isHittable() {
        return false;
    }

    protected int hittable_height() {
        return 0;
    }

    public boolean isGroundObject() {
        return false;
    }

    public boolean isFalling() {
        return false;
    }

    public boolean canHit(IslandObject other) {
        return false;
    }

    public boolean isTouching(IslandObject other) {
        int thisY = this.isFalling() ? this.getBottomY() : this.getTopY();
        int otherY = other.isFalling() ? other.getBottomY() : other.getTopY();

        // y “match” with tolerance
        boolean yAligned = Math.abs(thisY - otherY) <= HIT_EPS;

        // x overlap: center of one within left..right of the other
        int thisCenterX = this.getCenterX();
        int otherLeftX  = other.getLeftX();
        int otherRightX = other.getRightX();
        boolean xOverlap = (thisCenterX >= otherLeftX && thisCenterX <= otherRightX)
                || (other.getCenterX() >= this.getLeftX() && other.getCenterX() <= this.getRightX());

        return yAligned && xOverlap;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getTopY() {
        return y;
    }

    /** Returns the bottom Y coordinate of this object */
    public int getBottomY() {
        return y + width;
    }

    /** Returns the left X coordinate of this object */
    public int getLeftX() {
        return x;
    }

    /** Returns the right X coordinate of this object */
    public int getRightX() {
        return x + width;
    }

    /** Returns the center X coordinate of this object */
    public int getCenterX() {
        return x + width / 2;
    }

    public boolean isCrab() { return false; }
    public boolean isCoconut() { return false; }
    public boolean isLaser() { return false; }

    public abstract void step();
}
