/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ccr.CrossClusterReplicationConfigurationHelper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationObserver;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;

import java.net.InetAddress;

import java.util.List;
import java.util.Map;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "com.liferay.portal.search.opensearch2.configuration.OpenSearchConnectionConfiguration",
	service = OpenSearchConnectionManager.class
)
public class OpenSearchConnectionManagerImpl
	implements OpenSearchConfigurationObserver, OpenSearchConnectionManager {

	@Override
	public void addOpenSearchConnection(
		OpenSearchConnection openSearchConnection) {

		openSearchConnectionsHolder.addOpenSearchConnection(
			openSearchConnection);
	}

	@Override
	public int compareTo(
		OpenSearchConfigurationObserver openSearchConfigurationObserver) {

		return openSearchConfigurationWrapper.compare(
			this, openSearchConfigurationObserver);
	}

	@Override
	public JsonpMapper getJsonpMapper(String connectionId) {
		OpenSearchConnection openSearchConnection;

		if (connectionId == null) {
			openSearchConnection = getOpenSearchConnection();
		}
		else {
			openSearchConnection = getOpenSearchConnection(connectionId);
		}

		if (openSearchConnection != null) {
			return openSearchConnection.getJsonpMapper();
		}

		return new JacksonJsonpMapper();
	}

	@Override
	public String getLocalClusterConnectionId() {
		ClusterNode localClusterNode = _clusterExecutor.getLocalClusterNode();

		CrossClusterReplicationConfigurationHelper
			currentCrossClusterReplicationConfigurationHelper =
				_crossClusterReplicationConfigurationHelperSnapshot.get();

		if (localClusterNode == null) {
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

		InetAddress portalInetAddress = localClusterNode.getPortalInetAddress();

		if ((portalInetAddress == null) ||
			(currentCrossClusterReplicationConfigurationHelper == null)) {

			return null;
		}

		Map<String, String> localClusterConnectionConfigurations =
			currentCrossClusterReplicationConfigurationHelper.
				getLocalClusterConnectionIdsMap();

		String localClusterNodeHostName =
			portalInetAddress.getHostName() + StringPool.COLON +
				localClusterNode.getPortalPort();

		return localClusterConnectionConfigurations.get(
			localClusterNodeHostName);
	}

	@Override
	public OpenSearchClient getOpenSearchClient() {
		return getOpenSearchClient(null);
	}

	@Override
	public OpenSearchClient getOpenSearchClient(String connectionId) {
		return getOpenSearchClient(connectionId, false);
	}

	@Override
	public OpenSearchClient getOpenSearchClient(
		String connectionId, boolean preferLocalCluster) {

		OpenSearchConnection openSearchConnection = getOpenSearchConnection(
			connectionId, preferLocalCluster);

		if (openSearchConnection == null) {
			throw new OpenSearchConnectionNotInitializedException(
				_getExceptionMessage(
					"OpenSearch connection not found.", connectionId,
					preferLocalCluster));
		}

		OpenSearchClient openSearchClient =
			openSearchConnection.getOpenSearchClient();

		if (openSearchClient == null) {
			throw new OpenSearchConnectionNotInitializedException(
				_getExceptionMessage(
					"OpenSearch client not found.",
					openSearchConnection.getConnectionId(),
					preferLocalCluster));
		}

		return openSearchClient;
	}

	@Override
	public OpenSearchConnection getOpenSearchConnection() {
		return getOpenSearchConnection(null, false);
	}

	@Override
	public OpenSearchConnection getOpenSearchConnection(
		boolean preferLocalCluster) {

		return getOpenSearchConnection(null, preferLocalCluster);
	}

	@Override
	public OpenSearchConnection getOpenSearchConnection(String connectionId) {
		if (Validator.isBlank(connectionId)) {
			throw new RuntimeException("OpenSearch connection not configured");
		}

		OpenSearchConnection openSearchConnection =
			openSearchConnectionsHolder.getOpenSearchConnection(connectionId);

		if (openSearchConnection != null) {
			if (_log.isInfoEnabled()) {
				_log.info("Returning connection with ID: " + connectionId);
			}

			return openSearchConnection;
		}

		throw new OpenSearchConnectionNotInitializedException(
			_getExceptionMessage(
				"OpenSearch connection not found.", connectionId, null));
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
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
	public void onOpenSearchConfigurationUpdate() {
	}

	@Override
	public void removeOpenSearchConnection(String connectionId) {
		openSearchConnectionsHolder.removeOpenSearchConnection(connectionId);
	}

	@Activate
	protected void activate() {
		openSearchConfigurationWrapper.register(this);
	}

	@Deactivate
	protected void deactivate() {
		openSearchConfigurationWrapper.unregister(this);
	}

	protected OpenSearchConnection getOpenSearchConnection(
		String connectionId, boolean preferLocalCluster) {

		if (_log.isInfoEnabled()) {
			_log.info("Connection requested for ID: " + connectionId);
		}

		if (!Validator.isBlank(connectionId)) {
			if (_log.isInfoEnabled()) {
				_log.info("Getting connection with ID: " + connectionId);
			}

			return getOpenSearchConnection(connectionId);
		}

		if (preferLocalCluster && isCrossClusterReplicationEnabled()) {
			String localClusterConnectionId = getLocalClusterConnectionId();

			if (localClusterConnectionId != null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Getting local cluster connection with ID: " +
							localClusterConnectionId);
				}

				return getOpenSearchConnection(localClusterConnectionId);
			}
		}

		String remoteClusterConnectionId =
			openSearchConfigurationWrapper.remoteClusterConnectionId();

		if (_log.isInfoEnabled()) {
			_log.info(
				"Getting remote cluster connection with ID: " +
					remoteClusterConnectionId);
		}

		return getOpenSearchConnection(remoteClusterConnectionId);
	}

	@Reference
	protected OpenSearchConfigurationWrapper openSearchConfigurationWrapper;

	@Reference
	protected OpenSearchConnectionsHolder openSearchConnectionsHolder;

	private String _getExceptionMessage(
		String message, String connectionId, Boolean preferLocalCluster) {

		return StringBundler.concat(
			message, " Connection ID: ", connectionId,
			", Prefer Local Cluster: ", preferLocalCluster,
			", Cross-Cluster Replication Enabled: ",
			isCrossClusterReplicationEnabled(), ". Enable INFO logs on ",
			OpenSearchConnectionManager.class, " for more information");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchConnectionManagerImpl.class);

	private static final Snapshot<CrossClusterReplicationConfigurationHelper>
		_crossClusterReplicationConfigurationHelperSnapshot = new Snapshot<>(
			OpenSearchConnectionManagerImpl.class,
			CrossClusterReplicationConfigurationHelper.class, null, true);

	@Reference
	private ClusterExecutor _clusterExecutor;

}