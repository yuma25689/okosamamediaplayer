package okosama.app.widget;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.view.View;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class Edit extends absWidget {
	
	public Edit( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private EditImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(EditImpl impl) {
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
		impl = new EditImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	public void clearValue()
	{
		impl.setText(null);
	}
	public String getText()
	{
		String ret = null;
		if( null != impl.getText() )
		{
			ret = ((SpannableStringBuilder)impl.getText()).toString();
		}
		return ret;
	}
	public void setHint(int resId)
	{
		impl.setHint(resId);
	}
	public void setText(String strSong) {
		impl.setText(strSong);
		
	}
	
}
