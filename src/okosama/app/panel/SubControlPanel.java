package okosama.app.panel;

import okosama.app.ControlIDs;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.CycleRepeatAction;
import okosama.app.action.IViewAction;
import okosama.app.action.ToggleShuffleAction;
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
	public static SubControlPanel getInstance()
	{
		return instance;
	}
	public static void insertToLayout( ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			if( instance.getView().getParent() != null )
			{
				ViewParent v = instance.getView().getParent();
				if( v instanceof ViewGroup )
				{
					((ViewGroup) v).removeView(instance.getView());
				}
			}
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

	public SubControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
				// --------------------- SHUFFLE
				new TabComponentPropertySetter(
					ControlIDs.SHUFFLE_BUTTON, null, ComponentType.BUTTON, 
					20, 420, 100, 100
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),
				// --------------------- REPEAT
				new TabComponentPropertySetter(
					ControlIDs.REPEAT_BUTTON, null, ComponentType.BUTTON, 
					200, 390, 100, 100
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),
		};
	
		absWidget widgets[] = {
				getShuffleButton()
				,getRepeatButton()
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

		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actMapShuffle )
				,new TabComponentActionSetter( actMapRepeat )
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
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_no_repeat_image);
                    break;
                case MediaPlaybackService.REPEAT_CURRENT:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_one_repeat_image);
                    break;
                default:
                	((ButtonImpl)btnRepeat.getView()).setImageResource(R.drawable.btn_repeat_all_image);
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
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_shuffle_auto_image);
                    break;
                case MediaPlaybackService.SHUFFLE_NORMAL:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_shuffle_all_image);
                    break;
                //case MediaPlaybackService.SHUFFLE_NONE:
                default:
                	((ButtonImpl)btnShuffle.getView()).setImageResource(R.drawable.btn_no_shuffle_image);
                    break;
            }
        } catch (RemoteException ex) {
        }	
	}

}
