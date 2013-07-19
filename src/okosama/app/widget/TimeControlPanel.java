package okosama.app.widget;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.IViewAction;
import okosama.app.action.TimeButtonClickAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import android.app.Activity;
import android.util.SparseArray;
import android.widget.ImageView.ScaleType;

public class TimeControlPanel extends ControlPanel {

	public TimeControlPanel(Activity activity) {
		super(activity);

		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- HOUR
			new TabComponentPropertySetter(
				"hour10", ComponentType.BUTTON, 
				22, 400, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
			),
			new TabComponentPropertySetter(
				"hour1", ComponentType.BUTTON, 
				92, 400, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num3_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
			),

			// ------------- MINUTE
			new TabComponentPropertySetter(
				"minute10", ComponentType.BUTTON, 
				167, 390, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num4_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
			),
			new TabComponentPropertySetter(
				"minute1", ComponentType.BUTTON, 
				237, 375, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num6_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
			),
			// --------------------- SECOND
			new TabComponentPropertySetter(
				"sec10", ComponentType.BUTTON, 
				303, 390, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num8_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY 
			),
			new TabComponentPropertySetter(
				"sec1", ComponentType.BUTTON, 
				373, 375, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
			),
			// --------------------- DURATION
			new TabComponentPropertySetter(
				"durationlabel", ComponentType.LABEL, 
				40, 370, 200, 50
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),		
			// --------------------- SONG
			new TabComponentPropertySetter(
				"songlabel", ComponentType.LABEL, 
				30, 320, 400, 50
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),		
			// --------------------- ARTIST
			new TabComponentPropertySetter(
				"artistlabel", ComponentType.LABEL, 
				35, 480, 400, 50
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),		
			// --------------------- ALBUM
			new TabComponentPropertySetter(
				"albumlabel", ComponentType.LABEL, 
				35, 530, 400, 50
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),			
		};
		// OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	
		absWidget widgets[] = {
				OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[0]
				,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[1]
				,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[2]
				,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[3]
				,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[4]
				,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[5]
				,getDurationLabel()
				,getNowPlayingSongLabel()
				,getNowPlayingArtistLabel()
				,getNowPlayingAlbumLabel()
			};
		// ---- action
		// Timeコンポーネント
		SparseArray< IViewAction > actMapTimeClick1 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTimeClick2 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTimeClick3 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTimeClick4 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTimeClick5 = new SparseArray< IViewAction >();
		SparseArray< IViewAction > actMapTimeClick6 = new SparseArray< IViewAction >();
		actMapTimeClick1.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_10) );	
		actMapTimeClick2.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_1) );	
		actMapTimeClick3.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_10) );	
		actMapTimeClick4.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_1) );	
		actMapTimeClick5.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_10) );	
		actMapTimeClick6.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_1) );	

		TabComponentActionSetter actionSetterCont[] = {
				new TabComponentActionSetter( actMapTimeClick1 )
				,new TabComponentActionSetter( actMapTimeClick2 )
				,new TabComponentActionSetter( actMapTimeClick3 )
				,new TabComponentActionSetter( actMapTimeClick4 )
				,new TabComponentActionSetter( actMapTimeClick5 )
				,new TabComponentActionSetter( actMapTimeClick6 )
				,null
				,null
				,null
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
			addChild( widget );
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
	Label nowPlayingSongLabel = null;
	public Label getNowPlayingSongLabel()
	{
		if( nowPlayingSongLabel == null )
		{
			nowPlayingSongLabel = DroidWidgetKit.getInstance().MakeLabel();
		}		
		return nowPlayingSongLabel;
	}
    public void setNowPlayingSongLabel(String strSong)
    {
    	if( nowPlayingSongLabel == null
    	|| nowPlayingSongLabel.getView() == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingSongLabel.getView()).setText(strSong);
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
    public void setNowPlayingArsistLabel(String strSong)
    {
    	if( nowPlayingArtistLabel == null
    	|| nowPlayingArtistLabel.getView() == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingArtistLabel.getView()).setText(strSong);
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
    public void setNowPlayingAlbumLabel(String strSong)
    {
    	if( nowPlayingAlbumLabel == null
    	|| nowPlayingAlbumLabel.getView() == null )
    	{
    		return;
    	}
    	((LabelImpl)nowPlayingAlbumLabel.getView()).setText(strSong);
    	return;
    }

}
