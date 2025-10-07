/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryTopPages;
import com.liferay.analytics.cms.rest.dto.v1_0.TopPage;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryTopPagesResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class ObjectEntryTopPagesResourceTest
	extends BaseObjectEntryTopPagesResourceTestCase {

	@Override
	@Test
	public void testGetObjectEntryTopPages() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.nextLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							"http://" + RandomTestUtil.randomString()
						).build())) {

			ReflectionTestUtil.setFieldValue(
				_objectEntryTopPagesResource, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/asset-metric/objectEntry/appears-on",
						() -> JSONUtil.put(
							"pages",
							JSONUtil.putAll(
								JSONUtil.put(
									"canonicalUrl", "https://test.com/1"
								).put(
									"defaultMetric",
									JSONUtil.put(
										"metricType", "VIEWS"
									).put(
										"value", 12
									)
								).put(
									"pageTitle", "Title 1"
								).put(
									"siteName", "Site 1"
								),
								JSONUtil.put(
									"canonicalUrl", "https://test.com/2"
								).put(
									"defaultMetric",
									JSONUtil.put(
										"metricType", "VIEWS"
									).put(
										"value", 10
									)
								).put(
									"pageTitle", "Title 2"
								).put(
									"siteName", "Site 1"
								),
								JSONUtil.put(
									"canonicalUrl", "https://test.com/3"
								).put(
									"defaultMetric",
									JSONUtil.put(
										"metricType", "VIEWS"
									).put(
										"value", 9
									)
								).put(
									"pageTitle", "Title 3"
								).put(
									"siteName", "Site 1"
								))
						).put(
							"totalCount", 50
						).toString())));

			ObjectEntryTopPages objectEntryTopPages =
				_objectEntryTopPagesResource.getObjectEntryTopPages(
					"1", null, 30);

			TopPage[] topPages = objectEntryTopPages.getTopPages();

			Assert.assertEquals(Arrays.toString(topPages), 3, topPages.length);

			TopPage topPage1 = topPages[0];

			Assert.assertEquals(
				"https://test.com/1", topPage1.getCanonicalUrl());

			Metric metric1 = topPage1.getDefaultMetric();

			Assert.assertEquals("VIEWS", metric1.getMetricType());
			Assert.assertEquals(12, metric1.getValue(), 0);

			Assert.assertEquals("Title 1", topPage1.getPageTitle());
			Assert.assertEquals("Site 1", topPage1.getSiteName());

			TopPage topPage2 = topPages[1];

			Assert.assertEquals(
				"https://test.com/2", topPage2.getCanonicalUrl());

			Metric metric2 = topPage2.getDefaultMetric();

			Assert.assertEquals("VIEWS", metric2.getMetricType());
			Assert.assertEquals(10, metric2.getValue(), 0);

			Assert.assertEquals("Title 2", topPage2.getPageTitle());
			Assert.assertEquals("Site 1", topPage2.getSiteName());

			TopPage topPage3 = topPages[2];

			Assert.assertEquals(
				"https://test.com/3", topPage3.getCanonicalUrl());

			Metric metric3 = topPage3.getDefaultMetric();

			Assert.assertEquals("VIEWS", metric3.getMetricType());
			Assert.assertEquals(9, metric3.getValue(), 0);

			Assert.assertEquals("Title 3", topPage3.getPageTitle());
			Assert.assertEquals("Site 1", topPage3.getSiteName());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_objectEntryTopPagesResource, "_http", _http);
		}
	}

	@Inject
	private Http _http;

	@Inject
	private ObjectEntryTopPagesResource _objectEntryTopPagesResource;

}