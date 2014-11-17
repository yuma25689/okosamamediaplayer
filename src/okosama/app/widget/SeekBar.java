package okosama.app.widget;

import okosama.app.R;
import okosama.app.action.IViewAction;
import android.app.Activity;
import android.view.View;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * ���̃A�v���P�[�V�����ŗ��p����V�[�N�o�[�̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class SeekBar extends absWidget {
	public SeekBar( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private SeekBarImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(SeekBarImpl impl) {
		this.impl = impl;
	}
	
	/**
	 * �L����������
	 * @param b
	 */
	@Override
	public void setEnabled( boolean b )
	{
		impl.setEnabled(b);
	}
	/**
	 * �\������
	 * @param b
	 */
	@Override
	public void setVisible( boolean b )
	{
		if( b )
		{
			impl.setVisibility(View.VISIBLE);
		}
		else
		{
			impl.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public int create() {
		// TODO �����Ɣėp���̂�������ɂł���͂�
		impl = new SeekBarImpl(activity);
		impl.setProgressDrawable(activity.getResources().getDrawable(R.drawable.selector_progress_image));
		impl.setIndeterminate(false);
		impl.setClickable(true);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	@Override
	public void configureAction()
	{
		if( actionMap.get( IViewAction.ACTION_ID_ONCLICKSEEK, null ) != null )
		{
			// impl.setOnSeekBarChangeListener();
			impl.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(
							android.widget.SeekBar seekBar, int progress,
							boolean fromUser) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStartTrackingTouch(
							android.widget.SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStopTrackingTouch(
							android.widget.SeekBar seekBar) {
						// TODO Auto-generated method stub
						actionMap.get( IViewAction.ACTION_ID_ONCLICKSEEK, null ).doAction(
								new Long(seekBar.getProgress()));
						
					}
				}
			);
//			impl.setOnClickListener(new View.OnClickListener() {
//	            public void onClick(View v) {
//	                // �N���b�N���̏���
//	            	actionMap.get( IViewAction.ACTION_ID_ONCLICK )
//	            		.doAction(v);
//	            }
//	        });
		}
	}
	
	public void setMax( int max )
	{
		impl.setMax( max );
	}
	public void setProgress( int val )
	{
		impl.setProgress( val );
	}
	public void setVisibility( int i )
	{
		impl.setVisibility( i );
	}
}
