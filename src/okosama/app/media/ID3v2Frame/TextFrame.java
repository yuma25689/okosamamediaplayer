package okosama.app.media.ID3v2Frame;

import java.io.IOException;
import java.io.InputStream;

import okosama.app.media.StreamReadUtil;

import android.util.Log;

/// <summary>
/// The main class for any type of frame to inherit
/// </summary>
public class TextFrame extends TextOnlyFrame
{

    /*
     * Note: This class support both URL and Text frames
     * the diffrence between these two types is: URL frame don't contain
     * TextEncoding and always use Ascii as Encoding but TextFrames contain
     * URLs start with 'W' texts with 'T'
     */
    // private TextEncodings _TextEncoding;

    /// <summary>
    /// Create new TextFrame Class
    /// </summary>
    /// <param name="FrameID">4 Characters frame identifier</param>
    /// <param name="Flags">Flag of frame</param>
    /// <param name="Data">FileStream to read frame data from</param>
    /// <param name="Length">Maximum length of frame</param>
    TextFrame(String FrameID, byte[] Flags, InputStream Data, int Length)
    {
    	super(FrameID, Flags);
        // If it was URL frame the TextEncoding is ascii and must not read
//        if (IsUrl())
//        {
//            TextEncoding = TextEncodings.Ascii;
//        }
//        else
//        {
//            TextEncoding = (TextEncodings)Data.ReadByte();
//            Length--;
//            if (!IsValidEnumValue(TextEncoding, ValidatingErrorTypes.ID3Error))
//                return;
//        }
    	// å≥ÇÕURLÇ©Ç«Ç§Ç©Çå©ÇƒÇ¢ÇΩÇ™ÅAÇ∆ÇËÇ†Ç¶Ç∏å©Ç»Ç¢
        try {
			setText( StreamReadUtil.readStreamAsString(Data, new byte[Length], Length) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /// <summary>
    /// Create new TextFrame with specific information
    /// </summary>
    /// <param name="Text">Text of TextFrame</param>
    /// <param name="TextEncoding">TextEncoding of TextFrame</param>
    /// <param name="FrameID">FrameID of TextFrame</param>
    /// <param name="Flags">Flags of Frame</param>
    public TextFrame(String FrameID, byte[] Flags, String Text, //TextEncodings TextEncoding,
        int Ver)
    {
    	super(FrameID, Flags);
        if (FramesInfo.IsTextFrame(FrameID, Ver) != 1)
        {
            // throw (new ArgumentException(FrameID + " is not valid TextFrame FrameID"));
        	Log.e("construct error",FrameID + " is not valid TextFrame FrameID");
        	return;
        }

        setText( Text );
    }

    protected TextFrame(String FrameID, byte[] Flags)
    {
    	super(FrameID, Flags);
    }

    /// <summary>
    /// Get or Set current frame TextEncoding
    /// </summary>
//    public TextEncodings TextEncoding
//    {
//        get
//        { return _TextEncoding; }
//        set
//        {
//            if (IsValidEnumValue(value, ValidatingErrorTypes.Exception))
//                _TextEncoding = value;
//        }
//    }

    /// <summary>
    /// Get MemoryStream of frame data
    /// </summary>
    /// <returns>MemoryStream of current frame</returns>
//    public override MemoryStream FrameStream(int MinorVersion)
//    {
//        MemoryStream ms = FrameHeader(MinorVersion);
//
//        if (!IsUrl)
//        {
//            if (ID3v2.AutoTextEncoding)
//                SetEncoding();
//
//            ms.WriteByte((byte)_TextEncoding); // Write Text Encoding
//            WriteText(ms, Text, _TextEncoding, false); // Write Text
//        }
//        else
//            WriteText(ms, Text, TextEncodings.Ascii, false);
//
//        return ms;
//    }
//
//    /// <summary>
//    /// Get length of current frame
//    /// </summary>
//    public override int Length
//    {
//        get
//        {
//            // 1: Encoding
//            // TextLength ( Ascii Or Unicode )
//            // this frame don't contain text seprator
//            return (1 + GetTextLength(Text, _TextEncoding, false));
//        }
//    }

    /// <summary>
    /// Indicate if this frame have usefull data (Text:null;Empty) (TextEncoding:Valid)
    /// </summary>
//    public override bool IsAvailable
//    {
//        get
//        {
//            // if TextEncoding and Text value is valid this frame is valid
//            // otherwise not
//            if (!IsValidEnumValue(_TextEncoding, ValidatingErrorTypes.Nothing) ||
//                Text == null || Text == "")
//                return false;
//            else
//                return true;
//        }
//    }

    /// <summary>
    /// Set TextEncoding according to Data of current frame
    /// </summary>
//    private void SetEncoding()
//    {
//        if (IsAscii(Text))
//            TextEncoding = TextEncodings.Ascii;
//        else
//            TextEncoding = ID3v2.DefaultUnicodeEncoding;
//    }
//
//    #endregion

    @Override
    public  boolean equals(Object obj)
    {
        if (false == obj instanceof TextFrame)
            return false;

        // if FrameID of two text frames were equal they are equal
        // ( the text is not important )
        return (this.getFrameID() == ((TextFrame)obj).getFrameID());
    }

//    public override int GetHashCode()
//    {
//        return base.GetHashCode();
//    }

    /// <summary>
    /// Indicate if current frame contain URL information
    /// </summary>
    protected boolean IsUrl()
    {
        // first character of URL frames always is 'W'
        return (getFrameID().charAt(0) == 'W');
    }


}
