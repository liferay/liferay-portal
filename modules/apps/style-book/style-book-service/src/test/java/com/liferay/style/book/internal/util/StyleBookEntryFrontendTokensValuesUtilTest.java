/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testNormalizeFrontendTokensValues() throws Exception {
		String themeId = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringPool.BLANK,
			StyleBookEntryFrontendTokensValuesUtil.
				normalizeFrontendTokensValues(StringPool.BLANK, themeId));
		Assert.assertNull(
			StyleBookEntryFrontendTokensValuesUtil.
				normalizeFrontendTokensValues(null, themeId));

		String frontendTokensValues = JSONUtil.put(
			themeId + ":token1",
			JSONUtil.put(
				"cssVariableMapping", "token-1"
			).put(
				"name", themeId + ":token2"
			).put(
				"tokenDefinitionId", themeId
			).put(
				"value", "#000"
			)
		).put(
			"custom:token3",
			JSONUtil.put(
				"cssVariableMapping", "token-3"
			).put(
				"tokenDefinitionId", "custom"
			).put(
				"value", "#fff"
			)
		).toString();

		Assert.assertEquals(
			frontendTokensValues,
			StyleBookEntryFrontendTokensValuesUtil.
				normalizeFrontendTokensValues(frontendTokensValues, themeId));

		JSONAssert.assertEquals(
			JSONUtil.put(
				themeId + ":token1",
				JSONUtil.put(
					"cssVariableMapping", "token-1"
				).put(
					"name", themeId + ":token2"
				).put(
					"tokenDefinitionId", themeId
				).put(
					"value", "#fff"
				)
			).toString(),
			StyleBookEntryFrontendTokensValuesUtil.
				normalizeFrontendTokensValues(
					JSONUtil.put(
						themeId + ":token1",
						JSONUtil.put(
							"cssVariableMapping", "token-1"
						).put(
							"name", "token2"
						).put(
							"tokenDefinitionId", themeId
						).put(
							"value", "#fff"
						)
					).put(
						"token1",
						JSONUtil.put(
							"cssVariableMapping", "token-1"
						).put(
							"value", "#000"
						)
					).toString(),
					themeId),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				themeId + ":token1",
				JSONUtil.put(
					"cssVariableMapping", "token-1"
				).put(
					"name", themeId + ":token2"
				).put(
					"tokenDefinitionId", themeId
				).put(
					"value", "var(--token-2)"
				)
			).put(
				themeId + ":token2",
				JSONUtil.put(
					"cssVariableMapping", "token-2"
				).put(
					"tokenDefinitionId", themeId
				).put(
					"value", "#000"
				)
			).toString(),
			StyleBookEntryFrontendTokensValuesUtil.
				normalizeFrontendTokensValues(
					JSONUtil.put(
						"token1",
						JSONUtil.put(
							"cssVariableMapping", "token-1"
						).put(
							"name", "token2"
						).put(
							"value", "var(--token-2)"
						)
					).put(
						"token2",
						JSONUtil.put(
							"cssVariableMapping", "token-2"
						).put(
							"value", "#000"
						)
					).toString(),
					themeId),
			JSONCompareMode.STRICT);
	}

}