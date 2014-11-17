package okosama.app.tab;

import java.util.ArrayList;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.ArtistAlbumListRawAdapter;
import okosama.app.adapter.PlaylistListRawAdapter;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.adapter.VideoListRawAdapter;
import okosama.app.storage.AlbumData;
import okosama.app.storage.PlaylistData;
import okosama.app.storage.TrackData;
import okosama.app.storage.VideoData;
import okosama.app.widget.absWidget;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ToggleButton;
// import okosama.app.adapter.PlaylistListAdapter;

/**
 * Tab�̍\���v�f���쐬���邽�߂ɕK�v�ȑS�Ă̏��������Ɋi�[����TabComponent�ɓn��
 * �܂��A�����TabComponent�ɓK�p����A�N�V����
 * TabComponent�ւ�Visitor
 * @author 25689
 *
 */
public class TabComponentPropertySetter implements ITabComponentConfigurator {
	public TabComponentPropertySetter(
			Integer internalID,
			TabPage page,
			ComponentType type ) 
	{
		this.internalID = internalID;
		this.tabPageParent = page;
		this.type = type;
	}
	public TabComponentPropertySetter(Integer internalID,
			TabPage page,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId ) 
	{
		this.internalID = internalID;
		this.tabPageParent = page;		
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
	}	
	public TabComponentPropertySetter(Integer internalID,
			TabPage page,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId, Integer bkImageId ) 
	{
		this.internalID = internalID;
		this.tabPageParent = page;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
		this.bkImageId = bkImageId;
	}
	public TabComponentPropertySetter(Integer internalID,
			TabPage page,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId,
			Integer bkImageId, String text, ScaleType scaleType) 
	{
		this.internalID = internalID;
		this.tabPageParent = page;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
		this.bkImageId = bkImageId;
		this.text = text;
		this.scaleType = scaleType;
	}
	public TabComponentPropertySetter(Integer internalID,
			TabPage page,
			ComponentType type, ViewGroup.LayoutParams layoutParams, Integer imageId,
			Integer bkImageId, String text, ScaleType scaleType) 
	{
		this.internalID = internalID;
		this.tabPageParent = page;
		this.type = type;
		this.layoutParams = layoutParams;
		this.imageId = imageId;
		this.bkImageId = bkImageId;
		this.text = text;
		this.scaleType = scaleType;
	}
	public static enum ComponentType {
		NONE, TAB, TAB_PAGE, BUTTON, LIST_ALBUM, 
		LIST_ARTIST, LIST_SONG, LIST_PLAYLIST, LIST_NOWPLAYLIST,
		TEXT, IMAGE, EXPLIST, TOGGLEBUTTON, LABEL, PROGRESS, LIST_VIDEO, EDIT, COMBO
	};
	
	
	boolean external = false;	// TODO:�b���
	// String internalName;
	Integer internalID;
	ComponentType type = ComponentType.NONE;
	ViewGroup.LayoutParams layoutParams = null;
	Integer left, top, width, height;
	Integer imageId;
	Integer bkImageId;
	String text;
	ScaleType scaleType = null;
	int visibleFlag = View.VISIBLE;
	TabPage tabPageParent = null;
	public void setVisibleFlag( int f ) 
	{
		visibleFlag = f;
	}
	int clrBack = -1;
	public void setColorBack( int clrCode )
	{
		clrBack = clrCode;
	}
	
	public Integer getInternalID()
	{
		return internalID;
	}
	
	@Override
	public int configure(ITabComponent component) {
		component.setInternalID( this.internalID );
		// ���������AActivity��ResourceAccessor����擾����
		OkosamaMediaPlayerActivity activity 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		
		View v = component.getView();
		if( v == null )
		{
			return -1;
		}
		v.setId(this.internalID);
		
		if( layoutParams != null )
		{
			v.setLayoutParams(layoutParams);
		}
		else if( left != null && top != null && width != null && height != null )
		{
			layoutParams = OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
	        		left, top, width, height 
	        );
			v.setLayoutParams(layoutParams);
		}
		if( type == ComponentType.BUTTON )
		{
			// ���̕ӂ̃\�[�X�́A�ň��ł��B
			// ImageButton
			ImageButton imgBtn = null;
			if( v instanceof ImageButton )
			{
				imgBtn = (ImageButton)v;
			}
			if( imgBtn != null )
			{
				if( scaleType != null )
				{
					imgBtn.setScaleType(scaleType);
				}
				if( imageId != null )
				{
					// ����������̃e�X�g�p
					// TODO:��ŕK������
					imgBtn.setImageBitmap(null);
					
					imgBtn.setImageResource(imageId);
					// imgBtn.setImageBitmap(OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId(imageId));
				}
			}
			else
			{
				LogWrapper.e("component setting error",
						"invalid image button component setting");
			}
		}
		else if( type == ComponentType.TOGGLEBUTTON )
		{
			ToggleButton tbtn = null;
			if( v instanceof ToggleButton )
			{
				tbtn = (ToggleButton)v;
				tbtn.setText(this.text);
			}
		}
		else if( type == ComponentType.IMAGE )
		{			
			// ImageView
			ImageView img = null;
			if( v instanceof ImageView )
			{
				img = (ImageView)v;
			}
			if( img != null )
			{
				if( scaleType != null )
				{
					img.setScaleType(scaleType);
				}
				if( imageId != null )
				{
					// Selector�Ȃ̂ŁA����Bitmap�ɂ���͖̂���
					// TODO: �ł������Ŏ��͎����H
					// img.setImageBitmap(OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId(imageId));
					img.setImageResource(imageId);
				}
			}
			else
			{
				LogWrapper.e("component setting error",
						"invalid image component setting");
			}			
		}
		////////////// Album /////////////////////
		else if( type == ComponentType.LIST_ALBUM )
		{
			// SimpleList
			ListView lst = null;
			if( v instanceof ListView )
			{
				lst = (ListView)v;
				// activity.setAlbumList(lst);
			}
			
			// Adapter�̍쐬
			if( activity.getAlbumAdp() == null )
			{
				LogWrapper.i("AlbumAdapter","re create");
				activity.putAdapter(//setAlbumAdp(
					TabPage.TABPAGE_ID_ALBUM,
					new AlbumListRawAdapter(
						activity,
						R.layout.track_list_item,
						new ArrayList<AlbumData>(),
						tabPageParent
					)
				);
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getAlbumAdp());

			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);
			
			// �J�[�\���̍쐬
			// Database.getInstance(external).createAlbumCursor(activity.getAlbumAdp().getQueryHandler(), null, null);
		}
		////////////// Artist ///////////////////
		else if( type == ComponentType.LIST_ARTIST )
		{
			// SimpleList
			ExpandableListView lst = null;
			if( v instanceof ExpandableListView )
			{
				lst = (ExpandableListView)v;
				// activity.setArtistList(lst);				
			}
			
			if( activity.getArtistAdp() == null )
			{
				// Adapter�̍쐬
				activity.putAdapter(//setAlbumAdp(
						TabPage.TABPAGE_ID_ARTIST,				
				// activity.setArtistAdp(
						new ArtistAlbumListRawAdapter(
						activity,
						//new SparseArray<ArtistGroupData>(),
	                    R.layout.track_list_item_group,
//	                    new String[] {},
//	                    new int[] {},
						//new SparseArray<ArtistChildData[]>(),
	                    R.layout.track_list_item_child
						,tabPageParent
				));
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getArtistAdp());

			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			//lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// �J�[�\���̍쐬
			// Database.getInstance(external).createArtistCursor(activity.getArtistAdp().getQueryHandler(), null);			
		}
		else if( type == ComponentType.LIST_SONG )
		{
			// SimpleList
			ListView lst = null;
			if( v instanceof ListView )
			{
				lst = (ListView)v;
				// activity.setSongList(lst);								
			}
			
			// TODO: ����́A�b���
			boolean editMode = false;
//			 "nowplaying".equals(mPlaylist),
//             mPlaylist != null &&
//             !(mPlaylist.equals("podcasts") || mPlaylist.equals("recentlyadded")
			boolean isnowplaying = false;
			boolean disablenowplayingindicator = false;
			//String genre = "";
			String albumId = "";
			String artistId = "";
			
			// Adapter�̍쐬
			if( activity.getTrackAdp() == null )
			{
				//ArrayList<TrackData> data = null;//new ArrayList<TrackData>();
				activity.putAdapter(//setAlbumAdp(
						TabPage.TABPAGE_ID_SONG,				
				// activity.setTrackAdp(
					new TrackListRawAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
//						null,//Cursor cursor,
//						new String[] {}, 
//						new int[] {},
						new ArrayList<TrackData>(),//data,
						isnowplaying,
						disablenowplayingindicator,
						//genre,
						albumId,
			            artistId
						,tabPageParent			            
			        )
				);
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getTrackAdp());
			
			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// �J�[�\���̍쐬
			// Database.getInstance(external).createTrackCursor(activity.getTrackAdp().getQueryHandler(), null, null, true, null, null, null);			
		}
		else if( type == ComponentType.LIST_PLAYLIST )
		{
			// SimpleList
			ListView lst = null;
			if( v instanceof ListView )
			{
				lst = (ListView)v;
				// activity.setPlaylistList(lst);				
			}
			
			// Adapter�̍쐬
			if( activity.getPlaylistAdp() == null )
			{
				//ArrayList<PlaylistData> data = //new ArrayList<PlaylistData>();
				activity.putAdapter(//setAlbumAdp(
						TabPage.TABPAGE_ID_PLAYLIST,				
				// activity.setPlaylistAdp(
					new PlaylistListRawAdapter(
						// activity.getApplication(),
						activity,
						R.layout.track_list_item,
						new ArrayList<PlaylistData>()//data
						//null,//Cursor cursor,
						//new String[] { PlaylistsColumns.NAME},
	                    //new int[] { android.R.id.text1 }
						,tabPageParent						
					)
				);
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getPlaylistAdp());
			
			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// �J�[�\���̍쐬
			// Database.getInstance(external).createPlaylistCursor(activity.getPlaylistAdp().getQueryHandler(), null, false);						
		}
		else if( type == ComponentType.LIST_NOWPLAYLIST )
		{
			// SimpleList
			ListView lst = null;
			if( v instanceof ListView )
			{
				lst = (ListView)v;
			}
			
			// TODO: ����́A�b���
			boolean editMode = false;
//			 "nowplaying".equals(mPlaylist),
//             mPlaylist != null &&
//             !(mPlaylist.equals("podcasts") || mPlaylist.equals("recentlyadded")
			boolean isnowplaying = false;
			boolean disablenowplayingindicator = false;
			String genre = "";
			String albumId = "";
			String artistId = "";
			
			// Adapter�̍쐬
			if( activity.getTrackAdp() == null )
			{
				ArrayList<TrackData> data = new ArrayList<TrackData>();
				activity.putAdapter(//setAlbumAdp(
						TabPage.TABPAGE_ID_SONG,
				// activity.setTrackAdp(
					new TrackListRawAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
//						null,//Cursor cursor,
//						new String[] {}, 
//						new int[] {},
						data,
						isnowplaying,
						disablenowplayingindicator,
						//genre,
						albumId,
			            artistId
						,tabPageParent			            
			        )
				);
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getTrackAdp());
			
			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			//lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// �J�[�\���̍쐬
			// ���݂̍Đ��Ώۂ̃v���C���X�g���w�肷��
			// Database.getInstance(external).createTrackCursor(activity.getTrackAdp().getQueryHandler(), Database.PlaylistName_NowPlaying, null, true, null, null, null);			
		}		
		////////////// Video /////////////////////
		else if( type == ComponentType.LIST_VIDEO )
		{
			// �ʏ�̃��X�g
			ListView lst = null;
			if( v instanceof ListView )
			{
				lst = (ListView)v;
			}
			
			// Adapter�̍쐬
			if( activity.getVideoAdp() == null )
			{
				LogWrapper.i("VideoAdapter","re create");
				activity.putAdapter(
					TabPage.TABPAGE_ID_VIDEO,
					new VideoListRawAdapter(
						activity,
						R.layout.track_list_item,
						new ArrayList<VideoData>(),
						tabPageParent
					)
				);
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getVideoAdp());

			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			lst.setTag(component);
			activity.registerForContextMenu(lst);
		}
		else if( type == ComponentType.LABEL )
		{
//			TextView txt = null;
//			if( v instanceof TextView )
//			{
//				txt = (TextView)v;
//			}
			//txt.setTextSize(18.0f);
		}
		else if( type == ComponentType.PROGRESS )
		{
		}		
		if( bkImageId != null )
		{
			// ����������̃e�X�g�p
			// TODO:����
			if( v.getBackground() != null )
			{
				v.getBackground().setCallback(null);
			}
			Bitmap bmp = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId(bkImageId);
			if( bmp == null )
			{
				// ����Selector�������Ȃ̂ŁABitmap�ɂ���͖̂���
				v.setBackgroundResource(bkImageId);
			}
			else
			{
				BitmapDrawable d = new BitmapDrawable( bmp );
				v.setBackgroundDrawable(d);
			}
			// TODO: �ł������Ŏ��͎����H
			// v.setBackgroundResource(bkImageId);
		}
		if( clrBack != -1 )
		{
			v.setBackgroundColor(clrBack);
		}
		if( component instanceof absWidget )
		{
			((absWidget)component).setVisibleFlag(visibleFlag);
			((absWidget)component).setVisible(visibleFlag);
	    }
        
        // ���C�A�E�g�ւ̔z�u�́A��ʂ�
        // m_RLmain.addView(m_musicPlayTabButton);
		return 0;
	}
	
	
}
