/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.dependency.factory.internal;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.PortletDependencyAbsolutePortalURLBuilder;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class PortletDependencyImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-102542")
	public void testToStringBundler() {
		AbsolutePortalURLBuilder absolutePortalURLBuilder = Mockito.mock(
			AbsolutePortalURLBuilder.class);

		PortletDependencyImpl portletDependencyImpl = new PortletDependencyImpl(
			"main.css", null, null, null, absolutePortalURLBuilder);

		String cssURL = RandomTestUtil.randomString();

		_setUpAbsolutePortalURLBuilder(
			absolutePortalURLBuilder, portletDependencyImpl, cssURL);

		Assert.assertEquals(
			"<link href=\"" + cssURL + "\" type=\"text/css\">",
			String.valueOf(portletDependencyImpl.toStringBundler()));

		portletDependencyImpl = new PortletDependencyImpl(
			"main.js", null, null, null, absolutePortalURLBuilder);

		String javaScriptURL = RandomTestUtil.randomString();

		_setUpAbsolutePortalURLBuilder(
			absolutePortalURLBuilder, portletDependencyImpl, javaScriptURL);

		Assert.assertEquals(
			"<script src=\"" + javaScriptURL +
				"\" type=\"text/javascript\"></script>",
			String.valueOf(portletDependencyImpl.toStringBundler()));
	}

	private void _setUpAbsolutePortalURLBuilder(
		AbsolutePortalURLBuilder absolutePortalURLBuilder,
		PortletDependencyImpl portletDependencyImpl, String url) {

		PortletDependencyAbsolutePortalURLBuilder
			portletDependencyAbsolutePortalURLBuilder = Mockito.mock(
				PortletDependencyAbsolutePortalURLBuilder.class);

		Mockito.when(
			portletDependencyAbsolutePortalURLBuilder.build()
		).thenReturn(
			url
		);

		Mockito.when(
			absolutePortalURLBuilder.forPortletDependency(
				portletDependencyImpl, PropsValues.PORTLET_DEPENDENCY_CSS_URN,
				PropsValues.PORTLET_DEPENDENCY_JAVASCRIPT_URN)
		).thenReturn(
			portletDependencyAbsolutePortalURLBuilder
		);
	}

}