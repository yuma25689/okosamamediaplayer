package okosama.app.tab.media;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabPage;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

/**
 * タブを模倣したクラス。このクラスは表示を持たない。
 * タブというよりは、Mediatorに近い。
 * タブの作成および、タブ上のコンポーネントの有効/無効、表示/非表示のみを制御する
 * @author 25689
 *
 */
public class TabMediaSelect extends Tab {
	// このタブのボタンの大きさ
	public static final int BUTTON_WIDTH = 90;
	public static final int BUTTON_HEIGHT = 90;
	// このタブのフッタの大きさ
	//static final int HOOTER_SIZE = BUTTON_HEIGHT + Tab.HDR_SIZE;
	
	/**
	 * コンストラクタ 親とほぼ同様 タブIDに、TAB_ID_MEDIAを利用する
	 * @param ID
	 * @param ll
	 * @param rl
	 */
	public TabMediaSelect(int ID, LinearLayout ll, ViewGroup rl) {
		super(ControlIDs.TAB_ID_MEDIA, ll, rl);
	}

	/**
	 * タブ全体の作成
	 * @return 0:正常 0以外:異常
	 */
	@Override
	public int create(int panelLayoutId) {
		int errCode = 0;
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

		// タブのパネルを作成
		LayoutInflater inflator = act.getLayoutInflater();
		
		if( false == OkosamaMediaPlayerActivity.getResourceAccessor().isSdCanRead() )
		{
			// SDカードが読めない状態の場合、それを表示するビューを作っておしまい
			OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(false);
			tabBaseLayout = (ViewGroup)inflator.inflate(R.layout.tab_layout_sdcard_cant_read, null, false);
		}
		else
		{
			// フラグONのタイミングが早いが、この際それでいい
			OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(true);
			tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutId, null, false);
			// パネル位置の設定(FILL_PARENT)
			RelativeLayout.LayoutParams lp 
			= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
	        		0, 0 
	        );
			tabBaseLayout.setLayoutParams(lp);
			// タブボタンを置くヘッダとなるレイアウト
			RelativeLayout rlHooter = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_hooter);
			// アルバムタブボタン
			
			mapBtn.put( TabPage.TABPAGE_ID_ALBUM, DroidWidgetKit.getInstance().MakeButton() );
			TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.ALBUM_TAB_BUTTON, null, 
				ComponentType.BUTTON, 
				0, 0, // DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE,
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.music_select_album_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_ALBUM).acceptConfigurator(tabBtnCreationData);
			SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_ALBUM ) );
			TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_ALBUM).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_ALBUM).getView());
	
			// アーティストタブボタン
			mapBtn.put( TabPage.TABPAGE_ID_ARTIST, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.ARTIST_TAB_BUTTON, null, ComponentType.BUTTON, 
				BUTTON_WIDTH + 5, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.music_select_artist_image,
				R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_ARTIST).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_ARTIST ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_ARTIST).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_ARTIST).getView());
	
			// ソングタブ
			mapBtn.put( TabPage.TABPAGE_ID_SONG, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.SONG_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 2, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.music_select_song_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_SONG).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_SONG ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );		
			mapBtn.get(TabPage.TABPAGE_ID_SONG).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_SONG).getView());
			
			// プレイリストタブ
			mapBtn.put( TabPage.TABPAGE_ID_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.PLAYLIST_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 3, 0,//DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.music_select_playlist_image,
				R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_PLAYLIST ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).getView());
	
			// videoタブ
			mapBtn.put( TabPage.TABPAGE_ID_VIDEO, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.VIDEO_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 4, 0, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.video_select_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_VIDEO).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_VIDEO ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );		
			mapBtn.get(TabPage.TABPAGE_ID_VIDEO).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_VIDEO).getView());
		
			
			RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
			// タブの追加
			addChild( TabPage.TABPAGE_ID_ALBUM, new TabPageAlbum( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_ARTIST, new TabPageArtist( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_SONG, new TabPageSong( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_PLAYLIST, new TabPagePlayList( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_VIDEO, new TabPageVideo( this, pageContainer, rlCont ) );
			//rlHooter.setBackgroundResource(R.color.gradiant_test4);
			rlCont.setBackgroundResource(R.color.gradiant_tab_base);
		}
		
		// タブのパネルを親から与えられたレイアウトに追加
		componentContainer.addView(tabBaseLayout);
				
		return errCode;
	}

}
