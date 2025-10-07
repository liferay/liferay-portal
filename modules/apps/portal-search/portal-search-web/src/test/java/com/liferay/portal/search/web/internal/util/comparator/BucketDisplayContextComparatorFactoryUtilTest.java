/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.search.web.internal.util.comparator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rodrigo Guedes de Souza
 */
public class BucketDisplayContextComparatorFactoryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetBucketDisplayContextComparator() {
		try {
			BucketDisplayContextComparatorFactoryUtil.
				getBucketDisplayContextComparator("invalid");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertNotNull(illegalArgumentException);
		}

		List<BucketDisplayContext> bucketDisplayContexts = Arrays.asList(
			_createBucketDisplayContext("01", 1),
			_createBucketDisplayContext("1", 1),
			_createBucketDisplayContext("11", 1),
			_createBucketDisplayContext("2", 3),
			_createBucketDisplayContext("Allen", 1),
			_createBucketDisplayContext("Aslan", 1),
			_createBucketDisplayContext("albert", 2),
			_createBucketDisplayContext("tom", 1),
			_createBucketDisplayContext("tom", 2),
			_createBucketDisplayContext("Árbol", 2));

		bucketDisplayContexts.sort(
			BucketDisplayContextComparatorFactoryUtil.
				getBucketDisplayContextComparator("count:asc"));

		_assertOrder(
			bucketDisplayContexts,
			List.of(
				"01:1", "1:1", "11:1", "Allen:1", "Aslan:1", "tom:1",
				"albert:2", "Árbol:2", "tom:2", "2:3"));

		bucketDisplayContexts.sort(
			BucketDisplayContextComparatorFactoryUtil.
				getBucketDisplayContextComparator("count:desc"));

		_assertOrder(
			bucketDisplayContexts,
			List.of(
				"2:3", "albert:2", "Árbol:2", "tom:2", "01:1", "1:1", "11:1",
				"Allen:1", "Aslan:1", "tom:1"));

		bucketDisplayContexts.sort(
			BucketDisplayContextComparatorFactoryUtil.
				getBucketDisplayContextComparator("key:asc"));

		_assertOrder(
			bucketDisplayContexts,
			List.of(
				"01:1", "1:1", "2:3", "11:1", "albert:2", "Allen:1", "Árbol:2",
				"Aslan:1", "tom:2", "tom:1"));

		bucketDisplayContexts.sort(
			BucketDisplayContextComparatorFactoryUtil.
				getBucketDisplayContextComparator("key:desc"));

		_assertOrder(
			bucketDisplayContexts,
			List.of(
				"tom:2", "tom:1", "Aslan:1", "Árbol:2", "Allen:1", "albert:2",
				"11:1", "2:3", "1:1", "01:1"));
	}

	private void _assertOrder(
		List<BucketDisplayContext> bucketDisplayContexts,
		List<String> expectedBucketTextsAndFrequencies) {

		for (int i = 0; i < expectedBucketTextsAndFrequencies.size(); i++) {
			BucketDisplayContext bucketDisplayContext =
				bucketDisplayContexts.get(i);
			String[] parts = StringUtil.split(
				expectedBucketTextsAndFrequencies.get(i), StringPool.COLON);

			Assert.assertEquals(parts[0], bucketDisplayContext.getBucketText());
			Assert.assertEquals(
				parts[1], String.valueOf(bucketDisplayContext.getFrequency()));
		}
	}

	private BucketDisplayContext _createBucketDisplayContext(
		String bucketText, int frequency) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(bucketText);
		bucketDisplayContext.setFrequency(frequency);
		bucketDisplayContext.setLocale(LocaleUtil.getDefault());

		return bucketDisplayContext;
	}

}