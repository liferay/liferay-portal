/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class InfoCollectionProviderActionDropdownItems {

	public InfoCollectionProviderActionDropdownItems(
		InfoCollectionProvider<?> infoCollectionProvider,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_infoCollectionProvider = infoCollectionProvider;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			_getViewInfoCollectionProviderItemsActionUnsafeConsumer()
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getViewInfoCollectionProviderItemsActionUnsafeConsumer()
		throws Exception {

		PortletURL viewInfoCollectionProviderItemsURL =
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/view_info_collection_provider_items.jsp"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"infoCollectionProviderKey", _infoCollectionProvider.getKey()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildPortletURL();

		return dropdownItem -> {
			dropdownItem.putData("action", "viewInfoCollectionProviderItems");
			dropdownItem.putData(
				"infoCollectionProviderTitle",
				_infoCollectionProvider.getLabel(_themeDisplay.getLocale()));
			dropdownItem.putData(
				"viewInfoCollectionProviderItemsURL",
				String.valueOf(viewInfoCollectionProviderItemsURL));
			dropdownItem.setIcon("view");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-items"));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final InfoCollectionProvider<?> _infoCollectionProvider;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}