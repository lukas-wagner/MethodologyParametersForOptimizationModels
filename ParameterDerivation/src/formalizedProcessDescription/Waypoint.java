
package formalizedProcessDescription;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class Waypoint implements Serializable
{

    @SerializedName("original")
    @Expose
    private Original original;
    @SerializedName("x")
    @Expose
    private int x;
    @SerializedName("y")
    @Expose
    private int y;
    private final static long serialVersionUID = -7371960303306163657L;

    public Original getOriginal() {
        return original;
    }

    public void setOriginal(Original original) {
        this.original = original;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
