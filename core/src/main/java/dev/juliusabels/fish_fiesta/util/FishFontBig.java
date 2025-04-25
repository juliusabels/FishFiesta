package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import dev.juliusabels.fish_fiesta.FishFiestaGame;

/**
 * A utility class for drawing bitmap font text with custom scaling.
 * <p>
 * This class wraps a BitmapFont to provide simplified text rendering with
 * automatic uppercase conversion and aspect ratio correction when scaling.
 * The vertical scaling is automatically adjusted by a correction factor to
 * maintain proper pixel-accurate appearance.
 */
public class FishFontBig {
    /**
     * Correction factor for vertical scaling.
     * <p>
     * When scaling the font, the vertical scale is divided by this value
     * to maintain proper pixel proportions and prevent distortion.
     */
    private static final float SCALE_CORRECTOR = 1.9F;

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
     * Draws text with the given scale.
     * <p>
     * The text is automatically converted to uppercase, because the font doesn't support lowercase letters.
     * The scale is applied with aspect ratio correction to maintain pixel-accurate rendering.
     *
     * @param batch The sprite batch to draw with
     * @param str The text to draw
     * @param x The x-coordinate position
     * @param y The y-coordinate position
     * @param scale The scale factor (horizontal scale is used directly,
     *              vertical scale is adjusted by the correction factor)
     * @return The GlyphLayout containing the text metrics
     */
    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, float scale) {
        this.font.getData().setScale(scale, scale / SCALE_CORRECTOR);
        return this.font.draw(batch, str.toString().toUpperCase(), x, y);
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

        // Create a new style with our font
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = upDrawable;
        style.down = downDrawable;
        style.over = overDrawable;

        // Create a copy of the font and scale it appropriately
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
