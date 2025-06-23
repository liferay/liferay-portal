/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.AnalyzeIndexRequestExecutorTest;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class RestoreSnapshotRequestExecutorImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			AnalyzeIndexRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testCreateRestoreSnapshotRequest() {
		RestoreSnapshotRequest restoreSnapshotRequest =
			new RestoreSnapshotRequest("repositoryName", "snapshotName");

		restoreSnapshotRequest.setIncludeAliases(true);
		restoreSnapshotRequest.setPartialRestore(true);
		restoreSnapshotRequest.setRestoreGlobalState(true);
		restoreSnapshotRequest.setWaitForCompletion(true);
		restoreSnapshotRequest.setIndexNames("index1", "index2");

		RestoreSnapshotRequestExecutor restoreSnapshotRequestExecutor =
			new RestoreSnapshotRequestExecutor(_elasticsearchFixture);

		org.elasticsearch.action.admin.cluster.snapshots.restore.
			RestoreSnapshotRequest elasticsearchRestoreSnapshotRequest =
				restoreSnapshotRequestExecutor.createRestoreSnapshotRequest(
					restoreSnapshotRequest);

		Assert.assertArrayEquals(
			restoreSnapshotRequest.getIndexNames(),
			elasticsearchRestoreSnapshotRequest.indices());
		Assert.assertEquals(
			restoreSnapshotRequest.isIncludeAliases(),
			elasticsearchRestoreSnapshotRequest.includeAliases());
		Assert.assertEquals(
			restoreSnapshotRequest.isPartialRestore(),
			elasticsearchRestoreSnapshotRequest.partial());
		Assert.assertEquals(
			restoreSnapshotRequest.getRepositoryName(),
			elasticsearchRestoreSnapshotRequest.repository());
		Assert.assertEquals(
			restoreSnapshotRequest.isRestoreGlobalState(),
			elasticsearchRestoreSnapshotRequest.includeGlobalState());
		Assert.assertEquals(
			restoreSnapshotRequest.getSnapshotName(),
			elasticsearchRestoreSnapshotRequest.snapshot());
		Assert.assertEquals(
			restoreSnapshotRequest.isWaitForCompletion(),
			elasticsearchRestoreSnapshotRequest.waitForCompletion());
	}

	private ElasticsearchFixture _elasticsearchFixture;

}