/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Noor Najjar
 */
public class EditVocabularyDisplayContext {

	public EditVocabularyDisplayContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
	}

	public List<Map<String, String>> getClassNameIdOptions()
		throws PortalException {

		List<Map<String, String>> selectOptions = new ArrayList<>();

		List<ObjectFolder> objectFolders = new ArrayList<>();

		objectFolders.add(
			ObjectFolderLocalServiceUtil.getObjectFolderByExternalReferenceCode(
				"L_CMS_CONTENT_STRUCTURES", _themeDisplay.getCompanyId()));
		objectFolders.add(
			ObjectFolderLocalServiceUtil.getObjectFolderByExternalReferenceCode(
				"L_CMS_FILE_TYPES", _themeDisplay.getCompanyId()));

		for (ObjectFolder objectFolder : objectFolders) {
			for (ObjectDefinition objectDefinition :
					ObjectDefinitionLocalServiceUtil.
						getObjectFolderObjectDefinitions(
							objectFolder.getObjectFolderId())) {

				selectOptions.add(
					HashMapBuilder.put(
						"restricted", Boolean.FALSE.toString()
					).put(
						"type", objectDefinition.getLabelCurrentValue()
					).put(
						"typeId",
						String.valueOf(
							PortalUtil.getClassNameId(
								objectDefinition.getClassName()))
					).build());
			}
		}

		return selectOptions;
	}

	public Map<String, Object> getReactData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"availableAssetTypes", getClassNameIdOptions()
		).put(
			"backURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false,
					"/categorization/view_vocabularies"),
				_themeDisplay)
		).put(
			"defaultLanguageId",
			LocaleUtil.toLanguageId(_themeDisplay.getSiteDefaultLocale())
		).put(
			"locales",
			JSONUtil.toJSONArray(
				LanguageUtil.getCompanyAvailableLocales(
					_themeDisplay.getCompanyId()),
				locale -> {
					String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

					return JSONUtil.put(
						"id", LocaleUtil.toLanguageId(locale)
					).put(
						"label", w3cLanguageId
					).put(
						"name", locale.getDisplayName()
					).put(
						"symbol", StringUtil.toLowerCase(w3cLanguageId)
					);
				})
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"vocabularyId",
			ParamUtil.getLong(_httpServletRequest, "vocabularyId")
		).put(
			"vocabularyPermissionsAPIURL", getVocabularyPermissionsAPIURL()
		).build();
	}

	public String getVocabularyPermissionsAPIURL() {
		long vocabularyId = ParamUtil.getLong(
			_httpServletRequest, "vocabularyId");

		if (vocabularyId == 0) {
			return "/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies" +
				"/{taxonomyVocabularyId}/permissions";
		}

		return StringBundler.concat(
			"/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/",
			vocabularyId, "/permissions");
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}