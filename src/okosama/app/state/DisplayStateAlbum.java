package okosama.app.state;



import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateAlbum extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �A���o���ʑI����ʂւ̐؂�ւ�
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_ALBUM );
		action.doAction(null);
		
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(OkosamaMediaPlayerActivity.tabNameMedia, false);
		return ret;
	}

}
