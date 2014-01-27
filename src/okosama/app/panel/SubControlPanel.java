package okosama.app.panel;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.CycleRepeatAction;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaStopAction;
import okosama.app.action.ToggleShuffleAction;
import okosama.app.action.TweetAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
import okosama.app.widget.ButtonImpl;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView.ScaleType;

public class SubControlPanel extends ControlPanel {
	static SubControlPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new SubControlPanel(activity);
		}
	}
	public static void deleteInstance()
	{
		removeFromParent();
		instance = null;
		
	}
	
	public static SubControlPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent(instance.getView());
			//if( -1 == tabBaseLayout.indexOfChild(instance.getView()) )
			//{
			tabBaseLayout.addView(instance.getView());
			parent = tabBaseLayout;
			//}
		}
		else
		{
			Log.e("error","insert sub control panel");
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

	public SubControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		TabComponentPropertySetter creationData[] = null;
		
		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataPort[] = {
					// --------------------- SHUFFLE
					new TabComponentPropertySetter(
						ControlIDs.SHUFFLE_BUTTON, null, ComponentType.BUTTON, 
						20, 470, 100, 100
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),
					// --------------------- REPEAT
					new TabComponentPropertySetter(
						ControlIDs.REPEAT_BUTTON, null, ComponentType.BUTTON, 
						200, 460, 100, 100
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),
					// --------------------- STOP
					new TabComponentPropertySetter(
						ControlIDs.STOP_BUTTON, null, ComponentType.BUTTON, 
						150, 560, 100, 100
						, null, R.drawable.selector_stop_button_image, "", ScaleType.FIT_XY
					),
					// --------------------- TWITTER
					new TabComponentPropertySetter(
						ControlIDs.TWEET_BUTTON, null, ComponentType.BUTTON, 
						370, 500, 80, 80
						, null, R.drawable.selector_internal_btn_image, "", ScaleType.FIT_XY
					),				
			};
			creationData = creationDataPort;
		}
		else
		{
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataHorz[] = {
					// --------------------- SHUFFLE
					new TabComponentPropertySetter(
						ControlIDs.SHUFFLE_BUTTON, null, ComponentType.BUTTON, 
						40, 580, 100, 100
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),
					// --------------------- REPEAT
					new TabComponentPropertySetter(
						ControlIDs.REPEAT_BUTTON, null, ComponentType.BUTTON, 
						350, 610, 100, 100
						, null, drawable.no_image, "", ScaleType.FIT_XY
					),
					// --------------------- STOP
					new TabComponentPropertySetter(
						ControlIDs.STOP_BUTTON, null, ComponentType.BUTTON, 
						130, 360, 100, 100
						, null, R.drawable.selector_stop_button_image, "", ScaleType.FIT_XY
					),
					// --------------------- TWITTER
					new TabComponentPropertySetter(
						ControlIDs.TWEET_BUTTON, null, ComponentType.BUTTON, 
						370, 500, 80, 80
						, null, R.drawable.selector_internal_btn_image, "", ScaleType.FIT_XY
					),				
			};
			creationData = creationDataHorz;			
		}
	
		absWidget widgets[] = {
				getShuffleButton()
				,getRepeatButton()
				,DroidWidgetKit.getInstance().MakeButton()
				,DroidWidgetKit.getInstance().MakeButton()
				
			};
		// ---- action
		// Timeコンポーネント
		// shuffleボタン
		SparseArray< IViewAction > actMapShuffle
			= new SparseArray< IViewAction >();
		actMapShuffle.put( IViewAction.ACTION_ID_ONCLICK, new ToggleShuffleAction() );
		// repeatボタン
		SparseArray< IViewAction > actMapRepeat
			= new SparseArray< IViewAction >();
		actMapRepeat.put( IViewAction.ACTION_ID_ONCLICK, new CycleRepeatAction() );

		// stopボタン
		SparseArray< IViewAction > actMapStop 
		= new SparseArray< IViewAction >();
		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
		// twitterボタン
		SparseArray< IViewAction > actMapTwitter
			= new SparseArray< IViewAction >();
		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
				
		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actMapShuffle )
				,new TabComponentActionSetter( actMapRepeat )
				,new TabComponentActionSetter( actMapStop )
				,new TabComponentActionSetter( actMapTwitter )
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
	
	}

	Button btnRepeat = null;
	public Button getRepeatButton()
	{
		if( btnRepeat == null )
		{
			btnRepeat = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnRepeat;
	}
	public void setRepeatButtonImage()
	{
        if (MediaPlayerUtil.sService == null || btnRepeat == null || btnRepeat.getView() == null ) return;
        try {
            switch (MediaPlayerUtil.sService.getRepeatMode()) {
                case MediaPlaybackService.REPEAT_ALL:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.selector_btn_no_repeat_image);
                    break;
                case MediaPlaybackService.REPEAT_CURRENT:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.selector_btn_one_repeat_image);
                    break;
                default:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.selector_btn_repeat_all_image);
                    break;
            }
        } catch (RemoteException ex) {
        }	
	}
	Button btnShuffle = null;
	public Button getShuffleButton()
	{
		if( btnShuffle == null )
		{
			btnShuffle = DroidWidgetKit.getInstance().MakeButton();
		}		
		return btnShuffle;
	}
	public void setShuffleButtonImage()
	{
        if (MediaPlayerUtil.sService == null || btnShuffle == null || btnShuffle.getView() == null ) return;
        try {
            switch (MediaPlayerUtil.sService.getShuffleMode()) {
                case MediaPlaybackService.SHUFFLE_AUTO:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.selector_btn_shuffle_auto_image);
                    break;
                case MediaPlaybackService.SHUFFLE_NORMAL:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.selector_btn_shuffle_all_image);
                    break;
                //case MediaPlaybackService.SHUFFLE_NONE:
                default:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.selector_btn_no_shuffle_image);
                    break;
            }
        } catch (RemoteException ex) {
        }	
	}

}
