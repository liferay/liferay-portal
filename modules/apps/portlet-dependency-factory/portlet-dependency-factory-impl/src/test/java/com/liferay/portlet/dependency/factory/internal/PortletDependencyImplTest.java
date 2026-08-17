/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.dependency.factory.internal;

import com.liferay.petra.string.StringBundler;
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

		String cssURL = RandomTestUtil.randomString();

		_testToStringBundler(
			absolutePortalURLBuilder,
			"<link href=\"" + cssURL + "\" type=\"text/css\">",
			RandomTestUtil.randomString() + ".css", cssURL);

		String javaScriptURL = RandomTestUtil.randomString();

		_testToStringBundler(
			absolutePortalURLBuilder,
			"<script src=\"" + javaScriptURL +
				"\" type=\"text/javascript\"></script>",
			RandomTestUtil.randomString() + ".js", javaScriptURL);

		_testToStringBundlerWithMarkup(absolutePortalURLBuilder);
		_testToStringBundlerWithUnknownType(absolutePortalURLBuilder);
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

	private void _testToStringBundler(
		AbsolutePortalURLBuilder absolutePortalURLBuilder,
		String expectedMarkup, String fileName, String url) {

		PortletDependencyImpl portletDependencyImpl = new PortletDependencyImpl(
			fileName, null, null, null, absolutePortalURLBuilder);

		_setUpAbsolutePortalURLBuilder(
			absolutePortalURLBuilder, portletDependencyImpl, url);

		Assert.assertEquals(
			expectedMarkup,
			String.valueOf(portletDependencyImpl.toStringBundler()));
	}

	private void _testToStringBundlerWithMarkup(
		AbsolutePortalURLBuilder absolutePortalURLBuilder) {

		String markup = RandomTestUtil.randomString();

		PortletDependencyImpl portletDependencyImpl = new PortletDependencyImpl(
			null, null, null, markup, absolutePortalURLBuilder);

		Assert.assertEquals(
			markup, String.valueOf(portletDependencyImpl.toStringBundler()));
	}

	private void _testToStringBundlerWithUnknownType(
		AbsolutePortalURLBuilder absolutePortalURLBuilder) {

		String name = RandomTestUtil.randomString();
		String scope = RandomTestUtil.randomString();
		String version = RandomTestUtil.randomString();

		PortletDependencyImpl portletDependencyImpl = new PortletDependencyImpl(
			name, scope, version, null, absolutePortalURLBuilder);

		Assert.assertEquals(
			StringBundler.concat(
				"<!-- Unknown portlet resource dependency type name=\"", name,
				"\" scope=\"", scope, "\" version=\"", version, "\" -->"),
			String.valueOf(portletDependencyImpl.toStringBundler()));
	}

}