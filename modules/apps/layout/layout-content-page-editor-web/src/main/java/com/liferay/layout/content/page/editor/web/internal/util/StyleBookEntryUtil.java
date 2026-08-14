/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookEntryUtil {

	public static Map<String, Object> getFrontendTokensValues(
			FrontendTokenDefinition frontendTokenDefinition, Locale locale,
			StyleBookEntry styleBookEntry)
		throws Exception {

		Map<String, Object> frontendTokensValues = new LinkedHashMap<>();

		if (frontendTokenDefinition == null) {
			return frontendTokensValues;
		}

		JSONObject frontendTokenValuesJSONObject =
			_getFrontendTokenValuesJSONObject(styleBookEntry);

		JSONObject frontendTokenDefinitionJSONObject =
			_getFrontendTokenDefinitionJSONObject(
				frontendTokenDefinition, locale, styleBookEntry);

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			JSONArray frontendTokenSetsJSONArray =
				frontendTokenCategoryJSONObject.getJSONArray(
					"frontendTokenSets");

			for (int j = 0; j < frontendTokenSetsJSONArray.length(); j++) {
				JSONObject frontendTokenSetJSONObject =
					frontendTokenSetsJSONArray.getJSONObject(j);

				JSONArray frontendTokensJSONArray =
					frontendTokenSetJSONObject.getJSONArray("frontendTokens");

				for (int k = 0; k < frontendTokensJSONArray.length(); k++) {
					JSONObject frontendTokenJSONObject =
						frontendTokensJSONArray.getJSONObject(k);

					frontendTokensValues.put(
						frontendTokenJSONObject.getString("name"),
						_getProcessedFrontendTokenValue(
							frontendTokenCategoryJSONObject.getString("label"),
							frontendTokenJSONObject,
							frontendTokenSetJSONObject.getString("label"),
							frontendTokenValuesJSONObject,
							frontendTokenDefinition.getThemeId()));
				}
			}
		}

		return frontendTokensValues;
	}

	public static List<Map<String, Object>> getStyleBookEntryMaps(
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeFrontendTokensValues, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		long groupId = StagingUtil.getLiveGroupId(layout.getGroupId());
		Map<Long, Group> scopeGroups = new HashMap<>();

		return TransformUtil.transform(
			StyleBookEntryProviderUtil.getStyleBookEntries(
				layout.getCompanyId(), groupId,
				frontendTokenDefinition.getThemeId()),
			styleBookEntry -> _getStyleBookEntryMap(
				frontendTokenDefinition, groupId, includeFrontendTokensValues,
				scopeGroups, styleBookEntry, themeDisplay));
	}

	private static JSONObject _getFrontendTokenDefinitionJSONObject(
			FrontendTokenDefinition frontendTokenDefinition, Locale locale,
			StyleBookEntry styleBookEntry)
		throws Exception {

		JSONObject frontendTokenDefinitionJSONObject =
			frontendTokenDefinition.getJSONObject(locale);

		if (!DefaultStyleBookEntryUtil.isStyleBookEntryApplicable(
				frontendTokenDefinition, styleBookEntry)) {

			return frontendTokenDefinitionJSONObject;
		}

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.parseFrontendTokenDefinitionJSONObject(
				styleBookEntry.getFrontendTokenDefinition());

		if (overrideFrontendTokenDefinitionJSONObject == null) {
			return frontendTokenDefinitionJSONObject;
		}

		return FrontendTokenDefinitionUtil.
			getMergedFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);
	}

	private static JSONObject _getFrontendTokenValuesJSONObject(
			StyleBookEntry styleBookEntry)
		throws Exception {

		if (styleBookEntry != null) {
			return JSONFactoryUtil.createJSONObject(
				styleBookEntry.getFrontendTokensValues());
		}

		return JSONFactoryUtil.createJSONObject();
	}

	private static Map<String, Object> _getProcessedFrontendTokenValue(
		String frontendTokenCategoryLabel, JSONObject frontendTokenJSONObject,
		String frontendTokenSetLabel, JSONObject frontendTokenValuesJSONObject,
		String themeId) {

		String name = frontendTokenJSONObject.getString("name");

		JSONObject valueJSONObject = null;

		if (Validator.isNotNull(themeId)) {
			valueJSONObject = frontendTokenValuesJSONObject.getJSONObject(
				themeId + StringPool.COLON + name);
		}

		if (valueJSONObject == null) {
			valueJSONObject = frontendTokenValuesJSONObject.getJSONObject(name);
		}

		String value = StringPool.BLANK;

		if (valueJSONObject != null) {
			value = valueJSONObject.getString("value");
		}
		else {
			value = frontendTokenJSONObject.getString("defaultValue");
		}

		return HashMapBuilder.<String, Object>put(
			FrontendTokenMapping.TYPE_CSS_VARIABLE,
			() -> {
				JSONArray mappingsJSONArray =
					frontendTokenJSONObject.getJSONArray("mappings");

				for (int l = 0; l < mappingsJSONArray.length(); l++) {
					JSONObject mappingJSONObject =
						mappingsJSONArray.getJSONObject(l);

					if (Objects.equals(
							mappingJSONObject.getString("type"),
							FrontendTokenMapping.TYPE_CSS_VARIABLE)) {

						return mappingJSONObject.getString("value");
					}
				}

				return null;
			}
		).put(
			"editorType", frontendTokenJSONObject.get("editorType")
		).put(
			"label", frontendTokenJSONObject.get("label")
		).put(
			"name", name
		).put(
			"tokenCategoryLabel", frontendTokenCategoryLabel
		).put(
			"tokenSetLabel", frontendTokenSetLabel
		).put(
			"value", value
		).build();
	}

	private static Group _getScopeGroup(
		long groupId, Map<Long, Group> scopeGroups,
		StyleBookEntry styleBookEntry) {

		if (styleBookEntry.getGroupId() == groupId) {
			return null;
		}

		return scopeGroups.computeIfAbsent(
			styleBookEntry.getGroupId(), GroupLocalServiceUtil::fetchGroup);
	}

	private static Map<String, Object> _getStyleBookEntryMap(
		FrontendTokenDefinition frontendTokenDefinition, long groupId,
		boolean includeFrontendTokensValues, Map<Long, Group> scopeGroups,
		StyleBookEntry styleBookEntry, ThemeDisplay themeDisplay) {

		Group scopeGroup = _getScopeGroup(groupId, scopeGroups, styleBookEntry);

		return HashMapBuilder.<String, Object>put(
			"imagePreviewURL", styleBookEntry.getImagePreviewURL(themeDisplay)
		).put(
			"name", styleBookEntry.getName()
		).put(
			"styleBookEntryERC", styleBookEntry.getExternalReferenceCode()
		).put(
			"styleBookEntryScopeERC",
			() -> {
				if (scopeGroup == null) {
					return null;
				}

				return scopeGroup.getExternalReferenceCode();
			}
		).put(
			"subtitle",
			() -> {
				if (scopeGroup == null) {
					return null;
				}

				return scopeGroup.getDescriptiveName(themeDisplay.getLocale());
			}
		).put(
			"tokenValues",
			() -> {
				if (!includeFrontendTokensValues) {
					return null;
				}

				return getFrontendTokensValues(
					frontendTokenDefinition, themeDisplay.getLocale(),
					styleBookEntry);
			}
		).build();
	}

}