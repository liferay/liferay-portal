/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class DeleteSnapshotRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCreateDeleteSnapshotRequest() {
		DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest(
			"repositoryName", "snapshotName");

		DeleteSnapshotRequestExecutor deleteSnapshotRequestExecutor =
			new DeleteSnapshotRequestExecutor(openSearchConnectionManager);

		org.opensearch.client.opensearch.snapshot.DeleteSnapshotRequest
			openSearchDeleteSnapshotRequest =
				deleteSnapshotRequestExecutor.createDeleteSnapshotRequest(
					deleteSnapshotRequest);

		Assert.assertEquals(
			deleteSnapshotRequest.getRepositoryName(),
			openSearchDeleteSnapshotRequest.repository());
		Assert.assertEquals(
			deleteSnapshotRequest.getSnapshotName(),
			openSearchDeleteSnapshotRequest.snapshot());
	}

}