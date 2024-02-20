
package formalizedProcessDescription;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class FormalizedProcessDescription implements Serializable
{

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("targetNamespace")
    @Expose
    private String targetNamespace;
    @SerializedName("entryPoint")
    @Expose
    private String entryPoint;
    @SerializedName("process")
    @Expose
    private Process process;
    @SerializedName("elementDataInformation")
    @Expose
    private List<ElementDataInformation> elementDataInformation;
    @SerializedName("elementVisualInformation")
    @Expose
    private List<ElementVisualInformation> elementVisualInformation;
    private final static long serialVersionUID = 6022271242215354979L;

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public List<ElementDataInformation> getElementDataInformation() {
        return elementDataInformation;
    }

    public void setElementDataInformation(List<ElementDataInformation> elementDataInformation) {
        this.elementDataInformation = elementDataInformation;
    }

    public List<ElementVisualInformation> getElementVisualInformation() {
        return elementVisualInformation;
    }

    public void setElementVisualInformation(List<ElementVisualInformation> elementVisualInformation) {
        this.elementVisualInformation = elementVisualInformation;
    }

}
