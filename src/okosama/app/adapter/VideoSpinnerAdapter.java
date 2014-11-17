package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.VideoData;
import android.widget.ArrayAdapter;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;

/**
 * VideoList�̃A�_�v�^
 * @author 25689
 *
 */
public class VideoSpinnerAdapter extends ArrayAdapter<VideoData> { 
	private static ArrayList<Integer> unknownVideoIds = new ArrayList<Integer>();
	private final String mNoSelection;

    /**
     * �A�_�v�^�̃R���X�g���N�^
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public VideoSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<VideoData> items) {
        super(currentactivity, layout, items );

        mNoSelection = currentactivity.getString(R.string.no_selection);
    }

	/**
	 * @param items the items to set
	 */
	public ArrayList<VideoData> convertItems(ArrayList<VideoData> items) {
		unknownVideoIds.clear();
        ArrayList<VideoData> itemsTmp = new ArrayList<VideoData>(items);
        VideoData dataNoSelect = new VideoData();
        dataNoSelect.setDataId(-1);
        dataNoSelect.setName(mNoSelection);
        itemsTmp.add(0,dataNoSelect);        
		return itemsTmp;
	}	
 }
