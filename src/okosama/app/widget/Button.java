package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.action.IViewAction;
import okosama.app.tab.*;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class Button extends absWidget {
	public Button( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private ButtonImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ButtonImpl impl) {
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
		impl = new ButtonImpl(activity);
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
			impl.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // �N���b�N���̏���
	            	actionMap.get( IViewAction.ACTION_ID_ONCLICK )
	            		.doAction(v);
	            }
	        });
		}
	}
}
