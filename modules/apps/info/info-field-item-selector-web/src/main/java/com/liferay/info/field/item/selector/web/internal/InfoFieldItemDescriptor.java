/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.item.selector.web.internal;

import com.liferay.info.field.InfoField;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class InfoFieldItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public InfoFieldItemDescriptor(
		HttpServletRequest httpServletRequest, InfoField<?> infoField) {

		_httpServletRequest = httpServletRequest;
		_infoField = infoField;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"label",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _infoField.getLabel(themeDisplay.getLocale());
			}
		).put(
			"localizable", _infoField.isLocalizable()
		).put(
			"name", _infoField.getName()
		).put(
			"uniqueId", _infoField.getUniqueId()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return null;
	}

	private final HttpServletRequest _httpServletRequest;
	private final InfoField<?> _infoField;

}