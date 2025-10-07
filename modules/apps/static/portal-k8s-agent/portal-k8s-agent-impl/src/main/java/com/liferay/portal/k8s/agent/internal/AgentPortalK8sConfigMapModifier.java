/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal;

import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.InMemoryOnlyConfigurationThreadLocal;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.k8s.agent.configuration.PortalK8sAgentConfiguration;
import com.liferay.portal.k8s.agent.custodian.VirtualInstanceCustodian;
import com.liferay.portal.k8s.agent.internal.thread.local.AgentPortalK8sThreadLocal;
import com.liferay.portal.k8s.agent.mutator.PortalK8sConfigurationPropertiesMutator;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerEventListener;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;

import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.felix.configurator.impl.json.BinUtil;
import org.apache.felix.configurator.impl.json.BinaryManager;
import org.apache.felix.configurator.impl.json.JSONUtil;
import org.apache.felix.configurator.impl.model.ConfigurationFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.k8s.agent.configuration.PortalK8sAgentConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "portalK8sConfigurationPropertiesMutators.cardinality.minimum:Integer=3",
	service = {PortalK8sConfigMapModifier.class, VirtualInstanceCustodian.class}
)
public class AgentPortalK8sConfigMapModifier
	implements PortalK8sConfigMapModifier, VirtualInstanceCustodian {

	@Activate
	public AgentPortalK8sConfigMapModifier(
			BundleContext bundleContext,
			@Reference ClusterExecutor clusterExecutor,
			@Reference ClusterMasterExecutor clusterMasterExecutor,
			@Reference ConfigurationAdmin configurationAdmin,
			@Reference(
				target = "(config.plugin.id=org.apache.felix.configadmin.plugin.interpolation)"
			)
			ConfigurationPlugin configurationPlugin,
			@Reference(
				name = "portalK8sConfigurationPropertiesMutators",
				policyOption = ReferencePolicyOption.GREEDY
			)
			List
				<PortalK8sConfigurationPropertiesMutator>
					portalK8sConfigurationPropertiesMutators,
			Map<String, Object> properties)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Initializing K8s agent with " + properties);
		}

		_clusterExecutor = clusterExecutor;
		_clusterMasterExecutor = clusterMasterExecutor;
		_configurationAdmin = configurationAdmin;
		_portalK8sConfigurationPropertiesMutators =
			portalK8sConfigurationPropertiesMutators;

		_bundle = bundleContext.getBundle();
		_portalK8sAgentConfiguration = ConfigurableUtil.createConfigurable(
			PortalK8sAgentConfiguration.class, properties);
		_scheduledExecutorService =
			Executors.newSingleThreadScheduledExecutor();

		_kubernetesClient = new DefaultKubernetesClient(
			_toConfig(_portalK8sAgentConfiguration));

		_sharedIndexInformer = _toSharedIndexInformer(
			_kubernetesClient, _portalK8sAgentConfiguration);

		if (_log.isInfoEnabled()) {
			_log.info("Initialized K8s agent");
		}
	}

	@Override
	public void clean(List<String> companyWebIds) {
		ConfigMapList configMapList = _kubernetesClient.configMaps(
		).withLabel(
			"lxc.liferay.com/metadataType", "dxp"
		).list();

		for (ConfigMap configMap : configMapList.getItems()) {
			ObjectMeta objectMeta = configMap.getMetadata();

			Map<String, String> labels = objectMeta.getLabels();

			String virtualInstanceId = labels.get(
				"dxp.lxc.liferay.com/virtualInstanceId");

			if (Validator.isNull(virtualInstanceId) ||
				!companyWebIds.contains(virtualInstanceId)) {

				_kubernetesClient.configMaps(
				).delete(
					configMap
				);

				if (_log.isDebugEnabled()) {
					_log.debug("Deleted custodian " + configMap);
				}
			}
		}
	}

	@Override
	public Result modifyConfigMap(
		Consumer<ConfigMapModel> configMapModelConsumer, String configMapName) {

		if (_portalK8sAgentConfiguration.debounceDelayMillis() <= 0) {
			return _modifyConfigMap(configMapModelConsumer, configMapName);
		}

		if (_log.isInfoEnabled()) {
			_log.info("Schedule modify config map " + configMapName);
		}

		_configMapModelConsumers.merge(
			configMapName, configMapModelConsumer, Consumer::andThen);

		_scheduleModifyConfigMap(configMapName);

		return Result.BUFFERED;
	}

	@Deactivate
	protected void deactivate() {
		if (_log.isInfoEnabled()) {
			_log.info("Deactivating K8s agent");
		}

		for (String configMapName : _configMapModelConsumers.keySet()) {
			_flushModifyConfigMap(configMapName);
		}

		_sharedIndexInformer.close();

		_kubernetesClient.close();

		_scheduledExecutorService.shutdown();

		if (_log.isInfoEnabled()) {
			_log.info("Deactivated K8s agent");
		}
	}

	private void _add(ConfigMap configMap) {
		if (_log.isInfoEnabled()) {
			_log.info("Adding config map " + configMap.toString());
		}

		Map<String, String> data = configMap.getData();

		if (data == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Data is null for config map " + configMap);
			}

			return;
		}

		for (Map.Entry<String, String> entry : data.entrySet()) {
			try {
				_processConfigurations(
					configMap, entry.getKey(), entry.getValue());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private Map<String, String> _copy(Map<String, String> annotations) {
		return new TreeMap<>(_getMap(annotations));
	}

	private void _delete(ConfigMap configMap) {
		if (_log.isInfoEnabled()) {
			_log.info("Deleting config map " + configMap);
		}

		Map<String, String> data = configMap.getData();

		if (data == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Data is null for config map " + configMap);
			}

			return;
		}

		Configuration[] configurations = null;

		try {
			ObjectMeta objectMeta = configMap.getMetadata();

			configurations = _configurationAdmin.listConfigurations(
				"(.k8s.config.uid=" + objectMeta.getUid() + ")");
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			try {
				configuration.delete();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private Result _flushModifyConfigMap(String configMapName) {
		if (_log.isInfoEnabled()) {
			_log.info("Flushing modify config map " + configMapName);
		}

		Consumer<ConfigMapModel> configMapModelConsumer =
			_configMapModelConsumers.remove(configMapName);

		if (configMapModelConsumer == null) {
			_futures.remove(configMapName);

			return Result.UNCHANGED;
		}

		Result result = _modifyConfigMap(configMapModelConsumer, configMapName);

		_futures.remove(configMapName);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Flushed config map ", configMapName, " ", result));
		}

		return result;
	}

	private Configuration _getConfiguration(String pid) throws Exception {
		if (pid.endsWith(_FILE_EXTENSION)) {
			pid = pid.substring(0, pid.length() - _FILE_EXTENSION.length());
		}

		int index = pid.indexOf(CharPool.TILDE);

		if (index <= 0) {
			index = pid.indexOf(CharPool.UNDERLINE);

			if (index <= 0) {
				index = pid.indexOf(CharPool.DASH);
			}
		}

		if (index > 0) {
			String name = pid.substring(index + 1);

			pid = pid.substring(0, index);

			return _configurationAdmin.getFactoryConfiguration(
				pid, name, StringPool.QUESTION);
		}

		return _configurationAdmin.getConfiguration(pid, StringPool.QUESTION);
	}

	private Map<String, String> _getMap(Map<String, String> map) {
		if (map == null) {
			map = new TreeMap<>();
		}

		return map;
	}

	private String _getVirtualInstancePid(
		org.apache.felix.configurator.impl.model.Config config,
		String virtualInstanceId) {

		String pid = config.getPid();

		String factoryPid = ConfigurationFactoryUtil.getFactoryPidFromPid(pid);

		if (factoryPid == null) {
			return pid;
		}

		return StringBundler.concat(pid, "/", virtualInstanceId);
	}

	private Result _modifyConfigMap(
		Consumer<ConfigMapModel> configMapModelConsumer, String configMapName) {

		if (_log.isInfoEnabled()) {
			_log.info("Modify config map " + configMapName);
		}

		if (_clusterMasterExecutor.isEnabled()) {
			if (_log.isDebugEnabled()) {
				ClusterNode localClusterNode =
					_clusterExecutor.getLocalClusterNode();

				_log.debug(
					StringBundler.concat(
						"Current node ", localClusterNode.getClusterNodeId(),
						" is ",
						!_clusterMasterExecutor.isMaster() ? "not " : "",
						"master"));
			}

			if (AgentPortalK8sThreadLocal.isExecuteOnCurrentNode()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Execute on current node");
				}
			}
			else if (!_clusterMasterExecutor.isMaster()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Execute on master only");
				}

				return Result.UNCHANGED;
			}
		}

		Objects.requireNonNull(
			configMapModelConsumer, "Config map model consumer is null");

		_validateConfigMapName(configMapName);

		ConfigMap configMap = _kubernetesClient.configMaps(
		).inNamespace(
			_portalK8sAgentConfiguration.namespace()
		).withName(
			configMapName
		).get();

		if (configMap != null) {
			ObjectMeta objectMeta = configMap.getMetadata();

			Map<String, String> originalAnnotations = _getMap(
				objectMeta.getAnnotations());

			Map<String, String> annotations = _copy(originalAnnotations);

			Map<String, String> originalBinaryData = _getMap(
				configMap.getBinaryData());

			Map<String, String> binaryData = _copy(originalBinaryData);

			Map<String, String> originalData = _getMap(configMap.getData());

			Map<String, String> data = _copy(originalData);

			Map<String, String> originalLabels = _getMap(
				objectMeta.getLabels());

			Map<String, String> labels = _copy(originalLabels);

			configMapModelConsumer.accept(
				new ConfigMapModel() {

					@Override
					public Map<String, String> annotations() {
						return annotations;
					}

					@Override
					public Map<String, String> binaryData() {
						return binaryData;
					}

					@Override
					public Map<String, String> data() {
						return data;
					}

					@Override
					public Map<String, String> labels() {
						return labels;
					}

				});

			if (binaryData.isEmpty() && data.isEmpty()) {
				_kubernetesClient.configMaps(
				).delete(
					configMap
				);

				if (_log.isInfoEnabled()) {
					_log.info("Deleted " + configMap);
				}

				return Result.DELETED;
			}
			else if (!Objects.equals(annotations, originalAnnotations) ||
					 !Objects.equals(binaryData, originalBinaryData) ||
					 !Objects.equals(data, originalData) ||
					 !Objects.equals(labels, originalLabels)) {

				_validateLabels(configMapName, labels);

				configMap.setData(binaryData);
				configMap.setData(data);

				objectMeta.setAnnotations(annotations);
				objectMeta.setLabels(labels);

				configMap = _kubernetesClient.configMaps(
				).withName(
					configMapName
				).createOrReplace(
					configMap
				);

				if (_log.isInfoEnabled()) {
					_log.info("Updated " + configMap);
				}

				return Result.UPDATED;
			}

			if (_log.isInfoEnabled()) {
				_log.info("Unchanged " + configMap);
			}

			return Result.UNCHANGED;
		}

		Map<String, String> annotations = new TreeMap<>();
		Map<String, String> binaryData = new TreeMap<>();
		Map<String, String> data = new TreeMap<>();
		Map<String, String> labels = new TreeMap<>();

		configMapModelConsumer.accept(
			new ConfigMapModel() {

				@Override
				public Map<String, String> annotations() {
					return annotations;
				}

				@Override
				public Map<String, String> binaryData() {
					return binaryData;
				}

				@Override
				public Map<String, String> data() {
					return data;
				}

				@Override
				public Map<String, String> labels() {
					return labels;
				}

			});

		if (binaryData.isEmpty() && data.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Config map does not exist and no data was supplied ",
						"for ", configMapName, " resulting in no change"));
			}

			return Result.UNCHANGED;
		}

		_validateLabels(configMapName, labels);

		ConfigMapBuilder configMapBuilder = new ConfigMapBuilder();

		configMap = configMapBuilder.withNewMetadata(
		).withNamespace(
			_portalK8sAgentConfiguration.namespace()
		).withName(
			configMapName
		).addToAnnotations(
			annotations
		).addToLabels(
			labels
		).endMetadata(
		).addToBinaryData(
			binaryData
		).addToData(
			data
		).build();

		configMap = _kubernetesClient.configMaps(
		).withName(
			configMapName
		).createOrReplace(
			configMap
		);

		if (_log.isInfoEnabled()) {
			_log.info("Created " + configMap);
		}

		return Result.CREATED;
	}

	private void _processConfiguration(
			org.apache.felix.configurator.impl.model.Config config,
			ObjectMeta objectMeta)
		throws Exception {

		Map<String, String> labels = _getMap(objectMeta.getLabels());

		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if (virtualInstanceId == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map labels must contain the key ",
					"\"dxp.lxc.liferay.com/virtualInstanceId\" whose value is ",
					"the web ID of the target virtual instance"));
		}

		// LPS-172217

		String virtualInstancePid = _getVirtualInstancePid(
			config, virtualInstanceId);

		try {
			InMemoryOnlyConfigurationThreadLocal.setInMemoryOnly(true);

			Configuration configuration = null;

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(.k8s.config.key=", virtualInstancePid, ")"));

			if (ArrayUtil.isNotEmpty(configurations)) {
				configuration = configurations[0];

				Dictionary<String, Object> properties =
					configuration.getProperties();

				if (Objects.equals(
						properties.get(".k8s.config.resource.version"),
						objectMeta.getResourceVersion())) {

					if (_log.isInfoEnabled()) {
						_log.info(
							"Configuration and Kubernetes resource versions " +
								"are identical");
					}

					return;
				}
			}
			else {
				configuration = _getConfiguration(virtualInstancePid);
			}

			Set<Configuration.ConfigurationAttribute> configurationAttributes =
				configuration.getAttributes();

			if (configurationAttributes.contains(
					Configuration.ConfigurationAttribute.READ_ONLY)) {

				configuration.removeAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY);
			}

			Map<String, String> annotations = _getMap(
				objectMeta.getAnnotations());
			Dictionary<String, Object> properties = config.getProperties();

			for (PortalK8sConfigurationPropertiesMutator
					portalK8sConfigurationPropertiesMutator :
						_portalK8sConfigurationPropertiesMutators) {

				portalK8sConfigurationPropertiesMutator.
					mutateConfigurationProperties(
						annotations, labels, properties);
			}

			properties.put(".k8s.config.key", virtualInstancePid);
			properties.put(
				".k8s.config.resource.version",
				objectMeta.getResourceVersion());
			properties.put(".k8s.config.uid", objectMeta.getUid());

			if (_log.isInfoEnabled()) {
				_log.info("Processed configuration " + properties);
			}

			configuration.updateIfDifferent(properties);

			configuration.addAttributes(
				Configuration.ConfigurationAttribute.READ_ONLY);
		}
		finally {
			InMemoryOnlyConfigurationThreadLocal.setInMemoryOnly(false);
		}
	}

	private void _processConfigurations(
			ConfigMap configMap, String fileName, String json)
		throws Exception {

		if (!fileName.endsWith(_FILE_EXTENSION)) {
			throw new IllegalArgumentException("Invalid file " + fileName);
		}

		JSONUtil.Report report = new JSONUtil.Report();

		BinaryManager binaryManager = new BinaryManager(
			new BinUtil.ResourceProvider() {

				@Override
				public Enumeration<URL> findEntries(
					String path, String pattern) {

					return Collections.emptyEnumeration();
				}

				@Override
				public long getBundleId() {
					return _bundle.getBundleId();
				}

				@Override
				public URL getEntry(String path) {
					return null;
				}

				@Override
				public String getIdentifier() {
					return fileName;
				}

			},
			report);

		ConfigurationFile configurationFile = JSONUtil.readJSON(
			binaryManager, fileName, new URL("file", null, fileName),
			_bundle.getBundleId(), json, report);

		for (String error : report.errors) {
			_log.error(error);
		}

		for (String warning : report.warnings) {
			if (_log.isWarnEnabled()) {
				_log.warn(warning);
			}
		}

		if (configurationFile == null) {
			return;
		}

		for (org.apache.felix.configurator.impl.model.Config config :
				configurationFile.getConfigurations()) {

			try {
				_processConfiguration(config, configMap.getMetadata());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private void _run(Runnable runnable) {
		ClusterNode localClusterNode = _clusterExecutor.getLocalClusterNode();

		if (_clusterMasterExecutor.isEnabled() &&
			!_clusterMasterExecutor.isMaster()) {

			_scheduledExecutorService.schedule(
				() -> {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Defer execution on cluster node " +
								localClusterNode.getClusterNodeId());
					}

					runnable.run();
				},
				_portalK8sAgentConfiguration.deferSecondaryNodeMillis(),
				TimeUnit.MILLISECONDS);
		}
		else {
			if ((localClusterNode != null) && _log.isDebugEnabled()) {
				_log.debug(
					"Execute on master node " +
						localClusterNode.getClusterNodeId());
			}

			runnable.run();
		}
	}

	private Future<Result> _scheduleModifyConfigMap(String configMapName) {
		_lock.lock();

		try {
			Future<Result> future = _futures.remove(configMapName);

			if (future != null) {
				future.cancel(false);
			}

			future = _scheduledExecutorService.schedule(
				() -> {
					try {
						return _flushModifyConfigMap(configMapName);
					}
					catch (Exception exception) {
						_log.error(
							"Unable to flush modify config map " +
								configMapName,
							exception);

						return Result.UNCHANGED;
					}
				},
				_portalK8sAgentConfiguration.debounceDelayMillis(),
				TimeUnit.MILLISECONDS);

			_futures.put(configMapName, future);

			return future;
		}
		finally {
			_lock.unlock();
		}
	}

	private Config _toConfig(
		PortalK8sAgentConfiguration portalK8sAgentConfiguration) {

		Config config = Config.empty();

		Map<Integer, String> errorMessages = config.getErrorMessages();

		errorMessages.put(401, _ERROR_MESSAGE);
		errorMessages.put(403, _ERROR_MESSAGE);

		config.setCaCertData(portalK8sAgentConfiguration.caCertData());

		String protocol = Http.HTTP;

		if (portalK8sAgentConfiguration.apiServerSSL()) {
			protocol = Http.HTTPS;
		}

		config.setMasterUrl(
			StringBundler.concat(
				protocol, Http.PROTOCOL_DELIMITER,
				portalK8sAgentConfiguration.apiServerHost(), StringPool.COLON,
				portalK8sAgentConfiguration.apiServerPort(), StringPool.SLASH));

		config.setNamespace(portalK8sAgentConfiguration.namespace());
		config.setOauthToken(portalK8sAgentConfiguration.saToken());

		Config.configFromSysPropsOrEnvVars(config);

		return config;
	}

	private SharedIndexInformer<ConfigMap> _toSharedIndexInformer(
		KubernetesClient kubernetesClient,
		PortalK8sAgentConfiguration portalK8sAgentConfiguration) {

		SharedInformerFactory sharedInformerFactory =
			kubernetesClient.informers();

		sharedInformerFactory.addSharedInformerEventListener(
			new SharedInformerEventListener() {

				@Override
				public void onException(Exception exception) {
					_log.error(exception);
				}

			});

		return kubernetesClient.configMaps(
		).inNamespace(
			portalK8sAgentConfiguration.namespace()
		).withLabel(
			portalK8sAgentConfiguration.labelSelector()
		).inform(
			new ResourceEventHandler<ConfigMap>() {

				@Override
				public void onAdd(ConfigMap configMap) {
					_run(() -> _add(configMap));
				}

				@Override
				public void onDelete(
					ConfigMap configMap, boolean deletedFinalStateUnknown) {

					_run(() -> _delete(configMap));
				}

				@Override
				public void onUpdate(
					ConfigMap oldConfigMap, ConfigMap newConfigMap) {

					_run(() -> _update(oldConfigMap, newConfigMap));
				}

			}
		);
	}

	private void _update(ConfigMap oldConfigMap, ConfigMap newConfigMap) {
		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Updating config map ", oldConfigMap, " to ",
					newConfigMap));
		}

		Map<String, String> data = newConfigMap.getData();
		ObjectMeta objectMeta = newConfigMap.getMetadata();

		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				try {
					_processConfigurations(
						newConfigMap, entry.getKey(), entry.getValue());
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}

		Configuration[] configurations = null;

		try {
			ObjectMeta oldObjectMeta = oldConfigMap.getMetadata();

			if (Objects.equals(
					oldObjectMeta.getResourceVersion(),
					objectMeta.getResourceVersion())) {

				return;
			}

			configurations = _configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(&(.k8s.config.resource.version=",
					oldObjectMeta.getResourceVersion(), ")(.k8s.config.uid=",
					objectMeta.getUid(), "))"));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			try {
				configuration.delete();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private void _validateConfigMapName(String configMapName) {
		Objects.requireNonNull(configMapName, "Config map name is null");

		if (!configMapName.endsWith("-lxc-dxp-metadata") &&
			!configMapName.endsWith("-lxc-ext-init-metadata")) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map name ", configMapName,
					" does not follow a recognized pattern"));
		}
	}

	private void _validateLabels(
		String configMapName, Map<String, String> labels) {

		_validateConfigMapName(configMapName);

		String metadataType = labels.get("lxc.liferay.com/metadataType");

		if ((metadataType == null) ||
			(!Objects.equals(metadataType, "dxp") &&
			 !Objects.equals(metadataType, "ext-init"))) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map labels must contain the key ",
					"\"lxc.liferay.com/metadataType\" with a value of \"dxp\" ",
					"or \"ext-init\""));
		}

		String virtualInstanceId = labels.get(
			"dxp.lxc.liferay.com/virtualInstanceId");

		if (virtualInstanceId == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Config map labels must contain the key ",
					"\"dxp.lxc.liferay.com/virtualInstanceId\" whose value is ",
					"the web ID of the virtual instance from which the ",
					"configuration originated"));
		}

		// <virtualInstanceId>-lxc-dxp-metadata

		if (configMapName.endsWith("-lxc-dxp-metadata") &&
			!Objects.equals(
				virtualInstanceId.concat("-lxc-dxp-metadata"), configMapName)) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"A config map name with the suffix \"-lxc-dxp-metadata\" ",
					"must begin with the value of the label ",
					"\"dxp.lxc.liferay.com/virtualInstanceId\" followed by ",
					"\"-lxc-dxp-metadata\""));
		}

		// <serviceId>-<virtualInstanceId>-lxc-ext-init-metadata

		else if (configMapName.endsWith("-lxc-ext-init-metadata")) {
			String serviceId = labels.get("ext.lxc.liferay.com/serviceId");

			if (serviceId == null) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"A config map with the suffix ",
						"\"-lxc-ext-init-metadata\" must have a label with ",
						"the key \"ext.lxc.liferay.com/serviceId\" whose ",
						"value is the target service ID"));
			}

			if (!Objects.equals(
					configMapName,
					StringBundler.concat(
						serviceId, "-", virtualInstanceId,
						"-lxc-ext-init-metadata"))) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"A config map name with suffix ",
						"\"-lxc-ext-init-metadata\" must begin with the value ",
						"of the label \"ext.lxc.liferay.com/serviceId\" ",
						"followed by a \"-\" and then the value of the label ",
						"\"dxp.lxc.liferay.com/virtualInstanceId\" followed ",
						"by \"-lxc-ext-init-metadata\""));
			}
		}
	}

	private static final String _ERROR_MESSAGE =
		"Configured service account does not have access. Service account " +
			"may have been revoked.";

	private static final String _FILE_EXTENSION =
		".client-extension-config.json";

	private static final Log _log = LogFactoryUtil.getLog(
		AgentPortalK8sConfigMapModifier.class);

	private final Bundle _bundle;
	private final ClusterExecutor _clusterExecutor;
	private final ClusterMasterExecutor _clusterMasterExecutor;
	private final Map<String, Consumer<ConfigMapModel>>
		_configMapModelConsumers = new ConcurrentHashMap<>();
	private final ConfigurationAdmin _configurationAdmin;
	private final Map<String, Future<Result>> _futures =
		new ConcurrentHashMap<>();
	private final KubernetesClient _kubernetesClient;
	private final Lock _lock = new ReentrantLock();
	private final PortalK8sAgentConfiguration _portalK8sAgentConfiguration;
	private final List<PortalK8sConfigurationPropertiesMutator>
		_portalK8sConfigurationPropertiesMutators;
	private final ScheduledExecutorService _scheduledExecutorService;
	private final SharedIndexInformer<ConfigMap> _sharedIndexInformer;

}