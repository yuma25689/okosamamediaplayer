package okosama.app.widget;

import android.app.Activity;
import android.view.View;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class Label extends absWidget {
	
	public Label( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private LabelImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(LabelImpl impl) {
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
		impl = new LabelImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
