/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.context.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.Portlet;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();

		Bundle bundle = FrameworkUtil.getBundle(
			SegmentsDisplayContextTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(
			bundleContext,
			bundleContext.createFilter(
				"(component.name=com.liferay.segments.web.internal.portlet." +
					"SegmentsPortlet)"),
			null);

		_serviceTracker.open();

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		_serviceTracker.close();
	}

	@Test
	public void testGetAssignUserRolesDataMap() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Map<String, Object> assignUserRolesDataMap = _getAssignUserRolesDataMap(
			segmentsEntry);

		Assert.assertEquals(
			assignUserRolesDataMap.get("segmentsEntryId"),
			segmentsEntry.getSegmentsEntryId());

		String itemSelectorURL = String.valueOf(
			assignUserRolesDataMap.get("itemSelectorURL"));

		itemSelectorURL = HttpComponentsUtil.decodeURL(itemSelectorURL);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			_getExcludedRoleNames());

		Assert.assertTrue(itemSelectorURL.contains(jsonArray.toString()));

		Assert.assertTrue(
			itemSelectorURL.contains("\"type\":" + RoleConstants.TYPE_SITE));
	}

	@Test
	public void testGetAssignUserRolesLinkCssDisabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", false
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", false
							).build())) {

				SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _user.getUserId()));

				Assert.assertFalse(
					_isAssignUserRolesButtonEnabled(
						segmentsEntry.getCompanyId()));
			}
		}
	}

	@Test
	public void testGetAssignUserRolesLinkCssEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", true
							).build())) {

				SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _user.getUserId()));

				Assert.assertTrue(
					_isAssignUserRolesButtonEnabled(
						segmentsEntry.getCompanyId()));
			}
		}
	}

	@Test
	public void testGetAvailableActions() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertEquals(
			"deleteSegmentsEntries", _getAvailableActions(segmentsEntry));
	}

	@Test
	public void testGetAvailableActionsWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		Assert.assertEquals(
			StringPool.BLANK, _getAvailableActions(segmentsEntry));
	}

	@Test
	public void testGetDeleteURL() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		String deleteURL = _getDeleteURL(segmentsEntry);

		Assert.assertTrue(
			deleteURL.contains(
				"param_jakarta.portlet.action=/segments" +
					"/delete_segments_entry"));
		Assert.assertTrue(
			deleteURL.contains(
				"param_segmentsEntryId=" + segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetEditURL() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		String editURL = _getEditURL(segmentsEntry);

		Assert.assertTrue(
			editURL.contains(
				"param_mvcRenderCommandName=/segments/edit_segments_entry"));
		Assert.assertTrue(
			editURL.contains(
				"param_segmentsEntryId=" + segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetPermissionURL() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		String permissionURL = _getPermissionURL(segmentsEntry);

		String mvcPath = URLEncoder.encode(
			"/edit_permissions.jsp", StandardCharsets.UTF_8.name());

		Assert.assertTrue(permissionURL.contains("mvcPath=" + mvcPath));

		Assert.assertTrue(
			permissionURL.contains(
				"modelResource=" + SegmentsEntry.class.getName()));
		Assert.assertTrue(
			permissionURL.contains(
				"resourcePrimKey=" + segmentsEntry.getSegmentsEntryId()));
		Assert.assertTrue(permissionURL.contains("p_p_state=pop_up"));
	}

	@Test
	public void testGetPreviewMembersURL() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		String previewMembersURL = _getPreviewMembersURL(segmentsEntry);

		Assert.assertTrue(
			previewMembersURL.contains(
				"param_mvcRenderCommandName=/segments" +
					"/preview_segments_entry_users"));
		Assert.assertTrue(
			previewMembersURL.contains("clearSessionCriteria=true"));
		Assert.assertTrue(
			previewMembersURL.contains(
				"param_segmentsEntryId=" + segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetScopeName() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertEquals("Current Site", _getScopeName(segmentsEntry));
	}

	@Test
	public void testGetScopeNameWithGlobalSite() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), _user.getUserId()));

		Assert.assertEquals("Global", _getScopeName(segmentsEntry));
	}

	@Test
	public void testGetSegmentsEntryURL() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), _user.getUserId()));

		String segmentsEntryURL = _getSegmentsEntryURL(segmentsEntry);

		Assert.assertTrue(
			segmentsEntryURL.contains(
				"segmentsEntryId=" + segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetSegmentsEntryURLTarget() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), _user.getUserId()));

		Assert.assertEquals("_self", _getSegmentsEntryURLTarget(segmentsEntry));
	}

	@Test
	public void testGetSegmentsEntryURLTargetWithAsahFaroBackendSourceAndNullCriteria()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertEquals(
			"_blank", _getSegmentsEntryURLTarget(segmentsEntry));
	}

	@Test
	public void testGetSegmentsEntryURLWithAsahFaroBackendSourceAndNotNullCriteria()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsURL", RandomTestUtil.randomString()
						).build())) {

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				CriteriaSerializer.serialize(new Criteria()),
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));

			String segmentsEntryURL = _getSegmentsEntryURL(segmentsEntry);

			Assert.assertTrue(
				segmentsEntryURL.contains(
					"segmentsEntryId=" + segmentsEntry.getSegmentsEntryId()));
		}
	}

	@Test
	public void testGetSegmentsEntryURLWithAsahFaroBackendSourceAndNullCriteria()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsURL", RandomTestUtil.randomString()
						).build())) {

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));

			String segmentsEntryURL = _getSegmentsEntryURL(segmentsEntry);

			Assert.assertTrue(
				segmentsEntryURL.endsWith(
					"/contacts/segments/" +
						segmentsEntry.getSegmentsEntryKey()));
		}
	}

	@Test
	public void testIsRoleSegmentationDisabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", false
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", false
							).build())) {

				Assert.assertFalse(
					_isRoleSegmentationEnabled(TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsRoleSegmentationEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", true
							).build())) {

				Assert.assertTrue(
					_isRoleSegmentationEnabled(TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsSegmentationDisabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", false
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", false
							).build())) {

				Assert.assertFalse(
					_isSegmentationEnabled(TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsSegmentationEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				Assert.assertTrue(
					_isSegmentationEnabled(TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsShowAssignUserRolesActionWithoutPermissions()
		throws Exception {

		User user = UserTestUtil.addUser();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		Assert.assertFalse(_isShowAssignUserRolesAction(segmentsEntry));
	}

	@Test
	public void testIsShowAssignUserRolesActionWithPermissions()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertTrue(_isShowAssignUserRolesAction(segmentsEntry));
	}

	@Test
	public void testIsShowDeleteActionDifferentSites() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), _user.getUserId()));

		Assert.assertFalse(_isShowDeleteAction(segmentsEntry));
	}

	@Test
	public void testIsShowDeleteActionSameSite() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertTrue(_isShowDeleteAction(segmentsEntry));
	}

	@Test
	public void testIsShowDeleteActionSameSiteDifferentUsers()
		throws Exception {

		User user = UserTestUtil.addUser();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		Assert.assertFalse(_isShowDeleteAction(segmentsEntry));
	}

	@Test
	public void testIsShowPermissionAction() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertTrue(_isShowPermissionAction(segmentsEntry));
	}

	@Test
	public void testIsShowPermissionActionDifferentUsers() throws Exception {
		User user = UserTestUtil.addUser();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		Assert.assertFalse(_isShowPermissionAction(segmentsEntry));
	}

	@Test
	public void testIsShowUpdateAction() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertTrue(_isShowUpdateAction(segmentsEntry));
	}

	@Test
	public void testIsShowUpdateActionDifferentUsers() throws Exception {
		User user = UserTestUtil.addUser();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		Assert.assertFalse(_isShowUpdateAction(segmentsEntry));
	}

	@Test
	public void testIsShowViewAction() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		Assert.assertTrue(_isShowViewAction(segmentsEntry));
	}

	private Map<String, Object> _getAssignUserRolesDataMap(
			SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getAssignUserRolesDataMap", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getAvailableActions(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getAvailableActions", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getDeleteURL(SegmentsEntry segmentsEntry) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getDeleteURL", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getEditURL(SegmentsEntry segmentsEntry) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getEditURL", new Class<?>[] {SegmentsEntry.class}, segmentsEntry);
	}

	private String[] _getExcludedRoleNames() {
		RoleTypeContributor roleTypeContributor =
			_roleTypeContributorProvider.getRoleTypeContributor(
				RoleConstants.TYPE_SITE);

		if (roleTypeContributor != null) {
			return roleTypeContributor.getExcludedRoleNames();
		}

		return new String[0];
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		com.liferay.portal.kernel.model.Portlet portlet =
			_portletLocalService.getPortletById(SegmentsPortletKeys.SEGMENTS);

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(portlet, null));

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());
		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletRenderRequest.setAttribute(
			"view.jsp-eventName", "assignSiteRoles");
		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		return mockLiferayPortletRenderRequest;
	}

	private String _getPermissionURL(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getPermissionURL", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getPreviewMembersURL(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getPreviewMembersURL", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getScopeName(SegmentsEntry segmentsEntry) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getScopeName", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getSegmentsEntryURL(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getSegmentsEntryURL", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private String _getSegmentsEntryURLTarget(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"getSegmentsEntryURLTarget", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	private boolean _isAssignUserRolesButtonEnabled(long companyId)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isRoleSegmentationEnabled", new Class<?>[] {long.class},
			companyId);
	}

	private boolean _isRoleSegmentationEnabled(long companyId)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isRoleSegmentationEnabled", new Class<?>[] {long.class},
			companyId);
	}

	private boolean _isSegmentationEnabled(long companyId) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isSegmentationEnabled", new Class<?>[] {long.class}, companyId);
	}

	private boolean _isShowAssignUserRolesAction(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isShowAssignUserRolesAction", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private boolean _isShowDeleteAction(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isShowDeleteAction", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private boolean _isShowPermissionAction(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isShowPermissionAction", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private boolean _isShowUpdateAction(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isShowUpdateAction", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private boolean _isShowViewAction(SegmentsEntry segmentsEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				_SEGMENTS_DISPLAY_CONTEXT),
			"isShowViewAction", new Class<?>[] {SegmentsEntry.class},
			segmentsEntry);
	}

	private MockLiferayPortletRenderRequest _renderPortlet() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		Portlet portlet = _serviceTracker.getService();

		portlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest;
	}

	private static final String _SEGMENTS_DISPLAY_CONTEXT =
		"com.liferay.segments.web.internal.display.context." +
			"SegmentsDisplayContext";

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	private ServiceTracker<Portlet, Portlet> _serviceTracker;
	private User _user;

}