package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.game.level.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import dev.juliusabels.fish_fiesta.screens.FFBaseScreen;
import dev.juliusabels.fish_fiesta.screens.journal.JournalOverlay;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;
import dev.juliusabels.fish_fiesta.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Represents the primary gameplay screen where players evaluate fish against level conditions.
 * <p>
 * This class manages the main gameplay loop where players must accept or deny fish based on
 * specific room conditions. The screen displays a fish camera view, condition requirements,
 * mistake counters, and provides navigation options.
 * <p>
 * Players fail the level if they make 3 mistakes, and complete it by correctly evaluating
 * all fish with fewer than 3 mistakes.
 */
@Slf4j
public class LevelScreen extends FFBaseScreen {
    /** Handles showing tooltips when hovering over UI elements */
    private final TooltipHandler tooltipHandler;

    /** Manages fish data and loading */
    private final FishManager fishManager;

    /** Handles access to game resources like textures and data */
    private final ResourceHandler resourceHandler;

    /** Manages level progression, completion status, and game flow */
    private final LevelManager levelManager;

    /** Dialog shown when the player tries to exit the level */
    private final DialogOverlay exitDialog;

    /** Journal for fish information, can be toggled with J key */
    private final JournalOverlay journal;

    /** The current level being played */
    private final Level currentLevel;

    /** Font used for basic text elements */
    private final BitmapFont basicTextFont;

    /** Custom fish font */
    private final FishFontBig fishFontBig;

    /** List of fish IDs that will appear in this level */
    private final List<String> fishes;

    /** Total number of fish in the level */
    private final int fishAmount;

    /** Current index of the fish being evaluated */
    private int fishIndex;

    /** Whether the player has started the level */
    private boolean levelStarted;

    /**
     * Creates a new level screen for the specified level.
     *
     * @param game The main game instance
     * @param currentLevel The level to be played
     */
    public LevelScreen(FishFiestaGame game, Level currentLevel) {
        super(game);
        this.currentLevel = currentLevel;
        resourceHandler = game.getResourceHandler();
        exitDialog = new DialogOverlay(game, stage, contentTable);
        tooltipHandler = new TooltipHandler();
        basicTextFont = new BitmapFont();
        basicTextFont.getData().setScale(0.5F);
        fishFontBig = new FishFontBig(game);
        levelStarted = false;
        fishManager = resourceHandler.getFishManager();
        fishes = currentLevel.getFishIDs();
        fishAmount = fishes.size();
        levelManager = resourceHandler.getLevelManager();
        fishIndex = currentLevel.getFishIndex();
        journal = new JournalOverlay(game, contentTable, stage);

        setupInputProcessing();
    }

    /**
     * Configures input processing for this screen.
     * <p>
     * Sets up an InputMultiplexer to handle input for both the stage and journal toggle.
     */
    private void setupInputProcessing() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(journal.getJournalInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Sets up the screen when it becomes visible.
     * <p>
     * If the level is complete, marks it completed and navigates to level selection.
     * Otherwise, builds the UI with mistakes display, fish camera, and conditions window.
     */
    @Override
    public void show() {
        super.show();

        if (isLevelComplete()) {
            levelManager.markLevelCompleted(currentLevel.getId(), currentLevel.getMistakes());
            game.setScreen(new LevelSelectionScreen(game));
            return;
        }

        contentTable.add(createMistakesDisplay()).row();

        Table fishCamWindow = createFishCamWindow();
        contentTable.add(fishCamWindow).expand().left().padLeft(60).padBottom(160);

        Table conditionsWindow = createConditionsWindow();
        contentTable.add(conditionsWindow).expand().right().padRight(20).padBottom(100);
    }

    /**
     * Determines if the level has been completed.
     * <p>
     * A level is complete when all fish have been processed and
     * the player made fewer than 3 mistakes.
     *
     * @return true if the level is complete, false otherwise
     */
    private boolean isLevelComplete() {
        return fishIndex == fishAmount && currentLevel.getMistakes() != 3;
    }

    /**
     * Creates the mistakes display widget showing the current number of errors.
     *
     * @return A table containing the mistakes display
     */
    private Table createMistakesDisplay() {
        Table mistakes = new Table();
        mistakes.background(this.monitorSkin.getDrawable("mistake-bg"));

        Label mistakeText = fishFontBig.createLabel("Mistakes: ", 1.0F);
        mistakes.add(mistakeText).space(5).padBottom(3);

        if (this.currentLevel.getMistakes() > 0) {
            Table icons = new Table();
            for (int i = 0; i < currentLevel.getMistakes(); i++) {
                Image mistakeIcon = new Image(this.monitorSkin.getDrawable("mistake-icon"));
                icons.add(mistakeIcon).space(5).padBottom(3);
            }
            mistakes.add(icons);
        }

        return mistakes;
    }

    /**
     * Creates the fish camera window where fish are displayed for evaluation.
     * <p>
     * Shows either a start button or the current fish with decision buttons.
     *
     * @return A table containing the fish camera UI
     */
    private Table createFishCamWindow() {
        Table fishCamWindow = new Table();
        fishCamWindow.background(this.monitorSkin.getDrawable("fishcam-window"));

        Label fishcamTitle = fishFontBig.createLabel("Fish Cam", 1.1f);
        fishCamWindow.add(fishcamTitle).top().padTop(5).padLeft(4).left().row();

        Table fishcamContent = new Table();

        if (!levelStarted && !currentLevel.isInProgress()) {
            addStartButton(fishcamContent);
            fishCamWindow.add(fishcamContent).expand().fill().row();
        } else if (fishIndex < fishAmount) {
            setupCurrentFishDisplay(fishcamContent, fishCamWindow);
        }

        return fishCamWindow;
    }

    /**
     * Adds the level start button to the container.
     * <p>
     * When clicked, this button starts the level and rebuilds the UI.
     *
     * @param container The table to add the start button to
     */
    private void addStartButton(Table container) {
        Button startButton = new Button(
            this.monitorSkin.getDrawable("start_button"),
            this.monitorSkin.getDrawable("start_button-down")
        );
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelStarted = true;
                show();
            }
        });
        container.add(startButton).expand().center();
    }

    /**
     * Sets up the display for the current fish being evaluated.
     * <p>
     * Loads the current fish image and adds decision buttons below it.
     *
     * @param fishcamContent The table to add the fish image to
     * @param fishCamWindow The parent window container
     */
    private void setupCurrentFishDisplay(Table fishcamContent, Table fishCamWindow) {
        String currentFishId = fishes.get(fishIndex);
        log.debug("Get Fish: {}", currentFishId);

        if (!fishManager.loadFishForId(currentFishId) || fishManager.getCurrentFish() == null) {
            return;
        }

        Image fishImage = new Image(resourceHandler.getFishTexture(currentFishId));
        fishcamContent.add(fishImage).expand().center().padTop(40);
        fishCamWindow.add(fishcamContent).expand().fill().row();

        Table buttonContainer = createFishDecisionButtons(currentFishId);
        fishCamWindow.add(buttonContainer).center().padBottom(7).row();
    }

    /**
     * Creates the accept/deny buttons for fish evaluation.
     * <p>
     * These buttons trigger the appropriate handlers when clicked.
     *
     * @param currentFishId The ID of the fish being evaluated
     * @return A table containing the decision buttons
     */
    private Table createFishDecisionButtons(String currentFishId) {
        Table buttons = new Table();

        Button acceptButton = new Button(
            this.monitorSkin.getDrawable("fishcam-accept"),
            this.monitorSkin.getDrawable("fishcam-accept-down")
        );
        acceptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleFishAccepted(currentFishId);
            }
        });

        Button denyButton = new Button(
            this.monitorSkin.getDrawable("fishcam-deny"),
            this.monitorSkin.getDrawable("fishcam-deny-down")
        );
        denyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleFishDenied(currentFishId);
            }
        });

        buttons.add(acceptButton).padRight(20);
        buttons.add(denyButton).padLeft(20);

        return buttons;
    }

    /**
     * Handles the player's decision to accept a fish.
     * <p>
     * Checks if the fish meets the level conditions. If it does, the decision is correct.
     * If not, a mistake is recorded and the level may fail if the mistake limit is reached.
     *
     * @param fishId The ID of the fish being accepted
     */
    private void handleFishAccepted(String fishId) {
        boolean matchesConditions = currentLevel.meetsConditions(fishManager.getCurrentFish());

        if (matchesConditions) {
            log.info("Accepted fish: <{}> matches the level conditions. Correct!", fishId);
        } else {
            log.info("Accepted fish: <{}> does not match the level conditions. Wrong!", fishId);
            currentLevel.increaseMistakes();
            checkForLevelFail();
        }

        if (!currentLevel.isFailed()) {
            fishIndex++;
            show();
        }
    }

    /**
     * Handles the player's decision to deny a fish.
     * <p>
     * Checks if the fish meets the level conditions. If it doesn't, the decision is correct.
     * If it does meet conditions, a mistake is recorded and the level may fail if the mistake limit is reached.
     *
     * @param fishId The ID of the fish being denied
     */
    private void handleFishDenied(String fishId) {
        boolean matchesConditions = currentLevel.meetsConditions(fishManager.getCurrentFish());

        if (!matchesConditions) {
            log.info("Declined fish: <{}> doesn't match the level conditions. Correct!", fishId);
        } else {
            log.info("Declined fish: <{}> does match the level conditions. Wrong!", fishId);
            currentLevel.increaseMistakes();
            checkForLevelFail();
        }

        if (!currentLevel.isFailed()) {
            fishIndex++;
            show();
        }
    }

    /**
     * Creates the conditions window showing the required criteria for fish selection.
     *
     * @return A table containing the conditions window UI
     */
    private Table createConditionsWindow() {
        Table guestListWindow = new Table();
        guestListWindow.background(this.monitorSkin.getDrawable("guestlist-window"));

        Label guestListTitle = fishFontBig.createLabel("Room Conditions", 1.1f);
        guestListWindow.add(guestListTitle).top().padTop(5).padLeft(4).left().row();

        Table conditionsTable = createConditionsTable();
        guestListWindow.add(conditionsTable).expand().fill().top().left().padLeft(10).padTop(5);

        return guestListWindow;
    }

    /**
     * Creates a table displaying all level conditions.
     *
     * @return A table containing text and visual condition indicators
     */
    private Table createConditionsTable() {
        Table conditions = new Table();
        conditions.left().top();

        // Add text-based conditions first
        addTextBasedConditions(conditions);

        // Add visual conditions (size and temperature)
        addVisualConditions(conditions);

        return conditions;
    }

    /**
     * Adds text-based conditions to the conditions table.
     * <p>
     * These include all condition types except size and temperature,
     * which are handled separately with visual indicators.
     *
     * @param container The table to add the text conditions to
     */
    private void addTextBasedConditions(Table container) {
        this.currentLevel.getConditions().forEach((type, values) -> {
            if (type != ConditionType.SIZE && type != ConditionType.TEMPERATURE && !values.isEmpty()) {
                String typeName = FishManager.formatIdToName(type.name().toLowerCase()) + ": ";

                StringBuilder builder = new StringBuilder();
                int valuesSize = values.size() - 1;
                for (int i = 0; i <= valuesSize; i++) {
                    builder.append(values.get(i));
                    if (i != valuesSize) {
                        builder.append(", ");
                    }
                }

                String formattedValues = FishManager.formatIdToName(builder.toString());
                Label label = fishFontBig.createLabel(typeName + formattedValues, 0.7F, Color.BLACK);
                container.add(label).left().fillX().padTop(10).row();
            }
        });
    }

    /**
     * Adds visual conditions (size and temperature) to the conditions table.
     * <p>
     * These conditions are displayed as icons with tooltips rather than text.
     *
     * @param container The table to add the visual conditions to
     */
    private void addVisualConditions(Table container) {
        this.currentLevel.getConditions().forEach((type, values) -> {
            if (type == ConditionType.SIZE && !values.isEmpty()) {
                Table sizeTable = new Table();
                sizeTable.left();

                for (String value : values) {
                    Image image = new Image(this.monitorSkin.getDrawable("fish_size-" + value.toLowerCase()));
                    tooltipHandler.appendTooltip(value.toLowerCase() + " fish", image);
                    sizeTable.add(image).left().space(5);
                }

                container.add(sizeTable).left().padTop(20).row();
            } else if (type == ConditionType.TEMPERATURE && !values.isEmpty()) {
                Image image = new Image(this.monitorSkin.getDrawable("temperature-" + values.getFirst().toLowerCase()));
                tooltipHandler.appendTooltip(values.getFirst().toLowerCase(), image);
                container.add(image).left().padTop(10).row();
            }
        });
    }

    /**
     * Checks if the player has failed the level by making too many mistakes.
     * <p>
     * If 3 or more mistakes have been made, navigates back to the level selection screen.
     */
    public void checkForLevelFail() {
        if (this.currentLevel.getMistakes() >= 3) {
            log.info("Failed Level: {}", this.currentLevel.getId());
            levelManager.markLevelFailed(this.currentLevel.getId(), this.currentLevel.getMistakes());
            game.setScreen(new LevelSelectionScreen(game));
        }
    }

    /**
     * Displays the exit dialog with options to resume, save progress, or exit.
     * <p>
     * The dialog appears when ESC is pressed during gameplay.
     */
    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Resume") {
                @Override
                public void run() {
                    // Do nothing & resume game
                }
            },
            new DialogButton("Save") {
                @Override
                public void run() {
                    levelManager.safeLevelProgress(currentLevel.getId(), currentLevel.getMistakes(), fishIndex);
                    showExitDialog();
                }
            },
            new DialogButton("Exit") {
                @Override
                public void run() {
                    game.setScreen(new LevelSelectionScreen(game));
                }
            }
        );
    }

    /**
     * Renders the screen and handles input.
     * <p>
     * Checks for the ESC key to show/hide the exit dialog.
     *
     * @param delta Time elapsed since the last frame
     */
    @Override
    public void render(float delta) {
        super.render(delta);

        // If back key pressed, show dialog instead of immediate exit. If pressed again close dialog window again
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!exitDialog.isVisible() && !journal.isVisible()) {
                showExitDialog();
            } else if (!journal.isVisible()) {
                exitDialog.hide();
            }
        }
    }

    /**
     * Disposes of resources when the screen is no longer needed.
     * <p>
     * Cleans up all assets to prevent memory leaks.
     */
    @Override
    public void dispose() {
        super.dispose();
        exitDialog.dispose();
        basicTextFont.dispose();
        fishFontBig.dispose();
        levelManager.setActiveLevel(null);
        journal.dispose();
        tooltipHandler.dispose();
    }
}
