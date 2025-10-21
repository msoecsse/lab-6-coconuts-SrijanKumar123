package coconuts;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import static coconuts.HitEventType.*;

public class ScoreBoard extends HBox implements Observer {
    private HBox hBox;
    private int destroyedCount = 0;
    private int beachCount = 0;
    private boolean crabisAlive = true;
    private int shotsFired = 0;
    private int time = 0;
    private int seconds = 0;
    private String gameState = "Ready";

    private final Label time1 = new Label();
    private final Label destroyed1 = new Label();
    private final Label beachCount1 = new Label();
    private final Label shots1 = new Label();
    private final Label crab1 = new Label();
    private final Label stateLbl = new Label();

    public ScoreBoard() {
        setSpacing(16);
        setPadding(new Insets(6,12,6,12));
        setMouseTransparent(true);
        getStyleClass().add("scoreboard");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(time1,destroyed1,beachCount1,shots1,crab1);
        refresh();
    }

    public void update(HitEvent e) {
        switch (e.getType()){
            case LASER_HIT ->destroyedCount++;
            case BEACH_HIT -> beachCount++;
            case CRAB_HIT -> {
                crabisAlive = false;
                gameState = "Game Over";
            }
            case SHOT_FIRED -> shotsFired++;
            case TICK -> seconds++;
            case GAME_STARTED -> gameState = "Running";
            case GAME_PAUSED -> gameState = "Paused";
            case GAME_RESUMED -> gameState = "Running";
            case GAME_OVER -> gameState = "Game Over";
        }
        refresh();
    }

    private void refresh() {
        time1.setText("Time: " + fmtTime(seconds));
        shots1.setText("Shots: " + shotsFired);
        destroyed1.setText("Destroyed: " + destroyedCount);
        beachCount1.setText("Beach: " + beachCount);
        crab1.setText("Crab: " + (crabisAlive ? "Alive" : "Dead"));
        stateLbl.setText(gameState);
    }

    private static String fmtTime(int secs) {
        int m = secs / 60, s = secs % 60;
        return m + ":" + (s < 10 ? "0" : "") + s;
    }
}
