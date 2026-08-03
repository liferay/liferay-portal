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

		String metricType = RandomTestUtil.randomString();

		Metric metric = new Metric();

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

		String metricType = RandomTestUtil.randomString();
		double previousValue = RandomTestUtil.randomDouble();
		String previousValueKey = RandomTestUtil.randomString();

		Metric metric = new Metric();

		metric.setMetricType(metricType);
		metric.setPreviousValue(previousValue);
		metric.setPreviousValueKey(previousValueKey);

		double percentage = RandomTestUtil.randomDouble();
		String trendClassification = RandomTestUtil.randomString();

		Trend trend = new Trend();

		trend.setPercentage(BigDecimal.valueOf(percentage));
		trend.setTrendClassification(trendClassification);

		metric.setTrend(trend);

		double value = RandomTestUtil.randomDouble();
		String valueKey = RandomTestUtil.randomString();

		metric.setValue(value);
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