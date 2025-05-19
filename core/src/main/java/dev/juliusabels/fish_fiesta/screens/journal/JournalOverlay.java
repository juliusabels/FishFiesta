package dev.juliusabels.fish_fiesta.screens.journal;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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

    private final Table journalTable;
    private final Table indexPage;
    private final Map<String, Table> fishPages = new HashMap<>();

    @Getter
    private boolean isVisible = false;

    private int currentPage = 0;
    private final int fishPerPage = 14;
    private final int fishPerColumn = 7;

    public JournalOverlay(FishFiestaGame game, Table activeScreenTable, Stage mainStage) {
        this.game = game;
        this.fishManager = game.getResourceHandler().getFishManager();
        this.fishFont = new FishFontBig(game);
        this.activeScreenTable = activeScreenTable;

        Skin journalSkin = game.getResourceHandler().getJournalSkin();

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

        // Create index page once for better performance
        indexPage = createIndexPage();

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

        // Title
        Label titleLabel = fishFont.createLabel("FISH INDEX", 1.2f);
        titleLabel.setColor(Color.BLACK);
        content.add(titleLabel).colspan(2).padTop(40).padBottom(20).row();

        // Get all fish IDs
        List<String> fishIds = fishManager.getAllFishIds();
        int totalPages = (int)Math.ceil(fishIds.size() / (float)fishPerPage);

        // Get current page's fish
        int startIdx = currentPage * fishPerPage;
        int endIdx = Math.min(startIdx + fishPerPage, fishIds.size());

        // Create left and right columns
        Table leftColumn = new Table();
        Table rightColumn = new Table();

        // Fill columns with fish entries
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

            // Add click listener for fish page navigation
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

        // Add columns to content with 80px space between them
        Table columnsTable = new Table();
        columnsTable.add(leftColumn).width(240);
        columnsTable.add().width(80); // Space between columns
        columnsTable.add(rightColumn).width(240);

        content.add(columnsTable).expand().fill().row();

        // Add pagination controls if needed
        if (totalPages > 1) {
            Table paginationTable = new Table();

            // Previous page button
            if (currentPage > 0) {
                TextButton prevButton = new TextButton("<", game.getResourceHandler().getMonitorSkin());
                prevButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentPage--;
                        showIndexPage();
                    }
                });
                paginationTable.add(prevButton).left().padBottom(20);
            } else {
                paginationTable.add().width(40); // Placeholder for alignment
            }

            // Page indicator
            Label pageLabel = fishFont.createLabel((currentPage + 1) + "/" + totalPages, 0.9f);
            pageLabel.setColor(Color.BLACK);
            paginationTable.add(pageLabel).width(100).center();

            // Next page button
            if (currentPage < totalPages - 1) {
                TextButton nextButton = new TextButton(">", game.getResourceHandler().getMonitorSkin());
                nextButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentPage++;
                        showIndexPage();
                    }
                });
                paginationTable.add(nextButton).right().padBottom(20);
            } else {
                paginationTable.add().width(40); // Placeholder for alignment
            }

            content.add(paginationTable).expandX().fillX().padBottom(30).padTop(10);
        }

        return content;
    }

    private Table createFishPage(String fishId) {
        Table content = new Table();

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

        Table pageTable = new Table();
        pageTable.add(scrollPane).expand().fill().padLeft(60).padRight(60).padTop(30).padBottom(30);

        return pageTable;
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
        // Clear current content
        Table backgroundTable = (Table)journalTable.getChild(0);
        backgroundTable.clearChildren();

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
