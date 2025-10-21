package coconuts;

// https://stackoverflow.com/questions/42443148/how-to-correctly-separate-view-from-model-in-javafx

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// This class manages the game, including tracking all island objects and detecting when they hit
public class OhCoconutsGameManager implements Subject{
    private final List<Observer> observers = new ArrayList<>();
    private final Collection<IslandObject> allObjects = new LinkedList<>();
    private final Collection<HittableIslandObject> hittableIslandSubjects = new LinkedList<>();
    private final Collection<IslandObject> scheduledForRemoval = new LinkedList<>();
    private final int height, width;
    private final int DROP_INTERVAL = 10;
    private final int MAX_TIME = 100;
    private Pane gamePane;
    private Crab theCrab;
    private Beach theBeach;
    /* game play */
    private int coconutsInFlight = 0;
    private int gameTick = 0;

    public OhCoconutsGameManager(int height, int width, Pane gamePane) {
        this.height = height;
        this.width = width;
        this.gamePane = gamePane;

        this.theCrab = new Crab(this, height, width);
        registerObject(theCrab);
        gamePane.getChildren().add(theCrab.getImageView());

        this.theBeach = new Beach(this, height, width);
        registerObject(theBeach);
        if (theBeach.getImageView() != null)
            System.out.println("Unexpected image view for beach");
    }

    private void registerObject(IslandObject object) {
        allObjects.add(object);
        if (object.isHittable()) {
            HittableIslandObject asHittable = (HittableIslandObject) object;
            hittableIslandSubjects.add(asHittable);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void coconutDestroyed() {
        coconutsInFlight -= 1;
    }

    public void tryDropCoconut() {
        if (theCrab == null) return;
        if (gameTick % DROP_INTERVAL == 0) {
            coconutsInFlight++;
            Coconut c = new Coconut(this, (int)(Math.random()*width));
            registerObject(c);
            gamePane.getChildren().add(c.getImageView());
        }
        gameTick++;
    }

    public Crab getCrab() {
        return theCrab;
    }

    public void killCrab() {
        theCrab = null;
    }

    public void advanceOneTick() {
        for (IslandObject o : allObjects) {
            o.step();
            o.display();
        }
        // see if objects hit; the hit itself is something you will add
        // you can't change the lists while processing them, so collect
        //   items to be removed in the first pass and remove them later
        scheduledForRemoval.clear();
        for (IslandObject thisObj : allObjects) {
            for (HittableIslandObject hittable : hittableIslandSubjects) {
                if (thisObj == hittable) continue;

                if (thisObj.isTouching(hittable)) {
                    HitEventType type = null;

                    // 1) Coconut hits crab (thisObj is the crab, hittable is the coconut)
                    if (thisObj.isCrab() && hittable.isCoconut()) {
                        type = HitEventType.CRAB_HIT;

                        // 2) Coconut hits beach (thisObj is ground, hittable is the coconut)
                    } else if (thisObj.isGroundObject() && hittable.isCoconut()) {
                        type = HitEventType.BEACH_HIT;

                        // 3) Laser hits coconut (thisObj is laser, hittable is coconut)
                    } else if (thisObj.isLaser() && hittable.isCoconut()) {
                        type = HitEventType.LASER_HIT;
                    }

                    if (type == null) {
                        // nothing interesting about this contact—skip
                        continue;
                    }

                    // notify observers
                    notifyAllObservers(new HitEvent(type, thisObj, hittable));

                    // apply effects & schedule removals
                    switch (type) {
                        case LASER_HIT -> {
                            IslandObject coconut = hittable;             // the hittable is the coconut
                            scheduledForRemoval.add(coconut);
                            if (coconut.getImageView() != null) gamePane.getChildren().remove(coconut.getImageView());
                            coconutDestroyed();

                            IslandObject laser = thisObj;                // remove the laser too
                            scheduledForRemoval.add(laser);
                            if (laser.getImageView() != null) gamePane.getChildren().remove(laser.getImageView());
                        }
                        case BEACH_HIT -> {
                            IslandObject coconut = hittable;
                            scheduledForRemoval.add(coconut);
                            if (coconut.getImageView() != null) gamePane.getChildren().remove(coconut.getImageView());
                            coconutDestroyed();
                        }
                        case CRAB_HIT -> {
                            // remove coconut
                            IslandObject coconut = hittable;
                            scheduledForRemoval.add(coconut);
                            if (coconut.getImageView() != null) gamePane.getChildren().remove(coconut.getImageView());
                            coconutDestroyed();

                            // remove crab & stop the game from spawning more
                            removeCrabNow();                      // <-- add this helper (next section)
                            notifyAllObservers(new HitEvent(HitEventType.GAME_OVER, thisObj, hittable));
                        }
                    }
                }
            }
        }
    }

    private void removeCrabNow() {
        if (theCrab == null) return;
        if (theCrab.getImageView() != null) {
            gamePane.getChildren().remove(theCrab.getImageView());
        }
        scheduledForRemoval.add(theCrab);
        theCrab = null;
    }


        public void fireLaserFromCrab() {
        if (theCrab == null) return;

        int startY = (int) Math.round(theCrab.getY());
        int startX = (int) Math.round(theCrab.getX());
        // Match your LaserBeam constructor’s param order:
        LaserBeam laser = new LaserBeam(this, startY, startX);
        registerObject(laser);
        if (laser.getImageView() != null) gamePane.getChildren().add(laser.getImageView());

        notifyAllObservers(new HitEvent(HitEventType.SHOT_FIRED, theCrab, laser));
    }

    public void scheduleForDeletion(IslandObject islandObject) {
        scheduledForRemoval.add(islandObject);
    }

    public boolean done() {
        //return coconutsInFlight == 0 && gameTick >= MAX_TIME;
        boolean noCoconuts = coconutsInFlight == 0;
        boolean timeCapped = gameTick >= MAX_TIME;
        boolean crabDead   = (theCrab == null);
        return (timeCapped && noCoconuts) || (crabDead && noCoconuts);
    }

    @Override
    public void attach(Observer o) {
        if (!observers.contains(o)) observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers(HitEvent event) {
        for (Observer o : observers) {
            o.update(event);
        }

    }
}
