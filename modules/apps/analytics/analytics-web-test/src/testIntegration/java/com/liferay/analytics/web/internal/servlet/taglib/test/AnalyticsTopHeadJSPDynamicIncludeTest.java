/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.web.internal.servlet.taglib.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class AnalyticsTopHeadJSPDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInclude() throws Exception {
		_assertInclude(false);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsEndpointURL",
							"http://" + RandomTestUtil.randomString()
						).build())) {

			_assertInclude(true);
		}
	}

	private void _assertInclude(boolean analyticsTrackingEnabled)
		throws Exception {

		String content = _http.URLtoString(
			"http://localhost:" + _portal.getPortalServerPort(false) +
				"/web/guest");

		Assert.assertEquals(
			analyticsTrackingEnabled,
			content.contains("id=\"liferayAnalyticsScript\""));

		for (String eventId :
				new String[] {
					"'VOTE'", "'documentDownloaded'", "'fieldBlurred'",
					"'posted'", "'shared'"
				}) {

			Assert.assertEquals(
				eventId, analyticsTrackingEnabled, content.contains(eventId));
		}
	}

	@Inject
	private Http _http;

	@Inject
	private Portal _portal;

}