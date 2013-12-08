package okosama.app.panel;

import okosama.app.ControlIDs;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayPauseAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.factory.DroidWidgetKit;
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

public class PlayControlPanel extends ControlPanel {
	static PlayControlPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new PlayControlPanel(activity);
		}
	}
	public static PlayControlPanel getInstance()
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
			Log.e("error","insert play control panel");
		}
	}
	public static void removeToLayout( ViewGroup tabBaseLayout )
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

			parent = null;				
		}
	}
	
	public PlayControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
				// --------------------- PLAY
				new TabComponentPropertySetter(
					ControlIDs.PLAY_BUTTON, null, ComponentType.BUTTON, 
					180, 10, 80, 100
					, null, R.drawable.play_button_image, "", ScaleType.FIT_XY
				),
				// --------------------- NEXT
				new TabComponentPropertySetter(
					ControlIDs.NEXT_BUTTON, null, ComponentType.BUTTON, 
					360, 6, 100, 100
					, null, R.drawable.next_button_image, "", ScaleType.FIT_XY
				),
				// --------------------- PREV
				new TabComponentPropertySetter(
					ControlIDs.PREV_BUTTON, null, ComponentType.BUTTON, 
					30, 18, 100, 100
					, null, R.drawable.back_button_image, "", ScaleType.FIT_XY
				),
		};
	
		absWidget widgets[] = {
				getPlayPauseButton()
				,getNextButton()
				,getPrevButton()
			};
		// ---- action
		// Timeコンポーネント
		SparseArray< IViewAction > actPlayClick = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actNextClick = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actPrevClick = new SparseArray< IViewAction >();
		actPlayClick.put( IViewAction.ACTION_ID_ONCLICK, new MediaPlayPauseAction() );	
		actNextClick.put( IViewAction.ACTION_ID_ONCLICK, new NextAction() );	
		actPrevClick.put( IViewAction.ACTION_ID_ONCLICK, new PrevAction() );	

		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actPlayClick )
				,new TabComponentActionSetter( actNextClick )
				,new TabComponentActionSetter( actPrevClick )
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
		tabBaseLayout.setBackgroundResource(R.drawable.okosama_app_widget_bg);
		return;
		
	}

	Button btnPlayPause = null;
	public Button getPlayPauseButton()
	{
		if( btnPlayPause == null )
		{
			btnPlayPause = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnPlayPause;
	}
	public void setPlayPauseButtonImage()
	{
        if (MediaPlayerUtil.sService == null 
        		|| btnPlayPause == null 
        		|| btnPlayPause.getView() == null ) return;
        try {
            if(MediaPlayerUtil.sService.isPlaying()== true) 
            {
               	((ButtonImpl)btnPlayPause.getView()).setImageResource(R.drawable.pause_button_image);
            }
            else
            {
            	((ButtonImpl)btnPlayPause.getView()).setImageResource(R.drawable.play_button_image);
            }
        } catch (RemoteException ex) {
        }	
	}    
	Button btnNext = null;
	public Button getNextButton()
	{
		if( btnNext == null )
		{
			btnNext = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnNext;
	}
	Button btnPrev = null;
	public Button getPrevButton()
	{
		if( btnPrev == null )
		{
			btnPrev = DroidWidgetKit.getInstance().MakeButton();
		}
		return btnPrev;
	}
}
