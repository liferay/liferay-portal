/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.url.builder.facet.CacheAwareAbsolutePortalURLBuilder.CachePolicy;
import com.liferay.portal.url.builder.internal.util.CacheHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Dictionary;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public abstract class BaseAbsolutePortalURLBuilderTestCase {

	protected Bundle mockBundle() {
		Bundle bundle = Mockito.mock(Bundle.class);

		Dictionary<String, String> headers = HashMapDictionaryBuilder.put(
			"Web-ContextPath", "/wcp"
		).build();

		Mockito.when(
			bundle.getHeaders(StringPool.BLANK)
		).thenReturn(
			headers
		);

		return bundle;
	}

	protected CacheHelper mockCacheHelper() {
		CacheHelper cacheHelper = Mockito.mock(CacheHelper.class);

		Mockito.doAnswer(
			invocationOnMock -> {
				StringBundler sb = invocationOnMock.getArgument(
					0, StringBundler.class);

				sb.append("mac=aG9saQ==");

				return null;
			}
		).when(
			cacheHelper
		).appendCacheParam(
			Mockito.any(StringBundler.class), Mockito.any(Bundle.class),
			Mockito.any(CachePolicy.class), Mockito.anyString()
		);

		return cacheHelper;
	}

	protected HttpServletRequest mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLanguageId()
		).thenReturn(
			"es"
		);

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getThemeId()
		).thenReturn(
			"atheme"
		);

		Mockito.when(
			themeDisplay.getTheme()
		).thenReturn(
			theme
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return httpServletRequest;
	}

	protected Portal mockPortal(boolean context, boolean proxy, boolean cdnHost)
		throws PortalException {

		Portal portal = Mockito.mock(Portal.class);

		String pathProxy = proxy ? "/proxy" : StringPool.BLANK;

		Mockito.when(
			portal.getPathProxy()
		).thenReturn(
			pathProxy
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			pathProxy + (context ? "/context" : StringPool.BLANK)
		);

		Mockito.when(
			portal.getCDNHost(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			cdnHost ? "http://cdn-host" : StringPool.BLANK
		);

		Mockito.when(
			portal.isCDNDynamicResourcesEnabled(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			cdnHost ? true : false
		);

		Mockito.doAnswer(
			invocationOnMock -> {
				String uri = invocationOnMock.getArgument(1, String.class);
				String queryString = invocationOnMock.getArgument(
					2, String.class);
				Long timestamp = invocationOnMock.getArgument(3, Long.class);

				return StringBundler.concat(
					uri, StringPool.QUESTION, queryString, "&t=", timestamp);
			}
		).when(
			portal
		).getStaticResourceURL(
			Mockito.any(HttpServletRequest.class), Mockito.anyString(),
			Mockito.anyString(), Mockito.anyLong()
		);

		return portal;
	}

	protected void setUp() throws Exception {
		_browserSnifferUtilMockedStatic = Mockito.mockStatic(
			BrowserSnifferUtil.class);

		_browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getBrowserId(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			BrowserSnifferUtil.BROWSER_ID_FIREFOX
		);
	}

	protected void tearDown() {
		_browserSnifferUtilMockedStatic.close();
	}

	private static MockedStatic<BrowserSnifferUtil>
		_browserSnifferUtilMockedStatic;

}