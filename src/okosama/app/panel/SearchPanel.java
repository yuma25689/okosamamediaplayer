package okosama.app.panel;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.IViewAction;
import okosama.app.adapter.AlbumSpinnerAdapter;
import okosama.app.adapter.ArtistSpinnerAdapter;
import okosama.app.adapter.GenreSpinnerAdapter;
import okosama.app.adapter.PlaylistSpinnerAdapter;
import okosama.app.adapter.SpinnerArrayAdapter;
import okosama.app.adapter.TrackSpinnerAdapter;
import okosama.app.adapter.VideoSpinnerAdapter;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.storage.AlbumData;
import okosama.app.storage.ArtistGroupData;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
import okosama.app.storage.PlaylistData;
import okosama.app.storage.VideoData;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabPage;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.AutoCompleteEdit;
import okosama.app.widget.Button;
import okosama.app.widget.Combo;
import okosama.app.widget.Image;
import okosama.app.widget.absWidget;
import android.app.Activity;
//import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

public class SearchPanel extends ControlPanel {
	
	static final int VALUE_CTRL_WIDTH = 380;
	static final int PANEL_WIDTH_PORTRAIT = ControlDefs.APP_BASE_WIDTH;
	static final int PANEL_WIDTH_HORIZONTAL = ControlDefs.APP_BASE_WIDTH;
	static final int TOP_IF_PANEL_NOT_EXISTS = 0;//250;
	
	static final int SEARCH_PANEL_TYPE_SONG = 1;
	static final int SEARCH_PANEL_TYPE_ALBUM = 2;
	static final int SEARCH_PANEL_TYPE_PLAYLIST = 3;
	static final int SEARCH_PANEL_TYPE_VIDEO = 4;
	static final int SEARCH_PANEL_TYPE_ARTIST = 5;

	LayoutParams lpSrchBtn;
	LayoutParams lpLine1Icon;
	LayoutParams lpLine2Icon;
	LayoutParams lpLine3Icon;
	LayoutParams lpLine4Icon;
	LayoutParams lpLine1Val;
	LayoutParams lpLine2Val;
	LayoutParams lpLine3Val;
	LayoutParams lpLine4Val;
	
	static String mNoSelection;
	Button btnSrch;
	Image imgSong;
	Button imgArtist;
	Button imgAlbum;
	Image imgPlaylist;
	Image imgGenre;
	Image imgVideo;
	AutoCompleteEdit edtSong;
	AutoCompleteEdit edtArtist;
	AutoCompleteEdit edtAlbum;
	AutoCompleteEdit edtPlaylist;
	AutoCompleteEdit edtVideo;
	Combo cmbArtist;
	Combo cmbAlbum;
	Combo cmbGenre;
	
	public void clearAllControlValue()
	{
		edtSong.clearValue();
		edtArtist.clearValue();
		edtAlbum.clearValue();
		edtPlaylist.clearValue();
		edtVideo.clearValue();
		cmbArtist.clearValue();
		cmbAlbum.clearValue();
		cmbGenre.clearValue();
	}
	View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			return true;
		}
	};
	
	static SearchPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new SearchPanel(activity);
		}
	}
	public static void deleteInstance()
	{
		removeFromParent();
		instance = null;
		
	}
	
	public static SearchPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			
			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        mNoSelection = act.getString(R.string.no_selection);
			switch( act.getCurrentTabPageId() )
			{
			case TabPage.TABPAGE_ID_SONG:
				SearchPanel.getInstance().switchPanelType(SEARCH_PANEL_TYPE_SONG);
				break;
			case TabPage.TABPAGE_ID_ALBUM:
				SearchPanel.getInstance().switchPanelType(SEARCH_PANEL_TYPE_ALBUM);
				break;
			case TabPage.TABPAGE_ID_ARTIST:
				SearchPanel.getInstance().switchPanelType(SEARCH_PANEL_TYPE_ARTIST);
				break;
			case TabPage.TABPAGE_ID_PLAYLIST:
				SearchPanel.getInstance().switchPanelType(SEARCH_PANEL_TYPE_PLAYLIST);
				break;
			case TabPage.TABPAGE_ID_VIDEO:
				SearchPanel.getInstance().switchPanelType(SEARCH_PANEL_TYPE_VIDEO);
				break;
			default:
				// 種別がない場合は、表示させない
				return;
			}
			if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
			{
				instance.getView().setLayoutParams( 
						OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					0, 150, PANEL_WIDTH_PORTRAIT, PANEL_WIDTH_PORTRAIT )
				);
			}
			else
			{
				instance.getView().setLayoutParams( 
						OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					0, 150, PANEL_WIDTH_HORIZONTAL, PANEL_WIDTH_HORIZONTAL )
				);				
			}
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());
			//if( -1 == tabBaseLayout.indexOfChild(instance.getView()) )
			//{
			tabBaseLayout.addView(instance.getView());
			instance.getView().setBackgroundResource(R.color.search_bk);
			
			parent = tabBaseLayout;
			//}
		}
		else
		{
			Log.e("error","insert search control panel");
		}
	}
	public static void removeFromParent()//ToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());

			parent = null;				
		}
	}

	public SearchPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		//TabComponentPropertySetter creationData[] = null;
		
		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
			lpSrchBtn = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					0, TOP_IF_PANEL_NOT_EXISTS + 370, PANEL_WIDTH_HORIZONTAL, 100
			);
			lpLine1Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					20, TOP_IF_PANEL_NOT_EXISTS + 280, 80, 80
		        );
				lpLine2Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					16, TOP_IF_PANEL_NOT_EXISTS + 190, 80, 80
			    );
				lpLine3Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						22, TOP_IF_PANEL_NOT_EXISTS + 100, 80, 80
			        );
				lpLine4Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						20, TOP_IF_PANEL_NOT_EXISTS + 10, 80, 80
			        );
				lpLine1Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						120, TOP_IF_PANEL_NOT_EXISTS + 275, VALUE_CTRL_WIDTH - 2, 100
			        );
				lpLine2Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						116, TOP_IF_PANEL_NOT_EXISTS + 178, VALUE_CTRL_WIDTH - 4, 100
			        );
				lpLine3Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						110, TOP_IF_PANEL_NOT_EXISTS + 90, VALUE_CTRL_WIDTH - 8, 100
			        );
				lpLine4Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						117, TOP_IF_PANEL_NOT_EXISTS, VALUE_CTRL_WIDTH - 5, 100
			        );
		}
		else
		{			
			lpSrchBtn = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					0, TOP_IF_PANEL_NOT_EXISTS + 370, PANEL_WIDTH_HORIZONTAL, 100
			);
			lpLine1Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					20, TOP_IF_PANEL_NOT_EXISTS + 280, 80, 80
		        );
				lpLine2Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					16, TOP_IF_PANEL_NOT_EXISTS + 190, 80, 80
			    );
				lpLine3Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						22, TOP_IF_PANEL_NOT_EXISTS + 100, 80, 80
			        );
				lpLine4Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						20, TOP_IF_PANEL_NOT_EXISTS + 10, 80, 80
			        );
				lpLine1Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						120, TOP_IF_PANEL_NOT_EXISTS + 275, VALUE_CTRL_WIDTH - 9, 100
			        );
				lpLine2Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						116, TOP_IF_PANEL_NOT_EXISTS + 178, VALUE_CTRL_WIDTH - 4, 100
			        );
				lpLine3Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						110, TOP_IF_PANEL_NOT_EXISTS + 90, VALUE_CTRL_WIDTH - 8, 100
			        );
				lpLine4Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						117, TOP_IF_PANEL_NOT_EXISTS, VALUE_CTRL_WIDTH - 5, 100
			        );
		}
		//////////////////// control settings //////////////////////////			
		TabComponentPropertySetter creationData[] = {
				// --------------------- Search Button
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_BUTTON, null, ComponentType.BUTTON, 
					lpSrchBtn
					, null, drawable.filter_btn_image, "", ScaleType.FIT_XY
				),
				// --------------------- SongButtonImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_SONG_ICON, null, ComponentType.IMAGE, 
					lpLine1Icon
					, null, drawable.songtabbtn_normal, "", ScaleType.FIT_XY
				),
				// --------------------- SongEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_SONG_EDIT, null, ComponentType.EDIT, 
					lpLine1Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- ArtistImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ARTIST_ICON, null, ComponentType.BUTTON, 
					lpLine1Icon
					, null, drawable.music_select_artist_image, "", ScaleType.FIT_XY
				),
				// --------------------- ArtistEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ARTIST_EDIT, null, ComponentType.EDIT, 
					lpLine1Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- AlbumImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ALBUM_ICON, null, ComponentType.BUTTON, 
					lpLine1Icon
					, null, drawable.music_select_album_image, "", ScaleType.FIT_XY
				),
				// --------------------- AlbumEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ALBUM_EDIT, null, ComponentType.EDIT, 
					lpLine1Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- VideoImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_VIDEO_ICON, null, ComponentType.IMAGE, 
					lpLine1Icon
					, null, drawable.video_normal, "", ScaleType.FIT_XY
				),
				// --------------------- VideoEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_VIDEO_EDIT, null, ComponentType.EDIT, 
					lpLine1Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- PlaylistImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_PLAYLIST_ICON, null, ComponentType.IMAGE, 
					lpLine1Icon
					, null, drawable.playlist_normal, "", ScaleType.FIT_XY
				),
				// --------------------- PlaylistEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_PLAYLIST_EDIT, null, ComponentType.EDIT, 
					lpLine1Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- GenreImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_GENRE_ICON, null, ComponentType.IMAGE, 
					lpLine2Icon
					, null, drawable.genre_normal, "", ScaleType.FIT_XY
				),
				// --------------------- GenreEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_GENRE_SPINNER, null, ComponentType.COMBO, 
					lpLine2Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- GenreImage
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ARTIST_SPINNER, null, ComponentType.COMBO, 
					lpLine3Val
					, null, null, "", ScaleType.FIT_XY
				),
				// --------------------- GenreEdit
				new TabComponentPropertySetter(
					ControlIDs.SEARCH_ALBUM_SPINNER, null, ComponentType.COMBO, 
					lpLine3Val
					, null, null, "", ScaleType.FIT_XY
				),
				
		};
		//creationData = creationDataPort;
	
		btnSrch = DroidWidgetKit.getInstance().MakeButton();
		imgSong = DroidWidgetKit.getInstance().MakeImage();
		imgArtist = DroidWidgetKit.getInstance().MakeButton();
		imgAlbum = DroidWidgetKit.getInstance().MakeButton();
		imgGenre = DroidWidgetKit.getInstance().MakeImage();
		imgVideo = DroidWidgetKit.getInstance().MakeImage();
		imgPlaylist = DroidWidgetKit.getInstance().MakeImage();
		edtSong = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtSong.setHint(R.string.srch_song_hint);
		edtArtist = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtArtist.setHint(R.string.srch_artist_hint);
		edtAlbum = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtAlbum.setHint(R.string.srch_album_hint);
		edtPlaylist = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtPlaylist.setHint(R.string.srch_playlist_hint);
		edtVideo = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtVideo.setHint(R.string.srch_video_hint);
		cmbArtist = DroidWidgetKit.getInstance().MakeCombo();
		cmbAlbum = DroidWidgetKit.getInstance().MakeCombo();
		cmbGenre = DroidWidgetKit.getInstance().MakeCombo();
		absWidget widgets[] = {
				btnSrch,
				imgSong,
				edtSong,
				imgArtist,
				edtArtist,
				imgAlbum,
				edtAlbum,
				imgVideo,
				edtVideo,
				imgPlaylist,
				edtPlaylist,
				imgGenre,
				cmbGenre,
				cmbArtist,
				cmbAlbum
			};
		// ---- action
		// Timeコンポーネント
		// shuffleボタン
		SparseArray< IViewAction > actMapSearch
			= new SparseArray< IViewAction >();
		// TODO: Search Actionの作成
		actMapSearch.put( IViewAction.ACTION_ID_ONCLICK, null );//new ToggleShuffleAction() );
				
		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actMapSearch ),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
			};
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:ボタンのアクションを設定
			if( actionSetterCont[i] != null )
			{
				widget.acceptConfigurator(actionSetterCont[i]);
			}
			
			// ボタンをこのタブ子項目として追加
			addChild( creationData[i].getInternalID(), widget );			
			tabBaseLayout.addView( widget.getView() );
			i++;
		}
		this.getView().setOnTouchListener(mTouchListener);
	}

	/**
	 * パネル種別によって、パネルの内容を切り替える
	 * @param panelTypeCode
	 */
	void switchPanelType(int panelTypeCode)
	{
		// 値を一旦クリア
		clearAllControlValue();
		switch( panelTypeCode )
		{
		case SEARCH_PANEL_TYPE_SONG:
			imgSong.setVisible(View.VISIBLE);
			imgSong.resetLayoutParams(lpLine1Icon);
			imgArtist.setVisible(View.VISIBLE);
			imgArtist.resetLayoutParams(lpLine2Icon);
			imgAlbum.setVisible(View.VISIBLE);
			imgAlbum.resetLayoutParams(lpLine3Icon);
			imgGenre.setVisible(View.VISIBLE);
			imgGenre.resetLayoutParams(lpLine4Icon);
			edtSong.setVisible(View.VISIBLE);
			edtSong.resetLayoutParams(lpLine1Val);			
			cmbArtist.setVisible(View.VISIBLE);
			cmbArtist.resetLayoutParams(lpLine2Val);
			cmbAlbum.setVisible(View.VISIBLE);
			cmbAlbum.resetLayoutParams(lpLine3Val);
			cmbGenre.setVisible(View.VISIBLE);
			cmbGenre.resetLayoutParams(lpLine4Val);
			
			imgVideo.setVisible(View.GONE);
			imgPlaylist.setVisible(View.GONE);
			edtArtist.setVisible(View.GONE);
			edtAlbum.setVisible(View.GONE);
			edtPlaylist.setVisible(View.GONE);
			edtVideo.setVisible(View.GONE);
			
			break;
		case SEARCH_PANEL_TYPE_ALBUM:
			imgAlbum.setVisible(View.VISIBLE);
			imgAlbum.resetLayoutParams(lpLine1Icon);
			imgArtist.setVisible(View.VISIBLE);
			imgArtist.resetLayoutParams(lpLine2Icon);
			imgGenre.setVisible(View.VISIBLE);
			imgGenre.resetLayoutParams(lpLine3Icon);
			edtAlbum.setVisible(View.VISIBLE);
			edtAlbum.resetLayoutParams(lpLine1Val);
			cmbArtist.setVisible(View.VISIBLE);
			cmbArtist.resetLayoutParams(lpLine2Val);
			cmbGenre.setVisible(View.VISIBLE);
			cmbGenre.resetLayoutParams(lpLine3Val);

			imgSong.setVisible(View.GONE);
			edtSong.setVisible(View.GONE);
			imgVideo.setVisible(View.GONE);
			edtVideo.setVisible(View.GONE);
			imgPlaylist.setVisible(View.GONE);
			edtPlaylist.setVisible(View.GONE);
			edtArtist.setVisible(View.GONE);
			cmbAlbum.setVisible(View.GONE);
			
			break;
		case SEARCH_PANEL_TYPE_PLAYLIST:
			imgPlaylist.setVisible(View.VISIBLE);
			imgPlaylist.resetLayoutParams(lpLine1Icon);
//			imgAlbum.setVisible(View.VISIBLE);
//			imgAlbum.resetLayoutParams(lpLine2Icon);
//			imgGenre.setVisible(View.VISIBLE);
//			imgGenre.resetLayoutParams(lpLine2Icon);
			edtPlaylist.setVisible(View.VISIBLE);
			edtPlaylist.resetLayoutParams(lpLine1Val);
//			cmbAlbum.setVisible(View.VISIBLE);
//			cmbAlbum.resetLayoutParams(lpLine2Val);
//			cmbGenre.setVisible(View.VISIBLE);
//			cmbGenre.resetLayoutParams(lpLine2Val);

			imgAlbum.setVisible(View.GONE);
			cmbAlbum.setVisible(View.GONE);
			imgGenre.setVisible(View.GONE);
			cmbGenre.setVisible(View.GONE);	
			imgSong.setVisible(View.GONE);
			edtSong.setVisible(View.GONE);
			imgVideo.setVisible(View.GONE);
			//imgPlaylist.setVisible(View.GONE);
			cmbArtist.setVisible(View.GONE);
			edtAlbum.setVisible(View.GONE);
			//edtPlaylist.setVisible(View.GONE);
			edtVideo.setVisible(View.GONE);
			break;
		case SEARCH_PANEL_TYPE_VIDEO:
			imgVideo.setVisible(View.VISIBLE);
			imgVideo.resetLayoutParams(lpLine1Icon);
			edtVideo.setVisible(View.VISIBLE);
			edtVideo.resetLayoutParams(lpLine1Val);

			imgAlbum.setVisible(View.GONE);
			cmbAlbum.setVisible(View.GONE);
			imgGenre.setVisible(View.GONE);
			cmbGenre.setVisible(View.GONE);	
			imgSong.setVisible(View.GONE);
			edtSong.setVisible(View.GONE);
//			imgVideo.setVisible(View.GONE);
//			edtVideo.setVisible(View.GONE);
			imgPlaylist.setVisible(View.GONE);
			edtPlaylist.setVisible(View.GONE);
			imgArtist.setVisible(View.GONE);
			cmbArtist.setVisible(View.GONE);
			edtArtist.setVisible(View.GONE);
			edtAlbum.setVisible(View.GONE);
			break;
		case SEARCH_PANEL_TYPE_ARTIST:
			imgAlbum.setVisible(View.VISIBLE);
			imgAlbum.resetLayoutParams(lpLine1Icon);
			imgArtist.setVisible(View.VISIBLE);
			imgArtist.resetLayoutParams(lpLine2Icon);
			imgGenre.setVisible(View.VISIBLE);
			imgGenre.resetLayoutParams(lpLine3Icon);
			edtAlbum.setVisible(View.VISIBLE);
			edtAlbum.resetLayoutParams(lpLine1Val);
			cmbArtist.setVisible(View.VISIBLE);
			cmbArtist.resetLayoutParams(lpLine2Val);
			cmbGenre.setVisible(View.VISIBLE);
			cmbGenre.resetLayoutParams(lpLine3Val);

			imgSong.setVisible(View.GONE);
			edtSong.setVisible(View.GONE);
			imgVideo.setVisible(View.GONE);
			edtVideo.setVisible(View.GONE);
			imgPlaylist.setVisible(View.GONE);
			edtPlaylist.setVisible(View.GONE);
			edtArtist.setVisible(View.GONE);
			cmbAlbum.setVisible(View.GONE);			
			break;
		}
		// Songのテキスト
		TrackSpinnerAdapter adpSong = new TrackSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				android.R.layout.simple_spinner_item,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().getAllItems());
		edtSong.setAdapter(adpSong);
		
		// Spinnerとエディットボックスの補完の設定
//		cmbArtist.setAdapter(
//				(SpinnerAdapter) OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp());
		// Albumのスピナ
		AlbumSpinnerAdapter adpAlbum = new AlbumSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				android.R.layout.simple_spinner_item,
				//R.layout.track_list_item_group,
				AlbumSpinnerAdapter.convertItems(
						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getAlbumAdp().getItems()//,
						//new AlbumData()
					)
				);
		
		cmbAlbum.setAdapter(
			adpAlbum
		);
		adpAlbum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// AlbumのEdit
		edtAlbum.setAdapter(adpAlbum);
		
		// Artistのスピナ
		ArrayList<ArtistGroupData> arrArtist = new ArrayList<ArtistGroupData>();
		HashMap<Integer,ArtistGroupData> groupDataMap = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp().getGroupData();
		for( ArtistGroupData data : groupDataMap.values() )
		{
			arrArtist.add(data);
		}
		ArtistSpinnerAdapter adpArtist = new ArtistSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				android.R.layout.simple_spinner_item,				
				//R.layout.track_list_item_group,
				ArtistSpinnerAdapter.convertItems(
						arrArtist//,
						//new ArtistGroupData()
					)
				);
		cmbArtist.setAdapter(
			adpArtist
		);
		adpArtist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// ArtistのEdit
		edtArtist.setAdapter(adpArtist);

		// Genreのスピナ
		SpinnerArrayAdapter<GenreData> adpGenre = new SpinnerArrayAdapter<GenreData>(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				android.R.layout.simple_spinner_item,
				SpinnerArrayAdapter.convertItems(
						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getGenreStocker().getDistinctItems(),
						new GenreData(),
						R.string.genre_
					)
				);
		cmbGenre.setAdapter(
				adpGenre
		);
		adpGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// PlaylistのEdit
		SpinnerArrayAdapter<PlaylistData> adpPlaylist = new SpinnerArrayAdapter<PlaylistData>(		
		// PlaylistSpinnerAdapter adpPlaylist = new PlaylistSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				android.R.layout.simple_spinner_item,				
				//R.layout.track_list_item_group,
				SpinnerArrayAdapter.convertItems(
						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getPlaylistAdp().getItems(),
						new PlaylistData(),
						R.string.playlist_
					)
				);
		edtPlaylist.setAdapter(adpPlaylist);
		
		
		// VideoのEdit
		SpinnerArrayAdapter<VideoData> adpVideo = new SpinnerArrayAdapter<VideoData>(		
		// VideoSpinnerAdapter adpVideo = new VideoSpinnerAdapter(
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
			android.R.layout.simple_spinner_item,				
			//R.layout.track_list_item_group,
			SpinnerArrayAdapter.convertItems(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoAdp().getAllItems(),
				new VideoData(),
				R.string.video_
			)
		);
		
		edtVideo.setAdapter(adpVideo);
	}
	
	public FilterData getFilterData()
	{
		FilterData data = new FilterData();
		
		data.setStrSong(edtSong.getText());
		data.setStrArtist(edtArtist.getText());
		data.setStrAlbum(edtAlbum.getText());
		data.setStrPlaylist( edtPlaylist.getText() );
		data.setStrVideo( edtVideo.getText() );
		ArtistGroupData artistData = (ArtistGroupData) cmbArtist.getSelectedItem();
		AlbumData albumData = (AlbumData) cmbAlbum.getSelectedItem();
		GenreData genreData = (GenreData) cmbGenre.getSelectedItem();
		if( artistData != null )
		{
			data.setArtistId(String.valueOf(artistData.getDataId()));
		}
		if( albumData != null )
		{
			data.setAlbumId(String.valueOf(albumData.getDataId()));
		}
		if( genreData != null )
		{
			data.setGenreId(genreData.getDataId());
		}
		return data;
	}
}
