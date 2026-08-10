/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

		String frontendTokensValues = getFrontendTokensValues(
			httpServletRequest);

		if (Validator.isNull(frontendTokensValues)) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return Collections.singletonList(
			new ScopedCSSVariables() {

				public Map<String, String> getCSSVariables() {
					Map<String, String> cssVariables = new HashMap<>();

					try {
						JSONObject frontendTokensValuesJSONObject =
							_jsonFactory.createJSONObject(frontendTokensValues);

						List<String> sortedKeys = _getSortedKeys(
							themeDisplay.getCompanyId(),
							frontendTokensValuesJSONObject);

						for (String key : sortedKeys) {
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

	protected String getFrontendTokensValues(
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

	private List<String> _getSortedKeys(long companyId, JSONObject jsonObject) {
		Map<String, Integer> tokenDefinitionPriorities = new HashMap<>();

		for (FrontendTokenDefinition frontendTokenDefinition :
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					companyId)) {

			tokenDefinitionPriorities.put(
				frontendTokenDefinition.getThemeId(),
				frontendTokenDefinition.getPriority());
		}

		List<String> keys = ListUtil.fromCollection(jsonObject.keySet());

		Map<String, Integer> priorities = new HashMap<>(keys.size());

		for (String key : keys) {
			JSONObject tokenValueJSONObject = jsonObject.getJSONObject(key);

			String tokenDefinitionId = tokenValueJSONObject.getString(
				"tokenDefinitionId");

			if (Objects.equals(
					tokenDefinitionId,
					StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID)) {

				priorities.put(
					key, FrontendTokenDefinitionConstants.PRIORITY_CUSTOM);

				continue;
			}

			priorities.put(
				key,
				tokenDefinitionPriorities.getOrDefault(
					tokenDefinitionId,
					FrontendTokenDefinitionConstants.PRIORITY_LEGACY));
		}

		return ListUtil.sort(
			keys,
			(key1, key2) -> Integer.compare(
				priorities.get(key1), priorities.get(key2)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookScopedCSSVariablesProvider.class);

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}