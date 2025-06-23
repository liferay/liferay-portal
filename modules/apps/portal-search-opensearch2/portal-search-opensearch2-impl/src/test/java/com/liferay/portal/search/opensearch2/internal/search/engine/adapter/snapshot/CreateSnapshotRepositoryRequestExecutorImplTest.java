/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.snapshot.CreateRepositoryRequest;
import org.opensearch.client.opensearch.snapshot.RepositorySettings;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRepositoryRequestExecutorImplTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCreatePutRepositoryRequest() {
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest =
			new CreateSnapshotRepositoryRequest(
				"repositoryName", "snapshotName");

		createSnapshotRepositoryRequest.setCompress(true);
		createSnapshotRepositoryRequest.setType("type");
		createSnapshotRepositoryRequest.setVerify(true);

		CreateSnapshotRepositoryRequestExecutor
			createSnapshotRepositoryRequestExecutor =
				new CreateSnapshotRepositoryRequestExecutor(
					openSearchConnectionManager);

		CreateRepositoryRequest createRepositoryRequest =
			createSnapshotRepositoryRequestExecutor.
				createCreateRepositoryRequest(createSnapshotRepositoryRequest);

		RepositorySettings repositorySettings =
			createRepositoryRequest.settings();

		Assert.assertEquals(
			createSnapshotRepositoryRequest.isCompress(),
			repositorySettings.compress());
		Assert.assertEquals(
			String.valueOf(createSnapshotRepositoryRequest.getLocation()),
			repositorySettings.location());

		Assert.assertEquals(
			createSnapshotRepositoryRequest.getName(),
			createRepositoryRequest.name());
		Assert.assertEquals(
			createSnapshotRepositoryRequest.getType(),
			createRepositoryRequest.type());
		Assert.assertEquals(
			createSnapshotRepositoryRequest.isVerify(),
			createRepositoryRequest.verify());
	}

}