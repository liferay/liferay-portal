/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.task.progress;

import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Matija Petanjek
 */
public class CSVBatchEngineTaskProgressImplTest
	extends BaseBatchEngineTaskProgressImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTotalItems() throws Exception {
		_testGetTotalItemsCount(PRODUCTS_COUNT);
		_testGetTotalItemsCount(0);
	}

	private void _testGetTotalItemsCount(int expectedTotalItemsCount)
		throws Exception {

		StringBundler sb = new StringBundler();

		Iterator<String> iterator = productJSONObject.keys();

		while (iterator.hasNext()) {
			sb.append(iterator.next());

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		for (int i = 0; i < expectedTotalItemsCount; i++) {
			sb.append(StringPool.NEW_LINE);

			for (String key : productJSONObject.keySet()) {
				sb.append(productJSONObject.getString(key));
				sb.append(StringPool.COMMA);
			}
		}

		String content = sb.toString();

		Assert.assertEquals(
			expectedTotalItemsCount,
			_batchEngineTaskProgress.getTotalItemsCount(
				compress(
					content.getBytes(),
					BatchEngineTaskContentType.CSV.toString())));
	}

	private static final BatchEngineTaskProgress _batchEngineTaskProgress =
		new CSVBatchEngineTaskProgressImpl();

}