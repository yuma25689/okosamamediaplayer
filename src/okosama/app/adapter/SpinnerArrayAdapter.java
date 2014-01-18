package okosama.app.adapter;

import java.util.ArrayList;

import android.widget.ArrayAdapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.ISimpleData;

public class SpinnerArrayAdapter<T extends ISimpleData> extends ArrayAdapter<T> {
	private final String mNoSelection;

    /**
     * アダプタのコンストラクタ
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public SpinnerArrayAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<T> items) {
        super(currentactivity, layout, items );

        mNoSelection = currentactivity.getString(R.string.no_selection);
    }

	/**
	 * @param items the items to set
	 */
	public static <E extends ISimpleData> ArrayList<E> convertItems(ArrayList<E> items, E topData, int nameId) {
        ArrayList<E> itemsTmp = new ArrayList<E>(items);
//        T dataNoSelect = new T();
        String name = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(nameId);
        String noSelection = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(R.string.no_selection);
        if( topData != null )
        {
        	topData.setDataId(-1);
            topData.setName(name + ":" + noSelection);
        	itemsTmp.add(0,topData);
        }
		return itemsTmp;
	}

}
