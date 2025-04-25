package dev.juliusabels.fish_fiesta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.level.LevelSelectionScreen;
import dev.juliusabels.fish_fiesta.util.FishFontBig;

public class MainMenuScreen extends FFBaseScreen {
    private final SpriteBatch batch;
    private final FishFontBig font;

    public MainMenuScreen(FishFiestaGame game) {
        super(game);
        font = new FishFontBig(game);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        super.show();
        Table buttons = new Table();

        // Create button style based on big fish font
        TextButton.TextButtonStyle style = font.createButtonStyle(
            this.monitorSkin.getDrawable("menu_button"),
            this.monitorSkin.getDrawable("menu_button-pressed"),
            this.monitorSkin.getDrawable("menu_button-hovered"),
            4.0F
        );

        // Create the button with uppercase text
        TextButton playButton = font.createButton("Play", style);
        playButton.getLabelCell().padBottom(5F);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectionScreen(game));
            }
        });
        buttons.add(playButton).top().pad(10).fillX();

        buttons.row().pad(40); // Add spacing between rows

        TextButton quitButton = font.createButton("Quit", style);
        quitButton.getLabelCell().padBottom(5F);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit the application
                Gdx.app.exit();
            }
        });
        buttons.add(quitButton).pad(10).fillX();

        this.contentTable.add(buttons).center().padBottom(40F).expand();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }
}
