/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.HttpPortRange;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.PathUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.SidecarManager;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.Map;

import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.client.IngestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.xcontent.XContentType;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ElasticsearchConnectionFixture
	implements ElasticsearchClientResolver {

	public static Builder builder() {
		return new Builder();
	}

	public ElasticsearchConnection createElasticsearchConnection() {
		PropsUtil.set(PropsKeys.LIFERAY_HOME, _TMP_PATH.toString());
		PropsUtil.set(
			PropsKeys.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
			String.valueOf(_TMP_PATH.resolve("lib-process-executor")));

		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper =
			new ElasticsearchConfigurationWrapper() {
				{
					setElasticsearchConfiguration(
						ConfigurableUtil.createConfigurable(
							ElasticsearchConfiguration.class,
							_elasticsearchConfigurationProperties));
				}

				@Override
				public String httpCORSAllowOrigin() {
					return "'*'";
				}

			};

		Sidecar sidecar = new Sidecar(
			elasticsearchConfigurationWrapper, new LocalProcessExecutor(),
			_TMP_PATH.resolve("sidecar-elasticsearch"),
			Mockito.mock(SidecarManager.class),
			new File(_workPath.toFile(), "sidecar.process"), _workPath);

		ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
			new ElasticsearchConnectionBuilder();

		elasticsearchConnectionBuilder.active(
			true
		).connectionId(
			ConnectionConstants.SIDECAR_CONNECTION_ID
		).postCloseRunnable(
			sidecar::stop
		).preConnectElasticsearchConnectionConsumer(
			elasticsearchConnection -> {
				_deleteTmpDir();

				sidecar.start();

				elasticsearchConnection.setNetworkHostAddresses(
					new String[] {sidecar.getNetworkHostAddress()});
			}
		);

		_elasticsearchConnection = elasticsearchConnectionBuilder.build();

		return _elasticsearchConnection;
	}

	public void createNode() {
		createElasticsearchConnection();

		_elasticsearchConnection.connect();

		_putTimestampPipeline(getRestHighLevelClient());
	}

	public void destroyNode() {
		if (_elasticsearchConnection != null) {
			_elasticsearchConnection.close();
		}

		_deleteTmpDir();
	}

	public Map<String, Object> getElasticsearchConfigurationProperties() {
		return _elasticsearchConfigurationProperties;
	}

	public ElasticsearchConnection getElasticsearchConnection() {
		return _elasticsearchConnection;
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient() {
		return _elasticsearchConnection.getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(String connectionId) {
		return getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(
		String connectionId, boolean preferLocalCluster) {

		return getRestHighLevelClient();
	}

	public static class Builder {

		public ElasticsearchConnectionFixture build() {
			ElasticsearchConnectionFixture elasticsearchConnectionFixture =
				new ElasticsearchConnectionFixture();

			elasticsearchConnectionFixture.
				_elasticsearchConfigurationProperties =
					createElasticsearchConfigurationProperties(
						_elasticsearchConfigurationProperties, _clusterName);
			elasticsearchConnectionFixture._workPath = _TMP_PATH.resolve(
				_clusterName);

			return elasticsearchConnectionFixture;
		}

		public ElasticsearchConnectionFixture.Builder clusterName(
			String clusterName) {

			_clusterName = clusterName;

			return this;
		}

		public Builder elasticsearchConfigurationProperties(
			Map<String, Object> elasticsearchConfigurationProperties) {

			if (elasticsearchConfigurationProperties == null) {
				elasticsearchConfigurationProperties =
					Collections.<String, Object>emptyMap();
			}

			_elasticsearchConfigurationProperties =
				elasticsearchConfigurationProperties;

			return this;
		}

		protected static Map<String, Object>
			createElasticsearchConfigurationProperties(
				Map<String, Object> elasticsearchConfigurationProperties,
				String clusterName) {

			String sidecarJVMOptions = "-Xmx256m";

			if (!JavaDetector.isJDK8()) {
				sidecarJVMOptions =
					"-Xmx256m|--add-opens=java.base/java.lang=ALL-UNNAMED|--" +
						"add-opens=java.base/java.lang.invoke=ALL-UNNAMED";
			}

			return HashMapBuilder.<String, Object>put(
				"clusterName", clusterName
			).put(
				"configurationPid", ElasticsearchConfiguration.class.getName()
			).put(
				"httpCORSAllowOrigin", "*"
			).put(
				"logExceptionsOnly", false
			).put(
				"sidecarHttpPort", HttpPortRange.AUTO
			).put(
				"sidecarJVMOptions", sidecarJVMOptions
			).putAll(
				elasticsearchConfigurationProperties
			).build();
		}

		private String _clusterName;
		private Map<String, Object> _elasticsearchConfigurationProperties =
			Collections.<String, Object>emptyMap();

	}

	private void _deleteTmpDir() {
		PathUtil.deleteDir(_workPath);
	}

	private void _putTimestampPipeline(
		RestHighLevelClient restHighLevelClient) {

		IngestClient ingestClient = restHighLevelClient.ingest();

		String json = JSONUtil.put(
			"description", "Adds timestamp to documents"
		).put(
			"processors",
			JSONUtil.put(
				JSONUtil.put(
					"set",
					JSONUtil.put(
						"field", "_source.timestamp"
					).put(
						"value", "{{{_ingest.timestamp}}}"
					)))
		).toString();

		PutPipelineRequest putPipelineRequest = new PutPipelineRequest(
			"timestamp", new BytesArray(json.getBytes(StandardCharsets.UTF_8)),
			XContentType.JSON);

		try {
			ingestClient.putPipeline(
				putPipelineRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Path _TMP_PATH = Paths.get("tmp");

	private Map<String, Object> _elasticsearchConfigurationProperties =
		Collections.<String, Object>emptyMap();
	private ElasticsearchConnection _elasticsearchConnection;
	private Path _workPath;

}