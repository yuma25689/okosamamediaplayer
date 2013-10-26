package okosama.app.anim;

// import android.view.animation.Animation;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.MotionObserver.MagneticFieldValue;
import android.util.Log;
import android.view.animation.AnimationSet;
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
	    //float fromY = (float) mag.getAzimuth();
		TranslateAnimation translate1 = new TranslateAnimation(-100, 0, fromY, 0);
		translate1.setDuration(100);
//		TranslateAnimation translate2 = new TranslateAnimation(-50, 0, -50, 0);
//		translate2.setDuration(50);
		set.addAnimation(translate1); 
//		set.addAnimation(translate2);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		
		return set;
	}
	public static AnimationSet createTabOutAnimation()
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
	    float rate = ((float)mag.getPitch()) / 360f;
	    float toY = rate * 100;
	    // float toY = (float) mag.getPitch();
	    Log.d("animin","pitchY=" + toY );
		
	    AnimationSet set = new AnimationSet(true);
	    
		TranslateAnimation translate1 = new TranslateAnimation(0, 100, 0, toY);
		translate1.setDuration(100);
		set.addAnimation(translate1); 
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		return set;
	}
	
}