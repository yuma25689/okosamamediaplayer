package okosama.app.storage;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class GenreStocker {
	// �J�����̃C���f�b�N�X�ێ��p 
    int mNameIdx;
    int mGenreIdIdx;
    // int mAudioIdIdx = MediaStore.Audio.Media._ID;
    String[] projAudio={MediaStore.Audio.Media._ID};
    
	private ArrayList<GenreData> allItems = new ArrayList<GenreData>();
	
    /**
     * �J�����̃C���f�b�N�X��ݒ�
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �e�J�����̃C���f�b�N�X��ݒ�
        	// �^�C�g���A�A�[�e�B�X�g�A����
        	mNameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
        	mGenreIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
        }
        return 0;
    }
	
    /**
     * android�̃��f�B�A����A�W�������̃f�[�^������ɐݒ肷��
     * @return
     */
    public int stockMediaDataFromDevice()
    {
    	Log.i("stockMediaDataFromDevice - genre","start");
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground - genre","start");
            	
            	// �J�[�\�������[�v����
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
			        			// �S�Ă̗v�f�����[�v����
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
