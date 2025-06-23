/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.snapshot.GetRepositoryRequest;

/**
 * @author Michael C. Han
 */
public class GetSnapshotRepositoriesRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testGetSnapshotRepositoriesRequest() {
		GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest =
			new GetSnapshotRepositoriesRequest(
				"repositoryName1", "repositoryName2");

		GetSnapshotRepositoriesRequestExecutor
			getSnapshotRepositoriesRequestExecutor =
				new GetSnapshotRepositoriesRequestExecutor(
					openSearchConnectionManager);

		GetRepositoryRequest getRepositoriesRequest =
			getSnapshotRepositoriesRequestExecutor.createGetRepositoryRequest(
				getSnapshotRepositoriesRequest);

		Assert.assertArrayEquals(
			getSnapshotRepositoriesRequest.getRepositoryNames(),
			ArrayUtil.toStringArray(getRepositoriesRequest.name()));
	}

}