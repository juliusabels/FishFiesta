package dev.juliusabels.fish_fiesta.screens.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.util.FishFontBig;

/**
 * Creates and manages dialog overlays for the game.
 * <p>
 * A dialog overlay displays as a semi-transparent panel that darkens the screen
 * and presents one or more dialog buttons to the user. When active, it disables
 * interaction with the underlying screen elements.
 * <p>
 * This class is used throughout the game for pause menus, confirmation dialogs,
 * and other modal interactions. Each button can have custom behavior defined via
 * the {@link DialogButton} class.
 */
public class DialogOverlay {
    /** Reference to the main game instance for accessing resources */
    private final FishFiestaGame game;

    /** The root table for the overlay that covers the entire screen */
    private final Table overlayTable;

    /** Custom font for dialog buttons */
    private final FishFontBig font;

    /** Reference to the active screen's content table to manage interaction state */
    private final Table activeScreenTable;

    /**
     * Creates a new dialog overlay.
     *
     * @param game The main game instance
     * @param stage The stage to which the overlay will be added
     * @param activeScreenTable The content table of the active screen
     */
    public DialogOverlay(FishFiestaGame game, Stage stage, Table activeScreenTable) {
        this.game = game;
        this.font = new FishFontBig(game);
        this.activeScreenTable = activeScreenTable;

        // Create overlay container
        overlayTable = new Table();
        overlayTable.setFillParent(true);
        overlayTable.align(Align.center);
        overlayTable.setVisible(false);

        // Set semi-transparent background
        overlayTable.setBackground(game.getResourceHandler().getMonitorSkin()
            .newDrawable("white", new Color(0, 0, 0, 0.2F)));

        stage.addActor(overlayTable);
    }

    /**
     * Displays the dialog overlay with the specified buttons.
     * <p>
     * Each button is created with consistent styling, and when clicked,
     * executes its associated action after the dialog is dismissed.
     * <p>
     * While the dialog is visible, interaction with the underlying screen
     * is disabled.
     *
     * @param buttons One or more dialog buttons to display in the overlay
     */
    public void showButtons(DialogButton... buttons) {
        // Disable interaction with the underlying screen
        this.activeScreenTable.setTouchable(Touchable.disabled);

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

        // Add dialog to overlay and display it
        overlayTable.clear();
        overlayTable.add(dialogBox).width(300).height(200);
        overlayTable.setVisible(true);
    }

    /**
     * Hides the dialog overlay and re-enables interaction with the main screen.
     */
    public void hide() {
        overlayTable.setVisible(false);
        this.activeScreenTable.setTouchable(Touchable.enabled);
    }

    /**
     * Checks if the dialog overlay is currently visible.
     *
     * @return true if the overlay is visible, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isVisible() {
        return overlayTable.isVisible();
    }

    /**
     * Disposes of resources when the overlay is no longer needed.
     * <p>
     * This method should be called when the screen containing this overlay
     * is disposed to prevent memory leaks.
     */
    public void dispose() {
        font.dispose();
    }
}
