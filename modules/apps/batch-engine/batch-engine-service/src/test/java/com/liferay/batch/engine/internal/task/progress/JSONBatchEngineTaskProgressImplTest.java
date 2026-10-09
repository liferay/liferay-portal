/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.task.progress;

import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Matija Petanjek
 */
public class JSONBatchEngineTaskProgressImplTest
	extends BaseBatchEngineTaskProgressImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTotalItems() throws Exception {
		_testGetTotalItemsCount(0, true);
		_testGetTotalItemsCount(PRODUCTS_COUNT, false);
		_testGetTotalItemsCount(0, true);
	}

	private void _testGetTotalItemsCount(
			int expectedTotalItemsCount, boolean invalidJSONSyntax)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		for (int i = 0; i < expectedTotalItemsCount; i++) {
			sb.append(productJSONObject.toString());

			if (i >= (PRODUCTS_COUNT - 1)) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		if (!invalidJSONSyntax) {
			sb.append(StringPool.CLOSE_BRACKET);
		}

		String content = sb.toString();

		Assert.assertEquals(
			expectedTotalItemsCount,
			_batchEngineTaskProgress.getTotalItemsCount(
				compress(
					content.getBytes(),
					BatchEngineTaskContentType.JSON.toString())));
	}

	private static final BatchEngineTaskProgress _batchEngineTaskProgress =
		new JSONBatchEngineTaskProgressImpl();

}