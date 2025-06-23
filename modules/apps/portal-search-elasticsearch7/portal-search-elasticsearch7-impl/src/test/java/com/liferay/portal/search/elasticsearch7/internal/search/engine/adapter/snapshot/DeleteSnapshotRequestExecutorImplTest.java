/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.AnalyzeIndexRequestExecutorTest;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class DeleteSnapshotRequestExecutorImplTest {

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
	public void testCreateDeleteSnapshotRequest() {
		DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest(
			"repository", "snapshot");

		DeleteSnapshotRequestExecutor deleteSnapshotRequestExecutor =
			new DeleteSnapshotRequestExecutor(_elasticsearchFixture);

		org.elasticsearch.action.admin.cluster.snapshots.delete.
			DeleteSnapshotRequest elasticDeleteSnapshotRequest =
				deleteSnapshotRequestExecutor.createDeleteSnapshotRequest(
					deleteSnapshotRequest);

		Assert.assertEquals(
			deleteSnapshotRequest.getRepositoryName(),
			elasticDeleteSnapshotRequest.repository());
		Assert.assertEquals(
			deleteSnapshotRequest.getSnapshotName(),
			elasticDeleteSnapshotRequest.snapshots()[0]);
	}

	private ElasticsearchFixture _elasticsearchFixture;

}