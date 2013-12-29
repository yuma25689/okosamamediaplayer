package okosama.app.audio;

import java.io.FileInputStream;
import java.io.IOException;

import android.util.Log;

public class AudioTagParser implements IAudioTagParser {

	static final String ID3v2_MARK = "ID3";
	static final String ID3v1_MARK = "TAG";
	static final int ID3v1_TAG_LEN = 128;
	FileInputStream inStream = null;
	
	@Override
	public IAudioTagData parse(String fileName) {
		IAudioTagData data = null;
		// fileをストリームにロード
		try {
			inStream = new FileInputStream(fileName);
			
			// ID3タグを持つファイルかどうか調べる
			int iRet = haveID3(inStream);
			if ( NOT_HAVE_ID3 == iRet
			|| ERROR_PARSE == iRet )
		    {
				// ID3を持たない
				inStream.close();
		        return data;
		    }

			// ID3v2を持つ場合、それを使う
			if( (iRet & HAVE_ID3_V2 ) == HAVE_ID3_V2 )
			{
				// 既にhaveID3を実行済みの場合、3バイト進んだところにシーク済み
				// data = new ID3v2TagData();
			}
			
			// ID3v1しか持たない場合、それを使う
			else if( (iRet & HAVE_ID3_V1) == HAVE_ID3_V1 )
			{
				// 既にhaveID3を実行済みの場合、最後尾から125バイト前にシーク済み
				data = new ID3v1TagData();
			}
			
			inStream.close();
		} catch (IOException e) {
		}
		return data;
	}
	
	private static final int ERROR_PARSE = -1;
	private static final int NOT_HAVE_ID3 = 0x0000;
	private static final int HAVE_ID3_V1 = 0x0001;
	private static final int HAVE_ID3_V2 = 0x0002;
	public int haveID3( FileInputStream fis )
	{
		int iRet = NOT_HAVE_ID3;
		try
		{
			if( haveID3v2( fis ) )
			{
				iRet &= HAVE_ID3_V2;
			}
			else // ID3v2を持っていたら、最初は両方やろうと思っていたが、
				// ID3v1を判定するとファイルのシークが走る（戻ることはできない）ので、ID3v1かどうかは判定しない 
			if( haveID3v1( fis ) )
			{
				iRet &= HAVE_ID3_V1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_PARSE;
		}		
		return iRet; 
	}
	
	public boolean haveID3v2( FileInputStream fis ) throws IOException
    {
        /* if the first three characters in begining of a file
         * be "ID3". that file contain ID3v2 information
         */
		String strTmp = new String();
		
		int ch;
		while ((ch = inStream.read()) != -1) {
			strTmp += Character.toChars(ch);
			if( ID3v2_MARK.length() <= strTmp.length() )
			{
				break;
			}
		}
		Log.d("haveID3v2","mark=" + strTmp);
		if( strTmp.equals(ID3v2_MARK))
		{
			return true;
		}
		return false;
    }
	/**
	 * この関数の中で、ファイルを最後尾から128バイト前までシークするので注意する
	 * @param fis
	 * @return
	 * @throws IOException
	 */
    public boolean haveID3v1( FileInputStream fis ) throws IOException
    {
    	fis.skip( fis.available() - ID3v1_TAG_LEN );
        // base.Seek(-128, SeekOrigin.End);
    	
		String strTmp = new String();    	
		int ch;    	
		while ((ch = inStream.read()) != -1) {
			strTmp += Character.toChars(ch);
			if( ID3v1_MARK.length() <= strTmp.length() )
			{
				break;
			}
		}
		Log.d("haveID3v1","mark=" + strTmp);		
		if( strTmp.equals(ID3v1_MARK))
		{
			return true;
		}
		return false;
    	
    }

}
