package okosama.app.anim;

// import android.view.animation.Animation;
import okosama.app.ControlDefs;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.MotionObserver.MagneticFieldValue;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class TabAnimationFactory {

	public static AnimationSet createTabInAnimation(int nLevel,long nDuration)
	{
		MagneticFieldValue mag =
		OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
	
		int random = (int)Math.random() * (nLevel*2) + 5;
	    int iFromX = -1 * (int)( OkosamaMediaPlayerActivity.dispInfo.getBkImageWidth());;
	    float fromY = 0;
	    AnimationSet set = new AnimationSet(true);

	    if( 1 < nLevel )
		{
			float azimuth = (float) mag.getAzimuth();
			float rate = azimuth / 360f;
		    fromY = rate * 100;
		    float rotateY = ControlDefs.LIST_HEIGHT_2 * rate;
		    Log.d("animin","azimuthY=" + fromY );
		    int iRate = (int)fromY;
		    boolean direction = (iRate % 2 == 0);
		    int iDirection = 1;
		    if( direction )
		    {
		    	iFromX = (int)( OkosamaMediaPlayerActivity.dispInfo.getBkImageWidth());
		    	iDirection = -1;
		    	// azimuth *= -1;
		    }
			RotateAnimation rotate = new RotateAnimation((float) iDirection * 360 * random,0,iFromX,rotateY);
			set.addAnimation(rotate);
		}
		TranslateAnimation translate1 = new TranslateAnimation(iFromX, 0, fromY, 0);
	    
	    //float fromY = (float) mag.getAzimuth();
//		TranslateAnimation translate2 = new TranslateAnimation(-50, 0, -50, 0);
//		translate2.setDuration(50);
		set.addAnimation(translate1);
		if( 2 < nLevel )
		{
		    ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1);
			set.addAnimation(scale);
		}
		set.setDuration(nDuration);
		
//		BounceInterpolator bound = new BounceInterpolator();
//		set.setInterpolator(bound);
		
		return set;
	}
	public static AnimationSet createTabOutAnimation(int nLevel,long nDuration)
	{
	    AnimationSet set = new AnimationSet(true);
	    int iToX = (int)( OkosamaMediaPlayerActivity.dispInfo.getBkImageWidth());
	    float toY = 0;
		
	    
	    RotateAnimation rotate = null;
	    ScaleAnimation scale = null;

	    if( 1 < nLevel )
		{
			MagneticFieldValue mag =
			OkosamaMediaPlayerActivity.getResourceAccessor().motionObserver. getNowMagnetic();
			//float azimuth = (float) mag.getAzimuth();
			int random = (int)Math.random() * 2 + 3;
		
			float pitch = (float)mag.getPitch();
		    float rate = pitch / 360f;
		    toY = -1 * rate * 100;
		    float rotateY = ControlDefs.LIST_HEIGHT_2 * rate;
		    // float toY = (float) mag.getPitch();
		    Log.d("animin","pitchY=" + toY );
		    int iRate = (int)toY;
		    boolean direction = (iRate % 2 == 0);
		    int iDirection = 1;
		    if( direction )
		    {
		    	iToX = -1 * (int)( OkosamaMediaPlayerActivity.dispInfo.getBkImageWidth());
		    	iDirection = -1;
		    	//azimuth *= -1;
		    }
		    if( 2 < nLevel )
		    {
				rotate = new RotateAnimation(0,180 * iDirection * random,iToX, rotateY);
			    scale = new ScaleAnimation(1, random, 1, random);
		    }
		}
	    
		TranslateAnimation translate1 = new TranslateAnimation(0, iToX, 0, toY);
		set.addAnimation(translate1);
		if(2 < nLevel)
		{
			set.addAnimation(rotate);
			set.addAnimation(scale);
		}
		set.setFillAfter(false);
		set.setFillEnabled(true);
		set.setDuration(nDuration);
		
		return set;
	}
	
}
