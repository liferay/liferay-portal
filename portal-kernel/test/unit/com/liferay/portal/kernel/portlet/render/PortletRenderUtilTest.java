/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.render;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class PortletRenderUtilTest {

	@After
	public void tearDown() {
		_htmlUtilMockedStatic.close();

		_portalUtilMockedStatic.close();
	}

	@Test
	public void testGetPortletRenderParts() {
		_setUpMocks(false, "");

		String portletHTML = "<div>Hola</div>";

		PortletRenderParts portletRenderParts =
			PortletRenderUtil.getPortletRenderParts(
				_httpServletRequest, portletHTML, _portlet);

		_assertEquals(
			Arrays.asList(
				"/header-portal.css?t=7", "/nocombo-header-portal.css?t=7",
				"/o/portlet-web/header-portlet.css?t=7",
				"/o/portlet-web/nocombo-header-portlet.css?t=7",
				"http://example.com/header-portal.css",
				"http://example.com/header-portlet.css"),
			portletRenderParts.getHeaderCssPaths());
		_assertEquals(
			Arrays.asList(
				"/header-portal.js?t=7", "/nocombo-header-portal.js?t=7",
				"/o/portlet-web/header-portlet.js?t=7",
				"/o/portlet-web/nocombo-header-portlet.js?t=7",
				"http://example.com/header-portal.js",
				"http://example.com/header-portlet.js",
				"module:/module-header-portal.js?t=7",
				"module:/o/portlet-web/module-header-portlet.js?t=7",
				"module:http://example.com/module-header-portal.js",
				"module:http://example.com/module-header-portlet.js"),
			portletRenderParts.getHeaderJavaScriptPaths());
		_assertEquals(
			Arrays.asList(
				"/footer-portal.js?t=7", "/nocombo-footer-portal.js?t=7",
				"/o/portlet-web/footer-portlet.js?t=7",
				"/o/portlet-web/nocombo-footer-portlet.js?t=7",
				"http://example.com/footer-portal.js",
				"http://example.com/footer-portlet.js",
				"module:/module-footer-portal.js?t=7",
				"module:/o/portlet-web/module-footer-portlet.js?t=7",
				"module:http://example.com/module-footer-portal.js",
				"module:http://example.com/module-footer-portlet.js"),
			portletRenderParts.getFooterJavaScriptPaths());
		_assertEquals(
			Arrays.asList(
				"/footer-portal.css?t=7", "/nocombo-footer-portal.css?t=7",
				"/o/portlet-web/footer-portlet.css?t=7",
				"/o/portlet-web/nocombo-footer-portlet.css?t=7",
				"http://example.com/footer-portal.css",
				"http://example.com/footer-portlet.css"),
			portletRenderParts.getFooterCssPaths());

		Assert.assertEquals(portletHTML, portletRenderParts.getPortletHTML());
		Assert.assertFalse(portletRenderParts.isRefresh());
	}

	@Test
	public void testGetPortletRenderPartsWithContext() {
		_setUpMocks(false, "/portal");

		String portletHTML = "<div>Hola</div>";

		PortletRenderParts portletRenderParts =
			PortletRenderUtil.getPortletRenderParts(
				_httpServletRequest, portletHTML, _portlet);

		_assertEquals(
			Arrays.asList(
				"/portal/header-portal.css?t=7",
				"/portal/nocombo-header-portal.css?t=7",
				"/portal/o/portlet-web/header-portlet.css?t=7",
				"/portal/o/portlet-web/nocombo-header-portlet.css?t=7",
				"http://example.com/header-portal.css",
				"http://example.com/header-portlet.css"),
			portletRenderParts.getHeaderCssPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/header-portal.js?t=7",
				"/portal/nocombo-header-portal.js?t=7",
				"/portal/o/portlet-web/header-portlet.js?t=7",
				"/portal/o/portlet-web/nocombo-header-portlet.js?t=7",
				"http://example.com/header-portal.js",
				"http://example.com/header-portlet.js",
				"module:/portal/module-header-portal.js?t=7",
				"module:/portal/o/portlet-web/module-header-portlet.js?t=7",
				"module:http://example.com/module-header-portal.js",
				"module:http://example.com/module-header-portlet.js"),
			portletRenderParts.getHeaderJavaScriptPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/footer-portal.css?t=7",
				"/portal/nocombo-footer-portal.css?t=7",
				"/portal/o/portlet-web/footer-portlet.css?t=7",
				"/portal/o/portlet-web/nocombo-footer-portlet.css?t=7",
				"http://example.com/footer-portal.css",
				"http://example.com/footer-portlet.css"),
			portletRenderParts.getFooterCssPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/footer-portal.js?t=7",
				"/portal/nocombo-footer-portal.js?t=7",
				"/portal/o/portlet-web/footer-portlet.js?t=7",
				"/portal/o/portlet-web/nocombo-footer-portlet.js?t=7",
				"http://example.com/footer-portal.js",
				"http://example.com/footer-portlet.js",
				"module:/portal/module-footer-portal.js?t=7",
				"module:/portal/o/portlet-web/module-footer-portlet.js?t=7",
				"module:http://example.com/module-footer-portal.js",
				"module:http://example.com/module-footer-portlet.js"),
			portletRenderParts.getFooterJavaScriptPaths());

		Assert.assertEquals(portletHTML, portletRenderParts.getPortletHTML());
		Assert.assertFalse(portletRenderParts.isRefresh());
	}

	@Test
	public void testGetPortletRenderPartsWithContextAndFastLoad() {
		_setUpMocks(true, "/portal");

		String portletHTML = "<div>Hola</div>";

		PortletRenderParts portletRenderParts =
			PortletRenderUtil.getPortletRenderParts(
				_httpServletRequest, portletHTML, _portlet);

		_assertEquals(
			Arrays.asList(
				"/portal/nocombo-header-portal.css",
				"/portal/o/portlet-web/nocombo-header-portlet.css",
				"http://example.com/header-portal.css",
				"http://example.com/header-portlet.css",
				StringBundler.concat(
					"/portal/combo?minifierType=css&themeId=theme_id&",
					"com.liferay.portlet.1:/portal/o/portlet-web",
					"/header-portlet.css&/portal/header-portal.css&t=8")),
			portletRenderParts.getHeaderCssPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/nocombo-header-portal.js",
				"/portal/o/portlet-web/nocombo-header-portlet.js",
				"http://example.com/header-portal.js",
				"http://example.com/header-portlet.js",
				"module:/portal/module-header-portal.js",
				"module:/portal/o/portlet-web/module-header-portlet.js",
				"module:http://example.com/module-header-portal.js",
				"module:http://example.com/module-header-portlet.js",
				StringBundler.concat(
					"/portal/combo?minifierType=js&themeId=theme_id&",
					"com.liferay.portlet.1:/portal/o/portlet-web",
					"/header-portlet.js&/portal/header-portal.js&t=8")),
			portletRenderParts.getHeaderJavaScriptPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/nocombo-footer-portal.css",
				"/portal/o/portlet-web/nocombo-footer-portlet.css",
				"http://example.com/footer-portal.css",
				"http://example.com/footer-portlet.css",
				StringBundler.concat(
					"/portal/combo?minifierType=css&themeId=theme_id&",
					"com.liferay.portlet.1:/portal/o/portlet-web",
					"/footer-portlet.css&/portal/footer-portal.css&t=8")),
			portletRenderParts.getFooterCssPaths());
		_assertEquals(
			Arrays.asList(
				"/portal/nocombo-footer-portal.js",
				"/portal/o/portlet-web/nocombo-footer-portlet.js",
				"http://example.com/footer-portal.js",
				"http://example.com/footer-portlet.js",
				"module:/portal/module-footer-portal.js",
				"module:/portal/o/portlet-web/module-footer-portlet.js",
				"module:http://example.com/module-footer-portal.js",
				"module:http://example.com/module-footer-portlet.js",
				StringBundler.concat(
					"/portal/combo?minifierType=js&themeId=theme_id&",
					"com.liferay.portlet.1:/portal/o/portlet-web",
					"/footer-portlet.js&/portal/footer-portal.js&t=8")),
			portletRenderParts.getFooterJavaScriptPaths());

		Assert.assertEquals(portletHTML, portletRenderParts.getPortletHTML());
		Assert.assertFalse(portletRenderParts.isRefresh());
	}

	@Test
	public void testGetPortletRenderPartsWithFastLoad() {
		_setUpMocks(true, "");

		String portletHTML = "<div>Hola</div>";

		PortletRenderParts portletRenderParts =
			PortletRenderUtil.getPortletRenderParts(
				_httpServletRequest, portletHTML, _portlet);

		_assertEquals(
			Arrays.asList(
				"/nocombo-header-portal.css",
				"/o/portlet-web/nocombo-header-portlet.css",
				"http://example.com/header-portal.css",
				"http://example.com/header-portlet.css",
				StringBundler.concat(
					"/combo?minifierType=css&themeId=theme_id&",
					"com.liferay.portlet.1:/o/portlet-web/header-portlet.css&",
					"/header-portal.css&t=8")),
			portletRenderParts.getHeaderCssPaths());
		_assertEquals(
			Arrays.asList(
				"/nocombo-header-portal.js",
				"/o/portlet-web/nocombo-header-portlet.js",
				"http://example.com/header-portal.js",
				"http://example.com/header-portlet.js",
				"module:/module-header-portal.js",
				"module:/o/portlet-web/module-header-portlet.js",
				"module:http://example.com/module-header-portal.js",
				"module:http://example.com/module-header-portlet.js",
				StringBundler.concat(
					"/combo?minifierType=js&themeId=theme_id&",
					"com.liferay.portlet.1:/o/portlet-web/header-portlet.js&",
					"/header-portal.js&t=8")),
			portletRenderParts.getHeaderJavaScriptPaths());
		_assertEquals(
			Arrays.asList(
				"/nocombo-footer-portal.css",
				"/o/portlet-web/nocombo-footer-portlet.css",
				"http://example.com/footer-portal.css",
				"http://example.com/footer-portlet.css",
				StringBundler.concat(
					"/combo?minifierType=css&themeId=theme_id&",
					"com.liferay.portlet.1:/o/portlet-web/footer-portlet.css&",
					"/footer-portal.css&t=8")),
			portletRenderParts.getFooterCssPaths());
		_assertEquals(
			Arrays.asList(
				"/nocombo-footer-portal.js",
				"/o/portlet-web/nocombo-footer-portlet.js",
				"http://example.com/footer-portal.js",
				"http://example.com/footer-portlet.js",
				"module:/module-footer-portal.js",
				"module:/o/portlet-web/module-footer-portlet.js",
				"module:http://example.com/module-footer-portal.js",
				"module:http://example.com/module-footer-portlet.js",
				StringBundler.concat(
					"/combo?minifierType=js&themeId=theme_id&",
					"com.liferay.portlet.1:/o/portlet-web/footer-portlet.js&",
					"/footer-portal.js&t=8")),
			portletRenderParts.getFooterJavaScriptPaths());

		Assert.assertEquals(portletHTML, portletRenderParts.getPortletHTML());
		Assert.assertFalse(portletRenderParts.isRefresh());
	}

	private void _assertEquals(
		Collection<String> expected, Collection<String> actual) {

		Set<String> expectedSet = new HashSet<>(expected);

		for (String actualString : actual) {
			Assert.assertTrue(
				"Actual string " + actualString,
				expectedSet.remove(actualString));
		}

		Assert.assertTrue(
			"Nonempty expected set " + expectedSet, expectedSet.isEmpty());
	}

	private void _setUpMocks(boolean fastLoad, String pathContext) {

		// HtmlUtil

		_htmlUtilMockedStatic.when(
			() -> HtmlUtil.escapeURL(Mockito.anyString())
		).thenAnswer(
			new Answer<String>() {

				@Override
				public String answer(InvocationOnMock invocationOnMock) {
					return invocationOnMock.getArgument(0);
				}

			}
		);

		// PortalUtil

		_portalUtilMockedStatic.when(
			PortalUtil::getPathContext
		).thenReturn(
			pathContext
		);

		_portalUtilMockedStatic.when(
			PortalUtil::getPathProxy
		).thenReturn(
			""
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getStaticResourceURL(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyLong())
		).thenAnswer(
			new Answer<String>() {

				@Override
				public String answer(InvocationOnMock invocationOnMock) {
					String uri = invocationOnMock.getArgument(1, String.class);
					long timestamp = invocationOnMock.getArgument(
						2, Long.class);

					if (timestamp < 0) {
						return uri;
					}

					return uri + "?t=" + timestamp;
				}

			}
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getStaticResourceURL(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyLong())
		).thenAnswer(
			new Answer<String>() {

				@Override
				public String answer(InvocationOnMock invocationOnMock) {
					String uri = invocationOnMock.getArgument(1, String.class);
					String queryString = invocationOnMock.getArgument(
						2, String.class);
					long timestamp = invocationOnMock.getArgument(
						3, Long.class);

					if (timestamp < 0) {
						return uri + "?" + queryString;
					}

					return StringBundler.concat(
						uri, "?", queryString, "&t=",
						String.valueOf(timestamp));
				}

			}
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.stripURLAnchor(
				Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			new Answer<String[]>() {

				@Override
				public String[] answer(InvocationOnMock invocationOnMock) {
					String url = invocationOnMock.getArgument(0, String.class);
					String separator = invocationOnMock.getArgument(
						1, String.class);

					String[] parts = url.split(separator);

					return new String[] {parts[0], ""};
				}

			}
		);

		// Theme

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getTimestamp()
		).thenReturn(
			8L
		);

		// ThemeDisplay

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCDNBaseURL()
		).thenReturn(
			""
		);

		Mockito.when(
			themeDisplay.getCDNDynamicResourcesHost()
		).thenReturn(
			""
		);

		Mockito.when(
			themeDisplay.getPathContext()
		).thenReturn(
			pathContext
		);

		Mockito.when(
			themeDisplay.getTheme()
		).thenReturn(
			theme
		);

		Mockito.when(
			themeDisplay.getThemeId()
		).thenReturn(
			"theme_id"
		);

		Mockito.when(
			themeDisplay.isThemeCssFastLoad()
		).thenReturn(
			fastLoad
		);

		Mockito.when(
			themeDisplay.isThemeJsFastLoad()
		).thenReturn(
			fastLoad
		);

		// HttpServletRequest

		_httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		// Root portlet

		Portlet rootPortlet = Mockito.mock(Portlet.class);

		Mockito.when(
			rootPortlet.getTimestamp()
		).thenReturn(
			7L
		);

		// Portlet

		_portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			_portlet.getContextPath()
		).thenReturn(
			pathContext + "/o/portlet-web"
		);

		Mockito.when(
			_portlet.getFooterPortalCss()
		).thenReturn(
			Arrays.asList(
				"/footer-portal.css", "http://example.com/footer-portal.css",
				"nocombo:/nocombo-footer-portal.css")
		);

		Mockito.when(
			_portlet.getFooterPortalJavaScript()
		).thenReturn(
			Arrays.asList(
				"/footer-portal.js", "http://example.com/footer-portal.js",
				"module:/module-footer-portal.js",
				"module:http://example.com/module-footer-portal.js",
				"nocombo:/nocombo-footer-portal.js")
		);

		Mockito.when(
			_portlet.getFooterPortletCss()
		).thenReturn(
			Arrays.asList(
				"/footer-portlet.css", "http://example.com/footer-portlet.css",
				"nocombo:/nocombo-footer-portlet.css")
		);

		Mockito.when(
			_portlet.getFooterPortletJavaScript()
		).thenReturn(
			Arrays.asList(
				"/footer-portlet.js", "http://example.com/footer-portlet.js",
				"module:/module-footer-portlet.js",
				"module:http://example.com/module-footer-portlet.js",
				"nocombo:/nocombo-footer-portlet.js")
		);

		Mockito.when(
			_portlet.getHeaderPortalCss()
		).thenReturn(
			Arrays.asList(
				"/header-portal.css", "http://example.com/header-portal.css",
				"nocombo:/nocombo-header-portal.css")
		);

		Mockito.when(
			_portlet.getHeaderPortalJavaScript()
		).thenReturn(
			Arrays.asList(
				"/header-portal.js", "http://example.com/header-portal.js",
				"module:/module-header-portal.js",
				"module:http://example.com/module-header-portal.js",
				"nocombo:/nocombo-header-portal.js")
		);

		Mockito.when(
			_portlet.getHeaderPortletCss()
		).thenReturn(
			Arrays.asList(
				"/header-portlet.css", "http://example.com/header-portlet.css",
				"nocombo:/nocombo-header-portlet.css")
		);

		Mockito.when(
			_portlet.getHeaderPortletJavaScript()
		).thenReturn(
			Arrays.asList(
				"/header-portlet.js", "http://example.com/header-portlet.js",
				"module:/module-header-portlet.js",
				"module:http://example.com/module-header-portlet.js",
				"nocombo:/nocombo-header-portlet.js")
		);

		Mockito.when(
			_portlet.getPortletId()
		).thenReturn(
			"com.liferay.portlet.1"
		);

		Mockito.when(
			_portlet.getRootPortlet()
		).thenReturn(
			rootPortlet
		);

		Mockito.when(
			_portlet.isAjaxable()
		).thenReturn(
			true
		);

		Mockito.when(
			_portlet.isInstanceable()
		).thenReturn(
			false
		);
	}

	private final MockedStatic<HtmlUtil> _htmlUtilMockedStatic =
		Mockito.mockStatic(HtmlUtil.class);
	private final HttpServletRequest _httpServletRequest =
		new MockHttpServletRequest();
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private Portlet _portlet;

}