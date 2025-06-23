/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.snapshot.RestoreRequest;

/**
 * @author Michael C. Han
 */
public class RestoreSnapshotRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCreateRestoreSnapshotRequest() {
		RestoreSnapshotRequest restoreSnapshotRequest =
			new RestoreSnapshotRequest("repositoryName", "snapshotName");

		restoreSnapshotRequest.setIncludeAliases(true);
		restoreSnapshotRequest.setIndexNames("index1", "index2");
		restoreSnapshotRequest.setPartialRestore(true);
		restoreSnapshotRequest.setRestoreGlobalState(true);
		restoreSnapshotRequest.setWaitForCompletion(true);

		RestoreSnapshotRequestExecutor restoreSnapshotRequestExecutor =
			new RestoreSnapshotRequestExecutor(openSearchConnectionManager);

		RestoreRequest restoreRequest =
			restoreSnapshotRequestExecutor.createRestoreRequest(
				restoreSnapshotRequest);

		Assert.assertEquals(
			restoreSnapshotRequest.isIncludeAliases(),
			restoreRequest.includeAliases());
		Assert.assertEquals(
			restoreSnapshotRequest.isRestoreGlobalState(),
			restoreRequest.includeGlobalState());
		Assert.assertArrayEquals(
			restoreSnapshotRequest.getIndexNames(),
			ArrayUtil.toStringArray(restoreRequest.indices()));
		Assert.assertEquals(
			restoreSnapshotRequest.isPartialRestore(),
			restoreRequest.partial());
		Assert.assertEquals(
			restoreSnapshotRequest.getRepositoryName(),
			restoreRequest.repository());
		Assert.assertEquals(
			restoreSnapshotRequest.getSnapshotName(),
			restoreRequest.snapshot());
		Assert.assertEquals(
			restoreSnapshotRequest.isWaitForCompletion(),
			restoreRequest.waitForCompletion());
	}

}