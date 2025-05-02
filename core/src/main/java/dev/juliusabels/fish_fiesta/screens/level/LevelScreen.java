package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;

public class LevelScreen extends FFBaseScreen {
    private final DialogOverlay exitDialog;
    private Level currentLevel;
    private final BitmapFont basicTextFont;

    public LevelScreen(FishFiestaGame game, Level currentLevel) {
        super(game);
        this.currentLevel = currentLevel;
        exitDialog = new DialogOverlay(game, stage);
        basicTextFont = new BitmapFont();
        basicTextFont.getData().setScale(0.7F);
    }

    public void show() {
        super.show();

        contentTable.clear();




        Table guestListWindow = new Table();
        guestListWindow.background(this.monitorSkin.getDrawable("guestlist-window"));

        Table conditions = new Table();
        conditions.setFillParent(true);
        this.currentLevel.getConditions().forEach((type, values) -> {
            if (type == ConditionType.SIZE && !values.isEmpty()) {
                Table size = new Table();
                size.left();
                Label label = new Label("Fish Size: ", new Label.LabelStyle(basicTextFont, Color.BLACK));
                size.add(label).padTop(20).left().padLeft(5);
                for (String value : values) {
                    Image image = new Image(this.monitorSkin.getDrawable("fish_size-" + value.toLowerCase()));
                    size.add(image).padTop(20).space(5).left();
                }
                conditions.add(size).left().expandX().row();
            }


            /*
            String typeName = type.name() + ": ";
            StringBuilder builder = new StringBuilder();
            int valuesSize = values.size() - 1;
            for (int i = 0; i <= valuesSize; i++) {
                builder.append(values.get(i));
                if (i != valuesSize) {
                    builder.append(", ");
                }
            }
            Label label = new Label(typeName + builder, new Label.LabelStyle(font, Color.BLACK));
            conditions.add(label).row();
             */
        });
        guestListWindow.add(conditions);

        contentTable.add(guestListWindow).expand().right().padRight(20).padBottom(130);
    }

    //TODO For level screen we want "continue playing", "exit to level selection", "safe level state", "restart level"
    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Continue") {
                @Override
                public void run() {
                    //Do nothing
                }
            },
            new DialogButton("Safe") {
                @Override
                public void run() {
                    //TODO safe game state here later
                }
            },
            new DialogButton("Exit") {
                @Override
                public void run() {
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
        basicTextFont.dispose();
    }
}
