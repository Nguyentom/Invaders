package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.*;

/**
 * Implements the score screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class ScoreScreen extends Screen {

    /**
     * Milliseconds between changes in user selection.
     */
    private static final int SELECTION_TIME = 200;
    /**
     * Maximum number of high scores.
     */
    private static final int MAX_HIGH_SCORE_NUM = 7;
    /**
     * Code of first mayus character.
     */
    private static final int FIRST_CHAR = 65;
    /**
     * Code of last mayus character.
     */
    private static final int LAST_CHAR = 90;

    /**
     * Current score.
     */
    private int score;
    /**
     * Player lives left.
     */
    private int livesRemaining;
    /**
     * Total bullets shot by the player.
     */
    private int bulletsShot;
    /**
     * Total ships destroyed by the player.
     */
    private int shipsDestroyed;
    /**
     * List of past high scores.
     */
    private List<Score> highScores;
    /**
     * Checks if current score is a new high score.
     */
    private boolean isNewRecord;
    /**
     * Player name for record input.
     */
    private char[] name;
    /**
     * Character of players name selected for change.
     */
    private int nameCharSelected;
    /**
     * Time between changes in user selection.
     */
    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState Current game state.
     */
    public ScoreScreen(final GameState gameState) {
        super();

        this.score = gameState.getScore();
        this.livesRemaining = gameState.getLivesRemaining();
        this.bulletsShot = gameState.getBulletsShot();
        this.shipsDestroyed = gameState.getShipsDestroyed();
        this.isNewRecord = false;
        this.name = "AAA".toCharArray();
        this.nameCharSelected = 0;
        this.selectionCooldown = Main.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        try {
            this.highScores = Main.getFileManager().loadHighScores();
            if (highScores.size() < MAX_HIGH_SCORE_NUM
                    || highScores.get(highScores.size() - 1).getScore()
                    < this.score)
                this.isNewRecord = true;

        } catch (IOException e) {
            logger.warning("Couldn't load high scores!");
        }
    }

    /**
     * Gets the screen's type
     *
     * @return The screen's type
     */
    public ScreenType getScreenType() {
        return ScreenType.ScoreScreen;
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final ScreenType run() {
        Main.getLogger().info("Score info: "
                + this.score + ", "
                + this.livesRemaining + " lives remaining, "
                + this.bulletsShot + " bullets shot and "
                + this.shipsDestroyed + " ships destroyed.");

        super.run();

        return this.nextScreenTpe;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                // Return to main menu.
                this.nextScreenTpe = ScreenType.TitleScreen;
                this.isRunning = false;
                if (this.isNewRecord)
                    saveScore();
            } else if (inputManager.isSpaceKeyDown()) {
                // Play again.
                this.nextScreenTpe = ScreenType.GameScreen;
                this.isRunning = false;
                if (this.isNewRecord)
                    saveScore();
            }

            if (this.isNewRecord && this.selectionCooldown.checkFinished()) {
                if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    this.nameCharSelected = this.nameCharSelected == 2 ? 0
                            : this.nameCharSelected + 1;
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                    this.nameCharSelected = this.nameCharSelected == 0 ? 2
                            : this.nameCharSelected - 1;
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                    this.name[this.nameCharSelected] =
                            (char) (this.name[this.nameCharSelected]
                                    == LAST_CHAR ? FIRST_CHAR
                                    : this.name[this.nameCharSelected] + 1);
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                    this.name[this.nameCharSelected] =
                            (char) (this.name[this.nameCharSelected]
                                    == FIRST_CHAR ? LAST_CHAR
                                    : this.name[this.nameCharSelected] - 1);
                    this.selectionCooldown.reset();
                }
            }
        }

    }

    /**
     * Saves the score as a high score.
     */
    private void saveScore() {
        highScores.add(new Score(new String(this.name), score));
        Collections.sort(highScores);
        if (highScores.size() > MAX_HIGH_SCORE_NUM)
            highScores.remove(highScores.size() - 1);

        try {
            Main.getFileManager().saveHighScores(highScores);
        } catch (IOException e) {
            logger.warning("Couldn't load high scores!");
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
                this.isNewRecord);
        drawManager.drawResults(this, this.score, this.livesRemaining,
                this.shipsDestroyed, (float) this.shipsDestroyed
                        / this.bulletsShot, this.isNewRecord);

        if (this.isNewRecord)
            drawManager.drawNameInput(this, this.name, this.nameCharSelected);

        drawManager.completeDrawing(this);
    }
}
