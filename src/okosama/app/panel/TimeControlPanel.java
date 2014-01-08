package okosama.app.panel;

import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaSeekAction;
import okosama.app.action.TimeButtonClickAction;
import okosama.app.action.TimeButtonFlickDownAction;
import okosama.app.factory.DroidWidgetKit;
// import okosama.app.tab.ITabComponent;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
import okosama.app.widget.Label;
import okosama.app.widget.LabelImpl;
import okosama.app.widget.SeekBar;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewParent;
//import android.view.LayoutInflater;
//import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class TimeControlPanel extends ControlPanel {
	static TimeControlPanel instance;
	public static void createInstance(Activity activity)
	{
		if( instance == null )
		{
			instance = new TimeControlPanel(activity);
		}
	}
	public static void deleteInstance()
	{
		removeFromParent();
		instance = null;
		
	}
	public static TimeControlPanel getInstance()
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
	public static void removeFromParent()//ViewGroup tabBaseLayout )
	{
		if( instance != null && instance.getView() != null )
		{
			OkosamaMediaPlayerActivity.removeFromParent( instance.getView() );
//			if( instance.getView().getParent() != null )
//			{
//				ViewParent v = instance.getView().getParent();
//				if( v instanceof ViewGroup )
//				{
//					((ViewGroup) v).removeView(instance.getView());
//				}
//			}

			parent = null;				
		}
	}

	public TimeControlPanel(Activity activity) {
		super(activity);
		resetPanelViews(R.layout.tab_layout_content_generic);

		createTimesButton();

		TabComponentPropertySetter creationData[] = null;
		
		if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
		{
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataPort[] = {
				// ------------- HOUR
				new TabComponentPropertySetter(
					ControlIDs.TIME_HOUR10_BUTTON, null, ComponentType.BUTTON, 
					22, 240, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_HOUR1_BUTTON, null, ComponentType.BUTTON, 
					92, 240, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num3_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
	
				// ------------- MINUTE
				new TabComponentPropertySetter(
					ControlIDs.TIME_MINUTE10_BUTTON, null, ComponentType.BUTTON, 
					167, 230, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num4_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_MINUTE1_BUTTON, null, ComponentType.BUTTON, 
					237, 215, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num6_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				// --------------------- SECOND
				new TabComponentPropertySetter(
					ControlIDs.TIME_SECOND10_BUTTON, null, ComponentType.BUTTON, 
					303, 230, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num8_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY 
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_SECOND1_BUTTON, null, ComponentType.BUTTON, 
					373, 215, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				// --------------------- DURATION
				new TabComponentPropertySetter(
					ControlIDs.TIME_DURATION_LABEL, null, ComponentType.LABEL, 
					370, 340, 200, 50
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),		
			};
			creationData = creationDataPort;
			// OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		}
		else
		{
			//////////////////// button //////////////////////////
			TabComponentPropertySetter creationDataHorz[] = {
				// ------------- HOUR
				new TabComponentPropertySetter(
					ControlIDs.TIME_HOUR10_BUTTON, null, ComponentType.BUTTON, 
					170, 92, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_HOUR1_BUTTON, null, ComponentType.BUTTON, 
					180, 177, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num3_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
	
				// ------------- MINUTE
				new TabComponentPropertySetter(
					ControlIDs.TIME_MINUTE10_BUTTON, null, ComponentType.BUTTON, 
					200, 262, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num4_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_MINUTE1_BUTTON, null, ComponentType.BUTTON, 
					215, 347, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num6_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				// --------------------- SECOND
				new TabComponentPropertySetter(
					ControlIDs.TIME_SECOND10_BUTTON, null, ComponentType.BUTTON, 
					230, 432, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num8_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY 
				),
				new TabComponentPropertySetter(
					ControlIDs.TIME_SECOND1_BUTTON, null, ComponentType.BUTTON, 
					215, 518, 
					ControlDefs.TIMECHAR_WIDTH, 
					ControlDefs.TIMECHAR_HEIGHT
					, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
				),
				// --------------------- DURATION
				new TabComponentPropertySetter(
					ControlIDs.TIME_DURATION_LABEL, null, ComponentType.LABEL, 
					270, 140
					, 50, 200
					, null, drawable.no_image, "", ScaleType.FIT_XY
				),		
			};
			creationData = creationDataHorz;			
		}
		absWidget widgets[] = {
				getTimesButton()[0]
				,getTimesButton()[1]
				,getTimesButton()[2]
				,getTimesButton()[3]
				,getTimesButton()[4]
				,getTimesButton()[5]
				,getDurationLabel()
			};
		// ---- action
		// Timeコンポーネント
		SparseArray< IViewAction > actMapTime1 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTime2 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTime3 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTime4 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTime5 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTime6 = new SparseArray< IViewAction >();
		actMapTime1.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_10) );	
		actMapTime2.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_1) );	
		actMapTime3.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_10) );	
		actMapTime4.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_1) );	
		actMapTime5.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_10) );	
		actMapTime6.put( IViewAction.ACTION_ID_ONFLICKUP, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_1) );	
		actMapTime1.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_HOUR_10) );	
		actMapTime2.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_HOUR_1) );	
		actMapTime3.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_MINUTE_10) );	
		actMapTime4.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_MINUTE_1) );	
		actMapTime5.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_SEC_10) );	
		actMapTime6.put( IViewAction.ACTION_ID_ONFLICKDOWN, new TimeButtonFlickDownAction(TimeButtonFlickDownAction.TIME_ID_SEC_1) );	

		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actMapTime1 )
				,new TabComponentActionSetter( actMapTime2 )
				,new TabComponentActionSetter( actMapTime3 )
				,new TabComponentActionSetter( actMapTime4 )
				,new TabComponentActionSetter( actMapTime5 )
				,new TabComponentActionSetter( actMapTime6 )
				,null
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
			// ボタンを配置
			// これは、setActivateで行う?
			// componentContainer.addView( btn.getView() );
			i++;
		}
		return;
	}
	Label durationLabel = null;
	public Label getDurationLabel()
	{
		if( durationLabel == null )
		{
			durationLabel = DroidWidgetKit.getInstance().MakeLabel();
			((TextView)durationLabel.getView()).setTextColor(
					OkosamaMediaPlayerActivity.getResourceAccessor().getColor(
							android.R.color.secondary_text_dark));
			((TextView)durationLabel.getView()).setTextSize(24.0f);//32.0f);			
		}		
		return durationLabel;
	}
    private static final Integer[] sDurationArgs = new Integer[6];	
    public void setDurationLabel(long duration)
    {
    	if( durationLabel == null
    	|| durationLabel.getView() == null )
    	{
    		return;
    	}
    	if( duration == 0 )
    	{
    		((LabelImpl)durationLabel.getView()).setText("");
    		return;
    	}
    	boolean bShowImgFlg[] = {
                ( duration >= (3600*60) )
                ,( duration >= 3600 )
                ,( duration >= 600 )
                ,( duration >= 60)
                ,( duration >= 10)
                ,( duration > 0)
    	};    	
    	long tmp = 0;
        final Integer[] timeArgs = sDurationArgs;
        tmp = duration;
        timeArgs[0] = (int) (tmp / (3600*60));
        tmp -= timeArgs[0]*(3600*60);
        timeArgs[1] = (int) (tmp / 3600);
        tmp -= timeArgs[1]*3600;
        timeArgs[2] = (int) (tmp / 600);
        tmp -= timeArgs[2]*600;
        timeArgs[3] = (int) (tmp / 60);
        tmp -= timeArgs[3]*60;
        timeArgs[4] = (int) (tmp / 10);
        tmp -= timeArgs[4]*10;
        timeArgs[5] = (int) tmp;
        
        StringBuilder sDuration = new StringBuilder();
        for( int i = 0; i < timeArgs.length; i++ )
        {
        	if( bShowImgFlg[i] == true )
        	{
        		sDuration.append(timeArgs[i]);
        	}
        }
        ((LabelImpl)durationLabel.getView()).setText(sDuration);
    }
    // TODO: 後で別に移すべき
	SeekBar seekBar = null;
	public SeekBar getProgressBar()
	{
		if( seekBar == null )
		{
			seekBar = DroidWidgetKit.getInstance().MakeSeekBar();
		}		
		return seekBar;
	}    
	/**
	 * Activeかどうかを設定。子の同関数もコールする
	 * @param b
	 *
	public void setActivate( boolean b )
	{
		for( int i=0; i < children.size(); i++ )
		{
			children.valueAt(i).setActivate( b );
        }
	}
	*/

	private void createTimesButton()
	{
		if( btnTimes == null )
		{
			btnTimes = new Button[6];
			btnTimes[0] = DroidWidgetKit.getInstance().MakeButton();
			btnTimes[1] = DroidWidgetKit.getInstance().MakeButton();
			btnTimes[2] = DroidWidgetKit.getInstance().MakeButton();
			btnTimes[3] = DroidWidgetKit.getInstance().MakeButton();
			btnTimes[4] = DroidWidgetKit.getInstance().MakeButton();
			btnTimes[5] = DroidWidgetKit.getInstance().MakeButton();		
		}
	}
	public Button[] getTimesButton()
	{
		return btnTimes;
	}
	
	Button btnTimes[] = null;

	public static void clearTimeDisplays() {
		if( TimeControlPanel.getInstance() != null )
		{
			TimeControlPanel.getInstance().setDurationLabel(0);
			TimeControlPanel.getInstance().getProgressBar().setMax(0);
			TimeControlPanel.getInstance().getProgressBar().setProgress(0);
			TimeControlPanel.getInstance().getProgressBar().setVisibility(View.VISIBLE);
		}
	}
	
}
