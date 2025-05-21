package dev.juliusabels.fish_fiesta.screens.overlay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a button in dialog overlays within the game.
 * <p>
 * DialogButton is an abstract class that implements Runnable, allowing each button
 * to define custom behavior when clicked. It provides a standardized way to create
 * interactive buttons for dialog overlays throughout the game.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 *    new DialogButton("Save") {
 *      @Override
 *      public void run() {
 *         // Action to perform when this button is clicked
 *         saveGameState();
 *      }
 *    }
 * }
 * </pre>
 */
@Getter
@AllArgsConstructor
public abstract class DialogButton implements Runnable {
    /**
     * The text displayed on the button.
     */
    private String name;
}
