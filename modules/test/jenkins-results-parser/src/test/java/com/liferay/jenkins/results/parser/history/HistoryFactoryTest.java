/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class HistoryFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewTestClassHistory() {
		BatchHistory integrationBatchHistory = _mockBatchHistory(
			"modules-integration", "master");
		BatchHistory unitBatchHistory = _mockBatchHistory(
			"modules-unit", "master");

		JSONObject jsonObject = new JSONObject();

		String testClassName = RandomTestUtil.randomString();

		TestClassHistory testClassHistory = HistoryFactory.newTestClassHistory(
			unitBatchHistory, jsonObject, testClassName);

		Assert.assertSame(
			testClassHistory,
			HistoryFactory.newTestClassHistory(
				unitBatchHistory, jsonObject, testClassName));

		Assert.assertNotSame(
			testClassHistory,
			HistoryFactory.newTestClassHistory(
				integrationBatchHistory, jsonObject, testClassName));
	}

	@Test
	public void testNewTestTaskHistory() {
		BatchHistory integrationBatchHistory = _mockBatchHistory(
			"modules-integration", "master");
		BatchHistory unitBatchHistory = _mockBatchHistory(
			"modules-unit", "master");

		JSONObject jsonObject = new JSONObject();

		String testTaskName = RandomTestUtil.randomString();

		TestTaskHistory testTaskHistory = HistoryFactory.newTestTaskHistory(
			unitBatchHistory, jsonObject, testTaskName);

		Assert.assertSame(
			testTaskHistory,
			HistoryFactory.newTestTaskHistory(
				unitBatchHistory, jsonObject, testTaskName));

		Assert.assertNotSame(
			testTaskHistory,
			HistoryFactory.newTestTaskHistory(
				integrationBatchHistory, jsonObject, testTaskName));
	}

	private BatchHistory _mockBatchHistory(
		String batchName, String portalUpstreamBranchName) {

		BatchHistory batchHistory = Mockito.mock(BatchHistory.class);

		Mockito.when(
			batchHistory.getPortalUpstreamBranchName()
		).thenReturn(
			portalUpstreamBranchName
		);

		Mockito.when(
			batchHistory.getBatchName()
		).thenReturn(
			batchName
		);

		return batchHistory;
	}

}