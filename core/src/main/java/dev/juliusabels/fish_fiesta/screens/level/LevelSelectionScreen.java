package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

/**
 * Displays a grid of level selection folders for the game.
 * <p>
 * This screen presents available game levels as folders in a scrollable grid layout.
 * Each folder displays:
 * <ul>
 *   <li>Level number</li>
 *   <li>Visual status indicating if the level is completed, failed, or in progress</li>
 *   <li>Mistake indicators showing player performance</li>
 * </ul>
 * <p>
 * Players can click on folders to play levels. For completed levels, a confirmation
 * dialog is shown before replaying. The user can return to the main menu by pressing
 * the escape key, which shows a confirmation dialog.
 */
@Slf4j
public class LevelSelectionScreen extends FFBaseScreen {
    /** Dialog shown when the user attempts to exit the screen */
    private final DialogOverlay exitDialog;

    /** Manager for accessing level data and persistence */
    private final LevelManager levelManager;

    /** Font used for level numbers and labels */
    private final BitmapFont font;

    /** Width of each level folder in pixels */
    private static final float FOLDER_WIDTH = 40;

    /** Height of each level folder in pixels */
    private static final float FOLDER_HEIGHT = 85;

    /** Padding around level folders in pixels */
    private static final int FOLDER_PADDING = 10;

    /** Left and right padding for the content area */
    private static final int HORIZONTAL_PADDING = 255;

    /** Top and bottom padding for the content area */
    private static final int VERTICAL_PADDING = 150;

    /**
     * Creates a new level selection screen.
     *
     * @param game The main game instance
     */
    public LevelSelectionScreen(FishFiestaGame game) {
        super(game);
        exitDialog = new DialogOverlay(game, stage, contentTable);
        levelManager = game.getResourceHandler().getLevelManager();
        font = new BitmapFont();
    }

    /**
     * Sets up the level selection UI when the screen becomes active.
     * <p>
     * Creates a scrollable grid of level folders with appropriate status
     * indicators and click handlers for level selection.
     */
    @Override
    public void show() {
        super.show();

        // Calculate available space and create container for level folders
        Table levelsTable = createLevelsContainer();

        // Calculate layout constraints
        int availableWidth = calculateAvailableWidth();
        int availableHeight = calculateAvailableHeight();
        int foldersPerRow = calculateFoldersPerRow(availableWidth);

        log.info("Available space: {}x{}, folder size: {}x{}, folders per row: {}",
            availableWidth, availableHeight, FOLDER_WIDTH, FOLDER_HEIGHT, foldersPerRow);

        // Add level folders to the grid
        addLevelFolders(levelsTable, foldersPerRow);

        // Create and configure scroll pane
        addScrollPane(levelsTable, availableHeight);
    }

    /**
     * Creates the container table for level folders.
     *
     * @return The configured table for level folders
     */
    private Table createLevelsContainer() {
        Table levelsTable = new Table();
        levelsTable.top().left();
        levelsTable.defaults().pad(FOLDER_PADDING);
        return levelsTable;
    }

    /**
     * Calculates the available width for the level grid.
     *
     * @return The available width in pixels
     */
    private int calculateAvailableWidth() {
        return MONITOR_WIDTH - HORIZONTAL_PADDING;
    }

    /**
     * Calculates the available height for the level grid.
     *
     * @return The available height in pixels
     */
    private int calculateAvailableHeight() {
        return MONITOR_HEIGHT - VERTICAL_PADDING;
    }

    /**
     * Calculates how many folders can fit in one row.
     *
     * @param availableWidth The available width for the grid
     * @return The number of folders that fit in one row
     */
    private int calculateFoldersPerRow(int availableWidth) {
        return (int)(availableWidth / FOLDER_WIDTH);
    }

    /**
     * Adds level folders to the grid layout.
     *
     * @param levelsTable The table to add folders to
     * @param foldersPerRow The number of folders per row
     */
    private void addLevelFolders(Table levelsTable, int foldersPerRow) {
        // Get and sort level IDs to ensure proper order
        List<String> sortedLevelIds = getSortedLevelIds();
        int currentColumn = 0;

        for (String levelId : sortedLevelIds) {
            // Create and add the level folder cell
            Table levelCell = createLevelCell(levelId);
            levelsTable.add(levelCell).width(FOLDER_WIDTH).height(FOLDER_HEIGHT);

            // Handle row wrapping
            currentColumn++;
            if (currentColumn >= foldersPerRow) {
                levelsTable.row();
                currentColumn = 0;
            }
        }
    }

    /**
     * Gets the sorted list of level IDs.
     * <p>
     * Sorts numerical level IDs in numerical order (level1, level2, etc.)
     *
     * @return A sorted list of level IDs
     */
    private List<String> getSortedLevelIds() {
        List<String> sortedLevelIds = new ArrayList<>(levelManager.getAllLevelIds());
        sortedLevelIds.sort((a, b) -> {
            int numA = Integer.parseInt(a.replace("level", ""));
            int numB = Integer.parseInt(b.replace("level", ""));
            return Integer.compare(numA, numB);
        });
        return sortedLevelIds;
    }

    /**
     * Creates a cell containing a level folder with its indicators.
     *
     * @param levelId The ID of the level to create a cell for
     * @return A table containing the level folder components
     */
    private Table createLevelCell(String levelId) {
        Table levelCell = new Table();
        levelCell.defaults().space(5);

        // Add level number label
        addLevelLabel(levelCell, levelId);

        // Add folder button with appropriate state
        addFolderButton(levelCell, levelId);

        // Add mistake indicators
        addMistakeIndicators(levelCell, levelId);

        return levelCell;
    }

    /**
     * Adds a level number label to a level cell.
     *
     * @param levelCell The cell to add the label to
     * @param levelId The ID of the level
     */
    private void addLevelLabel(Table levelCell, String levelId) {
        String levelNumber = levelId.replace("level", "");
        Label levelName = new Label(levelNumber, new Label.LabelStyle(font, Color.BLACK));
        levelCell.add(levelName).row();
    }

    /**
     * Adds a folder button with appropriate visual state to a level cell.
     *
     * @param levelCell The cell to add the button to
     * @param levelId The ID of the level
     */
    private void addFolderButton(Table levelCell, String levelId) {
        String folderSuffix = determineFolderSuffix(levelId);
        Button.ButtonStyle style = new Button.ButtonStyle(
            monitorSkin.getDrawable("folder" + folderSuffix),
            monitorSkin.getDrawable("folder-open"),
            null
        );

        Button button = new Button(style);
        button.addListener(createFolderClickListener(levelId));
        levelCell.add(button).row();
    }

    /**
     * Determines the appropriate folder suffix based on level state.
     *
     * @param levelId The ID of the level
     * @return The suffix for the folder drawable
     */
    private String determineFolderSuffix(String levelId) {
        if (levelManager.isLevelCompleted(levelId)) {
            return "-complete";
        } else if (levelManager.isLevelFailed(levelId)) {
            return "-failed";
        } else if (levelManager.isLevelInProgress(levelId)) {
            return "-inProgress";
        } else {
            return "";
        }
    }

    /**
     * Creates a click listener for a level folder button.
     *
     * @param levelId The ID of the level
     * @return A ClickListener for the folder button
     */
    private ClickListener createFolderClickListener(String levelId) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (levelManager.isLevelCompleted(levelId)) {
                    // Show confirmation dialog for completed levels
                    showReplayDialog(levelId);
                } else {
                    // For non-completed levels, load directly
                    loadLevel(levelId);
                }
            }
        };
    }

    /**
     * Shows a confirmation dialog for replaying completed levels.
     *
     * @param levelId The ID of the level to replay
     */
    private void showReplayDialog(String levelId) {
        DialogOverlay replayDialog = new DialogOverlay(game, stage, contentTable);
        replayDialog.showButtons(
            new DialogButton("Replay") {
                @Override
                public void run() {
                    loadLevel(levelId);
                }
            },
            new DialogButton("Cancel") {
                @Override
                public void run() {
                    // Do nothing, go back to selection screen
                }
            }
        );
    }

    /**
     * Adds mistake indicators to a level cell.
     *
     * @param levelCell The cell to add indicators to
     * @param levelId The ID of the level
     */
    private void addMistakeIndicators(Table levelCell, String levelId) {
        Table icons = new Table();

        if (levelManager.isLevelCompleted(levelId) ||
            levelManager.isLevelFailed(levelId) ||
            levelManager.isLevelInProgress(levelId)) {

            int mistakes = levelManager.getMistakes(levelId);
            if (mistakes == 0) {
                // Add placeholder to maintain consistent sizing
                icons.add(new Image(this.monitorSkin.getDrawable("mistake-icon-placeholder")));
            }

            // Add mistake icons based on count
            for (int i = 0; i < mistakes; i++) {
                Image mistakeIcon = new Image(this.monitorSkin.getDrawable("mistake-icon"));
                icons.add(mistakeIcon);
            }
        } else {
            // Add placeholder for consistent sizing
            icons.add(new Image(this.monitorSkin.getDrawable("mistake-icon-placeholder")));
        }

        levelCell.add(icons);
    }

    /**
     * Adds a scrollable container for the level selection grid.
     *
     * @param levelsTable The table containing level folders
     * @param availableHeight The maximum height for the scroll pane
     */
    private void addScrollPane(Table levelsTable, int availableHeight) {
        ScrollPane scrollPane = new ScrollPane(levelsTable, monitorSkin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Only vertical scrolling
        scrollPane.setScrollBarPositions(false, true); // Vertical scrollbar on right
        scrollPane.setOverscroll(false, false); // Disable overscroll effect

        // Add scroll pane to content table with height constraint
        contentTable.add(scrollPane).expand().top().fill().maxHeight(availableHeight);
    }

    /**
     * Loads and transitions to the selected level screen.
     *
     * @param levelId The ID of the level to load
     */
    private void loadLevel(String levelId) {
        if (levelManager.loadLevelForId(levelId) && levelManager.getActiveLevel() != null) {
            game.setScreen(new LevelScreen(game, levelManager.getActiveLevel()));
        } else {
            log.error("Unable to load level from id: {}", levelId);
        }
    }

    /**
     * Shows a dialog confirming if the user wants to exit to the main menu.
     */
    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Resume") {
                @Override
                public void run() {
                    // Do nothing, stay on this screen
                }
            },
            new DialogButton("Exit") {
                @Override
                public void run() {
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        );
    }

    /**
     * Renders the level selection screen and handles input.
     * <p>
     * Checks for escape key presses to show/hide the exit dialog.
     *
     * @param delta Time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        super.render(delta);

        // Handle escape key for showing/hiding the exit dialog
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!exitDialog.isVisible()) {
                showExitDialog();
            } else {
                exitDialog.hide();
            }
        }
    }

    /**
     * Disposes of resources when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        super.dispose();
        exitDialog.dispose();
        font.dispose();
    }
}
