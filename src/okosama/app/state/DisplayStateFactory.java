package okosama.app.state;

import okosama.app.tab.TabPage;

public class DisplayStateFactory {

	/**
	 * 指定された画面IDの画面状態クラスを作成して返却する
	 * 引数(画面状態クラスを作成するのに必要な情報)が画面IDだけで良いのかは微妙
	 * TODO:また、タブの階層が表現できていないので、タブの階層を表現するために、状態は階層構造にする必要があるかもしれない
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
			//画面IDが分からない場合、とりあえず再生画面へ飛ばす？
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
