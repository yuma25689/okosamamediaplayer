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
			// �܂��́A�f�t�H���g�̕����Z�b�g�ŃG���R�[�h����
			strRet = new String( bytes, Charset.defaultCharset().name() );
			//DEFAULT_CHARSET_NAME ); // API level 9���K�v?-> Charset.defaultCharset() );
			
			// �G���R�[�h�����������`�F�b�N���s��
			if( isEncodeCorrect( Charset.defaultCharset().name(),
					bytes, len, strRet ) )
			{
				// �������ꍇ�A�I��
				strEncNameRet = Charset.defaultCharset().name();
			}
			else
			{
				// �������Ȃ��ꍇ�A�S�Ă̗��p�\�ȕ����Z�b�g�ŃG���R�[�h������
				for( String strEncName : encodeTargetList )
				{
					// �G���R�[�h����
					strRet = new String( bytes, strEncName );
					
					// �G���R�[�h�����������`�F�b�N���s��
					if( isEncodeCorrect( strEncName, bytes, len, strRet ) )
					{
						strEncNameRet = strEncName;
						break;
					}
				}
				// �����܂ŗ��Ă��܂�����A�G���R�[�h���s
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
		// �G���R�[�h�ϕ�������A�ēx�o�C�g�ɕϊ�����
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
