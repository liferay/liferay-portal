/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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
 * @author Javier Moral
 */
public class DisplayPageTemplateDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_displayPageTemplateDesignLibraryResourceTypeContributor,
			"_infoItemServiceRegistry", _infoItemServiceRegistry);
		ReflectionTestUtil.setFieldValue(
			_displayPageTemplateDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);

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

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(_CLASS_NAME)
		).thenReturn(
			_CLASS_NAME_ID
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
	public void testGetCreationItems() throws Exception {
		_setUpInfoItemServiceRegistry();

		List<DesignLibraryResourceCreationItem>
			designLibraryResourceCreationItems =
				_displayPageTemplateDesignLibraryResourceTypeContributor.
					getCreationItems(
						_httpServletRequest, _depotEntry, _BACK_URL);

		Assert.assertEquals(
			designLibraryResourceCreationItems.toString(), 1,
			designLibraryResourceCreationItems.size());

		DesignLibraryResourceCreationItem designLibraryResourceCreationItem =
			designLibraryResourceCreationItems.get(0);

		Assert.assertEquals(
			"add-display-page-template",
			designLibraryResourceCreationItem.getId());
		Assert.assertEquals(
			"new-display-page-template",
			designLibraryResourceCreationItem.getLabel());
		Assert.assertEquals(
			"{AddDisplayPageTemplateDesignLibraryModalContent} from " +
				"layout-page-template-admin-web",
			designLibraryResourceCreationItem.getModule());

		Map<String, Object> moduleProps =
			designLibraryResourceCreationItem.getModuleProps();

		Assert.assertEquals(_NAMESPACE, moduleProps.get("namespace"));

		JSONArray mappingTypesJSONArray = (JSONArray)moduleProps.get(
			"mappingTypes");

		Assert.assertEquals(
			mappingTypesJSONArray.toString(), 1,
			mappingTypesJSONArray.length());

		JSONObject mappingTypeJSONObject = mappingTypesJSONArray.getJSONObject(
			0);

		Assert.assertEquals(
			String.valueOf(_CLASS_NAME_ID),
			mappingTypeJSONObject.getString("id"));
		Assert.assertEquals(
			_CLASS_NAME_LABEL, mappingTypeJSONObject.getString("label"));

		JSONArray subtypesJSONArray = mappingTypeJSONObject.getJSONArray(
			"subtypes");

		Assert.assertEquals(
			subtypesJSONArray.toString(), 1, subtypesJSONArray.length());

		JSONObject subtypeJSONObject = subtypesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			_FORM_VARIATION_KEY, subtypeJSONObject.getString("id"));
		Assert.assertEquals(
			_FORM_VARIATION_LABEL, subtypeJSONObject.getString("label"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					_httpServletRequest, _depotEntry, _BACK_URL);

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 4,
			fdsActionDropdownItems.size());

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "pencil", "edit", "edit", null,
			"get", "link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "copy", "copy", "duplicate", "post",
			"copy", "async");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "password-policies", "permissions",
			"permissions", null, "permissions", "modal-permissions");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "trash", "delete", "delete",
			"delete", "delete", "async");

		FDSActionDropdownItem copyFDSActionDropdownItem =
			fdsActionDropdownItems.get(1);

		Assert.assertEquals(
			"{actions.copy.href}", copyFDSActionDropdownItem.get("href"));

		FDSActionDropdownItem deleteFDSActionDropdownItem =
			fdsActionDropdownItems.get(3);

		Assert.assertEquals(
			"{actions.delete.href}", deleteFDSActionDropdownItem.get("href"));

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin/edit_display_page"
		);

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin/view_display_page_permissions"
		);

		Mockito.verify(
			_liferayPortletURL, Mockito.times(2)
		).setParameter(
			"displayPageTemplateExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			String.valueOf(LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE),
			_displayPageTemplateDesignLibraryResourceTypeContributor.getType());
	}

	@Test
	public void testHasAddPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	public void testHasViewPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID, ActionKeys.VIEW)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
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

	private void _setUpInfoItemServiceRegistry() {
		InfoItemClassDetails infoItemClassDetails = Mockito.mock(
			InfoItemClassDetails.class);

		Mockito.when(
			infoItemClassDetails.getClassName()
		).thenReturn(
			_CLASS_NAME
		);

		Mockito.when(
			infoItemClassDetails.getLabel(LocaleUtil.US)
		).thenReturn(
			_CLASS_NAME_LABEL
		);

		Mockito.when(
			_infoItemServiceRegistry.getInfoItemClassDetails(
				_GROUP_ID, DisplayPageInfoItemCapability.KEY,
				_permissionChecker)
		).thenReturn(
			Collections.singletonList(infoItemClassDetails)
		);

		InfoItemFormVariation infoItemFormVariation = Mockito.mock(
			InfoItemFormVariation.class);

		Mockito.when(
			infoItemFormVariation.getKey()
		).thenReturn(
			_FORM_VARIATION_KEY
		);

		InfoLocalizedValue<String> labelInfoLocalizedValue = Mockito.mock(
			InfoLocalizedValue.class);

		Mockito.when(
			labelInfoLocalizedValue.getValue(LocaleUtil.US)
		).thenReturn(
			_FORM_VARIATION_LABEL
		);

		Mockito.when(
			infoItemFormVariation.getLabelInfoLocalizedValue()
		).thenReturn(
			labelInfoLocalizedValue
		);

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			Mockito.mock(InfoItemFormVariationsProvider.class);

		Mockito.when(
			infoItemFormVariationsProvider.getInfoItemFormVariations(_GROUP_ID)
		).thenReturn(
			(List)Collections.singletonList(infoItemFormVariation)
		);

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, _CLASS_NAME)
		).thenReturn(
			infoItemFormVariationsProvider
		);

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, _CLASS_NAME)
		).thenReturn(
			null
		);
	}

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _CLASS_NAME_ID = RandomTestUtil.randomLong();

	private static final String _CLASS_NAME_LABEL =
		RandomTestUtil.randomString();

	private static final String _FORM_VARIATION_KEY =
		RandomTestUtil.randomString();

	private static final String _FORM_VARIATION_LABEL =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _NAMESPACE = RandomTestUtil.randomString();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final DisplayPageTemplateDesignLibraryResourceTypeContributor
		_displayPageTemplateDesignLibraryResourceTypeContributor =
			new DisplayPageTemplateDesignLibraryResourceTypeContributor();
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final InfoItemServiceRegistry _infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}