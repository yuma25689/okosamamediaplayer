package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.tab.*;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class Image extends TabLeaf {
		
	public Image( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private ImageImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ImageImpl impl) {
		this.impl = impl;
	}
	
	/**
	 * �L����������
	 * @param b
	 */
	public void setEnabled( boolean b )
	{
		impl.setEnabled(b);
	}

	@Override
	public int create() {
		// TODO �����Ɣėp���̂�������ɂł���͂�
		impl = new ImageImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
