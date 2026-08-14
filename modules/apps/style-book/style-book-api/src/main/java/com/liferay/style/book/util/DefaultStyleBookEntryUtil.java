/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Víctor Galán
 */
public class DefaultStyleBookEntryUtil {

	public static StyleBookEntry getDefaultMasterStyleBookEntry(Layout layout) {
		StyleBookEntry styleBookEntry = _getMasterLayoutStyleBookEntry(layout);

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		FrontendTokenDefinition frontendTokenDefinition =
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(layout);

		if (frontendTokenDefinition != null) {
			if ((styleBookEntry == null) ||
				StringUtil.equals(
					frontendTokenDefinition.getThemeType(),
					FrontendTokenDefinitionConstants.
						THEME_TYPE_THEME_CSS_CET)) {

				return StyleBookEntryLocalServiceUtil.
					fetchDefaultStyleBookEntry(
						StagingUtil.getLiveGroupId(layout.getGroupId()),
						frontendTokenDefinition.getThemeId());
			}

			return styleBookEntry;
		}

		return null;
	}

	public static StyleBookEntry getDefaultStyleBookEntry(Layout layout) {
		StyleBookEntry styleBookEntry =
			StyleBookEntryProviderUtil.getStyleBookEntry(layout);

		if ((styleBookEntry == null) ||
			!_isStyleBookEntryApplicable(layout, styleBookEntry)) {

			return getDefaultMasterStyleBookEntry(layout);
		}

		return styleBookEntry;
	}

	public static String getStyleBookEntryName(
		Layout layout, Locale locale, StyleBookEntry styleBookEntry) {

		if ((styleBookEntry != null) &&
			(styleBookEntry.getStyleBookEntryId() > 0)) {

			return styleBookEntry.getName();
		}

		StyleBookEntry defaultStyleBookEntry = getDefaultMasterStyleBookEntry(
			layout);

		if (defaultStyleBookEntry == null) {
			return LanguageUtil.format(
				locale, "styles-from-x",
				StyleBookUtil.getThemeName(layout, locale));
		}

		StyleBookEntry masterLayoutStyleBookEntry =
			_getMasterLayoutStyleBookEntry(layout);

		if (masterLayoutStyleBookEntry != null) {
			return LanguageUtil.get(locale, "styles-from-master");
		}

		return LanguageUtil.get(locale, "styles-by-default");
	}

	public static boolean isStyleBookEntryApplicable(
		FrontendTokenDefinition frontendTokenDefinition,
		StyleBookEntry styleBookEntry) {

		if ((frontendTokenDefinition == null) || (styleBookEntry == null)) {
			return false;
		}

		return Objects.equals(
			frontendTokenDefinition.getThemeId(), styleBookEntry.getThemeId());
	}

	private static StyleBookEntry _getMasterLayoutStyleBookEntry(
		Layout layout) {

		StyleBookEntry styleBookEntry = null;

		if (layout.getMasterLayoutPlid() > 0) {
			Layout masterLayout = LayoutLocalServiceUtil.fetchLayout(
				layout.getMasterLayoutPlid());

			if (masterLayout != null) {
				styleBookEntry = StyleBookEntryProviderUtil.getStyleBookEntry(
					masterLayout);
			}
		}

		return styleBookEntry;
	}

	private static boolean _isStyleBookEntryApplicable(
		Layout layout, StyleBookEntry styleBookEntry) {

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		return isStyleBookEntryApplicable(
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(layout),
			styleBookEntry);
	}

	private static final Snapshot<FrontendTokenDefinitionRegistry>
		_frontendTokenDefinitionRegistrySnapshot = new Snapshot<>(
			DefaultStyleBookEntryUtil.class,
			FrontendTokenDefinitionRegistry.class);

}