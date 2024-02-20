package formalizedProcessDescription;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class FpdState.
 */
public class FpdState {

	/** The id. */
	String id; 
	
	/** The type. */
	String type;
	
	/** The incoming. */
	List<String> incoming = new ArrayList<String>(); 
	
	/** The outgoing. */
	List<String> outgoing = new ArrayList<String>(); 
	
	/** The flow type input side. */
	List<String> flowTypeInputSide =  new ArrayList<String>();
	
	List<String> flowTypeOutputSide =  new ArrayList<String>();
	/**
	/**
	 * Instantiates a new info energy product.
	 *
	 * @param uniqueId the unique id
	 * @param typeConnector the type connector
	 * @param in the in
	 * @param out the out
	 */
	public FpdState (String uniqueId, String typeConnector, List<String> in, List<String> out) {
		setId(uniqueId);
		setType(typeConnector);
		setIncoming(in);
		setOutgoing(out);
	}
	
	/**
	 * Instantiates a new info energy product.
	 */
	public FpdState() {
		// TODO Auto-generated constructor stub
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
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @param incoming the incoming to set
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
	 * @param outgoing the outgoing to set
	 */
	public void setOutgoing(List<String> outgoing) {
		this.outgoing = outgoing;
	}

	/**
	 * @return the flowTypeInputSide
	 */
	public List<String> getFlowTypeInputSide() {
		return flowTypeInputSide;
	}

	/**
	 * @param flowTypeInputSide the flowTypeInputSide to set
	 */
	public void setFlowTypeInputSide(List<String> flowTypeInputSide) {
		this.flowTypeInputSide = flowTypeInputSide;
	}

	/**
	 * @return the flowTypeOutputSide
	 */
	public List<String> getFlowTypeOutputSide() {
		return flowTypeOutputSide;
	}

	/**
	 * @param flowTypeOutputSide the flowTypeOutputSide to set
	 */
	public void setFlowTypeOutputSide(List<String> flowTypeOutputSide) {
		this.flowTypeOutputSide = flowTypeOutputSide;
	}


}
