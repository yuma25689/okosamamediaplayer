package okosama.app.widget;

import okosama.app.R;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.service.MediaPlayerUtil;
import android.app.Activity;
import android.os.RemoteException;

public class PlayControlPanel extends ControlPanel {

	public PlayControlPanel(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
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
