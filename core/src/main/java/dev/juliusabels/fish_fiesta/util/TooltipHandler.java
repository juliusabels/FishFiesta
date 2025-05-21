package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Utility class for creating and attaching tooltips to actors in a LibGDX application.
 * <p>
 * This handler manages tooltip styling and behavior consistently across the game,
 * allowing simple attachment of tooltips to any Actor instance.
 */
public class TooltipHandler {
    /** The font used for tooltip text */
    private final BitmapFont font;

    /** Style configuration for tooltips */
    private final TextTooltip.TextTooltipStyle style;

    /** Manager controlling tooltip behavior */
    private final TooltipManager manager;

    /**
     * Creates a new tooltip handler with default settings.
     * <p>
     * Tooltips will appear instantly with default positioning and black text.
     */
    public TooltipHandler() {
        font = new BitmapFont();
        style = new TextTooltip.TextTooltipStyle(new Label.LabelStyle(font, Color.BLACK), null);
        manager = new TooltipManager();
        manager.instant();
        manager.offsetY = 5;
        manager.offsetX = 5;
    }

    /**
     * Attaches a tooltip with the specified text to an actor.
     *
     * @param tooltipText The text to display in the tooltip
     * @param actor The actor to attach the tooltip to
     */
    public void appendTooltip(String tooltipText, Actor actor) {
        TextTooltip tooltip = new TextTooltip(tooltipText, manager, style);
        actor.addListener(tooltip);
    }

    /**
     * Releases all resources held by this tooltip handler.
     * <p>
     * Must be called when the handler is no longer needed to prevent memory leaks.
     */
    public void dispose() {
        font.dispose();
    }
}
