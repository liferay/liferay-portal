/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Lima
 */
public class StyleBookScopedCSSVariablesProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetScopedCSSVariablesCollectionWithNamespacedKeys()
		throws Exception {

		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			JSONUtil.put(
				"clay:primaryColor",
				_createTokenValueJSONObject("--clay-primary", "clay", "#fff")
			).put(
				"theme:primaryColor",
				_createTokenValueJSONObject("--theme-primary", "theme", "#000")
			),
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#000", cssVariables.get("--theme-primary"));
				Assert.assertEquals("#fff", cssVariables.get("--clay-primary"));
			});
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWithPriority()
		throws Exception {

		JSONObject frontendTokensValuesJSONObject = JSONUtil.put(
			"clay:primaryColor",
			_createTokenValueJSONObject("--primary-color", "clay", "#fff")
		).put(
			"custom:secondaryColor",
			_createTokenValueJSONObject(
				"--secondary-color",
				StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID, "#0f0")
		).put(
			"theme:primaryColor",
			_createTokenValueJSONObject("--primary-color", "theme", "#000")
		).put(
			"theme:secondaryColor",
			_createTokenValueJSONObject("--secondary-color", "theme", "#000")
		);

		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			frontendTokensValuesJSONObject,
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#000", cssVariables.get("--primary-color"));
				Assert.assertEquals(
					"#0f0", cssVariables.get("--secondary-color"));
			});
		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			frontendTokensValuesJSONObject,
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#fff", cssVariables.get("--primary-color"));
				Assert.assertEquals(
					"#0f0", cssVariables.get("--secondary-color"));
			});
	}

	private FrontendTokenDefinition _createFrontendTokenDefinition(
		int priority, String themeId) {

		return (FrontendTokenDefinition)ProxyUtil.newProxyInstance(
			FrontendTokenDefinition.class.getClassLoader(),
			new Class<?>[] {FrontendTokenDefinition.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getPriority")) {
					return priority;
				}

				if (Objects.equals(method.getName(), "getThemeId")) {
					return themeId;
				}

				return null;
			});
	}

	private JSONObject _createTokenValueJSONObject(
		String cssVariableMapping, String tokenDefinitionId, String value) {

		return JSONUtil.put(
			"cssVariableMapping", cssVariableMapping
		).put(
			"tokenDefinitionId", tokenDefinitionId
		).put(
			"value", value
		);
	}

	private void _testGetScopedCSSVariablesCollection(
			int clayPriority, JSONObject frontendTokensValuesJSONObject,
			int themePriority,
			UnsafeConsumer<Map<String, String>, Exception> unsafeConsumer)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = (Company)ProxyUtil.newProxyInstance(
			Company.class.getClassLoader(), new Class<?>[] {Company.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getCompanyId")) {
					return 12345L;
				}

				if (Objects.equals(method.getName(), "getGroupId")) {
					return 67890L;
				}

				return null;
			});

		themeDisplay.setCompany(company);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		FrontendTokenDefinitionRegistry mockedRegistry =
			(FrontendTokenDefinitionRegistry)ProxyUtil.newProxyInstance(
				FrontendTokenDefinitionRegistry.class.getClassLoader(),
				new Class<?>[] {FrontendTokenDefinitionRegistry.class},
				(proxy, method, args) -> {
					if (!Objects.equals(
							method.getName(), "getFrontendTokenDefinitions")) {

						return null;
					}

					return ListUtil.fromArray(
						_createFrontendTokenDefinition(clayPriority, "clay"),
						_createFrontendTokenDefinition(themePriority, "theme"));
				});

		StyleBookScopedCSSVariablesProvider
			styleBookScopedCSSVariablesProvider =
				new TestStyleBookScopedCSSVariablesProvider(
					frontendTokensValuesJSONObject.toString());

		ReflectionTestUtil.setFieldValue(
			styleBookScopedCSSVariablesProvider,
			"_frontendTokenDefinitionRegistry", mockedRegistry);

		Collection<ScopedCSSVariables> scopedCSSVariablesCollection =
			styleBookScopedCSSVariablesProvider.getScopedCSSVariablesCollection(
				mockHttpServletRequest);

		Assert.assertEquals(
			scopedCSSVariablesCollection.toString(), 1,
			scopedCSSVariablesCollection.size());

		for (ScopedCSSVariables scopedCSSVariables :
				scopedCSSVariablesCollection) {

			unsafeConsumer.accept(scopedCSSVariables.getCSSVariables());
		}
	}

	private class TestStyleBookScopedCSSVariablesProvider
		extends StyleBookScopedCSSVariablesProvider {

		public TestStyleBookScopedCSSVariablesProvider(
			String frontendTokensValues) {

			_frontendTokensValues = frontendTokensValues;

			ReflectionTestUtil.setFieldValue(
				this, "_jsonFactory", new JSONFactoryImpl());
		}

		@Override
		protected String getFrontendTokensValues(
			HttpServletRequest httpServletRequest) {

			return _frontendTokensValues;
		}

		private final String _frontendTokensValues;

	}

}