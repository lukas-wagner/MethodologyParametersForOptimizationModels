
package formalizedProcessDescription;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// TODO: Auto-generated Javadoc
/**
 * The Class ElementDataInformation.
 */
//@Generated("jsonschema2pojo")
public class ElementDataInformation implements Serializable
{

    /** The $type. */
    @SerializedName("$type")
    @Expose
    private String $type;
    
    /** The id. */
    @SerializedName("id")
    @Expose
    private String id;
    
    /** The elements container. */
    @SerializedName("elementsContainer")
    @Expose
    private List<String> elementsContainer;
    
    /** The identification. */
    @SerializedName("identification")
    @Expose
    private Identification identification;
    
    /** The is assigned to. */
    @SerializedName("isAssignedTo")
    @Expose
    private List<String> isAssignedTo;
    
    /** The incoming. */
    @SerializedName("incoming")
    @Expose
    private List<String> incoming;
    
    /** The outgoing. */
    @SerializedName("outgoing")
    @Expose
    private List<String> outgoing;
    
    /** The source ref. */
    @SerializedName("sourceRef")
    @Expose
    private String sourceRef;
    
    /** The target ref. */
    @SerializedName("targetRef")
    @Expose
    private String targetRef;
    
    /** The name. */
    @SerializedName("name")
    @Expose
    private String name;
    
    /** The Constant serialVersionUID. */
    private final static long serialVersionUID = -2624717506782507272L;

    /**
     * Gets the $type.
     *
     * @return the $type
     */
    public String get$type() {
        return $type;
    }

    /**
     * Sets the $type.
     *
     * @param $type the new $type
     */
    public void set$type(String $type) {
        this.$type = $type;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the elements container.
     *
     * @return the elements container
     */
    public List<String> getElementsContainer() {
        return elementsContainer;
    }

    /**
     * Sets the elements container.
     *
     * @param elementsContainer the new elements container
     */
    public void setElementsContainer(List<String> elementsContainer) {
        this.elementsContainer = elementsContainer;
    }

    /**
     * Gets the identification.
     *
     * @return the identification
     */
    public Identification getIdentification() {
        return identification;
    }

    /**
     * Sets the identification.
     *
     * @param identification the new identification
     */
    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    /**
     * Gets the checks if is assigned to.
     *
     * @return the checks if is assigned to
     */
    public List<String> getIsAssignedTo() {
        return isAssignedTo;
    }

    /**
     * Sets the checks if is assigned to.
     *
     * @param isAssignedTo the new checks if is assigned to
     */
    public void setIsAssignedTo(List<String> isAssignedTo) {
        this.isAssignedTo = isAssignedTo;
    }

    /**
     * Gets the incoming.
     *
     * @return the incoming
     */
    public List<String> getIncoming() {
        return incoming;
    }

    /**
     * Sets the incoming.
     *
     * @param incoming the new incoming
     */
    public void setIncoming(List<String> incoming) {
        this.incoming = incoming;
    }

    /**
     * Gets the outgoing.
     *
     * @return the outgoing
     */
    public List<String> getOutgoing() {
        return outgoing;
    }

    /**
     * Sets the outgoing.
     *
     * @param outgoing the new outgoing
     */
    public void setOutgoing(List<String> outgoing) {
        this.outgoing = outgoing;
    }

    /**
     * Gets the source ref.
     *
     * @return the source ref
     */
    public String getSourceRef() {
        return sourceRef;
    }

    /**
     * Sets the source ref.
     *
     * @param sourceRef the new source ref
     */
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    /**
     * Gets the target ref.
     *
     * @return the target ref
     */
    public String getTargetRef() {
        return targetRef;
    }

    /**
     * Sets the target ref.
     *
     * @param targetRef the new target ref
     */
    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

}
