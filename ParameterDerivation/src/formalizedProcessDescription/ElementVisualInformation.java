
package formalizedProcessDescription;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class ElementVisualInformation implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("x")
    @Expose
    private int x;
    @SerializedName("y")
    @Expose
    private int y;
    @SerializedName("waypoints")
    @Expose
    private List<Waypoint> waypoints;
    private final static long serialVersionUID = -7127261613607247134L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}
