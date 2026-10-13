/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Evan Thibodeau
 */
public class StyleBookUtil {

	public static List<Map<String, Object>> getFrontendTokenDefinitionProviders(
		long companyId, Locale locale) {

		List<Map<String, Object>> frontendTokenDefinitionProviders =
			new ArrayList<>();

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		if (frontendTokenDefinitionRegistry == null) {
			return frontendTokenDefinitionProviders;
		}

		CETManager cetManager = _cetManagerSnapshot.get();

		for (FrontendTokenDefinition frontendTokenDefinition :
				frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					companyId)) {

			if (Objects.equals(
					frontendTokenDefinition.getThemeType(),
					FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL)) {

				continue;
			}

			if (Objects.equals(
					frontendTokenDefinition.getThemeType(),
					FrontendTokenDefinitionConstants.
						THEME_TYPE_THEME_CSS_CET)) {

				if (cetManager == null) {
					continue;
				}

				CET cet = cetManager.getCET(
					companyId, frontendTokenDefinition.getThemeId());

				ThemeCSSCET themeCSSCET = (ThemeCSSCET)cet;

				if ((themeCSSCET == null) ||
					StringUtil.equalsIgnoreCase(
						themeCSSCET.getScope(), "controlPanel")) {

					continue;
				}
			}

			frontendTokenDefinitionProviders.add(
				HashMapBuilder.<String, Object>put(
					"name", _getThemeName(frontendTokenDefinition, locale)
				).put(
					"themeId", frontendTokenDefinition.getThemeId()
				).build());
		}

		return frontendTokenDefinitionProviders;
	}

	public static List<String> getFrontendTokensValuesKeys(
		Locale locale, StyleBookEntry styleBookEntry) {

		List<String> frontendTokensValuesKeys = new ArrayList<>();

		frontendTokensValuesKeys.addAll(
			_getFrontendTokensValuesKeys(
				styleBookEntry.getCompanyId(), locale,
				StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_GLOBAL));
		frontendTokensValuesKeys.addAll(
			_getFrontendTokensValuesKeys(
				styleBookEntry.getCompanyId(), locale,
				styleBookEntry.getThemeId()));
		frontendTokensValuesKeys.addAll(
			_getFrontendTokensValuesKeys(
				StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM,
				FrontendTokenDefinitionUtil.
					parseFrontendTokenDefinitionJSONObject(
						styleBookEntry.getFrontendTokenDefinition())));

		return frontendTokensValuesKeys;
	}

	public static StyleBookEntry getStyleFromThemeStyleBookEntry(
		FrontendTokenDefinition frontendTokenDefinition, long groupId,
		Locale locale) {

		StyleBookEntry styleFromThemeStyleBookEntry =
			StyleBookEntryLocalServiceUtil.create();

		styleFromThemeStyleBookEntry.setHeadId(-1);
		styleFromThemeStyleBookEntry.setStyleBookEntryId(0);
		styleFromThemeStyleBookEntry.setGroupId(groupId);

		if (frontendTokenDefinition == null) {
			return styleFromThemeStyleBookEntry;
		}

		styleFromThemeStyleBookEntry.setThemeId(
			frontendTokenDefinition.getThemeId());

		StyleBookEntry defaultStyleBookEntry =
			StyleBookEntryLocalServiceUtil.fetchDefaultStyleBookEntry(
				groupId, frontendTokenDefinition.getThemeId());

		if (defaultStyleBookEntry == null) {
			styleFromThemeStyleBookEntry.setDefaultStyleBookEntry(true);
		}

		styleFromThemeStyleBookEntry.setName(
			LanguageUtil.format(
				locale, "styles-from-x",
				_getThemeName(frontendTokenDefinition, locale)));

		return styleFromThemeStyleBookEntry;
	}

	public static StyleBookEntry getStyleFromThemeStyleBookEntry(
		Layout layout, Locale locale) {

		return getStyleFromThemeStyleBookEntry(
			_getFrontendTokenDefinition(layout), layout.getGroupId(), locale);
	}

	public static String getThemeName(Layout layout, Locale locale) {
		FrontendTokenDefinition frontendTokenDefinition =
			_getFrontendTokenDefinition(layout);

		if (frontendTokenDefinition != null) {
			return _getThemeName(frontendTokenDefinition, locale);
		}

		return null;
	}

	public static String getThemeName(
		long companyId, Locale locale, String themeId) {

		FrontendTokenDefinition frontendTokenDefinition =
			_getFrontendTokenDefinition(companyId, themeId);

		if (frontendTokenDefinition == null) {
			return themeId;
		}

		return _getThemeName(frontendTokenDefinition, locale);
	}

	public static boolean isThemeInactive(long companyId, String themeId) {
		if (_getFrontendTokenDefinition(companyId, themeId) == null) {
			return true;
		}

		return false;
	}

	private static FrontendTokenDefinition _getFrontendTokenDefinition(
		Layout layout) {

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		return frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
			layout);
	}

	private static FrontendTokenDefinition _getFrontendTokenDefinition(
		long companyId, String themeId) {

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		return frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
			companyId, themeId);
	}

	private static List<String> _getFrontendTokensValuesKeys(
		long companyId, Locale locale, String themeId) {

		FrontendTokenDefinition frontendTokenDefinition =
			_getFrontendTokenDefinition(companyId, themeId);

		if (frontendTokenDefinition == null) {
			return Collections.emptyList();
		}

		return _getFrontendTokensValuesKeys(
			themeId, frontendTokenDefinition.getJSONObject(locale));
	}

	private static List<String> _getFrontendTokensValuesKeys(
		String frontendTokenDefinitionId,
		JSONObject frontendTokenDefinitionJSONObject) {

		return TransformUtil.transform(
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				frontendTokenDefinitionJSONObject),
			frontendTokenName ->
				FrontendTokensValuesUtil.getFrontendTokensValuesKey(
					frontendTokenDefinitionId, frontendTokenName));
	}

	private static String _getThemeName(
		FrontendTokenDefinition frontendTokenDefinition, Locale locale) {

		if (Objects.equals(
				frontendTokenDefinition.getThemeType(),
				FrontendTokenDefinitionConstants.THEME_TYPE_BUNDLE)) {

			return LanguageUtil.format(
				locale, "x-theme",
				frontendTokenDefinition.getThemeName(locale));
		}

		if (Objects.equals(
				frontendTokenDefinition.getThemeType(),
				FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL)) {

			return frontendTokenDefinition.getThemeName(locale);
		}

		return LanguageUtil.format(
			locale, "x-theme-css-client-extension",
			frontendTokenDefinition.getThemeName(locale));
	}

	private static final Snapshot<CETManager> _cetManagerSnapshot =
		new Snapshot<>(StyleBookUtil.class, CETManager.class);
	private static final Snapshot<FrontendTokenDefinitionRegistry>
		_frontendTokenDefinitionRegistrySnapshot = new Snapshot<>(
			StyleBookUtil.class, FrontendTokenDefinitionRegistry.class);

}