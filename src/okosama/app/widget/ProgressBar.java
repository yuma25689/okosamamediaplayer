package okosama.app.widget;

import okosama.app.action.IViewAction;
import android.app.Activity;
import android.view.View;

/**
 * ���̃A�v���P�[�V�����ŗ��p����v���O���X�o�[�̃n���h��
 * ->SeekBar�Ɗ��Ⴂ���Ă����悤�Ȃ̂ŁA�i�v�ɗ��p����Ȃ���������Ȃ�
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class ProgressBar extends absWidget {
	public ProgressBar( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private ProgressBarImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ProgressBarImpl impl) {
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
			impl.setVisibility(View.GONE);
		}
	}
	@Override
	public int create() {
		// TODO �����Ɣėp���̂�������ɂł���͂�
		impl = new ProgressBarImpl(activity);
		//impl.setProgressDrawable(activity.getResources().getDrawable(R.drawable.progress_image));
		// impl.setIndeterminate(false);
		// impl.setClickable(true);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	@Override
	public void configureAction()
	{
		if( actionMap.get( IViewAction.ACTION_ID_ONCLICK, null ) != null )
		{
			// impl.setOnSeekBarChangeListener();
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
