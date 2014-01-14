package okosama.app.media.ID3v2Frame;

public abstract class TextOnlyFrame extends Frame
{
    private String _Text; // Contain text of current frame

    protected TextOnlyFrame(String FrameID, byte[] Flags)
    {
    	super(FrameID, Flags);
    }

    /// <summary>
    /// Get or Set current TextOnlyFrame text
    /// </summary>
    public String getText()
    {
        return _Text;
    }
    public void setText( String value )
    {
    	_Text = value;
    }

    /// <summary>
    /// Indicate if this frame have usefull data (Text!=null,Empty)
    /// </summary>
//    @Override
//    public boolean isAvailable()
//    {
//        if (_Text == null || _Text == "")
//            return false;
//        else
//            return true;
//    }

    /// <summary>
    /// Get stream to save current Frame
    /// </summary>
    /// <returns>Bytes for saving this frame</returns>
//    public override MemoryStream FrameStream(int MinorVersion)
//    {
//        MemoryStream ms = FrameHeader(MinorVersion);
//
//        WriteText(ms, _Text, TextEncodings.Ascii, false);
//
//        return ms;
//    }

    /// <summary>
    /// Get length of current frame
    /// </summary>
//    @Override
//    public int Length()
//    {
//        // in Ascii Encoding each character is one byte
//        return _Text.length();
//    }

}
