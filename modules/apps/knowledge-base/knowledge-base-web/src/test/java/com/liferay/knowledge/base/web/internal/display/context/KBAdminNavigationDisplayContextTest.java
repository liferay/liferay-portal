/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class KBAdminNavigationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsProductMenuOpen() {
		KBAdminNavigationDisplayContext kbAdminNavigationDisplayContext =
			Mockito.mock(
				KBAdminNavigationDisplayContext.class,
				Mockito.CALLS_REAL_METHODS);
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ReflectionTestUtil.setFieldValue(
			kbAdminNavigationDisplayContext, "_httpServletRequest",
			httpServletRequest);

		try (MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					httpServletRequest,
					"com.liferay.application.list_sidebarState", "closed")
			).thenReturn(
				"open"
			);

			Assert.assertTrue(
				kbAdminNavigationDisplayContext.isProductMenuOpen());

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					httpServletRequest,
					"com.liferay.application.list_sidebarState", "closed")
			).thenReturn(
				"closed"
			);

			Assert.assertFalse(
				kbAdminNavigationDisplayContext.isProductMenuOpen());
		}
	}

}