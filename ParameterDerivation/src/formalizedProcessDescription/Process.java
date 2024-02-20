
package formalizedProcessDescription;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class Process implements Serializable
{

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("elementsContainer")
    @Expose
    private List<String> elementsContainer;
    @SerializedName("isDecomposedProcessOperator")
    @Expose
    private Object isDecomposedProcessOperator;
    @SerializedName("consistsOfStates")
    @Expose
    private List<String> consistsOfStates;
    @SerializedName("consistsOfSystemLimit")
    @Expose
    private String consistsOfSystemLimit;
    @SerializedName("consistsOfProcesses")
    @Expose
    private List<Object> consistsOfProcesses;
    @SerializedName("consistsOfProcessOperator")
    @Expose
    private List<String> consistsOfProcessOperator;
    private final static long serialVersionUID = -8523508097964235178L;

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getElementsContainer() {
        return elementsContainer;
    }

    public void setElementsContainer(List<String> elementsContainer) {
        this.elementsContainer = elementsContainer;
    }

    public Object getIsDecomposedProcessOperator() {
        return isDecomposedProcessOperator;
    }

    public void setIsDecomposedProcessOperator(Object isDecomposedProcessOperator) {
        this.isDecomposedProcessOperator = isDecomposedProcessOperator;
    }

    public List<String> getConsistsOfStates() {
        return consistsOfStates;
    }

    public void setConsistsOfStates(List<String> consistsOfStates) {
        this.consistsOfStates = consistsOfStates;
    }

    public String getConsistsOfSystemLimit() {
        return consistsOfSystemLimit;
    }

    public void setConsistsOfSystemLimit(String consistsOfSystemLimit) {
        this.consistsOfSystemLimit = consistsOfSystemLimit;
    }

    public List<Object> getConsistsOfProcesses() {
        return consistsOfProcesses;
    }

    public void setConsistsOfProcesses(List<Object> consistsOfProcesses) {
        this.consistsOfProcesses = consistsOfProcesses;
    }

    public List<String> getConsistsOfProcessOperator() {
        return consistsOfProcessOperator;
    }

    public void setConsistsOfProcessOperator(List<String> consistsOfProcessOperator) {
        this.consistsOfProcessOperator = consistsOfProcessOperator;
    }

}
