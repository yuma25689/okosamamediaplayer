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
		// file���X�g���[���Ƀ��[�h
		try {
			inStream = new FileInputStream(fileName);
			
			// ID3�^�O�����t�@�C�����ǂ������ׂ�
			int iRet = haveID3(inStream);
			if ( NOT_HAVE_ID3 == iRet
			|| ERROR_PARSE == iRet )
		    {
				// ID3�������Ȃ�
				inStream.close();
		        return data;
		    }

			// ID3v2�����ꍇ�A������g��
			if( (iRet & HAVE_ID3_V2 ) == HAVE_ID3_V2 )
			{
				// ����haveID3�����s�ς݂̏ꍇ�A3�o�C�g�i�񂾂Ƃ���ɃV�[�N�ς�
				// data = new ID3v2TagData();
			}
			
			// ID3v1���������Ȃ��ꍇ�A������g��
			else if( (iRet & HAVE_ID3_V1) == HAVE_ID3_V1 )
			{
				// ����haveID3�����s�ς݂̏ꍇ�A�Ō������125�o�C�g�O�ɃV�[�N�ς�
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
			else // ID3v2�������Ă�����A�ŏ��͗�����낤�Ǝv���Ă������A
				// ID3v1�𔻒肷��ƃt�@�C���̃V�[�N������i�߂邱�Ƃ͂ł��Ȃ��j�̂ŁAID3v1���ǂ����͔��肵�Ȃ� 
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
	 * ���̊֐��̒��ŁA�t�@�C�����Ō������128�o�C�g�O�܂ŃV�[�N����̂Œ��ӂ���
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
