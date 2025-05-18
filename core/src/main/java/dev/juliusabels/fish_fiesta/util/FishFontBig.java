package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import dev.juliusabels.fish_fiesta.FishFiestaGame;

/**
 * A utility class for drawing the big fish font
 */
public class FishFontBig {

    /**
     * The underlying bitmap font instance.
     */
    private final BitmapFont font;

    /**
     * Creates a new FishFontBig instance.
     *
     * @param game The game instance to retrieve font resources from
     */
    public FishFontBig(FishFiestaGame game) {
        this.font = game.getResourceHandler().getFishFontBig();
    }

    /**
     * Creates a label with the big fish font.
     *
     * @param text The text to display
     * @param scale The scale factor for the font
     * @return The created Label
     */
    public Label createLabel(String text, float scale) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();

        BitmapFont fontCopy = new BitmapFont(font.getData().getFontFile());
        fontCopy.getData().setScale(scale);
        labelStyle.font = fontCopy;
        labelStyle.fontColor = Color.WHITE;
        return new Label(text.toUpperCase(), labelStyle);
    }

    /**
     * Creates and returns a TextButton.TextButtonStyle with this font properly scaled.
     *
     * @param upDrawable The drawable for the button's up state
     * @param downDrawable The drawable for the button's down state
     * @param overDrawable The drawable for the button's over state
     * @param scale The scale to apply to the font
     * @param fontColor The color for normal state (optional)
     * @param overFontColor The color when hovered (optional)
     * @param downFontColor The color when pressed (optional)
     * @return A TextButton.TextButtonStyle with the properly scaled font
     */
    public TextButton.TextButtonStyle createButtonStyle(Drawable upDrawable, Drawable downDrawable, Drawable overDrawable, float scale, Color fontColor, Color overFontColor, Color downFontColor) {

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = upDrawable;
        style.down = downDrawable;
        style.over = overDrawable;

        BitmapFont buttonFont = new BitmapFont(font.getData().getFontFile());
        buttonFont.getData().setScale(scale);
        style.font = buttonFont;

        style.fontColor = fontColor;
        style.overFontColor = overFontColor;
        style.downFontColor = downFontColor;

        return style;
    }

    public TextButton.TextButtonStyle createButtonStyle(Drawable upDrawable, Drawable downDrawable, Drawable overDrawable, float scale) {
        return createButtonStyle(upDrawable, downDrawable, overDrawable, scale, Color.WHITE, Color.LIGHT_GRAY, Color.GRAY);
    }

    /**
     * Creates a TextButton with scaled font and uppercase text.
     *
     * @param text The button text (will be converted to uppercase)
     * @param style The button style
     * @return A TextButton with uppercase text
     */
    public TextButton createButton(String text, TextButton.TextButtonStyle style) {
        return new TextButton(text.toUpperCase(), style);
    }

    /**
     * Releases all resources held by this font.
     * <p>
     * Must be called when the font is no longer needed to prevent memory leaks.
     */
    public void dispose() {
        this.font.dispose();
    }
}
