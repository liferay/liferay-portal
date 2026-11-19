/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.servlet.taglib;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.jsp.tagext.Tag;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Tejesh Boggavarapu
 */
public class CaptchaTagTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-115999")
	public void testDoEndTag() throws Exception {
		_testDoEndTag(false, Mockito.times(1));
		_testDoEndTag(true, Mockito.never());
	}

	private void _testDoEndTag(
			boolean renderLayoutContent, VerificationMode verificationMode)
		throws Exception {

		boolean originalRenderLayoutContent =
			RenderLayoutContentThreadLocal.isRenderLayoutContent();

		try (MockedStatic<CaptchaUtil> captchaUtilMockedStatic =
				Mockito.mockStatic(CaptchaUtil.class)) {

			Captcha captcha = Mockito.mock(Captcha.class);

			captchaUtilMockedStatic.when(
				CaptchaUtil::getCaptcha
			).thenReturn(
				captcha
			);

			RenderLayoutContentThreadLocal.setRenderLayoutContent(
				renderLayoutContent);

			CaptchaTag captchaTag = new CaptchaTag();

			captchaTag.setPageContext(
				new MockPageContext(
					new MockServletContext(), new MockHttpServletRequest()));
			captchaTag.setUrl(RandomTestUtil.randomString());

			Assert.assertEquals(Tag.EVAL_PAGE, captchaTag.doEndTag());

			captchaUtilMockedStatic.verify(
				CaptchaUtil::getCaptcha, verificationMode);

			Mockito.verify(
				captcha, verificationMode
			).render(
				Mockito.any(), Mockito.any()
			);
		}
		finally {
			RenderLayoutContentThreadLocal.setRenderLayoutContent(
				originalRenderLayoutContent);
		}
	}

}