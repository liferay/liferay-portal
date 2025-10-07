/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.information;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConnectionConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.http.util.EntityUtils;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

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
		return org.elasticsearch.Version.CURRENT.toString();
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

		if (elasticsearchConfigurationWrapper.isProductionModeEnabled() &&
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

		if (elasticsearchConfigurationWrapper.isProductionModeEnabled() &&
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
				elasticsearchConnectionManager.getRestHighLevelClient());

			if (elasticsearchConfigurationWrapper.isProductionModeEnabled() &&
				elasticsearchConnectionManager.
					isCrossClusterReplicationEnabled()) {

				String localClusterNodesString = _getClusterNodesString(
					elasticsearchConnectionManager.getRestHighLevelClient(
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

		if (elasticsearchConfigurationWrapper.isDevelopmentModeEnabled()) {
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
				elasticsearchConnection.getRestHighLevelClient());
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

		if (elasticsearchConfigurationWrapper.isProductionModeEnabled() &&
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
		RestHighLevelClient restHighLevelClient) {

		try {
			if (restHighLevelClient == null) {
				return StringPool.BLANK;
			}

			ConnectionInformationBuilder connectionInformationBuilder =
				connectionInformationBuilderFactory.
					getConnectionInformationBuilder();

			_setClusterAndNodeInformation(
				connectionInformationBuilder, new LinkedHashSet<>(),
				restHighLevelClient);

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

	private Version _getServerVersion() throws Exception {
		String serverVersionString = _getServerVersionString();

		if (Validator.isBlank(serverVersionString)) {
			return null;
		}

		return Version.parseVersion(serverVersionString);
	}

	private String _getServerVersionString() throws Exception {
		RestHighLevelClient restHighLevelClient =
			elasticsearchConnectionManager.getRestHighLevelClient();

		RestClient restClient = restHighLevelClient.getLowLevelClient();

		Response response = restClient.performRequest(
			new Request("GET", StringPool.SLASH));

		String responseBody = EntityUtils.toString(response.getEntity());

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
			responseBody);

		JSONObject versionJSONObject = responseJSONObject.getJSONObject(
			"version");

		if (versionJSONObject != null) {
			return versionJSONObject.getString("number");
		}

		return null;
	}

	private void _setClusterAndNodeInformation(
			ConnectionInformationBuilder connectionInformationBuilder,
			Set<String> labels, RestHighLevelClient restHighLevelClient)
		throws Exception {

		RestClient restClient = restHighLevelClient.getLowLevelClient();

		String endpoint = "/_nodes";

		Request request = new Request("GET", endpoint);

		request.addParameter("timeout", "10000ms");

		Response response = restClient.performRequest(request);

		String responseBody = EntityUtils.toString(response.getEntity());

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
			responseBody);

		String clusterName = GetterUtil.getString(
			responseJSONObject.get("cluster_name"));

		connectionInformationBuilder.clusterName(clusterName);

		JSONObject nodesJSONObject = responseJSONObject.getJSONObject("nodes");

		Set<String> nodes = nodesJSONObject.keySet();

		List<NodeInformation> nodeInformationList = new ArrayList<>();

		for (String node : nodes) {
			JSONObject nodeJSONObject = nodesJSONObject.getJSONObject(node);

			NodeInformationBuilder nodeInformationBuilder =
				nodeInformationBuilderFactory.getNodeInformationBuilder();

			nodeInformationBuilder.name(
				GetterUtil.getString(nodeJSONObject.get("name")));

			Version version = Version.parseVersion(
				GetterUtil.getString(nodeJSONObject.get("version")));

			nodeInformationBuilder.version(version.toString());

			if (version.getMajor() == 7) {
				labels.add("deprecated");
			}

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

	private static final Version _VERSION_8_11 = Version.parseVersion("8.11");

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchSearchEngineInformation.class);

	@Reference
	private JSONFactory _jsonFactory;

}