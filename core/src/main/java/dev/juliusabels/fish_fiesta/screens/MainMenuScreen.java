package dev.juliusabels.fish_fiesta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.level.LevelSelectionScreen;
import dev.juliusabels.fish_fiesta.util.FishFontBig;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents the main menu screen of the game.
 * <p>
 * This screen provides the initial navigation options for the player after loading completes.
 * It displays two main buttons:
 * <ul>
 *   <li>Play: Transitions to the level selection screen</li>
 *   <li>Quit: Exits the application</li>
 * </ul>
 * <p>
 * In the future this screen would also hold a button to the credits screen
 */
@Slf4j
public class MainMenuScreen extends FFBaseScreen {

    /** Large fish-themed font used for menu text */
    private final FishFontBig font;

    /**
     * Creates a new main menu screen.
     *
     * @param game The main game instance
     */
    public MainMenuScreen(FishFiestaGame game) {
        super(game);
        font = new FishFontBig(game);
    }

    /**
     * Sets up the main menu UI when the screen becomes active.
     * <p>
     * Creates and positions the Play and Quit buttons with appropriate
     * styling and click listeners.
     */
    @Override
    public void show() {
        super.show();
        Table buttons = new Table();

        TextButton.TextButtonStyle style = font.createButtonStyle(
            this.monitorSkin.getDrawable("menu_button"),
            this.monitorSkin.getDrawable("menu_button-pressed"),
            this.monitorSkin.getDrawable("menu_button-hovered"),
            4.0F
        );

        // Create and configure Play button
        TextButton playButton = font.createButton("Play", style);
        playButton.getLabelCell().padBottom(5F);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectionScreen(game));
            }
        });
        buttons.add(playButton).top().pad(10).fillX();

        buttons.row().pad(40);

        // Create and configure Quit button
        TextButton quitButton = font.createButton("Quit", style);
        quitButton.getLabelCell().padBottom(5F);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit the application
                Gdx.app.exit();
            }
        });
        buttons.add(quitButton).pad(10).fillX();

        this.contentTable.add(buttons).center().padBottom(40F).expand();
    }

    /**
     * Disposes of resources when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }
}
