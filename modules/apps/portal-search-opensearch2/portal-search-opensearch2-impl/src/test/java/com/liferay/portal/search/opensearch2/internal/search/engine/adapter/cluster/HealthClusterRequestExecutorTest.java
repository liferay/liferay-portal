/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch.cluster.HealthRequest;

/**
 * @author Dylan Rebelak
 */
public class HealthClusterRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testClusterRequestTranslation() {
		HealthClusterRequest healthClusterRequest = new HealthClusterRequest(
			TEST_INDEX_NAME);

		healthClusterRequest.setTimeout(1000);
		healthClusterRequest.setWaitForClusterHealthStatus(
			ClusterHealthStatus.GREEN);

		HealthClusterRequestExecutor healthClusterRequestExecutor =
			new HealthClusterRequestExecutor(openSearchConnectionManager);

		HealthRequest healthRequest =
			healthClusterRequestExecutor.createHealthRequest(
				healthClusterRequest);

		Assert.assertArrayEquals(
			new String[] {TEST_INDEX_NAME},
			ArrayUtil.toStringArray(healthRequest.index()));

		Assert.assertEquals(
			healthClusterRequest.getWaitForClusterHealthStatus(),
			ClusterHealthStatusTranslatorUtil.translate(
				healthRequest.waitForStatus()));

		String expectedTimeout = "1000ms";

		Time masterTimeout = healthRequest.masterTimeout();

		Assert.assertEquals(expectedTimeout, masterTimeout.time());

		Time timeout = healthRequest.timeout();

		Assert.assertEquals(expectedTimeout, timeout.time());
	}

}