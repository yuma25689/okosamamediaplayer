package okosama.app.tab;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.AlbumListAdapter;
import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.adapter.PlaylistListAdapter;
import okosama.app.adapter.TrackListAdapter;
import okosama.app.storage.Database;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Tab�̍\���v�f���쐬���邽�߂ɕK�v�ȑS�Ă̏��������Ɋi�[����TabComponent�ɓn��
 * �܂��A�����TabComponent�ɓK�p����A�N�V����
 * TabComponent�ւ�Visitor
 * @author 25689
 *
 */
public class TabComponentPropertySetter implements ITabComponentConfigurator {
	public TabComponentPropertySetter(
			String internalName,
			ComponentType type ) 
	{
		this.internalName = internalName;
		this.type = type;
	}
	public TabComponentPropertySetter(String internalName,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId ) 
	{
		this.internalName = internalName;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
	}	
	public TabComponentPropertySetter(String internalName,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId, Integer bkImageId ) 
	{
		this.internalName = internalName;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
		this.bkImageId = bkImageId;
	}
	public TabComponentPropertySetter(String internalName,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId,
			Integer bkImageId, String text, ScaleType scaleType) 
	{
		this.internalName = internalName;
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
	public static enum ComponentType {
		NONE, TAB, TAB_PAGE, BUTTON, LIST_ALBUM, LIST_ARTIST, LIST_SONG, LIST_PLAYLIST, LIST_NOWPLAYLIST, TEXT, IMAGE, EXPLIST, TOGGLEBUTTON
	};
	
	
	boolean external = false;	// TODO:�b���
	String internalName;
	ComponentType type = ComponentType.NONE;
	Integer left, top, width, height;
	Integer imageId;
	Integer bkImageId;
	String text;
	ScaleType scaleType = null;
	
	@Override
	public int configure(ITabComponent component) {
		component.setName( this.internalName );
		// ���������AActivity��ResourceAccessor����擾����
		OkosamaMediaPlayerActivity activity = (OkosamaMediaPlayerActivity) OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		View v = component.getView();
		if( v == null )
		{
			return -1;
		}
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		left, top, width, height 
        );
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
				Log.e("component setting error",
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
				Log.e("component setting error",
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
				activity.setAlbumAdp(
					new AlbumListAdapter(
						activity,
						R.layout.track_list_item,
						null,//Cursor cursor,
						new String[] {}, 
						new int[] {}
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
			Database.getInstance(external).createAlbumCursor(activity.getAlbumAdp().getQueryHandler(), null, null);
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
				activity.setArtistAdp(
						new ArtistAlbumListAdapter(
						activity,
						null, // cursor
	                    R.layout.track_list_item_group,
	                    new String[] {},
	                    new int[] {},
	                    R.layout.track_list_item_child,
	                    new String[] {},
	                    new int[] {}
				));
			}
			// Adapter�̐ݒ�
			lst.setAdapter(activity.getArtistAdp());

			// Activity�̃R���e�L�X�g���j���[�ɓo�^
			//lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// �J�[�\���̍쐬
			Database.getInstance(external).createArtistCursor(activity.getArtistAdp().getQueryHandler(), null);			
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
			String genre = "";
			String albumId = "";
			String artistId = "";
			
			// Adapter�̍쐬
			if( activity.getTrackAdp() == null )
			{
				activity.setTrackAdp(
					new TrackListAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
						null,//Cursor cursor,
						new String[] {}, 
						new int[] {},
						isnowplaying,
						disablenowplayingindicator,
						genre,
						albumId,
			            artistId
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
			Database.getInstance(external).createTrackCursor(activity.getTrackAdp().getQueryHandler(), null, null, true, null, null, null);			
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
				activity.setPlaylistAdp(
					new PlaylistListAdapter(
						activity.getApplication(),
						activity,
						R.layout.track_list_item,
						null,//Cursor cursor,
						new String[] { MediaStore.Audio.Playlists.NAME},
	                    new int[] { android.R.id.text1 }
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
			Database.getInstance(external).createPlaylistCursor(activity.getPlaylistAdp().getQueryHandler(), null, false);						
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
				activity.setTrackAdp(
					new TrackListAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
						null,//Cursor cursor,
						new String[] {}, 
						new int[] {},
						isnowplaying,
						disablenowplayingindicator,
						genre,
						albumId,
			            artistId
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
			Database.getInstance(external).createTrackCursor(activity.getTrackAdp().getQueryHandler(), Database.PlaylistName_NowPlaying, null, true, null, null, null);			
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
        v.setLayoutParams(lp);
        
        // ���C�A�E�g�ւ̔z�u�́A��ʂ�
        // m_RLmain.addView(m_musicPlayTabButton);
		return 0;
	}
	
	
}
