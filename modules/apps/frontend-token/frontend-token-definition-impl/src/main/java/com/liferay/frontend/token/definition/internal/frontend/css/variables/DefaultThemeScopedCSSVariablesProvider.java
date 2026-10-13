/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
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

		Map<String, String> cssVariables =
			frontendTokenDefinition.getCSSVariables();

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

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

}