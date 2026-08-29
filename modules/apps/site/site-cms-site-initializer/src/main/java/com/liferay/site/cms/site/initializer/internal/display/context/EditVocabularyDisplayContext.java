/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

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

	public String getBackURL() throws Exception {
		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isNotNull(backURL)) {
			return backURL;
		}

		return PortalUtil.getLayoutFullURL(
			LayoutLocalServiceUtil.getLayoutByFriendlyURL(
				_themeDisplay.getScopeGroupId(), false,
				"/categorization/view-vocabularies"),
			_themeDisplay);
	}

	public Map<String, Object> getReactData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"availableAssetTypes",
			TransformUtil.transform(
				ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					CompanyThreadLocal.getCompanyId(),
					new String[] {
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
						ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
					}),
				objectDefinition -> HashMapBuilder.<String, Object>put(
					"required", Boolean.FALSE
				).put(
					"type", objectDefinition.getLabelCurrentValue()
				).put(
					"typeId",
					PortalUtil.getClassNameId(objectDefinition.getClassName())
				).build())
		).put(
			"backURL", getBackURL()
		).put(
			"cmsGroupId", _themeDisplay.getScopeGroupId()
		).put(
			"defaultLanguageId",
			LocaleUtil.toLanguageId(_themeDisplay.getSiteDefaultLocale())
		).put(
			"externalReferenceCodeMaxLength",
			ModelHintsUtil.getMaxLength(
				AssetVocabulary.class.getName(), "externalReferenceCode")
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