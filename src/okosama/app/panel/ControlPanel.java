package okosama.app.panel;

import okosama.app.tab.TabComponentParent;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * �R���g���[���p�̃E�B�W�F�b�g���܂Ƃ߂��p�l���̐e�N���X
 * @author 25689
 *
 */
public class ControlPanel extends TabComponentParent {	
	static ViewGroup parent;
	/**
	 * resume�O�ɐe���甲���Ă����Ȃ��ƁAresume���ɗ�����̂ŁA�d���Ȃ�
	 */
	public void removeViewFromParent()
	{
		if( parent != null && getView() != null )
			parent.removeView(getView());
	}
	
	public ControlPanel(Activity activity) {
		super(activity);
	}

	@Override
	public int create(int panelLayoutId) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	/**
	 * Panel��ԋp
	 */
	public View getView() {
		return tabBaseLayout;
	}
	
	public void setLayoutParams( ViewGroup.LayoutParams lp )
	{
		if( tabBaseLayout != null )
		{
			tabBaseLayout.setLayoutParams(lp);
		}
	}
}
