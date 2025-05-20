package dev.juliusabels.fish_fiesta.screens.journal;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.util.FishFontBig;
import dev.juliusabels.fish_fiesta.util.FishManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JournalOverlay {
    private final FishFiestaGame game;
    private final FishFontBig fishFont;
    private final FishManager fishManager;
    private final Table activeScreenTable;
    private final BitmapFont regularFont;
    private final Skin journalSkin;

    private final Table journalTable;
    private final Map<String, Table> fishPages = new HashMap<>();

    @Getter
    private boolean isVisible = false;

    private int currentPage = 0;
    private static final int fishPerPage = 14;
    private static final int fishPerColumn = 7;

    public JournalOverlay(FishFiestaGame game, Table activeScreenTable, Stage mainStage) {
        this.game = game;
        this.fishManager = game.getResourceHandler().getFishManager();
        this.fishFont = new FishFontBig(game);
        this.regularFont = new BitmapFont();
        this.regularFont.getData().setScale(0.8F);
        this.activeScreenTable = activeScreenTable;

        journalSkin = game.getResourceHandler().getJournalSkin();

        // Create journal container
        journalTable = new Table();
        journalTable.setFillParent(true);
        journalTable.align(Align.center);
        journalTable.setVisible(false);

        // Create background table with proper size
        Table backgroundTable = new Table();
        backgroundTable.setBackground(journalSkin.getDrawable("background"));
        // Set fixed size to match texture dimensions
        backgroundTable.setSize(582, 400);

        // Add background and content to main journal table
        journalTable.add(backgroundTable).size(582, 400);

        mainStage.addActor(journalTable);
    }

    public void toggle() {
        isVisible = !isVisible;
        journalTable.setVisible(isVisible);

        if (isVisible) {
            // Show index page on open
            showIndexPage();
            // Disable interaction with main screen
            activeScreenTable.setTouchable(Touchable.disabled);
        } else {
            // Re-enable interaction with main screen
            activeScreenTable.setTouchable(Touchable.enabled);
        }
    }

    private Table createIndexPage() {
        Table content = new Table();

        Table fixedContainer = new Table();
        fixedContainer.setSize(582, 400);

        Label titleLabel = fishFont.createLabel("Fish Index", 1.2f);
        titleLabel.setColor(Color.BLACK);
        titleLabel.setPosition(60, 370);
        fixedContainer.addActor(titleLabel);

        Table columnsTable = new Table();
        columnsTable.setPosition(45, 70);
        columnsTable.setSize(492, 260);

        Table leftColumn = new Table();
        Table rightColumn = new Table();

        List<String> fishIds = fishManager.getAllFishIds();
        int totalPages = (int)Math.ceil(fishIds.size() / (float)fishPerPage);
        int startIdx = currentPage * fishPerPage;
        int endIdx = Math.min(startIdx + fishPerPage, fishIds.size());

        for (int i = startIdx; i < endIdx; i++) {
            String fishId = fishIds.get(i);
            Table fishEntry = new Table();

            // Fish icon
            TextureRegion fishIcon = game.getResourceHandler().getFishSprites().findRegion(fishId);
            if (fishIcon != null) {
                Image icon = new Image(fishIcon);
                icon.setSize(32, 32);
                fishEntry.add(icon).size(32, 32).padRight(10);
            }

            // Fish name
            String fishName = FishManager.formatIdToName(fishId);
            Label nameLabel = fishFont.createLabel(fishName, 0.8f);
            nameLabel.setColor(Color.BLACK);
            fishEntry.add(nameLabel).expandX().left();

            fishEntry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showFishPage(fishId);
                }
            });

            // Add to left or right column based on index
            int relativeIndex = i - startIdx;
            if (relativeIndex < fishPerColumn) {
                leftColumn.add(fishEntry).expandX().fillX().padBottom(10).row();
            } else {
                rightColumn.add(fishEntry).expandX().fillX().padBottom(10).row();
            }
        }

        columnsTable.add(leftColumn).width(230);
        columnsTable.add().width(80);
        columnsTable.add(rightColumn).width(230);
        fixedContainer.addActor(columnsTable);

        if (totalPages > 1) {
            if (currentPage > 0) {
                Button prevButton = new Button(new Button.ButtonStyle(journalSkin.getDrawable("button_left"), journalSkin.getDrawable("button_left-pressed"), null));
                prevButton.sizeBy(3);
                prevButton.setPosition(50, 30);
                prevButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentPage--;
                        showIndexPage();
                    }
                });
                fixedContainer.addActor(prevButton);
            }

            // Page indicator
            Label pageLabel = fishFont.createLabel((currentPage + 1) + "/" + totalPages, 0.9f);
            pageLabel.setColor(Color.BLACK);
            pageLabel.setPosition(275, 30);
            fixedContainer.addActor(pageLabel);

            // Next button (right side)
            if (currentPage < totalPages - 1) {
                Button nextButton = new Button(new Button.ButtonStyle(journalSkin.getDrawable("button_right"), journalSkin.getDrawable("button_right-pressed"), null));
                nextButton.sizeBy(3);
                nextButton.setPosition(500, 30);
                nextButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentPage++;
                        showIndexPage();
                    }
                });
                fixedContainer.addActor(nextButton);
            }
        }

        content.add(fixedContainer).size(582, 400);
        return content;
    }

    private Table createFishPage(String fishId) {
        Table content = new Table();

        // Create a container with fixed dimensions
        Table fixedContainer = new Table();
        fixedContainer.setSize(582, 400);

        // Load fish data
        if (!fishManager.loadFishForId(fishId)) {
            log.error("Could not load fish data for ID: {}", fishId);
            return content;
        }

        WaterCreature fish = fishManager.getCurrentFish();
        if (fish == null) {
            log.error("Fish data is null for ID: {}", fishId);
            return content;
        }

        Table leftPage = new Table();
        leftPage.setPosition(45, 50);

        Label titleLabel = fishFont.createLabel(fish.getName(), 1.2f);
        titleLabel.setColor(Color.BLACK);
        titleLabel.setPosition(0, 300);
        leftPage.addActor(titleLabel);

        // Get fish texture
        TextureRegion fishTexture = game.getResourceHandler().getFishSprites().findRegion(fishId);
        if (fishTexture != null) {
            // Define sizes
            float frameSize = 137;
            float fishSize = 64;

            // Calculate positions
            float centerX = (271 - frameSize) / 2;
            float positionY = 100;

            // Calculate offsets to center fish within frame
            float fishOffsetX = (frameSize - fishSize) / 2;
            float fishOffsetY = (frameSize - fishSize) / 2;

            // Create the fish image
            Image fishImage = new Image(fishTexture);
            fishImage.setSize(fishSize * 1.5F, fishSize * 1.5F);
            fishImage.setPosition(centerX + fishOffsetX - 66, positionY + fishOffsetY - 18);

            // Create the frame image
            Image frameImage = new Image(game.getResourceHandler().getJournalSkin().getDrawable("fish_frame"));
            frameImage.setSize(frameSize, frameSize);
            frameImage.setPosition(centerX - 50, positionY);

            // Add both to left page (fish first, then frame on top)
            leftPage.addActor(fishImage);
            leftPage.addActor(frameImage);
        }

        Table rightPage = new Table();
        rightPage.setPosition(300, 50);
        rightPage.top().left(); // Align to top-left
        rightPage.setSize(240, 330);

        Label descLabel = new Label(JournalDescGenerator.generateFor(fish), new Label.LabelStyle(regularFont, Color.BLACK));
        descLabel.setColor(Color.BLACK);
        descLabel.setWrap(true);
        rightPage.add(descLabel).width(240).top().left().padLeft(32);

        // Back button at bottom
        Button backButton = new Button(new Button.ButtonStyle(journalSkin.getDrawable("button_left"), journalSkin.getDrawable("button_left-pressed"), null));
        backButton.sizeBy(3);
        backButton.setPosition(30, 30);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showIndexPage();
            }
        });

        // Add all elements to the fixed container
        fixedContainer.addActor(leftPage);
        fixedContainer.addActor(rightPage);
        fixedContainer.addActor(backButton);

        content.add(fixedContainer).size(582, 400);
        return content;
    }


    private void showIndexPage() {
        // Clear current content
        Table backgroundTable = (Table)journalTable.getChild(0);
        backgroundTable.clearChildren();

        Table indexPage = createIndexPage();

        // Add index page
        backgroundTable.add(indexPage).expand().fill();
    }

    private void showFishPage(String fishId) {
        if (!fishPages.containsKey(fishId)) {
            fishPages.put(fishId, createFishPage(fishId));
        }

        // Clear current content
        Table backgroundTable = (Table)journalTable.getChild(0);
        backgroundTable.clearChildren();

        // Add fish page
        backgroundTable.add(fishPages.get(fishId)).expand().fill();
    }

    public void dispose() {
        fishFont.dispose();
        regularFont.dispose();
    }

    public InputAdapter getJournalInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.J) {
                    toggle();
                    return true;
                }
                return false;
            }
        };
    }
}
