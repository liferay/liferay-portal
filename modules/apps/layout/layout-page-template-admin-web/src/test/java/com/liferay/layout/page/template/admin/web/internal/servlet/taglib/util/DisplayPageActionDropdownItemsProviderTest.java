/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.page.template.admin.web.internal.configuration.LayoutPageTemplateAdminWebConfiguration;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class DisplayPageActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAssetDisplayPageEntryServiceUtil();
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpLayoutLocalServiceUtil();
		_setUpPortalUtil();
		_setUpPortletURLBuilder();
		_setUpRenderRequest();
		_setUpRenderURLBuilder();
		_setUpResourceURLBuilder();
		_setUpThemeDisplay();
		_setUpUploadServletRequestConfigurationProviderUtil();
	}

	@After
	public void tearDown() {
		_assetDisplayPageEntryServiceUtilMockedStatic.close();
		_layoutLocalServiceUtilMockedStatic.close();
		_layoutPageTemplateEntryPermissionMockedStatic.close();
		_renderURLBuilderMockedStatic.close();
		_resourceURLBuilderMockedStatic.close();
		_portletURLBuilderMockedStatic.close();
		_uploadServletRequestConfigurationProviderUtilMockedStatic.close();
	}

	@Test
	public void testGetActionDropdownItemsWithoutUpdatePermission()
		throws Exception {

		_setUpLayoutPageTemplateEntryPermission(false);

		DisplayPageActionDropdownItemsProvider
			displayPageActionDropdownItemsProvider =
				new DisplayPageActionDropdownItemsProvider(
					false, false, _layoutPageTemplateEntry, _renderRequest,
					_renderResponse);

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			displayPageActionDropdownItemsProvider.getActionDropdownItems());

		Assert.assertEquals(dropdownItems.toString(), 2, dropdownItems.size());

		_assertDropdownItems(
			dropdownItems, new String[] {"view-usages", "export"});
	}

	@Test
	public void testGetActionDropdownItemsWithUpdatePermission()
		throws Exception {

		_setUpLayoutPageTemplateEntryPermission(true);

		DisplayPageActionDropdownItemsProvider
			displayPageActionDropdownItemsProvider =
				new DisplayPageActionDropdownItemsProvider(
					false, false, _layoutPageTemplateEntry, _renderRequest,
					_renderResponse);

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			displayPageActionDropdownItemsProvider.getActionDropdownItems());

		Assert.assertEquals(dropdownItems.toString(), 11, dropdownItems.size());

		_assertDropdownItems(
			dropdownItems,
			new String[] {
				"edit", "change-content-type", "change-thumbnail", "rename",
				"view-usages", "make-a-copy", "export", "move", "configure",
				"permissions", "delete"
			});
	}

	private void _assertDropdownItems(
		List<DropdownItem> dropdownItems, String[] expectedValues) {

		for (int i = 0; i < dropdownItems.size(); i++) {
			DropdownItem dropdownItem = dropdownItems.get(i);

			Assert.assertEquals(expectedValues[i], dropdownItem.get("label"));
		}
	}

	private List<DropdownItem> _getActionDropdownItems(
		List<DropdownItem> dropdownItems) {

		List<DropdownItem> allDropdownItems = new ArrayList<>();

		for (DropdownItem dropdownItem : dropdownItems) {
			if (!StringUtil.equals((String)dropdownItem.get("type"), "group")) {
				allDropdownItems.add(dropdownItem);

				continue;
			}

			allDropdownItems.addAll(
				(List<DropdownItem>)dropdownItem.get("items"));
		}

		return allDropdownItems;
	}

	private void _setUpAssetDisplayPageEntryServiceUtil() {
		_assetDisplayPageEntryServiceUtilMockedStatic.when(
			() ->
				AssetDisplayPageEntryServiceUtil.
					getAssetDisplayPageEntriesCount(
						Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
						Mockito.anyBoolean())
		).thenReturn(
			1
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR)
		).thenReturn(
			_itemSelector
		);

		Mockito.when(
			_httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebConfiguration.class.getName())
		).thenReturn(
			_layoutPageTemplateAdminWebConfiguration
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		Mockito.when(
			languageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArguments()[1]
		);
	}

	private void _setUpLayoutLocalServiceUtil() {
		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchDraftLayout(Mockito.anyLong())
		).thenReturn(
			_draftLayout
		);
	}

	private void _setUpLayoutPageTemplateEntryPermission(
		boolean hasPermissions) {

		_layoutPageTemplateEntryPermissionMockedStatic.when(
			() -> LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.DELETE)
		).thenReturn(
			hasPermissions
		);

		_layoutPageTemplateEntryPermissionMockedStatic.when(
			() -> LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.PERMISSIONS)
		).thenReturn(
			hasPermissions
		);

		_layoutPageTemplateEntryPermissionMockedStatic.when(
			() -> LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.UPDATE)
		).thenReturn(
			hasPermissions
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Mockito.when(
			portalUtil.getHttpServletRequest(_renderRequest)
		).thenReturn(
			_httpServletRequest
		);
	}

	private void _setUpPortletURLBuilder() {
		Mockito.when(
			PortletURLBuilder.create(Mockito.any())
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);

		Mockito.when(
			PortletURLBuilder.createActionURL(_renderResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpRenderRequest() {
		Mockito.when(
			_renderRequest.getAttribute(InfoItemServiceRegistry.class.getName())
		).thenReturn(
			_infoItemServiceRegistry
		);
	}

	private void _setUpRenderURLBuilder() {
		Mockito.when(
			RenderURLBuilder.createRenderURL(_renderResponse)
		).thenReturn(
			new RenderURLBuilder.RenderURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpResourceURLBuilder() {
		ResourceURLBuilder.ResourceURLStep resourceURLStep = Mockito.mock(
			ResourceURLBuilder.ResourceURLStep.class);

		Mockito.when(
			ResourceURLBuilder.createResourceURL(_renderResponse)
		).thenReturn(
			resourceURLStep
		);

		ResourceURLBuilder.AfterParameterStep afterParameterStep = Mockito.mock(
			ResourceURLBuilder.AfterParameterStep.class);

		Mockito.when(
			resourceURLStep.setParameter(Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			afterParameterStep
		);

		ResourceURLBuilder.AfterResourceIDStep afterResourceIDStep =
			Mockito.mock(ResourceURLBuilder.AfterResourceIDStep.class);

		Mockito.when(
			afterParameterStep.setResourceID(Mockito.anyString())
		).thenReturn(
			afterResourceIDStep
		);

		Mockito.when(
			afterResourceIDStep.buildString()
		).thenReturn(
			StringPool.BLANK
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			Mockito.mock(PortletDisplay.class)
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			Mockito.mock(Group.class)
		);
	}

	private void _setUpUploadServletRequestConfigurationProviderUtil() {
		UploadServletRequestConfigurationProvider
			uploadServletRequestConfigurationProvider = Mockito.mock(
				UploadServletRequestConfigurationProvider.class);

		Mockito.when(
			uploadServletRequestConfigurationProvider.getMaxSize()
		).thenReturn(
			0L
		);
	}

	private final MockedStatic<AssetDisplayPageEntryServiceUtil>
		_assetDisplayPageEntryServiceUtilMockedStatic = Mockito.mockStatic(
			AssetDisplayPageEntryServiceUtil.class);
	private final Layout _draftLayout = Mockito.mock(Layout.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final InfoItemServiceRegistry _infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	private final ItemSelector _itemSelector = Mockito.mock(ItemSelector.class);
	private final MockedStatic<LayoutLocalServiceUtil>
		_layoutLocalServiceUtilMockedStatic = Mockito.mockStatic(
			LayoutLocalServiceUtil.class);
	private final LayoutPageTemplateAdminWebConfiguration
		_layoutPageTemplateAdminWebConfiguration = Mockito.mock(
			LayoutPageTemplateAdminWebConfiguration.class);
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry =
		Mockito.mock(LayoutPageTemplateEntry.class);
	private final MockedStatic<LayoutPageTemplateEntryPermission>
		_layoutPageTemplateEntryPermissionMockedStatic = Mockito.mockStatic(
			LayoutPageTemplateEntryPermission.class);
	private final MockedStatic<PortletURLBuilder>
		_portletURLBuilderMockedStatic = Mockito.mockStatic(
			PortletURLBuilder.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final MockedStatic<RenderURLBuilder> _renderURLBuilderMockedStatic =
		Mockito.mockStatic(RenderURLBuilder.class);
	private final MockedStatic<ResourceURLBuilder>
		_resourceURLBuilderMockedStatic = Mockito.mockStatic(
			ResourceURLBuilder.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final MockedStatic<UploadServletRequestConfigurationProviderUtil>
		_uploadServletRequestConfigurationProviderUtilMockedStatic =
			Mockito.mockStatic(
				UploadServletRequestConfigurationProviderUtil.class);

}