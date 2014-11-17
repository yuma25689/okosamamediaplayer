package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.GenreData;
import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;

/**
 * GenreList�̃A�_�v�^
 * @author 25689
 *
 */
public class GenreSpinnerAdapter extends ArrayAdapter<GenreData> { 
//implements IAdapterUpdate, SectionIndexer {
    
	private LayoutInflater inflater;
	// private ArrayList<GenreData> items;
	private int iLayoutId;
	private ArrayList<GenreData> items = new ArrayList<GenreData>();
	private ArrayList<Long> unknownGenreIds = new ArrayList<Long>();
    // private OkosamaMediaPlayerActivity mActivity;
	private final String mUnknownGenre;
	private final String mNoSelection;

    // View�̃z���_�H
    static class ViewHolder {
        TextView line1;
    }

    /**
     * �A�_�v�^�̃R���X�g���N�^
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public GenreSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<GenreData> items) {
        super(currentactivity, layout, items );

        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUnknownGenre = currentactivity.getString(R.string.unknown_album_name);
        mNoSelection = currentactivity.getString(R.string.no_selection);
        
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        // mActivity = currentactivity;
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);
        setItems(items);
        // setItems(items);

    }

    /**
     * �V�����r���[�̍쐬�H
     */
//    @Override
//    public View getView(int pos, View convertView, ViewGroup parent) {
//    	View v = convertView;  
//    	if (v == null) {
//    	   ViewHolder vh = new ViewHolder();
//	       v = inflater.inflate(iLayoutId, null); 
//	       vh.line1 = (TextView) v.findViewById(R.id.line1);
//	       v.setTag(vh);
//    	}
//	    bindView(v,pos);
//    	return v;
//    }
//
//    /**
//     * �r���[�ƃf�[�^��R����
//     */
//    //@Override
//    public void bindView(View view, int pos) {
//        
//       	// �^�O����r���[�z���_�[���擾
//        ViewHolder vh = (ViewHolder) view.getTag();
//        // position����f�[�^���擾
//    	GenreData data = getItem(pos);
//    	
//    	if( data == null )
//    	{
//    		// �f�[�^���Ȃ��Ƃ����̂́A���S�ɂ���������Ԃ����E�E
//    		 vh.line1.setText("");
//    		 return;
//    	}
// 
//        // �A���o�������擾�A�r���[�ɐݒ�
//        String name = data.getGenreName();
//        String displayname = name;
//        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
//        if (unknown) {
//            displayname = mUnknownGenre;
//        }
//        vh.line1.setText(displayname);
//        
//    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    

	/**
	 * @return the items
	 */
	public ArrayList<GenreData> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<GenreData> items) {
		unknownGenreIds.clear();
		ArrayList<Integer> arrRemove = new ArrayList<Integer>();
        int index = 0;
		for( GenreData data : items )
		{
	        String name = data.getName();
	        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
			if( unknown )
			{
		        unknownGenreIds.add(data.getDataId());
		        if( 1 < unknownGenreIds.size())
		        {
		        	arrRemove.add(index);
		        	//this.items.remove(index);
		        }
			}
			index++;
		}
		for(int i=arrRemove.size();0<i;i--)
		{
			items.remove(arrRemove.get(i-1));
		}
        ArrayList<GenreData> itemsTmp = new ArrayList<GenreData>( items );
        GenreData dataNoSelect = new GenreData();
        dataNoSelect.setDataId(-1);
        dataNoSelect.setName(mNoSelection);
        itemsTmp.add(0,dataNoSelect);        
		this.items = itemsTmp;
	}	
 }
