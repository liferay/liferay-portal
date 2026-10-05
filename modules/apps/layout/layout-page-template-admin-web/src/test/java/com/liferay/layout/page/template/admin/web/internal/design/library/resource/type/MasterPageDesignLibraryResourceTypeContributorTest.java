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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;

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

		Mockito.when(
			_liferayPortletURL.toString()
		).thenReturn(
			_URL
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_httpServletRequest), Mockito.eq(_group),
				Mockito.eq(
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_liferayPortletURL
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortletNamespace(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES)
		).thenReturn(
			_NAMESPACE
		);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-81022")
	public void testGetColor() {
		Assert.assertEquals(
			"blue", _masterPageDesignLibraryResourceTypeContributor.getColor());
	}

	@Test
	@TestInfo("LPD-81022")
	public void testGetCreationItems() throws Exception {
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

		Assert.assertEquals(_URL, moduleProps.get("addMasterLayoutURL"));
		Assert.assertEquals(_NAMESPACE, moduleProps.get("namespace"));

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			ActionRequest.ACTION_NAME,
			"/layout_page_template_admin/add_master_layout"
		);

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"redirect", _BACK_URL
		);
	}

	@Test
	@TestInfo("LPD-81022")
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_masterPageDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					_httpServletRequest, _depotEntry, _BACK_URL);

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "pencil", "edit", "edit", null,
			"get", "link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "star", "markAsDefault",
			"mark-as-default", "post", "markAsDefault", "async");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "star-o", "unmarkAsDefault",
			"unmark-as-default", "post", "unmarkAsDefault", "async");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "copy", "copyMenu", "make-a-copy",
			null, null, null);
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(4), "password-policies", "permissions",
			"permissions", null, "permissions", "modal-permissions");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(5), "trash", "delete", "delete",
			"delete", "delete", "async");
		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 6,
			fdsActionDropdownItems.size());

		FDSActionDropdownItem copyFDSActionDropdownItem =
			fdsActionDropdownItems.get(3);

		Assert.assertEquals(
			"contextual", copyFDSActionDropdownItem.get("type"));

		List<FDSActionDropdownItem> copyFDSActionDropdownItems =
			(List<FDSActionDropdownItem>)copyFDSActionDropdownItem.get("items");

		_assertFDSActionDropdownItem(
			copyFDSActionDropdownItems.get(0), null, "copy", "master-page",
			"post", "copy", "async");
		_assertFDSActionDropdownItem(
			copyFDSActionDropdownItems.get(1), null, "copyWithPermission",
			"master-page-with-permissions", "post", "copyWithPermission",
			"async");
		Assert.assertEquals(
			copyFDSActionDropdownItems.toString(), 2,
			copyFDSActionDropdownItems.size());

		FDSActionDropdownItem masterPageFDSActionDropdownItem =
			copyFDSActionDropdownItems.get(0);

		Assert.assertEquals(
			"{actions.copy.href}", masterPageFDSActionDropdownItem.get("href"));

		FDSActionDropdownItem masterPageWithPermissionsFDSActionDropdownItem =
			copyFDSActionDropdownItems.get(1);

		Assert.assertEquals(
			"{actions.copyWithPermission.href}",
			masterPageWithPermissionsFDSActionDropdownItem.get("href"));

		FDSActionDropdownItem markAsDefaultFDSActionDropdownItem =
			fdsActionDropdownItems.get(1);

		Assert.assertEquals(
			"{actions.markAsDefault.href}",
			markAsDefaultFDSActionDropdownItem.get("href"));

		FDSActionDropdownItem unmarkAsDefaultFDSActionDropdownItem =
			fdsActionDropdownItems.get(2);

		Assert.assertEquals(
			"{actions.unmarkAsDefault.href}",
			unmarkAsDefaultFDSActionDropdownItem.get("href"));

		FDSActionDropdownItem deleteFDSActionDropdownItem =
			fdsActionDropdownItems.get(5);

		Assert.assertEquals(
			"{actions.delete.href}", deleteFDSActionDropdownItem.get("href"));

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin/edit_master_layout"
		);

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin/view_master_layout_permissions"
		);

		Mockito.verify(
			_liferayPortletURL, Mockito.times(2)
		).setParameter(
			"masterPageExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);
	}

	@Test
	@TestInfo("LPD-81022")
	public void testGetType() {
		Assert.assertEquals(
			String.valueOf(LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT),
			_masterPageDesignLibraryResourceTypeContributor.getType());
	}

	@Test
	@TestInfo("LPD-81022")
	public void testHasAddPermission() {
		Assert.assertFalse(
			_masterPageDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));

		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_masterPageDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));
	}

	@Test
	@TestInfo("LPD-81022")
	public void testHasViewPermission() {
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-81022"))
			).thenReturn(
				true
			);

			Assert.assertFalse(
				_masterPageDesignLibraryResourceTypeContributor.
					hasViewPermission(_permissionChecker, _depotEntry));

			Mockito.when(
				_portletResourcePermission.contains(
					_permissionChecker, _GROUP_ID, ActionKeys.VIEW)
			).thenReturn(
				true
			);

			Assert.assertTrue(
				_masterPageDesignLibraryResourceTypeContributor.
					hasViewPermission(_permissionChecker, _depotEntry));

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-81022"))
			).thenReturn(
				false
			);

			Assert.assertFalse(
				_masterPageDesignLibraryResourceTypeContributor.
					hasViewPermission(_permissionChecker, _depotEntry));
		}
	}

	private void _assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String icon, String id,
		String label, String method, String permissionKey, String target) {

		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(target, fdsActionDropdownItem.get("target"));

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));
		Assert.assertEquals(permissionKey, data.get("permissionKey"));
	}

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _NAMESPACE = RandomTestUtil.randomString();

	private static final String _URL = RandomTestUtil.randomString();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final MasterPageDesignLibraryResourceTypeContributor
		_masterPageDesignLibraryResourceTypeContributor =
			new MasterPageDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}