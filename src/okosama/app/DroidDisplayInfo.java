package okosama.app;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

/**
 * ��ʏ��擾�p�N���X�̎���(android�p)
 * Dip->Pixel�ւ̕ϊ��ɗ��p
 * 1�s�N�Z���~metrics.scaledDensity=1dip
 * @author 25689
 *
 */
public final class DroidDisplayInfo {
	
	DisplayMetrics metrics;

	int titleBarHeightPixels;
	int statusBarHeightPixels;
	int clientHeightPixels;
	
	Activity activity;
	Handler handler;
	View viewForMeasureBarHeight;
	private static Drawable backgroundImgBase = null;
	double widthScaleCorrectDensity = 0;
	double heightScaleCorrectDensity = 0;
	
	public static final int CURRENT_BASE_DEVICE_HEIGHT = 859;
	
	public void clear()
	{
		titleBarHeightPixels = 
		statusBarHeightPixels =
		clientHeightPixels = 0;
		
		widthScaleCorrectDensity =
		heightScaleCorrectDensity = 0;

		metrics = new DisplayMetrics();
	}
	
	
	/**
	 * �������B��ʏ��̎擾�ɕK�v�ȏ������炢�A�����̉�ʏ����X�V����
	 * @param _activity
	 * @param _viewForMeasureBarHeight
	 */
	public void init( Activity _activity,
			View _viewForMeasureBarHeight,
			Handler handler )
	{
		this.activity = _activity;
		this.viewForMeasureBarHeight = _viewForMeasureBarHeight;
		this.handler = handler;
		if( activity != null )
		{
			// �T�C�Y�̃x�[�X�ƂȂ�摜���擾
			// ���Ƌ��������A���̉摜���X�N���[���ƍl�����ʒu�ɑ��̃R���|�[�l���g��z�u����
			// ���̉摜���ł̑��̃R���|�[�l���g�̈ʒu�͕����邪�A�v���O�������ł�density���l�����Ȃ���΂Ȃ�Ȃ�
			// density���l�������ꍇ�́A�␳�l���v�Z����
	        backgroundImgBase = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.background_3);
		}
		updateDisplayMetrics();
	}
	
	/**
	 * Activity�̉�ʂ�Metrics������Ɋi�[����
	 */
	public void updateDisplayMetrics()
	{
		clear();

		if( activity == null )
		{
			return;
		}

		
	    // �f�B�X�v���C���̎擾
	    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

	    if( viewForMeasureBarHeight == null )
	    {
	    	return;
	    }
	    
	    // �X�e�[�^�X�o�[�ƁA�^�C�g���o�[�̍������擾
	    // View����\���̏�Ԃ��ƁA���Ȃ��B
	    viewForMeasureBarHeight.post(new Runnable() {
		    @Override
			public void run() {
		    	// �������邱�ƂŁAView���n�܂��Ă��Ȃ��ꍇ�ł��AView���J�n���Ă�������擾�ł���H
		        Rect rect = new Rect();
		        // View����X�e�[�^�X�o�[�̍������擾
		        viewForMeasureBarHeight.getWindowVisibleDisplayFrame(rect);
		        statusBarHeightPixels = rect.top;
		        
		        // �X�e�[�^�X�o�[�ƃ^�C�g���o�[�̍��v�H��Window����擾
				int contentViewTopPx = 
					activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
				// �^�C�g���o�[�̍������擾
				titleBarHeightPixels = contentViewTopPx - statusBarHeightPixels;
			    // �N���C�A���g�̈�̍������擾
			    clientHeightPixels = metrics.heightPixels - titleBarHeightPixels - statusBarHeightPixels;
			    
		        // ���ۂ�Display�̍����Ɖ����ƁA�w�i�摜�̍����Ɖ����Ƃ̔䗦�����߂�
				// �x�[�X�摜�́A���i�v���O�����ɓ����Ă���O�j�̍����ƕ�
				double orgHeightOfBk 
					= (backgroundImgBase.getIntrinsicHeight()); /// metrics.density;
				double orgWidthOfBk 
					= (backgroundImgBase.getIntrinsicWidth());// / metrics.density;
				// �v���O�������ł̃N���C�A���g�̈�̃T�C�Y�ƁA���̉摜�̃T�C�Y�Ƃ̔䗦�����߂�
		        heightScaleCorrectDensity
		        	=  clientHeightPixels 
		        		/ orgHeightOfBk;
		        widthScaleCorrectDensity 
		        =  metrics.widthPixels 
		        		/ orgWidthOfBk;
				;
			    
				
				// handler�ɒʒm����
				Message msg = Message.obtain();
				msg.what = DisplayInfo.MSG_INIT_END;
				// msg.arg1 = DisplayInfo.MSG_INIT_END;
				handler.sendMessage( msg );
		    }
		});	    
	}

	/**
	 * density���l�����Ȃ������ꍇ�̈ʒu���A
	 * density���l�������ꍇ�̍��W�ɒ����ĕԋp����
	 * @param orgY density�l���O�̈ʒu
	 * @return density�l����̈ʒu
	 * 
	 */
	public int getCorrectionXConsiderDensity( int orgX )
	{
		int ret = (int)( widthScaleCorrectDensity * orgX );
		return ret;
	}	
	/**
	 * density���l�����Ȃ������ꍇ�̈ʒu���A
	 * density���l�������ꍇ�̍��W�ɒ����ĕԋp����
	 * @param orgY density�l���O�̈ʒu
	 * @return density�l����̈ʒu
	 * 
	 */
	public int getCorrectionYConsiderDensity( int orgY )
	{
		int ret = (int)( heightScaleCorrectDensity * orgY );
		return ret;
	}
	
	/**
	 * 
	 * @return �N���C�A���g�̈�̃T�C�Y
	 */
//	public Rect getClientRect()
//	{
//		Rect rect = new Rect();
//		rect.set( 0, 0, metrics.widthPixels, clientHeightPixels );
//		return rect;
//	}
	
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * @param viewForMeasureBarHeight the viewForMeasureBarHeight to set
	 */
	public void setViewForMeasureBarHeight(View viewForMeasureBarHeight) {
		this.viewForMeasureBarHeight = viewForMeasureBarHeight;
	}	
}
