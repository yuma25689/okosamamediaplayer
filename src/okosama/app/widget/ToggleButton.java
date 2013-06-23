package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import okosama.app.action.IViewAction;
import okosama.app.tab.*;

/**
 * ���̃A�v���P�[�V�����ŗ��p����{�^���̃n���h��
 * Bridge�p�^�[����K�p
 * @author 25689
 *
 */
public class ToggleButton extends TabLeaf {
	
	
	public ToggleButton( Activity activity )
	{
		super( activity );
		create();
	}
	
	/**
	 * �����N���X
	 */
	private ToggleButtonImpl impl;

	/**
	 * �����N���X�̐ݒ�
	 * @param impl
	 */
	public void setImpl(ToggleButtonImpl impl) {
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
		impl = new ToggleButtonImpl(activity);
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	@Override
	public void configureAction()
	{
		if( actionMap.containsKey( IViewAction.ACTION_ID_ONTOGGLEON ) 
		&& actionMap.containsKey( IViewAction.ACTION_ID_ONTOGGLEOFF ))
		{
			impl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton v, boolean isChecked) {
	                // �N���b�N���̏���
					if( isChecked )
					{
		            	actionMap.get( IViewAction.ACTION_ID_ONTOGGLEON )
		            		.doAction(v);
					}
					else
					{
		            	actionMap.get( IViewAction.ACTION_ID_ONTOGGLEOFF )
	            		.doAction(v);						
					}
	            }
	        });
		}
	}
}
