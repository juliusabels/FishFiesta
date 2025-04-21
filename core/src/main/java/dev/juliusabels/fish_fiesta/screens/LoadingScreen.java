package dev.juliusabels.fish_fiesta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.util.ResourceHandler;

/**
 * Loading screen that displays a progress bar while game assets are loaded.
 * <p>
 * This screen is shown when the game first starts and is responsible for:
 * <ul>
 *   <li>Loading its own minimal assets immediately (progress bar visuals)</li>
 *   <li>Initiating and monitoring the loading of all other game assets</li>
 *   <li>Displaying a visual progress indicator to the user</li>
 *   <li>Transitioning to the main menu once loading completes</li>
 * </ul>
 * <p>
 * The loading screen does not extend FFBaseScreen since it needs to display
 * before the base screen's resources are available.
 */
public class LoadingScreen implements Screen {
    /** Reference to the main game instance */
    private final FishFiestaGame game;

    /** Sprite batch for rendering loading visuals */
    private final SpriteBatch batch;

    /** Background texture for the loading bar */
    private Texture loadingBarBg;

    /** Foreground texture that fills based on loading progress */
    private Texture loadingBarFill;

    /** Texture for "Loading" text display */
    private Texture loadingText;

    /** Width of the loading bar in pixels */
    private float barWidth;

    /** Height of the loading bar in pixels */
    private float barHeight;

    /** X-coordinate for the loading bar position */
    private float barX;

    /** Y-coordinate for the loading bar position */
    private float barY;

    /**
     * Creates a new loading screen.
     *
     * @param game The main game instance
     */
    public LoadingScreen(FishFiestaGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
    }

    /**
     * Called when this screen becomes the currently active screen.
     * <p>
     * Loads the minimal assets needed for the loading screen itself,
     * then initiates loading of all other game assets.
     */
    @Override
    public void show() {
        // Load loading bar textures immediately (before other assets)
        loadingBarBg = new Texture(ResourceHandler.assetFile("loading/loading_bar_bg.png"));
        loadingBarFill = new Texture(ResourceHandler.assetFile("loading/loading_bar_fill.png"));
        loadingText = new Texture(ResourceHandler.assetFile("loading/loading_text.png"));

        // Calculate loading bar position and dimensions
        barWidth = Gdx.graphics.getWidth() * 0.6f; // 60% of screen width
        barHeight = 45;
        barX = (Gdx.graphics.getWidth() - barWidth) / 2;
        barY = Gdx.graphics.getHeight() / 2F - barHeight / 2F;

        // Start loading other assets
        this.game.getResourceHandler().loadAssets();
    }

    /**
     * Renders the loading screen and updates the loading progress.
     * <p>
     * Displays the loading bar with a fill level representing current progress.
     * When loading completes, transitions to the main menu screen.
     *
     * @param delta Time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(Color.BLACK);

        // Update loading progress
        boolean finished = this.game.getResourceHandler().updateLoading();
        float progress = this.game.getResourceHandler().getLoadingProgress();

        // Render loading bar
        batch.begin();
        // Draw background
        batch.draw(loadingBarBg, barX, barY, barWidth, barHeight);
        // Draw fill based on progress
        batch.draw(loadingBarFill, barX, barY, barWidth * progress, barHeight);
        // Draw loading text
        batch.draw(loadingText, barX + 10, barY + 50, 224, 34);
        batch.end();

        // If loading is complete, transition to the main game screen
        if (finished) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}


    @Override
    public void hide() {}

    /**
     * Disposes of resources when the screen is no longer needed.
     * <p>
     * Releases all textures and the SpriteBatch to prevent memory leaks.
     */
    @Override
    public void dispose() {
        batch.dispose();
        loadingBarBg.dispose();
        loadingBarFill.dispose();
        loadingText.dispose();
    }
}
