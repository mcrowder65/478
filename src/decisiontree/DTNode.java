package decisiontree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import toolkit.Matrix;

/**
 * String value<br>
 * Map of String, DTNode nodes<br>
 * Matrix features<br>
 * Matrix labels<br>
 * 
 * @author mcrowder65
 *
 */
public class DTNode {
	private String value;
	private Map<String, DTNode> nodes;
	private ObjectMapper mapper;
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

	public DTNode(DTNode node) {
		this.value = node.getValue();
		this.nodes = node.getNodes();
		this.features = node.getFeatures();
		this.labels = node.getLabels();
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

	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	@Override
	public String toString() {

		try {
			return getMapper().writeValueAsString(this);
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
