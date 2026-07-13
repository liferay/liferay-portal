/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
public class LayoutServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_locale = _portal.getSiteDefaultLocale(_group);
	}

	@Test
	public void testAddPortletsToPrivateLayout() throws Exception {
		_testAddPortletsToLayout(true);
	}

	@Test
	public void testAddPortletsToPublicLayout() throws Exception {
		_testAddPortletsToLayout(false);
	}

	@Test
	public void testAddPrivateLayout() throws Exception {
		_assertAddLayout(true);
	}

	@Test
	public void testAddPublicLayout() throws Exception {
		_assertAddLayout(false);
	}

	@Test(expected = PrincipalException.class)
	public void testCopyLayoutWithoutPermissions() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), ActionKeys.ADD_LAYOUT);

		_removeResourcePermission(layout);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId());

			serviceContext.setRequest(new MockHttpServletRequest());

			_layoutService.copyLayout(
				_group.getGroupId(), layout.isPrivateLayout(),
				RandomTestUtil.randomLocaleStringMap(), false, false, false,
				layout.getPlid(), serviceContext);
		}
	}

	@Test
	public void testFetchFirstLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_assertFetchFirstLayoutAsGuestUser(layout, false);
	}

	@Test
	public void testFetchFirstLayoutFirstLayoutUnpublished() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(
			LayoutTestUtil.addTypeContentLayout(_group),
			LayoutTestUtil.addTypeContentLayout(_group));

		_assertFetchFirstLayoutAsGuestUser(layout, false);
	}

	@Test
	public void testFetchFirstLayoutFirstLayoutWithoutPermission()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout1, LayoutTestUtil.addTypeContentLayout(_group));

		_removeResourcePermission(layout1);

		_assertFetchFirstLayoutAsGuestUser(layout2, false);
	}

	@Test
	public void testFetchFirstLayoutNoLayoutPublished() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		_assertFetchFirstLayoutAsGuestUser(layout, false);
	}

	@Test
	public void testFetchFirstLayoutNoLayoutWithPermission() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout3 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout1, layout2, layout3);

		_removeResourcePermission(layout1, layout2, layout3);

		_assertFetchFirstLayoutAsGuestUser(null, false);
	}

	@Test
	public void testFetchFirstLayoutPublished() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(
			layout, LayoutTestUtil.addTypeContentLayout(_group),
			LayoutTestUtil.addTypeContentLayout(_group));

		_assertFetchFirstLayoutAsGuestUser(layout);
	}

	@Test
	public void testFetchFirstLayoutPublishedFirstLayoutUnpublished()
		throws Exception {

		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout, LayoutTestUtil.addTypeContentLayout(_group));

		_assertFetchFirstLayoutAsGuestUser(layout);
	}

	@Test
	public void testFetchFirstLayoutPublishedFirstLayoutWithoutPermission()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(
			layout1, layout2, LayoutTestUtil.addTypeContentLayout(_group));

		_removeResourcePermission(layout1);

		_assertFetchFirstLayoutAsGuestUser(layout2);
	}

	@Test
	public void testFetchFirstLayoutPublishedNoLayoutPublished()
		throws Exception {

		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		_assertFetchFirstLayoutAsGuestUser(null);
	}

	@Test
	public void testFetchFirstLayoutPublishedNoLayoutWithPermission()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout3 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout1, layout2, layout3);

		_removeResourcePermission(layout1, layout2, layout3);

		_assertFetchFirstLayoutAsGuestUser(null);
	}

	@Test
	public void testFetchLayout() throws Exception {
		Layout newLayout = LayoutTestUtil.addTypePortletLayout(_group);

		Layout layout = _layoutService.fetchLayout(
			0L, newLayout.isPrivateLayout(), newLayout.getLayoutId());

		Assert.assertNull(layout);

		layout = _layoutService.fetchLayout(
			_group.getGroupId(), !newLayout.isPrivateLayout(),
			newLayout.getLayoutId());

		Assert.assertNull(layout);

		layout = _layoutService.fetchLayout(
			_group.getGroupId(), newLayout.isPrivateLayout(), 0L);

		Assert.assertNull(layout);

		layout = _layoutService.fetchLayout(
			_group.getGroupId(), newLayout.isPrivateLayout(),
			newLayout.getLayoutId());

		Assert.assertNotNull(layout);

		Assert.assertEquals(layout.getPlid(), newLayout.getPlid());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testFetchLayoutWithoutPermissions() throws Exception {
		Layout newLayout = LayoutTestUtil.addTypePortletLayout(_group, true);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = UserTestUtil.addUser();

			_roleLocalService.deleteUserRoles(
				user.getUserId(), user.getRoleIds());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			_layoutService.fetchLayout(
				_group.getGroupId(), newLayout.isPrivateLayout(),
				newLayout.getLayoutId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	@TestInfo({"LPD-78893", "LPD-94951"})
	public void testGetTempFileNames() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		// Company group with control panel permission

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUserWithControlPanelPermission(
					PortletKeys.COMPANY_IMPORT))) {

			_layoutService.getTempFileNames(
				companyGroup.getGroupId(), RandomTestUtil.randomString());
		}

		// Company group without permission

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser())) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _layoutService.getTempFileNames(
					companyGroup.getGroupId(), RandomTestUtil.randomString()));
		}

		// Inside publication

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			String folderName = RandomTestUtil.randomString();
			String fileName = RandomTestUtil.randomString();

			_layoutService.addTempFileEntry(
				_group.getGroupId(), folderName, fileName,
				new ByteArrayInputStream(RandomTestUtil.randomBytes()),
				ContentTypes.APPLICATION_ZIP);

			String[] tempFileNames = _layoutService.getTempFileNames(
				_group.getGroupId(), folderName);

			Assert.assertEquals(
				Arrays.toString(tempFileNames), 1, tempFileNames.length);
			Assert.assertEquals(fileName, tempFileNames[0]);
		}
		finally {
			_ctCollectionLocalService.deleteCTCollection(ctCollection);
		}

		// Site group with control panel permission

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUserWithControlPanelPermission(
					PortletKeys.COMPANY_IMPORT))) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _layoutService.getTempFileNames(
					_group.getGroupId(), RandomTestUtil.randomString()));
		}

		// Site group with export/import permission

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_layoutService.getTempFileNames(
				_group.getGroupId(), RandomTestUtil.randomString());
		}
	}

	@Test
	public void testUpdateLayoutTemplate() throws Exception {
		Layout layout = _addTypePortletLayout(
			RandomTestUtil.randomString(), false, StringPool.BLANK);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		for (String layoutTemplateId : _LAYOUT_TEMPLATE_IDS) {
			Assert.assertNotEquals(
				layoutTemplateId,
				typeSettingsUnicodeProperties.getProperty(
					"layout-template-id"));

			typeSettingsUnicodeProperties.setProperty(
				"layout-template-id", layoutTemplateId);

			layout = _layoutService.updateTypeSettings(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), typeSettingsUnicodeProperties.toString());

			typeSettingsUnicodeProperties = layout.getTypeSettingsProperties();

			Assert.assertEquals(
				layoutTemplateId,
				typeSettingsUnicodeProperties.getProperty(
					"layout-template-id"));
		}
	}

	private String[] _addPortletsToLayout(
			int columnIndex, long plid, String[] portletNames)
		throws Exception {

		String[] portletIds = new String[portletNames.length];

		for (int i = 0; i < portletNames.length; i++) {
			portletIds[i] = _addPortletToLayout(
				columnIndex, i, plid, portletNames[i]);
		}

		return portletIds;
	}

	private String _addPortletToLayout(
			int columnIndex, int columnPos, long plid, String portletName)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		String columnId = "column-" + columnIndex;

		String portletId = layoutTypePortlet.addPortletId(
			TestPropsValues.getUserId(), portletName, columnId, columnPos);

		layoutTypePortlet.resetModes();
		layoutTypePortlet.resetStates();

		_layoutService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		return portletId;
	}

	private Layout _addTypePortletLayout(
			String name, boolean privateLayout, String typeSettings)
		throws Exception {

		Map<Locale, String> map = HashMapBuilder.put(
			_locale, name
		).build();

		return _layoutService.addLayout(
			null, _group.getGroupId(), privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0, map, map,
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), LayoutConstants.TYPE_PORTLET, typeSettings,
			false, false,
			HashMapBuilder.put(
				_locale,
				_friendlyURLNormalizer.normalizeWithEncoding(
					StringPool.SLASH + name)
			).build(),
			null,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private User _addUserWithControlPanelPermission(String portletId)
		throws Exception {

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role, portletId, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);

		return user;
	}

	private void _assertAddLayout(boolean privateLayout) throws Exception {
		String name = RandomTestUtil.randomString();

		Layout layout = _addTypePortletLayout(
			name, privateLayout, StringPool.BLANK);

		Assert.assertEquals(name, layout.getName(_locale));
		Assert.assertEquals(privateLayout, layout.isPrivateLayout());
		Assert.assertTrue(layout.isTypePortlet());
	}

	private void _assertFetchFirstLayoutAsGuestUser(Layout layout)
		throws Exception {

		_assertFetchFirstLayoutAsGuestUser(layout, true);
	}

	private void _assertFetchFirstLayoutAsGuestUser(
			Layout layout, boolean published)
		throws Exception {

		User user = _userLocalService.fetchGuestUser(_group.getCompanyId());

		try {
			UserTestUtil.setUser(user);

			Layout curLayout = _layoutService.fetchFirstLayout(
				_group.getGroupId(), false, published);

			if (layout == null) {
				Assert.assertNull(curLayout);
			}
			else {
				Assert.assertEquals(layout.getPlid(), curLayout.getPlid());
			}
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	private void _assertPortletIdsInColumn(
			int columnIndex, long plid, String[] portletIds)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		UnicodeProperties layoutTypeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertArrayEquals(
			portletIds,
			StringUtil.split(
				layoutTypeSettingsUnicodeProperties.getProperty(
					"column-" + columnIndex)));
	}

	private void _publishLayouts(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);
		}
	}

	private void _removeResourcePermission(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			RoleTestUtil.removeResourcePermission(
				RoleConstants.GUEST, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), ActionKeys.VIEW);
		}
	}

	private void _testAddPortletsToLayout(boolean privateLayout)
		throws Exception {

		Layout layout = _addTypePortletLayout(
			RandomTestUtil.randomString(), privateLayout,
			"layout-template-id=2_columns_ii");

		String[] column1PortletIds = _addPortletsToLayout(
			1, layout.getPlid(), _COLUMN_1_PORTLET_NAMES);
		String[] column2PortletIds = _addPortletsToLayout(
			2, layout.getPlid(), _COLUMN_2_PORTLET_NAMES);

		_assertPortletIdsInColumn(1, layout.getPlid(), column1PortletIds);
		_assertPortletIdsInColumn(2, layout.getPlid(), column2PortletIds);
	}

	private static final String[] _COLUMN_1_PORTLET_NAMES = {
		"com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet",
		"com_liferay_blogs_web_portlet_BlogsPortlet",
		"com_liferay_site_navigation_breadcrumb_web_portlet_" +
			"SiteNavigationBreadcrumbPortlet",
		"com_liferay_asset_categories_navigation_web_portlet_" +
			"AssetCategoriesNavigationPortlet",
		"com_liferay_document_library_web_portlet_DLPortlet"
	};

	private static final String[] _COLUMN_2_PORTLET_NAMES = {
		"com_liferay_site_navigation_language_web_portlet_" +
			"SiteNavigationLanguagePortlet",
		"com_liferay_document_library_web_portlet_IGDisplayPortlet",
		"com_liferay_message_boards_web_portlet_MBPortlet",
		"com_liferay_site_my_sites_web_portlet_MySitesPortlet",
		"com_liferay_site_navigation_menu_web_portlet_" +
			"SiteNavigationMenuPortlet",
		"com_liferay_asset_publisher_web_portlet_RelatedAssetsPortlet"
	};

	private static final String[] _LAYOUT_TEMPLATE_IDS = {
		"1-column", "2-columns-i", "2-columns-ii", "2-columns-iii", "3-columns",
		"1-2-columns-i", "1-2-columns-ii", "1-2-1-columns-i",
		"1-2-1-columns-ii", "1-3-1-columns", "1-3-2-columns", "2-1-2-columns",
		"2-2-columns", "3-2-3-columns"
	};

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutService _layoutService;

	private Locale _locale;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}