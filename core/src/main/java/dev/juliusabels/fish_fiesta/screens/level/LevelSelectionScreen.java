package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.MainMenuScreen;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;
import dev.juliusabels.fish_fiesta.util.LevelManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LevelSelectionScreen extends FFBaseScreen {
    private final DialogOverlay exitDialog;
    private final LevelManager levelManager;
    private final BitmapFont font;

    public LevelSelectionScreen(FishFiestaGame game) {
        super(game);
        exitDialog = new DialogOverlay(game, stage);
        levelManager = game.getResourceHandler().getLevelManager();
        font = new BitmapFont();
    }

    @Override
    public void show() {
        super.show();

        // Clear existing content
        contentTable.clear();

        // Create a table for level folders with wrapping
        Table levelsTable = new Table();
        levelsTable.top().left(); // Align to top-left
        levelsTable.defaults().pad(10);

        // Increase folder width to a more realistic value
        float folderWidth = 40; // Adjust based on actual folder size
        float folderHeight = 50; // Approximate height of each folder cell with padding

        // Calculate available space inside the monitor (accounting for contentTable's padding)
        int availableWidth = MONITOR_WIDTH - 255; // 40px padding on left and right
        int availableHeight = MONITOR_HEIGHT - 150; // 40px padding on top and bottom

        int foldersPerRow = (int)(availableWidth / folderWidth);

        // Debug available space
        log.info("Available space: {}x{}, folder size: {}x{}, folders per row: {}", availableWidth, availableHeight, folderWidth, folderHeight, foldersPerRow);

        // Get and sort level IDs to ensure proper order
        List<String> sortedLevelIds = new ArrayList<>(levelManager.getAllLevelIds());
        sortedLevelIds.sort((a, b) -> {
            // Extract level numbers and compare numerically
            int numA = Integer.parseInt(a.replace("level", ""));
            int numB = Integer.parseInt(b.replace("level", ""));
            return Integer.compare(numA, numB);
        });

        int currentColumn = 0;

        for (String levelId : sortedLevelIds) {
            Table levelCell = new Table();
            levelCell.defaults().space(5);

            Label levelName = new Label(levelId.replace("level", ""), new Label.LabelStyle(font, Color.BLACK));
            levelCell.add(levelName).row();

            Button.ButtonStyle style = new Button.ButtonStyle(
                levelManager.isLevelCompleted(levelId) ? monitorSkin.getDrawable("folder-blocked") : monitorSkin.getDrawable("folder"),
                monitorSkin.getDrawable("folder-open"),
                null
            );

            Button button = new Button(style);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new LevelScreen(game, levelId));
                }
            });
            levelCell.add(button).row();

            if (levelManager.isLevelCompleted(levelId)) {
                Label remainingLives = new Label(String.valueOf(levelManager.getRemainingLives(levelId)), new Label.LabelStyle(font, Color.RED));
                levelCell.add(remainingLives);
            }

            levelsTable.add(levelCell).width(folderWidth).height(folderHeight);

            currentColumn++;
            if (currentColumn >= foldersPerRow) {
                levelsTable.row();
                currentColumn = 0;
            }
        }

        //TODO scrollbar can be improved, but it only required once we reach 41 levels

        //Create scroll pane with improved configuration
        ScrollPane scrollPane = new ScrollPane(levelsTable, monitorSkin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Only vertical scrolling
        scrollPane.setScrollBarPositions(false, true); // Vertical scrollbar on right
        scrollPane.setOverscroll(false, false); // Disable overscroll effect

        // Set a fixed height to define the cutoff point
        contentTable.add(scrollPane).expand().top().fill().maxHeight(availableHeight); // This defines the cutoff point
    }

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
                    game.setScreen(new MainMenuScreen(game));
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
