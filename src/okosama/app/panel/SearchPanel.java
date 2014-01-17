package okosama.app.panel;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.IViewAction;
import okosama.app.adapter.AlbumSpinnerAdapter;
import okosama.app.adapter.ArtistSpinnerAdapter;
import okosama.app.adapter.GenreSpinnerAdapter;
import okosama.app.adapter.PlaylistSpinnerAdapter;
import okosama.app.adapter.TrackSpinnerAdapter;
import okosama.app.adapter.VideoSpinnerAdapter;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.storage.AlbumData;
import okosama.app.storage.ArtistGroupData;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabPage;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.AutoCompleteEdit;
import okosama.app.widget.Button;
import okosama.app.widget.Combo;
import okosama.app.widget.Edit;
import okosama.app.widget.Image;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

public class SearchPanel extends ControlPanel {
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
				// ��ʂ��Ȃ��ꍇ�́A�\�������Ȃ�
				return;
			}
			
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());
			//if( -1 == tabBaseLayout.indexOfChild(instance.getView()) )
			//{
			tabBaseLayout.addView(instance.getView());
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
					380, 510, 100, 100
			);
			lpLine1Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					20, 530, 80, 80
		        );
				lpLine2Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					16, 440, 80, 80
			    );
				lpLine3Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						22, 350, 80, 80
			        );
				lpLine4Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						20, 260, 80, 80
			        );
				lpLine1Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						120, 525, 300, 100
			        );
				lpLine2Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						116, 428, 300, 100
			        );
				lpLine3Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						110, 340, 300, 100
			        );
				lpLine4Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						117, 250, 300, 100
			        );
		}
		else
		{
			lpSrchBtn = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					380, 510, 100, 100
			);
			lpLine1Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					20, 530, 80, 80
		        );
				lpLine2Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
					16, 440, 80, 80
			    );
				lpLine3Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						22, 350, 80, 80
			        );
				lpLine4Icon = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						20, 260, 80, 80
			        );
				lpLine1Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						120, 525, 300, LayoutParams.WRAP_CONTENT//100
			        );
				lpLine2Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						116, 428, 300, LayoutParams.WRAP_CONTENT//100
			        );
				lpLine3Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						110, 340, 300, LayoutParams.WRAP_CONTENT//100
			        );
				lpLine4Val = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
						117, 250, 300, LayoutParams.WRAP_CONTENT//100
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
					, null, drawable.filter_normal, "", ScaleType.FIT_XY
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
		edtArtist = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtAlbum = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtPlaylist = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
		edtVideo = DroidWidgetKit.getInstance().MakeAutoCompleteEdit();
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
		// Time�R���|�[�l���g
		// shuffle�{�^��
		SparseArray< IViewAction > actMapSearch
			= new SparseArray< IViewAction >();
		// TODO: Search Action�̍쐬
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
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			if( actionSetterCont[i] != null )
			{
				widget.acceptConfigurator(actionSetterCont[i]);
			}
			
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( creationData[i].getInternalID(), widget );			
			tabBaseLayout.addView( widget.getView() );
			i++;
		}
	
	}

	/**
	 * �p�l����ʂɂ���āA�p�l���̓��e��؂�ւ���
	 * @param panelTypeCode
	 */
	void switchPanelType(int panelTypeCode)
	{
		// �l����U�N���A
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
		// Song�̃e�L�X�g
		TrackSpinnerAdapter adpSong = new TrackSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().getAllItems());
		edtSong.setAdapter(adpSong);
		
		// Spinner�ƃG�f�B�b�g�{�b�N�X�̕⊮�̐ݒ�
//		cmbArtist.setAdapter(
//				(SpinnerAdapter) OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp());
		// Album�̃X�s�i
		AlbumSpinnerAdapter adpAlbum = new AlbumSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getAlbumAdp().getItems());
		cmbAlbum.setAdapter(
			adpAlbum
		);
		adpAlbum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Album��Edit
		edtAlbum.setAdapter(adpAlbum);
		
		// Artist�̃X�s�i
		ArrayList<ArtistGroupData> arrArtist = new ArrayList<ArtistGroupData>();
		HashMap<Integer,ArtistGroupData> groupDataMap = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp().getGroupData();
		for( ArtistGroupData data : groupDataMap.values() )
		{
			arrArtist.add(data);
		}
		ArtistSpinnerAdapter adpArtist = new ArtistSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				arrArtist
				);
		cmbAlbum.setAdapter(
			adpArtist
		);
		adpArtist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Artist��Edit
		edtArtist.setAdapter(adpArtist);

		// Genre�̃X�s�i
		GenreSpinnerAdapter adpGenre = new GenreSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getGenreStocker().getDistinctItems());
		cmbGenre.setAdapter(
				adpGenre
		);
		adpGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Playlist��Edit
		PlaylistSpinnerAdapter adpPlaylist = new PlaylistSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getPlaylistAdp().getItems());
		edtPlaylist.setAdapter(adpPlaylist);
		
		
		// Video��Edit
		VideoSpinnerAdapter adpVideo = new VideoSpinnerAdapter(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
				panelTypeCode,
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoAdp().getAllItems());
		
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
			data.setArtistId(artistData.getArtistId());
		}
		if( albumData != null )
		{
			data.setAlbumId(albumData.getAlbumId());
		}
		if( genreData != null )
		{
			data.setGenreId(genreData.getGenreId());
		}
		
		
		return data;
	}
}
