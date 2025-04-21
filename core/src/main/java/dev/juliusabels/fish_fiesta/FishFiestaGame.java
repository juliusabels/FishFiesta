package dev.juliusabels.fish_fiesta;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import dev.juliusabels.fish_fiesta.screens.LoadingScreen;
import dev.juliusabels.fish_fiesta.util.ResourceHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * Main game class for Fish Fiesta.
 * Manages screens and provides access to shared resources.
 */
@Getter
public class FishFiestaGame extends Game {
    private ResourceHandler resourceHandler;

    @Override
    public void create() {
        // Initialize resource handler
        resourceHandler = new ResourceHandler();

        // Start with loading screen
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        // Dispose current screen
        if (screen != null) {
            screen.dispose();
        }

        // Dispose resource handler
        if (resourceHandler != null) {
            resourceHandler.dispose();
        }
    }
}
