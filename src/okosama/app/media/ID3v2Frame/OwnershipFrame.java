package okosama.app.media.ID3v2Frame;

public class OwnershipFrame {
    // Inherits:
    //      Text
    //      Encoding
    private Price _Price;
    private SDate _DateOfPurch;

    /// <summary>
    /// Create new OwnershipFrame
    /// </summary>
    /// <param name="FrameID">4 Characters tag identifier</param>
    /// <param name="Flags">2 Bytes flags identifier</param>
    /// <param name="Data">Contain Data for this frame</param>
    OwnershipFrame(String FrameID, byte[] Flags, FileStreamEx Data, int Length)
    {
        super(FrameID, Flags);
        TextEncoding = (TextEncodings)Data.ReadByte();
        Length--;
        if (!IsValidEnumValue(TextEncoding, ValidatingErrorTypes.ID3Error))
            return;

        _Price = new Price(Data, Length);
        Length -= _Price.Length;
        if (!_Price.IsValid)
        {
            ErrorOccur("Price is not valid value. ownership frame will not read", true);
            return;
        }

        if (Length >= 8)
        {
            _DateOfPurch = new SDate(Data);
            Length -= 8;
        }
        else
        {
            ErrorOccur("Date is not valid for this frame", true);
            return;
        }

        Seller = Data.ReadText(Length, TextEncoding);
    }

    public OwnershipFrame(FrameFlags Flags, Price PricePayed, SDate PurchDate,
        String Seller, TextEncodings TEncoding)
        : base("OWNE", Flags)
    {
        _Price = PricePayed;
        _DateOfPurch = PurchDate;
        this.Seller = Seller;
    }

    /// <summary>
    /// Get/Set DateOfPurch for current frame
    /// </summary>
    public SDate DateOfPurch
    {
        get
        { return _DateOfPurch; }
        set
        { _DateOfPurch = value; }
    }

    /// <summary>
    /// Get price of current frame
    /// </summary>
    public Price Price
    {
        get
        {
            return _Price;
        }
    }

//    #region -> Override method and properties <-
//
//    /// <summary>
//    /// Get Length of current frame
//    /// </summary>
//    public override int Length
//    {
//        get
//        {
//            // base.Length: 10(Header) + 1(Encoding) + Text.Length(According to encoding)
//            // Price.Length + 8(Date) + 1(Seprator of Price)
//            return (base.Length + _Price.Length) + 9;
//        }
//    }
//
//    /// <summary>
//    /// Get MemoryStream to save current frame
//    /// </summary>
//    /// <returns>MemoryStream that represent current frame data</returns>
//    public override MemoryStream FrameStream(int MinorVersion)
//    {
//        MemoryStream ms = FrameHeader(MinorVersion);
//
//        if (ID3v2.AutoTextEncoding)
//            SetEncoding();
//
//        ms.WriteByte((byte)TextEncoding); // Write Text Encoding
//
//        WriteText(ms, _Price.ToString(), TextEncodings.Ascii, true);
//
//        WriteText(ms, _DateOfPurch.String, TextEncodings.Ascii, false);
//
//        WriteText(ms, Seller, TextEncoding, false);
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
//            if (_DateOfPurch == null && _Price == null)
//                return base.IsAvailable;
//
//            return false;
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

    /// <summary>
    /// This property is not available for Ownership
    /// </summary>
//    public new String Text
//    {
//        get
//        {
//            throw (new InvalidOperationException("This property not available for Ownership"));
//        }
//    }

    /// <summary>
    /// Get/Set Current frame seller
    /// </summary>
    public String Seller
    {
        get
        { return base.Text; }
        set
        {
            base.Text = value;
            // Base.Text control the value for null
        }
    }

}
