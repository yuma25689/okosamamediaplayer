package okosama.app.media.ID3v2Frame;

import android.util.Log;

/// <summary>
/// The main class for any type of frame to inherit
/// </summary>
public abstract class Frame {
    public class FrameFlags
    {
        public static final int TagAlterPreservation = 0x8000;
        public static final int FileAlterPreservation = 0x4000;
        public static final int ReadOnly = 0x2000;
        public static final int Compression = 0x0080;
        public static final int Encryption = 0x0040;
        public static final int GroupingIdentity = 0x0020;
    }    	
    protected class ValidatingErrorTypes
    {
        public static final int Nothing = 0;
        public static final int ID3Error = 1;
        public static final int Exception = 2;
    }    
    
    private String _FrameID; // Contain FrameID of current Frame
    private byte[] frameFlagsOfByte = new byte[2]; // Contain Flags of current frame
    private int frameFlags = 0;
    // After reading frame if must drop value were true it means frame is not readable
    protected boolean _MustDrop;
    private boolean _IsLinked; // indicate is current frame a linked frame or not
    private String _ErrorMessage; // Contain Error Message if occur

    /// <summary>
    /// Create a new Frame class
    /// </summary>
    /// <param name="FrameID">4 Characters tag identifier</param>
    /// <param name="Flag">Frame Falgs</param>
    protected Frame(String FrameID, byte[] Flags)
    {
        // All FrameID letters must be capital
        FrameID = FrameID.toUpperCase();

        if (!ValidatingFrameID(FrameID, ValidatingErrorTypes.Exception))
        {
            _MustDrop = true;
            return;
        }

        frameFlagsOfByte = Flags;
        frameFlags = 0;
		for (int i = 0; i < frameFlagsOfByte.length; i++) {
			int tmpN = frameFlagsOfByte[i] & 0xFF;
			if( 0 < (frameFlagsOfByte.length-i))
			{
				tmpN = tmpN << (8*(frameFlagsOfByte.length-i));
			}
			frameFlags += tmpN;
		}			
        
        _FrameID = FrameID;
        _MustDrop = false;
        _IsLinked = false;
    }


    /// <summary>
    /// Get header of current frame according to specific Size
    /// </summary>
    /// <param name="MinorVersion">Minor version of ID3v2</param>
    /// <returns>MemoryStream contain frame header</returns>
//    protected MemoryStream FrameHeader(int MinorVersion)
//    {
//        byte[] Buf;
//        MemoryStream ms = new MemoryStream();
//        int FrameIDLength = MinorVersion == 2 ? 3 : 4; // Length of FrameID according to version
//        String Temp = _FrameID;
//
//        // if minor version of ID3 were 2, the frameID is 3 character length
//        if (MinorVersion == 2)
//        {
//            Temp = FramesInfo.Get3CharID(Temp);
//            if (Temp == null) // This frame is not availabe in this version
//                return null;
//        }
//
//        ms.Write(Encoding.ASCII.GetBytes(Temp), 0, FrameIDLength); // Write FrameID
//        Buf = BitConverter.GetBytes(Length);
//        Array.Reverse(Buf);
//        if (MinorVersion == 2)
//            ms.Write(Buf, 1, Buf.Length - 1); // Write Frame Size
//        else
//            ms.Write(Buf, 0, Buf.Length); // Write Frame Size
//
//        if (MinorVersion != 2)
//        {
//            // If newer than version 2 it have Flags
//            Buf = _FrameFlags;//BitConverter.GetBytes((ushort)_FrameFlags);
//            Array.Reverse(Buf);
//            ms.Write(Buf, 0, Buf.Length); // Write Frame Flag
//        }
//
//        return ms;
//    }

//    /// <summary>
//    /// Indicate if this frame is readable
//    /// </summary>
//    public boolean IsReadableFrame()
//    {
//    	return (!_MustDrop | IsAvailable);
//    }

    protected void ErrorOccur(String Message, boolean MustDrop)
    {
        _ErrorMessage = Message;
        _MustDrop = MustDrop;
    }

    /// <summary>
    /// Get length of Specific String according to Encoding
    /// </summary>
    /// <param name="Text">Text to get length</param>
    /// <param name="TEncoding">TextEncoding to use for Length calculation</param>
    /// <returns>Length of text</returns>
    protected int GetTextLength(String Text, String encoding, boolean AddNullCharacter)
    {
        int StringLength;

        StringLength = Text.length();
        if (encoding != null
        && ( encoding.equals("UTF_16")
        || encoding.equals("UTF_16BE") )
        )
        {
            StringLength *= 2; // in UTF-16 each character is 2 bytes
        }

        if (AddNullCharacter)
        {
            if (encoding != null
                    && ( encoding.equals("UTF_16")
                    || encoding.equals("UTF_16BE") )
                    )
                StringLength += 2;
            else
                StringLength++;
        }

        return StringLength;
    }

    /// <summary>
    /// Write specific String to specific MemoryStream
    /// </summary>
    /// <param name="Data">MemoryStream to write text to</param>
    /// <param name="Text">Text to write in MemoryStream</param>
    /// <param name="TEncoding">TextEncoding use for text</param>
    /// <param name="AddNullCharacter">indicate if need to add null characters</param>
//    protected void WriteText(MemoryStream Data, String Text, TextEncodings TEncoding, boolean AddNullCharacter)
//    {
//        byte[] Buf;
//        Buf = FileStreamEx.GetEncoding(TEncoding).GetBytes(Text);
//        Data.Write(Buf, 0, Buf.Length);
//        if (AddNullCharacter)
//        {
//            Data.WriteByte(0);
//            if (TEncoding == TextEncodings.UTF_16 || TEncoding == TextEncodings.UTF_16BE)
//                Data.WriteByte(0);
//        }
//    }

//        protected enum ValidatingErrorTypes
//        {
//            Nothing = 0,
//            ID3Error,
//            Exception
//        }

    /// <summary>
    /// Indicate is value of Enumeration valid for that enum
    /// </summary>
    /// <param name="Enumeration">Enumeration to control value for</param>
    /// <param name="ErrorType">if not valid how error occur</param>
    /// <returns>true if valid otherwise false</returns>
//    protected boolean IsValidEnumValue(Enum Enumeration, int ErrorType)
//    {
//        if (Enum.IsDefined(Enumeration.GetType(), Enumeration))
//            return true;
//        else
//        {
//            if (ErrorType == ValidatingErrorTypes.ID3Error)
//            {
//                ErrorOccur(
//                    " is out of range of ", true);
//            }
//            else if (ErrorType == ValidatingErrorTypes.Exception)
//            {
//                //" is out of range of "; //+ Enumeration.GetType().ToString()));
//            	Log.e("exception"," is out of range of ");
//            }
//
//            return false;
//        }
//    }

    protected boolean ValidatingFrameID(String FrameIdentifier, int ErrorType)
    {
        boolean IsValid = FramesInfo.IsValidFrameID(FrameIdentifier);

        if (!IsValid)
        {
            if (ErrorType == ValidatingErrorTypes.Exception)
            {
            	Log.e("id3v2 exception","FrameID must be 4 capital letters");
            }
            else if (ErrorType == ValidatingErrorTypes.ID3Error)
            {
                ErrorOccur(FrameIdentifier + " is not valid FrameID", true);
            }
        }

        return IsValid;
    }

//        /// <summary>
//        /// Indicate if this frame available
//        /// </summary>
//        public abstract boolean IsAvailable
//        {
//            get;
//        }

    /// <summary>
    /// Get stream containing this frame information
    /// </summary>
    /// <param name="MinorVersion">Minor version of ID3v2</param>
    /// <returns>MemoryStream according to this frame</returns>
    // public abstract MemoryStream FrameStream(int MinorVersion);

//        /// <summary>
//        /// Get Length of current frame
//        /// </summary>
//        public abstract int Length
//        {
//            get;
//        }

    /// <summary>
    /// Get FrameID of current frame
    /// </summary>
    public String getFrameID()
    {
    	return _FrameID;
    }

    /// <summary>
    /// Gets or sets if current frame is ReadOnly
    /// </summary>
    public boolean IsReadOnly()
    {
        if ((frameFlags & FrameFlags.ReadOnly)
            == FrameFlags.ReadOnly)
        		return true;
        else
                return false;
    }

    /// <summary>
    /// Gets or sets if current frame is Encrypted
    /// </summary>
    public boolean getEncryption()
    {
        if ((frameFlags & FrameFlags.Encryption)
            == FrameFlags.Encryption)
            return true;
        else
            return false;
    }
    public void setEncryption(boolean value)
    {
        if (value == true)
        	frameFlags |= FrameFlags.Encryption;
        else
        	frameFlags &= ~FrameFlags.Encryption;
    }

    /// <summary>
    /// Gets or sets whether or not frame belongs in a group with other frames
    /// </summary>
    public boolean getGroupIdentity()
    {
        if ((frameFlags & FrameFlags.GroupingIdentity)
            == FrameFlags.GroupingIdentity)
            return true;
        else
            return false;
    }
    public void setGroupIdentity(boolean value)
    {
        if (value == true)
            frameFlags |= FrameFlags.GroupingIdentity;
        else
            frameFlags &= ~FrameFlags.GroupingIdentity;
    }

    /// <summary>
    /// Gets or sets whether or not this frame was compressed
    /// </summary>
    public boolean getCompression()
    {
        if ((frameFlags & FrameFlags.Compression)
           == FrameFlags.Compression)
            return true;
        else
            return false;
    }
    public void setCompression(boolean value)
    {
        if (value == true)
            frameFlags |= FrameFlags.Compression;
        else
            frameFlags &= ~FrameFlags.Compression;
    }

    /// <summary>
    /// Gets or sets if it's unknown frame it should be preserved or discared
    /// </summary>
    public boolean getagAlterPreservation()
    {
        if ((frameFlags & FrameFlags.TagAlterPreservation)
           == FrameFlags.TagAlterPreservation)
            return true;
        else
            return false;
    }
    public void setagAlterPreservation(boolean value)
    {
        if (value == true)
        	frameFlags |= FrameFlags.TagAlterPreservation;
        else
        	frameFlags &= ~FrameFlags.TagAlterPreservation;
    }

    /// <summary>
    /// Gets or sets what to do if file excluding frame, Preseved or discared
    /// </summary>
    public boolean IsFileAlterPreservation()
    {
        if ((frameFlags & FrameFlags.FileAlterPreservation)
           == FrameFlags.FileAlterPreservation)
            return true;
        else
            return false;
    }

    /// <summary>
    /// Gets or sets is current frame a linked frame
    /// </summary>
    public boolean IsLinked()
    {
        return _IsLinked;
    }


//        /// <summary>
//        /// Retrun a String that represent FrameID of current Frame
//        /// </summary>
//        /// <returns>FrameID of current Frame</returns>
//        public String ToString()
//        {
//            return _FrameID;
//        }

    /// <summary>
    /// Get error message of current Frame
    /// </summary>
    String getErrorMessage()
    {
        return _ErrorMessage;
    }

    /// <summary>
    /// Indicate if specific text is Ascii
    /// </summary>
    /// <param name="Text">Text to detect</param>
    /// <returns>true if is ascii otherwise false</returns>
}
