/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.ElasticsearchCatClient;
import co.elastic.clients.elasticsearch.cat.RepositoriesResponse;
import co.elastic.clients.elasticsearch.cat.repositories.RepositoriesRecord;
import co.elastic.clients.elasticsearch.snapshot.CreateSnapshotRequest;
import co.elastic.clients.elasticsearch.snapshot.DeleteRepositoryRequest;
import co.elastic.clients.elasticsearch.snapshot.DeleteSnapshotRequest;
import co.elastic.clients.elasticsearch.snapshot.ElasticsearchSnapshotClient;
import co.elastic.clients.elasticsearch.snapshot.GetSnapshotRequest;
import co.elastic.clients.elasticsearch.snapshot.GetSnapshotResponse;
import co.elastic.clients.elasticsearch.snapshot.SnapshotInfo;

import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch8.internal.ElasticsearchSearchEngine;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch8.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class ElasticsearchSearchEngineBackupTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				ElasticsearchSearchEngineBackupTest.class.getSimpleName()
			).build();

		ElasticsearchSearchEngineFixture elasticsearchSearchEngineFixture =
			new ElasticsearchSearchEngineFixture(
				elasticsearchConnectionFixture);

		elasticsearchSearchEngineFixture.setUp();

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;

		_elasticsearchSearchEngineFixture = elasticsearchSearchEngineFixture;

		if (_hasBackupRepository()) {
			_deleteBackupRepository();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchSearchEngineFixture.tearDown();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
		}
	}

	@Test
	public void testBackup() throws SearchException {
		ElasticsearchSearchEngine elasticsearchSearchEngine =
			_elasticsearchSearchEngineFixture.getElasticsearchSearchEngine();

		String snapshotName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		long companyId = RandomTestUtil.randomLong();

		elasticsearchSearchEngine.initialize(companyId);

		elasticsearchSearchEngine.backup(companyId, snapshotName);

		GetSnapshotResponse getSnapshotResponse = _getGetSnapshotResponse(
			true, _BACKUP_REPOSITORY_NAME, new String[] {snapshotName});

		List<SnapshotInfo> snapshotInfos = getSnapshotResponse.snapshots();

		Assert.assertTrue(snapshotInfos.size() == 1);

		_deleteSnapshot(_BACKUP_REPOSITORY_NAME, snapshotName);
	}

	@Test
	public void testRestore() throws SearchException {
		ElasticsearchSearchEngine elasticsearchSearchEngine =
			_elasticsearchSearchEngineFixture.getElasticsearchSearchEngine();

		long companyId = RandomTestUtil.randomLong();

		elasticsearchSearchEngine.initialize(companyId);

		elasticsearchSearchEngine.createBackupRepository();

		_createSnapshot(
			_BACKUP_REPOSITORY_NAME, "restore_test", true,
			String.valueOf(companyId));

		elasticsearchSearchEngine.restore(companyId, "restore_test");

		_deleteSnapshot(_BACKUP_REPOSITORY_NAME, "restore_test");
	}

	protected ElasticsearchSnapshotClient getSnapshotClient() {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchConnectionFixture.getElasticsearchClient();

		return elasticsearchClient.snapshot();
	}

	private static void _deleteBackupRepository() throws Exception {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchConnectionFixture.getElasticsearchClient();

		ElasticsearchSnapshotClient elasticsearchSnapshotClient =
			elasticsearchClient.snapshot();

		elasticsearchSnapshotClient.deleteRepository(
			DeleteRepositoryRequest.of(
				deleteRepositoryRequest -> deleteRepositoryRequest.name(
					_BACKUP_REPOSITORY_NAME)));
	}

	private static boolean _hasBackupRepository() {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchConnectionFixture.getElasticsearchClient();

		ElasticsearchCatClient elasticsearchCatClient =
			elasticsearchClient.cat();

		try {
			RepositoriesResponse repositoriesResponse =
				elasticsearchCatClient.repositories();

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

	private void _createSnapshot(
		String repositoryName, String snapshotName, boolean waitForCompletion,
		String... indexNames) {

		CreateSnapshotRequest.Builder builder =
			new CreateSnapshotRequest.Builder();

		builder.indices(ListUtil.fromArray(indexNames));
		builder.repository(repositoryName);
		builder.snapshot(snapshotName);
		builder.waitForCompletion(waitForCompletion);

		ElasticsearchSnapshotClient elasticsearchSnapshotClient =
			getSnapshotClient();

		try {
			elasticsearchSnapshotClient.create(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _deleteSnapshot(String repository, String snapshot) {
		DeleteSnapshotRequest.Builder builder =
			new DeleteSnapshotRequest.Builder();

		builder.repository(repository);
		builder.snapshot(snapshot);

		ElasticsearchSnapshotClient elasticsearchSnapshotClient =
			getSnapshotClient();

		try {
			elasticsearchSnapshotClient.delete(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private GetSnapshotResponse _getGetSnapshotResponse(
		boolean ignoreUnavailable, String repository, String[] snapshots) {

		GetSnapshotRequest.Builder builder = new GetSnapshotRequest.Builder();

		builder.ignoreUnavailable(ignoreUnavailable);
		builder.repository(repository);
		builder.snapshot(ListUtil.fromArray(snapshots));

		ElasticsearchSnapshotClient elasticsearchSnapshotClient =
			getSnapshotClient();

		try {
			return elasticsearchSnapshotClient.get(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String _BACKUP_REPOSITORY_NAME = "liferay_backup";

	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;
	private static ElasticsearchSearchEngineFixture
		_elasticsearchSearchEngineFixture;

}