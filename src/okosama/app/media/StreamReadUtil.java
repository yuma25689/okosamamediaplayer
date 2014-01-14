package okosama.app.media;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okosama.app.service.ByteEncoder;

public class StreamReadUtil {
	public static String readStreamAsString(InputStream is, byte[] buf, int len) throws IOException
	{
		String strRet = null;
		if( len == is.read(buf, 0, len ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
			
			// byte配列を、Stringに変換する
			// エンコードの取得
			ByteEncoder encoder = new ByteEncoder();
			
			//String strEncName = 
			encoder.encode(buf, len, strRet);
			
		}
		return strRet;
	}
	public static Byte readStreamAsByte(InputStream is) throws IOException
	{
		byte b[] = new byte[1];
		if( 1 == is.read(b, 0, 1 ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
		}
		return b[0];
	}
	
	public static byte[] readStreamAsByte(InputStream is, int len) throws IOException
	{
		byte[] ret = new byte[len];
		// bufferID3v1[0] = 0x00;
		if( len == is.read(ret, 0, len ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
		}
		return ret;//bufferID3v1[0];
	}
	public static int readStreamAsUInt(InputStream is) throws IOException
	{
		byte[] tempBuf = new byte[4];
		// bufferID3v1[0] = 0x00;
		char ret=0;
		if( 4 == is.read( tempBuf, 0, 4 ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
			for (int i = 0; i < tempBuf.length; i++) {
				int tmpN = tempBuf[i] & 0xFF;
				if( 0 < (tempBuf.length-i))
				{
					tmpN = tmpN << (8*(tempBuf.length-i));
				}
				ret += tmpN;
			}			
			
		}
		return ret;//bufferID3v1[0];
	}
	public static long convertByteToLong(byte[] tempBuf)
	{
		long ret = 0;
		// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
		for (int i = 0; i < tempBuf.length; i++) {
			long tmpN = tempBuf[i] & 0xFF;
			if( 0 < (tempBuf.length-i))
			{
				tmpN = tmpN << (8*(tempBuf.length-i));
			}
			ret += tmpN;
		}			
		return ret;//bufferID3v1[0];
	}
    public static void reverseByte(byte[] arr) 
    {
    	for(int i = 0; i < arr.length / 2; i++)
    	{
    	    byte temp = arr[i];
    	    arr[i] = arr[arr.length - i - 1];
    	    arr[arr.length - i - 1] = temp;    		
    	}
    	// return arr;
    }

}
