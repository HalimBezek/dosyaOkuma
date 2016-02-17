package com.example.dosyaokuma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.security.Signer;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


public class DosyaMain extends BaseGameActivity 
{
	private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
	private static final float Y = 0;
	private static final float X = 0;
	private static final String FILENAME = "siir.txt";
    private Camera camera;
    private Engine engine;
    private PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_DEATH_STAR_I), false);
    private PhysicsWorld physicsWorldd = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_DEATH_STAR_I), false);
    private FixtureDef fixDef = PhysicsFactory.createFixtureDef(0.5f, 0.5f, 0.5f);
	Scene sahne;
	
	private Texture texSaha, texOyuncu1, texOyuncu2, texOyuncu3;
	private TextureRegion texRegSaha, texRegOyuncu1, texRegOyuncu2, texRegOyuncu3;
	private Sprite spriteSaha, spriteOyuncu1, spriteOyuncu2, spriteOyuncu3;
	
	
	private Body bodyOyuncu1, bodyOyuncu2, bodyOyuncu3,bodyOyuncu4;
	
	private Body[] body;
	private PhysicsWorld []physicsWorld2;
	
	private PhysicsWorld physicsWorld1;
	protected Context context=this;
    
	@Override
	public Engine onLoadEngine() 
	{
		// TODO Auto-generated method stub
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camera);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        engine = new Engine(engineOptions);
		
		return engine;
	}

	@Override
	public void onLoadResources() 
	{
		// TODO Auto-generated method stub
		
		// Texture nesneleri oluþturuluyor
		texSaha = new Texture(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);	
		texOyuncu1 = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texOyuncu2 = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texOyuncu3 = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		// TextureRegion nesneleri oluþturuluyor
		texRegSaha = TextureRegionFactory.createFromAsset(texSaha, this, "gfx/Arkaplan.jpg", 0, 0);
		texRegOyuncu1 = TextureRegionFactory.createFromAsset(texOyuncu1, this, "gfx/kol1.png", 0, 0);
		texRegOyuncu2 = TextureRegionFactory.createFromAsset(texOyuncu2, this, "gfx/kol2.png", 0, 0);
		texRegOyuncu3 = TextureRegionFactory.createFromAsset(texOyuncu2, this, "gfx/kol2.png", 0, 0);
		
		Texture [] textures = {texSaha, texOyuncu1, texOyuncu2, texOyuncu3};
		// Texture nesneleri yþkleniyor.
		mEngine.getTextureManager().loadTextures(textures);
	}
	
	@Override
	public Scene onLoadScene() 
	{
		// TODO Auto-generated method stub
		
		this.engine.registerUpdateHandler(new FPSLogger());
		this.sahne = new Scene();
		this.physicsWorld1= new PhysicsWorld(new Vector2(0,SensorManager.GRAVITY_EARTH), false);			

		
		// Sprite nesnesi oluþturuluyor
		spriteSaha = new Sprite(0,0,texRegSaha);
		
		// SpriteOyuncu1 oluþturuluyor. 
		spriteOyuncu1 = new Sprite(600, 32, texRegOyuncu1);
		
		
		// SpriteOyuncu2 oluþturuluyor.
		spriteOyuncu2 = new Sprite(600, 480 - 160, texRegOyuncu2){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) 
            {
				
						// TODO Auto-generated method stub
				
				
							try {
								writeToFile("oyun2.txt", "yazýlacak yazý");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

				
				return true;
				
            }
			
			
		};
		spriteOyuncu3 = new Sprite(600, 480 - 160, texRegOyuncu3);
		
		
		
		
		
		Sprite[] sprites ={spriteOyuncu3,spriteOyuncu2};
		
		
	
		
		// Body nesneleri oluþturuluyor
		bodyOyuncu1 = PhysicsFactory.createCircleBody(physicsWorld,spriteOyuncu1.getX()+64,spriteOyuncu1.getY()+64,47,0,BodyType.DynamicBody, fixDef);
		bodyOyuncu2 = PhysicsFactory.createCircleBody(physicsWorldd,spriteOyuncu2.getX()+64,spriteOyuncu2.getY()+64,47,0,BodyType.DynamicBody, fixDef);
		bodyOyuncu3 = PhysicsFactory.createCircleBody(physicsWorld,spriteOyuncu3.getX()+64,spriteOyuncu3.getY()+64,47,0,BodyType.DynamicBody, fixDef);
		
		// Body ve Sprite nesneleri birbirlerine baþlanþyor
		this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(spriteOyuncu1, bodyOyuncu1, true, true));
		
		this.physicsWorldd.registerPhysicsConnector(new PhysicsConnector(spriteOyuncu2, bodyOyuncu2, true, true));

		this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(spriteOyuncu3, bodyOyuncu3, true, true));
		
		// Hþz verme iþlemleri yapþlþyor
		bodyOyuncu1.setLinearVelocity(-10, 5);
		//bodyOyuncu2.setLinearVelocity(-10, -5);
		bodyOyuncu3.setLinearVelocity(-10, -5);
		
		// Sprite nesneleri ekrana(sahneye) þizdiriliyor.
		this.sahne.attachChild(spriteSaha);
		this.sahne.attachChild(spriteOyuncu1);
		this.sahne.attachChild(spriteOyuncu2);
		this.sahne.attachChild(spriteOyuncu3);
		
		this.sahne.registerTouchArea(spriteOyuncu2);
		
		this.sahne.registerUpdateHandler(physicsWorld);
		this.sahne.registerUpdateHandler(physicsWorldd);
		
		return this.sahne;
	}



	
		// TODO Auto-generated method stub
	
		 private String writeToFile(String name,String text)  throws IOException{
			
			
	
			
			 
		 BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(getFilesDir()+File.separator+name)));
         bufferedWriter.write(text);
         bufferedWriter.close();
            
            
			 int karekterSayisi=0;
           BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(getFilesDir()+File.separator+name)));
           String read;
           StringBuilder builder = new StringBuilder(" ");
        
           while((read = bufferedReader.readLine()) != null){
           builder.append(read);
                   	  
           }
           Log.d("Output", builder.toString());
           //String yazi=bufferedReader.readLine(); 
           bufferedReader.close();
          
              
              
           	  alert(builder.toString(),karekterSayisi);
            
           	 return   builder.toString();
           
           
}

	private void alert(final String yazi, final int karekterSayisi) {
			// TODO Auto-generated method stub
		final String al=yazi;
		
		runOnUiThread(new Runnable() {
			public void run() {
				// String y = builder.toString();
		           char[] c = new char [400];
		              c=yazi.toCharArray();
		              char ch =c[7];
		              char[] c1;
		              sekil dizi = new sekil();
		              c1=dizi.sekiloyun;
		              char c2=c1[2];
		              
		
final Builder  alert = new AlertDialog.Builder(context);
alert.setTitle("PUANINIZ! ");

alert.setMessage("     Oyun Puaný : "+(int)c2+"dir"  );

alert.setCancelable(false);
alert.setPositiveButton("SEÇENEKLER", new DialogInterface.OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
	//	startActivity(new Intent(besliSayfasi.this, seceneklerSayfasi.class));

		
	}

	});
alert.setNegativeButton("YENÝDEN OYNA!", new DialogInterface.OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
	 //  startActivity(new Intent(null, null, context, besliSayfasi.class));
		
	}
});
alert.show();
}

});
		}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
	}   
}
