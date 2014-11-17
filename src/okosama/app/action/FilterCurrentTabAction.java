package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.SearchPanel;
import okosama.app.storage.FilterData;

public class FilterCurrentTabAction implements IViewAction {

	@Override
	public int doAction(Object param) {

		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		FilterData data = SearchPanel.getInstance().getFilterData();

		// �I�𒆂̃^�u�̃t�B���^�����O
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
//			// ��ʂ��Ȃ��ꍇ�́A�\�������Ȃ�
//		}
		// �����p�l���̍폜
		SearchPanel.removeFromParent();
		return 0;
	}

}
