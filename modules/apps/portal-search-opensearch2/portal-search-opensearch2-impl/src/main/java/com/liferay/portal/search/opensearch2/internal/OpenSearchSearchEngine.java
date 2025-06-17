/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.ccr.CrossClusterReplicationHelper;
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
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotDetails;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotState;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationObserver;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.index.CompanyIndexHelper;
import com.liferay.portal.search.opensearch2.internal.index.IndexFactory;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cat.OpenSearchCatClient;
import org.opensearch.client.opensearch.cat.RepositoriesResponse;
import org.opensearch.client.opensearch.cat.repositories.RepositoriesRecord;
import org.opensearch.client.opensearch.cluster.GetClusterSettingsResponse;
import org.opensearch.client.opensearch.cluster.OpenSearchClusterClient;
import org.opensearch.client.opensearch.cluster.PutClusterSettingsRequest;
import org.opensearch.client.opensearch.ingest.OpenSearchIngestClient;
import org.opensearch.client.opensearch.ingest.Processor;
import org.opensearch.client.opensearch.ingest.PutPipelineRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(
	property = "search.engine.impl=OpenSearch", service = SearchEngine.class
)
public class OpenSearchSearchEngine
	implements OpenSearchConfigurationObserver, SearchEngine {

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
		OpenSearchConfigurationObserver openSearchConfigurationObserver) {

		return _openSearchConfigurationWrapper.compare(
			this, openSearchConfigurationObserver);
	}

	public void createBackupRepository() {
		if (_hasBackupRepository()) {
			return;
		}

		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest =
			new CreateSnapshotRepositoryRequest(
				_BACKUP_REPOSITORY_NAME, "opensearch_backup");

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
		return "OpenSearch";
	}

	@Override
	public void initialize(long companyId) {
		_waitForYellowStatus();

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		boolean created = _indexFactory.initializeIndex(
			companyId, openSearchClient.indices());

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
	public void onOpenSearchConfigurationUpdate() {
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
			OpenSearchClient openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();

			_indexFactory.deleteIndex(companyId, openSearchClient.indices());

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

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		OpenSearchClusterClient openSearchClusterClient =
			openSearchClient.cluster();

		try {
			JsonData jsonData = JsonData.of(
				_createAutoCreateIndexSetting(enable));

			openSearchClusterClient.putSettings(
				PutClusterSettingsRequest.of(
					putClusterSettingsRequest ->
						putClusterSettingsRequest.persistent(
							"action.auto_create_index", jsonData)));
		}
		catch (IOException ioException) {
			String message = StringUtil.toLowerCase(ioException.getMessage());

			if (message.contains("forbidden") ||
				message.contains("unauthorized")) {

				StringBundler sb = new StringBundler(4);

				sb.append("Unable to update cluster auto create index ");
				sb.append("setting due to lack of permissions. This can lead ");
				sb.append("to incorrectly created index mappings: ");

				sb.append(ioException.getMessage());

				_log.error(sb.toString());

				if (_log.isDebugEnabled()) {
					_log.debug(ioException);
				}
			}
			else {
				_log.error(ioException);
			}
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_indexFactory = new IndexFactory(
			_companyIndexHelper, _companyLocalService,
			_openSearchConfigurationWrapper, _openSearchConnectionManager);
		_checkNodeVersions();

		_openSearchConfigurationWrapper.register(this);

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				OpenSearchSearchEngine.class.getClassLoader())) {

			_checkNodeVersions();

			setAutoCreateIndex(false);

			if (StartupHelperUtil.isDBNew()) {
				_companyLocalService.forEachCompanyId(
					companyId -> removeCompany(companyId),
					_getIndexedCompanyIds());
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
		String minimumVersionString =
			_openSearchConfigurationWrapper.minimumRequiredNodeVersion();

		if (minimumVersionString.equals("0.0.0")) {
			String clientVersion =
				_searchEngineInformation.getClientVersionString();

			minimumVersionString = clientVersion.substring(
				0, clientVersion.lastIndexOf("."));
		}

		Version minimumVersion = Version.parseVersion(minimumVersionString);

		List<ConnectionInformation> connectionInformationList =
			_searchEngineInformation.getConnectionInformationList();

		for (ConnectionInformation connectionInformation :
				connectionInformationList) {

			List<NodeInformation> nodeInformationList =
				connectionInformation.getNodeInformationList();

			for (NodeInformation nodeInformation : nodeInformationList) {
				if (!meetsMinimumVersionRequirement(
						minimumVersion, nodeInformation.getVersion())) {

					_log.error(
						StringBundler.concat(
							"OpenSearch node ", nodeInformation.getName(),
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
		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		OpenSearchClusterClient openSearchClusterClient =
			openSearchClient.cluster();

		GetClusterSettingsResponse getClusterSettingsResponse =
			openSearchClusterClient.getSettings();

		Map<String, JsonData> persistentSettings =
			getClusterSettingsResponse.persistent();

		JsonData jsonData = persistentSettings.get("action");

		if (jsonData == null) {
			return null;
		}

		JsonValue jsonValue = jsonData.toJson();

		JsonObject jsonObject = jsonValue.asJsonObject();

		return jsonObject.getString("auto_create_index");
	}

	private long[] _getIndexedCompanyIds() {
		List<Long> companyIds = new ArrayList<>();

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

		return ArrayUtil.toLongArray(companyIds);
	}

	private boolean _hasBackupRepository() {
		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		OpenSearchCatClient openSearchCatClient = openSearchClient.cat();

		try {
			RepositoriesResponse repositoriesResponse =
				openSearchCatClient.repositories();

			List<RepositoriesRecord> repositoriesRecords =
				repositoriesResponse.valueBody();

			for (RepositoriesRecord repositoriesRecord : repositoriesRecords) {
				if (Objects.equals(
						repositoriesRecord.id(), _BACKUP_REPOSITORY_NAME)) {

					return true;
				}
			}

			return false;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _putTimestampPipeline() {
		PutPipelineRequest putPipelineRequest = new PutPipelineRequest.Builder(
		).id(
			"timestamp"
		).description(
			"Adds timestamp to documents"
		).processors(
			new Processor.Builder(
			).set(
				setProcessor -> setProcessor.field(
					"_source.timestamp"
				).value(
					JsonData.of("{{{_ingest.timestamp}}}")
				)
			).build()
		).build();

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIngestClient openSearchIngestClient =
			openSearchClient.ingest();

		try {
			openSearchIngestClient.putPipeline(putPipelineRequest);
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

		if (StringUtil.indexOfAny(backupName, _INVALID_CHARS) > 0) {
			throw new SearchException(
				"Backup name must not contain invalid file name characters");
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
				"Unable to initialize OpenSearch cluster: " +
					healthClusterResponse);
		}
	}

	private static final String _BACKUP_REPOSITORY_NAME = "liferay_backup";

	private static final char[] _INVALID_CHARS = {
		'\\', '/', '*', '?', '"', '<', '>', '|', ' ', ','
	};

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchSearchEngine.class);

	private static final Snapshot<CrossClusterReplicationHelper>
		_crossClusterReplicationHelperSnapshot = new Snapshot(
			OpenSearchSearchEngine.class, CrossClusterReplicationHelper.class,
			null, true);

	@Reference
	private CompanyIndexHelper _companyIndexHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	private IndexFactory _indexFactory;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference(target = "(search.engine.impl=OpenSearch)")
	private IndexSearcher _indexSearcher;

	@Reference(target = "(search.engine.impl=OpenSearch)")
	private IndexWriter _indexWriter;

	@Reference
	private OpenSearchConfigurationWrapper _openSearchConfigurationWrapper;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

	@Reference(target = "(search.engine.impl=OpenSearch)")
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}