/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.servlet;

import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookConstants;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Gabriel Lima
 */
public class LayoutStructureCommonStylesCSSServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetCustomFrontendTokensJSONObject() {
		LayoutStructureCommonStylesCSSServlet
			layoutStructureCommonStylesCSSServlet =
				new LayoutStructureCommonStylesCSSServlet();

		ReflectionTestUtil.setFieldValue(
			layoutStructureCommonStylesCSSServlet, "_jsonFactory",
			new JSONFactoryImpl());

		String cssVariable = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		JSONObject customFrontendTokensJSONObject = ReflectionTestUtil.invoke(
			layoutStructureCommonStylesCSSServlet,
			"_getCustomFrontendTokensJSONObject",
			new Class<?>[] {JSONObject.class},
			JSONUtil.put(
				_getCustomFrontendTokenKey(name),
				JSONUtil.put("cssVariableMapping", cssVariable)
			).put(
				_getCustomFrontendTokenKey(RandomTestUtil.randomString()),
				JSONUtil.put("cssVariableMapping", StringPool.BLANK)
			).put(
				_getCustomFrontendTokenKey(RandomTestUtil.randomString()),
				RandomTestUtil.randomString()
			).put(
				RandomTestUtil.randomString() + StringPool.COLON +
					RandomTestUtil.randomString(),
				JSONUtil.put(
					"cssVariableMapping", RandomTestUtil.randomString())
			));

		Assert.assertEquals(1, customFrontendTokensJSONObject.length());

		JSONObject customFrontendTokenJSONObject =
			customFrontendTokensJSONObject.getJSONObject(name);

		Assert.assertEquals(
			cssVariable,
			customFrontendTokenJSONObject.getString(
				FrontendTokenMapping.TYPE_CSS_VARIABLE));
	}

	private String _getCustomFrontendTokenKey(String name) {
		return StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID +
			StringPool.COLON + name;
	}

}