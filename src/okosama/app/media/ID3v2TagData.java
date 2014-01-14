package okosama.app.media;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.util.Log;

import okosama.app.media.ID3v2Frame.FramesCollection;
import okosama.app.media.ID3v2Frame.FramesInfo;
import okosama.app.media.ID3v2Frame.TextFrame;
import okosama.app.media.ID3v2Frame.UserTextFrame;
import okosama.app.service.ByteEncoder;

/**
 * @author 25689
 *
 */
public class ID3v2TagData implements IAudioTagData {
//	private String filePath;
//	private String title;
//	private String artist;
//	private String album;
//	private String year;
//	private String comment;
//	private byte trackNumber;
//	private byte genre;
	
	// ID3v2 バージョン      $03 00
	private byte[] version;
	final int VERSION_MAJOR_CODE_INDEX = 0;
	final int VERSION_MINOR_CODE_INDEX = 1;
	// ID3v2 フラグ %abc00000
	private byte[] flag;
	// ID3v2 サイズ          4 * %0xxxxxxx
	private byte[] sizeExpressionBy4Byte;
	private int size = 0; 
	
    private String _FilePath; // ID3 file path
//    private FilterCollection _Filter; // Contain Filter Frames
//    private FilterTypes _FilterType; //Indicate wich filter type use
//    private ID3v2Flags _Flags;
    // private boolean _LoadLinkedFrames; // Indicate load Link frames when loading ID3 or not
    // private boolean _DropUnknown; // trueなら、分からないフレームは取得しない
    // private Version _ver; // ID3 version
    // private boolean _HaveTag; // Indicate if current file have ID3v2 Info
    // private ErrorCollection _Errors; // Contain Errors that occured
    // private static TextEncodings _DefaultUnicodeEncoding = TextEncodings.UTF_16; 
    // when use AutoTextEncoding which unicode type must use
    // private static boolean _AutoTextEncoding = true; 
    // when want to save frame use automatic text encoding
    // フレーム格納時に、自動でエンコードするかどうか

    
    // エラー情報
    ArrayList<ID3Error> errors = new ArrayList<ID3Error>();
    private void AddError(ID3Error Error)
    {
    	errors.add(Error);
    }
    
    // Frames that can be more than one
    private FramesCollection<TextFrame> _TextFrames;
    private FramesCollection<UserTextFrame> _UserTextFrames;
    private FramesCollection<PrivateFrame> _PrivateFrames;
    private FramesCollection<TextWithLanguageFrame> _TextWithLangFrames;
    private FramesCollection<SynchronisedText> _SynchronisedTextFrames;
    private FramesCollection<AttachedPictureFrame> _AttachedPictureFrames;
    private FramesCollection<GeneralFileFrame> _EncapsulatedObjectFrames;
    private FramesCollection<PopularimeterFrame> _PopularimeterFrames;
    private FramesCollection<AudioEncryptionFrame> _AudioEncryptionFrames;
    private FramesCollection<LinkFrame> _LinkFrames;
    private FramesCollection<TermOfUseFrame> _TermOfUseFrames;
    private FramesCollection<DataWithSymbolFrame> _DataWithSymbolFrames;
    private FramesCollection<BinaryFrame> _UnknownFrames;

    // Frames that can't repeat
    private BinaryFrame _MCDIFrame;
    private SynchronisedTempoFrame _SYTCFrame; // Synchronised tempo codes        
    private PlayCounterFrame _PCNTFrame; // Play Counter        
    private RecomendedBufferSizeFrame _RBUFFrame;
    private OwnershipFrame _OWNEFrame; // Owner ship
    private CommercialFrame _COMRFrame;
    private ReverbFrame _RVRBFrame;
    private Equalisation _EQUAFrame;
    private RelativeVolumeFrame _RVADFrame;
    private EventTimingCodeFrame _ETCOFrame;
    private PositionSynchronisedFrame _POSSFrame;
	
	
	public static final int ID3_V2_VERSION_BYTE_COUNT = 2;
	public static final int ID3_V2_FLAG_BYTE_COUNT = 1;
	public static final int ID3_V2_SIZE_BYTE_COUNT = 4;
		
	public String readStreamAsString(InputStream is, int len) throws IOException
	{
		byte buffer[] = new byte[len];
		String strRet = null;
		if( len == is.read(buffer, 0, len ) )
		{
			// 読み込まれた長さがlenと等しい場合のみ、読み込み成功
			
			// byte配列を、Stringに変換する
			// エンコードの取得
			ByteEncoder encoder = new ByteEncoder();
			
			//String strEncName = 
			encoder.encode(buffer, len, strRet);
			
		}
		return strRet;
	}
	
	/**
	 * このメソッドの場所はあまり正しくないかもしれない
	 * @param fis
	 */
	@Override
	public boolean Load( FileInputStream fis )
	{		
		try 
		{
			// ヘッダのロード
			version = StreamReadUtil.readStreamAsByte( fis, ID3_V2_VERSION_BYTE_COUNT );
			flag = StreamReadUtil.readStreamAsByte( fis, ID3_V2_FLAG_BYTE_COUNT );
			sizeExpressionBy4Byte = StreamReadUtil.readStreamAsByte( fis, ID3_V2_SIZE_BYTE_COUNT );

			// 拡張ヘッダのロード
			// 現状、CRCくらいしか格納していないので、不要
			
			// フレームのロード
			// フレームヘッダ(10byte)
			// Frame ID　$xx xx xx xx (four characters)
			// Size      $xx xx xx xx
			// Flags     $xx xx
			readFrames( fis, fis.available() );
			
		} catch( IOException ex )
		{
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	static final int FRAME_FLAG_LEN = 2;
	static final int MARK_BUFFER_SIZE = 8192;	
    /// <summary>
    /// Read all frames from specific FileStream
    /// </summary>
    /// <param name="Data">FileStream to read data from</param>
    /// <param name="Length">Length of data to read from FileStream</param>
    private boolean readFrames(FileInputStream fis_, int Length)
    {
        String FrameID;
        int FrameLength;
        byte[] frameFlags = new byte[FRAME_FLAG_LEN];
        // FrameFlags Flags = new FrameFlags();
        // byte Buf;
        // If ID3v2 is ID3v2.2 FrameID, FrameLength of Frames is 3 byte
        // otherwise it's 4 character
        // いきなりアクセスしているので、少し危険かもしれない
        int FrameIDLen = version[VERSION_MINOR_CODE_INDEX] == 2 ? 3 : 4;

        // Minimum frame size is 10 because frame header is 10 byte
        while (Length > 10)
        {
        	// check for padding( 00 bytes )
            // Buf = fis.read()
        	BufferedInputStream bis = new BufferedInputStream( fis_ );
        	bis.mark(MARK_BUFFER_SIZE);
        	long nPosFromMark = 0;
            if (bis.read() == 0)
            {
                Length--;
                nPosFromMark++;
                if( MARK_BUFFER_SIZE <= nPosFromMark )
                {
                	bis.mark( MARK_BUFFER_SIZE );
                	nPosFromMark = 0;
                }
                continue;
            }

            // if readed byte is not zero. it must read as FrameID
            bis.reset();
            bis.skip(nPosFromMark);

            // ---------- Read Frame Header -----------------------
            FrameID = readStreamAsString(bis, FrameIDLen);//, TextEncodings.Ascii);
            if (FrameIDLen == 3)
            {
                FrameID = FramesInfo.Get4CharID(FrameID);
            }
            
            // FrameSize
            FrameLength = StreamReadUtil.readStreamAsUInt(bis);
            if (FrameIDLen == 4)
            {
            	frameFlags = StreamReadUtil.readStreamAsByte(bis, FRAME_FLAG_LEN );
                // Flags = (FrameFlags)fis.ReadUInt(2);
            }
            else
            {
                // Flags = 0; // must set to default flag
            }

            long Position = bis.available();//fis.Position;

            if (Length > 0x10000000)
            {
            	//throw (new FileLoadException("This file contain frame that have more than 256MB data"));
            	Log.e("readFrames","This file contain frame that have more than 256MB data");
            	return false;
            }
            boolean Added = false;
            if (IsAddable(FrameID)) // Check if frame is not filter
                Added = AddFrame(bis, FrameID, FrameLength, frameFlags);

            if (!Added)
                // if don't read this frame
                // we must go forward to read next frame
                bis.skip( Position + FrameLength );

            Length -= FrameLength + 10;
        }
    }
    /// <summary>
    /// Indicate can add specific frame according to Filter
    /// </summary>
    /// <param name="FrameID">FrameID to check</param>
    /// <returns>true if can add otherwise false</returns>
    private boolean IsAddable(String FrameID)
    {
    	return true;
//        if (_FilterType == FilterTypes.NoFilter)
//            return true;
//        else if (_FilterType == FilterTypes.LoadFiltersOnly)
//            return _Filter.IsExists(FrameID);
//        else // Not Load Filters
//            return !_Filter.IsExists(FrameID);
    }
    /// <summary>
    /// Add Frame information to where it must store
    /// </summary>
    /// <param name="Data">FileStream contain Frame</param>
    /// <param name="FrameID">FrameID of frame</param>
    /// <param name="Length">Maximum available length to read</param>
    /// <param name="Flags">Flags of frame</param>
    private boolean AddFrame(InputStream is, String FrameID, int Length, byte[] Flags)
    {
        // NOTE: All FrameIDs must be capital letters
    	// フレームIDが妥当なものかどうか調べる
        if (!FramesInfo.IsValidFrameID(FrameID))
        {
            AddError(new ID3Error("nonValid Frame found and dropped", FrameID));
            return false;
        }

		// テキストフレームかどうか調べる
        int IsText = FramesInfo.IsTextFrame(FrameID, version[VERSION_MINOR_CODE_INDEX]);
        if (IsText == 1)
        {
			// テキストフレームを作成
            TextFrame TempTextFrame = new TextFrame(FrameID, Flags, Data, Length);
            if (TempTextFrame.IsReadableFrame)
            {
                _TextFrames.Add(TempTextFrame);
                return true;
            }
            return false;
        }

        if (IsText == 2)
        {
            UserTextFrame TempUserTextFrame = new UserTextFrame(FrameID, Flags, Data, Length);
            if (TempUserTextFrame.IsReadableFrame)
            {
                _UserTextFrames.Add(TempUserTextFrame);
                return true;
            }
            return false;
        }

        switch (FrameID)
        {
            case "UFID":
            case "PRIV":
                PrivateFrame TempPrivateFrame = new PrivateFrame(FrameID, Flags, Data, Length);
                if (TempPrivateFrame.IsReadableFrame)
                {
                    _PrivateFrames.Add(TempPrivateFrame); return true;
                }
                else
                    AddError(new ID3Error(TempPrivateFrame.ErrorMessage, FrameID));
                break;
            case "USLT":
            case "COMM":
                TextWithLanguageFrame TempTextWithLangFrame = new TextWithLanguageFrame(FrameID, Flags, Data, Length);
                if (TempTextWithLangFrame.IsReadableFrame)
                { _TextWithLangFrames.Add(TempTextWithLangFrame); return true; }
                else
                    AddError(new ID3Error(TempTextWithLangFrame.ErrorMessage, FrameID));
                break;
            case "SYLT":
                SynchronisedText TempSynchronisedText = new SynchronisedText(FrameID, Flags, Data, Length);
                if (TempSynchronisedText.IsReadableFrame)
                { _SynchronisedTextFrames.Add(TempSynchronisedText); return true; }
                else
                    AddError(new ID3Error(TempSynchronisedText.ErrorMessage, FrameID));
                break;
            case "GEOB":
                GeneralFileFrame TempGeneralFileFrame = new GeneralFileFrame(FrameID, Flags, Data, Length);
                if (TempGeneralFileFrame.IsReadableFrame)
                { _EncapsulatedObjectFrames.Add(TempGeneralFileFrame); return true; }
                else
                    AddError(new ID3Error(TempGeneralFileFrame.ErrorMessage, FrameID));
                break;
            case "POPM":
                PopularimeterFrame TempPopularimeterFrame = new PopularimeterFrame(FrameID, Flags, Data, Length);
                if (TempPopularimeterFrame.IsReadableFrame)
                { _PopularimeterFrames.Add(TempPopularimeterFrame); return true; }
                else
                    AddError(new ID3Error(TempPopularimeterFrame.ErrorMessage, FrameID));
                break;
            case "AENC":
                AudioEncryptionFrame TempAudioEncryptionFrame = new AudioEncryptionFrame(FrameID, Flags, Data, Length);
                if (TempAudioEncryptionFrame.IsReadableFrame)
                { _AudioEncryptionFrames.Add(TempAudioEncryptionFrame); return true; }
                else
                    AddError(new ID3Error(TempAudioEncryptionFrame.ErrorMessage, FrameID));
                break;
            case "USER":
                TermOfUseFrame TempTermOfUseFrame = new TermOfUseFrame(FrameID, Flags, Data, Length);
                if (TempTermOfUseFrame.IsReadableFrame)
                { _TermOfUseFrames.Add(TempTermOfUseFrame); return true; }
                else
                    AddError(new ID3Error(TempTermOfUseFrame.ErrorMessage, FrameID));
                break;
            case "ENCR":
            case "GRID":
                DataWithSymbolFrame TempDataWithSymbolFrame = new DataWithSymbolFrame(FrameID, Flags, Data, Length);
                if (TempDataWithSymbolFrame.IsReadableFrame)
                { _DataWithSymbolFrames.Add(TempDataWithSymbolFrame); return true; }
                else
                    AddError(new ID3Error(TempDataWithSymbolFrame.ErrorMessage, FrameID));
                break;
            case "LINK":
                LinkFrame LF = new LinkFrame(FrameID, Flags, Data, Length);
                if (LF.IsReadableFrame)
                {
                    _LinkFrames.Add(LF);
                    if (_LoadLinkedFrames)
                    { LoadFrameFromFile(LF.FrameIdentifier, LF.URL); return true; }
                }
                else
                    AddError(new ID3Error(LF.ErrorMessage, FrameID));
                break;
            case "APIC":
                AttachedPictureFrame TempAttachedPictureFrame = new AttachedPictureFrame(FrameID, Flags, Data, Length);
                if (TempAttachedPictureFrame.IsReadableFrame)
                { _AttachedPictureFrames.Add(TempAttachedPictureFrame); return true; }
                else
                    AddError(new ID3Error(TempAttachedPictureFrame.ErrorMessage, FrameID));
                break;
            case "MCDI":
                BinaryFrame MCDI = new BinaryFrame(FrameID, Flags, Data, Length);
                if (MCDI.IsReadableFrame)
                { _MCDIFrame = MCDI; return true; }
                else
                    AddError(new ID3Error(MCDI.ErrorMessage, FrameID));
                break;
            case "SYTC":
                SynchronisedTempoFrame SYTC = new SynchronisedTempoFrame(FrameID, Flags, Data, Length);
                if (SYTC.IsReadableFrame)
                { _SYTCFrame = SYTC; return true; }
                else
                    AddError(new ID3Error(SYTC.ErrorMessage, FrameID));
                break;
            case "PCNT":
                PlayCounterFrame PCNT = new PlayCounterFrame(FrameID, Flags, Data, Length);
                if (PCNT.IsReadableFrame)
                { _PCNTFrame = PCNT; return true; }
                else
                    AddError(new ID3Error(PCNT.ErrorMessage, FrameID));
                break;
            case "RBUF":
                RecomendedBufferSizeFrame RBUF = new RecomendedBufferSizeFrame(FrameID, Flags, Data, Length);
                if (RBUF.IsReadableFrame)
                { _RBUFFrame = RBUF; return true; }
                else
                    AddError(new ID3Error(RBUF.ErrorMessage, FrameID));
                break;
            case "OWNE":
                OwnershipFrame OWNE = new OwnershipFrame(FrameID, Flags, Data, Length);
                if (OWNE.IsReadableFrame)
                { _OWNEFrame = OWNE; return true; }
                else
                    AddError(new ID3Error(OWNE.ErrorMessage, FrameID));
                break;
            case "COMR":
                CommercialFrame COMR = new CommercialFrame(FrameID, Flags, Data, Length);
                if (COMR.IsReadableFrame)
                { _COMRFrame = COMR; return true; }
                else
                    AddError(new ID3Error(COMR.ErrorMessage, FrameID));
                break;
            case "RVRB":
                ReverbFrame RVRB = new ReverbFrame(FrameID, Flags, Data, Length);
                if (RVRB.IsReadableFrame)
                { _RVRBFrame = RVRB; return true; }
                else
                    AddError(new ID3Error(RVRB.ErrorMessage, FrameID));
                break;
            case "EQUA":
                Equalisation EQUA = new Equalisation(FrameID, Flags, Data, Length);
                if (EQUA.IsReadableFrame)
                { _EQUAFrame = EQUA; return true; }
                else
                    AddError(new ID3Error(EQUA.ErrorMessage, FrameID));
                break;
            case "RVAD":
                RelativeVolumeFrame RVAD = new RelativeVolumeFrame(FrameID, Flags, Data, Length);
                if (RVAD.IsReadableFrame)
                { _RVADFrame = RVAD; return true; }
                else
                    AddError(new ID3Error(RVAD.ErrorMessage, FrameID));
                break;
            case "ETCO":
                EventTimingCodeFrame ETCO = new EventTimingCodeFrame(FrameID, Flags, Data, Length);
                if (ETCO.IsReadableFrame)
                { _ETCOFrame = ETCO; return true; }
                else
                    AddError(new ID3Error(ETCO.ErrorMessage, FrameID));
                break;
            case "POSS":
                PositionSynchronisedFrame POSS = new PositionSynchronisedFrame(FrameID, Flags, Data, Length);
                if (POSS.IsReadableFrame)
                { _POSSFrame = POSS; return true; }
                else
                    AddError(new ID3Error(POSS.ErrorMessage, FrameID));
                break;
            default:
                BinaryFrame Temp = new BinaryFrame(FrameID, Flags, Data, Length);
                if (Temp.IsReadableFrame)
                { _UnknownFrames.Add(Temp); return true; }
                else
                    AddError(new ID3Error(Temp.ErrorMessage, FrameID));
                break;
            // TODO: Mpeg Location
        }

        return false;
    }
	
}
