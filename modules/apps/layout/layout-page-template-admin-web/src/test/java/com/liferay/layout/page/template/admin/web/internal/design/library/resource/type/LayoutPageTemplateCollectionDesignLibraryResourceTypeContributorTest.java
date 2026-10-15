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
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
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
public class
	LayoutPageTemplateCollectionDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

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
			_group.getGroupId()
		).thenReturn(
			_GROUP_ID
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

		ReflectionTestUtil.setFieldValue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-109442")
	public void testGetCreationItems() throws Exception {
		_setUpLayoutPageTemplateCollectionLocalService();

		List<DesignLibraryResourceCreationItem>
			designLibraryResourceCreationItems =
				_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
					getCreationItems(
						_httpServletRequest, _depotEntry, _BACK_URL);

		_assertDesignLibraryResourceCreationItem(
			designLibraryResourceCreationItems.get(0),
			"add-content-page-template", "new-content-page-template",
			"page-template");
		_assertDesignLibraryResourceCreationItem(
			designLibraryResourceCreationItems.get(1), "add-page-template-set",
			"new-page-template-set", "set");
		Assert.assertEquals(
			designLibraryResourceCreationItems.toString(), 2,
			designLibraryResourceCreationItems.size());
	}

	@Test
	@TestInfo("LPD-109442")
	public void testGetCreationItemsWhenFeatureFlagIsEnabled()
		throws Exception {

		_setUpLayoutPageTemplateCollectionLocalService();

		Mockito.when(
			_liferayPortletURL.toString()
		).thenReturn(
			"http://localhost/select_layout_page_template_entry_master_layout"
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-81022"))
			).thenReturn(
				true
			);

			List<DesignLibraryResourceCreationItem>
				designLibraryResourceCreationItems =
					_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
						getCreationItems(
							_httpServletRequest, _depotEntry, _BACK_URL);

			Assert.assertEquals(
				designLibraryResourceCreationItems.toString(), 2,
				designLibraryResourceCreationItems.size());

			DesignLibraryResourceCreationItem
				designLibraryResourceCreationItem =
					designLibraryResourceCreationItems.get(0);

			Assert.assertEquals(
				"http://localhost" +
					"/select_layout_page_template_entry_master_layout",
				designLibraryResourceCreationItem.getHref());
			Assert.assertEquals(
				"add-content-page-template",
				designLibraryResourceCreationItem.getId());
			Assert.assertEquals(
				"new-content-page-template",
				designLibraryResourceCreationItem.getLabel());
			Assert.assertNull(designLibraryResourceCreationItem.getModule());
			Assert.assertNull(
				designLibraryResourceCreationItem.getModuleProps());

			_assertDesignLibraryResourceCreationItem(
				designLibraryResourceCreationItems.get(1),
				"add-page-template-set", "new-page-template-set", "set");
		}

		_portalUtilMockedStatic.verify(
			() -> PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, _group,
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES, 0, 0,
				PortletRequest.RENDER_PHASE));

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcPath", "/select_layout_page_template_entry_master_layout.jsp"
		);

		Mockito.verify(
			_liferayPortletURL, Mockito.times(2)
		).setParameter(
			"redirect", _BACK_URL
		);
	}

	@Test
	@TestInfo("LPD-107400")
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					_httpServletRequest, _depotEntry,
					RandomTestUtil.randomString());

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "view", "view", "view", null, null,
			"link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "pencil", "edit", "edit", null, null,
			"link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "password-policies", "permissions",
			"permissions", null, "permissions", "modal-permissions");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "trash", "delete", "delete",
			"delete", "delete", "async");
		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 4,
			fdsActionDropdownItems.size());

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin" +
				"/view_layout_page_template_collection_permissions"
		);

		Mockito.verify(
			_liferayPortletURL, Mockito.times(3)
		).setParameter(
			"layoutPageTemplateCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);
	}

	@Test
	@TestInfo("LPD-104840")
	public void testHasAddPermission() {
		Assert.assertFalse(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	@TestInfo("LPD-104840")
	public void testHasViewPermission() {
		Assert.assertFalse(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
	}

	private void _assertDesignLibraryResourceCreationItem(
		DesignLibraryResourceCreationItem designLibraryResourceCreationItem,
		String id, String label, String mode) {

		Assert.assertNull(designLibraryResourceCreationItem.getHref());
		Assert.assertEquals(id, designLibraryResourceCreationItem.getId());
		Assert.assertEquals(
			label, designLibraryResourceCreationItem.getLabel());
		Assert.assertEquals(
			"{AddLayoutPageTemplateEntryDesignLibraryModalContent} from " +
				"layout-page-template-admin-web",
			designLibraryResourceCreationItem.getModule());

		Map<String, Object> moduleProps =
			designLibraryResourceCreationItem.getModuleProps();

		Assert.assertEquals(mode, moduleProps.get("mode"));

		JSONArray pageTemplateSetsJSONArray = (JSONArray)moduleProps.get(
			"pageTemplateSets");

		Assert.assertEquals(
			pageTemplateSetsJSONArray.toString(), 1,
			pageTemplateSetsJSONArray.length());

		JSONObject pageTemplateSetJSONObject =
			pageTemplateSetsJSONArray.getJSONObject(0);

		Assert.assertEquals(12345, pageTemplateSetJSONObject.getLong("id"));
		Assert.assertEquals(
			"Basic Templates", pageTemplateSetJSONObject.getString("name"));
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

	private void _setUpAddLayoutPageTemplateCollectionPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION)
		).thenReturn(
			true
		);
	}

	private void _setUpLayoutPageTemplateCollectionLocalService() {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		Mockito.when(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).thenReturn(
			12345L
		);

		Mockito.when(
			layoutPageTemplateCollection.getName()
		).thenReturn(
			"Basic Templates"
		);

		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService = Mockito.mock(
				LayoutPageTemplateCollectionLocalService.class);

		Mockito.when(
			layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollections(
					_GROUP_ID, LayoutPageTemplateCollectionTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Collections.singletonList(layoutPageTemplateCollection)
		);

		ReflectionTestUtil.setFieldValue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor,
			"_layoutPageTemplateCollectionLocalService",
			layoutPageTemplateCollectionLocalService);
	}

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final
		LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor =
				new LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor();
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}