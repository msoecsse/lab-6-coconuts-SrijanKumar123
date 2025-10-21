package coconuts;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

// JavaFX Controller class for the game - generally, JavaFX elements (other than Image) should be here
public class GameController {

    /**
     * Time between calls to step() (ms)
     */
    private static final double MILLISECONDS_PER_STEP = 1000.0 / 30;
    private Timeline coconutTimeline;
    private boolean started = false;
    private int tickCounter = 0;
    private ScoreBoard board;

    @FXML
    private Pane gamePane;
    @FXML
    private Pane theBeach;
    private OhCoconutsGameManager theGame;

    @FXML
    public void initialize() {
        theGame = new OhCoconutsGameManager(
                (int) (gamePane.getPrefHeight() - theBeach.getPrefHeight()),
                (int) (gamePane.getPrefWidth()),
                gamePane
        );

        // Scoreboard as observer
        board = new ScoreBoard();
        theGame.attach(board);
        gamePane.getChildren().add(board);
        board.setLayoutX(0);
        board.setLayoutY(0);
        board.prefWidthProperty().bind(gamePane.widthProperty());

        // Build timeline (30 FPS)
        gamePane.setFocusTraversable(true);
        coconutTimeline = new Timeline(new KeyFrame(Duration.millis(MILLISECONDS_PER_STEP), e -> {
            theGame.tryDropCoconut();
            theGame.advanceOneTick();

            // emit TICK about once per second
            if (++tickCounter % 30 == 0) {
                theGame.notifyAllObservers(new HitEvent(HitEventType.TICK, null, null));
            }

            if (theGame.done()) {
                coconutTimeline.pause();
            }
        }));
        coconutTimeline.setCycleCount(Timeline.INDEFINITE);  // <-- important!

        javafx.application.Platform.runLater(() -> gamePane.requestFocus());
    }


    // PRESS: movement + shoot only
    @FXML
    public void onKeyPressed(KeyEvent e) {
        if (!theGame.done()) {
            if (e.getCode() == KeyCode.RIGHT) theGame.getCrab().crawl(10);
            else if (e.getCode() == KeyCode.LEFT) theGame.getCrab().crawl(-10);
            else if (e.getCode() == KeyCode.UP) theGame.fireLaserFromCrab();
        }
    }

    private void resetGame() {
        // remove all game sprites but keep the scoreboard node in place
        gamePane.getChildren().removeIf(n -> n != board);

        // rebuild the manager
        theGame = new OhCoconutsGameManager(
                (int) (gamePane.getPrefHeight() - theBeach.getPrefHeight()),
                (int) (gamePane.getPrefWidth()),
                gamePane);

        // reattach the scoreboard as an observer
        theGame.attach(board);

        // reset local counters (timer display etc.)
        tickCounter = 0;
    }

    // RELEASE: SPACE toggles once
    @FXML
    public void onKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.SPACE) {
            if (coconutTimeline.getStatus() == Timeline.Status.RUNNING) {
                coconutTimeline.pause();
            } else {
                // If last game is over, rebuild everything
                if (theGame.done()) {
                    resetGame();
                }
                coconutTimeline.play();
            }
        }
    }


}
