package dev.juliusabels.fish_fiesta;

import com.badlogic.gdx.Game;
import dev.juliusabels.fish_fiesta.screens.LoadingScreen;
import dev.juliusabels.fish_fiesta.util.ResourceHandler;
import lombok.Getter;

import java.util.logging.Level;

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
