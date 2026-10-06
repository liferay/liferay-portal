/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.display.context.util;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Iván Zaera Avellón
 */
public class CETLabelUtil {

	public static String getAddLabel(Locale locale, String type) {
		return LanguageUtil.format(
			locale, "add-x", _getCETTypeLanguageKey(type));
	}

	public static String getHelpLabel(Locale locale, String type) {
		return LanguageUtil.get(locale, "help-" + _getCETTypeLanguageKey(type));
	}

	public static String getLearnResourceKey(String type) {
		return "learn-" + _getCETTypeLanguageKey(type);
	}

	public static String getNewLabel(Locale locale, String type) {
		return LanguageUtil.format(
			locale, "new-x", _getCETTypeLanguageKey(type));
	}

	public static String getTypeLabel(Locale locale, String type) {
		return LanguageUtil.get(locale, _getCETTypeLanguageKey(type));
	}

	private static String _getCETTypeLanguageKey(String type) {
		if (Objects.equals(
				type,
				ClientExtensionEntryConstants.
					TYPE_AUDIENCES_CUSTOM_ATTRIBUTES)) {

			return "audiences-custom-attributes";
		}
		else if (Objects.equals(
					type,
					ClientExtensionEntryConstants.
						TYPE_COMMERCE_CHECKOUT_STEP)) {

			return "commerce-checkout-step";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT)) {

			return "custom-element";
		}
		else if (Objects.equals(
					type,
					ClientExtensionEntryConstants.
						TYPE_EDITOR_CONFIG_CONTRIBUTOR)) {

			return "editor-config-contributor";
		}
		else if (Objects.equals(
					type,
					ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER)) {

			return "fds-cell-renderer";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_FDS_FILTER)) {

			return "fds-filter";
		}
		else if (Objects.equals(
					type,
					ClientExtensionEntryConstants.
						TYPE_FDS_VISUALIZATION_MODE)) {

			return "fds-visualization-mode";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_GLOBAL_CSS)) {

			return "css";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_GLOBAL_JS)) {

			return "js";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_IFRAME)) {

			return "iframe";
		}
		else if (Objects.equals(
					type,
					ClientExtensionEntryConstants.TYPE_JS_IMPORT_MAPS_ENTRY)) {

			return "js-import-maps-entry";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_STATIC_CONTENT)) {

			return "static-content";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_CSS)) {

			return "theme-css";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_FAVICON)) {

			return "theme-favicon";
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP)) {

			return "theme-svg";
		}

		return type;
	}

}