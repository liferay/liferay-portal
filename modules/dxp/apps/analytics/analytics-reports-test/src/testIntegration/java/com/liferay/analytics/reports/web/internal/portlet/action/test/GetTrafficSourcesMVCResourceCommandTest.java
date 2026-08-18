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
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;

import java.util.Dictionary;
import java.util.ResourceBundle;

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
public class GetTrafficSourcesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testGetTrafficSources() throws Exception {
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
							"/api/1.0/pages/acquisition-channels",
							() -> JSONUtil.put(
								"organic", 3192L
							).put(
								"paid", 1L
							).put(
								"referral", 2L
							).put(
								"social", 385L
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

					JSONObject jsonObject1 = JSONFactoryUtil.createJSONObject(
						new String(byteArrayOutputStream.toByteArray()));

					JSONArray jsonArray = jsonObject1.getJSONArray(
						"trafficSources");

					Assert.assertEquals(5, jsonArray.length());

					JSONObject jsonObject2 = jsonArray.getJSONObject(0);

					Assert.assertEquals("organic", jsonObject2.get("name"));
					Assert.assertEquals(
						String.format("%.1f", 89.20D),
						jsonObject2.getString("share"));

					Assert.assertEquals(3192, jsonObject2.get("value"));

					JSONObject jsonObject3 = jsonArray.getJSONObject(1);

					Assert.assertEquals(
						"http//localhost/test?",
						jsonObject3.getString("endpointURL"));
					Assert.assertEquals("social", jsonObject3.get("name"));
					Assert.assertEquals(385, jsonObject3.getInt("value"));

					JSONObject jsonObject4 = jsonArray.getJSONObject(2);

					Assert.assertEquals(
						"http//localhost/test?",
						jsonObject4.getString("endpointURL"));
					Assert.assertEquals("referral", jsonObject4.get("name"));
					Assert.assertEquals(2L, jsonObject4.getInt("value"));

					JSONObject jsonObject5 = jsonArray.getJSONObject(3);

					Assert.assertEquals("paid", jsonObject5.get("name"));
					Assert.assertEquals(1L, jsonObject5.getInt("value"));

					JSONObject jsonObject6 = jsonArray.getJSONObject(4);

					Assert.assertEquals("direct", jsonObject6.get("name"));
					Assert.assertEquals(0, jsonObject6.getInt("value"));
					Assert.assertNull(jsonObject6.get("endpointURL"));
				});
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_http", _http);
		}
	}

	@Test
	public void testGetTrafficSourcesWithoutLiferayAnalyticsDataSourceId()
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

					JSONArray jsonArray = jsonObject.getJSONArray(
						"trafficSources");

					ResourceBundle resourceBundle =
						ResourceBundleUtil.getBundle(
							LocaleUtil.getDefault(),
							_mvcResourceCommand.getClass());

					Assert.assertEquals(
						JSONUtil.putAll(
							JSONUtil.put(
								"helpMessage",
								ResourceBundleUtil.getString(
									resourceBundle,
									"this-is-the-number-of-page-views-" +
										"generated-by-people-arriving-" +
											"directly-to-your-page")
							).put(
								"name", "direct"
							).put(
								"title",
								ResourceBundleUtil.getString(
									resourceBundle, "direct")
							),
							JSONUtil.put(
								"helpMessage",
								ResourceBundleUtil.getString(
									resourceBundle,
									"this-is-the-number-of-page-views-" +
										"generated-by-people-coming-from-a-" +
											"search-engine")
							).put(
								"name", "organic"
							).put(
								"title",
								ResourceBundleUtil.getString(
									resourceBundle, "organic")
							),
							JSONUtil.put(
								"helpMessage",
								ResourceBundleUtil.getString(
									resourceBundle,
									"this-is-the-number-of-page-views-" +
										"generated-by-people-that-find-your-" +
											"page-through-google-adwords")
							).put(
								"name", "paid"
							).put(
								"title",
								ResourceBundleUtil.getString(
									resourceBundle, "paid")
							),
							JSONUtil.put(
								"endpointURL", "http//localhost/test?"
							).put(
								"helpMessage",
								ResourceBundleUtil.getString(
									resourceBundle,
									StringBundler.concat(
										"this-is-the-number-of-page-views-",
										"generated-by-people-coming-to-your-",
										"page-from-other-sites-which-are-not-",
										"search-engine-pages-or-social-sites"))
							).put(
								"name", "referral"
							).put(
								"title",
								ResourceBundleUtil.getString(
									resourceBundle, "referral")
							),
							JSONUtil.put(
								"endpointURL", "http//localhost/test?"
							).put(
								"helpMessage",
								ResourceBundleUtil.getString(
									resourceBundle,
									"this-is-the-number-of-page-views-" +
										"generated-by-people-coming-to-your-" +
											"page-from-social-sites")
							).put(
								"name", "social"
							).put(
								"title",
								ResourceBundleUtil.getString(
									resourceBundle, "social")
							)
						).toString(),
						jsonArray.toString());
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
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject(filter = "mvc.command.name=/analytics_reports/get_traffic_sources")
	private MVCResourceCommand _mvcResourceCommand;

}