/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.portal.kernel.servlet.taglib.PageContextWrapper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Shuyang Zhou
 */
public class PipingServletResponseFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreatePipingServletResponse() {
		PageContext pageContext = new KernelPageContextWrapper();

		Assert.assertSame(
			pageContext.getResponse(),
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));
	}

	private static class KernelPageContextWrapper
		extends MockPageContext implements PageContextWrapper {

		@Override
		public JspWriter getOut() {
			return new MockJspWriter((HttpServletResponse)getResponse()) {

				@Override
				public int getBufferSize() {
					return 0;
				}

			};
		}

	}

}