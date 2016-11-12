package xyz.hexagons.client.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BlurEffect {
	
	static final int SCALE = 2;
	
	final String VERT =
			"attribute vec4 "+ ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +

			"uniform mat4 u_projTrans;\n" +
			" \n" +
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +

			"void main() {\n" +
			"	vColor = "+ ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";

	final String FRAG = Gdx.files.internal("assets/shader/blur.fsh").readString();

	public int width = 1024;
	public int height = 768;
	public float power = 1f;
	public float darkness = 1f;
	public boolean depth = false;

	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera camera2;
	FrameBuffer buffer;
	FrameBuffer buffer2;
	ShaderProgram program;

	public BlurEffect(int width, int height, boolean depth) {
		program = new ShaderProgram(VERT, Gdx.files.internal("assets/shader/blur.fsh").readString());
		if (!program.isCompiled()) {
			System.err.println(program.getLog());
			System.exit(0);
		}
		if (program.getLog().length()!=0)
			System.out.println(program.getLog());


		program.begin();
		program.setUniformf("resolution", width, height);
		program.setUniformf("darkness", darkness);
		program.setUniformf("power", power);
		program.end();
		batch = new SpriteBatch();
		camera = new OrthographicCamera(width/SCALE, height/SCALE);
		camera2 = new OrthographicCamera(width, height);
		buffer = new FrameBuffer(Format.RGBA8888, (this.width = width)/SCALE, (this.height = height)/SCALE, this.depth = depth);
		buffer2 = new FrameBuffer(Format.RGBA8888, (this.width = width)/SCALE, (this.height = height)/SCALE, this.depth = depth);
	}

	public void bind(){
		buffer.bind();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, width/SCALE, height/SCALE);
	}

	public void bind2(){
		buffer2.bind();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, width/SCALE, height/SCALE);
	}

	public void unbind(){
		Gdx.gl.glViewport(0, 0, width, height);
		buffer.unbind();
	}

	public void unbind2(){
		Gdx.gl.glViewport(0, 0, width, height);
		buffer2.unbind();
	}

	public void render(Batch baatch){

		bind2();
		batch.begin();
		batch.setProjectionMatrix(camera.combined);

		batch.setShader(program);
		program.setUniformf("resolution", buffer.getWidth(), buffer.getHeight());
		program.setUniformf("darkness", darkness);
		program.setUniformf("power", power);

		batch.draw(buffer.getColorBufferTexture(), 0, 0, width/SCALE, height/SCALE);

		batch.setShader(null);
		batch.end();
		unbind2();

		batch.begin();
		batch.setProjectionMatrix(camera2.combined);
		batch.draw(buffer2.getColorBufferTexture(), 0, 0, width, height);
		batch.end();

	}

	public void setPower(float power){
		this.power = power;
	}

	public void setDarkness(float darkness){
		this.darkness = darkness;
	}

	public void resize(int width, int height){
		buffer.dispose();
		buffer = new FrameBuffer(Format.RGBA8888, (this.width = width)/SCALE, (this.height = height)/SCALE, depth);
		buffer2 = new FrameBuffer(Format.RGBA8888, (this.width = width)/SCALE, (this.height = height)/SCALE, depth);
		camera.setToOrtho(true, width/SCALE, height/SCALE);
		camera2.setToOrtho(true, width, height);
	}


}
