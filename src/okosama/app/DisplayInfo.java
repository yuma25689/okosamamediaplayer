package okosama.app;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

/**
 * ��ʏ��擾�p�N���X�̃n���h���B
 * @author 25689
 *
 */
public final class DisplayInfo {
	
	public static final int MSG_INIT_END = 100;
	
	// Singleton
	private static DisplayInfo instance = new DisplayInfo();
	private DisplayInfo() {}
	public static DisplayInfo getInstance() {
		return instance;
	}
	
	private DroidDisplayInfo _impl = new DroidDisplayInfo();
	
	/**
	 * ������ ���̊֐��͔�����android�ŗL�ɂȂ��Ă��܂������A�d���Ȃ��B
	 * @param activity
	 * @param viewForMeasureBarHeight
	 */
	public void init(Activity activity,
			View viewForMeasureBarHeight,
			Handler handler)
	{
		_impl.init(activity,viewForMeasureBarHeight,handler);
	}
	
	/**
	 * �T�C�Y�̕␳�l���擾
	 * @return
	 */
	public int getCorrectionXConsiderDensity( int orgX )
	{
		return _impl.getCorrectionXConsiderDensity( orgX );
	}
	public int getCorrectionYConsiderDensity( int orgY )
	{
		return _impl.getCorrectionYConsiderDensity( orgY );
	}
}
