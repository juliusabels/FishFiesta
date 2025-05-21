package dev.juliusabels.fish_fiesta.screens.journal;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

/**
 * Creates and manages the interactive fish journal overlay for the game.
 * <p>
 * The journal provides a comprehensive encyclopedia of fish species that players can
 * browse through to learn about different water creatures. It features:
 * <ul>
 *   <li>An index page with paginated fish listings</li>
 *   <li>Individual fish detail pages with images and descriptions</li>
 * </ul>
 * <p>
 * The journal can be toggled with the 'J' key during gameplay, temporarily disabling
 * interaction with the main game screen. Fish pages are cached for performance.
 */
@Slf4j
public class JournalOverlay {
    /** Main game instance for accessing resources */
    private final FishFiestaGame game;

    /** Custom font for titles and labels */
    private final FishFontBig fishFont;

    /** Provides access to fish data and management */
    private final FishManager fishManager;

    /** Reference to the active screen's table to manage interaction state */
    private final Table activeScreenTable;

    /** Standard font for descriptive text */
    private final BitmapFont regularFont;

    /** Skin containing journal UI elements and styles */
    private final Skin journalSkin;

    /** Root table for the journal overlay */
    private final Table journalTable;

    /** Cache of created fish detail pages to avoid rebuilding them */
    private final Map<String, Table> fishPages = new HashMap<>();

    /** Flag indicating if the journal is currently visible */
    @Getter
    private boolean isVisible = false;

    /** Current page index in the fish index listing */
    private int currentPage = 0;

    /** Number of fish to display per index page */
    private static final int FISH_PER_PAGE = 14;

    /** Number of fish to display per column in the index */
    private static final int FISH_PER_COLUMN = 7;

    /** Width of the journal overlay in pixels */
    private static final int JOURNAL_WIDTH = 582;

    /** Height of the journal overlay in pixels */
    private static final int JOURNAL_HEIGHT = 400;

    /** Width of each column in the fish index */
    private static final float COLUMN_WIDTH = 230F;

    /**
     * Creates a new journal overlay.
     *
     * @param game The main game instance
     * @param activeScreenTable The content table of the active screen
     * @param mainStage The stage to which the overlay will be added
     */
    public JournalOverlay(FishFiestaGame game, Table activeScreenTable, Stage mainStage) {
        this.game = game;
        this.fishManager = game.getResourceHandler().getFishManager();
        this.fishFont = new FishFontBig(game);
        this.regularFont = new BitmapFont();
        this.regularFont.getData().setScale(0.8F);
        this.activeScreenTable = activeScreenTable;
        this.journalSkin = game.getResourceHandler().getJournalSkin();

        // Create journal container
        journalTable = new Table();
        journalTable.setFillParent(true);
        journalTable.align(Align.center);
        journalTable.setVisible(false);

        // Create background table with proper size
        Table backgroundTable = new Table();
        backgroundTable.setBackground(journalSkin.getDrawable("background"));
        backgroundTable.setSize(JOURNAL_WIDTH, JOURNAL_HEIGHT);

        // Add background to main journal table
        journalTable.add(backgroundTable).size(JOURNAL_WIDTH, JOURNAL_HEIGHT);

        mainStage.addActor(journalTable);
    }

    /**
     * Toggles the visibility of the journal overlay.
     * <p>
     * When shown, the journal displays the index page and disables interaction
     * with the main game screen. When hidden, interaction with the game is re-enabled.
     */
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

    /**
     * Creates the index page displaying all available fish in a paginated list.
     *
     * @return A table containing the fish index page layout
     */
    private Table createIndexPage() {
        Table content = new Table();
        Table fixedContainer = new Table();
        fixedContainer.setSize(JOURNAL_WIDTH, JOURNAL_HEIGHT);

        // Add title
        addIndexPageTitle(fixedContainer);

        // Add fish listings in two columns
        addFishListings(fixedContainer);

        // Add pagination controls if needed
        addPaginationControls(fixedContainer);

        content.add(fixedContainer).size(JOURNAL_WIDTH, JOURNAL_HEIGHT);
        return content;
    }

    /**
     * Adds the title to the index page.
     *
     * @param container The container to add the title to
     */
    private void addIndexPageTitle(Table container) {
        Label titleLabel = fishFont.createLabel("Fish Index", 1.2f);
        titleLabel.setColor(Color.BLACK);
        titleLabel.setPosition(60, 370);
        container.addActor(titleLabel);
    }

    /**
     * Adds the fish listings to the index page.
     *
     * @param container The container to add the listings to
     */
    private void addFishListings(Table container) {
        Table columnsTable = new Table();
        columnsTable.setPosition(45, 70);
        columnsTable.setSize(492, 260);

        Table leftColumn = new Table();
        Table rightColumn = new Table();

        List<String> fishIds = fishManager.getAllFishIds();
        int startIdx = currentPage * FISH_PER_PAGE;
        int endIdx = Math.min(startIdx + FISH_PER_PAGE, fishIds.size());

        for (int i = startIdx; i < endIdx; i++) {
            String fishId = fishIds.get(i);
            Table fishEntry = createFishEntry(fishId);

            // Add to left or right column based on index
            int relativeIndex = i - startIdx;
            if (relativeIndex < FISH_PER_COLUMN) {
                leftColumn.add(fishEntry).expandX().fillX().padBottom(10).row();
            } else {
                rightColumn.add(fishEntry).expandX().fillX().padBottom(10).row();
            }
        }

        columnsTable.add(leftColumn).width(COLUMN_WIDTH);
        columnsTable.add().width(80);
        columnsTable.add(rightColumn).width(COLUMN_WIDTH);
        container.addActor(columnsTable);
    }

    /**
     * Creates a fish entry for the index listing.
     *
     * @param fishId The ID of the fish to create an entry for
     * @return A table containing the fish entry
     */
    private Table createFishEntry(String fishId) {
        Table fishEntry = new Table();

        // Fish icon
        TextureRegion fishIcon = game.getResourceHandler().getFishTexture(fishId);
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

        // Add click handler to show fish details
        fishEntry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showFishPage(fishId);
            }
        });

        return fishEntry;
    }

    /**
     * Adds pagination controls to the index page if needed.
     *
     * @param container The container to add the controls to
     */
    private void addPaginationControls(Table container) {
        List<String> fishIds = fishManager.getAllFishIds();
        int totalPages = (int)Math.ceil(fishIds.size() / (float)FISH_PER_PAGE);

        if (totalPages <= 1) {
            return;
        }

        // Previous page button (left side)
        if (currentPage > 0) {
            Button prevButton = createNavigationButton("button_left", 50, 30, () -> {
                currentPage--;
                showIndexPage();
            });
            container.addActor(prevButton);
        }

        // Page indicator
        Label pageLabel = fishFont.createLabel((currentPage + 1) + "/" + totalPages, 0.9f);
        pageLabel.setColor(Color.BLACK);
        pageLabel.setPosition(275, 30);
        container.addActor(pageLabel);

        // Next page button (right side)
        if (currentPage < totalPages - 1) {
            Button nextButton = createNavigationButton("button_right", 500, 30, () -> {
                currentPage++;
                showIndexPage();
            });
            container.addActor(nextButton);
        }
    }

    /**
     * Creates a navigation button with the specified properties.
     *
     * @param styleName The name of the button style in the journal skin
     * @param x The x-coordinate for the button
     * @param y The y-coordinate for the button
     * @param action The action to perform when the button is clicked
     * @return The created button
     */
    private Button createNavigationButton(String styleName, float x, float y, Runnable action) {
        Button button = new Button(new Button.ButtonStyle(
            journalSkin.getDrawable(styleName),
            journalSkin.getDrawable(styleName + "-pressed"),
            null));
        button.sizeBy(3);
        button.setPosition(x, y);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    /**
     * Creates a detailed page for a specific fish.
     *
     * @param fishId The ID of the fish to create a page for
     * @return A table containing the fish detail page
     */
    private Table createFishPage(String fishId) {
        Table content = new Table();
        Table fixedContainer = new Table();
        fixedContainer.setSize(JOURNAL_WIDTH, JOURNAL_HEIGHT);

        // Load fish data
        if (!fishManager.loadFishForId(fishId)) {
            log.error("Could not load fish data for ID: {}", fishId);
            return createErrorPage("Failed to load fish data");
        }

        WaterCreature fish = fishManager.getCurrentFish();
        if (fish == null) {
            log.error("Fish data is null for ID: {}", fishId);
            return createErrorPage("Fish data is missing");
        }

        // Create left page with image
        Table leftPage = createFishPageLeftSide(fish, fishId);
        fixedContainer.addActor(leftPage);

        // Create right page with description
        Table rightPage = createFishPageRightSide(fish);
        fixedContainer.addActor(rightPage);

        // Add back button
        Button backButton = createNavigationButton("button_left", 30, 30, this::showIndexPage);
        fixedContainer.addActor(backButton);

        content.add(fixedContainer).size(JOURNAL_WIDTH, JOURNAL_HEIGHT);
        return content;
    }

    /**
     * Creates the left side of a fish detail page with image and title.
     *
     * @param fish The fish data to display
     * @param fishId The ID of the fish for image lookup
     * @return A table containing the left page layout
     */
    private Table createFishPageLeftSide(WaterCreature fish, String fishId) {
        Table leftPage = new Table();
        leftPage.setPosition(45, 50);

        // Add fish name as title
        Label titleLabel = fishFont.createLabel(fish.getName(), 1.2f);
        titleLabel.setColor(Color.BLACK);
        titleLabel.setPosition(0, 300);
        leftPage.addActor(titleLabel);

        // Add fish image with frame
        addFishImageWithFrame(leftPage, fishId);

        return leftPage;
    }

    /**
     * Adds a fish image with decorative frame to the specified container.
     *
     * @param container The container to add the image to
     * @param fishId The ID of the fish for image lookup
     */
    private void addFishImageWithFrame(Table container, String fishId) {
        TextureRegion fishTexture = game.getResourceHandler().getFishTexture(fishId);
        if (fishTexture == null) {
            return;
        }

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
        Image frameImage = new Image(journalSkin.getDrawable("fish_frame"));
        frameImage.setSize(frameSize, frameSize);
        frameImage.setPosition(centerX - 50, positionY);

        // Add both to container (fish first, then frame on top)
        container.addActor(fishImage);
        container.addActor(frameImage);
    }

    /**
     * Creates the right side of a fish detail page with description.
     *
     * @param fish The fish data to display
     * @return A table containing the right page layout
     */
    private Table createFishPageRightSide(WaterCreature fish) {
        Table rightPage = new Table();
        rightPage.setPosition(300, 50);
        rightPage.top().left(); // Align to top-left
        rightPage.setSize(240, 330);

        Label descLabel = new Label(JournalDescGenerator.generateFor(fish),
            new Label.LabelStyle(regularFont, Color.BLACK));
        descLabel.setWrap(true);
        rightPage.add(descLabel).width(240).top().left().padLeft(30);

        return rightPage;
    }

    /**
     * Creates an error page to display when fish data cannot be loaded.
     *
     * @param errorMessage The error message to display
     * @return A table containing the error page
     */
    private Table createErrorPage(String errorMessage) {
        Table content = new Table();
        Table fixedContainer = new Table();
        fixedContainer.setSize(JOURNAL_WIDTH, JOURNAL_HEIGHT);

        Label errorLabel = fishFont.createLabel("Error: " + errorMessage, 1.0f);
        errorLabel.setColor(Color.RED);
        errorLabel.setPosition(JOURNAL_WIDTH / 2F - 100, JOURNAL_HEIGHT / 2F);
        fixedContainer.addActor(errorLabel);

        Button backButton = createNavigationButton("button_left", 30, 30, this::showIndexPage);
        fixedContainer.addActor(backButton);

        content.add(fixedContainer).size(JOURNAL_WIDTH, JOURNAL_HEIGHT);
        return content;
    }

    /**
     * Shows the fish index page.
     * <p>
     * This method clears the current journal content and displays the paginated
     * fish index listing.
     */
    private void showIndexPage() {
        Table backgroundTable = (Table)journalTable.getChild(0);
        backgroundTable.clearChildren();
        backgroundTable.add(createIndexPage()).expand().fill();
    }

    /**
     * Shows a detailed page for a specific fish.
     * <p>
     * This method uses a caching mechanism to avoid recreating fish pages
     * that have already been viewed.
     *
     * @param fishId The ID of the fish to display
     */
    private void showFishPage(String fishId) {
        // Create and cache the fish page if it doesn't exist
        if (!fishPages.containsKey(fishId)) {
            fishPages.put(fishId, createFishPage(fishId));
        }

        // Clear current content and show the fish page
        Table backgroundTable = (Table)journalTable.getChild(0);
        backgroundTable.clearChildren();
        backgroundTable.add(fishPages.get(fishId)).expand().fill();
    }

    /**
     * Disposes of resources when the overlay is no longer needed.
     * <p>
     * This method should be called when the screen containing this overlay
     * is disposed to prevent memory leaks.
     */
    public void dispose() {
        fishFont.dispose();
        regularFont.dispose();
        fishPages.clear();
    }

    /**
     * Gets an input processor that handles journal toggle key presses.
     * <p>
     * This processor listens for the 'J' key to toggle the journal visibility.
     *
     * @return An InputAdapter that handles journal-related key presses
     */
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
