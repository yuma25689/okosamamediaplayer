package okosama.app.widget;

import okosama.app.R;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.app.Activity;
import android.os.RemoteException;

public class SubControlPanel extends ControlPanel {

	public SubControlPanel(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
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
