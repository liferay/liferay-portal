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
public class JSONLBatchEngineTaskProgressImplTest
	extends BaseBatchEngineTaskProgressImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTotalItemsCount() throws Exception {
		_testGetTotalItemsCount(0);
		_testGetTotalItemsCount(PRODUCTS_COUNT);
	}

	private void _testGetTotalItemsCount(int expectedTotalItemsCount)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (int i = 0; i < expectedTotalItemsCount; i++) {
			sb.append(productJSONObject.toString());

			if (i >= (PRODUCTS_COUNT - 1)) {
				continue;
			}

			sb.append(StringPool.NEW_LINE);
		}

		String content = sb.toString();

		Assert.assertEquals(
			expectedTotalItemsCount,
			_batchEngineTaskProgress.getTotalItemsCount(
				compress(
					content.getBytes(),
					BatchEngineTaskContentType.JSONL.toString())));
	}

	private static final BatchEngineTaskProgress _batchEngineTaskProgress =
		new JSONLBatchEngineTaskProgressImpl();

}