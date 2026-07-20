/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MAX_VALUE,
	service = ScopedCSSVariablesProvider.class
)
public class DefaultThemeScopedCSSVariablesProvider
	implements ScopedCSSVariablesProvider {

	@Override
	public Collection<ScopedCSSVariables> getScopedCSSVariablesCollection(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		FrontendTokenDefinition frontendTokenDefinition = null;

		String styleBookEntryThemeId = ParamUtil.getString(
			httpServletRequest, "styleBookEntryThemeId");

		if (Validator.isNotNull(styleBookEntryThemeId)) {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getCompanyId(), styleBookEntryThemeId);
		}
		else {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getLayout());
		}

		if (frontendTokenDefinition == null) {
			return Collections.emptyList();
		}

		Map<String, String> cssVariables = _cssVariablesMap.computeIfAbsent(
			frontendTokenDefinition, this::_getCSSVariables);

		return Collections.singletonList(
			new ScopedCSSVariables() {

				@Override
				public Map<String, String> getCSSVariables() {
					return cssVariables;
				}

				@Override
				public String getScope() {
					return ":root";
				}

			});
	}

	private Map<String, String> _getCSSVariables(
		FrontendTokenDefinition frontendTokenDefinition) {

		Map<String, String> cssVariables = new HashMap<>();

		for (FrontendToken frontendToken :
				frontendTokenDefinition.getFrontendTokens()) {

			if (frontendToken.<Object>getDefaultValue() == null) {
				continue;
			}

			String defaultValue = String.valueOf(
				frontendToken.<Object>getDefaultValue());

			if (Validator.isNull(defaultValue)) {
				continue;
			}

			Collection<FrontendTokenMapping> frontendTokenMappings =
				frontendToken.getFrontendTokenMappings(
					FrontendTokenMapping.TYPE_CSS_VARIABLE);

			if (frontendTokenMappings == null) {
				continue;
			}

			for (FrontendTokenMapping frontendTokenMapping :
					frontendTokenMappings) {

				cssVariables.put(frontendTokenMapping.getValue(), defaultValue);
			}
		}

		return Collections.unmodifiableMap(cssVariables);
	}

	private final Map<FrontendTokenDefinition, Map<String, String>>
		_cssVariablesMap = new ConcurrentReferenceKeyHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

}