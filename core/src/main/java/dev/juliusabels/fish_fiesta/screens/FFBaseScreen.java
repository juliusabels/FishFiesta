package dev.juliusabels.fish_fiesta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.util.ResourceHandler;

/**
 * Base screen implementation for Fish Fiesta game screens.
 * <p>
 * This class provides common functionality used across all game screens:
 * <ul>
 *   <li>A repeating background texture that fills the entire screen</li>
 *   <li>A monitor-styled UI overlay with a content area for screen-specific elements</li>
 *   <li>Basic screen lifecycle management</li>
 * </ul>
 * <p>
 * The screen uses two separate viewports:
 * <ul>
 *   <li>backgroundViewport (ScreenViewport): Handles the tiled background rendering</li>
 *   <li>monitorViewport (FitViewport): Handles the monitor UI with fixed dimensions</li>
 * </ul>
 * <p>
 * Child screens should extend this class and add their specific UI elements to the
 * provided contentTable.
 */
public class  FFBaseScreen implements Screen {
    /** Width of the monitor UI in pixels - maintains consistent UI sizing */
    protected static final int MONITOR_WIDTH = 614;

    /** Height of the monitor UI in pixels - maintains consistent UI sizing */
    protected static final int MONITOR_HEIGHT = 543;

    /** Reference to the main game instance for accessing shared resources */
    protected final FishFiestaGame game;

    /** Sprite batch used for rendering the background and UI */
    protected final SpriteBatch batch;

    /** Texture for the repeating background pattern */
    protected Texture background;

    /** Region for the background texture to enable tiling */
    protected TextureRegion textureRegion;

    /** Viewport for the background that adapts to screen size */
    protected ScreenViewport backgroundViewport;

    /** Viewport for the monitor UI with fixed aspect ratio */
    protected FitViewport monitorViewport;

    /** Stage for the monitor UI components */
    protected Stage stage;

    /** Skin containing styles for the monitor UI elements */
    protected Skin monitorSkin;

    /** Table for screen-specific content that appears inside the monitor frame */
    protected Table contentTable;

    /**
     * Creates a new base screen with background and monitor UI.
     * <p>
     * Initializes the background texture and monitor UI components.
     * Sets up the scene2d stage and input processing.
     *
     * @param game The main game instance containing shared resources
     */
    public FFBaseScreen(FishFiestaGame game) {
        this.game = game;
        this.batch = new SpriteBatch();

        initializeBackground();
        initializeMonitorUI();
    }

    /**
     * Initializes the repeating background texture.
     * <p>
     * Retrieves the background texture from the resource handler,
     * configures it for repeating in both directions, and creates
     * a TextureRegion and viewport for rendering.
     */
    protected void initializeBackground() {
        // Get background texture from resource handler
        background = game.getResourceHandler().getBackgroundTexture();
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureRegion = new TextureRegion(background);

        // Create viewport for background
        backgroundViewport = new ScreenViewport();
    }

    /**
     * Initializes the monitor-styled UI.
     * <p>
     * Sets up the scene2d stage with a fixed aspect ratio viewport,
     * creates a root table with the monitor frame background,
     * and adds a content table for child screens to populate.
     * Also configures the input processor to handle UI interactions.
     */
    protected void initializeMonitorUI() {
        // Get monitor skin from resource handler
        monitorSkin = game.getResourceHandler().getMonitorSkin();

        // Create viewport and stage for the monitor UI
        monitorViewport = new FitViewport(MONITOR_WIDTH, MONITOR_HEIGHT);
        stage = new Stage(monitorViewport, batch);

        // Setup root table with monitor background
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(monitorSkin.getDrawable("monitor"));
        stage.addActor(root);

        // Add content table for child screens to populate
        contentTable = new Table();
        root.add(contentTable).expand().fill().pad(40);

        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the repeating background texture.
     * <p>
     * Calculates the visible tiles based on the current camera position
     * and screen dimensions, then draws each visible tile to create a
     * seamless tiling effect that covers the entire visible area.
     */
    protected void renderBackground() {
        backgroundViewport.apply();
        batch.setProjectionMatrix(backgroundViewport.getCamera().combined);
        batch.begin();

        float tileWidth = background.getWidth();
        float tileHeight = background.getHeight();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Get camera position
        float camX = backgroundViewport.getCamera().position.x;
        float camY = backgroundViewport.getCamera().position.y;

        // Calculate visible tile range with buffer
        int startX = (int)Math.floor((camX - screenWidth/2) / tileWidth) - 1;
        int startY = (int)Math.floor((camY - screenHeight/2) / tileHeight) - 1;
        int endX = (int)Math.ceil((camX + screenWidth/2) / tileWidth) + 1;
        int endY = (int)Math.ceil((camY + screenHeight/2) / tileHeight) + 1;

        // Draw visible tiles
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                batch.draw(textureRegion, x * tileWidth, y * tileHeight, tileWidth, tileHeight);
            }
        }

        batch.end();
    }

    /**
     * Main render method called every frame.
     * <p>
     * Clears the screen, renders the tiled background,
     * and then draws the monitor UI on top.
     *
     * @param delta Time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(Color.BLACK);

        // Render repeating background
        renderBackground();

        //Render Monitor UI
        renderMonitorUI(delta);
    }

    /**
     * Renders the monitor UI overlay.
     * <p>
     * Applies the monitor viewport, updates the stage actors,
     * and draws the stage.
     *
     * @param delta Time in seconds since the last frame
     */
    private void renderMonitorUI(float delta) {
        monitorViewport.apply();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Handles screen resize events.
     * <p>
     * Updates both viewports when the screen size changes.
     * The background viewport adapts to fill the screen, while
     * the monitor viewport maintains its aspect ratio.
     *
     * @param width The new screen width
     * @param height The new screen height
     */
    @Override
    public void resize(int width, int height) {
        backgroundViewport.update(width, height);
        monitorViewport.update(width, height, true);
    }

    /**
     * Disposes of resources when the screen is no longer needed.
     * <p>
     * Releases the SpriteBatch and Stage to prevent memory leaks.
     * Child classes should override this method to dispose of
     * additional resources, making sure to call super.dispose().
     */
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    /**
     * Child classes should override this method to initialize
     * any screen-specific resources or state.
     */
    @Override
    public void show() {
        // To be implemented by child classes
    }

    /**
     * Called when the game is paused.
     * <p>
     * Child classes should override this method to handle
     * pausing game-specific logic or animations.
     */
    @Override
    public void pause() {
        // To be implemented by child classes
    }

    /**
     * Called when the game resumes from a paused state.
     * <p>
     * Child classes should override this method to resume
     * any paused game logic or animations.
     */
    @Override
    public void resume() {
        // To be implemented by child classes
    }

    /**
     * Called when this screen is no longer the current screen.
     * <p>
     * Child classes should override this method to pause or stop
     * any ongoing processes specific to the screen.
     */
    @Override
    public void hide() {
        // To be implemented by child classes
    }
}
