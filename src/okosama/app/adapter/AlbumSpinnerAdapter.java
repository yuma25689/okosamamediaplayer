package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.AlbumData;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;

/**
 * AlbumListのアダプタ
 * @author 25689
 *
 */
public class AlbumSpinnerAdapter extends ArrayAdapter<AlbumData> { 
	private static ArrayList<Long> unknownAlbumIds = new ArrayList<Long>();
	private static String mNoSelection;

    /**
     * アダプタのコンストラクタ
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public AlbumSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<AlbumData> items) {
        super(currentactivity, layout, items );

        mNoSelection = currentactivity.getString(R.string.no_selection);
    }

	/**
	 * @param items the items to set
	 */
	public static ArrayList<AlbumData> convertItems(ArrayList<AlbumData> items) {
		unknownAlbumIds.clear();
		ArrayList<Integer> arrRemove = new ArrayList<Integer>();
        int index = 0;
		for( AlbumData data : items )
		{
	        String name = data.getName();
	        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
			if( unknown )
			{
		        unknownAlbumIds.add(data.getDataId());
		        if( 1 < unknownAlbumIds.size())
		        {
		        	arrRemove.add(index);
		        }
			}
			index++;
		}
		for(int i=arrRemove.size();0<i;i--)
		{
			items.remove(arrRemove.get(i-1));
		}
        ArrayList<AlbumData> itemsTmp = new ArrayList<AlbumData>(items.size());
        itemsTmp.addAll(items);
        
        AlbumData dataNoSelect = new AlbumData();
        dataNoSelect.setDataId(-1);
        String name = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(R.string.album_);                
        String noSelection = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(R.string.no_selection);
        dataNoSelect.setName(name + ":" + noSelection);
        itemsTmp.add(0,dataNoSelect);
        
        return itemsTmp;
	}	
 }
