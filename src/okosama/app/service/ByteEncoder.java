package okosama.app.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Set;

public class ByteEncoder {
	static Set<String> encodeTargetList = null;
	// static final String DEFAULT_CHARSET_NAME = "UTF-16";
	
	public ByteEncoder()
	{
		encodeTargetList = Charset.availableCharsets().keySet();
	}
	/**
	 * 
	 * @param strRet string after encode
	 * @return null : encode failed
	 *         encodeName : encode success
	 */
	public String encode( byte[] bytes, int len, String strRet ) 
	{
		String strEncNameRet = null;
		
		try {
			// まずは、デフォルトの文字セットでエンコードする
			strRet = new String( bytes, Charset.defaultCharset().name() );
			//DEFAULT_CHARSET_NAME ); // API level 9が必要?-> Charset.defaultCharset() );
			
			// エンコードが正しいかチェックを行う
			if( isEncodeCorrect( Charset.defaultCharset().name(),
					bytes, len, strRet ) )
			{
				// 正しい場合、終了
				strEncNameRet = Charset.defaultCharset().name();
			}
			else
			{
				// 正しくない場合、全ての利用可能な文字セットでエンコードし直す
				for( String strEncName : encodeTargetList )
				{
					// エンコードする
					strRet = new String( bytes, strEncName );
					
					// エンコードが正しいかチェックを行う
					if( isEncodeCorrect( strEncName, bytes, len, strRet ) )
					{
						strEncNameRet = strEncName;
						break;
					}
				}
				// ここまで来てしまったら、エンコード失敗
				strEncNameRet = null;
				strRet = null;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			strRet = null;
			return null;
		}
		
		return strEncNameRet;
	}
	
	boolean isEncodeCorrect( String strEnc, byte[] originByte, int len, String strAfterEncode )
	{	
		// エンコード済文字列を、再度バイトに変換する
		byte[] byteTmp = strAfterEncode.getBytes( Charset.forName( strEnc ) );
		if( byteTmp.length != originByte.length )
		{
			return false;
		}
		for( int i=0; i < len && i < byteTmp.length; i++ )
		{
			if( byteTmp[i] != originByte[i] )
			{
				return false;
			}
		}
		return true;
	}
}
