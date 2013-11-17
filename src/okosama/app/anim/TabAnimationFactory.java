package okosama.app.anim;

// import android.view.animation.Animation;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.MotionObserver.MagneticFieldValue;
import android.util.Log;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class TabAnimationFactory {

	public static AnimationSet createTabInAnimation()
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
		
	    AnimationSet set = new AnimationSet(true);
	    float rate = ((float)mag.getAzimuth()) / 360f;
	    float fromY = rate * 100;
	    Log.d("animin","azimuthY=" + fromY );
	    int iRate = (int)fromY;
	    boolean direction = (iRate % 2 == 0);
	    int iFromX = -100;
	    if( direction )
	    {
	    	iFromX = 100;
	    }
	    //float fromY = (float) mag.getAzimuth();
		TranslateAnimation translate1 = new TranslateAnimation(iFromX, 0, fromY, 0);
//		TranslateAnimation translate2 = new TranslateAnimation(-50, 0, -50, 0);
//		translate2.setDuration(50);
		RotateAnimation rotate = new RotateAnimation((float) mag.getAzimuth(),0);
		set.addAnimation(translate1); 
		set.addAnimation(rotate);
		set.setDuration(200);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		
		return set;
	}
	public static AnimationSet createTabOutAnimation()
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
	    float rate = ((float)mag.getPitch()) / 360f;
	    float toY = -1 * rate * 100;
	    // float toY = (float) mag.getPitch();
	    Log.d("animin","pitchY=" + toY );
	    int iRate = (int)toY;
	    boolean direction = (iRate % 2 == 0);
	    int iToX = 100;
	    if( direction )
	    {
	    	iToX = -100;
	    }
		
	    AnimationSet set = new AnimationSet(true);
	    
		TranslateAnimation translate1 = new TranslateAnimation(0, iToX, 0, toY);
		RotateAnimation rotate = new RotateAnimation(0,(float) mag.getAzimuth());
		
		set.addAnimation(translate1);
		set.addAnimation(rotate);
		set.setDuration(200);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		return set;
	}
	
}
