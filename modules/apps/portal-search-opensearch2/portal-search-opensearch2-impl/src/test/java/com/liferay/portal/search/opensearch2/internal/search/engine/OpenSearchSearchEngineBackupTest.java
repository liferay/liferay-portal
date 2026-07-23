/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine;

import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchSearchEngine;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cat.OpenSearchCatClient;
import org.opensearch.client.opensearch.cat.RepositoriesResponse;
import org.opensearch.client.opensearch.cat.repositories.RepositoriesRecord;
import org.opensearch.client.opensearch.snapshot.CreateSnapshotRequest;
import org.opensearch.client.opensearch.snapshot.DeleteRepositoryRequest;
import org.opensearch.client.opensearch.snapshot.DeleteSnapshotRequest;
import org.opensearch.client.opensearch.snapshot.GetSnapshotRequest;
import org.opensearch.client.opensearch.snapshot.GetSnapshotResponse;
import org.opensearch.client.opensearch.snapshot.OpenSearchSnapshotClient;
import org.opensearch.client.opensearch.snapshot.SnapshotInfo;

/**
 * @author André de Oliveira
 */
public class OpenSearchSearchEngineBackupTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		OpenSearchSearchEngineFixture openSearchSearchEngineFixture =
			new OpenSearchSearchEngineFixture(openSearchConnectionManager);

		openSearchSearchEngineFixture.setUp();

		_openSearchSearchEngineFixture = openSearchSearchEngineFixture;

		if (_hasBackupRepository()) {
			_deleteBackupRepository();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_openSearchSearchEngineFixture.tearDown();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
		}
	}

	@Test
	public void testBackup() throws SearchException {
		OpenSearchSearchEngine openSearchSearchEngine =
			_openSearchSearchEngineFixture.getOpenSearchSearchEngine();

		String snapshotName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		long companyId = RandomTestUtil.randomLong();

		openSearchSearchEngine.initialize(companyId);

		openSearchSearchEngine.backup(companyId, snapshotName);

		GetSnapshotResponse getSnapshotResponse = _getGetSnapshotResponse(
			true, _BACKUP_REPOSITORY_NAME, new String[] {snapshotName});

		List<SnapshotInfo> snapshotInfos = getSnapshotResponse.snapshots();

		Assert.assertTrue(snapshotInfos.size() == 1);

		_deleteSnapshot(_BACKUP_REPOSITORY_NAME, snapshotName);
	}

	@Test
	public void testRestore() throws SearchException {
		OpenSearchSearchEngine openSearchSearchEngine =
			_openSearchSearchEngineFixture.getOpenSearchSearchEngine();

		long companyId = RandomTestUtil.randomLong();

		openSearchSearchEngine.initialize(companyId);

		openSearchSearchEngine.createBackupRepository();

		_createSnapshot(
			_BACKUP_REPOSITORY_NAME, "restore_test", true,
			String.valueOf(companyId));

		openSearchSearchEngine.restore(companyId, "restore_test");

		_deleteSnapshot(_BACKUP_REPOSITORY_NAME, "restore_test");
	}

	protected OpenSearchSnapshotClient getOpenSearchSnapshotClient() {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		return openSearchClient.snapshot();
	}

	private static void _deleteBackupRepository() throws Exception {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		OpenSearchSnapshotClient openSearchSnapshotClient =
			openSearchClient.snapshot();

		openSearchSnapshotClient.deleteRepository(
			DeleteRepositoryRequest.of(
				deleteRepositoryRequest -> deleteRepositoryRequest.name(
					_BACKUP_REPOSITORY_NAME)));
	}

	private static boolean _hasBackupRepository() {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

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

	private void _createSnapshot(
		String repositoryName, String snapshotName, boolean waitForCompletion,
		String... indexNames) {

		CreateSnapshotRequest.Builder builder =
			new CreateSnapshotRequest.Builder();

		builder.indices(ListUtil.fromArray(indexNames));
		builder.repository(repositoryName);
		builder.snapshot(snapshotName);
		builder.waitForCompletion(waitForCompletion);

		OpenSearchSnapshotClient snapshotClient = getOpenSearchSnapshotClient();

		try {
			snapshotClient.create(builder.build());
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

		OpenSearchSnapshotClient openSearchSnapshotClient =
			getOpenSearchSnapshotClient();

		try {
			openSearchSnapshotClient.delete(builder.build());
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

		OpenSearchSnapshotClient openSearchSnapshotClient =
			getOpenSearchSnapshotClient();

		try {
			return openSearchSnapshotClient.get(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String _BACKUP_REPOSITORY_NAME = "liferay_backup";

	private static OpenSearchSearchEngineFixture _openSearchSearchEngineFixture;

}