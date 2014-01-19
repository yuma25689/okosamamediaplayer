package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.SearchPanel;
import okosama.app.storage.FilterData;
import okosama.app.tab.TabPage;

public class FilterCurrentTabAction implements IViewAction {

	@Override
	public int doAction(Object param) {

		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		FilterData data = SearchPanel.getInstance().getFilterData();

		// 選択中のタブのフィルタリング
		act.getAdpStocker().setFilterData(act.getCurrentTabPageId(), data);
//		switch( act.getCurrentTabPageId() )
//		{
//		case TabPage.TABPAGE_ID_SONG:
//			break;
//		case TabPage.TABPAGE_ID_ALBUM:
//			break;
//		case TabPage.TABPAGE_ID_ARTIST:
//			break;
//		case TabPage.TABPAGE_ID_PLAYLIST:
//			break;
//		case TabPage.TABPAGE_ID_VIDEO:
//			break;
//		default:
//			// 種別がない場合は、表示させない
//		}
		// 検索パネルの削除
		SearchPanel.removeFromParent();
		return 0;
	}

}
