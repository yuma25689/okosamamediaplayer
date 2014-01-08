package okosama.app.panel;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.color;
import okosama.app.R.drawable;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Label;
import okosama.app.widget.LabelImpl;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class NowPlayingControlPanel extends ControlPanel {
	static NowPlayingControlPanel instance;
	
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new NowPlayingControlPanel(activity);
		}
	}
	public static void deleteInstance()
	{
		removeFromParent();
		instance = null;
		
	}	
	public static NowPlayingControlPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());

//			if( -1 == tabBaseLayout.indexOfChild(instance.getView()) )
//			{
			tabBaseLayout.addView(instance.getView());
			parent = tabBaseLayout;				
//			}
		}
		else
		{
			Log.e("error","insert sub control panel");
		}
	}
	public static void removeFromParent() //ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());

			parent = null;
			
			// marquee対応
			if( instance.getNowPlayingSongLabel() != null && instance.getNowPlayingSongLabel().getView() != null )
			{
				LabelImpl tv = ((LabelImpl)instance.getNowPlayingSongLabel().getView());
				tv.setEllipsize(null);
			}
			
		}
	}

	public NowPlayingControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		TabComponentPropertySetter creationData[] = null;
		
		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
			
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataPort[] = {
					// --------------------- ARTIST
					new TabComponentPropertySetter(
						ControlIDs.TIME_ARTIST_LABEL, null, ComponentType.LABEL, 
						35, 320, 400, 80
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),		
					// --------------------- ALBUM
					new TabComponentPropertySetter(
						ControlIDs.TIME_ALBUM_LABEL, null, ComponentType.LABEL, 
						35, 380, 400, 80
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),			
			};
			creationData = creationDataPort;
		}
		else
		{
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataHorz[] = {
					// --------------------- ARTIST
					new TabComponentPropertySetter(
						ControlIDs.TIME_ARTIST_LABEL, null, ComponentType.LABEL, 
						300, 135, 80, 400
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),		
					// --------------------- ALBUM
					new TabComponentPropertySetter(
						ControlIDs.TIME_ALBUM_LABEL, null, ComponentType.LABEL, 
						400, 130, 80, 400
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),			
			};
			creationData = creationDataHorz;
			
		}
		absWidget widgets[] = {
				getNowPlayingArtistLabel()
				,getNowPlayingAlbumLabel()
			};
		// ---- action
//		// Timeコンポーネント
//		// shuffleボタン
//		SparseArray< IViewAction > actMapShuffle
//			= new SparseArray< IViewAction >();
//		actMapShuffle.put( IViewAction.ACTION_ID_ONCLICK, new ToggleShuffleAction() );
//		// repeatボタン
//		SparseArray< IViewAction > actMapRepeat
//			= new SparseArray< IViewAction >();
//		actMapRepeat.put( IViewAction.ACTION_ID_ONCLICK, new CycleRepeatAction() );
//
//		TabComponentActionSetter actionSetterCont[] = {
//				new TabComponentActionSetter( actMapShuffle )
//				,new TabComponentActionSetter( actMapRepeat )
//			};
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
//			// TODO:ボタンのアクションを設定
//			if( actionSetterCont[i] != null )
//			{
//				widget.acceptConfigurator(actionSetterCont[i]);
//			}
			
			// ボタンをこのタブ子項目として追加
			addChild( creationData[i].getInternalID(), widget );			
			tabBaseLayout.addView( widget.getView() );
			i++;
		}
		//tabBaseLayout.setBackgroundResource(R.drawable.okosama_app_widget_bg);		
	}
	Label nowPlayingSongLabel = null;
	public Label getNowPlayingSongLabel()
	{
		if( nowPlayingSongLabel == null )
		{
			nowPlayingSongLabel = DroidWidgetKit.getInstance().MakeLabel();
			if( null != nowPlayingSongLabel.getView() )
			{
				TextView tv = ((TextView)nowPlayingSongLabel.getView());
				tv.setTextColor(
						OkosamaMediaPlayerActivity.getResourceAccessor().getColor(
								android.R.color.primary_text_light_nodisable));
				tv.setSingleLine(); // 文字列を1行で表示. これがないと複数行に渡って表示されてしまうので、スクロールできない
				tv.setFocusableInTouchMode(true);				
				//tv.setEllipsize(TruncateAt.MARQUEE);
				tv.setGravity(Gravity.CENTER);
				tv.setTextSize(24.0f);//tv.getHeight()-2);//32.0f);
			}
		}		
		return nowPlayingSongLabel;
	}
    public void updateNowPlayingSongLabel(String strValue)
    {
    	if( nowPlayingSongLabel == null
    	|| nowPlayingSongLabel.getView() == null 
    	|| strValue == null )
    	{
    		return;
    	}
		LabelImpl tv = ((LabelImpl)nowPlayingSongLabel.getView());
    	try {
			if( MediaPlayerUtil.sService != null && MediaPlayerUtil.sService.isPlaying() )
			{
				//tv.setSingleLine(); // 文字列を1行で表示. これがないと複数行に渡って表示されてしまうので、スクロールできない
				//tv.setFocusableInTouchMode(true);	
				if( TruncateAt.MARQUEE != tv.getEllipsize() )
				{
					Log.d("marquee","set");
					tv.setEllipsize(TruncateAt.MARQUEE);
					tv.requestFocus();
				}
			}
			else
			{
				tv.setEllipsize(null);    		
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if( strValue.equals( tv.getText() ))
    	{
    		// いちいち設定し直すとMarqueeが効いていないように見えるため、テキストが同じ場合は、更新しない
    		return;
    	}
    	tv.setText(strValue);
    	return;
    }	
	Label nowPlayingArtistLabel = null;
	public Label getNowPlayingArtistLabel()
	{
		if( nowPlayingArtistLabel == null )
		{
			nowPlayingArtistLabel = DroidWidgetKit.getInstance().MakeLabel();
			((TextView)nowPlayingArtistLabel.getView()).setTextSize(22.0f);			
		}		
		return nowPlayingArtistLabel;
	}
    public void setNowPlayingArsistLabel(String strValue)
    {
    	if( nowPlayingArtistLabel == null
    	|| nowPlayingArtistLabel.getView() == null 
    	|| strValue == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingArtistLabel.getView()).setText(strValue);
    	return;
    }	
	Label nowPlayingAlbumLabel = null;
	public Label getNowPlayingAlbumLabel()
	{
		if( nowPlayingAlbumLabel == null )
		{
			nowPlayingAlbumLabel = DroidWidgetKit.getInstance().MakeLabel();
			((TextView)nowPlayingAlbumLabel.getView()).setTextSize(18.0f);			
		}		
		return nowPlayingAlbumLabel;
	}
    public void setNowPlayingAlbumLabel(String strValue)
    {
    	if( nowPlayingAlbumLabel == null
    	|| nowPlayingAlbumLabel.getView() == null 
    	|| strValue == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingAlbumLabel.getView()).setText(strValue);
    	return;
    }
	public static void clearNowPlayingDisplays() {
		if( NowPlayingControlPanel.getInstance() != null )
		{
			NowPlayingControlPanel.getInstance().updateNowPlayingSongLabel("");
			NowPlayingControlPanel.getInstance().setNowPlayingArsistLabel("");
			NowPlayingControlPanel.getInstance().setNowPlayingAlbumLabel("");
		}
	}

}
