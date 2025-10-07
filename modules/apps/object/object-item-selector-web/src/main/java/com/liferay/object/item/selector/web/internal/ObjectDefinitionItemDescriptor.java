/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Jonathan McCann
 */
public class ObjectDefinitionItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public ObjectDefinitionItemDescriptor(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;
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
	public Date getModifiedDate() {
		return _objectDefinition.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"label", _objectDefinition.getLabel(themeDisplay.getLocale())
		).put(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return HtmlUtil.escape(_objectDefinition.getLabel(locale));
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;

}