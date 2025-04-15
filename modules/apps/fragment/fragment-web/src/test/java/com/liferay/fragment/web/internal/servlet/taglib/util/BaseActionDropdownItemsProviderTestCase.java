/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.fragment.web.internal.configuration.FragmentPortletConfiguration;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseActionDropdownItemsProviderTestCase {

	@Before
	public void setUp() {
		_setUpFragmentPortletConfiguration();
		_setUpHttpServletRequest();
		_setUpItemSelector();
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpPortletURLBuilder();
		_setUpRenderRequest();
		_setUpRenderResponse();
		_setUpUploadServletRequestConfigurationProviderUtil();
	}

	@After
	public void tearDown() {
		_fragmentPermissionMockedStatic.close();
		_portletURLBuilderMockedStatic.close();
		_uploadServletRequestConfigurationProviderUtilMockedStatic.close();
	}

	protected void assertDropdownItemsInCorrectOrder(
		List<DropdownItem> dropdownItems, String... labels) {

		dropdownItems = _getActionDropdownItems(dropdownItems);

		Assert.assertEquals(
			dropdownItems.toString(), labels.length, dropdownItems.size());

		for (int i = 0; i < dropdownItems.size(); i++) {
			DropdownItem dropdownItem = dropdownItems.get(i);

			Assert.assertEquals(labels[i], dropdownItem.get("label"));
		}
	}

	protected void setUpFragmentPermission(boolean contains) {
		Mockito.when(
			FragmentPermission.contains(
				Mockito.any(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			contains
		);
	}

	protected final HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	protected final RenderRequest renderRequest = Mockito.mock(
		RenderRequest.class);
	protected final RenderResponse renderResponse = Mockito.mock(
		RenderResponse.class);

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

	private void _setUpFragmentPortletConfiguration() {
		Mockito.when(
			_fragmentPortletConfiguration.thumbnailExtensions()
		).thenReturn(
			new String[0]
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			httpServletRequest.getAttribute(
				FragmentPortletConfiguration.class.getName())
		).thenReturn(
			_fragmentPortletConfiguration
		);

		Mockito.when(
			httpServletRequest.getAttribute(ItemSelector.class.getName())
		).thenReturn(
			_itemSelector
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpItemSelector() {
		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(), Mockito.anyString(), Mockito.any())
		).thenReturn(
			Mockito.mock(PortletURL.class)
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

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Mockito.when(
			portalUtil.getHttpServletRequest(renderRequest)
		).thenReturn(
			httpServletRequest
		);
	}

	private void _setUpPortletURLBuilder() {
		Mockito.when(
			PortletURLBuilder.create(Mockito.any())
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);

		Mockito.when(
			PortletURLBuilder.createActionURL(renderResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);

		Mockito.when(
			PortletURLBuilder.createRenderURL(renderResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpRenderRequest() {
		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpRenderResponse() {
		Mockito.when(
			renderResponse.createRenderURL()
		).thenReturn(
			Mockito.mock(LiferayPortletURL.class)
		);

		Mockito.when(
			renderResponse.createResourceURL()
		).thenReturn(
			Mockito.mock(LiferayPortletURL.class)
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

	private final MockedStatic<FragmentPermission>
		_fragmentPermissionMockedStatic = Mockito.mockStatic(
			FragmentPermission.class);
	private final FragmentPortletConfiguration _fragmentPortletConfiguration =
		Mockito.mock(FragmentPortletConfiguration.class);
	private final ItemSelector _itemSelector = Mockito.mock(ItemSelector.class);
	private final MockedStatic<PortletURLBuilder>
		_portletURLBuilderMockedStatic = Mockito.mockStatic(
			PortletURLBuilder.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final MockedStatic<UploadServletRequestConfigurationProviderUtil>
		_uploadServletRequestConfigurationProviderUtilMockedStatic =
			Mockito.mockStatic(
				UploadServletRequestConfigurationProviderUtil.class);

}