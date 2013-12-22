package okosama.app.storage;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class GenreStocker {
	// カラムのインデックス保持用 
    int mNameIdx;
    int mGenreIdIdx;
    // int mAudioIdIdx = MediaStore.Audio.Media._ID;
    String[] projAudio={MediaStore.Audio.Media._ID};
    
	private ArrayList<GenreData> allItems = new ArrayList<GenreData>();
	
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
						        Log.i("Tag-Number of songs for this genre", data.getGenreName() + ":" + cursorAudio.getCount()+"");
								if(cursorAudio.moveToFirst())
								{
									do{
										int index=cursorAudio.getColumnIndexOrThrow(
												MediaStore.Audio.Media._ID);
									    data.addAudioId(cursorAudio.getLong(index));  
									}while(
										OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false
										&& cursorAudio.moveToNext());
								}
								allItems.add(data);
			        		} while( 
			        		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false 
			        		&& cursor.moveToNext() );
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
            	Log.d("onPostExecute - genre","ret=" + ret );
            }
        };
        task.execute();
        return 0;
    }
	
	
	
}
