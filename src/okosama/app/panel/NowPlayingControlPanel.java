package okosama.app.panel;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Label;
import okosama.app.widget.LabelImpl;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView.ScaleType;

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
		}
	}

	public NowPlayingControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
				// --------------------- ARTIST
				new TabComponentPropertySetter(
					ControlIDs.TIME_ARTIST_LABEL, null, ComponentType.LABEL, 
					35, 300, 400, 80
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),		
				// --------------------- ALBUM
				new TabComponentPropertySetter(
					ControlIDs.TIME_ALBUM_LABEL, null, ComponentType.LABEL, 
					35, 380, 400, 80
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),			
		};
	
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
		}		
		return nowPlayingSongLabel;
	}
    public void setNowPlayingSongLabel(String strValue)
    {
    	if( nowPlayingSongLabel == null
    	|| nowPlayingSongLabel.getView() == null 
    	|| strValue == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingSongLabel.getView()).setText(strValue);
    	return;
    }	
	Label nowPlayingArtistLabel = null;
	public Label getNowPlayingArtistLabel()
	{
		if( nowPlayingArtistLabel == null )
		{
			nowPlayingArtistLabel = DroidWidgetKit.getInstance().MakeLabel();
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
			NowPlayingControlPanel.getInstance().setNowPlayingSongLabel("");
			NowPlayingControlPanel.getInstance().setNowPlayingArsistLabel("");
			NowPlayingControlPanel.getInstance().setNowPlayingAlbumLabel("");
		}
	}

}
