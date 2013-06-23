/**
 * 
 */
package okosama.app;

//import java.util.HashMap;
//import java.util.Map;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * ���\�[�X�ɃA�N�Z�X���邽�߂̃N���X
 * dalvik�̐���(�����炭�A24MB�`48MB���炢�̃������m�ۂŗ�����)
 * ��������邽�߂ɁA�Ȃ�ׂ�Drawable��20MB�ȏ�ɂȂ�Ȃ��悤�ɂ���
 * (1��ʂŎg��Drawable�̃T�C�Y������𒴂��Ă��܂��A�Ȃǂ̂ǂ����悤���Ȃ��ꍇ�͏���)
 * @author 25689
 *
 */
public final class ResourceAccessor {
	
	
	
//	// ����̂��߂ɁADrawable��ێ�����}�b�v
//	// �}�b�v�̃L�[�ɂ́A�摜�𗘗p����Tabpage��ID�𗘗p����
//	// NONE�̏ꍇ�A�e��ʋ��ʃ��\�[�X�Ƃ��A������Ȃ��悤�ɂ���
//	HashMap<Integer,Drawable> drawableMap = new HashMap<Integer,Drawable>();
//	
//	public void clearDrawable()
//	{
//		for( Map.Entry<Integer,Drawable> e : drawableMap.entrySet() )
//		{
//			e.getValue().
//		}
//	}
	
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
		if( instance == null ) {
			instance = new ResourceAccessor( activity );
		}
		else
		{
			instance.setActivity(activity);
		}
	}
	public static ResourceAccessor getInstance() {
		return instance;
	}
	public Bitmap createBitmapFromDrawableId( int id )
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		return BitmapFactory.decodeResource(activity.getResources(), id, options);
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
	    if (isUnknown) {
	    	// �����炭�A�A���o����������Ȃ��ꍇ
	    	// �Ȑ���ݒ�
	        if (numsongs == 1) {
	            songs_albums.append(context.getString(R.string.onesong));
	        } else {
	            String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
	            sFormatBuilder.setLength(0);
	            sFormatter.format(f, Integer.valueOf(numsongs));
	            songs_albums.append(sFormatBuilder);
	        }
	    } else {
	    	// �A���o������ݒ�
	        String f = r.getQuantityText(R.plurals.Nalbums, numalbums).toString();
	        sFormatBuilder.setLength(0);
	        sFormatter.format(f, Integer.valueOf(numalbums));
	        songs_albums.append(sFormatBuilder);
	        songs_albums.append(context.getString(R.string.albumsongseparator));
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
	
	public String getQuantityString( int id, int num, Object[] args )
	{
		return activity.getResources().getQuantityString(id, num, args);
	}
}
