package okosama.app.anim;

// import android.view.animation.Animation;
import okosama.app.ControlDefs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.MotionObserver.MagneticFieldValue;
import android.util.Log;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class TabAnimationFactory {

	public static AnimationSet createTabInAnimation()
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
		
		int random = (int)Math.random() * 5 + 5;
		float azimuth = (float) mag.getAzimuth();
	    float rate = azimuth / 360f;
	    AnimationSet set = new AnimationSet(true);
	    float fromY = rate * 100;
	    float rotateY = ControlDefs.LIST_HEIGHT_2 * rate;
	    
	    Log.d("animin","azimuthY=" + fromY );
	    int iRate = (int)fromY;
	    boolean direction = (iRate % 2 == 0);
	    int iFromX = -100;
	    int iDirection = 1;
	    if( direction )
	    {
	    	iFromX = 100;
	    	iDirection = -1;
	    	// azimuth *= -1;
	    }
	    //float fromY = (float) mag.getAzimuth();
		TranslateAnimation translate1 = new TranslateAnimation(iFromX, 0, fromY, 0);
//		TranslateAnimation translate2 = new TranslateAnimation(-50, 0, -50, 0);
//		translate2.setDuration(50);
		RotateAnimation rotate = new RotateAnimation((float) iDirection * 360 * random,0,iFromX,rotateY);
	    ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1);
		set.addAnimation(rotate);
		set.addAnimation(translate1);
		set.addAnimation(scale);
		
		set.setDuration(300);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		
		return set;
	}
	public static AnimationSet createTabOutAnimation()
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
		float azimuth = (float) mag.getAzimuth();
		int random = (int)Math.random() * 2 + 3;
		
		float pitch = (float)mag.getPitch();
	    float rate = pitch / 360f;
	    float toY = -1 * rate * 100;
	    float rotateY = ControlDefs.LIST_HEIGHT_2 * rate;
	    // float toY = (float) mag.getPitch();
	    Log.d("animin","pitchY=" + toY );
	    int iRate = (int)toY;
	    boolean direction = (iRate % 2 == 0);
	    int iDirection = 1;
	    int iToX = 100;
	    if( direction )
	    {
	    	iToX = -100;
	    	iDirection = -1;
	    	azimuth *= -1;
	    }
		
	    AnimationSet set = new AnimationSet(true);
	    
		TranslateAnimation translate1 = new TranslateAnimation(0, iToX, 0, toY);
		RotateAnimation rotate = new RotateAnimation(0,180 * iDirection * random,iToX, rotateY);
	    ScaleAnimation scale = new ScaleAnimation(1, random, 1, random);
		
		set.addAnimation(rotate);
		set.addAnimation(translate1);
		set.addAnimation(scale);
		set.setDuration(300);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		return set;
	}
	
}
