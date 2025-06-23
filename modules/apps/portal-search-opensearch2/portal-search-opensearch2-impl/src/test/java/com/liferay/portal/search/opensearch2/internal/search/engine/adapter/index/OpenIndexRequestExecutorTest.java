/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.WaitForActiveShards;
import org.opensearch.client.opensearch.indices.OpenRequest;

/**
 * @author Michael C. Han
 */
public class OpenIndexRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() {
		OpenIndexRequest openIndexRequest = new OpenIndexRequest(
			TEST_INDEX_NAME);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setIgnoreUnavailable(true);

		openIndexRequest.setIndicesOptions(indicesOptions);

		openIndexRequest.setTimeout(100);
		openIndexRequest.setWaitForActiveShards(200);

		OpenIndexRequestExecutor openIndexRequestExecutor =
			new OpenIndexRequestExecutor(openSearchConnectionManager);

		OpenRequest openRequest = openIndexRequestExecutor.createOpenRequest(
			openIndexRequest);

		Assert.assertArrayEquals(
			openIndexRequest.getIndexNames(),
			ArrayUtil.toStringArray(openRequest.index()));

		Time masterTimeout = openRequest.masterTimeout();

		Assert.assertEquals(
			openIndexRequest.getTimeout() + "ms", masterTimeout.time());

		Time timeout = openRequest.timeout();

		Assert.assertEquals(
			openIndexRequest.getTimeout() + "ms", timeout.time());

		WaitForActiveShards waitForActiveShards =
			openRequest.waitForActiveShards();

		Integer count = waitForActiveShards.count();

		Assert.assertEquals(
			openIndexRequest.getWaitForActiveShards(), count.intValue());
	}

}