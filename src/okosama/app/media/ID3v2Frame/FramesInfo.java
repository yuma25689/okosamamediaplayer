package okosama.app.media.ID3v2Frame;

import java.util.HashMap;

public class FramesInfo {
	public static HashMap< String, FrameInfo > framesDictionary = new HashMap<String,FrameInfo>();
	
    /// <summary>
    /// Initialize All FrameID infos
    /// </summary>
    public static void InitialValues()
    {
        /* 
         * Also we can use an XML file to store this information
         * and just load it here. but i think in that way using DLL
         * will not as relax as it don't use XML. because you need always
         * have that XML file beside DLL. if you don't the DLL won't work
         * without XML or any type of other storing file just you need this DLL
         * and it's enough. i think it's better
         */
        framesDictionary.put("", new FrameInfo("", "CRM", "Encrypted Meta File",
            new boolean[] { true, false, false }));
        framesDictionary.put("AENC", new FrameInfo("AENC", "CRA", "Audio Encryption",
            new boolean[] { true, true, true }));
        framesDictionary.put("APIC", new FrameInfo("APIC", "PIC", "Attached Picture",
            new boolean[] { true, true, true }));
        framesDictionary.put("ASPI", new FrameInfo("ASPI", null, "Audio Seek Point Index",
            new boolean[] { false, false, true }));
        framesDictionary.put("COMM", new FrameInfo("COMM", "COM", "Comment",
            new boolean[] { true, true, true }));
        framesDictionary.put("COMR", new FrameInfo("COMR", null, "Commercial Frame",
            new boolean[] { false, true, true }));
        framesDictionary.put("ENCR", new FrameInfo("ENCR", null, "Encryption Method Registration",
            new boolean[] { false, true, true }));
        framesDictionary.put("EQU2", new FrameInfo("EQU2", null, "Equalisation (2)",
            new boolean[] { false, false, true }));
        framesDictionary.put("EQUA", new FrameInfo("EQUA", "EQU", "Equalisation",
            new boolean[] { true, true, false }));
        framesDictionary.put("ETCO", new FrameInfo("ETCO", "ETC", "Event Timing Code",
            new boolean[] { true, true, true }));
        framesDictionary.put("GEOB", new FrameInfo("GEOB", "GEO", "General Encapsulated Object",
            new boolean[] { true, true, true }));
        framesDictionary.put("GRID", new FrameInfo("GRID", null, "Group Identification Registration",
            new boolean[] { false, true, true }));
        framesDictionary.put("IPLS", new FrameInfo("IPLS", "IPL", "Involved People List",
            new boolean[] { true, true, false }));
        framesDictionary.put("LINK", new FrameInfo("LINK", "LNK", "Linked Information",
            new boolean[] { true, true, true }));
        framesDictionary.put("MCDI", new FrameInfo("MCDI", "MCI", "Music CD Identifier",
            new boolean[] { true, true, true }));
        framesDictionary.put("MLLT", new FrameInfo("MLLT", "MLL", "Mepg Location Lookup Table",
            new boolean[] { true, true, true }));
        framesDictionary.put("OWNE", new FrameInfo("OWNE", null, "Ownership Information",
            new boolean[] { false, true, true }));
        framesDictionary.put("PCNT", new FrameInfo("PCNT", "CNT", "Play Counter",
            new boolean[] { true, true, true }));
        framesDictionary.put("POPM", new FrameInfo("POPM", "POP", "Popularimeter",
            new boolean[] { true, true, true }));
        framesDictionary.put("POSS", new FrameInfo("POSS", null, "Position Synchronisation Frame",
            new boolean[] { false, true, true }));
        framesDictionary.put("PRIV", new FrameInfo("PRIV", null, "Private Frame",
            new boolean[] { false, true, true }));
        framesDictionary.put("RBUF", new FrameInfo("RBUF", "BUF", "Recommended Buffer Size",
            new boolean[] { true, true, true }));
        framesDictionary.put("RVA2", new FrameInfo("RVA2", null, "Relative Volume Adjustment (2)",
            new boolean[] { false, false, true }));
        framesDictionary.put("RVAD", new FrameInfo("RVAD", "RVA", "Relative Volume Adjustment",
            new boolean[] { true, true, false }));
        framesDictionary.put("RVRB", new FrameInfo("RVRB", "REV", "Reverb",
            new boolean[] { true, true, true }));
        framesDictionary.put("SEEK", new FrameInfo("SEEK", null, "Seek Frame",
            new boolean[] { false, false, true }));
        framesDictionary.put("SIGN", new FrameInfo("SIGN", null, "Signature Frame",
            new boolean[] { false, false, true }));
        framesDictionary.put("SYLT", new FrameInfo("SYLT", "SLT", "Synchronized Lyric/Text",
            new boolean[] { true, true, true }));
        framesDictionary.put("SYTC", new FrameInfo("SYTC", "STC", "Synced Tempo Codes",
            new boolean[] { true, true, true }));
        framesDictionary.put("TALB", new FrameInfo("TALB", "TAL", "Album",
            new boolean[] { true, true, true }));
        framesDictionary.put("TBPM", new FrameInfo("TBPM", "TBP", "BPM ( Beats Per Minutes)",
            new boolean[] { true, true, true }));
        framesDictionary.put("TCOM", new FrameInfo("TCOM", "TCM", "Composer",
            new boolean[] { true, true, true }));
        framesDictionary.put("TCON", new FrameInfo("TCON", "TCO", "Content Type",
            new boolean[] { true, true, true }));
        framesDictionary.put("TCOP", new FrameInfo("TCOP", "TCR", "Copyright Message",
            new boolean[] { true, true, true }));
        framesDictionary.put("TDAT", new FrameInfo("TDAT", "TDA", "Date",
            new boolean[] { true, true, false }));
        framesDictionary.put("TDEN", new FrameInfo("TDEN", null, "Encoding Time",
            new boolean[] { false, false, true }));
        framesDictionary.put("TDLY", new FrameInfo("TDLY", "TDY", "Playlist Delay",
            new boolean[] { true, true, true }));
        framesDictionary.put("TDOR", new FrameInfo("TDOR", null, "Orginal Release Time",
            new boolean[] { false, false, true }));
        framesDictionary.put("TDRC", new FrameInfo("TDRC", null, "Recording Time",
            new boolean[] { false, false, true }));
        framesDictionary.put("TDRL", new FrameInfo("TDRL", null, "Release Time",
            new boolean[] { false, false, true }));
        framesDictionary.put("TDTG", new FrameInfo("TDTG", null, "Tagging Time",
            new boolean[] { false, false, true }));
        framesDictionary.put("TENC", new FrameInfo("TENC", "TEN", "Encoded By",
            new boolean[] { true, true, true }));
        framesDictionary.put("TEXT", new FrameInfo("TEXT", "TXT", "Lyric/Text Writer",
            new boolean[] { true, true, true }));
        framesDictionary.put("TFLT", new FrameInfo("TFLT", "TFT", "File Type",
            new boolean[] { true, true, true }));
        framesDictionary.put("TIME", new FrameInfo("TIME", "TIM", "Time",
            new boolean[] { true, true, false }));
        framesDictionary.put("TIPL", new FrameInfo("TIPL", null, "Involved People List",
            new boolean[] { false, false, true }));
        framesDictionary.put("TIT1", new FrameInfo("TIT1", "TT1", "Content Group Description",
            new boolean[] { true, true, true }));
        framesDictionary.put("TIT2", new FrameInfo("TIT2", "TT2", "Title",
            new boolean[] { true, true, true }));
        framesDictionary.put("TIT3", new FrameInfo("TIT3", "TT3", "Subtitle/Desripction",
            new boolean[] { true, true, true }));
        framesDictionary.put("TKEY", new FrameInfo("TKEY", "TKE", "Initial Key",
            new boolean[] { true, true, true }));
        framesDictionary.put("TLAN", new FrameInfo("TLAN", "TLA", "Language",
            new boolean[] { true, true, true }));
        framesDictionary.put("TLEN", new FrameInfo("TLEN", "TLE", "Length",
            new boolean[] { true, true, true }));
        framesDictionary.put("TMCL", new FrameInfo("TMCL", null, "Musician Credits List",
            new boolean[] { false, false, true }));
        framesDictionary.put("TMED", new FrameInfo("TMED", "TMT", "Media Type",
            new boolean[] { true, true, true }));
        framesDictionary.put("TMOO", new FrameInfo("TMOO", null, "Mood",
            new boolean[] { false, false, true }));
        framesDictionary.put("TOAL", new FrameInfo("TOAL", "TOT", "Orginal Title",
            new boolean[] { true, true, true }));
        framesDictionary.put("TOFN", new FrameInfo("TOFN", "TOF", "Orginal Filename",
            new boolean[] { true, true, true }));
        framesDictionary.put("TOLY", new FrameInfo("TOLY", "TOL", "Orginal Lyricist",
            new boolean[] { true, true, true }));
        framesDictionary.put("TOPE", new FrameInfo("TOPE", "TOA", "Orginal Artist",
            new boolean[] { true, true, true }));
        framesDictionary.put("TORY", new FrameInfo("TORY", "TOR", "Orginal Release Year",
            new boolean[] { true, true, false }));
        framesDictionary.put("TOWN", new FrameInfo("TOWN", null, "File Owner",
            new boolean[] { false, true, true }));
        framesDictionary.put("TPE1", new FrameInfo("TPE1", "TP1", "Lead Artist",
            new boolean[] { true, true, true }));
        framesDictionary.put("TPE2", new FrameInfo("TPE2", "TP2", "Band Artist",
            new boolean[] { true, true, true }));
        framesDictionary.put("TPE3", new FrameInfo("TPE3", "TP3", "Conductor",
            new boolean[] { true, true, true }));
        framesDictionary.put("TPE4", new FrameInfo("TPE4", "TP4", "Interpreted",
            new boolean[] { true, true, true }));
        framesDictionary.put("TPOS", new FrameInfo("TPOS", "TPA", "Part of set",
            new boolean[] { true, true, true }));
        framesDictionary.put("TPRO", new FrameInfo("TPRO", null, "Produced Notice",
            new boolean[] { false, false, true }));
        framesDictionary.put("TPUB", new FrameInfo("TPUB", "TPB", "Publisher",
            new boolean[] { true, true, true }));
        framesDictionary.put("TRCK", new FrameInfo("TRCK", "TRK", "Track Number",
            new boolean[] { true, true, true }));
        framesDictionary.put("TRDA", new FrameInfo("TRDA", "TRD", "Recording Date",
            new boolean[] { true, true, false }));
        framesDictionary.put("TRSN", new FrameInfo("TRSN", null, "Internet Radio Station Name",
            new boolean[] { false, true, true }));
        framesDictionary.put("TRSO", new FrameInfo("TRSO", null, "Internet Radio Station Owner",
            new boolean[] { false, true, true }));
        framesDictionary.put("TSIZ", new FrameInfo("TSIZ", "TSI", "Size",
            new boolean[] { true, true, false }));
        framesDictionary.put("TSOA", new FrameInfo("TSOA", null, "Album Sort Order",
            new boolean[] { false, false, true }));
        framesDictionary.put("TSOP", new FrameInfo("TSOP", null, "Preformer Sort Order",
            new boolean[] { false, false, true }));
        framesDictionary.put("TSOT", new FrameInfo("TSOT", null, "Title Sort Order",
            new boolean[] { false, false, true }));
        framesDictionary.put("TSRC", new FrameInfo("TSRC", "TRC", "ISRC",
            new boolean[] { true, true, true }));
        framesDictionary.put("TSSE", new FrameInfo("TSSE", "TSS", "Software/Hardware And Setting Used For Encoding",
            new boolean[] { true, true, true }));
        framesDictionary.put("TSST", new FrameInfo("TSST", null, "Set Subtitle",
            new boolean[] { false, false, true }));
        framesDictionary.put("TYER", new FrameInfo("TYER", "TYE", "Year",
            new boolean[] { true, true, false }));
        framesDictionary.put("UFID", new FrameInfo("UFID", "UFI", "Unique File Identifier",
            new boolean[] { true, true, true }));
        framesDictionary.put("USER", new FrameInfo("USER", null, "Term Of Use",
            new boolean[] { false, true, true }));
        framesDictionary.put("USLT", new FrameInfo("USLT", "ULT", "Unsynchronized Lyric",
            new boolean[] { true, true, true }));
        framesDictionary.put("WCOM", new FrameInfo("WCOM", "WCM", "Commercial Information",
            new boolean[] { true, true, true }));
        framesDictionary.put("WCOP", new FrameInfo("WCOP", "WCP", "Copyright Information",
            new boolean[] { true, true, true }));
        framesDictionary.put("WOAF", new FrameInfo("WOAF", "WAF", "Official Audio File web",
            new boolean[] { true, true, true }));
        framesDictionary.put("WOAR", new FrameInfo("WOAR", "WAR", "Official Artist web",
            new boolean[] { true, true, true }));
        framesDictionary.put("WOAS", new FrameInfo("WOAS", "WAS", "Official Audio Source web",
            new boolean[] { true, true, true }));
        framesDictionary.put("WORS", new FrameInfo("WORS", null, "Official Radio Station Web",
            new boolean[] { false, true, true }));
        framesDictionary.put("WPAY", new FrameInfo("WPAY", null, "Payment web",
            new boolean[] { false, true, true }));
        framesDictionary.put("WPUB", new FrameInfo("WPUB", "WPB", "Publisher web",
            new boolean[] { true, true, true }));
    }
	
    /// <summary>
    /// Get FrameInfo from 4 chacarter FrameID
    /// </summary>
    /// <param name="FrameID">4 character FrameID to get FrameInfo</param>
    /// <returns>FrameInfo contain Specific frame information</returns>
    public static FrameInfo getFrameInfo(String FrameID)
    {
        return framesDictionary.get(FrameID);
    }

    /// <summary>
    /// Get 4 Character FrameID for specific 3 Character FrameID
    /// </summary>
    /// <param name="FrameID">3 character FrameID</param>
    /// <returns>System.String contain 4 Character FrameID or null if not found</returns>
    public static String Get4CharID(String FrameID3)
    {
        for (FrameInfo FI : framesDictionary.values() )
            if (FrameID3.equals( FI.getFrameID3Ch() ) )
                return FI.getFrameID4Ch();
        return null;
    }

    /// <summary>
    /// Get 3 character FrameID from specific 4 Character FrameID
    /// </summary>
    /// <param name="FrameID">4 character FrameID</param>
    /// <returns>3 Chacater FrameID</returns>
    public static String Get3CharID(String FrameID)
    {
        if (framesDictionary.containsKey(FrameID))
            return framesDictionary.get(FrameID).getFrameID3Ch();

        return null;
    }
    /// <summary>
    /// Indicate if specific FrameID is TextFrame(1), UserTextFrame(2) or non of them(0)
    /// </summary>
    /// <param name="FrameID">FrameID to control</param>
    /// <param name="Ver">minor version of ID3v2</param>
    /// <returns>int that indicate FrameID type</returns>
    public static int IsTextFrame(String FrameID, int Ver)
    {
        // 0: mean's it's not TextFrame and UserTextFrame either
        // 1: it's TextFrame
        // 2: it's UserTextFrame
        if (FrameID == "IPLS")
        {
            if (Ver == 4) // in version 4 IPLS frame removed
                return 0;
            else
                return 1;
        }

        if (FrameID.startsWith("T") || FrameID.startsWith("W"))
        {
            if (framesDictionary.containsKey(FrameID))
            {
                if (framesDictionary.get(FrameID).isValid(Ver))
                    return 1;
                return 2;
            }
            else
                return 2;
        }

        return 0;
    }
    /// <summary>
    /// Indicate if specific FrameID is compatible with specific minor version of ID3v2
    /// </summary>
    /// <param name="FrameID">FrameID to check</param>
    /// <param name="Ver">minor version of ID3v2</param>
    /// <returns>true if it's compatible otherwise false</returns>
    public static boolean IsCompatible(String FrameID, int Ver)
    {
        if (!framesDictionary.containsKey(FrameID))
            return false;

        return framesDictionary.get(FrameID).isValid(Ver);
    }

    /// <summary>
    /// Indicate if specific string is a valid FrameID
    /// </summary>
    /// <param name="FrameID">FrameID to check</param>
    /// <returns>true if valid otherwise false</returns>
    public static boolean IsValidFrameID(String FrameID)
    {
        if (FrameID == null)
            return false;

        if (FrameID.length() != 4)
            return false;
        else
        {
        	char[] chs = new char[4];
        	FrameID.getChars(0, 4, chs, 0);
            for (char ch : chs)
                if (!Character.isUpperCase(ch) && !Character.isDigit(ch))
                    return false;
        }
        return true;
    }
    
}
