/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.css.variables.web.internal.servlet.taglib;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class ScopedCSSVariablesTopHeadDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInclude() throws IOException {
		ScopedCSSVariablesTopHeadDynamicInclude
			scopedCSSVariablesTopHeadDynamicInclude =
				new ScopedCSSVariablesTopHeadDynamicInclude();

		ScopedCSSVariablesProvider scopedCSSVariablesProvider = Mockito.mock(
			ScopedCSSVariablesProvider.class);

		Collection<ScopedCSSVariables> scopedCSSVariables = Arrays.asList(
			new ScopedCSSVariables() {

				@Override
				public Map<String, String> getCSSVariables() {
					return Collections.singletonMap("color", "red");
				}

				@Override
				public String getScope() {
					return ":root";
				}

			});

		Mockito.when(
			scopedCSSVariablesProvider.getScopedCSSVariablesCollection(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			scopedCSSVariables
		);

		scopedCSSVariablesTopHeadDynamicInclude.setScopedCSSVariablesProviders(
			Arrays.asList(scopedCSSVariablesProvider));

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		scopedCSSVariablesTopHeadDynamicInclude.include(
			httpServletRequest, bufferCacheServletResponse,
			"/html/common/themes/top_head.jsp#post");

		Assert.assertEquals(
			_read("liferay_css_variables_1.html", true),
			bufferCacheServletResponse.getString());
	}

	@Test
	public void testIncludeWithMultipleProviders() throws IOException {
		ScopedCSSVariablesTopHeadDynamicInclude
			scopedCSSVariablesTopHeadDynamicInclude =
				new ScopedCSSVariablesTopHeadDynamicInclude();

		ScopedCSSVariablesProvider scopedCSSVariablesProvider1 = Mockito.mock(
			ScopedCSSVariablesProvider.class);

		Collection<ScopedCSSVariables> scopedCSSVariables1 = Arrays.asList(
			new ScopedCSSVariables() {

				@Override
				public Map<String, String> getCSSVariables() {
					return HashMapBuilder.put(
						"color", "red"
					).build();
				}

				@Override
				public String getScope() {
					return ":root";
				}

			});

		Mockito.when(
			scopedCSSVariablesProvider1.getScopedCSSVariablesCollection(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			scopedCSSVariables1
		);

		ScopedCSSVariablesProvider scopedCSSVariablesProvider2 = Mockito.mock(
			ScopedCSSVariablesProvider.class);

		Collection<ScopedCSSVariables> scopedCSSVariables2 = Arrays.asList(
			new ScopedCSSVariables() {

				@Override
				public Map<String, String> getCSSVariables() {
					return HashMapBuilder.put(
						"color", "green"
					).put(
						"fixed-font", "\"Lucida Console\""
					).put(
						"font", "Comic Sans"
					).build();
				}

				@Override
				public String getScope() {
					return "body";
				}

			},
			new ScopedCSSVariables() {

				@Override
				public Map<String, String> getCSSVariables() {
					return HashMapBuilder.put(
						"color", "yellow"
					).put(
						"font", "Arial"
					).build();
				}

				@Override
				public String getScope() {
					return ":root";
				}

			});

		Mockito.when(
			scopedCSSVariablesProvider2.getScopedCSSVariablesCollection(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			scopedCSSVariables2
		);

		scopedCSSVariablesTopHeadDynamicInclude.setScopedCSSVariablesProviders(
			Arrays.asList(
				scopedCSSVariablesProvider1, scopedCSSVariablesProvider2));

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		scopedCSSVariablesTopHeadDynamicInclude.include(
			httpServletRequest, bufferCacheServletResponse,
			"/html/common/themes/top_head.jsp#post");

		Assert.assertEquals(
			_read("liferay_css_variables_2.html", true),
			bufferCacheServletResponse.getString());
	}

	@Test
	public void testRegister() {
		ScopedCSSVariablesTopHeadDynamicInclude
			scopedCSSVariablesTopHeadDynamicInclude =
				new ScopedCSSVariablesTopHeadDynamicInclude();

		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry =
			Mockito.mock(DynamicInclude.DynamicIncludeRegistry.class);

		scopedCSSVariablesTopHeadDynamicInclude.register(
			dynamicIncludeRegistry);

		Mockito.verify(
			dynamicIncludeRegistry
		).register(
			"/html/common/themes/top_head.jsp#post"
		);
	}

	private final String _read(String fileName, boolean addNewLine) {
		Class<ScopedCSSVariablesTopHeadDynamicIncludeTest> clazz =
			ScopedCSSVariablesTopHeadDynamicIncludeTest.class;

		try (InputStream inputStream = clazz.getResourceAsStream(fileName)) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			StreamUtil.transfer(inputStream, byteArrayOutputStream);

			String content = byteArrayOutputStream.toString("UTF-8");

			if (addNewLine) {
				content += StringPool.NEW_LINE;
			}

			return content;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

}