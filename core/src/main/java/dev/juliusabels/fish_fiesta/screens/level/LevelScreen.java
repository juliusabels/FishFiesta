package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.MainMenuScreen;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;

public class LevelScreen extends FFBaseScreen {
    private DialogOverlay exitDialog;

    public LevelScreen(FishFiestaGame game) {
        super(game);
        exitDialog = new DialogOverlay(game, stage);
    }

    //TODO For level screen we want 'Continue', 'resign' (back to level selection), 'exit' (to main menu)
    private void showExitDialog() {
        exitDialog.show(
            // Continue action
            () -> {
                // Do nothing, continue playing
            },
            // Exit action
            () -> {
                //TODO safe game state here later
                game.setScreen(new LevelSelectionScreen(game));
            }
        );
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // If back key pressed, show dialog instead of immediate exit. If pressed again close dialog window again
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!exitDialog.isVisible()) {
                showExitDialog();
            } else {
                exitDialog.hide();
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        exitDialog.dispose();
    }
}
