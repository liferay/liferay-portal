/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.Layout;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Petteri Karttunen
 */
public class ContainerLayoutUtilTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testToLayoutWithEmptyJSONObject() {
		Assert.assertNull(ContainerLayoutUtil.toLayout(JSONUtil.put("", "")));
	}

	@Test
	public void testToLayoutWithJSONObjectWithoutLayoutFields() {
		Assert.assertNull(
			ContainerLayoutUtil.toLayout(JSONUtil.put("other", "value")));
	}

	@Test
	public void testToLayoutWithOnlyAlign() {
		Layout layout = ContainerLayoutUtil.toLayout(
			JSONUtil.put("align", "align-items-center"));

		Assert.assertNotNull(layout);
		Assert.assertEquals(Layout.Align.CENTER, layout.getAlign());
	}

	@Test
	public void testToLayoutWithOnlyContentDisplay() {
		Layout layout = ContainerLayoutUtil.toLayout(
			JSONUtil.put("contentDisplay", "flex-row"));

		Assert.assertNotNull(
			"Layout must not be null when contentDisplay is the only set " +
				"field; otherwise the FlexRow master page fragment " +
					"configuration is lost on full Pages export",
			layout);
		Assert.assertEquals(
			Layout.ContentDisplay.FLEX_ROW, layout.getContentDisplay());
	}

	@Test
	public void testToLayoutWithOnlyFlexWrap() {
		Layout layout = ContainerLayoutUtil.toLayout(
			JSONUtil.put("flexWrap", "flex-wrap"));

		Assert.assertNotNull(layout);
		Assert.assertEquals(Layout.FlexWrap.WRAP, layout.getFlexWrap());
	}

	@Test
	public void testToLayoutWithOnlyJustify() {
		Layout layout = ContainerLayoutUtil.toLayout(
			JSONUtil.put("justify", "justify-content-center"));

		Assert.assertNotNull(layout);
		Assert.assertEquals(Layout.Justify.CENTER, layout.getJustify());
	}

	@Test
	public void testToLayoutWithOnlyWidthType() {
		Layout layout = ContainerLayoutUtil.toLayout(
			JSONUtil.put("widthType", "fixed"));

		Assert.assertNotNull(layout);
		Assert.assertEquals(Layout.WidthType.FIXED, layout.getWidthType());
	}

}