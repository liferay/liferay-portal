/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCreatePutRepositoryRequest() {
		CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest(
			"repositoryName", "snapshotName");

		createSnapshotRequest.setIndexNames("index1", "index2");
		createSnapshotRequest.setWaitForCompletion(true);

		CreateSnapshotRequestExecutor createSnapshotRequestExecutor =
			new CreateSnapshotRequestExecutor(openSearchConnectionManager);

		org.opensearch.client.opensearch.snapshot.CreateSnapshotRequest
			openSearchCreateSnapshotRequest =
				createSnapshotRequestExecutor.createCreateSnapshotRequest(
					createSnapshotRequest);

		Assert.assertArrayEquals(
			createSnapshotRequest.getIndexNames(),
			ArrayUtil.toStringArray(openSearchCreateSnapshotRequest.indices()));
		Assert.assertEquals(
			createSnapshotRequest.getRepositoryName(),
			openSearchCreateSnapshotRequest.repository());
		Assert.assertEquals(
			createSnapshotRequest.getSnapshotName(),
			openSearchCreateSnapshotRequest.snapshot());
		Assert.assertEquals(
			createSnapshotRequest.isWaitForCompletion(),
			openSearchCreateSnapshotRequest.waitForCompletion());
	}

}