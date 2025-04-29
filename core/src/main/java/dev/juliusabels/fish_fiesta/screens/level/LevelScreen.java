package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.game.level.Level;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;

public class LevelScreen extends FFBaseScreen {
    private DialogOverlay exitDialog;
    private Level currentLevel;

    public LevelScreen(FishFiestaGame game, Level currentLevel) {
        super(game);
        this.currentLevel = currentLevel;
        exitDialog = new DialogOverlay(game, stage);
    }

    public void show() {
        super.show();

        contentTable.clear();

        Table conditionList = new Table();
        conditionList.background(this.monitorSkin.getDrawable("guestlist-window"));
        contentTable.add(conditionList).expand().right().padRight(20).padBottom(130);
    }

    //TODO For level screen we want continue playing, exit to level selection, safe level state, restart level
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
                    game.setScreen(new LevelSelectionScreen(game));
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
