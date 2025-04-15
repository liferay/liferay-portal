/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class DDMStructureItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public DDMStructureItemDescriptor(
		DDMStructure ddmStructure, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest) {

		_ddmStructure = ddmStructure;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return "forms";
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _ddmStructure.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"ddmstructureid", String.valueOf(_ddmStructure.getStructureId())
		).put(
			"ddmstructurekey", _ddmStructure.getStructureKey()
		).put(
			"name", _ddmStructure.getName(themeDisplay.getLocale())
		).put(
			"scope",
			() -> {
				Group group = _groupLocalService.fetchGroup(
					_ddmStructure.getGroupId());

				if (group == null) {
					return null;
				}

				return LanguageUtil.get(
					themeDisplay.getLocale(),
					group.getScopeLabel(themeDisplay));
			}
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return _ddmStructure.getDescription(locale);
	}

	@Override
	public String getTitle(Locale locale) {
		return _ddmStructure.getName(locale);
	}

	@Override
	public long getUserId() {
		return _ddmStructure.getUserId();
	}

	@Override
	public String getUserName() {
		return _ddmStructure.getUserName();
	}

	@Override
	public boolean isCompact() {
		return true;
	}

	private final DDMStructure _ddmStructure;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;

}