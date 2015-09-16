package me.wieku.hexagons.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BlurEffect {

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
	FrameBuffer buffer;
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
		camera = new OrthographicCamera(width, height);
		buffer = new FrameBuffer(Format.RGBA8888, this.width = width, this.height = height, this.depth = depth);
	}

	public void bind(){
		buffer.bind();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void unbind(){
		buffer.unbind();
	}

	public void render(Batch baatch){
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(program);
		program.setUniformf("resolution", width, height);
		program.setUniformf("darkness", darkness);
		program.setUniformf("power", power);
		batch.draw(buffer.getColorBufferTexture(), 0, 0, width, height);
		batch.setShader(null);
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
		buffer = new FrameBuffer(Format.RGBA8888, this.width = width, this.height = height, depth);
		camera.setToOrtho(true, width, height);
	}


}
