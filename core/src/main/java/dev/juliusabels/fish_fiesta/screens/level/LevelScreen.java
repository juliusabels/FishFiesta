package dev.juliusabels.fish_fiesta.screens.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import dev.juliusabels.fish_fiesta.screens.overlay.DialogButton;
import dev.juliusabels.fish_fiesta.screens.overlay.DialogOverlay;
import dev.juliusabels.fish_fiesta.util.FishManager;
import dev.juliusabels.fish_fiesta.util.LevelManager;
import dev.juliusabels.fish_fiesta.util.ResourceHandler;
import dev.juliusabels.fish_fiesta.util.TooltipHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class LevelScreen extends FFBaseScreen {
    private final DialogOverlay exitDialog;
    private Level currentLevel;
    private final BitmapFont basicTextFont;
    private boolean levelStarted;
    private Table fishcamContent;
    private TooltipHandler tooltipHandler;
    private final FishManager fishManager;
    private ResourceHandler resourceHandler;
    private final LevelManager levelManager;
    Iterator<String> fishes;

    public LevelScreen(FishFiestaGame game, Level currentLevel) {
        super(game);
        this.currentLevel = currentLevel;
        resourceHandler = game.getResourceHandler();
        exitDialog = new DialogOverlay(game, stage);
        tooltipHandler = new TooltipHandler();
        basicTextFont = new BitmapFont();
        basicTextFont.getData().setScale(0.8F);
        levelStarted = false;
        fishManager = resourceHandler.getFishManager();
        fishes = currentLevel.getFishIDs().iterator();
        levelManager = resourceHandler.getLevelManager();
    }

    public void show() {
        super.show();

        contentTable.clear();

        Table mistakes = new Table();
        Label mistakeText = new Label("Mistakes: ", new Label.LabelStyle(basicTextFont, Color.BLACK));
        mistakes.add(mistakeText).space(5);
        if (this.currentLevel.getMistakes() > 0) {
            Table icons = new Table();
            for (int i = 0; i < currentLevel.getMistakes(); i++) {
                Image mistakeIcon = new Image(this.monitorSkin.getDrawable("mistake-icon"));
                icons.add(mistakeIcon).space(5);
            }
            mistakes.add(icons);
        }

        contentTable.add(mistakes).row();

        Table fishCamWindow = new Table();
        fishCamWindow.background(this.monitorSkin.getDrawable("fishcam-window"));

        fishcamContent = new Table();

        if (!levelStarted) {
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
        } else if (fishes.hasNext()) {
            String currentFishId = fishes.next();
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
                        if (!levelManager.isLevelFailed(currentLevel.getId())) {
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

                        if (!levelManager.isLevelFailed(currentLevel.getId())) {
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

        //TODO make all sprites bigger including window sprite
        Table guestListWindow = new Table();
        guestListWindow.background(this.monitorSkin.getDrawable("guestlist-window"));

        Table conditions = new Table();
        conditions.setFillParent(true);

        this.currentLevel.getConditions().forEach((type, values) -> {
            if (type == ConditionType.SIZE && !values.isEmpty()) {
                Table size = new Table();
                for (String value : values) {
                    Image image = new Image(this.monitorSkin.getDrawable("fish_size-" + value.toLowerCase()));
                    tooltipHandler.appendTooltip(value.toLowerCase() + " fish", image);
                    size.add(image).padTop(20).space(5).left();
                }
                conditions.add(size).expandX().left().padRight(90).row();
            }
            conditions.row().space(5);

            if (type == ConditionType.TEMPERATURE && !values.isEmpty()) {
                Image image = new Image(this.monitorSkin.getDrawable("temperature-" + values.getFirst().toLowerCase()));
                tooltipHandler.appendTooltip(values.getFirst().toLowerCase(), image);
                conditions.add(image).left().row();
            }
        });
        guestListWindow.add(conditions);

        contentTable.add(guestListWindow).expand().right().padRight(20).padBottom(100);
    }

    public void checkForLevelFail() {
        if (this.currentLevel.getMistakes() >= 3) {
            log.info("Failed Level: {}", this.currentLevel.getId());
            levelManager.markLevelFailed(this.currentLevel.getId(), this.currentLevel.getMistakes());
            game.setScreen(new LevelSelectionScreen(game));
        }
    }

    //TODO For level screen we want "continue playing", "exit to level selection", "safe level state", "restart level"
    private void showExitDialog() {
        exitDialog.showButtons(
            new DialogButton("Continue") {
                @Override
                public void run() {
                    //Do nothing
                }
            },
            new DialogButton("Safe") {
                @Override
                public void run() {
                    //TODO safe game state here later
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
            if (!exitDialog.isVisible()) {
                showExitDialog();
            } else {
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
    }
}
