/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thiago Buarque
 */
public class FrontendTokensValuesUtilTest {

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
	public void testGetFrontendTokenValue() {
		JSONObject frontendTokensValuesJSONObject = JSONUtil.put(
			"theme:a", _createFrontendTokenValueJSONObject("theme:b", "#111")
		).put(
			"theme:b",
			_createFrontendTokenValueJSONObject("theme:c", "var(--c)")
		).put(
			"theme:c", _createFrontendTokenValueJSONObject(null, "#333")
		);

		Assert.assertEquals(
			"#333",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:a"));
		Assert.assertEquals(
			"#333",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:b"));
		Assert.assertEquals(
			"#333",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:c"));
	}

	@Test
	public void testGetFrontendTokenValueWhenDefaultFrontendTokenValuesIsNull() {
		Assert.assertEquals(
			"var(--b)",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				null,
				JSONUtil.put(
					"theme:a",
					_createFrontendTokenValueJSONObject("theme:b", "var(--b)")),
				"theme:a"));
		Assert.assertNull(
			FrontendTokensValuesUtil.getFrontendTokenValue(
				null, JSONUtil.put("theme:b", "#111"), "theme:a"));
	}

	@Test
	public void testGetFrontendTokenValueWhenFrontendTokenValueJSONObjectIsNull() {
		Assert.assertEquals(
			"#222",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.singletonMap("theme:a", "#222"),
				JSONUtil.put("theme:b", "#111"), "theme:a"));
		Assert.assertNull(
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), JSONUtil.put("theme:b", "#111"),
				"theme:a"));
		Assert.assertNull(
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), JSONUtil.put("theme:a", "#111"),
				"theme:a"));
	}

	@Test
	public void testGetFrontendTokenValueWhenFrontendTokensValuesJSONObjectIsNull() {
		Assert.assertEquals(
			"#222",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.singletonMap("theme:a", "#222"), null, "theme:a"));
		Assert.assertNull(
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), null, "theme:a"));
	}

	@Test
	public void testGetFrontendTokenValueWhenLinkIsCircular() {
		JSONObject frontendTokensValuesJSONObject = JSONUtil.put(
			"theme:a", _createFrontendTokenValueJSONObject("theme:b", "#111")
		).put(
			"theme:b", _createFrontendTokenValueJSONObject("theme:a", "#222")
		);

		Assert.assertEquals(
			"#222",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:a"));
		Assert.assertEquals(
			"#111",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:b"));
	}

	@Test
	public void testGetFrontendTokenValueWhenLinkedFrontendTokenValueJSONObjectIsNull() {
		JSONObject frontendTokensValuesJSONObject = JSONUtil.put(
			"theme:a",
			_createFrontendTokenValueJSONObject("theme:b", "var(--b)"));

		Assert.assertEquals(
			"#222",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.singletonMap("theme:b", "#222"),
				frontendTokensValuesJSONObject, "theme:a"));
		Assert.assertEquals(
			"var(--b)",
			FrontendTokensValuesUtil.getFrontendTokenValue(
				Collections.emptyMap(), frontendTokensValuesJSONObject,
				"theme:a"));
	}

	@Test
	public void testGetFrontendTokensValuesKey() {
		Assert.assertEquals(
			"theme:a",
			FrontendTokensValuesUtil.getFrontendTokensValuesKey("theme", "a"));
	}

	private JSONObject _createFrontendTokenValueJSONObject(
		String name, String value) {

		return JSONUtil.put(
			"name", name
		).put(
			"value", value
		);
	}

}