package screen;

import java.awt.Insets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import engine.*;

/**
 * Implements a generic screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public abstract class Screen {

    /**
     * Milliseconds until the screen accepts user input.
     */
    private static final int INPUT_DELAY = 1000;

    /**
     * Draw Manager instance.
     */
    protected DrawManager drawManager;
    /**
     * Input Manager instance.
     */
    protected InputManager inputManager;
    /**
     * Application logger.
     */
    protected Logger logger;
    /**
     * Screen insets.
     */
    protected Insets insets;
    /**
     * Time until the screen accepts user input.
     */
    protected Cooldown inputDelay;

    /**
     * If the screen is running.
     */
    protected boolean isRunning;
    /**
     * What kind of screen goes next.
     */
    protected ScreenType nextScreenTpe;

    /**
     * Constructor, establishes the properties of the screen.
     */
    public Screen() {
        this.drawManager = Main.getDrawManager();
        this.inputManager = Main.getInputManager();
        this.logger = Main.getLogger();
        this.inputDelay = Main.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();
        this.nextScreenTpe = ScreenType.EndGame;
    }

    /**
     * Gets the screen's type
     *
     * @return The screen's type
     */
    public abstract ScreenType getScreenType();

    /**
     * Initializes basic screen properties.
     */
    public void initialize() {

    }

    /**
     * Show this screen
     */
    public void show() {
        Main.getFrame().setScreen(this);
    }

    /**
     * Get the next screen
     *
     * @return The next screen
     */
    public ScreenType getNextScreenType() {
        return this.nextScreenTpe;
    }

    /**
     * Activates the screen.
     *
     * @return Next screen code.
     */
    public ScreenType run() {
        this.isRunning = true;

        while (this.isRunning) {
            long time = System.currentTimeMillis();

            update();

            time = (1000 / Main.FPS) - (System.currentTimeMillis() - time);
            if (time > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(time);
                } catch (InterruptedException e) {
                    return ScreenType.EndGame;
                }
            }
        }

        return ScreenType.EndGame;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected void update() {
    }

    /**
     * Getter for screen width.
     *
     * @return Screen width.
     */
    public final int getWidth() {
        return Main.getFrame().getWidth();
    }

    /**
     * Getter for screen height.
     *
     * @return Screen height.
     */
    public final int getHeight() {
        return Main.getFrame().getHeight();
    }
}