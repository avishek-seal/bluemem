package io.github.avishek.bluemem.configuration.maker;

import java.util.List;

import io.github.avishek.bluemem.configuration.model.Cluster;
import io.github.avishek.bluemem.configuration.model.Node;

public interface BluememConfiguration {

	String getCofigurationDirectory();
	
	String getDataDirectory();
	
	String getBluememConfigurationFileURL();
	
	String getBluememDataFileURL();
	
	Node getRootNode();
	
	Node getParentNode();
	
	List<Cluster> getClusters();
	
	boolean isRoot();
	
	boolean hasChildren();
}
