package okosama.app.action;

import okosama.app.widget.absWidget;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * �����p�l���̃R���g���[����؂�ւ���
 * @author 25689
 *
 */
public class SearchPanelCtrlSwitchAction implements IViewAction {

	absWidget vNowShow;
	absWidget vNowHide;
	LayoutParams lpShow;
	
	public SearchPanelCtrlSwitchAction(absWidget nowShow, absWidget nowHide, LayoutParams lp )
	{
		setActiveView( nowShow, nowHide, lp );
	}
	
	public void setActiveView(absWidget nowShow, absWidget nowHide, LayoutParams lp )
	{
		vNowShow = nowShow;
		vNowHide = nowHide;
		lpShow = lp;
	}
	
	@Override
	public int doAction(Object param) {
		
		if( vNowShow != null && vNowHide != null )
		{
			vNowShow.clearValue();
			vNowShow.setVisible(View.GONE);
			vNowHide.setLayoutParams(lpShow);
			vNowHide.setVisible(View.VISIBLE);
			// swap
			absWidget vTmp = vNowShow;
			vNowShow = vNowHide;
			vNowHide = vTmp;
		}
		
		return 0;
	}

}
