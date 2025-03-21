/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.util;

import com.liferay.cookies.banner.web.internal.display.context.CookiesPreferenceHandlingConfigurationDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Rachael Koestartyo
 */
public class CookiesPreferenceHandlingConfigurationActionDropdownItemsProvider {

	public CookiesPreferenceHandlingConfigurationActionDropdownItemsProvider(
		CookiesPreferenceHandlingConfigurationDisplayContext
			cookiesPreferenceHandlingConfigurationDisplayContext,
		HttpServletRequest httpServletRequest) {

		_cookiesPreferenceHandlingConfigurationDisplayContext =
			cookiesPreferenceHandlingConfigurationDisplayContext;
		_httpServletRequest = httpServletRequest;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					dropdownItem -> {
						dropdownItem.setHref(
							_cookiesPreferenceHandlingConfigurationDisplayContext.
								getDeleteConfigurationActionURL());
						dropdownItem.setLabel(
							LanguageUtil.get(
								_httpServletRequest, "reset-default-values"));
					}
				).add(
					dropdownItem -> {
						dropdownItem.setHref(
							_cookiesPreferenceHandlingConfigurationDisplayContext.
								getExportConfigurationActionURL());
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "export"));
					}
				).build())
		).build();
	}

	private final CookiesPreferenceHandlingConfigurationDisplayContext
		_cookiesPreferenceHandlingConfigurationDisplayContext;
	private final HttpServletRequest _httpServletRequest;

}