/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class CMSSearchUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-105769")
	public void testAddBucketKeys() {
		List<String> keys = Arrays.asList(
			"1", "10", "11", "12", "2", "20", "21", "3", "30");

		List<IncludeExcludeClause> includeExcludeClauses = new ArrayList<>();

		Set<String> bucketKeys = new HashSet<>();

		CMSSearchUtil.addBucketKeys(
			includeExcludeClause -> {
				includeExcludeClauses.add(includeExcludeClause);

				return _getTermsAggregationResult(
					includeExcludeClause, keys, 3);
			},
			bucketKeys, StringPool.BLANK);

		Assert.assertEquals(new TreeSet<>(keys), new TreeSet<>(bucketKeys));

		Assert.assertEquals(
			includeExcludeClauses.toString(), 8, includeExcludeClauses.size());
	}

	private TermsAggregationResult _getTermsAggregationResult(
		IncludeExcludeClause includeExcludeClause, List<String> keys,
		int size) {

		List<Bucket> buckets = new ArrayList<>();

		for (String key : keys) {
			if (!key.matches(includeExcludeClause.getIncludeRegex()) ||
				((includeExcludeClause.getExcludeRegex() != null) &&
				 key.matches(includeExcludeClause.getExcludeRegex()))) {

				continue;
			}

			buckets.add(new Bucket(key, RandomTestUtil.randomLong()));
		}

		TermsAggregationResult termsAggregationResult = Mockito.mock(
			TermsAggregationResult.class);

		Mockito.when(
			termsAggregationResult.getBuckets()
		).thenReturn(
			buckets.subList(0, Math.min(buckets.size(), size))
		);

		Mockito.when(
			termsAggregationResult.getOtherDocCounts()
		).thenReturn(
			(long)Math.max(buckets.size() - size, 0)
		);

		return termsAggregationResult;
	}

}