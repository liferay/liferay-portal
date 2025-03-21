/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal.display.context;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class AssetListEntryItemSelectorDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetInfoItemClassNames() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Mockito.when(
			(ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		InfoItemServiceRegistry infoItemServiceRegistry = Mockito.mock(
			InfoItemServiceRegistry.class);

		Mockito.when(
			infoItemServiceRegistry.getInfoItemClassNames(
				InfoItemFormProvider.class)
		).thenReturn(
			Arrays.asList("className1", "className2")
		);

		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry =
			Mockito.mock(InfoSearchClassMapperRegistry.class);

		Mockito.when(
			infoSearchClassMapperRegistry.getSearchClassName("className1")
		).thenReturn(
			"searchClassName1"
		);

		Mockito.when(
			infoSearchClassMapperRegistry.getSearchClassName("className2")
		).thenReturn(
			"className2"
		);

		AssetListEntryItemSelectorDisplayContext
			assetListEntryItemSelectorDisplayContext =
				new AssetListEntryItemSelectorDisplayContext(
					httpServletRequest, infoItemServiceRegistry,
					infoSearchClassMapperRegistry, null, null, null);

		String[] infoItemClassNames = ReflectionTestUtil.invoke(
			assetListEntryItemSelectorDisplayContext, "_getInfoItemClassNames",
			new Class<?>[0]);

		Assert.assertEquals(
			Arrays.toString(infoItemClassNames), 3, infoItemClassNames.length);

		Assert.assertTrue(ArrayUtil.contains(infoItemClassNames, "className1"));
		Assert.assertTrue(ArrayUtil.contains(infoItemClassNames, "className2"));
		Assert.assertTrue(
			ArrayUtil.contains(infoItemClassNames, "searchClassName1"));
	}

}