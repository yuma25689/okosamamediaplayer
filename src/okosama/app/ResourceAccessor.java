/**
 * 
 */
package okosama.app;

//import java.util.HashMap;
//import java.util.Map;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabChangeAnimation;
import okosama.app.widget.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Environment;
import android.util.SparseArray;

/**
 * ���\�[�X�ɃA�N�Z�X���邽�߂̃N���X
 * dalvik�̐���(�����炭�A24MB�`48MB���炢�̃������m�ۂŗ�����)
 * ��������邽�߂ɁA�Ȃ�ׂ�Drawable��20MB�ȏ�ɂȂ�Ȃ��悤�ɂ���
 * (1��ʂŎg��Drawable�̃T�C�Y������𒴂��Ă��܂��A�Ȃǂ̂ǂ����悤���Ȃ��ꍇ�͏���)
 * @author 25689
 *
 */
public final class ResourceAccessor {
	
	public static String SELECTOR_PREFIX ="selector_";
	public TabChangeAnimation tabAnim = new TabChangeAnimation();
	// �����ɁA�A�v���P�[�V�����̏�Ԃ��i�[����
	public AppStatus appStatus = new AppStatus();
	public MotionObserver motionObserver = new MotionObserver();
	public void initMotionSenser(Activity act)
	{
		motionObserver.init(act);
	}
	public void rereaseMotionSenser()
	{
		motionObserver.release();
	}	
	//public ArrayList<Button> commonBtns = null;

	public List nowPlayingListView = null;
	public List getNowPlayingListView()	//IListBehavior _behavior)
	{
		if( nowPlayingListView == null )
		{
			nowPlayingListView = DroidWidgetKit.getInstance().MakeList(null);//_behavior);
		}
		return nowPlayingListView;
	}
	
	public static final int SOUND_MAX_COUNT = 9;
	public static final int SOUND_RES_IDS[] =
		{
			R.raw.sound1,
			R.raw.sound2,
			R.raw.sound3,
			R.raw.sound4,
			R.raw.sound5,
			R.raw.sound6,
			R.raw.sound7,
			R.raw.sound8,
			R.raw.sound9
		};
	private int soundIds[];
	private int iSoundLoadCnt = 0;
	private SoundPool soundPool;
	
	private SparseArray<Bitmap> bmpArray = new SparseArray<Bitmap>();
	
	
	// ���\�[�X���擾���邽�߂̃A�N�e�B�r�e�B��ݒ�
	// TODO: �������A�����ɕێ����Ă����ƁA
	// �ċN����ȂǂɃA�N�e�B�r�e�B���L�����ǂ������ׂȂ��Ă����̂��낤���H
	OkosamaMediaPlayerActivity activity;
	
	public void setActivity(OkosamaMediaPlayerActivity activity) {
		this.activity = activity;
	}
	public OkosamaMediaPlayerActivity getActivity() {
		return this.activity;
	}
	// Singleton
	private static ResourceAccessor instance = null;
	private ResourceAccessor(OkosamaMediaPlayerActivity activity) 
	{
		this.activity = activity;
	}
	public static void CreateInstance( OkosamaMediaPlayerActivity activity )
	{
		if( instance == null ) 
		{
			instance = new ResourceAccessor( activity );
		}
		else
		{
			instance.setActivity( activity );
		}
	}
	public static ResourceAccessor getInstance()
	{
		return instance;
	}
	public void initSound()
	{
		// �����o�͐ݒ�̏��������s��
		soundPool = new SoundPool(SOUND_MAX_COUNT,AudioManager.STREAM_MUSIC,100);
		soundPool.setOnLoadCompleteListener(
				new OnLoadCompleteListener()
				{
					@Override
					public void onLoadComplete(SoundPool s,int Id, int sts)
					{
						if( sts == 0 ) iSoundLoadCnt++;
					}
				}
		);
		soundIds = new int[SOUND_RES_IDS.length];
		int j=0;
		for( int i : SOUND_RES_IDS ) {
			soundIds[j] = soundPool.load(this.activity, i, 1);
			j++;
		}
	}
	public void playSound( int idIndex )
	{
		if( iSoundLoadCnt != SOUND_RES_IDS.length 
		|| idIndex < 0 
		|| SOUND_RES_IDS.length <= idIndex)
		{
			// ���������̏ꍇ�A�������́Aindex�������̏ꍇ�A�Đ����Ȃ�
			return;
		}
		// id, leftVol, rightVol, priority, loop, speedrate
		soundPool.play(soundIds[idIndex], 2.0f, 2.0f, 1, 0, 1.0f);
		//soundPool.stop(soundIds[idIndex]);
	}
	public void releaseSound()
	{
		iSoundLoadCnt = 0;
		if( soundPool != null )
		{
			soundPool.release();
		}
	}
	public void clearAllBitmap()
	{
		bmpArray.clear();
	}
	public Bitmap createBitmapFromDrawableId( int id )
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		
		Bitmap ret = null;
		
		// selector�̏ꍇ�A���[�h�ł��Ȃ�
		// �l�[�~���O�K��ɂ���ē��̃v���t�B�b�N�X�͌Œ�Ƃ���
		String idString = activity.getResources().getResourceEntryName(id);
		if(idString.startsWith(SELECTOR_PREFIX))
		{
			return ret;
		}
		
		if( 0 < bmpArray.indexOfKey( id ) && bmpArray.get(id) != null )
		{
			ret = bmpArray.get(id);
		}
		else
		{
			boolean bMemErr = false;
			try {
				ret = BitmapFactory.decodeResource(activity.getResources(), id, options);
//				LogWrapper.i("test", activity.getResources().getResourcePackageName(id));
//				LogWrapper.i("test", activity.getResources().getResourceName(id));
//				LogWrapper.i("test", activity.getResources().getResourceEntryName(id));
			} catch( OutOfMemoryError ex ) {
				System.gc();
				LogWrapper.e("Out of memory occur","bitmap create");
				bMemErr = true;
				ret = null;
			}
			if( ret == null )
			{
				
				String log = String.format("%X", id);
				LogWrapper.e("decodeError",log);
				if( bMemErr == true )
					ret = BitmapFactory.decodeResource(activity.getResources(), id, options);
			}
			if( ret != null )
			{
				bmpArray.put( id, ret );
			}
		}
		return ret;
	}	
	public Drawable getResourceDrawable( int id )
	{
		Bitmap bitmap = createBitmapFromDrawableId(id);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
		return bitmapDrawable;
		//return activity.getResources().getDrawable(id);
	}
	
	public int getIntPref( String name, int def) {
        SharedPreferences prefs =
            activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(name, def);
    }
    
	public void setIntPref(String name, int value) {
        SharedPreferences prefs =
        	activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
        Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
    }
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
	
    /**
     * �A���o���̃��x�����쐬����
     * @param context
     * @param numalbums
     * @param numsongs
     * @param isUnknown
     * @return
     */
	public static String makeAlbumsLabel(Context context, int numalbums, int numsongs, boolean isUnknown)
	{
		// There are two formats for the albums/songs information:
	    // "N Song(s)"  - used for unknown artist/album
	    // "N Album(s)" - used for known albums
	    
	    StringBuilder songs_albums = new StringBuilder();
	
	    Resources r = context.getResources();
	    if (false == isUnknown) {
	    	// �A���o������ݒ�
	        String f = r.getQuantityText(R.plurals.Nalbums, numalbums).toString();
	        sFormatBuilder.setLength(0);
	        sFormatter.format(f, Integer.valueOf(numalbums));
	        songs_albums.append(sFormatBuilder);
	        if( 0 < songs_albums.length() )
	        {
	        	songs_albums.append(context.getString(R.string.albumsongseparator));
	        }
	    }
    	// �Ȑ���ݒ�
        if (numsongs == 1) {
            songs_albums.append(context.getString(R.string.onesong));
        } else {
            String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f, Integer.valueOf(numsongs));
            songs_albums.append(sFormatBuilder);
        }
	    return songs_albums.toString();
	}	
    /**
     * �A���o���̃��x�����쐬����
     * @param context
     * @param numsongs
     * @return
     */
	public static String makeNumSongsLabel(Context context, int numsongs )
	{
		// There are two formats for the albums/songs information:
	    // "N Song(s)"  - used for unknown artist/album
	    // "N Album(s)" - used for known albums
	    
	    StringBuilder songs_albums = new StringBuilder();
	
	    Resources r = context.getResources();
    	// �Ȑ���ݒ�
        if (numsongs == 1) {
            songs_albums.append(context.getString(R.string.onesong));
        } else {
            String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f, Integer.valueOf(numsongs));
            songs_albums.append(sFormatBuilder);
        }
	    return songs_albums.toString();
	}	
	/**
	 * �w�肳�ꂽ�b���̎��Ԃ��A�`���ԂƂ����\���ɕς���
	 * TODO:���̃A�v���P�[�V�����ł́A������ł̎��ԕ\���͍s��Ȃ��A���Ȃ킿�A�b��łł���A�����s�v�ɂȂ�̂ŁA�폜����
	 * ���������A���̃N���X�ɂ����̂͂�������
	 * @param context
	 * @param secs
	 * @return
	 */
	private static final Object[] sTimeArgs = new Object[5];

	public static String makeTimeString(Context context, long secs) {
		String durationformat = context.getString(
	                secs < 3600 ? R.string.durationformatshort : R.string.durationformatlong);
	        
		/* Provide multiple arguments so the format can be changed easily
		 * by modifying the xml.
		 */
	    sFormatBuilder.setLength(0);
	
	    final Object[] timeArgs = sTimeArgs;
	    timeArgs[0] = secs / 3600;
	    timeArgs[1] = secs / 60;
	    timeArgs[2] = (secs / 60) % 60;
	    timeArgs[3] = secs;
	    timeArgs[4] = secs % 60;
	
	    return sFormatter.format(durationformat, timeArgs).toString();
	}
	public String getString( int id )
	{
		return activity.getResources().getString(id);
	}
	public int getColor( int id )
	{
		return activity.getResources().getColor(id);
	}
	
	public String getQuantityString( int id, int num, Object[] args )
	{
		return activity.getResources().getQuantityString(id, num, args);
	}
	
	public boolean bReadSDcardSuccess = false;
	public boolean isReadSDCardSuccess()
	{
		return bReadSDcardSuccess;
	}
	public void setReadSDCardSuccess(boolean b)
	{
		bReadSDcardSuccess = b;
	}
	
	public boolean isSdCanRead() {
	  //SD�J�[�h�����邩�`�F�b�N
	  String status = Environment.getExternalStorageState();
	  if (!status.equals(Environment.MEDIA_MOUNTED)) {
	    return false;
	  }
	
	  File file = Environment.getExternalStorageDirectory();
	  if (file.canRead()){
	    return true;
	  }
	  return false;
	}	
}
