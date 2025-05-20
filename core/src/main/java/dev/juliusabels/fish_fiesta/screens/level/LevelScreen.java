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

@Slf4j
public class LevelScreen extends FFBaseScreen {
    private final DialogOverlay exitDialog;
    private final Level currentLevel;
    private final BitmapFont basicTextFont;
    private boolean levelStarted;
    private final TooltipHandler tooltipHandler;
    private final FishManager fishManager;
    private final ResourceHandler resourceHandler;
    private final LevelManager levelManager;
    private final List<String> fishes;
    private final int fishAmount;
    private int fishIndex;
    private final FishFontBig fishFontBig;
    private JournalOverlay journal;

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

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(journal.getJournalInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void show() {
        super.show();
        if (fishIndex == fishAmount && currentLevel.getMistakes() != 3) {
            levelManager.markLevelCompleted(currentLevel.getId(), currentLevel.getMistakes());
            game.setScreen(new LevelSelectionScreen(game));
        }

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

        contentTable.add(mistakes).row();

        Table fishCamWindow = new Table();
        fishCamWindow.background(this.monitorSkin.getDrawable("fishcam-window"));

        Label fishcamTitle = fishFontBig.createLabel("Fish Cam", 1.1f);
        fishCamWindow.add(fishcamTitle).top().padTop(5).padLeft(4).left().row();

        Table fishcamContent = new Table();

        if (!levelStarted && !currentLevel.isInProgress()) {
            Button startButton = new Button(this.monitorSkin.getDrawable("start_button"), this.monitorSkin.getDrawable("start_button-down"));
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    levelStarted = true;
                    show();
                }
            });
            fishcamContent.add(startButton).expand().center();

            fishCamWindow.add(fishcamContent).expand().fill().row();
        } else if (fishIndex < fishAmount) {
            String currentFishId = fishes.get(fishIndex);
            log.debug("Get Fish: {}", currentFishId);
            if (fishManager.loadFishForId(currentFishId) && fishManager.getCurrentFish() != null) {
                Image image = new Image(getFishTexture(currentFishId));
                fishcamContent.add(image).expand().center().padTop(40);
                fishCamWindow.add(fishcamContent).expand().fill().row();

                Table buttons = new Table();
                Button acceptButton = new Button(this.monitorSkin.getDrawable("fishcam-accept"), this.monitorSkin.getDrawable("fishcam-accept-down"));
                acceptButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (currentLevel.meetsConditions(fishManager.getCurrentFish())) {
                            log.info("Accepted fish: <{}> matches the level conditions. Correct!", currentFishId);
                        } else {
                            log.warn("Accepted fish: <{}> does not match the level conditions. Wrong!", currentFishId);
                            currentLevel.increaseMistakes();
                            checkForLevelFail();
                        }
                        if (!currentLevel.isFailed()) {
                            fishIndex++;
                            show();
                        }
                    }
                });
                Button denyButton = new Button(this.monitorSkin.getDrawable("fishcam-deny"), this.monitorSkin.getDrawable("fishcam-deny-down"));
                denyButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        if (!currentLevel.meetsConditions(fishManager.getCurrentFish())) {
                            log.info("Declined fish: <{}> doesn't match the level conditions. Correct!", currentFishId);
                        } else {
                            log.warn("Declined fish: <{}> does match the level conditions. Wrong!", currentFishId);
                            currentLevel.increaseMistakes();
                            checkForLevelFail();

                        }

                        if (!currentLevel.isFailed()) {
                            fishIndex++;
                            show();
                        }

                    }
                });

                buttons.add(acceptButton).padRight(20);
                buttons.add(denyButton).padLeft(20);
                fishCamWindow.add(buttons).center().padBottom(7).row();
            }
        }

        contentTable.add(fishCamWindow).expand().left().padLeft(60).padBottom(160);

        Table guestListWindow = new Table();
        guestListWindow.background(this.monitorSkin.getDrawable("guestlist-window"));

        Label guestListTitle = fishFontBig.createLabel("Room Conditions", 1.1f);
        guestListWindow.add(guestListTitle).top().padTop(5).padLeft(4).left().row();

        Table conditions = new Table();
        conditions.left().top();

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
                conditions.add(label).left().fillX().padTop(10).row();
            }
        });

        this.currentLevel.getConditions().forEach((type, values) -> {
            if (type == ConditionType.SIZE && !values.isEmpty()) {
                Table size = new Table();
                size.left();
                for (String value : values) {
                    Image image = new Image(this.monitorSkin.getDrawable("fish_size-" + value.toLowerCase()));
                    tooltipHandler.appendTooltip(value.toLowerCase() + " fish", image);
                    size.add(image).left().space(5);
                }
                conditions.add(size).left().padTop(20).row();
            } else if (type == ConditionType.TEMPERATURE && !values.isEmpty()) {
                Image image = new Image(this.monitorSkin.getDrawable("temperature-" + values.getFirst().toLowerCase()));
                tooltipHandler.appendTooltip(values.getFirst().toLowerCase(), image);
                conditions.add(image).left().padTop(10).row();
            }
        });

        guestListWindow.add(conditions).expand().fill().top().left().padLeft(10).padTop(5);

        contentTable.add(guestListWindow).expand().right().padRight(20).padBottom(100);
    }

    public void checkForLevelFail() {
        if (this.currentLevel.getMistakes() >= 3) {
            log.info("Failed Level: {}", this.currentLevel.getId());
            levelManager.markLevelFailed(this.currentLevel.getId(), this.currentLevel.getMistakes());
            game.setScreen(new LevelSelectionScreen(game));
        }
    }

    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Resume") {
                @Override
                public void run() {
                    //Do nothing & resume game
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

    public TextureRegion getFishTexture(String fishId) {
        return resourceHandler.getFishSprites().findRegion(fishId);
    }

    @Override
    public void dispose() {
        super.dispose();
        exitDialog.dispose();
        basicTextFont.dispose();
        fishFontBig.dispose();
        levelManager.setActivelevel(null);
        journal.dispose();
    }
}
