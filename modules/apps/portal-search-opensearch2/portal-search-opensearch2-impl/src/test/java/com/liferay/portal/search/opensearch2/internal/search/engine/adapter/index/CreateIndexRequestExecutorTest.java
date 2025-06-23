/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class CreateIndexRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			TEST_INDEX_NAME);

		createIndexRequest.setSource(
			JSONUtil.put(
				"settings",
				JSONUtil.put(
					"analysis",
					JSONUtil.put(
						"analyzer",
						JSONUtil.put(
							"content",
							JSONUtil.put(
								"tokenizer", "whitespace"
							).put(
								"type", "custom"
							))))
			).toString());

		CreateIndexRequestExecutor createIndexRequestExecutor =
			new CreateIndexRequestExecutor(
				new JSONFactoryImpl(), openSearchConnectionManager);

		org.opensearch.client.opensearch.indices.CreateIndexRequest
			openSearchCreateIndexRequest =
				createIndexRequestExecutor.createCreateIndexRequest(
					createIndexRequest);

		Assert.assertEquals(
			TEST_INDEX_NAME, openSearchCreateIndexRequest.index());
	}

}