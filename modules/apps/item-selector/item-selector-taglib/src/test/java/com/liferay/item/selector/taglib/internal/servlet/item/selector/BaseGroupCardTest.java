/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet.item.selector;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
public class BaseGroupCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-109452")
	public void testGetImageAlt() throws Exception {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Q\" data-xss=\"1"
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		TestGroupCard testGroupCard = new TestGroupCard(
			group, mockHttpServletRequest);

		Assert.assertEquals(
			"Q&quot; data-xss=&quot;1", testGroupCard.getImageAlt());
	}

	private static class TestGroupCard extends BaseGroupCard {

		private TestGroupCard(
			Group group, HttpServletRequest httpServletRequest) {

			super(group, null, httpServletRequest);
		}

	}

}