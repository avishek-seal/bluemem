package io.github.avishek.bluemem.configuration.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Node implements Serializable{

	private static final long serialVersionUID = 4490250086577909721L;

	private final UUID id = UUID.randomUUID();
	
	private String name;
	
	private String url;
	
	private Node root;
	
	private Node parent;
	
	private List<Cluster> clusters;

	public String getId() {
		return id.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", name=" + name + ", url=" + url + ", root=" + root + ", parent=" + parent
				+ ", clusters=" + clusters + "]";
	}
}
