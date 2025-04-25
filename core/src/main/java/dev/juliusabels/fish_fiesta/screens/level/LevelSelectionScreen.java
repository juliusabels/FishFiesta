package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.MainMenuScreen;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;

public class LevelSelectionScreen extends FFBaseScreen {
    private DialogOverlay exitDialog;

    public LevelSelectionScreen(FishFiestaGame game) {
        super(game);
        exitDialog = new DialogOverlay(game, stage);
    }

    @Override
    public void show() {
        super.show();
    }

    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Continue") {
                @Override
                public void run() {
                    //Do nothing
                }
            },
            new DialogButton("Exit") {
                @Override
                public void run() {
                    //TODO safe game state here later
                    game.setScreen(new MainMenuScreen(game));
                }
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
