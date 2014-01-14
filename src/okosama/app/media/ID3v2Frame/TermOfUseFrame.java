package okosama.app.media.ID3v2Frame;

import java.io.InputStream;

import okosama.app.media.StreamReadUtil;

public class TermOfUseFrame extends TextFrame {
    protected Language _Language;

    /// <summary>
    /// Create new TermOfUseFrame class
    /// </summary>
    /// <param name="FrameID">4 Characters tag identifier</param>
    /// <param name="Flags">2 Bytes flags identifier</param>
    /// <param name="Data">Contain Data for this frame</param>
    TermOfUseFrame(String FrameID, byte[] Flags, InputStream Data, int Length)
    {
        super(FrameID, Flags)
        //TextEncoding = (TextEncodings)Data.ReadByte();
        // NOTICE:未使用
        byte enc = StreamReadUtil.readStreamAsByte(Data);
        Length--;
//        if (!IsValidEnumValue(TextEncoding, ValidatingErrorTypes.ID3Error))
//        {
//            _MustDrop = true;
//            return;
//        }

        // TODO: 実装確認
        _Language = new Language(Data);
        Length -= 3;

        setText( StreamReadUtil.readStreamAsString(Data, new byte[Length], Length));
        
        //Text = Data.ReadText(Length, TextEncoding);
    }

    public TermOfUseFrame(byte[] Flags, String Text//,
        //TextEncodings TextEncoding
        , String Lang)
    {
        super("USER", Flags);
        setText( Text );
        // this.TextEncoding = TextEncoding;
        Language = new Language(Lang);
    }

    protected TermOfUseFrame(String FrameID, byte[] Flags)
//        : base(FrameID, Flags) 
    { 
        super(FrameID, Flags);
    }

    /// <summary>
    /// Gets or sets language of current frame
    /// </summary>
    public Language getLanguage()
    {
    	return _Language;
    }
    public void setLanguage(Language value)
    {
        _Language = value;
    }

//    /// <summary>
//    /// Get Length of current frame
//    /// </summary>
//    public override int Length
//    {
//        get
//        {
//            // 3: Language Length
//            return (base.Length + 3);
//        }
//    }
//
//    /// <summary>
//    /// Get bytes for saving this frame
//    /// </summary>
//    /// <returns>Bytes for saving this frame</returns>
//    public override MemoryStream FrameStream(int MinorVersion)
//    {
//        MemoryStream ms = FrameHeader(MinorVersion);
//
//        if (ID3v2.AutoTextEncoding)
//            SetEncoding();
//
//        ms.WriteByte((byte)TextEncoding); // Write Text Encoding
//
//        _Language.Write(ms);
//
//        WriteText(ms, Text, TextEncoding, false);
//
//        return ms;
//    }
//
//    /// <summary>
//    /// Indicate if current frame is available
//    /// </summary>
//    public override bool IsAvailable
//    {
//        get
//        {
//            if (base.IsAvailable == false)
//                return false;
//
//            return _Language.IsValidLanguage;
//        }
//    }
//
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
    public boolean Equals(Object obj)
    {
        if (false == obj instanceof TermOfUseFrame )
            return false;

        if (((TermOfUseFrame)obj).getLanguage() == this.getLanguage() &&
            ((TermOfUseFrame)obj).getFrameID() == this.getFrameID())
            return true;
        else
            return false;
    }

//    public override int GetHashCode()
//    {
//        return base.GetHashCode();
//    }

    @Override
    public String toString()
    {
        return "Term of use [" + _Language + "]";
    }

}
