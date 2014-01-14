package okosama.app.media.ID3v2Frame;

import java.io.IOException;
import java.io.InputStream;

import okosama.app.media.StreamReadUtil;

/// <summary>
/// A Class for frames that include Rating, Counter, Email
/// </summary>
public class PopularimeterFrame extends TextOnlyFrame
{
  protected long _Counter;
  protected byte _Rating;

  /// <summary>
  /// New PopularimeterFrame
  /// </summary>
  /// <param name="FrameID">4 Characters tag identifier</param>
  /// <param name="Flags">Frame Flags</param>
  /// <param name="Data">FileStream contain frame data</param>
  PopularimeterFrame(String FrameID, byte[] Flags, InputStream Data, int Length)
  {
	  super(FrameID, Flags);
	  
      try {

	      setEMail( StreamReadUtil.readStreamAsString(Data, new byte[Length], Length) );
	      //Data.ReadText(Length, TextEncodings.Ascii, ref Length, true); // Read Email Address
	
	      _Rating = StreamReadUtil.readStreamAsByte(Data); // Read Rating
	      Length--;
	
	      if (Length > 8)
	      {
	          ErrorOccur("Counter value for Popularimeter frame is more than 8 byte." +
	              " this is not supported by this program", true);
	          return;
	      }
	
	      byte[] LBuf = new byte[8];
	      byte[] Buf = new byte[Length];
	
		  Buf = StreamReadUtil.readStreamAsByte(Data, Length);
	      System.arraycopy(Buf, 8 - Buf.length, LBuf, 0, Buf.length);
	      //Buf(LBuf, 8 - Buf.length);
	      // Array.Reverse(LBuf);
	      StreamReadUtil.reverseByte(LBuf);
	      _Counter = StreamReadUtil.convertByteToLong(LBuf);
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }

  }

  /// <summary>
  /// New PopulariMeter frame from specific information
  /// </summary>
  /// <param name="Flags">Frame Flags</param>
  /// <param name="EMail">Email of user</param>
  /// <param name="Rating">User Rated value</param>
  /// <param name="Counter">How many times user listened to audio</param>
  public PopularimeterFrame(byte[] Flags, String EMail,
      byte Rating, long Counter)
  {
      super("POPM", Flags);
      setText( EMail );
      _Rating = Rating;
      _Counter = Counter;
  }

  /// <summary>
  /// Get or Set Rating value for current Email Address
  /// </summary>
  public byte getRating()
  {
      return _Rating;
  }
  public void setRating( byte value )
  {
	  _Rating = value;
  }

  /// <summary>
  /// Get or Set Counter for current User (Mail Address)
  /// </summary>
  public long getCounter()
  {
	  return _Counter;
  }
  public void setCounter( long value )
  {
	  _Counter = value;
  }

  /// <summary>
  /// Gets or sets Email for current User
  /// </summary>
  public String getEMail()
  {
	  return getText();
  }
  public void setEMail( String value )
  {
  	setText(value);
  }

//  #region -> Override method and properties <-

  /// <summary>
  /// Get length of current frame
  /// </summary>
//  public override int Length
//  {
//      get
//      {
//          return 10 + EMail.Length;
//          // 1:   Rating Length
//          // 1:   Seprator
//          // 8:   Counter
//      }
//  }

  /// <summary>
  /// Get MemoryStream ot save current frame
  /// </summary>
  /// <returns>MemoryStream that represent current frame data</returns>
//  public override MemoryStream FrameStream(int MinorVersion)
//  {
//      MemoryStream ms = FrameHeader(MinorVersion);
//      WriteText(ms, EMail, TextEncodings.Ascii, true);
//
//      ms.WriteByte(_Rating);
//
//      byte[] Buf = BitConverter.GetBytes(_Counter);
//      Array.Reverse(Buf);
//      ms.Write(Buf, 0, 8);
//
//      return ms;
//  }

  /// <summary>
  /// Indicate if current frame is available
  /// </summary>
//  public override bool IsAvailable
//  {
//      get
//      {
//          if (EMail != "")
//              return true;
//
//          return false;
//      }
//  }
//
//  #endregion

  @Override
  public boolean equals(Object obj)
  {
      if (false == obj instanceof PopularimeterFrame)
          return false;

      if (((PopularimeterFrame)obj).getEMail() == this.getEMail())
          return true;
      else
          return false;
  }

//  public override int GetHashCode()
//  {
//      return base.GetHashCode();
//  }

//  /// <summary>
//  /// This property is not usable for this class
//  /// </summary>
//  public new String Text
//  {
//      get
//      { throw (new Exception("This property is not useable for this class")); }
//  }
}
