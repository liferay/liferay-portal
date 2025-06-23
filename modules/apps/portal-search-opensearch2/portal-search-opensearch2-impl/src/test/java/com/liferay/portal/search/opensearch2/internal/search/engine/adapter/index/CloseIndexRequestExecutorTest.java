/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.Time;

/**
 * @author Michael C. Han
 */
public class CloseIndexRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() {
		CloseIndexRequest closeIndexRequest = new CloseIndexRequest(
			TEST_INDEX_NAME);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setIgnoreUnavailable(true);

		closeIndexRequest.setIndicesOptions(indicesOptions);

		closeIndexRequest.setTimeout(100);

		CloseIndexRequestExecutor closeIndexRequestExecutor =
			new CloseIndexRequestExecutor(openSearchConnectionManager);

		org.opensearch.client.opensearch.indices.CloseIndexRequest
			openSearchCloseIndexRequest =
				closeIndexRequestExecutor.createCloseIndexRequest(
					closeIndexRequest);

		Assert.assertArrayEquals(
			closeIndexRequest.getIndexNames(),
			ArrayUtil.toStringArray(openSearchCloseIndexRequest.index()));

		Time masterTimeout = openSearchCloseIndexRequest.masterTimeout();

		Assert.assertEquals(
			closeIndexRequest.getTimeout() + "ms", masterTimeout.time());

		Time timeout = openSearchCloseIndexRequest.timeout();

		Assert.assertEquals(
			closeIndexRequest.getTimeout() + "ms", timeout.time());
	}

}