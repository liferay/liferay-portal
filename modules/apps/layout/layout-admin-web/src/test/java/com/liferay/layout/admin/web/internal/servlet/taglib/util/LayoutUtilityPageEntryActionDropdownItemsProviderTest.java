/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.admin.web.internal.configuration.LayoutUtilityPageThumbnailConfiguration;
import com.liferay.layout.utility.page.constants.LayoutUtilityPageActionKeys;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Eudaldo Alonso
 */
public class LayoutUtilityPageEntryActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpItemSelector();
		_setUpLanguageUtil();
		_setUpLayoutLocalServiceUtil();
		_setUpLayoutUtilityPageEntryLocalServiceUtil();
		_setUpLayoutUtilityPageEntryPermission();
		_setUpLayoutUtilityPageThumbnailConfiguration();
		_setUpPermissionsURLTag();
		_setUpPortalUtil();
		_setUpPortletURLBuilder();
		_setUpRenderRequest();
		_setUpResourceURLBuilder();
		_setUpStagingGroupHelper();
		_setUpThemeDisplay();
		_setUpUploadServletRequestConfigurationProviderUtil();
	}

	@After
	public void tearDown() {
		_groupPermissionUtilMockedStatic.close();
		_permissionsURLTagMockedStatic.close();
		_portletURLBuilderMockedStatic.close();
		_resourceURLBuilderMockedStatic.close();
		_stagingGroupHelperUtilMockedStatic.close();
		_uploadServletRequestConfigurationProviderUtilMockedStatic.close();
	}

	@Test
	public void testGetActionDropdownItemsWithAssignDefaultLayoutUtilityPageEntryPermissions() {
		_setUpGroupPermissionUtil(true);

		LayoutUtilityPageEntryActionDropdownItemsProvider
			layoutUtilityPageEntryActionDropdownItemsProvider =
				new LayoutUtilityPageEntryActionDropdownItemsProvider(
					_layoutUtilityPageEntry, _renderRequest, _renderResponse);

		Assert.assertNotNull(
			_findFirstDropdownItem(
				layoutUtilityPageEntryActionDropdownItemsProvider.
					getActionDropdownItems(),
				"mark-as-default"));
	}

	@Test
	public void testGetActionDropdownItemsWithoutAssignDefaultLayoutUtilityPageEntryPermissions() {
		_setUpGroupPermissionUtil(false);

		LayoutUtilityPageEntryActionDropdownItemsProvider
			layoutUtilityPageEntryActionDropdownItemsProvider =
				new LayoutUtilityPageEntryActionDropdownItemsProvider(
					_layoutUtilityPageEntry, _renderRequest, _renderResponse);

		Assert.assertNull(
			_findFirstDropdownItem(
				layoutUtilityPageEntryActionDropdownItemsProvider.
					getActionDropdownItems(),
				"mark-as-default"));
	}

	private DropdownItem _findFirstDropdownItem(
		List<DropdownItem> dropdownItems, String label) {

		for (DropdownItem dropdownItem : dropdownItems) {
			if (!StringUtil.equals((String)dropdownItem.get("type"), "group")) {
				continue;
			}

			for (DropdownItem childDropdownItem :
					(List<DropdownItem>)dropdownItem.get("items")) {

				if (StringUtil.equals(
						(String)childDropdownItem.get("label"), label)) {

					return childDropdownItem;
				}
			}
		}

		return null;
	}

	private void _setUpGroupPermissionUtil(
		boolean assignDefaultLayoutUtilityPageEntry) {

		_groupPermissionUtilMockedStatic.when(
			() -> GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_layoutUtilityPageEntry.getGroupId(),
				LayoutUtilityPageActionKeys.
					ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY)
		).thenReturn(
			assignDefaultLayoutUtilityPageEntry
		);
	}

	private void _setUpItemSelector() {
		PortletURL itemSelectorPortletURL = Mockito.mock(PortletURL.class);

		Mockito.when(
			itemSelectorPortletURL.toString()
		).thenReturn(
			"itemSelectorPortletURL"
		);

		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(), Mockito.anyString(), Mockito.any())
		).thenReturn(
			itemSelectorPortletURL
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		Mockito.when(
			languageUtil.get(_httpServletRequest, "mark-as-default")
		).thenReturn(
			"mark-as-default"
		);
	}

	private void _setUpLayoutLocalServiceUtil() {
		LayoutLocalService layoutLocalService = Mockito.mock(
			LayoutLocalService.class);

		LayoutLocalServiceUtil.setService(layoutLocalService);

		Mockito.when(
			layoutLocalService.fetchDraftLayout(Mockito.anyLong())
		).thenReturn(
			_draftLayout
		);

		Mockito.when(
			layoutLocalService.fetchLayout(Mockito.anyLong())
		).thenReturn(
			_layout
		);
	}

	private void _setUpLayoutUtilityPageEntryLocalServiceUtil() {
		LayoutUtilityPageEntryLocalService layoutUtilityPageEntryLocalService =
			Mockito.mock(LayoutUtilityPageEntryLocalService.class);

		Mockito.when(
			layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_layoutUtilityPageEntry
		);

		ReflectionTestUtil.setFieldValue(
			LayoutUtilityPageEntryLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<LayoutUtilityPageEntryLocalService>(
				LayoutUtilityPageEntryLocalServiceUtil.class,
				LayoutUtilityPageEntryLocalService.class) {

				@Override
				public LayoutUtilityPageEntryLocalService get() {
					return layoutUtilityPageEntryLocalService;
				}

			});
	}

	private void _setUpLayoutUtilityPageEntryPermission() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ModelResourcePermission<LayoutUtilityPageEntry>
			layoutUtilityPageEntryPermission = Mockito.mock(
				ModelResourcePermission.class);

		Mockito.when(
			layoutUtilityPageEntryPermission.contains(
				Mockito.any(PermissionChecker.class),
				Mockito.any(LayoutUtilityPageEntry.class), Mockito.anyString())
		).thenReturn(
			true
		);

		bundleContext.registerService(
			ModelResourcePermission.class, layoutUtilityPageEntryPermission,
			MapUtil.singletonDictionary(
				"model.class.name",
				"com.liferay.layout.utility.page.model." +
					"LayoutUtilityPageEntry"));
	}

	private void _setUpLayoutUtilityPageThumbnailConfiguration() {
		Mockito.when(
			_layoutUtilityPageThumbnailConfiguration.thumbnailExtensions()
		).thenReturn(
			new String[0]
		);
	}

	private void _setUpPermissionsURLTag() throws Exception {
		Mockito.when(
			PermissionsURLTag.doTag(
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())
		).thenReturn(
			""
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
			PortletURLBuilder.createActionURL(_renderResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);

		Mockito.when(
			PortletURLBuilder.createRenderURL(_renderResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpRenderRequest() {
		Mockito.when(
			_renderRequest.getAttribute(ItemSelector.class.getName())
		).thenReturn(
			_itemSelector
		);

		Mockito.when(
			_renderRequest.getAttribute(
				LayoutUtilityPageThumbnailConfiguration.class.getName())
		).thenReturn(
			_layoutUtilityPageThumbnailConfiguration
		);

		Mockito.when(
			_renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
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

	private void _setUpStagingGroupHelper() {
		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			StagingGroupHelperUtil.getStagingGroupHelper()
		).thenReturn(
			stagingGroupHelper
		);

		Mockito.when(
			stagingGroupHelper.isLiveGroup(Mockito.any(Group.class))
		).thenReturn(
			false
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

	private final Layout _draftLayout = Mockito.mock(Layout.class);
	private final MockedStatic<GroupPermissionUtil>
		_groupPermissionUtilMockedStatic = Mockito.mockStatic(
			GroupPermissionUtil.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ItemSelector _itemSelector = Mockito.mock(ItemSelector.class);
	private final Layout _layout = Mockito.mock(Layout.class);
	private final LayoutUtilityPageEntry _layoutUtilityPageEntry = Mockito.mock(
		LayoutUtilityPageEntry.class);
	private final LayoutUtilityPageThumbnailConfiguration
		_layoutUtilityPageThumbnailConfiguration = Mockito.mock(
			LayoutUtilityPageThumbnailConfiguration.class);
	private final MockedStatic<PermissionsURLTag>
		_permissionsURLTagMockedStatic = Mockito.mockStatic(
			PermissionsURLTag.class);
	private final MockedStatic<PortletURLBuilder>
		_portletURLBuilderMockedStatic = Mockito.mockStatic(
			PortletURLBuilder.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final MockedStatic<ResourceURLBuilder>
		_resourceURLBuilderMockedStatic = Mockito.mockStatic(
			ResourceURLBuilder.class);
	private final MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final MockedStatic<UploadServletRequestConfigurationProviderUtil>
		_uploadServletRequestConfigurationProviderUtilMockedStatic =
			Mockito.mockStatic(
				UploadServletRequestConfigurationProviderUtil.class);

}