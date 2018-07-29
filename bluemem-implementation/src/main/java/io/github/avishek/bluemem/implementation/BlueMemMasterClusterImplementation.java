package io.github.avishek.bluemem.implementation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.github.avishek.bluemem.communicate.BlueMemAgent;
import io.github.avishek.bluemem.configuration.maker.BluememConfiguration;
import io.github.avishek.bluemem.configuration.model.Node;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.exception.BlueMemConfigurationException;
import io.github.avishek.bluemem.specification.BlueMemConverter;
import io.github.avishek.bluemem.specification.BlueMemMasterClusterSpecification;

@Component
public class BlueMemMasterClusterImplementation implements BlueMemMasterClusterSpecification<String, String>{

	@Autowired
	private BluememConfiguration bluememConfiguration;
	
	@Autowired
	private BlueMemAgent blueMemAgent;
	
	@Autowired
	private BlueMemConverter blueMemConverter;
	
	@Override
	public long getTimestamp() throws MalformedURLException, IOException {
		if(!StringUtils.isEmpty(bluememConfiguration.getRootNode().getUrl())) { //First Look Direct to Root Node
			String response = blueMemAgent.get(bluememConfiguration.getTimeStampURL(bluememConfiguration.getRootNode().getUrl()));
			
			return Long.parseLong(response);
		} else if (!StringUtils.isEmpty(bluememConfiguration.getParentNode().getUrl())) { //Second Look to Parent Node
			String response = blueMemAgent.get(bluememConfiguration.getTimeStampURL(bluememConfiguration.getParentNode().getUrl()));
			
			return Long.parseLong(response);
		} else {
			throw new BlueMemConfigurationException("Root or Parent Node has not been configured");
		}
	}
	
	@Override
	public void traceNodes() {
		final Collection<String[]> URLs = new LinkedList<>();
		
		if(!bluememConfiguration.isRoot()) {
			if(Objects.isNull(bluememConfiguration.getParentNode())) {
				throw new BlueMemConfigurationException("Orphan Node :: Configure Parent Node");
			} else {
				URLs.add(preparePingURL(bluememConfiguration.getParentNode()));
			}
		}

		if(!CollectionUtils.isEmpty(bluememConfiguration.getClusters())) {
			bluememConfiguration.getClusters().forEach(cluster -> {
				URLs.add(preparePingURL(cluster));
			});
		}
		
		final Collection<Callable<String>> callables = new LinkedList<>();
		
		URLs.forEach(url -> {
			callables.add(() -> {
				try {
					String response = blueMemAgent.get(url[1]);
					
					if("pong".equals(response)) {
						return url[0] + " is connected";
					} else {
						return url[0] + " is not connected";
					}
				} catch (Exception e) {
					return url[0] + " is not connected :: Reason : " + e.getMessage();
				}
			});
		});
		
		if(!CollectionUtils.isEmpty(callables)) {
			final ExecutorService executorService =  Executors.newFixedThreadPool(callables.size());
			try {
				final List<Future<String>> results = executorService.invokeAll(callables);
				System.out.println("######################## Master Clusters Connection ########################");
				results.stream().map(result -> {
					try {
						return result.get();
					} catch (InterruptedException e) {
						return e.getMessage();
					} catch (ExecutionException e) {
						return e.getMessage();
					}
				}).forEach(System.out::println);
				System.out.println("############################################################################");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void callForPut(Tupple<String, String> tupple) {
		final Collection<String[]> URLs = new LinkedList<>();
		
		if(!bluememConfiguration.isRoot()) {
			if(Objects.isNull(bluememConfiguration.getParentNode())) {
				throw new BlueMemConfigurationException("Orphan Node :: Configure Parent Node");
			} else if(!StringUtils.equals(tupple.getSender(), bluememConfiguration.getParentNode().getName())) {
				URLs.add(prepareBlueMemURL(bluememConfiguration.getParentNode(), null));
			}
		}

		if(!CollectionUtils.isEmpty(bluememConfiguration.getClusters())) {
			bluememConfiguration.getClusters().forEach(cluster -> {
				if(!StringUtils.equals(tupple.getSender(), cluster.getName())) {
					URLs.add(prepareBlueMemURL(cluster, null));
				}
			});
		}
		
		final Collection<Callable<String>> callables = new LinkedList<>();
		
		URLs.forEach(url -> {
			callables.add(() -> {
				final String payLoad = blueMemConverter.toPayload(tupple);
				
				try {
					String response = blueMemAgent.post(url[1], payLoad);
					
					if("Success".equals(response)) {
						return url[0] + " PUT :: " + payLoad;
					} else {
						return url[0] + "Could Not PUT :: " + payLoad + ":: Reason : " + response;
					}
				} catch (Exception e) {
					return url[0] + "Could Not PUT :: " + payLoad + ":: Reason : " + e.getMessage();
				}
			});
		});
		
		if(!CollectionUtils.isEmpty(callables)) {
			final ExecutorService executorService =  Executors.newFixedThreadPool(callables.size());
			try {
				final List<Future<String>> results = executorService.invokeAll(callables);
				System.out.println("######################## Master Clusters PUT Operation ########################");
				results.stream().map(result -> {
					try {
						return result.get();
					} catch (InterruptedException e) {
						return e.getMessage();
					} catch (ExecutionException e) {
						return e.getMessage();
					}
				}).forEach(System.out::println);
				System.out.println("############################################################################");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void callForDelete(Tupple<String, String> tupple) {
		final Collection<String[]> URLs = new LinkedList<>();
		
		if(!bluememConfiguration.isRoot()) {
			if(Objects.isNull(bluememConfiguration.getParentNode())) {
				throw new BlueMemConfigurationException("Orphan Node :: Configure Parent Node");
			} else {
				URLs.add(prepareBlueMemURL(bluememConfiguration.getParentNode(), tupple.getKey()));
			}
		}

		if(!CollectionUtils.isEmpty(bluememConfiguration.getClusters())) {
			bluememConfiguration.getClusters().forEach(cluster -> {
				if(!StringUtils.equals(tupple.getSender(), cluster.getName())) {
					URLs.add(prepareBlueMemURL(cluster, tupple.getKey()));
				}
			});
		}
		
		final Collection<Callable<String>> callables = new LinkedList<>();
		
		URLs.forEach(url -> {
			callables.add(() -> {
				final String payLoad = blueMemConverter.toPayload(tupple);
				try {
					final String response = blueMemAgent.delete(url[1], payLoad);
					
					if("Success".equals(response)) {
						return url[0] + " DELETED :: " + payLoad;
					} else {
						return url[0] + "Could Not DELETE :: " + payLoad + ":: Reason : " + response;
					}
				} catch (Exception e) {
					return url[0] + "Could Not PUT :: " + payLoad + ":: Reason : " + e.getMessage();
				}
			});
		});
		
		if(!CollectionUtils.isEmpty(callables)) {
			final ExecutorService executorService =  Executors.newFixedThreadPool(callables.size());
			try {
				final List<Future<String>> results = executorService.invokeAll(callables);
				System.out.println("######################## Master Clusters DELETE Operation ########################");
				results.stream().map(result -> {
					try {
						return result.get();
					} catch (InterruptedException e) {
						return e.getMessage();
					} catch (ExecutionException e) {
						return e.getMessage();
					}
				}).forEach(System.out::println);
				System.out.println("############################################################################");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String[] prepareBlueMemURL(Node node, String key) {
		return new String[]{node.getName(), bluememConfiguration.getBlueMemURL(node.getUrl(), key)};
	}
	
	private String[] preparePingURL(Node node) {
		return new String[]{node.getName(), bluememConfiguration.getPingURL(node.getUrl(), bluememConfiguration.getNodeName())};
	}
}
