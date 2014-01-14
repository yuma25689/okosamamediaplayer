package okosama.app.media.ID3v2Frame;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import okosama.app.media.StreamReadUtil;

public class UserTextFrame extends TextFrame {
    protected String _Description;

    /// <summary>
    /// Create new UserTextFrameClass
    /// </summary>
    /// <param name="FrameID">4 Characters tag identifier</param>
    /// <param name="Flags">Frame Flagsr</param>
    /// <param name="Data">MemoryStream to read information from</param>
    UserTextFrame(String FrameID, byte[] Flags, InputStream Data, int Length)
    {
        super(FrameID, Flags);
//        TextEncoding = (TextEncodings)Data.ReadByte();
//        Length--;
//        if (!IsValidEnumValue(TextEncoding, ValidatingErrorTypes.ID3Error))
//        {
//            _MustDrop = true;
//            return;
//        }
        try {
			_Description = StreamReadUtil.readStreamAsString(Data, new byte[Length], Length);

        // TODO: Length‚ð‚±‚Ì‚Ü‚ÜŽg‚¤‚Ì‚Í–¾‚ç‚©‚É‚¨‚©‚µ‚¢
        
        //_Description = Data.ReadText(Length, TextEncoding, ref Length, true);

//        if (!IsUrl) // is text frame
//            Text = Data.ReadText(Length, TextEncoding);
//        else
//            Text = Data.ReadText(Length, TextEncodings.Ascii);
			setText( StreamReadUtil.readStreamAsString(Data, new byte[Length], Length) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // User URL frames use this class and use Text property as URL
        // URL property must be in AScii format
        // all URL frames start with W and text frames with T
    }

    /// <summary>
    /// Create new UserTextFrame from specific information
    /// </summary>
    /// <param name="FrameID">FrameID of frame</param>
    /// <param name="Flags">Frame flags</param>
    /// <param name="Text">Frame text</param>
    /// <param name="Description">Frame description</param>
    /// <param name="TextEncoding">TextEncoding of texts</param>
    /// <param name="Ver">Minor version of ID3v2</param>
    public UserTextFrame(String FrameID, byte[] Flags, String Text,
        String Description//,TextEncodings TextEncoding
        , int Ver)
    {
        super(FrameID, Flags);
        if (FramesInfo.IsTextFrame(FrameID, Ver) != 2)
        {
            //throw (new ArgumentException(FrameID + " is not valid for UserTextFrame class"));
            Log.e("exception"," is not valid for UserTextFrame class");
        }

        setText( Text );
        //this.TextEncoding = TextEncoding;
        setDescription( Description );
    }

    protected UserTextFrame(String FrameID, byte[] Flags)
    { 
		super(FrameID, Flags);
    }

    /// <summary>
    /// Get/Set current frame Description
    /// </summary>
    public void setDescription(String value)
    {
        if (value == null)
        {
            Log.e("setDescription","Description can't be null");
        }
        _Description = value;
    }
    public String getDescription()
    {
        return _Description;
    }

    /// <summary>
    /// Get length of current frame
    /// </summary>
//    public override int Length
//    {
//        get
//        {
//            // TextLength
//            // Description Length ( + seprator )
//            // 1: Encoding
//            int TextLen;
//
//            if (!IsUrl)
//                TextLen = GetTextLength(Text, TextEncoding, false);
//            else
//                TextLen = GetTextLength(Text, TextEncodings.Ascii, false); ;
//
//            return 1 + TextLen + GetTextLength(_Description, TextEncoding, true);
//        }
//    }
//
//    /// <summary>
//    /// Get MemoryStream to save this frame
//    /// </summary>
//    /// <returns>MemoryStream that represent current frame data</returns>
//    public override MemoryStream FrameStream(int MinorVersion)
//    {
//        MemoryStream ms = FrameHeader(MinorVersion);
//
//        if (ID3v2.AutoTextEncoding)
//            SetEncoding();
//
//        ms.WriteByte((byte)TextEncoding); // Write Encoding
//
//        WriteText(ms, _Description, TextEncoding, true);
//
//        if (!IsUrl)
//            WriteText(ms, Text, TextEncoding, false);
//        else // URL frames always use ascii encoding for text value
//            WriteText(ms, Text, TextEncodings.Ascii, false);
//
//        return ms;
//    }
//
//    /// <summary>
//    /// Indicate if this frame contain data
//    /// </summary>
//    public override bool IsAvailable
//    {
//        get
//        {
//            if ((_Description != "" || Text != "") && IsValidEnumValue(TextEncoding, ValidatingErrorTypes.Nothing))
//                return true;
//
//            return false;
//        }
//    }
//
//    private void SetEncoding()
//    {
//        if (IsAscii(Text) && IsAscii(Description))
//            TextEncoding = TextEncodings.Ascii;
//        else
//            TextEncoding = ID3v2.DefaultUnicodeEncoding;
//    }
//
//    #endregion

    @Override
    public boolean equals(Object obj)
    {
        if (false == obj instanceof UserTextFrame)
            return false;

        if (this.getFrameID() == ((UserTextFrame)obj).getFrameID()
            && this._Description == ((UserTextFrame)obj)._Description)
            return true;
        else
            return false;
    }

//    public override int GetHashCode()
//    {
//        return base.GetHashCode();
//    }

}
