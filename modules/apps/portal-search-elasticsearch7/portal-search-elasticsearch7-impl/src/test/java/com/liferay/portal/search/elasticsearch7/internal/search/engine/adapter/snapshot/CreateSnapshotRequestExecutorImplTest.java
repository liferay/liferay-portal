/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.AnalyzeIndexRequestExecutorTest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRequestExecutorImplTest {

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
	public void testCreatePutRepositoryRequest() {
		CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest(
			"name", "location");

		createSnapshotRequest.setIndexNames("index1", "index2");
		createSnapshotRequest.setWaitForCompletion(true);

		CreateSnapshotRequestExecutor createSnapshotRequestExecutor =
			new CreateSnapshotRequestExecutor(_elasticsearchFixture);

		org.elasticsearch.action.admin.cluster.snapshots.create.
			CreateSnapshotRequest elasticsearchCreateSnapshotRequest =
				createSnapshotRequestExecutor.createCreateSnapshotRequest(
					createSnapshotRequest);

		Assert.assertArrayEquals(
			createSnapshotRequest.getIndexNames(),
			elasticsearchCreateSnapshotRequest.indices());
		Assert.assertEquals(
			createSnapshotRequest.getRepositoryName(),
			elasticsearchCreateSnapshotRequest.repository());
		Assert.assertEquals(
			createSnapshotRequest.getSnapshotName(),
			elasticsearchCreateSnapshotRequest.snapshot());
		Assert.assertEquals(
			createSnapshotRequest.isWaitForCompletion(),
			elasticsearchCreateSnapshotRequest.waitForCompletion());
	}

	private ElasticsearchFixture _elasticsearchFixture;

}