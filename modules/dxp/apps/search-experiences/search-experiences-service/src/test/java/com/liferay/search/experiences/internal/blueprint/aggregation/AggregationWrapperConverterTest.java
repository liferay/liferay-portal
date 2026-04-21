/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.aggregation;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.aggregation.bucket.ReverseNestedAggregation;
import com.liferay.portal.search.internal.aggregation.AggregationsImpl;
import com.liferay.portal.search.significance.SignificanceHeuristics;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.internal.blueprint.highlight.HighlightConverter;
import com.liferay.search.experiences.internal.blueprint.query.QueryConverter;
import com.liferay.search.experiences.internal.blueprint.script.ScriptConverter;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jason Myatt
 */
public class AggregationWrapperConverterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testReverseNestedAggregationWithoutPath() {
		ReverseNestedAggregation reverseNestedAggregation =
			_getReverseNestedAggregation(JSONFactoryUtil.createJSONObject());

		Assert.assertNull(reverseNestedAggregation.getPath());
	}

	@Test
	public void testReverseNestedAggregationWithPath() {
		String path = RandomTestUtil.randomString();

		ReverseNestedAggregation reverseNestedAggregation =
			_getReverseNestedAggregation(JSONUtil.put("path", path));

		Assert.assertEquals(path, reverseNestedAggregation.getPath());
	}

	private ReverseNestedAggregation _getReverseNestedAggregation(
		JSONObject jsonObject) {

		AggregationWrapper aggregationWrapper =
			_aggregationWrapperConverter.toAggregationWrapper(
				jsonObject, RandomTestUtil.randomString(), "reverse_nested");

		return (ReverseNestedAggregation)aggregationWrapper.getAggregation();
	}

	private final AggregationWrapperConverter _aggregationWrapperConverter =
		new AggregationWrapperConverter(
			new AggregationsImpl(), Mockito.mock(HighlightConverter.class),
			Mockito.mock(QueryConverter.class),
			Mockito.mock(ScriptConverter.class),
			Mockito.mock(SignificanceHeuristics.class),
			Mockito.mock(Sorts.class));

}