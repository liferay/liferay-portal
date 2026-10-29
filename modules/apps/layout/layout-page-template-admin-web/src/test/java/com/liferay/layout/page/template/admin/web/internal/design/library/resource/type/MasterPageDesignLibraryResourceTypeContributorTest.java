/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class MasterPageDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_masterPageDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);

		Mockito.when(
			_depotEntry.getGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			_depotEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);
	}

	@After
	public void tearDown() {
		_featureFlagManagerUtilMockedStatic.close();
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-108812")
	public void testGetCreationItems() throws Exception {
		_setUpAddLiferayPortletURL();
		_setUpPortletNamespace();

		List<DesignLibraryResourceCreationItem>
			designLibraryResourceCreationItems =
				_masterPageDesignLibraryResourceTypeContributor.
					getCreationItems(
						_httpServletRequest, _depotEntry, _BACK_URL);

		Assert.assertEquals(
			designLibraryResourceCreationItems.toString(), 1,
			designLibraryResourceCreationItems.size());

		DesignLibraryResourceCreationItem designLibraryResourceCreationItem =
			designLibraryResourceCreationItems.get(0);

		Assert.assertEquals(
			"add-master-page", designLibraryResourceCreationItem.getId());
		Assert.assertEquals(
			"new-master-page", designLibraryResourceCreationItem.getLabel());
		Assert.assertEquals(
			"{AddMasterPageDesignLibraryModalContent} from " +
				"layout-page-template-admin-web",
			designLibraryResourceCreationItem.getModule());

		Map<String, Object> moduleProps =
			designLibraryResourceCreationItem.getModuleProps();

		Assert.assertEquals(
			String.valueOf(_addLiferayPortletURL),
			moduleProps.get("addMasterLayoutURL"));
		Assert.assertEquals(_NAMESPACE, moduleProps.get("namespace"));

		Assert.assertEquals(
			"/layout_page_template_admin/add_master_layout",
			_addLiferayPortletURL.getParameter(ActionRequest.ACTION_NAME));
		Assert.assertEquals(
			_BACK_URL, _addLiferayPortletURL.getParameter("redirect"));
	}

	@Test
	@TestInfo("LPD-108812")
	public void testGetFDSActionDropdownItems() throws Exception {
		_setUpRenderLiferayPortletURLs();

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_masterPageDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					_httpServletRequest, _depotEntry, _BACK_URL);

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0),
			String.valueOf(_editLiferayPortletURL), "pencil", "edit", "edit",
			null, "get", "link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "{actions.markAsDefault.href}",
			"star", "markAsDefault", "mark-as-default", "post", "markAsDefault",
			"async");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "{actions.unmarkAsDefault.href}",
			"star-o", "unmarkAsDefault", "unmark-as-default", "post",
			"unmarkAsDefault", "async");
		_assertCopyMenuFDSActionDropdownItem(fdsActionDropdownItems.get(3));
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(4),
			String.valueOf(_permissionsLiferayPortletURL), "password-policies",
			"permissions", "permissions", null, "permissions",
			"modal-permissions");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(5), "{actions.delete.href}", "trash",
			"delete", "delete", "delete", "delete", "async");
		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 6,
			fdsActionDropdownItems.size());

		_assertLiferayPortletURL(
			_editLiferayPortletURL,
			"/layout_page_template_admin/edit_layout_page_template_entry");
		Assert.assertEquals(
			_BACK_URL, _editLiferayPortletURL.getParameter("redirect"));

		_assertLiferayPortletURL(
			_permissionsLiferayPortletURL,
			"/layout_page_template_admin" +
				"/view_layout_page_template_entry_permissions");
	}

	@Test
	@TestInfo("LPD-108812")
	public void testGetType() {
		Assert.assertEquals(
			String.valueOf(LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT),
			_masterPageDesignLibraryResourceTypeContributor.getType());
	}

	@Test
	@TestInfo("LPD-108812")
	public void testHasAddPermission() {
		Assert.assertFalse(
			_masterPageDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));

		_setUpPortletResourcePermission(
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		Assert.assertTrue(
			_masterPageDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));
	}

	@Test
	@TestInfo("LPD-108812")
	public void testHasViewPermission() {
		_setUpFeatureFlagManagerUtil();

		Assert.assertFalse(
			_masterPageDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));

		_setUpPortletResourcePermission(ActionKeys.VIEW);

		Assert.assertTrue(
			_masterPageDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));
	}

	private void _assertCopyMenuFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem) {

		_assertFDSActionDropdownItem(
			fdsActionDropdownItem, null, "copy", "copyMenu", "make-a-copy",
			null, null, null);
		Assert.assertEquals("contextual", fdsActionDropdownItem.get("type"));

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			(List<FDSActionDropdownItem>)fdsActionDropdownItem.get("items");

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "{actions.copy.href}", null, "copy",
			"master-page", "post", "copy", "async");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "{actions.copyWithPermission.href}",
			null, "copyWithPermission", "master-page-with-permissions", "post",
			"copyWithPermission", "async");
		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 2,
			fdsActionDropdownItems.size());
	}

	private void _assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String href, String icon,
		String id, String label, String method, String permissionKey,
		String target) {

		Assert.assertEquals(href, fdsActionDropdownItem.get("href"));
		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(target, fdsActionDropdownItem.get("target"));

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));
		Assert.assertEquals(permissionKey, data.get("permissionKey"));
	}

	private void _assertLiferayPortletURL(
		LiferayPortletURL liferayPortletURL, String mvcRenderCommandName) {

		Assert.assertEquals(
			"{embedded.externalReferenceCode}",
			liferayPortletURL.getParameter(
				"layoutPageTemplateEntryExternalReferenceCode"));
		Assert.assertEquals(
			mvcRenderCommandName,
			liferayPortletURL.getParameter("mvcRenderCommandName"));
	}

	private void _setUpAddLiferayPortletURL() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_httpServletRequest), Mockito.eq(_group),
				Mockito.eq(
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES),
				Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(PortletRequest.ACTION_PHASE))
		).thenReturn(
			_addLiferayPortletURL
		);
	}

	private void _setUpFeatureFlagManagerUtil() {
		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-81022"))
		).thenReturn(
			true
		);
	}

	private void _setUpPortletNamespace() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortletNamespace(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES)
		).thenReturn(
			_NAMESPACE
		);
	}

	private void _setUpPortletResourcePermission(String actionId) {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID, actionId)
		).thenReturn(
			true
		);
	}

	private void _setUpRenderLiferayPortletURLs() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_httpServletRequest), Mockito.eq(_group),
				Mockito.eq(
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES),
				Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(PortletRequest.RENDER_PHASE))
		).thenReturn(
			_editLiferayPortletURL, _permissionsLiferayPortletURL
		);
	}

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _NAMESPACE = RandomTestUtil.randomString();

	private final LiferayPortletURL _addLiferayPortletURL =
		new MockLiferayPortletURL();
	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final LiferayPortletURL _editLiferayPortletURL =
		new MockLiferayPortletURL();
	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final MasterPageDesignLibraryResourceTypeContributor
		_masterPageDesignLibraryResourceTypeContributor =
			new MasterPageDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final LiferayPortletURL _permissionsLiferayPortletURL =
		new MockLiferayPortletURL();
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}