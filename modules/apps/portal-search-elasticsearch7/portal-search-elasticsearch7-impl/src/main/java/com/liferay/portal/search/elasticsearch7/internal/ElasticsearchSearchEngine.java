/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.ccr.CrossClusterReplicationHelper;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.index.CompanyIndexHelper;
import com.liferay.portal.search.elasticsearch7.internal.index.IndexFactory;
import com.liferay.portal.search.engine.ConnectionInformation;
import com.liferay.portal.search.engine.NodeInformation;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesResponse;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotDetails;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRepositoryDetails;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotState;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.IngestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "search.engine.impl=Elasticsearch", service = SearchEngine.class
)
public class ElasticsearchSearchEngine
	implements ElasticsearchConfigurationObserver, SearchEngine {

	@Override
	public synchronized String backup(long companyId, String backupName)
		throws SearchException {

		backupName = StringUtil.toLowerCase(backupName);

		_validateBackupName(backupName);

		createBackupRepository();

		CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest(
			_BACKUP_REPOSITORY_NAME, backupName);

		createSnapshotRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId));

		CreateSnapshotResponse createSnapshotResponse =
			_searchEngineAdapter.execute(createSnapshotRequest);

		SnapshotDetails snapshotDetails =
			createSnapshotResponse.getSnapshotDetails();

		SnapshotState snapshotState = snapshotDetails.getSnapshotState();

		if (snapshotState.equals(SnapshotState.FAILED)) {
			throw new IllegalStateException("Unable to complete snapshot");
		}

		return backupName;
	}

	@Override
	public int compareTo(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		return _elasticsearchConfigurationWrapper.compare(
			this, elasticsearchConfigurationObserver);
	}

	public void createBackupRepository() {
		if (_hasBackupRepository()) {
			return;
		}

		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest =
			new CreateSnapshotRepositoryRequest(
				_BACKUP_REPOSITORY_NAME, "es_backup");

		_searchEngineAdapter.execute(createSnapshotRepositoryRequest);
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	@Override
	public int getPriority() {
		return 4;
	}

	@Override
	public String getVendor() {
		return "Elasticsearch";
	}

	@Override
	public void initialize(long companyId) {
		_waitForYellowStatus();

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		boolean created = _indexFactory.initializeIndex(
			companyId, restHighLevelClient.indices());

		_indexFactory.registerCompanyId(companyId);

		if (created) {
			_waitForYellowStatus();
		}

		CrossClusterReplicationHelper crossClusterReplicationHelper =
			_crossClusterReplicationHelperSnapshot.get();

		if (crossClusterReplicationHelper != null) {
			crossClusterReplicationHelper.follow(
				_indexNameBuilder.getIndexName(companyId));
		}
	}

	public boolean meetsMinimumVersionRequirement(
		Version minimumVersion, String versionString) {

		if (minimumVersion.compareTo(Version.parseVersion(versionString)) <=
				0) {

			return true;
		}

		return false;
	}

	@Override
	public void onElasticsearchConfigurationUpdate() {
		_putTimestampPipeline();
	}

	@Override
	public synchronized void removeBackup(long companyId, String backupName) {
		if (!_hasBackupRepository()) {
			return;
		}

		DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest(
			_BACKUP_REPOSITORY_NAME, backupName);

		_searchEngineAdapter.execute(deleteSnapshotRequest);
	}

	@Override
	public void removeCompany(long companyId) {
		CrossClusterReplicationHelper crossClusterReplicationHelper =
			_crossClusterReplicationHelperSnapshot.get();

		if (crossClusterReplicationHelper != null) {
			crossClusterReplicationHelper.unfollow(
				_indexNameBuilder.getIndexName(companyId));
		}

		setAutoCreateIndex(false);

		try {
			RestHighLevelClient restHighLevelClient =
				_elasticsearchConnectionManager.getRestHighLevelClient();

			_indexFactory.deleteIndex(companyId, restHighLevelClient.indices());

			_indexFactory.unregisterCompanyId(companyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete index for " + companyId, exception);
			}
		}
	}

	@Override
	public synchronized void restore(long companyId, String backupName)
		throws SearchException {

		backupName = StringUtil.toLowerCase(backupName);

		_validateBackupName(backupName);

		CloseIndexRequest closeIndexRequest = new CloseIndexRequest(
			_indexNameBuilder.getIndexName(companyId));

		CloseIndexResponse closeIndexResponse = _searchEngineAdapter.execute(
			closeIndexRequest);

		if (!closeIndexResponse.isAcknowledged()) {
			throw new SystemException(
				"Error closing index: " +
					_indexNameBuilder.getIndexName(companyId));
		}

		RestoreSnapshotRequest restoreSnapshotRequest =
			new RestoreSnapshotRequest(_BACKUP_REPOSITORY_NAME, backupName);

		restoreSnapshotRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId));

		_searchEngineAdapter.execute(restoreSnapshotRequest);

		_waitForYellowStatus();
	}

	public void setAutoCreateIndex(boolean enable) {
		if (Validator.isBlank(_indexNameBuilder.getIndexNamePrefix())) {
			return;
		}

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		ClusterClient clusterClient = restHighLevelClient.cluster();

		ClusterUpdateSettingsRequest clusterUpdateSettingsRequest =
			new ClusterUpdateSettingsRequest();

		try {
			clusterUpdateSettingsRequest.persistentSettings(
				Settings.builder(
				).put(
					"action.auto_create_index",
					_createAutoCreateIndexSetting(enable)
				));

			clusterClient.putSettings(
				clusterUpdateSettingsRequest, RequestOptions.DEFAULT);
		}
		catch (ElasticsearchStatusException elasticsearchStatusException) {
			if (Objects.equals(
					elasticsearchStatusException.status(),
					RestStatus.FORBIDDEN) ||
				Objects.equals(
					elasticsearchStatusException.status(),
					RestStatus.UNAUTHORIZED)) {

				StringBundler sb = new StringBundler(4);

				sb.append("Unable to update cluster auto create index ");
				sb.append("setting due to lack of permissions. This can lead ");
				sb.append("to incorrectly created index mappings: ");
				sb.append(elasticsearchStatusException.getMessage());

				_log.error(sb.toString());

				if (_log.isDebugEnabled()) {
					_log.debug(elasticsearchStatusException);
				}
			}
			else {
				_log.error(elasticsearchStatusException);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_indexFactory = new IndexFactory(
			_companyIndexHelper, _companyLocalService,
			_elasticsearchConfigurationWrapper,
			_elasticsearchConnectionManager);

		_elasticsearchConfigurationWrapper.register(this);

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				ElasticsearchSearchEngine.class.getClassLoader())) {

			_checkNodeVersions();

			setAutoCreateIndex(false);

			if (StartupHelperUtil.isDBNew()) {
				for (long companyId : _getIndexedCompanyIds()) {
					removeCompany(companyId);
				}
			}

			_putTimestampPipeline();

			initialize(CompanyConstants.SYSTEM);
		}
	}

	@Deactivate
	protected void deactivate() {
		_indexFactory.close();
	}

	private void _checkNodeVersions() {
		List<ConnectionInformation> connectionInformationList =
			_searchEngineInformation.getConnectionInformationList();

		if (_log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(
				connectionInformationList.size());

			for (ConnectionInformation connectionInformation :
					connectionInformationList) {

				Set<String> labels = connectionInformation.getLabels();

				if (labels.contains("deprecated")) {
					sb.append(connectionInformation.getConnectionId());
					sb.append(StringPool.COMMA_AND_SPACE);
				}
			}

			if (sb.length() > 0) {
				sb.setIndex(sb.index() - 1);

				_log.warn(
					StringBundler.concat(
						"Connecting to Elasticsearch 7 nodes is now ",
						"deprecated. Upgrade the Elasticsearch nodes ",
						"corresponding to the following connection IDs: ", sb,
						"."));
			}
		}

		if (_elasticsearchConfigurationWrapper.isDevelopmentModeEnabled()) {
			return;
		}

		String minimumVersionString =
			_elasticsearchConfigurationWrapper.minimumRequiredNodeVersion();

		if (minimumVersionString.equals("0.0.0")) {
			String clientVersion =
				_searchEngineInformation.getClientVersionString();

			minimumVersionString = clientVersion.substring(
				0, clientVersion.lastIndexOf("."));
		}

		Version minimumVersion = Version.parseVersion(minimumVersionString);

		for (ConnectionInformation connectionInformation :
				connectionInformationList) {

			List<NodeInformation> nodeInformationList =
				connectionInformation.getNodeInformationList();

			for (NodeInformation nodeInformation : nodeInformationList) {
				if (!meetsMinimumVersionRequirement(
						minimumVersion, nodeInformation.getVersion())) {

					_log.error(
						StringBundler.concat(
							"Elasticsearch node ", nodeInformation.getName(),
							" does not meet the minimum version requirement ",
							"of ", minimumVersionString));

					System.exit(1);
				}
			}
		}
	}

	private String _createAutoCreateIndexSetting(boolean enable)
		throws IOException {

		String currentValue = _getAutoCreateIndexSetting();
		String disableAutoCreateLiferayIndexPattern = StringBundler.concat(
			StringPool.MINUS, _indexNameBuilder.getIndexNamePrefix(),
			StringPool.STAR);
		String enableAutoCreateLiferayIndexPattern = StringBundler.concat(
			StringPool.PLUS, _indexNameBuilder.getIndexNamePrefix(),
			StringPool.STAR);

		if (enable) {
			if (Validator.isBlank(currentValue) ||
				currentValue.equals(StringPool.STAR) ||
				StringUtil.equalsIgnoreCase(currentValue, "true") ||
				currentValue.contains(enableAutoCreateLiferayIndexPattern)) {

				return currentValue;
			}
			else if (StringUtil.equalsIgnoreCase(currentValue, "false")) {
				return enableAutoCreateLiferayIndexPattern;
			}
			else if (currentValue.contains(
						disableAutoCreateLiferayIndexPattern)) {

				return StringUtil.replace(
					currentValue, disableAutoCreateLiferayIndexPattern,
					enableAutoCreateLiferayIndexPattern);
			}

			return StringBundler.concat(
				enableAutoCreateLiferayIndexPattern, StringPool.COMMA_AND_SPACE,
				currentValue);
		}

		if (Validator.isBlank(currentValue) ||
			currentValue.equals(StringPool.STAR) ||
			StringUtil.equalsIgnoreCase(currentValue, "true")) {

			return StringBundler.concat(
				disableAutoCreateLiferayIndexPattern,
				StringPool.COMMA_AND_SPACE, StringPool.STAR);
		}
		else if (StringUtil.equalsIgnoreCase(currentValue, "false") ||
				 currentValue.contains(disableAutoCreateLiferayIndexPattern)) {

			return currentValue;
		}
		else if (currentValue.contains(enableAutoCreateLiferayIndexPattern)) {
			return StringUtil.replace(
				currentValue, enableAutoCreateLiferayIndexPattern,
				disableAutoCreateLiferayIndexPattern);
		}

		return StringBundler.concat(
			disableAutoCreateLiferayIndexPattern, StringPool.COMMA_AND_SPACE,
			currentValue);
	}

	private String _getAutoCreateIndexSetting() throws IOException {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		ClusterClient clusterClient = restHighLevelClient.cluster();

		ClusterGetSettingsResponse clusterGetSettingsResponse =
			clusterClient.getSettings(
				new ClusterGetSettingsRequest(), RequestOptions.DEFAULT);

		Settings settings = clusterGetSettingsResponse.getPersistentSettings();

		return settings.get("action.auto_create_index");
	}

	private Collection<Long> _getIndexedCompanyIds() {
		Collection<Long> companyIds = new ArrayList<>();

		String firstIndexName = _indexNameBuilder.getIndexName(
			CompanyConstants.SYSTEM);

		String prefix = firstIndexName.substring(
			0, firstIndexName.length() - 1);

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(
				new GetIndexIndexRequest(prefix + StringPool.STAR));

		for (String indexName : getIndexIndexResponse.getIndexNames()) {
			long companyId = GetterUtil.getLong(
				StringUtil.removeSubstring(indexName, prefix));

			if (companyId == 0) {
				continue;
			}

			companyIds.add(companyId);
		}

		return companyIds;
	}

	private boolean _hasBackupRepository() {
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest =
			new GetSnapshotRepositoriesRequest(_BACKUP_REPOSITORY_NAME);

		GetSnapshotRepositoriesResponse getSnapshotRepositoriesResponse =
			_searchEngineAdapter.execute(getSnapshotRepositoriesRequest);

		List<SnapshotRepositoryDetails> snapshotRepositoryDetailsList =
			getSnapshotRepositoriesResponse.getSnapshotRepositoryDetails();

		return !snapshotRepositoryDetailsList.isEmpty();
	}

	private void _putTimestampPipeline() {
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

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		IngestClient ingestClient = restHighLevelClient.ingest();

		try {
			ingestClient.putPipeline(
				putPipelineRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			_log.error("Unable to put timestamp pipeline", ioException);
		}
	}

	private void _validateBackupName(String backupName) throws SearchException {
		if (Validator.isNull(backupName)) {
			throw new SearchException(
				"Backup name must not be an empty string");
		}

		if (StringUtil.contains(backupName, StringPool.COMMA)) {
			throw new SearchException("Backup name must not contain comma");
		}

		if (StringUtil.startsWith(backupName, StringPool.DASH)) {
			throw new SearchException("Backup name must not start with dash");
		}

		if (StringUtil.contains(backupName, StringPool.POUND)) {
			throw new SearchException("Backup name must not contain pounds");
		}

		if (StringUtil.contains(backupName, StringPool.SPACE)) {
			throw new SearchException("Backup name must not contain spaces");
		}

		if (StringUtil.contains(backupName, StringPool.TAB)) {
			throw new SearchException("Backup name must not contain tabs");
		}

		for (char c : backupName.toCharArray()) {
			if (Strings.INVALID_FILENAME_CHARS.contains(c)) {
				throw new SearchException(
					"Backup name must not contain invalid file name " +
						"characters");
			}
		}
	}

	private void _waitForYellowStatus() {
		long timeout = 30 * Time.SECOND;

		if (PortalRunMode.isTestMode()) {
			timeout = Time.HOUR;
		}

		HealthClusterRequest healthClusterRequest = new HealthClusterRequest();

		healthClusterRequest.setTimeout(timeout);
		healthClusterRequest.setWaitForClusterHealthStatus(
			ClusterHealthStatus.YELLOW);

		HealthClusterResponse healthClusterResponse =
			_searchEngineAdapter.execute(healthClusterRequest);

		if (healthClusterResponse.getClusterHealthStatus() ==
				ClusterHealthStatus.RED) {

			throw new IllegalStateException(
				"Unable to initialize Elasticsearch cluster: " +
					healthClusterResponse);
		}
	}

	private static final String _BACKUP_REPOSITORY_NAME = "liferay_backup";

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchSearchEngine.class);

	private static final Snapshot<CrossClusterReplicationHelper>
		_crossClusterReplicationHelperSnapshot = new Snapshot(
			ElasticsearchSearchEngine.class,
			CrossClusterReplicationHelper.class, null, true);

	@Reference
	private CompanyIndexHelper _companyIndexHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ElasticsearchConfigurationWrapper
		_elasticsearchConfigurationWrapper;

	@Reference
	private ElasticsearchConnectionManager _elasticsearchConnectionManager;

	private IndexFactory _indexFactory;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private IndexSearcher _indexSearcher;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private IndexWriter _indexWriter;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}