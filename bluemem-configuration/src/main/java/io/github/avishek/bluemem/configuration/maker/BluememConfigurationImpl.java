package io.github.avishek.bluemem.configuration.maker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.avishek.bluemem.configuration.model.Cluster;
import io.github.avishek.bluemem.configuration.model.Node;

@Configuration
@PropertySource({ "classpath:bluemem.properties" })
@Component
public class BluememConfigurationImpl implements BluememConfiguration, InitializingBean, DisposableBean {

	private Node node;

	@Value("${bluemem.dir.home}")
	private String HOME;

	@Value("${bluemem.dir.conf}")
	private String CONF_DIR;

	@Value("${bluemem.dir.data}")
	private String DATA_DIR;

	@Value("${bluemem.config.filename}")
	private String CONFIG_FILE_NAME;

	private String HOME_DIR;
	
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void afterPropertiesSet() throws Exception {
		HOME_DIR = System.getenv(HOME);
		CONF_DIR = HOME_DIR + "/" + CONF_DIR;
		DATA_DIR = HOME_DIR + "/" + DATA_DIR;
		
		prepareNodeConfiguration();
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCofigurationDirectory() {
		return CONF_DIR;
	}

	@Override
	public String getDataDirectory() {
		return DATA_DIR;
	}

	@Override
	public List<Cluster> getClusters() {
		return node.getClusters();
	}

	@Override
	public Node getParentNode() {
		return node.getParent();
	}

	@Override
	public Node getRootNode() {
		return node.getRoot();
	}

	@Override
	public boolean hasChildren() {
		return !CollectionUtils.isEmpty(node.getClusters());
	}

	@Override
	public boolean isRoot() {
		return (Objects.isNull(node.getParent())
				&& Objects.isNull(node.getRoot()))
				|| (StringUtils.isEmpty(node.getParent().getUrl())
				&& StringUtils.isEmpty(node.getRoot().getUrl()));
	}
	
	@Override
	public String getBluememConfigurationFileURL() {
		return CONF_DIR+"/"+CONFIG_FILE_NAME;
	}

	private void prepareNodeConfiguration() throws IOException {
		String bluememConfigData = new String(Files.readAllBytes(Paths.get(getBluememConfigurationFileURL())));
		node = GSON.fromJson(bluememConfigData, Node.class);
	}

}
