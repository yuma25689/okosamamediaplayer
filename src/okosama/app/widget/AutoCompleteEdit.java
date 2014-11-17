package okosama.app.widget;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class AutoCompleteEdit extends absWidget {
	
	public AutoCompleteEdit( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private AutoCompleteEditImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(AutoCompleteEditImpl impl) {
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
		impl = new AutoCompleteEditImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	public void setAdapter(ArrayAdapter<?> a)
	{
		impl.setAdapter(a);
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
