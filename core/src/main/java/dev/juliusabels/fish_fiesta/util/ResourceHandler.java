package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ResourceHandler {
    private final AssetManager assetManager;
    private final LevelManager levelManager;

    private static final String BACKGROUND_TEXTURE = assetFile("background.png");
    private static final String MONITOR_SKIN = assetFile("skin/monitor_skin.json");
    private static final String FISH_FONT_BIG = assetFile("font/fish_font_big.fnt");

    public ResourceHandler() {
        assetManager = new AssetManager();
        levelManager = new LevelManager();
    }

    public void loadResources() {
        log.info("Start loading assets");
        assetManager.load(BACKGROUND_TEXTURE, Texture.class);
        assetManager.load(MONITOR_SKIN, Skin.class, new SkinLoader.SkinParameter());
        assetManager.load(FISH_FONT_BIG, BitmapFont.class);

        // Initialize LevelManager and discover levels
        // This way level discovery is part of the loading process
        levelManager.discoverLevels();
    }

    public Texture getBackgroundTexture() {
        return assetManager.get(BACKGROUND_TEXTURE);
    }

    public Skin getMonitorSkin() {
        return assetManager.get(MONITOR_SKIN);
    }

    public BitmapFont getFishFontBig() {
        return assetManager.get(FISH_FONT_BIG);
    }

    public static String assetFile(String fileName) {
        return "assets/" + fileName;
    }

    public static String levelFile(String fileName) {
        return "levels/" + fileName;
    }

    public static FileHandle levelFileHandle(String fileName) {
        return Gdx.files.internal("levels/" + fileName);
    }

    public float getLoadingProgress() {
        return assetManager.getProgress();
    }

    public boolean updateLoading() {
        return assetManager.update();
    }

    public void dispose() {
        assetManager.dispose();
    }
}
