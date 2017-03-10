package decisiontree;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
		for (String key : nodes.keySet()) {
			this.nodes.put(key, nodes.get(key));
		}
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

	public void deleteNodes() {
		this.nodes = new HashMap<>();
	}

	public int getNumberOfNodes(DTNode node) {
		Map<String, DTNode> map = node.getNodes();
		int c = 1;
		if (map.size() == 0) {
			return 1;
		}
		for (String key : map.keySet()) {
			c += getNumberOfNodes(map.get(key));
		}
		return c;

	}

	public int getNumberOfLayers(DTNode node, List<String> keys, int num, DTNode originalNode, List<Integer> depths) {
		Map<String, DTNode> map = node.getNodes();
		if (map.size() == 0) {
			return num + 1;
		}
		for (String key : map.keySet()) {
			depths.add(getNumberOfLayers(map.get(key), keys, num + 1, originalNode, depths));

		}
		int greatest = Collections.max(depths);

		return greatest;

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
