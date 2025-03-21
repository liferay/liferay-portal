/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ScopedCSSVariablesProvider.class)
public class StyleBookScopedCSSVariablesProvider
	implements ScopedCSSVariablesProvider {

	@Override
	public Collection<ScopedCSSVariables> getScopedCSSVariablesCollection(
		HttpServletRequest httpServletRequest) {

		String frontendTokensValues = _getFrontendTokensValues(
			httpServletRequest);

		if (Validator.isNull(frontendTokensValues)) {
			return Collections.emptyList();
		}

		return Collections.singletonList(
			new ScopedCSSVariables() {

				public Map<String, String> getCSSVariables() {
					Map<String, String> cssVariables = new HashMap<>();

					try {
						JSONObject frontendTokensValuesJSONObject =
							_jsonFactory.createJSONObject(frontendTokensValues);

						Iterator<String> iterator =
							frontendTokensValuesJSONObject.keys();

						while (iterator.hasNext()) {
							String key = iterator.next();

							JSONObject frontendTokenValueJSONObject =
								frontendTokensValuesJSONObject.getJSONObject(
									key);

							cssVariables.put(
								frontendTokenValueJSONObject.getString(
									"cssVariableMapping"),
								frontendTokenValueJSONObject.getString(
									"value"));
						}
					}
					catch (JSONException jsonException) {
						if (_log.isDebugEnabled()) {
							_log.debug("Unable to parse JSON", jsonException);
						}
					}

					return cssVariables;
				}

				public String getScope() {
					return ":root";
				}

			});
	}

	private String _getFrontendTokensValues(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getSiteGroup();
		Layout layout = themeDisplay.getLayout();

		boolean styleBookEntryPreview = ParamUtil.getBoolean(
			httpServletRequest, "styleBookEntryPreview");

		if (group.isControlPanel() || layout.isTypeControlPanel() ||
			styleBookEntryPreview) {

			return StringPool.BLANK;
		}

		StyleBookEntry styleBookEntry =
			DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				themeDisplay.getLayout());

		if (styleBookEntry == null) {
			return StringPool.BLANK;
		}

		return styleBookEntry.getFrontendTokensValues();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookScopedCSSVariablesProvider.class);

	@Reference
	private JSONFactory _jsonFactory;

}