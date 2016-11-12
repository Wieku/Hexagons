package xyz.hexagons.client.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.slf4j.LoggerFactory;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public enum FontManager{
	MAIN("Orbitron-Medium"/*"Pixel-UniCode"*/, 128, 70, 78);
	//FF("ffforward"/*"Pixel-UniCode"*/, 16, 11, 12);
	protected static HashMap<FontManager, FontData> fonts = new HashMap<>();
	protected String name;
	protected int genSize;
	protected float widthScale;
	protected float heightScale;

	private FontManager(String name, int genSize, float widthScale, float heightScale) {
		this.name = name;
		this.genSize = genSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	public static BitmapFont getFont(FontManager val, int size) {
		BitmapFontData data = fonts.get(val).data, dataCopy = new BitmapFontData();

		try {
			for (Field field : dataCopy.getClass().getFields()) {
				field.setAccessible(true);
				field.set(dataCopy, data.getClass().getField(field.getName()).get(data));
			}
		} catch (Exception e) {
			throw new GdxRuntimeException("Failed to create font", e);
		}

		BitmapFont font = new BitmapFont(dataCopy, fonts.get(val).regions, false);
		font.getData().setScale((float) size / /*7*/val.widthScale, (float) size / /*8*/val.heightScale);
		return font;
	}


	public static void dispose() {
		fonts.clear();
	}



	public static void init() {

		LoggerFactory.getLogger(FontManager.class).info("Initializing FontManager...");

		for (FontManager val : values()) {
			FileHandle file = Gdx.files.internal("assets/fonts/" + val.name + ".ttf");
			String chars = "";
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(file.read()));

				for (int c = 0; c <= Character.MAX_CODE_POINT; c++) {
					if (font.canDisplay(c)) {
						chars += (char) c;
					}
				}

				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
				FreeTypeFontParameter pam = new FreeTypeFontParameter();
				pam.size = val.genSize;
				pam.genMipMaps = true;
				//pam.borderColor = Color.BLACK;//new Color(0.5f, 0.5f, 0.5f, 1f);
				pam.shadowColor = new Color(0.5f, 0.5f, 0.5f, 1f);
				//pam.borderWidth = 1f;
				//pam.borderColor = new Color(0.5f, 0.5f, 0.5f, 1f);
				pam.shadowOffsetX = 3;
				pam.shadowOffsetY = 3;
				pam.magFilter = TextureFilter.Linear;
				pam.minFilter = TextureFilter.MipMapLinearLinear;
				pam.characters = chars;

				FreeTypeBitmapFontData d = generator.generateData(pam);

				FontData fontData = new FontData();
				fontData.data = d;

				Field field = d.getClass().getDeclaredField("regions");
				field.setAccessible(true);

				fontData.regions = (Array<TextureRegion>) field.get(d);

				fonts.put(val, fontData);

				generator.dispose();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		LoggerFactory.getLogger(FontManager.class).info("FontManager initialized!");
	}
	static class FontData{
		public Array<TextureRegion> regions;
		public BitmapFontData data;
	}
}