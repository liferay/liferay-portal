/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.display.page.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class EditDisplayPageMenuDisplayContext {

	public EditDisplayPageMenuDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoEditURLProvider<Object> infoEditURLProvider) {

		_httpServletRequest = httpServletRequest;
		_infoEditURLProvider = infoEditURLProvider;

		_layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems() {
		UnsafeConsumer<DropdownItem, Exception>
			editURLDropdownItemUnsafeConsumer =
				_getEditURLDropdownItemUnsafeConsumer(_infoEditURLProvider);

		return DropdownItemListBuilder.add(
			() -> editURLDropdownItemUnsafeConsumer != null,
			editURLDropdownItemUnsafeConsumer
		).add(
			() -> LayoutPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getLayout(),
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					HttpComponentsUtil.addParameters(
						PortalUtil.getLayoutFullURL(
							LayoutLocalServiceUtil.fetchDraftLayout(
								_themeDisplay.getPlid()),
							_themeDisplay),
						"p_l_back_url", _themeDisplay.getURLCurrent(),
						"p_l_back_url_title",
						_layoutDisplayPageObjectProvider.getTitle(
							_themeDisplay.getLocale()),
						"p_l_mode", Constants.EDIT));

				dropdownItem.setLabel(
					LanguageUtil.get(
						_themeDisplay.getLocale(),
						"edit-display-page-template"));
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditURLDropdownItemUnsafeConsumer(
			InfoEditURLProvider<Object> infoEditURLProvider) {

		if (infoEditURLProvider == null) {
			return null;
		}

		return dropdownItem -> {
			String editURL = _infoEditURLProvider.getURL(
				_layoutDisplayPageObjectProvider.getDisplayObject(),
				_httpServletRequest);

			dropdownItem.setHref(editURL);

			dropdownItem.setLabel(
				LanguageUtil.format(
					_httpServletRequest, "edit-x",
					_layoutDisplayPageObjectProvider.getTitle(
						_themeDisplay.getLocale())));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final InfoEditURLProvider<Object> _infoEditURLProvider;
	private final LayoutDisplayPageObjectProvider<?>
		_layoutDisplayPageObjectProvider;
	private final ThemeDisplay _themeDisplay;

}