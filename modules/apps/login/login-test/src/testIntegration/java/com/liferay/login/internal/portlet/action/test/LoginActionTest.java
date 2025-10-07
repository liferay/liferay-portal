/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge García
 */
@FeatureFlag("LPD-6378")
@RunWith(Arquillian.class)
public class LoginActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = CompanyLocalServiceUtil.getCompany(_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testExecuteHasExclusiveState() throws Exception {
		HttpURLConnection httpURLConnection = _getHttpURLConnection();

		Assert.assertEquals(200, httpURLConnection.getResponseCode());

		URL url = httpURLConnection.getURL();

		String query = url.getQuery();

		Assert.assertTrue(query.contains("p_p_state=exclusive"));
	}

	@Test
	public void testExecuteWithContextPath() throws Exception {
		String contextPath = "/" + RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_portal, "_pathContext", contextPath)) {

			HttpURLConnection httpURLConnection = _getHttpURLConnection();

			httpURLConnection.setInstanceFollowRedirects(false);

			Assert.assertEquals(302, httpURLConnection.getResponseCode());

			String location = httpURLConnection.getHeaderField("Location");

			Assert.assertTrue(
				location.contains(
					StringBundler.concat(
						"_com_liferay_login_web_portlet_LoginPortlet_redirect=",
						"http%3A%2F%2F", _company.getVirtualHostname(),
						"%3A8080", HtmlUtil.escapeURL(contextPath),
						"%2Fweb%2Fguest%2Fhome")));
		}
	}

	@Test
	public void testExecuteWithNoDefaultSiteName() throws Exception {
		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.VIRTUAL_HOSTS_DEFAULT_SITE_NAME,
					StringPool.BLANK)) {

			HttpURLConnection httpURLConnection = _getHttpURLConnection();

			Assert.assertEquals(200, httpURLConnection.getResponseCode());

			URL url = httpURLConnection.getURL();

			String query = url.getQuery();

			Assert.assertTrue(
				query.contains(
					StringBundler.concat(
						"_com_liferay_login_web_portlet_LoginPortlet_redirect=",
						"http%3A%2F%2F", _company.getVirtualHostname(),
						"%3A8080%2Fweb%2Fguest%2Fhome")));
		}
	}

	@Test
	public void testExecuteWithPromptEnabled() throws Exception {
		long groupId = _group.getGroupId();

		try (GroupConfigurationTemporarySwapper configurationTemporarySwapper =
				new GroupConfigurationTemporarySwapper(
					groupId,
					"com.liferay.login.web.internal.configuration." +
						"AuthLoginConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"promptEnabled", true
					).build())) {

			UserTestUtil.setUser(TestPropsValues.getUser());

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.welcome");

			siteInitializer.initialize(groupId);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId());

			Layout layout = _layoutLocalService.addLayout(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false,
				StringPool.BLANK, serviceContext);

			Layout draftLayout = layout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			layout = _layoutLocalService.getLayout(layout.getPlid());

			Assert.assertTrue(layout.isPublished());

			Role guestRole = _roleLocalService.getRole(
				layout.getCompanyId(), RoleConstants.GUEST);

			_resourcePermissionLocalService.removeResourcePermission(
				layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), guestRole.getRoleId(),
				ActionKeys.VIEW);

			UserTestUtil.setUser(
				_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

			URL url = new URL(
				StringBundler.concat(
					"http://", _company.getVirtualHostname(), ":8080/web",
					_group.getFriendlyURL(), layout.getFriendlyURL()));

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("GET");

			Assert.assertEquals(200, httpURLConnection.getResponseCode());

			url = httpURLConnection.getURL();

			String query = url.getQuery();

			Assert.assertTrue(query.contains("p_p_state=normal"));
		}
	}

	private HttpURLConnection _getHttpURLConnection() throws Exception {
		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, _serviceContext.getUserId(), group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_LOGIN, 0, _serviceContext);

		UserTestUtil.setUser(
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

		URL url = new URL(
			StringBundler.concat(
				"http://", _company.getVirtualHostname(),
				":8080/c/portal/login?p_l_id=", TestPropsValues.getPlid(),
				"&windowState=exclusive"));

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("GET");

		return httpURLConnection;
	}

	private Company _company;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutUtilityPageEntry _layoutUtilityPageEntry;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private UserLocalService _userLocalService;

}