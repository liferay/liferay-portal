/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.information;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.ConnectionInformation;
import com.liferay.portal.search.engine.ConnectionInformationBuilder;
import com.liferay.portal.search.engine.ConnectionInformationBuilderFactory;
import com.liferay.portal.search.engine.NodeInformation;
import com.liferay.portal.search.engine.NodeInformationBuilder;
import com.liferay.portal.search.engine.NodeInformationBuilderFactory;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConnectionConfiguration;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnection;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch.nodes.NodesInfoRequest;
import org.opensearch.client.opensearch.nodes.NodesInfoResponse;
import org.opensearch.client.opensearch.nodes.OpenSearchNodesClient;
import org.opensearch.client.opensearch.nodes.info.NodeInfo;
import org.opensearch.client.transport.Version;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunen
 */
@Component(service = SearchEngineInformation.class)
public class OpenSearchSearchEngineInformation
	implements SearchEngineInformation {

	@Override
	public String getClientVersionString() {
		return Version.VERSION.toString();
	}

	@Override
	public List<ConnectionInformation> getConnectionInformationList() {
		List<ConnectionInformation> connectionInformationList =
			new LinkedList<>();

		OpenSearchConnection openSearchConnection =
			openSearchConnectionManager.getOpenSearchConnection();

		_addMainConnection(openSearchConnection, connectionInformationList);

		String filterString = String.format(
			"(&(service.factoryPid=%s)(active=%s)",
			OpenSearchConnectionConfiguration.class.getName(), true);

		if (!Validator.isBlank(
				openSearchConfigurationWrapper.remoteClusterConnectionId())) {

			filterString = filterString.concat(
				String.format(
					"(!(connectionId=%s))",
					openSearchConfigurationWrapper.
						remoteClusterConnectionId()));
		}

		OpenSearchConnection localClusterOpenSearchConnection =
			openSearchConnectionManager.getOpenSearchConnection(true);

		if (openSearchConnectionManager.isCrossClusterReplicationEnabled() &&
			!openSearchConnection.equals(localClusterOpenSearchConnection)) {

			_addCCRConnection(
				localClusterOpenSearchConnection, connectionInformationList);

			filterString = filterString.concat(
				String.format(
					"(!(connectionId=%s))",
					localClusterOpenSearchConnection.getConnectionId()));
		}

		filterString = filterString.concat(")");

		try {
			_addActiveConnections(filterString, connectionInformationList);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get active connections", exception);
			}
		}

		return connectionInformationList;
	}

	@Override
	public int[] getEmbeddingVectorDimensions() {
		return new int[] {256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096};
	}

	@Override
	public String getNodesString() {
		try {
			String clusterNodesString = _getClusterNodesString(
				openSearchConnectionManager.getOpenSearchClient());

			if (openSearchConnectionManager.
					isCrossClusterReplicationEnabled()) {

				String localClusterNodesString = _getClusterNodesString(
					openSearchConnectionManager.getOpenSearchClient(
						null, true));

				if (!Validator.isBlank(localClusterNodesString)) {
					clusterNodesString = StringBundler.concat(
						"Remote Cluster = ", clusterNodesString,
						", Local Cluster = ", localClusterNodesString);
				}
			}

			return clusterNodesString;
		}
		catch (Exception exception) {
			return exception.toString();
		}
	}

	@Override
	public String getVendorString() {
		return "OpenSearch";
	}

	@Reference
	protected ConfigurationAdmin configurationAdmin;

	@Reference
	protected ConnectionInformationBuilderFactory
		connectionInformationBuilderFactory;

	@Reference
	protected NodeInformationBuilderFactory nodeInformationBuilderFactory;

	@Reference
	protected OpenSearchConfigurationWrapper openSearchConfigurationWrapper;

	@Reference
	protected OpenSearchConnectionManager openSearchConnectionManager;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private void _addActiveConnections(
			String filterString,
			List<ConnectionInformation> connectionInformationList)
		throws Exception {

		Configuration[] configurations = configurationAdmin.listConfigurations(
			filterString);

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			_addConnectionInformation(
				openSearchConnectionManager.getOpenSearchConnection(
					(String)properties.get("connectionId")),
				connectionInformationList, null);
		}
	}

	private void _addCCRConnection(
		OpenSearchConnection openSearchConnection,
		List<ConnectionInformation> connectionInformationList) {

		_addConnectionInformation(
			openSearchConnection, connectionInformationList, "read");
	}

	private void _addConnectionInformation(
		OpenSearchConnection openSearchConnection,
		List<ConnectionInformation> connectionInformationList,
		String... labels) {

		if (openSearchConnection == null) {
			return;
		}

		ConnectionInformationBuilder connectionInformationBuilder =
			connectionInformationBuilderFactory.
				getConnectionInformationBuilder();

		try {
			_setClusterAndNodeInformation(
				connectionInformationBuilder,
				openSearchConnection.getOpenSearchClient());
		}
		catch (Exception exception) {
			connectionInformationBuilder.error(exception.toString());

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get node information", exception);
			}
		}

		connectionInformationBuilder.connectionId(
			openSearchConnection.getConnectionId());

		try {
			_setHealthInformation(
				connectionInformationBuilder,
				openSearchConnection.getConnectionId());
		}
		catch (RuntimeException runtimeException) {
			connectionInformationBuilder.health("unknown");

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get health information", runtimeException);
			}
		}

		if (ArrayUtil.isNotEmpty(labels)) {
			connectionInformationBuilder.labels(SetUtil.fromArray(labels));
		}

		connectionInformationList.add(connectionInformationBuilder.build());
	}

	private void _addMainConnection(
		OpenSearchConnection openSearchConnection,
		List<ConnectionInformation> connectionInformationList) {

		String[] labels = {"read", "write"};

		if (openSearchConnectionManager.isCrossClusterReplicationEnabled() &&
			!openSearchConnection.equals(
				openSearchConnectionManager.getOpenSearchConnection(true))) {

			labels = new String[] {"write"};
		}

		_addConnectionInformation(
			openSearchConnection, connectionInformationList, labels);
	}

	private String _getClusterNodesString(OpenSearchClient openSearchClient) {
		try {
			if (openSearchClient == null) {
				return StringPool.BLANK;
			}

			ConnectionInformationBuilder connectionInformationBuilder =
				connectionInformationBuilderFactory.
					getConnectionInformationBuilder();

			_setClusterAndNodeInformation(
				connectionInformationBuilder, openSearchClient);

			ConnectionInformation connectionInformation =
				connectionInformationBuilder.build();

			String clusterName = connectionInformation.getClusterName();

			List<NodeInformation> nodeInformations =
				connectionInformation.getNodeInformationList();

			StringBundler sb = new StringBundler(
				(nodeInformations.size() * 6) + 4);

			sb.append(clusterName);
			sb.append(StringPool.COLON);
			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_BRACKET);

			for (NodeInformation nodeInformation : nodeInformations) {
				sb.append(nodeInformation.getName());
				sb.append(StringPool.SPACE);
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(nodeInformation.getVersion());
				sb.append(StringPool.CLOSE_PARENTHESIS);
				sb.append(StringPool.COMMA_AND_SPACE);
			}

			sb.setIndex(sb.index() - 1);

			sb.append(StringPool.CLOSE_BRACKET);

			return sb.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get node information", exception);
			}

			return StringBundler.concat("(Error: ", exception, ")");
		}
	}

	private void _setClusterAndNodeInformation(
			ConnectionInformationBuilder connectionInformationBuilder,
			OpenSearchClient openSearchClient)
		throws Exception {

		OpenSearchNodesClient openSearchNodesClient = openSearchClient.nodes();

		NodesInfoResponse nodesInfoResponse = openSearchNodesClient.info(
			NodesInfoRequest.of(
				nodesInforequest -> nodesInforequest.timeout(
					Time.of(
						time -> time.time(
							"10000" + TimeUnit.Milliseconds.jsonValue())))));

		String clusterName = GetterUtil.getString(
			nodesInfoResponse.clusterName());

		connectionInformationBuilder.clusterName(clusterName);

		Map<String, NodeInfo> nodeInfos = nodesInfoResponse.nodes();

		List<NodeInformation> nodeInformationList = new ArrayList<>();

		for (Map.Entry<String, NodeInfo> entry : nodeInfos.entrySet()) {
			NodeInfo nodeInfo = entry.getValue();

			NodeInformationBuilder nodeInformationBuilder =
				nodeInformationBuilderFactory.getNodeInformationBuilder();

			nodeInformationBuilder.name(nodeInfo.name());
			nodeInformationBuilder.version(nodeInfo.version());

			nodeInformationList.add(nodeInformationBuilder.build());
		}

		connectionInformationBuilder.nodeInformationList(nodeInformationList);
	}

	private void _setHealthInformation(
		ConnectionInformationBuilder connectionInformationBuilder,
		String connectionId) {

		HealthClusterRequest healthClusterRequest = new HealthClusterRequest();

		healthClusterRequest.setConnectionId(connectionId);
		healthClusterRequest.setTimeout(1000);

		HealthClusterResponse healthClusterResponse =
			searchEngineAdapter.execute(healthClusterRequest);

		connectionInformationBuilder.health(
			String.valueOf(healthClusterResponse.getClusterHealthStatus()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchSearchEngineInformation.class);

}