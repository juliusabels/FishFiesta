package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;

public class TooltipHandler {
    private final BitmapFont font;
    private TextTooltip.TextTooltipStyle style;
    private TooltipManager manager;

    public TooltipHandler() {
        font = new BitmapFont();
        style = new TextTooltip.TextTooltipStyle(new Label.LabelStyle(font, Color.BLACK), null);
        manager = new TooltipManager();
        manager.instant();
        manager.offsetY = 5;
        manager.offsetX = 5;
    }

    public void appendTooltip(String tooltipText, Actor actor) {
        TextTooltip tooltip = new TextTooltip(tooltipText, manager, style);
        actor.addListener(tooltip);
    }

}
