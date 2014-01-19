package okosama.app.action;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

/**
 * 検索パネルのコントロールを切り替える
 * @author 25689
 *
 */
public class SearchPanelCtrlSwitchAction implements IViewAction {

	View vNowShow;
	View vNowHide;
	LayoutParams lpShow;
	
	public SearchPanelCtrlSwitchAction(View nowShow, View nowHide, LayoutParams lp )
	{
		setActiveView( nowShow, nowHide, lp );
	}
	
	public void setActiveView(View nowShow, View nowHide, LayoutParams lp )
	{
		vNowShow = nowShow;
		vNowHide = nowHide;
		lpShow = lp;
	}
	
	@Override
	public int doAction(Object param) {
		
		if( vNowShow != null && vNowHide != null )
		{
			vNowShow.setVisibility(View.GONE);
			vNowHide.setLayoutParams(lpShow);
			vNowHide.setVisibility(View.VISIBLE);
			// swap
			View vTmp = vNowShow;
			vNowShow = vNowHide;
			vNowHide = vTmp;
		}
		
		return 0;
	}

}
