package okosama.app.media.ID3v2Frame;

import android.util.Log;

public class FrameInfo {
    private String name;
    private String frameID4Ch;   // FrameID with 4 Characters
    private String frameID3Ch;   // FrameID with 3 Characters
    private boolean[] validation;

    public FrameInfo(String FrameID, String FrameID3,
        String Name, boolean[] Validation)
    {
        name = Name;
        frameID4Ch = FrameID;
        frameID3Ch = FrameID3;
        validation = Validation;
    }

    /// <summary>
    /// Get FrameID of current FrameIDInfo for specific version of ID3v2
    /// </summary>
    /// <param name="Version">minor version of ID3v2 to compatible with FrameID</param>
    /// <returns>System.String retrieve FrameID of current FrameIDInfo</returns>
    public String getFrameID(int Version)
    {
        if (Version < 2 || Version > 4)
        {
        	Log.e("ID3 tag VersionError(getFrameID)","Version must be between 2-4");
        	return null;//(new ArgumentOutOfRangeException("Version must be between 2-4"));
        }
        else if (Version == 2)
        {
            return frameID3Ch;
        }
        else
        {
            return frameID4Ch;
        }
    }

    /// <summary>
    /// Indicate if current FrameID is valid for specific Version of ID3v2
    /// </summary>
    /// <param name="Version">Version of ID3v2</param>
    /// <returns>true if it's valid otherwise false</returns>
    public boolean isValid(int Version)
    {
        if (Version < 2 && Version > 4)
        {
        	Log.e("ID3 tag VersionError(Varidate)","Version must be between 2-4");
        	
            // throw (new ArgumentOutOfRangeException("Version value must be between 2-4"));
        	return false;
        }

        return validation[Version - 2];
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the frameID4Ch
	 */
	public String getFrameID4Ch() {
		return frameID4Ch;
	}

	/**
	 * @param frameID4Ch the frameID4Ch to set
	 */
	public void setFrameID4Ch(String frameID4Ch) {
		this.frameID4Ch = frameID4Ch;
	}

	/**
	 * @return the frameID3Ch
	 */
	public String getFrameID3Ch() {
		return frameID3Ch;
	}

	/**
	 * @param frameID3Ch the frameID3Ch to set
	 */
	public void setFrameID3Ch(String frameID3Ch) {
		this.frameID3Ch = frameID3Ch;
	}

	/**
	 * @return the validation
	 */
	public boolean[] getValidation() {
		return validation;
	}

	/**
	 * @param validation the validation to set
	 */
	public void setValidation(boolean[] validation) {
		this.validation = validation;
	}
}
