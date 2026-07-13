/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.information;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchVersionInfo;
import co.elastic.clients.elasticsearch._types.TimeUnit;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.json.JsonpDeserializer;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.SimpleEndpoint;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConnectionConfiguration;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch8.internal.connection.constants.ConnectionConstants;
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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SearchEngineInformation.class)
public class ElasticsearchSearchEngineInformation
	implements SearchEngineInformation {

	@Override
	public String getClientVersionString() {
		return co.elastic.clients.transport.Version.VERSION.toString();
	}

	@Override
	public List<ConnectionInformation> getConnectionInformationList() {
		List<ConnectionInformation> connectionInformationList =
			new LinkedList<>();

		ElasticsearchConnection elasticsearchConnection =
			elasticsearchConnectionManager.getElasticsearchConnection();

		_addMainConnection(elasticsearchConnection, connectionInformationList);

		String filterString = String.format(
			"(&(service.factoryPid=%s)(active=%s)",
			ElasticsearchConnectionConfiguration.class.getName(), true);

		if (elasticsearchConfigurationWrapper.productionModeEnabled() &&
			!Validator.isBlank(
				elasticsearchConfigurationWrapper.
					remoteClusterConnectionId())) {

			filterString = filterString.concat(
				String.format(
					"(!(connectionId=%s))",
					elasticsearchConfigurationWrapper.
						remoteClusterConnectionId()));
		}

		ElasticsearchConnection localClusterElasticsearchConnection =
			elasticsearchConnectionManager.getElasticsearchConnection(true);

		if (elasticsearchConfigurationWrapper.productionModeEnabled() &&
			elasticsearchConnectionManager.isCrossClusterReplicationEnabled() &&
			!elasticsearchConnection.equals(
				localClusterElasticsearchConnection)) {

			_addCCRConnection(
				localClusterElasticsearchConnection, connectionInformationList);

			filterString = filterString.concat(
				String.format(
					"(!(connectionId=%s))",
					localClusterElasticsearchConnection.getConnectionId()));
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
		try {
			Version serverVersion = _getServerVersion();

			if ((serverVersion != null) &&
				(serverVersion.compareTo(_VERSION_8_11) >= 0)) {

				return new int[] {
					256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096
				};
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return new int[] {256, 384, 512, 768, 1024, 1536, 2048};
	}

	@Override
	public String getNodesString() {
		try {
			String clusterNodesString = _getClusterNodesString(
				elasticsearchConnectionManager.getElasticsearchClient());

			if (elasticsearchConfigurationWrapper.productionModeEnabled() &&
				elasticsearchConnectionManager.
					isCrossClusterReplicationEnabled()) {

				String localClusterNodesString = _getClusterNodesString(
					elasticsearchConnectionManager.getElasticsearchClient(
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
		String vendor = "Elasticsearch";

		if (!elasticsearchConfigurationWrapper.productionModeEnabled()) {
			return vendor + " (Sidecar)";
		}

		return vendor;
	}

	@Reference
	protected ConfigurationAdmin configurationAdmin;

	@Reference
	protected ConnectionInformationBuilderFactory
		connectionInformationBuilderFactory;

	@Reference
	protected ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected NodeInformationBuilderFactory nodeInformationBuilderFactory;

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

			String connectionId = (String)properties.get("connectionId");

			_addConnectionInformation(
				elasticsearchConnectionManager.getElasticsearchConnection(
					connectionId),
				connectionInformationList, new LinkedHashSet<>());
		}
	}

	private void _addCCRConnection(
		ElasticsearchConnection elasticsearchConnection,
		List<ConnectionInformation> connectionInformationList) {

		_addConnectionInformation(
			elasticsearchConnection, connectionInformationList,
			new LinkedHashSet<>(Arrays.asList("read")));
	}

	private void _addConnectionInformation(
		ElasticsearchConnection elasticsearchConnection,
		List<ConnectionInformation> connectionInformationList,
		Set<String> labels) {

		if (elasticsearchConnection == null) {
			return;
		}

		ConnectionInformationBuilder connectionInformationBuilder =
			connectionInformationBuilderFactory.
				getConnectionInformationBuilder();

		try {
			_setClusterAndNodeInformation(
				connectionInformationBuilder, labels,
				elasticsearchConnection.getElasticsearchClient());
		}
		catch (Exception exception) {
			connectionInformationBuilder.error(exception.toString());

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get node information", exception);
			}
		}

		String connectionId = elasticsearchConnection.getConnectionId();

		connectionInformationBuilder.connectionId(connectionId);

		if (connectionId.equals(ConnectionConstants.SIDECAR_CONNECTION_ID)) {
			labels.add("not-supported");
		}

		try {
			_setHealthInformation(
				connectionInformationBuilder,
				elasticsearchConnection.getConnectionId());
		}
		catch (RuntimeException runtimeException) {
			connectionInformationBuilder.health("unknown");

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get health information", runtimeException);
			}
		}

		if (!labels.isEmpty()) {
			connectionInformationBuilder.labels(labels);
		}

		connectionInformationList.add(connectionInformationBuilder.build());
	}

	private void _addMainConnection(
		ElasticsearchConnection elasticsearchConnection,
		List<ConnectionInformation> connectionInformationList) {

		Set<String> labels = new LinkedHashSet<>(
			Arrays.asList("read", "write"));

		if (elasticsearchConfigurationWrapper.productionModeEnabled() &&
			elasticsearchConnectionManager.isCrossClusterReplicationEnabled() &&
			!elasticsearchConnection.equals(
				elasticsearchConnectionManager.getElasticsearchConnection(
					true))) {

			labels.remove("read");
		}

		_addConnectionInformation(
			elasticsearchConnection, connectionInformationList, labels);
	}

	private String _getClusterNodesString(
		ElasticsearchClient elasticsearchClient) {

		try {
			if (elasticsearchClient == null) {
				return StringPool.BLANK;
			}

			ConnectionInformationBuilder connectionInformationBuilder =
				connectionInformationBuilderFactory.
					getConnectionInformationBuilder();

			_setClusterAndNodeInformation(
				connectionInformationBuilder, new LinkedHashSet<>(),
				elasticsearchClient);

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

	private JsonObject _getNodesInfoJsonObject(
			ElasticsearchClient elasticsearchClient)
		throws Exception {

		ElasticsearchTransport elasticsearchTransport =
			elasticsearchClient._transport();

		JsonValue jsonValue = elasticsearchTransport.performRequest(
			null,
			new SimpleEndpoint<Object, JsonValue>(
				"es/nodes.info", nodesInfoRequest -> "GET",
				nodesInfoRequest -> "/_nodes",
				nodesInfoRequest -> Collections.emptyMap(),
				nodesInfoRequest -> Collections.singletonMap(
					"timeout", "10000" + TimeUnit.Milliseconds.jsonValue()),
				nodesInfoRequest -> Collections.emptyMap(), false,
				JsonpDeserializer.jsonValueDeserializer()),
			null);

		return jsonValue.asJsonObject();
	}

	private Version _getServerVersion() throws Exception {
		String serverVersionString = _getServerVersionString();

		if (Validator.isBlank(serverVersionString)) {
			return null;
		}

		return Version.parseVersion(serverVersionString);
	}

	private String _getServerVersionString() throws Exception {
		ElasticsearchClient elasticsearchClient =
			elasticsearchConnectionManager.getElasticsearchClient();

		InfoResponse infoResponse = elasticsearchClient.info();

		ElasticsearchVersionInfo elasticsearchVersionInfo =
			infoResponse.version();

		return elasticsearchVersionInfo.number();
	}

	private void _setClusterAndNodeInformation(
			ConnectionInformationBuilder connectionInformationBuilder,
			Set<String> labels, ElasticsearchClient elasticsearchClient)
		throws Exception {

		List<NodeInformation> nodeInformationList = new ArrayList<>();

		JsonObject jsonObject = _getNodesInfoJsonObject(elasticsearchClient);

		connectionInformationBuilder.clusterName(
			jsonObject.getString("cluster_name", StringPool.BLANK));

		JsonObject nodesJsonObject = jsonObject.getJsonObject("nodes");

		if (nodesJsonObject != null) {
			for (String nodeId : nodesJsonObject.keySet()) {
				JsonObject nodeJsonObject = nodesJsonObject.getJsonObject(
					nodeId);

				NodeInformationBuilder nodeInformationBuilder =
					nodeInformationBuilderFactory.getNodeInformationBuilder();

				nodeInformationBuilder.name(
					nodeJsonObject.getString("name", StringPool.BLANK));

				Version version = Version.parseVersion(
					nodeJsonObject.getString("version", StringPool.BLANK));

				nodeInformationBuilder.version(version.toString());

				if (version.getMajor() == 7) {
					labels.add("deprecated");
				}

				nodeInformationList.add(nodeInformationBuilder.build());
			}
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

	private static final Version _VERSION_8_11 = Version.parseVersion("8.11");

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchSearchEngineInformation.class);

}