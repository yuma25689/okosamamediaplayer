package okosama.app.action;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;

/**
 * �^�u��I���������Ɏ��s����A�N�V����
 * @author 25689
 *
 */
public final class TabSelectAction implements IViewAction {

	public static final int MSG_ID_TAB_SELECT = 101;
	int tabId = ControlIDs.ID_NOT_SPECIFIED;
	int tabPageId = TabPage.TABPAGE_ID_UNKNOWN;

	public TabSelectAction(int tabId, int tabPageId) {
		super();
		this.tabId = tabId;
		this.tabPageId = tabPageId;
	}

	/**
	 * 
	 */
	@Override
	public int doAction( Object param ) {
		if( tabId != ControlIDs.ID_NOT_SPECIFIED  && tabId != TabPage.TABPAGE_ID_UNKNOWN )
		{
			if( tabPageId == OkosamaMediaPlayerActivity.getCurrentDisplayId(tabId) )
			{
				return 0;
			}
			
			OkosamaMediaPlayerActivity.getResourceAccessor().playSound(6);
			// �ĊO���R�X�g��������Ȃ�
			// System.gc();
			// tabRoot.setCurrentTab(tabId, (tabId != TabPage.TABPAGE_ID_NONE) );
			OkosamaMediaPlayerActivity.setCurrentDisplayId(tabId,tabPageId);
			
			if( tabId != ControlIDs.ID_NOT_SPECIFIED && tabPageId != TabPage.TABPAGE_ID_NONE )
			{			
				// handler�ɒʒm����
				Message msg = Message.obtain();
				msg.what = MSG_ID_TAB_SELECT;
				// msg.arg1 = MSG_ID_TAB_SELECT;
				msg.arg2 = tabPageId;
				msg.obj = tabId;
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
