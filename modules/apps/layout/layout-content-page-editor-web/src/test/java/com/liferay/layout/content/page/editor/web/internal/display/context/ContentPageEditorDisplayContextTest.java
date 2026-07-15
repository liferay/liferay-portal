/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Gabriel Lima
 */
public class ContentPageEditorDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_styleBookEntryProviderUtilMockedStatic.close();
	}

	@Test
	public void testGetStyleBookEntryERC() throws Exception {
		Layout layout = Mockito.mock(Layout.class);

		_styleBookEntryProviderUtilMockedStatic.when(
			() -> StyleBookEntryProviderUtil.getStyleBookEntry(layout)
		).thenReturn(
			Mockito.mock(StyleBookEntry.class)
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			layout
		);

		ContentPageEditorDisplayContext contentPageEditorDisplayContext =
			Mockito.mock(ContentPageEditorDisplayContext.class);

		ReflectionTestUtil.setFieldValue(
			contentPageEditorDisplayContext, "_frontendTokenDefinitionRegistry",
			Mockito.mock(FrontendTokenDefinitionRegistry.class));
		ReflectionTestUtil.setFieldValue(
			contentPageEditorDisplayContext, "themeDisplay", themeDisplay);

		Assert.assertEquals(
			StringPool.BLANK,
			ReflectionTestUtil.invoke(
				contentPageEditorDisplayContext, "_getStyleBookEntryERC",
				new Class<?>[0]));
	}

	private static final MockedStatic<StyleBookEntryProviderUtil>
		_styleBookEntryProviderUtilMockedStatic = Mockito.mockStatic(
			StyleBookEntryProviderUtil.class);

}