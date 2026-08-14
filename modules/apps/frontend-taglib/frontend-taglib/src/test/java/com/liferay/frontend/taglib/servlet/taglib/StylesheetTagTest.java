/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.util.ServicesProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.WebContextStylesheetAbsolutePortalURLBuilder;

import jakarta.servlet.jsp.tagext.Tag;

import java.util.Collections;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Georgel Pop
 */
public class StylesheetTagTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_servicesProviderMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-102542")
	public void testDoEndTag() throws Exception {
		StylesheetTag stylesheetTag = new StylesheetTag();

		String bundleSymbolicName = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String href = RandomTestUtil.randomString();
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_setUpServicesProvider(
			bundleSymbolicName, css, href, mockHttpServletRequest);

		stylesheetTag.setBundle(bundleSymbolicName);
		stylesheetTag.setCss(css);
		stylesheetTag.setPageContext(
			new MockPageContext(
				new MockServletContext(), mockHttpServletRequest));

		Assert.assertEquals(Tag.EVAL_PAGE, stylesheetTag.doEndTag());

		OutputData outputData = (OutputData)mockHttpServletRequest.getAttribute(
			WebKeys.OUTPUT_DATA);

		Assert.assertEquals(
			"<link href=\"" + href + "\" rel=\"stylesheet\" type=\"text/css\">",
			String.valueOf(
				outputData.getDataSB(
					bundleSymbolicName + "/" + css, WebKeys.PAGE_TOP)));
	}

	private void _setUpServicesProvider(
		String bundleSymbolicName, String css, String href,
		MockHttpServletRequest mockHttpServletRequest) {

		Bundle bundle = Mockito.mock(Bundle.class);

		String webContextPath = RandomTestUtil.randomString();

		Mockito.when(
			bundle.getHeaders(StringPool.BLANK)
		).thenReturn(
			new Hashtable<>(
				Collections.singletonMap("Web-ContextPath", webContextPath))
		);

		_servicesProviderMockedStatic.when(
			ServicesProvider::getBundleMap
		).thenReturn(
			Collections.singletonMap(bundleSymbolicName, bundle)
		);

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
				webContextPath, css)
		).thenReturn(
			webContextStylesheetAbsolutePortalURLBuilder
		);

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			Mockito.mock(AbsolutePortalURLBuilderFactory.class);

		Mockito.when(
			absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				mockHttpServletRequest)
		).thenReturn(
			absolutePortalURLBuilder
		);

		_servicesProviderMockedStatic.when(
			ServicesProvider::getAbsolutePortalURLBuilderFactory
		).thenReturn(
			absolutePortalURLBuilderFactory
		);
	}

	private final MockedStatic<ServicesProvider> _servicesProviderMockedStatic =
		Mockito.mockStatic(ServicesProvider.class);

}