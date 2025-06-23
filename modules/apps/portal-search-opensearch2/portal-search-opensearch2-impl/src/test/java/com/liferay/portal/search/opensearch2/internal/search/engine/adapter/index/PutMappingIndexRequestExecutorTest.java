/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.indices.PutMappingRequest;

/**
 * @author Dylan Rebelak
 */
public class PutMappingIndexRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() throws Exception {
		PutMappingIndexRequest putMappingIndexRequest =
			new PutMappingIndexRequest(
				new String[] {TEST_INDEX_NAME},
				JSONUtil.put(
					"properties",
					JSONUtil.put(_FIELD_NAME, JSONUtil.put("type", "text"))
				).toString());

		PutMappingIndexRequestExecutor putMappingIndexRequestExecutor =
			new PutMappingIndexRequestExecutor(
				new JSONFactoryImpl(), openSearchConnectionManager);

		PutMappingRequest putMappingRequest =
			putMappingIndexRequestExecutor.createPutMappingRequest(
				putMappingIndexRequest);

		Assert.assertArrayEquals(
			new String[] {TEST_INDEX_NAME},
			ArrayUtil.toStringArray(putMappingRequest.index()));

		Map<String, Property> properties = putMappingRequest.properties();

		Assert.assertNotNull(properties.get(_FIELD_NAME));
	}

	private static final String _FIELD_NAME = "testField";

}