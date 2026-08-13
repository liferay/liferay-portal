/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.aui.web.internal.servlet.taglib;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.WebContextStylesheetAbsolutePortalURLBuilder;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Georgel Pop
 */
public class AUITopHeadCSSDynamicIncludeTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-102542")
	public void testInclude() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		AUITopHeadCSSDynamicInclude auiTopHeadCSSDynamicInclude =
			new AUITopHeadCSSDynamicInclude();

		String href = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			auiTopHeadCSSDynamicInclude, "_absolutePortalURLBuilderFactory",
			_getAbsolutePortalURLBuilderFactory(href, mockHttpServletRequest));

		auiTopHeadCSSDynamicInclude.include(
			mockHttpServletRequest, mockHttpServletResponse,
			"/html/common/themes/top_head.jsp#post");

		Assert.assertEquals(
			"<link data-senna-track=\"permanent\" href=\"" + href +
				"\" rel=\"stylesheet\">\n",
			mockHttpServletResponse.getContentAsString());
	}

	private AbsolutePortalURLBuilderFactory _getAbsolutePortalURLBuilderFactory(
		String href, MockHttpServletRequest mockHttpServletRequest) {

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			Mockito.mock(AbsolutePortalURLBuilderFactory.class);

		WebContextStylesheetAbsolutePortalURLBuilder
			webContextStylesheetAbsolutePortalURLBuilder = Mockito.mock(
				WebContextStylesheetAbsolutePortalURLBuilder.class);

		Mockito.when(
			webContextStylesheetAbsolutePortalURLBuilder.build()
		).thenReturn(
			href
		);

		AbsolutePortalURLBuilder absolutePortalURLBuilder = Mockito.mock(
			AbsolutePortalURLBuilder.class);

		Mockito.when(
			absolutePortalURLBuilder.forWebContextStylesheet(
				"frontend-js-aui-web", "/alloy_ui.css")
		).thenReturn(
			webContextStylesheetAbsolutePortalURLBuilder
		);

		Mockito.when(
			absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				mockHttpServletRequest)
		).thenReturn(
			absolutePortalURLBuilder
		);

		return absolutePortalURLBuilderFactory;
	}

}