/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ccr.CrossClusterReplicationConfigurationHelper;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.helper.SearchLogHelperUtil;

import java.lang.reflect.Field;

import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import org.apache.http.HttpHost;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	service = {
		ElasticsearchClientResolver.class, ElasticsearchConnectionManager.class
	}
)
public class ElasticsearchConnectionManager
	implements ElasticsearchClientResolver, ElasticsearchConfigurationObserver {

	public void addElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection) {

		String connectionId = elasticsearchConnection.getConnectionId();

		if (connectionId == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skipping connection because connection ID is null");
			}

			return;
		}

		Supplier<ElasticsearchConnection> elasticsearchConnectionSupplier;

		if (elasticsearchConnection.isActive()) {
			FutureTask<ElasticsearchConnection> futureTask = new FutureTask<>(
				() -> {
					try {
						elasticsearchConnection.connect();
					}
					catch (RuntimeException runtimeException) {
						if (connectionId.equals(
								ConnectionConstants.SIDECAR_CONNECTION_ID)) {

							_log.error(
								StringBundler.concat(
									"Elasticsearch sidecar could not be ",
									"started. Search will be unavailable. ",
									"Manual installation of Elasticsearch and ",
									"activation of remote mode is ",
									"recommended."),
								runtimeException);
						}

						throw runtimeException;
					}

					return elasticsearchConnection;
				});

			ExecutorService executorService =
				SystemExecutorServiceUtil.getExecutorService();

			executorService.submit(futureTask);

			elasticsearchConnectionSupplier = () -> {
				try {
					return futureTask.get();
				}
				catch (Exception exception) {
					return ReflectionUtil.throwException(exception);
				}
			};
		}
		else {
			elasticsearchConnectionSupplier = () -> elasticsearchConnection;
		}

		_elasticsearchConnectionSuppliers.put(
			connectionId, elasticsearchConnectionSupplier);
	}

	@Override
	public int compareTo(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		return elasticsearchConfigurationWrapper.compare(
			this, elasticsearchConfigurationObserver);
	}

	public ElasticsearchConnection getElasticsearchConnection() {
		return getElasticsearchConnection(null, false);
	}

	public ElasticsearchConnection getElasticsearchConnection(
		boolean preferLocalCluster) {

		return getElasticsearchConnection(null, preferLocalCluster);
	}

	public ElasticsearchConnection getElasticsearchConnection(
		String connectionId) {

		Supplier<ElasticsearchConnection> elasticsearchConnectionSupplier =
			_elasticsearchConnectionSuppliers.get(connectionId);

		if (_log.isInfoEnabled()) {
			if (elasticsearchConnectionSupplier != null) {
				_log.info("Returning connection with ID: " + connectionId);
			}
			else {
				_log.info(
					"Connection not found. Returning null for ID: " +
						connectionId);
			}
		}

		if (elasticsearchConnectionSupplier == null) {
			return null;
		}

		return elasticsearchConnectionSupplier.get();
	}

	public Collection<ElasticsearchConnection> getElasticsearchConnections() {
		List<ElasticsearchConnection> elasticsearchConnections =
			new ArrayList<>();

		for (Supplier<ElasticsearchConnection> supplier :
				_elasticsearchConnectionSuppliers.values()) {

			elasticsearchConnections.add(supplier.get());
		}

		return elasticsearchConnections;
	}

	public String getLocalClusterConnectionId() {
		InetSocketAddress portalInetSocketAddress = _portalInetSocketAddress;

		CrossClusterReplicationConfigurationHelper
			currentCrossClusterReplicationConfigurationHelper =
				_crossClusterReplicationConfigurationHelperSnapshot.get();

		if (portalInetSocketAddress == null) {
			if (currentCrossClusterReplicationConfigurationHelper == null) {
				return null;
			}

			List<String> localClusterConnectionIds =
				currentCrossClusterReplicationConfigurationHelper.
					getLocalClusterConnectionIds();

			if (localClusterConnectionIds.isEmpty()) {
				return null;
			}

			return localClusterConnectionIds.get(0);
		}

		Map<String, String> localClusterConnectionConfigurations =
			currentCrossClusterReplicationConfigurationHelper.
				getLocalClusterConnectionIdsMap();

		String localClusterNodeHostName =
			portalInetSocketAddress.getHostName() + StringPool.COLON +
				portalInetSocketAddress.getPort();

		return localClusterConnectionConfigurations.get(
			localClusterNodeHostName);
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient() {
		return getRestHighLevelClient(null);
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(String connectionId) {
		return getRestHighLevelClient(connectionId, false);
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(
		String connectionId, boolean preferLocalCluster) {

		ElasticsearchConnection elasticsearchConnection =
			getElasticsearchConnection(connectionId, preferLocalCluster);

		if (elasticsearchConnection == null) {
			throw new ElasticsearchConnectionNotInitializedException(
				_getExceptionMessage(
					"Elasticsearch connection not found.", connectionId,
					preferLocalCluster));
		}

		RestHighLevelClient restHighLevelClient =
			elasticsearchConnection.getRestHighLevelClient();

		if (restHighLevelClient == null) {
			throw new ElasticsearchConnectionNotInitializedException(
				_getExceptionMessage(
					"REST high level client not found.",
					elasticsearchConnection.getConnectionId(),
					preferLocalCluster));
		}

		try {
			RestClient restClient = restHighLevelClient.getLowLevelClient();

			Class<?> clazz = restClient.getClass();

			Field blacklistField = clazz.getDeclaredField("blacklist");

			blacklistField.setAccessible(true);

			ConcurrentHashMap<HttpHost, Object> map =
				(ConcurrentHashMap<HttpHost, Object>)blacklistField.get(
					restClient);

			for (HttpHost httpHost : map.keySet()) {
				_log.error(
					"The REST client network host address " +
						httpHost.toString() + " is blacklisted");
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get REST client blacklist field", exception);
			}
		}

		return restHighLevelClient;
	}

	public boolean isCrossClusterReplicationEnabled() {
		CrossClusterReplicationConfigurationHelper
			currentCrossClusterReplicationConfigurationHelper =
				_crossClusterReplicationConfigurationHelperSnapshot.get();

		if (currentCrossClusterReplicationConfigurationHelper == null) {
			return false;
		}

		return currentCrossClusterReplicationConfigurationHelper.
			isCrossClusterReplicationEnabled();
	}

	@Override
	public void onElasticsearchConfigurationUpdate() {
		applyConfigurations();
	}

	public void removeElasticsearchConnection(String connectionId) {
		if (connectionId == null) {
			return;
		}

		Supplier<ElasticsearchConnection> elasticsearchConnectionSupplier =
			_elasticsearchConnectionSuppliers.get(connectionId);

		if (elasticsearchConnectionSupplier == null) {
			return;
		}

		ElasticsearchConnection elasticsearchConnection =
			elasticsearchConnectionSupplier.get();

		elasticsearchConnection.close();

		_elasticsearchConnectionSuppliers.remove(connectionId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			PortalInetSocketAddressEventListener.class,
			new ElasticsearchPortalInetSocketAddressEventListener(), null);

		elasticsearchConfigurationWrapper.register(this);

		applyConfigurations();
	}

	protected void applyConfigurations() {
		SearchLogHelperUtil.setRESTClientLoggerLevel(
			elasticsearchConfigurationWrapper.restClientLoggerLevel());

		if (elasticsearchConfigurationWrapper.isProductionModeEnabled()) {
			if (Validator.isBlank(
					elasticsearchConfigurationWrapper.
						remoteClusterConnectionId())) {

				addElasticsearchConnection(
					_createRemoteElasticsearchConnection());
			}
		}
		else {
			removeElasticsearchConnection(
				ConnectionConstants.REMOTE_CONNECTION_ID);
		}
	}

	protected ProxyConfig createProxyConfig() {
		ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.builder(http);

		return proxyConfigBuilder.networkAddresses(
			elasticsearchConfigurationWrapper.networkHostAddresses()
		).host(
			elasticsearchConfigurationWrapper.proxyHost()
		).password(
			elasticsearchConfigurationWrapper.proxyPassword()
		).port(
			elasticsearchConfigurationWrapper.proxyPort()
		).userName(
			elasticsearchConfigurationWrapper.proxyUserName()
		).build();
	}

	@Deactivate
	protected void deactivate() {
		elasticsearchConfigurationWrapper.unregister(this);

		for (Supplier<ElasticsearchConnection> supplier :
				_elasticsearchConnectionSuppliers.values()) {

			ElasticsearchConnection elasticsearchConnection = supplier.get();

			elasticsearchConnection.close();
		}

		_serviceRegistration.unregister();
	}

	protected ElasticsearchConnection getElasticsearchConnection(
		String connectionId, boolean preferLocalCluster) {

		if (_log.isInfoEnabled()) {
			_log.info("Connection requested for ID: " + connectionId);
		}

		if (!Validator.isBlank(connectionId)) {
			if (_log.isInfoEnabled()) {
				_log.info("Getting connection with ID: " + connectionId);
			}

			return getElasticsearchConnection(connectionId);
		}

		if (elasticsearchConfigurationWrapper.isDevelopmentModeEnabled()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Getting " + ConnectionConstants.SIDECAR_CONNECTION_ID +
						" connection");
			}

			return getElasticsearchConnection(
				ConnectionConstants.SIDECAR_CONNECTION_ID);
		}

		if (preferLocalCluster && isCrossClusterReplicationEnabled()) {
			String localClusterConnectionId = getLocalClusterConnectionId();

			if (localClusterConnectionId != null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Getting local cluster connection with ID: " +
							localClusterConnectionId);
				}

				return getElasticsearchConnection(localClusterConnectionId);
			}
		}

		String remoteClusterConnectionId =
			elasticsearchConfigurationWrapper.remoteClusterConnectionId();

		if (Validator.isBlank(remoteClusterConnectionId)) {
			remoteClusterConnectionId =
				ConnectionConstants.REMOTE_CONNECTION_ID;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Getting remote cluster connection with ID: " +
					remoteClusterConnectionId);
		}

		return getElasticsearchConnection(remoteClusterConnectionId);
	}

	@Reference
	protected ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected Http http;

	private ElasticsearchConnection _createRemoteElasticsearchConnection() {
		ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
			new ElasticsearchConnectionBuilder();

		elasticsearchConnectionBuilder.active(
			true
		).authenticationEnabled(
			elasticsearchConfigurationWrapper.authenticationEnabled()
		).connectionId(
			ConnectionConstants.REMOTE_CONNECTION_ID
		).httpSSLEnabled(
			elasticsearchConfigurationWrapper.httpSSLEnabled()
		).maxConnections(
			elasticsearchConfigurationWrapper.maxConnections()
		).maxConnectionsPerRoute(
			elasticsearchConfigurationWrapper.maxConnectionsPerRoute()
		).networkHostAddresses(
			elasticsearchConfigurationWrapper.networkHostAddresses()
		).password(
			elasticsearchConfigurationWrapper.password()
		).proxyConfig(
			createProxyConfig()
		).truststorePassword(
			elasticsearchConfigurationWrapper.truststorePassword()
		).truststorePath(
			elasticsearchConfigurationWrapper.truststorePath()
		).truststoreType(
			elasticsearchConfigurationWrapper.truststoreType()
		).userName(
			elasticsearchConfigurationWrapper.userName()
		);

		return elasticsearchConnectionBuilder.build();
	}

	private String _getExceptionMessage(
		String message, String connectionId, boolean preferLocalCluster) {

		return StringBundler.concat(
			message, " Production Mode Enabled: ",
			elasticsearchConfigurationWrapper.isProductionModeEnabled(),
			", Connection ID: ", connectionId, ", Prefer Local Cluster: ",
			preferLocalCluster, ", Cross-Cluster Replication Enabled: ",
			isCrossClusterReplicationEnabled(), ". Enable INFO logs on ",
			ElasticsearchConnectionManager.class, " for more information");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchConnectionManager.class);

	private static final Snapshot<CrossClusterReplicationConfigurationHelper>
		_crossClusterReplicationConfigurationHelperSnapshot = new Snapshot<>(
			ElasticsearchConnectionManager.class,
			CrossClusterReplicationConfigurationHelper.class, null, true);

	private final Map<String, Supplier<ElasticsearchConnection>>
		_elasticsearchConnectionSuppliers = new ConcurrentHashMap<>();
	private volatile InetSocketAddress _portalInetSocketAddress;
	private ServiceRegistration<?> _serviceRegistration;

	private class ElasticsearchPortalInetSocketAddressEventListener
		implements PortalInetSocketAddressEventListener {

		@Override
		public void portalLocalInetSocketAddressConfigured(
			InetSocketAddress inetSocketAddress, boolean secure) {

			_portalInetSocketAddress = inetSocketAddress;
		}

		@Override
		public void portalServerInetSocketAddressConfigured(
			InetSocketAddress inetSocketAddress, boolean secure) {
		}

	}

}