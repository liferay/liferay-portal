/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.configuration;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.configuration.OperationMode;
import com.liferay.portal.search.elasticsearch7.configuration.RESTClientLoggerLevel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Bryan Engler
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	service = ElasticsearchConfigurationWrapper.class
)
public class ElasticsearchConfigurationWrapper
	implements Comparator<ElasticsearchConfigurationObserver> {

	public String additionalConfigurations() {
		return _elasticsearchConfiguration.additionalConfigurations();
	}

	public String additionalIndexConfigurations() {
		return _elasticsearchConfiguration.additionalIndexConfigurations();
	}

	public String additionalTypeMappings() {
		return _elasticsearchConfiguration.additionalTypeMappings();
	}

	public boolean authenticationEnabled() {
		return _elasticsearchConfiguration.authenticationEnabled();
	}

	public boolean bootstrapMlockAll() {
		return _elasticsearchConfiguration.bootstrapMlockAll();
	}

	public String clusterName() {
		return _elasticsearchConfiguration.clusterName();
	}

	@Override
	public int compare(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver1,
		ElasticsearchConfigurationObserver
			elasticsearchConfigurationObserver2) {

		if (elasticsearchConfigurationObserver1.getPriority() >
				elasticsearchConfigurationObserver2.getPriority()) {

			return 1;
		}
		else if (elasticsearchConfigurationObserver1.getPriority() ==
					elasticsearchConfigurationObserver2.getPriority()) {

			return 0;
		}

		return -1;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public int embeddedHttpPort() {
		return _elasticsearchConfiguration.embeddedHttpPort();
	}

	public String httpCORSAllowOrigin() {
		return _elasticsearchConfiguration.httpCORSAllowOrigin();
	}

	public String httpCORSConfigurations() {
		return _elasticsearchConfiguration.httpCORSConfigurations();
	}

	public boolean httpCORSEnabled() {
		return _elasticsearchConfiguration.httpCORSEnabled();
	}

	public boolean httpSSLEnabled() {
		return _elasticsearchConfiguration.httpSSLEnabled();
	}

	public int indexMaxResultWindow() {
		return _elasticsearchConfiguration.indexMaxResultWindow();
	}

	public String indexNamePrefix() {
		return _elasticsearchConfiguration.indexNamePrefix();
	}

	public String indexNumberOfReplicas() {
		return _elasticsearchConfiguration.indexNumberOfReplicas();
	}

	public String indexNumberOfShards() {
		return _elasticsearchConfiguration.indexNumberOfShards();
	}

	public boolean isDevelopmentModeEnabled() {
		return !isProductionModeEnabled();
	}

	public boolean isProductionModeEnabled() {
		if (productionModeEnabled()) {
			return true;
		}

		OperationMode operationMode = operationMode();

		if (operationMode == OperationMode.REMOTE) {
			return true;
		}

		return false;
	}

	public boolean logExceptionsOnly() {
		return _elasticsearchConfiguration.logExceptionsOnly();
	}

	public int maxConnections() {
		return _elasticsearchConfiguration.maxConnections();
	}

	public int maxConnectionsPerRoute() {
		return _elasticsearchConfiguration.maxConnectionsPerRoute();
	}

	public String minimumRequiredNodeVersion() {
		return _elasticsearchConfiguration.minimumRequiredNodeVersion();
	}

	public String networkBindHost() {
		return _elasticsearchConfiguration.networkBindHost();
	}

	public String networkHost() {
		return _elasticsearchConfiguration.networkHost();
	}

	public String[] networkHostAddresses() {
		return _elasticsearchConfiguration.networkHostAddresses();
	}

	public String networkPublishHost() {
		return _elasticsearchConfiguration.networkPublishHost();
	}

	public String nodeName() {
		return _elasticsearchConfiguration.nodeName();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public OperationMode operationMode() {
		return _elasticsearchConfiguration.operationMode();
	}

	public String overrideTypeMappings() {
		return _elasticsearchConfiguration.overrideTypeMappings();
	}

	public String password() {
		return _elasticsearchConfiguration.password();
	}

	public boolean productionModeEnabled() {
		return _elasticsearchConfiguration.productionModeEnabled();
	}

	public String proxyHost() {
		return _elasticsearchConfiguration.proxyHost();
	}

	public String proxyPassword() {
		return _elasticsearchConfiguration.proxyPassword();
	}

	public int proxyPort() {
		return _elasticsearchConfiguration.proxyPort();
	}

	public String proxyUserName() {
		return _elasticsearchConfiguration.proxyUserName();
	}

	public void register(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		_elasticsearchConfigurationObservers.add(
			elasticsearchConfigurationObserver);
	}

	public String remoteClusterConnectionId() {
		return _elasticsearchConfiguration.remoteClusterConnectionId();
	}

	public RESTClientLoggerLevel restClientLoggerLevel() {
		return _elasticsearchConfiguration.restClientLoggerLevel();
	}

	public boolean sidecarDebug() {
		return _elasticsearchConfiguration.sidecarDebug();
	}

	public String sidecarDebugSettings() {
		return _elasticsearchConfiguration.sidecarDebugSettings();
	}

	public long sidecarHeartbeatInterval() {
		return _elasticsearchConfiguration.sidecarHeartbeatInterval();
	}

	public String sidecarHome() {
		return _elasticsearchConfiguration.sidecarHome();
	}

	public String sidecarHttpPort() {
		return _elasticsearchConfiguration.sidecarHttpPort();
	}

	public String[] sidecarJVMOptions() {
		if (_propsMap.containsKey("sidecarJVMOptions")) {
			return _propsElasticsearchConfiguration.sidecarJVMOptions();
		}

		return _elasticsearchConfiguration.sidecarJVMOptions();
	}

	public String[] sidecarModuleNames() {
		return _elasticsearchConfiguration.sidecarModuleNames();
	}

	public long sidecarShutdownTimeout() {
		return _elasticsearchConfiguration.sidecarShutdownTimeout();
	}

	public boolean trackTotalHits() {
		return _elasticsearchConfiguration.trackTotalHits();
	}

	public String transportTcpPort() {
		return _elasticsearchConfiguration.transportTcpPort();
	}

	public String truststorePassword() {
		return _elasticsearchConfiguration.truststorePassword();
	}

	public String truststorePath() {
		return _elasticsearchConfiguration.truststorePath();
	}

	public String truststoreType() {
		return _elasticsearchConfiguration.truststoreType();
	}

	public void unregister(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		_elasticsearchConfigurationObservers.remove(
			elasticsearchConfigurationObserver);
	}

	public String userName() {
		return _elasticsearchConfiguration.username();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> map) {
		Map<String, Object> propsMap = _getPropsMap(
			_PROPS_KEYS, ElasticsearchConfiguration.class);

		_elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			ElasticsearchConfiguration.class, map);
		_propsElasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			ElasticsearchConfiguration.class, propsMap);
		_propsMap = propsMap;

		_elasticsearchConfigurationObservers.forEach(
			ElasticsearchConfigurationObserver::
				onElasticsearchConfigurationUpdate);
	}

	protected void setElasticsearchConfiguration(
		ElasticsearchConfiguration elasticsearchConfiguration) {

		_elasticsearchConfiguration = elasticsearchConfiguration;
	}

	private Map<String, Object> _getPropsMap(String[] keys, Class<?> clazz) {
		Properties properties = PropsUtil.getProperties(
			StringUtil.toLowerCase(clazz.getName()) + CharPool.PERIOD, true);

		if ((properties == null) || properties.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Object> propsMap = new HashMap<>();

		for (String key : keys) {
			String property = properties.getProperty(
				StringUtil.toLowerCase(key));

			if (!Validator.isBlank(property)) {
				propsMap.put(key, property);
			}
		}

		return propsMap;
	}

	private static final String[] _PROPS_KEYS = {"sidecarJVMOptions"};

	private volatile ElasticsearchConfiguration _elasticsearchConfiguration;
	private final Set<ElasticsearchConfigurationObserver>
		_elasticsearchConfigurationObservers = new ConcurrentSkipListSet<>();
	private volatile ElasticsearchConfiguration
		_propsElasticsearchConfiguration;
	private volatile Map<String, Object> _propsMap = Collections.emptyMap();

}