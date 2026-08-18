/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.portlet.action.test;

import com.liferay.analytics.reports.test.util.MockContextUtil;
import com.liferay.analytics.reports.web.internal.portlet.action.test.util.MockThemeDisplayUtil;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class GetReferralTrafficSourcesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = _layoutLocalService.getLayout(TestPropsValues.getPlid());
	}

	@Test
	public void testGetReferralTrafficSources() throws Exception {
		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), dataSourceId)) {

			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http",
				new MockHttp(
					HashMapBuilder.
						<String, UnsafeSupplier<String, Exception>>put(
							"/api/1.0/data-sources/" + dataSourceId,
							() -> StringPool.BLANK
						).put(
							"/api/1.0/pages/page-referrer-hosts",
							() -> JSONUtil.put(
								"amazon.com", 7.0
							).put(
								"liferay.com", 1.0
							).put(
								"slickdeals.net", 2.0
							).toString()
						).put(
							"/api/1.0/pages/page-referrers",
							() -> JSONUtil.put(
								"https://slickdeals.net/credit-card-offers/",
								2.0
							).put(
								"https://slickdeals.net/most-viewed/", 1.0
							).put(
								"https://slickdeals.net/offers/", 8.0
							).toString()
						).build()));

			MockContextUtil.testWithMockContext(
				MockContextUtil.MockContext.builder(
				).build(),
				() -> {
					MockLiferayResourceRequest mockLiferayResourceRequest =
						_getMockLiferayResourceRequest();

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setRequest(
						mockLiferayResourceRequest.getHttpServletRequest());

					ServiceContextThreadLocal.pushServiceContext(
						serviceContext);

					MockLiferayResourceResponse mockLiferayResourceResponse =
						new MockLiferayResourceResponse();

					_mvcResourceCommand.serveResource(
						mockLiferayResourceRequest,
						mockLiferayResourceResponse);

					ByteArrayOutputStream byteArrayOutputStream =
						(ByteArrayOutputStream)
							mockLiferayResourceResponse.
								getPortletOutputStream();

					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						new String(byteArrayOutputStream.toByteArray()));

					JSONArray referringDomainsJSONArray =
						jsonObject.getJSONArray("referringDomains");

					JSONObject referringDomainsJSONObject1 =
						referringDomainsJSONArray.getJSONObject(0);

					Assert.assertEquals(
						"amazon.com", referringDomainsJSONObject1.get("url"));

					JSONObject referringDomainsJSONObject2 =
						referringDomainsJSONArray.getJSONObject(1);

					Assert.assertEquals(
						"slickdeals.net",
						referringDomainsJSONObject2.get("url"));

					JSONObject referringDomainsJSONObject3 =
						referringDomainsJSONArray.getJSONObject(2);

					Assert.assertEquals(
						"liferay.com", referringDomainsJSONObject3.get("url"));

					JSONArray referringPagesJSONArray = jsonObject.getJSONArray(
						"referringPages");

					JSONObject referringPagesJSONObject1 =
						referringPagesJSONArray.getJSONObject(0);

					Assert.assertEquals(
						"https://slickdeals.net/offers/",
						referringPagesJSONObject1.get("url"));

					JSONObject referringPagesJSONObject2 =
						referringPagesJSONArray.getJSONObject(1);

					Assert.assertEquals(
						"https://slickdeals.net/credit-card-offers/",
						referringPagesJSONObject2.get("url"));

					JSONObject referringPagesJSONObject3 =
						referringPagesJSONArray.getJSONObject(2);

					Assert.assertEquals(
						"https://slickdeals.net/most-viewed/",
						referringPagesJSONObject3.get("url"));
				});
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http", _http);
		}
	}

	@Test
	public void testGetReferralTrafficSourcesWithEmptyReferrers()
		throws Exception {

		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), dataSourceId)) {

			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http",
				new MockHttp(
					HashMapBuilder.
						<String, UnsafeSupplier<String, Exception>>put(
							() -> "/api/1.0/data-sources/" + dataSourceId,
							() -> StringPool.BLANK
						).put(
							"/api/1.0/pages/page-referrer-hosts",
							() -> String.valueOf(
								JSONFactoryUtil.createJSONObject())
						).put(
							"/api/1.0/pages/page-referrers",
							() -> String.valueOf(
								JSONFactoryUtil.createJSONObject())
						).build()));

			MockContextUtil.testWithMockContext(
				MockContextUtil.MockContext.builder(
				).build(),
				() -> {
					MockLiferayResourceRequest mockLiferayResourceRequest =
						_getMockLiferayResourceRequest();

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setRequest(
						mockLiferayResourceRequest.getHttpServletRequest());

					ServiceContextThreadLocal.pushServiceContext(
						serviceContext);

					MockLiferayResourceResponse mockLiferayResourceResponse =
						new MockLiferayResourceResponse();

					_mvcResourceCommand.serveResource(
						mockLiferayResourceRequest,
						mockLiferayResourceResponse);

					ByteArrayOutputStream byteArrayOutputStream =
						(ByteArrayOutputStream)
							mockLiferayResourceResponse.
								getPortletOutputStream();

					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						new String(byteArrayOutputStream.toByteArray()));

					System.out.println("--->> " + jsonObject);

					JSONArray referringDomainsJSONArray =
						jsonObject.getJSONArray("referringDomains");

					Assert.assertEquals(
						referringDomainsJSONArray.toString(), 0,
						referringDomainsJSONArray.length());

					JSONArray referringPagesJSONArray = jsonObject.getJSONArray(
						"referringPages");

					Assert.assertEquals(
						referringPagesJSONArray.toString(), 0,
						referringPagesJSONArray.length());
				});
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http", _http);
		}
	}

	@Test
	public void testGetReferralTrafficSourcesWithoutLiferayAnalyticsDataSourceId()
		throws Exception {

		Dictionary<String, Object> dictionary = new HashMapDictionary();

		dictionary.put("liferayAnalyticsDataSourceId", null);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(), dictionary)) {

			MockContextUtil.testWithMockContext(
				MockContextUtil.MockContext.builder(
				).build(),
				() -> {
					MockLiferayResourceRequest mockLiferayResourceRequest =
						_getMockLiferayResourceRequest();

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setRequest(
						mockLiferayResourceRequest.getHttpServletRequest());

					ServiceContextThreadLocal.pushServiceContext(
						serviceContext);

					MockLiferayResourceResponse mockLiferayResourceResponse =
						new MockLiferayResourceResponse();

					_mvcResourceCommand.serveResource(
						mockLiferayResourceRequest,
						mockLiferayResourceResponse);

					ByteArrayOutputStream byteArrayOutputStream =
						(ByteArrayOutputStream)
							mockLiferayResourceResponse.
								getPortletOutputStream();

					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						new String(byteArrayOutputStream.toByteArray()));

					Assert.assertEquals(
						"An unexpected error occurred.",
						jsonObject.getString("error"));
				});
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http", _http);
		}
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest() {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		try {
			mockLiferayResourceRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				MockThemeDisplayUtil.getThemeDisplay(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()),
					_group, _layout,
					_layoutSetLocalService.getLayoutSet(
						_group.getGroupId(), false),
					LocaleUtil.US));

			return mockLiferayResourceRequest;
		}
		catch (PortalException portalException) {
			throw new AssertionError(portalException);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Http _http;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject(
		filter = "mvc.command.name=/analytics_reports/get_referral_traffic_sources"
	)
	private MVCResourceCommand _mvcResourceCommand;

}