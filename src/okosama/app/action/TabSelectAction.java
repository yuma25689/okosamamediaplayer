package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * �^�u��I���������Ɏ��s����A�N�V����
 * @author 25689
 *
 */
public final class TabSelectAction implements IViewAction {

	public static final int MSG_ID_TAB_SELECT = 101;
	Tab tabRoot;
	int tabId = TabPage.TABPAGE_ID_UNKNOWN;

	public TabSelectAction(Tab tabRoot, int tabId) {
		super();
		this.tabRoot = tabRoot;
		this.tabId = tabId;
	}

	/**
	 * 
	 */
	@Override
	public int doAction( View v ) {
		if( tabRoot != null && tabId != TabPage.TABPAGE_ID_UNKNOWN )
		{
			// �ĊO���R�X�g��������Ȃ�
			// System.gc();
			tabRoot.setCurrentTab(tabId, true);
			
			if( tabId != TabPage.TABPAGE_ID_NONE )
			{			
				// handler�ɒʒm����
				Message msg = Message.obtain();
				msg.arg1 = MSG_ID_TAB_SELECT;
				msg.arg2 = tabId;
				msg.obj = tabRoot.getName();
                Handler hdr = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();                        
                if( hdr == null )
                {
                	return -1;
                }                		
                hdr.sendMessage( msg );
			}
		}
		return 0;
	}

}
