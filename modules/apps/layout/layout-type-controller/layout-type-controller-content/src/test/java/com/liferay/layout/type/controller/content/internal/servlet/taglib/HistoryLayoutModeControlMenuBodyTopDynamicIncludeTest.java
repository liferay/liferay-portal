/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.servlet.taglib;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.taglib.servlet.taglib.ProductNavigationControlMenuTag;
import com.liferay.taglib.TagSupport;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Lourdes Fernández Besada
 */
public class HistoryLayoutModeControlMenuBodyTopDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-103339")
	public void testInclude() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_l_mode", RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		HistoryLayoutModeControlMenuBodyTopDynamicInclude
			historyLayoutModeControlMenuBodyTopDynamicInclude =
				new HistoryLayoutModeControlMenuBodyTopDynamicInclude();

		MockPageContext mockPageContext = new MockPageContext();

		try (MockedStatic<PageContextFactoryUtil>
				pageContextFactoryUtilMockedStatic =
					_getPageContextFactoryUtilMockedStatic(
						mockHttpServletRequest, mockHttpServletResponse,
						mockPageContext);
			MockedConstruction<ProductNavigationControlMenuTag>
				productNavigationControlMenuTagMockedConstruction =
					_getProductNavigationControlMenuTagMockedConstruction()) {

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			mockHttpServletRequest.setParameter("p_l_mode", Constants.HISTORY);

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			pageContextFactoryUtilMockedStatic.verifyNoInteractions();

			Assert.assertTrue(
				ListUtil.isEmpty(
					productNavigationControlMenuTagMockedConstruction.
						constructed()));

			ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			pageContextFactoryUtilMockedStatic.verifyNoInteractions();

			Assert.assertTrue(
				ListUtil.isEmpty(
					productNavigationControlMenuTagMockedConstruction.
						constructed()));

			Layout layout = Mockito.mock(Layout.class);

			Mockito.when(
				themeDisplay.getLayout()
			).thenReturn(
				layout
			);

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			pageContextFactoryUtilMockedStatic.verifyNoInteractions();

			Assert.assertTrue(
				ListUtil.isEmpty(
					productNavigationControlMenuTagMockedConstruction.
						constructed()));

			Mockito.when(
				layout.isDraftLayout()
			).thenReturn(
				true
			);

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			pageContextFactoryUtilMockedStatic.verifyNoInteractions();

			Assert.assertTrue(
				ListUtil.isEmpty(
					productNavigationControlMenuTagMockedConstruction.
						constructed()));

			Mockito.when(
				layout.isTypeContent()
			).thenReturn(
				true
			);

			historyLayoutModeControlMenuBodyTopDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());

			pageContextFactoryUtilMockedStatic.verify(
				() -> PageContextFactoryUtil.create(
					mockHttpServletRequest, mockHttpServletResponse));

			List<ProductNavigationControlMenuTag>
				productNavigationControlMenuTags =
					productNavigationControlMenuTagMockedConstruction.
						constructed();

			ProductNavigationControlMenuTag productNavigationControlMenuTag =
				productNavigationControlMenuTags.get(0);

			Mockito.verify(
				productNavigationControlMenuTag
			).setPageContext(
				mockPageContext
			);

			Mockito.verify(
				productNavigationControlMenuTag
			).doStartTag();

			Mockito.verify(
				productNavigationControlMenuTag
			).doEndTag();

			Assert.assertEquals(
				productNavigationControlMenuTags.toString(), 1,
				productNavigationControlMenuTags.size());
		}
	}

	@Test
	@TestInfo("LPD-103339")
	public void testRegister() {
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry =
			Mockito.mock(DynamicInclude.DynamicIncludeRegistry.class);

		HistoryLayoutModeControlMenuBodyTopDynamicInclude
			historyLayoutModeControlMenuBodyTopDynamicInclude =
				new HistoryLayoutModeControlMenuBodyTopDynamicInclude();

		historyLayoutModeControlMenuBodyTopDynamicInclude.register(
			dynamicIncludeRegistry);

		Mockito.verify(
			dynamicIncludeRegistry
		).register(
			"/html/common/themes/body_top.jsp#post"
		);
	}

	private MockedStatic<PageContextFactoryUtil>
		_getPageContextFactoryUtilMockedStatic(
			MockHttpServletRequest mockHttpServletRequest,
			MockHttpServletResponse mockHttpServletResponse,
			MockPageContext mockPageContext) {

		MockedStatic<PageContextFactoryUtil>
			pageContextFactoryUtilMockedStatic = Mockito.mockStatic(
				PageContextFactoryUtil.class);

		pageContextFactoryUtilMockedStatic.when(
			() -> PageContextFactoryUtil.create(
				mockHttpServletRequest, mockHttpServletResponse)
		).thenReturn(
			mockPageContext
		);

		return pageContextFactoryUtilMockedStatic;
	}

	private MockedConstruction<ProductNavigationControlMenuTag>
		_getProductNavigationControlMenuTagMockedConstruction() {

		return Mockito.mockConstruction(
			ProductNavigationControlMenuTag.class,
			(productNavigationControlMenuTag, context) -> Mockito.when(
				productNavigationControlMenuTag.doStartTag()
			).thenReturn(
				TagSupport.EVAL_BODY_INCLUDE
			));
	}

}