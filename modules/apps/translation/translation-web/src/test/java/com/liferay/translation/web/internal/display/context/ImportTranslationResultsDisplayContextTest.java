/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.portal.kernel.redirect.RedirectURLSettingsUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class ImportTranslationResultsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_redirectURLSettingsUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpPortalUtil();
		_setUpRedirectURLSettingsUtil();
	}

	@Test
	@TestInfo("LPD-115188")
	public void testGetRedirect() {
		Assert.assertNull(_getRedirect("javascript:alert(document.domain)"));

		String url = "/myexample1.png?param1=a&param2=b";

		Assert.assertEquals(url, _getRedirect(url));
	}

	private String _getRedirect(String redirect) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("redirect", redirect);

		ImportTranslationResultsDisplayContext
			importTranslationResultsDisplayContext =
				new ImportTranslationResultsDisplayContext(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					Collections.emptyList(), RandomTestUtil.randomString(),
					Collections.emptyList(), RandomTestUtil.randomString(),
					WorkflowConstants.ACTION_PUBLISH,
					Mockito.mock(WorkflowDefinitionLinkLocalService.class));

		return importTranslationResultsDisplayContext.getRedirect(
			mockHttpServletRequest);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	private void _setUpRedirectURLSettingsUtil() {
		_redirectURLSettingsUtilMockedStatic.when(
			() -> RedirectURLSettingsUtil.getAllowedProtocols(Mockito.anyLong())
		).thenReturn(
			new String[] {"http", "https"}
		);
	}

	private static final MockedStatic<RedirectURLSettingsUtil>
		_redirectURLSettingsUtilMockedStatic = Mockito.mockStatic(
			RedirectURLSettingsUtil.class);

}