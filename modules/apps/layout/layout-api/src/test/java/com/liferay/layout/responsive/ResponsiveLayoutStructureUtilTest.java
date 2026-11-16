/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.responsive;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ResponsiveLayoutStructureUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testGetResponsivePropertyValue() {
		Map<String, JSONObject> viewportConfigurationJSONObjects =
			HashMapBuilder.<String, JSONObject>put(
				ViewportSize.DESKTOP.getViewportSizeId(),
				JSONUtil.put(
					"align", "desktop"
				).put(
					"size", "desktop"
				)
			).put(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
				JSONUtil.put("size", "landscape")
			).put(
				ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
				JSONUtil.put("align", "portrait")
			).build();

		Assert.assertEquals(
			"desktop",
			ResponsiveLayoutStructureUtil.getResponsivePropertyValue(
				ViewportSize.MOBILE_LANDSCAPE, viewportConfigurationJSONObjects,
				"align", "default"));
		Assert.assertEquals(
			"portrait",
			ResponsiveLayoutStructureUtil.getResponsivePropertyValue(
				ViewportSize.PORTRAIT_MOBILE, viewportConfigurationJSONObjects,
				"align", "default"));
		Assert.assertEquals(
			"default",
			ResponsiveLayoutStructureUtil.getResponsivePropertyValue(
				ViewportSize.PORTRAIT_MOBILE, viewportConfigurationJSONObjects,
				"color", "default"));
		Assert.assertEquals(
			"landscape",
			ResponsiveLayoutStructureUtil.getResponsivePropertyValue(
				ViewportSize.PORTRAIT_MOBILE, viewportConfigurationJSONObjects,
				"size", "default"));
		Assert.assertEquals(
			"desktop",
			ResponsiveLayoutStructureUtil.getResponsivePropertyValue(
				ViewportSize.TABLET, viewportConfigurationJSONObjects, "size",
				"default"));
	}

}