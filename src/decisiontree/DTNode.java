package decisiontree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import toolkit.Matrix;

/**
 * String value;<br>
 * boolean isLeafNode;<br>
 * Map of String, DTNode nodes;
 * 
 * @author mcrowder65
 *
 */
public class DTNode {
	private String value;
	private Map<String, DTNode> nodes;
	private ObjectMapper mapper = new ObjectMapper();
	private Matrix features;
	private Matrix labels;

	public Matrix getFeatures() {
		return features;
	}

	public void setFeatures(Matrix features) {
		this.features = features;
	}

	public Matrix getLabels() {
		return labels;
	}

	public void setLabels(Matrix labels) {
		this.labels = labels;
	}

	public DTNode(Matrix features, Matrix labels) {
		setLabels(labels);
		setFeatures(features);
		nodes = new HashMap<>();
		this.value = null;
	}

	/**
	 * Sets this.value to value, and isLeafNode to false
	 * 
	 * @param value
	 *            String
	 */
	public DTNode(String value) {
		nodes = new HashMap<>();
		this.value = value;
	}

	public DTNode(String value, boolean isLeafNode) {
		nodes = new HashMap<>();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, DTNode> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, DTNode> nodes) {
		this.nodes = nodes;
	}

	public DTNode getNode(String key) {
		return nodes.get(key);
	}

	public void setNode(String key, DTNode value) {
		nodes.put(key, value);
	}

	@Override
	public String toString() {

		try {
			return mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
