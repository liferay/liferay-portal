/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.snapshot.GetSnapshotRequest;

/**
 * @author Michael C. Han
 */
public class GetSnapshotsRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCreateGetSnapshotsRequest() {
		GetSnapshotsRequest getSnapshotsRequest = new GetSnapshotsRequest(
			"repositoryName");

		getSnapshotsRequest.setIgnoreUnavailable(true);
		getSnapshotsRequest.setSnapshotNames("snapshotName1", "snapshotName2");
		getSnapshotsRequest.setVerbose(true);

		GetSnapshotsRequestExecutor getSnapshotsRequestExecutor =
			new GetSnapshotsRequestExecutor(openSearchConnectionManager);

		GetSnapshotRequest openSearchGetSnapshotRequest =
			getSnapshotsRequestExecutor.createGetSnapshotRequest(
				getSnapshotsRequest);

		Assert.assertEquals(
			getSnapshotsRequest.getRepositoryName(),
			openSearchGetSnapshotRequest.repository());
		Assert.assertArrayEquals(
			getSnapshotsRequest.getSnapshotNames(),
			ArrayUtil.toStringArray(openSearchGetSnapshotRequest.snapshot()));
		Assert.assertEquals(
			getSnapshotsRequest.isIgnoreUnavailable(),
			openSearchGetSnapshotRequest.ignoreUnavailable());
		Assert.assertEquals(
			getSnapshotsRequest.isVerbose(),
			openSearchGetSnapshotRequest.verbose());
	}

}