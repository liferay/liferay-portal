/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Patrick Yeo
 */
public class DefaultThemeScopedCSSVariablesProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws JSONException {
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		_layout = Mockito.mock(Layout.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			_layout
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			Mockito.mock(FrontendTokenDefinitionRegistry.class);

		_frontendTokenDefinitionJSON = JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.put(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.put(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.put(
								JSONUtil.put(
									"defaultValue", "#FFF"
								).put(
									"mappings",
									JSONUtil.put(
										JSONUtil.put(
											"type", "cssVariable"
										).put(
											"value", "white"
										))
								).put(
									"name", "whiteColor"
								).put(
									"type", "String"
								))
						).put(
							"name", "grays"
						))
				).put(
					"name", "colorSystem"
				))
		).toString();

		JSONFactory jsonFactory = new JSONFactoryImpl();

		FrontendTokenDefinition frontendTokenDefinition =
			new FrontendTokenDefinitionImpl(
				jsonFactory.createJSONObject(_frontendTokenDefinitionJSON),
				jsonFactory, null, "theme_id", RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

		Mockito.when(
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(_layout)
		).thenReturn(
			frontendTokenDefinition
		);

		ReflectionTestUtil.setFieldValue(
			_defaultThemeScopedCSSVariablesProvider,
			"_frontendTokenDefinitionRegistry",
			frontendTokenDefinitionRegistry);
	}

	@Test
	public void testGetScopedCSSVariablesCollection() {
		Collection<ScopedCSSVariables> scopedCSSVariablesCollection =
			_defaultThemeScopedCSSVariablesProvider.
				getScopedCSSVariablesCollection(_httpServletRequest);

		Assert.assertEquals(
			scopedCSSVariablesCollection.toString(), 1,
			scopedCSSVariablesCollection.size());

		Iterator<ScopedCSSVariables> iterator =
			scopedCSSVariablesCollection.iterator();

		ScopedCSSVariables scopedCSSVariables = iterator.next();

		Assert.assertEquals(
			Collections.singletonMap("white", "#FFF"),
			scopedCSSVariables.getCSSVariables());
		Assert.assertEquals(":root", scopedCSSVariables.getScope());
	}

	@Test
	public void testGetScopedCSSVariablesCollectionCachesCSSVariables() {
		Assert.assertSame(
			_getCSSVariables(
				_defaultThemeScopedCSSVariablesProvider.
					getScopedCSSVariablesCollection(_httpServletRequest)),
			_getCSSVariables(
				_defaultThemeScopedCSSVariablesProvider.
					getScopedCSSVariablesCollection(_httpServletRequest)));
	}

	@Test
	public void testGetScopedCSSVariablesCollectionRecomputesCSSVariablesPerFrontendTokenDefinition()
		throws JSONException {

		Object cssVariables = _getCSSVariables(
			_defaultThemeScopedCSSVariablesProvider.
				getScopedCSSVariablesCollection(_httpServletRequest));

		JSONFactory jsonFactory = new JSONFactoryImpl();

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			ReflectionTestUtil.getFieldValue(
				_defaultThemeScopedCSSVariablesProvider,
				"_frontendTokenDefinitionRegistry");

		Mockito.when(
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(_layout)
		).thenReturn(
			new FrontendTokenDefinitionImpl(
				jsonFactory.createJSONObject(_frontendTokenDefinitionJSON),
				jsonFactory, null, "theme_id", RandomTestUtil.randomString(),
				RandomTestUtil.randomString())
		);

		Assert.assertNotSame(
			cssVariables,
			_getCSSVariables(
				_defaultThemeScopedCSSVariablesProvider.
					getScopedCSSVariablesCollection(_httpServletRequest)));
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWhenFrontendTokenDefinitionIsNull() {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		Assert.assertEquals(
			Collections.emptyList(),
			_defaultThemeScopedCSSVariablesProvider.
				getScopedCSSVariablesCollection(_httpServletRequest));
	}

	private Object _getCSSVariables(
		Collection<ScopedCSSVariables> scopedCSSVariablesCollection) {

		Iterator<ScopedCSSVariables> iterator =
			scopedCSSVariablesCollection.iterator();

		ScopedCSSVariables scopedCSSVariables = iterator.next();

		return scopedCSSVariables.getCSSVariables();
	}

	private final DefaultThemeScopedCSSVariablesProvider
		_defaultThemeScopedCSSVariablesProvider =
			new DefaultThemeScopedCSSVariablesProvider();
	private String _frontendTokenDefinitionJSON;
	private HttpServletRequest _httpServletRequest;
	private Layout _layout;

}