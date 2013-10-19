package okosama.app.tab;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.AlbumListAdapter;
import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.adapter.PlaylistListAdapter;
import okosama.app.adapter.TrackListAdapter;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.storage.AlbumData;
import okosama.app.storage.TrackData;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Tabの構成要素を作成するために必要な全ての情報をここに格納してTabComponentに渡す
 * また、それをTabComponentに適用するアクション
 * TabComponentへのVisitor
 * @author 25689
 *
 */
public class TabComponentPropertySetter implements ITabComponentConfigurator {
	public TabComponentPropertySetter(
			Integer internalID,
			ComponentType type ) 
	{
		this.internalID = internalID;
		this.type = type;
	}
	public TabComponentPropertySetter(Integer internalID,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId ) 
	{
		this.internalID = internalID;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
	}	
	public TabComponentPropertySetter(Integer internalID,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId, Integer bkImageId ) 
	{
		this.internalID = internalID;
		this.type = type;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.imageId = imageId;
		this.bkImageId = bkImageId;
	}
	public TabComponentPropertySetter(Integer internalID,
			ComponentType type, Integer left, Integer top,
			Integer width, Integer height, Integer imageId,
			Integer bkImageId, String text, ScaleType scaleType) 
	{
		this.internalID = internalID;
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
		NONE, TAB, TAB_PAGE, BUTTON, LIST_ALBUM, 
		LIST_ARTIST, LIST_SONG, LIST_PLAYLIST, LIST_NOWPLAYLIST,
		TEXT, IMAGE, EXPLIST, TOGGLEBUTTON, LABEL, PROGRESS
	};
	
	
	boolean external = false;	// TODO:暫定版
	// String internalName;
	Integer internalID;
	ComponentType type = ComponentType.NONE;
	Integer left, top, width, height;
	Integer imageId;
	Integer bkImageId;
	String text;
	ScaleType scaleType = null;
	
	public Integer getInternalID()
	{
		return internalID;
	}
	
	@Override
	public int configure(ITabComponent component) {
		component.setInternalID( this.internalID );
		// 微妙だが、ActivityはResourceAccessorから取得する
		OkosamaMediaPlayerActivity activity 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		
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
			// この辺のソースは、最悪です。
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
					// メモリ解放のテスト用
					// TODO:後で必ず消す
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
					// Selectorなので、多分Bitmapにするのは無理
					// TODO: できたら後で自力実装？
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
			
			// Adapterの作成
			if( activity.getAlbumAdp() == null )
			{
				ArrayList<AlbumData> data = new ArrayList<AlbumData>();
				activity.setAlbumAdp(
					new AlbumListRawAdapter(
						activity,
						R.layout.track_list_item,//,
						data//,//Cursor cursor,
//						new String[] {}, 
//						new int[] {}
					)
				);
			}
			// Adapterの設定
			lst.setAdapter(activity.getAlbumAdp());

			// Activityのコンテキストメニューに登録
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);
			
			// カーソルの作成
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
				// Adapterの作成
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
			// Adapterの設定
			lst.setAdapter(activity.getArtistAdp());

			// Activityのコンテキストメニューに登録
			//lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// カーソルの作成
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
			
			// TODO: これは、暫定版
			boolean editMode = false;
//			 "nowplaying".equals(mPlaylist),
//             mPlaylist != null &&
//             !(mPlaylist.equals("podcasts") || mPlaylist.equals("recentlyadded")
			boolean isnowplaying = false;
			boolean disablenowplayingindicator = false;
			String genre = "";
			String albumId = "";
			String artistId = "";
			
			// Adapterの作成
			if( activity.getTrackAdp() == null )
			{
				ArrayList<TrackData> data = new ArrayList<TrackData>();
				activity.setTrackAdp(
					new TrackListRawAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
//						null,//Cursor cursor,
//						new String[] {}, 
//						new int[] {},
						data,
						isnowplaying,
						disablenowplayingindicator,
						genre,
						albumId,
			            artistId
			        )
				);
			}
			// Adapterの設定
			lst.setAdapter(activity.getTrackAdp());
			
			// Activityのコンテキストメニューに登録
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// カーソルの作成
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
			
			// Adapterの作成
			if( activity.getPlaylistAdp() == null )
			{
				activity.setPlaylistAdp(
					new PlaylistListAdapter(
						activity.getApplication(),
						activity,
						R.layout.track_list_item,
						null,//Cursor cursor,
						new String[] { PlaylistsColumns.NAME},
	                    new int[] { android.R.id.text1 }
					)
				);
			}
			// Adapterの設定
			lst.setAdapter(activity.getPlaylistAdp());
			
			// Activityのコンテキストメニューに登録
			// lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// カーソルの作成
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
			
			// TODO: これは、暫定版
			boolean editMode = false;
//			 "nowplaying".equals(mPlaylist),
//             mPlaylist != null &&
//             !(mPlaylist.equals("podcasts") || mPlaylist.equals("recentlyadded")
			boolean isnowplaying = false;
			boolean disablenowplayingindicator = false;
			String genre = "";
			String albumId = "";
			String artistId = "";
			
			// Adapterの作成
			if( activity.getTrackAdp() == null )
			{
				ArrayList<TrackData> data = new ArrayList<TrackData>();
				activity.setTrackAdp(
					new TrackListRawAdapter(
						activity,
						editMode ? R.layout.edit_track_list_item : R.layout.track_list_item,
//						null,//Cursor cursor,
//						new String[] {}, 
//						new int[] {},
						data,
						isnowplaying,
						disablenowplayingindicator,
						genre,
						albumId,
			            artistId
			        )
				);
			}
			// Adapterの設定
			lst.setAdapter(activity.getTrackAdp());
			
			// Activityのコンテキストメニューに登録
			//lst.setTag(TabLeaf.TAGKEY_LISTNAME, component ); //this.internalName);
			lst.setTag(component);
			activity.registerForContextMenu(lst);

			// カーソルの作成
			// 現在の再生対象のプレイリストを指定する
			// Database.getInstance(external).createTrackCursor(activity.getTrackAdp().getQueryHandler(), Database.PlaylistName_NowPlaying, null, true, null, null, null);			
		}		
		else if( type == ComponentType.LABEL )
		{
			TextView txt = null;
			if( v instanceof TextView )
			{
				txt = (TextView)v;
			}
			txt.setTextSize(18.0f);
		}
		else if( type == ComponentType.PROGRESS )
		{
		}		
		if( bkImageId != null )
		{
			// メモリ解放のテスト用
			// TODO:検証
			if( v.getBackground() != null )
			{
				v.getBackground().setCallback(null);
			}
			Bitmap bmp = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId(bkImageId);
			if( bmp == null )
			{
				// 多分Selectorか何かなので、Bitmapにするのは無理
				v.setBackgroundResource(bkImageId);
			}
			else
			{
				BitmapDrawable d = new BitmapDrawable( bmp );
				v.setBackgroundDrawable(d);
			}
			// TODO: できたら後で自力実装？
			// v.setBackgroundResource(bkImageId);
		}
        v.setLayoutParams(lp);
        
        // レイアウトへの配置は、上位で
        // m_RLmain.addView(m_musicPlayTabButton);
		return 0;
	}
	
	
}
