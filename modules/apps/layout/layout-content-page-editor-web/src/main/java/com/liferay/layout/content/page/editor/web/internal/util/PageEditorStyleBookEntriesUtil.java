/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Lima
 */
public class PageEditorStyleBookEntriesUtil {

	public static List<Map<String, Object>> getStyleBookEntries(
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		long liveGroupId = StagingUtil.getLiveGroupId(layout.getGroupId());

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				layout.getCompanyId(), liveGroupId,
				frontendTokenDefinition.getThemeId());

		Map<Long, Group> scopeGroups = new HashMap<>();

		return TransformUtil.transform(
			styleBookEntries,
			styleBookEntry -> _getStyleBookEntryMap(
				frontendTokenDefinition, includeTokenValues, liveGroupId,
				scopeGroups, styleBookEntry, themeDisplay));
	}

	public static JSONArray getStyleBookEntriesJSONArray(
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		return JSONUtil.toJSONArray(
			getStyleBookEntries(
				frontendTokenDefinition, includeTokenValues, layout,
				themeDisplay),
			map -> JSONFactoryUtil.createJSONObject(map));
	}

	private static Map<String, Object> _getStyleBookEntryMap(
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues, long liveGroupId,
			Map<Long, Group> scopeGroups, StyleBookEntry styleBookEntry,
			ThemeDisplay themeDisplay)
		throws Exception {

		Map<String, Object> map = HashMapBuilder.<String, Object>put(
			"imagePreviewURL", styleBookEntry.getImagePreviewURL(themeDisplay)
		).put(
			"name", styleBookEntry.getName()
		).put(
			"styleBookEntryERC", styleBookEntry.getExternalReferenceCode()
		).build();

		long entryGroupId = styleBookEntry.getGroupId();

		if (entryGroupId != liveGroupId) {
			Group scopeGroup = scopeGroups.computeIfAbsent(
				entryGroupId, GroupLocalServiceUtil::fetchGroup);

			if (scopeGroup != null) {
				map.put(
					"styleBookEntryScopeERC",
					scopeGroup.getExternalReferenceCode());
				map.put(
					"subtitle",
					scopeGroup.getDescriptiveName(themeDisplay.getLocale()));
			}
		}

		if (includeTokenValues) {
			map.put(
				"tokenValues",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, themeDisplay.getLocale(),
					styleBookEntry));
		}

		return map;
	}

}