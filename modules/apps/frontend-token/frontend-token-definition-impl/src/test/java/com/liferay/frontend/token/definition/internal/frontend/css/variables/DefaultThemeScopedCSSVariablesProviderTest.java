/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
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
		_layout = Mockito.mock(Layout.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			_layout
		);

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		JSONFactory jsonFactory = new JSONFactoryImpl();

		_frontendTokenDefinition = new FrontendTokenDefinitionImpl(
			jsonFactory.createJSONObject(_FRONTEND_TOKEN_DEFINITION_JSON),
			jsonFactory, null, "theme_id", RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			Mockito.mock(FrontendTokenDefinitionRegistry.class);

		Mockito.when(
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(_layout)
		).thenReturn(
			_frontendTokenDefinition
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

	private static final String _FRONTEND_TOKEN_DEFINITION_JSON =
		StringBundler.concat(
			"{\"frontendTokenCategories\": [{\"frontendTokenSets\": ",
			"[{\"frontendTokens\": [{\"defaultValue\": \"#FFF\", ",
			"\"mappings\": [{\"type\": \"cssVariable\", \"value\": ",
			"\"white\"}], \"name\": \"whiteColor\", \"type\": \"String\"}], ",
			"\"name\": \"grays\"}], \"name\": \"colorSystem\"}]}");

	private final DefaultThemeScopedCSSVariablesProvider
		_defaultThemeScopedCSSVariablesProvider =
			new DefaultThemeScopedCSSVariablesProvider();
	private FrontendTokenDefinition _frontendTokenDefinition;
	private HttpServletRequest _httpServletRequest;
	private Layout _layout;

}