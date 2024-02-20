
package formalizedProcessDescription;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class Identification implements Serializable
{

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("uniqueIdent")
    @Expose
    private String uniqueIdent;
    @SerializedName("longName")
    @Expose
    private String longName;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("versionNumber")
    @Expose
    private String versionNumber;
    @SerializedName("revisionNumber")
    @Expose
    private String revisionNumber;
    private final static long serialVersionUID = -7109477478045826600L;

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public String getUniqueIdent() {
        return uniqueIdent;
    }

    public void setUniqueIdent(String uniqueIdent) {
        this.uniqueIdent = uniqueIdent;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

}
