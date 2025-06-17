/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Georgel Pop
 * @author Roberto Díaz
 */
public class SpaceListDisplayContext {

	public SpaceListDisplayContext(
		long groupId, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest) {

		_groupId = groupId;
		_groupLocalService = groupLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() throws Exception {
		Group group = _groupLocalService.fetchGroup(_groupId);

		if (group == null) {
			return HashMapBuilder.<String, Object>put(
				"displayType", "outline-0"
			).put(
				"name", StringPool.BLANK
			).put(
				"size", "sm"
			).build();
		}

		return HashMapBuilder.<String, Object>put(
			"displayType",
			() -> {
				UnicodeProperties unicodeProperties =
					group.getTypeSettingsProperties();

				return GetterUtil.get(
					unicodeProperties.get("logoColor"), "outline-0");
			}
		).put(
			"name", group.getDescriptiveName(_themeDisplay.getLocale())
		).put(
			"size", "sm"
		).build();
	}

	private final long _groupId;
	private final GroupLocalService _groupLocalService;
	private final ThemeDisplay _themeDisplay;

}