/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.redirect.RedirectURLSettingsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

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
public class ExportTranslationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_classNameLocalServiceUtilMockedStatic.close();
		_redirectURLSettingsUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpClassNameLocalServiceUtil();
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

		ExportTranslationDisplayContext exportTranslationDisplayContext =
			new ExportTranslationDisplayContext(
				RandomTestUtil.randomLong(),
				new long[] {RandomTestUtil.randomLong()},
				RandomTestUtil.randomLong(), mockHttpServletRequest,
				Mockito.mock(InfoItemServiceRegistry.class),
				Mockito.mock(LiferayPortletRequest.class),
				Mockito.mock(LiferayPortletResponse.class),
				Collections.emptyList(), RandomTestUtil.randomString(),
				Mockito.mock(
					TranslationInfoItemFieldValuesExporterRegistry.class));

		return exportTranslationDisplayContext.getRedirect();
	}

	private void _setUpClassNameLocalServiceUtil() {
		_classNameLocalServiceUtilMockedStatic.when(
			() -> ClassNameLocalServiceUtil.getClassName(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ClassName.class)
		);
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

	private static final MockedStatic<ClassNameLocalServiceUtil>
		_classNameLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ClassNameLocalServiceUtil.class);
	private static final MockedStatic<RedirectURLSettingsUtil>
		_redirectURLSettingsUtilMockedStatic = Mockito.mockStatic(
			RedirectURLSettingsUtil.class);

}