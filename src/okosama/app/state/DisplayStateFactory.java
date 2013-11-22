package okosama.app.state;

import okosama.app.tab.TabPage;

public class DisplayStateFactory {

	/**
	 * �w�肳�ꂽ���ID�̉�ʏ�ԃN���X���쐬���ĕԋp����
	 * ����(��ʏ�ԃN���X���쐬����̂ɕK�v�ȏ��)�����ID�����ŗǂ��̂��͔���
	 * TODO:�܂��A�^�u�̊K�w���\���ł��Ă��Ȃ��̂ŁA�^�u�̊K�w��\�����邽�߂ɁA��Ԃ͊K�w�\���ɂ���K�v�����邩������Ȃ�
	 * @param iDispId
	 * @return
	 */
	public static IDisplayState createDisplayState( int iDispId )
	{
		IDisplayState state = null;
		switch( iDispId )
		{
		case TabPage.TABPAGE_ID_NONE:
			state = new DisplayStateNone();
			break;
		case TabPage.TABPAGE_ID_UNKNOWN:
			//���ID��������Ȃ��ꍇ�A�Ƃ肠�����Đ���ʂ֔�΂��H
			// break;
		case TabPage.TABPAGE_ID_PLAY:
			state = new DisplayStatePlay();
			break;
		case TabPage.TABPAGE_ID_PLAY_SUB:
			state = new DisplayStatePlaySub();
			break;
		case TabPage.TABPAGE_ID_MEDIA:
			state = new DisplayStateMedia();
			break;
		case TabPage.TABPAGE_ID_ARTIST:
			state = new DisplayStateArtist();
			break;
		case TabPage.TABPAGE_ID_ALBUM:
			state = new DisplayStateAlbum();
			break;
		case TabPage.TABPAGE_ID_SONG:
			state = new DisplayStateSong();
			break;
		case TabPage.TABPAGE_ID_PLAYLIST:
			state = new DisplayStatePlaylist();
			break;
		case TabPage.TABPAGE_ID_NOW_PLAYLIST:
			state = new DisplayStateNowPlaylist();
			break;		
		}
		return state;
	}
}
