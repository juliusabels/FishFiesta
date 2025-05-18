package dev.juliusabels.fish_fiesta.screens.journal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import dev.juliusabels.fish_fiesta.FishFiestaGame;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.util.FishFontBig;
import dev.juliusabels.fish_fiesta.util.FishManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JournalOverlay {
    private final FishFiestaGame game;
    private final FishManager fishManager;
    private final Skin journalSkin;

    private final Stage journalStage;
    private final Table journalTable;
    private final FishFontBig fishFont;
    private final Table contentTable;

    private final Map<String, Table> fishPages = new HashMap<>();
    private final Table indexPage;

    private boolean isVisible = false;

    private final InputAdapter journalKeyListener = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.J) {
                toggle();
                return true;
            }
            return false;
        }
    };

    public JournalOverlay(FishFiestaGame game, Stage mainStage, Table mainTable) {
        this.game = game;
        this.fishManager = game.getResourceHandler().getFishManager();
        this.fishFont = new FishFontBig(game);
        this.contentTable = mainTable;

        journalSkin = game.getResourceHandler().getJournalSkin();

        // Create journal stage for input handling
        journalStage = new Stage(mainStage.getViewport());

        // Create journal container
        journalTable = new Table();
        journalTable.setFillParent(true);
        journalTable.align(Align.center);
        journalTable.setVisible(false);
        journalTable.setBackground(journalSkin.getDrawable("background"));
        journalTable.center();

        // Create index page
        indexPage = createIndexPage();

        journalStage.addActor(journalTable);
        mainStage.addActor(journalStage.getRoot());
    }

    public void toggle() {
        isVisible = !isVisible;
        journalTable.setVisible(isVisible);

        if (isVisible) {
            // Disable interaction with main screen
            contentTable.setTouchable(Touchable.disabled);

            // Show index page on open
            showIndexPage();
        } else {
            // Re-enable interaction with main screen
            contentTable.setTouchable(Touchable.enabled);
        }
    }

    public void render(float delta) {
        if (isVisible) {
            journalStage.act(delta);
            journalStage.draw();
        }
    }

    private Table createIndexPage() {
        Table page = new Table();

        // Journal book background
        Table bookTable = new Table();
        bookTable.background(journalSkin.getDrawable("background"));

        // Title
        Label titleLabel = fishFont.createLabel("FISH INDEX", 1.2f);
        titleLabel.setColor(Color.BLACK);

        // Fish list container
        Table fishListTable = new Table();

        // Get all fish IDs and create entries
        List<String> fishIds = fishManager.getAllFishIds();
        for (String fishId : fishIds) {
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

            // Make entry clickable
            fishEntry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showFishPage(fishId);
                }
            });

            fishListTable.add(fishEntry).expandX().fillX().padBottom(10).row();
        }

        // Add scrolling for fish list
        ScrollPane scrollPane = new ScrollPane(fishListTable);
        scrollPane.setScrollingDisabled(true, false);

        // Arrange elements in the book layout
        bookTable.add(titleLabel).padTop(40).padBottom(20).row();
        bookTable.add(scrollPane).expand().fill().padLeft(60).padRight(60).padBottom(40);

        page.add(bookTable);
        return page;
    }

    private Table createFishPage(String fishId) {
        Table page = new Table();

        // Load fish data
        if (!fishManager.loadFishForId(fishId)) {
            log.error("Could not load fish data for ID: {}", fishId);
            return page;
        }

        WaterCreature fish = fishManager.getCurrentFish();
        if (fish == null) {
            log.error("Fish data is null for ID: {}", fishId);
            return page;
        }

        // Journal book background
        Table bookTable = new Table();
        bookTable.background(journalSkin.getDrawable("background"));

        // Content layout
        Table content = new Table();

        // Fish title/name
        Label titleLabel = fishFont.createLabel(fish.getName(), 1.2f);
        titleLabel.setColor(Color.BLACK);
        content.add(titleLabel).colspan(2).padBottom(20).row();

        // Fish image
        TextureRegion fishTexture = game.getResourceHandler().getFishSprites().findRegion(fishId);
        if (fishTexture != null) {
            Image fishImage = new Image(fishTexture);
            content.add(fishImage).colspan(2).size(120, 120).padBottom(20).row();
        }

        // Fish details
        addDetailRow(content, "Size:", fish.getSize().toString());

        if (!fish.getWaterTypes().isEmpty()) {
            addDetailRow(content, "Water Type:", fish.getWaterTypes().toString()
                .replace("[", "").replace("]", ""));
        }

        if (!fish.getWaterSubtypes().isEmpty()) {
            addDetailRow(content, "Habitat:", fish.getWaterSubtypes().toString()
                .replace("[", "").replace("]", ""));
        }

        if (!fish.getWaterTemperatures().isEmpty()) {
            addDetailRow(content, "Temperature:", fish.getWaterTemperatures().toString()
                .replace("[", "").replace("]", ""));
        }

        // Description
        if (fish.getDescription() != null && !fish.getDescription().isEmpty()) {
            Label descTitle = fishFont.createLabel("Description:", 0.9f);
            descTitle.setColor(Color.BLACK);
            content.add(descTitle).colspan(2).left().padTop(15).row();

            Label descLabel = fishFont.createLabel(fish.getDescription(), 0.8f);
            descLabel.setColor(Color.BLACK);
            descLabel.setWrap(true);
            content.add(descLabel).colspan(2).width(400).left().padTop(5).row();
        }

        // Back button
        TextButton backButton = new TextButton("Back to Index", game.getResourceHandler().getMonitorSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showIndexPage();
            }
        });
        content.add(backButton).colspan(2).padTop(30).row();

        // Add scrollable content
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setScrollingDisabled(true, false);

        bookTable.add(scrollPane).expand().fill().padLeft(60).padRight(60).padTop(30).padBottom(30);
        page.add(bookTable).width(600).height(450);

        return page;
    }

    private void addDetailRow(Table table, String label, String value) {
        Label labelWidget = fishFont.createLabel(label, 0.9f);
        labelWidget.setColor(Color.BLACK);
        table.add(labelWidget).left().padRight(10);

        Label valueWidget = fishFont.createLabel(value, 0.9f);
        valueWidget.setColor(Color.BLACK);
        table.add(valueWidget).left().expandX().row();
    }

    private void showIndexPage() {
        journalTable.clear();
        journalTable.add(indexPage);
    }

    private void showFishPage(String fishId) {
        // Create fish page if not exists
        if (!fishPages.containsKey(fishId)) {
            fishPages.put(fishId, createFishPage(fishId));
        }

        // Show fish page
        journalTable.clear();
        journalTable.add(fishPages.get(fishId));
    }

    public InputAdapter getJournalKeyListener() {
        return journalKeyListener;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        fishFont.dispose();
        journalStage.dispose();
    }
}
