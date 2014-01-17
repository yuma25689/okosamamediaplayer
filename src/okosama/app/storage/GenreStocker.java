package okosama.app.storage;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
// import android.util.SparseArray;

public class GenreStocker {
	public static final String GENRENAME_SEPARATE_STRING = "/";
	// カラムのインデックス保持用 
    int mNameIdx;
    int mGenreIdIdx;
    // int mAudioIdIdx = MediaStore.Audio.Media._ID;
    String[] projAudio={MediaStore.Audio.Media._ID};
    
	private ArrayList<GenreData> allItems = new ArrayList<GenreData>();
	
	// オーディオに対するジャンルの一覧
	private HashMap<Long,ArrayList<GenreData>> allGenreOfAudio = new HashMap<Long,ArrayList<GenreData>>();
	
	/**
	 * audioidに対するジャンル情報の一覧を返却する
	 * @param audioId
	 * @return
	 */
	public ArrayList<GenreData> getGenreOfAudio( long audioId )
	{
		ArrayList<GenreData> ret = null;
		// Log.i("getGenreOfAudio", "allGenreOfAudio size=" + allGenreOfAudio.size() );
		
		synchronized( allGenreOfAudio )
    	{
			if( allGenreOfAudio.containsKey(audioId) )
			{
				ret = allGenreOfAudio.get(audioId);
			}
    	}
		return ret;
	}
	
	/**
	 * Stringを表示用にフォーマットして返すバージョン
	 * @param audioId
	 * @return
	 */
	public String getGenreOfAudioString( long audioId )
	{
		String ret = null;
		ArrayList<GenreData> lst = getGenreOfAudio(audioId);
		if( lst != null )
		{
			// Log.i("getGenreOfAudio", "getGenre audioId=" + audioId + " size=" + lst.size() );
			StringBuilder sb = new StringBuilder();
			boolean bFirst = true;
			for( GenreData data : lst )
			{
				if( bFirst == false )
				{
					sb.append( GENRENAME_SEPARATE_STRING );
				}
				sb.append( data.getGenreName() );
				
				bFirst = false;
			}
			if( bFirst == false )
			{
				ret = new String(sb);
			}
		}
		return ret;
	}
	
    /**
     * カラムのインデックスを設定
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// 各カラムのインデックスを設定
        	// タイトル、アーティスト、時間
        	mNameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
        	mGenreIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
        }
        return 0;
    }
	
    /**
     * androidのメディアから、ジャンルのデータを内部に設定する
     * @return
     */
    public int stockMediaDataFromDevice()
    {
    	Log.i("stockMediaDataFromDevice - genre","start");
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground - genre","start");
            	
            	// カーソルをループする
    			Cursor cursor = Database.getInstance(
    				OkosamaMediaPlayerActivity.isExternalRef()
    			).createGenreCursor();			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			Log.w("genre - doInBk", "cursor is null or cursor closed!");
        			return -1;
        		}
        		
        		try {
	        		if( 0 > getColumnIndices(cursor) )
	        		{
	        			return -1;
	        		}
	        		synchronized(allItems)
	        		{
		            	allItems.clear();
		            	synchronized( allGenreOfAudio )
		            	{
			            	allGenreOfAudio.clear();
			            	if( 0 < cursor.getCount() )
			            	{
				            	Log.i("doInBackground","moveToFirst");
				        		cursor.moveToFirst();
				        		do 
				        		{
				            		GenreData data = new GenreData();
				        			// 全ての要素をループする
				            		data.setGenreId( cursor.getLong(mGenreIdIdx) );
				            		data.setGenreName( cursor.getString(mNameIdx) );
				            		
					    			Cursor cursorAudio = Database.getInstance(
					    					OkosamaMediaPlayerActivity.isExternalRef()
					    			).createSongListCursorFromGenre(data.getGenreId());
					    			try
					    			{
								        // Log.i("Tag-Number of songs for this genre", data.getGenreName() + ":" + cursorAudio.getCount()+"");
										if(cursorAudio.moveToFirst())
										{
											do{
												int index=cursorAudio.getColumnIndexOrThrow(
														MediaStore.Audio.Media._ID);
												//String strAudioId = cursorAudio.getString(index);
												long audioId = cursorAudio.getLong(index);
												ArrayList<GenreData> arrGenreOfAudio = null;//new ArrayList<GenreData>();
												if( allGenreOfAudio.containsKey(audioId) )
												{
													arrGenreOfAudio = allGenreOfAudio.get(audioId);
												}
												else
												{
													arrGenreOfAudio = new ArrayList<GenreData>();
												}
												arrGenreOfAudio.add(data);
												// Log.i("stock - genre", audioId + " " + data.getGenreId() );
												allGenreOfAudio.put(audioId, arrGenreOfAudio);
											}while(
												OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false
												&& cursorAudio.moveToNext());
										}
					        		} finally {
					        			cursorAudio.close();
					        		}
										
									allItems.add(data);
				        		} while( 
				        		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false 
				        		&& cursor.moveToNext() );
			            	}
		            	}
	        		}
        		} finally {
        			cursor.close();
        		}
        		if( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() )
        			return -2;
        		
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.d("onPostExecute - genre","ret=" + ret + " allGenreOfAudio size=" + allGenreOfAudio.size());
            }
        };
        task.execute();
        return 0;
    }
	
	//private ArrayList<GenreData> distinctItems = new ArrayList<GenreData>();
	public ArrayList<GenreData> getDistinctItems()
	{
		return allItems;
//		ArrayList<String> names = new ArrayList<String>();
//		for( GenreData data : allItems )
//		{
//			distinctItems.clear();
//			if( false == names.contains(data.getGenreName()) )
//			{
//				names.add(data.getGenreName());
//				distinctItems.add(data);
//			}
//		}
//		return distinctItems;		
	}
    
	
	
}
