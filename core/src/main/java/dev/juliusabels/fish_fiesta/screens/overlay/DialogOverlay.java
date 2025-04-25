package dev.juliusabels.fish_fiesta.screens.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.MainMenuScreen;
import dev.juliusabels.fish_fiesta.util.FishFontBig;

public class DialogOverlay {
    private final FishFiestaGame game;
    private final Table overlayTable;
    private final FishFontBig font;

    public DialogOverlay(FishFiestaGame game, Stage stage) {
        this.game = game;
        this.font = new FishFontBig(game);

        // Create overlay container
        overlayTable = new Table();
        overlayTable.setFillParent(true);
        overlayTable.align(Align.center);
        overlayTable.setVisible(false);

        // Set semi-transparent background
        overlayTable.setBackground(game.getResourceHandler().getMonitorSkin().newDrawable("white", new Color(0, 0, 0, 0.2F)));

        stage.addActor(overlayTable);
    }

    public void showButtons(DialogButton... buttons) {
        // Create dialog box
        Table dialogBox = new Table();
        dialogBox.pad(20);

        // Create buttons
        TextButton.TextButtonStyle buttonStyle = font.createButtonStyle(
            game.getResourceHandler().getMonitorSkin().getDrawable("menu_button"),
            game.getResourceHandler().getMonitorSkin().getDrawable("menu_button-pressed"),
            game.getResourceHandler().getMonitorSkin().getDrawable("menu_button-hovered"),
            2.0F
        );

        for (DialogButton button : buttons) {
            TextButton textButton = font.createButton(button.getName(), buttonStyle);
            textButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hide();
                    button.run();
                }
            });
            // Add button to dialog
            dialogBox.add(textButton).pad(10).fillX().row();
        }

        // Add dialog to overlay
        overlayTable.clear();
        overlayTable.add(dialogBox).width(300).height(200);
        overlayTable.setVisible(true);
    }

    public void hide() {
        overlayTable.setVisible(false);
    }

    public boolean isVisible() {
        return overlayTable.isVisible();
    }

    public void dispose() {
        font.dispose();
    }
}
