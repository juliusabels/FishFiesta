package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages all game resources like textures, fonts and level & fish data.
 * <p>
 * This class serves as a central resource management system for the game,
 * handling the loading, access, and disposal of assets using LibGDX's AssetManager.
 * It also manages level and fish data through dedicated manager classes.
 */
@Getter
@Slf4j
public class ResourceHandler {
    /** The LibGDX asset manager that handles the actual loading and caching of resources */
    private final AssetManager assetManager;

    /** Manager for game levels */
    private final LevelManager levelManager;

    /** Manager for fish data */
    private final FishManager fishManager;

    /** Path to the background texture */
    private static final String BACKGROUND_TEXTURE = assetFile("background.png");

    /** Path to the monitor UI skin */
    private static final String MONITOR_SKIN = assetFile("skin/monitor_skin.json");

    /** Path to the journal UI skin */
    private static final String JOURNAL_SKIN = assetFile("skin/journal_skin.json");

    /** Path to the large fish font */
    private static final String FISH_FONT_BIG = assetFile("font/fish_font_big.fnt");

    /** Path to the fish sprite atlas */
    private static final String FISH_SPRITES = assetFile("fishes/fishes.atlas");

    /**
     * Creates a new resource handler and initializes the managers.
     * Note: This constructor doesn't load any assets yet. Call {@link #loadResources()} to begin loading.
     */
    public ResourceHandler() {
        assetManager = new AssetManager();
        levelManager = new LevelManager();
        fishManager = new FishManager();
    }

    /**
     * Begins asynchronous loading of all game resources.
     * This method queues assets for loading but doesn't wait for completion.
     * Use {@link #updateLoading()} to check and progress the loading state.
     */
    public void loadResources() {
        log.info("Start loading assets");
        assetManager.load(BACKGROUND_TEXTURE, Texture.class);
        assetManager.load(MONITOR_SKIN, Skin.class, new SkinLoader.SkinParameter());
        assetManager.load(JOURNAL_SKIN, Skin.class, new SkinLoader.SkinParameter());
        assetManager.load(FISH_FONT_BIG, BitmapFont.class);
        assetManager.load(FISH_SPRITES, TextureAtlas.class);

        //Load fish ids on startup, to safe time later
        fishManager.findFishes();

        //Load level ids on startup, to safe time later
        levelManager.findLevels();
    }

    /**
     * @return The background texture for the game
     */
    public Texture getBackgroundTexture() {
        return assetManager.get(BACKGROUND_TEXTURE);
    }

    /**
     * @return The skin used for monitor UI elements
     */
    public Skin getMonitorSkin() {
        return assetManager.get(MONITOR_SKIN);
    }

    /**
     * @return The skin used for journal UI elements
     */
    public Skin getJournalSkin() {
        return assetManager.get(JOURNAL_SKIN);
    }

    /**
     * @return The fish font used for titles and everything that should be uppercase only
     */
    public BitmapFont getFishFontBig() {
        return assetManager.get(FISH_FONT_BIG);
    }

    /**
     * @return The texture atlas containing all fish sprites
     */
    public TextureAtlas getFishSprites() {
        return assetManager.get(FISH_SPRITES);
    }

    /**
     * Utility method to construct an asset file path
     *
     * @param fileName The name of the file within the assets directory
     * @return The full path string relative to the assets folder
     */
    public static String assetFile(String fileName) {
        return "assets/" + fileName;
    }

    /**
     * Gets a FileHandle for a level file
     *
     * @param fileName The name of the level file
     * @return FileHandle for accessing the level file
     */
    public static FileHandle levelFileHandle(String fileName) {
        return Gdx.files.internal("levels/" + fileName);
    }

    /**
     * Gets a FileHandle for a fish data file
     *
     * @param fileName The name of the fish data file
     * @return FileHandle for accessing the fish data file
     */
    public static FileHandle fishFileHandle(String fileName) {
        return Gdx.files.internal("fishes/" + fileName);
    }

    /**
     * @return The current loading progress as a value between 0 and 1
     */
    public float getLoadingProgress() {
        return assetManager.getProgress();
    }

    /**
     * Continues the asset loading process
     *
     * @return true if all assets have finished loading, false otherwise
     */
    public boolean updateLoading() {
        return assetManager.update();
    }

    /**
     * Releases all resources managed by this handler.
     * Should be called when the game is closing or resource handler is no longer needed.
     */
    public void dispose() {
        assetManager.dispose();
    }
}
