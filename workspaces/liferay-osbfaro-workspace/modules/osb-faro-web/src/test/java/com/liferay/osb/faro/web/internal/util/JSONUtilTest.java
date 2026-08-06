/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.engine.client.model.Metric;
import com.liferay.osb.faro.engine.client.model.Trend;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Leslie Wong
 */
public class JSONUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testWriteValueAsStringExcludesNullMetricFields()
		throws Exception {

		Metric metric = new Metric();

		String metricType = RandomTestUtil.randomString();

		metric.setMetricType(metricType);

		JSONAssert.assertEquals(
			com.liferay.portal.kernel.json.JSONUtil.put(
				"metricType", metricType
			).put(
				"value", 0D
			).toString(),
			JSONUtil.writeValueAsString(metric), JSONCompareMode.STRICT);
	}

	@Test
	public void testWriteValueAsStringIncludesNonnullMetricFields()
		throws Exception {

		Metric metric = new Metric();

		String metricType = RandomTestUtil.randomString();

		metric.setMetricType(metricType);

		double previousValue = RandomTestUtil.randomDouble();

		metric.setPreviousValue(previousValue);

		String previousValueKey = RandomTestUtil.randomString();

		metric.setPreviousValueKey(previousValueKey);

		Trend trend = new Trend();

		double percentage = RandomTestUtil.randomDouble();

		trend.setPercentage(BigDecimal.valueOf(percentage));

		String trendClassification = RandomTestUtil.randomString();

		trend.setTrendClassification(trendClassification);

		metric.setTrend(trend);

		double value = RandomTestUtil.randomDouble();

		metric.setValue(value);

		String valueKey = RandomTestUtil.randomString();

		metric.setValueKey(valueKey);

		JSONAssert.assertEquals(
			com.liferay.portal.kernel.json.JSONUtil.put(
				"metricType", metricType
			).put(
				"previousValue", previousValue
			).put(
				"previousValueKey", previousValueKey
			).put(
				"trend",
				com.liferay.portal.kernel.json.JSONUtil.put(
					"percentage", percentage
				).put(
					"trendClassification", trendClassification
				)
			).put(
				"value", value
			).put(
				"valueKey", valueKey
			).toString(),
			JSONUtil.writeValueAsString(metric), JSONCompareMode.STRICT);
	}

}