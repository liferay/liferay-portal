/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.util.FrontendJSWebUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public abstract class BaseFrontendResourceRequestHandlerTestCase {

	protected ConfigurationProvider mockConfigurationProvider(
			Map<String, ?> configurationProperties)
		throws Exception {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		Class<?> aClass = getClass();

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				FrontendCachingConfiguration.class, COMPANY_ID)
		).thenReturn(
			(FrontendCachingConfiguration)ProxyUtil.newProxyInstance(
				aClass.getClassLoader(),
				new Class<?>[] {FrontendCachingConfiguration.class},
				(proxy, method, args) -> configurationProperties.get(
					method.getName()))
		);

		return configurationProvider;
	}

	protected void mockDeployedFile(
			HashedFilesRegistry hashedFilesRegistry, String uri, String content)
		throws Exception {

		String unhashedURI = uri;

		if (HashedFilesUtil.containsHash(uri)) {
			unhashedURI = HashedFilesUtil.removeHash(uri);

			Mockito.when(
				hashedFilesRegistry.getHashedFileURI(Mockito.eq(unhashedURI))
			).thenReturn(
				uri
			);
		}

		URL url = Mockito.mock(URL.class);

		URLConnection urlConnection = Mockito.mock(URLConnection.class);

		Mockito.when(
			urlConnection.getLastModified()
		).thenReturn(
			LAST_MODIFIED
		);

		Mockito.when(
			url.getFile()
		).thenReturn(
			uri.substring(uri.lastIndexOf(StringPool.SLASH) + 1)
		);

		Mockito.when(
			url.openConnection()
		).thenReturn(
			urlConnection
		);

		Mockito.when(
			url.openStream()
		).thenAnswer(
			(Answer<InputStream>)invocationOnMock -> new ByteArrayInputStream(
				content.getBytes(StandardCharsets.UTF_8))
		);

		Mockito.when(
			hashedFilesRegistry.getResource(Mockito.eq(unhashedURI))
		).thenReturn(
			url
		);

		Mockito.when(
			hashedFilesRegistry.getResource(Mockito.eq(uri))
		).thenReturn(
			url
		);
	}

	protected HttpServletRequest mockHttpServletRequest(
		String requestURI, Map<String, String> parameterMap) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setRequestURI(requestURI);

		if (parameterMap != null) {
			for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
				mockHttpServletRequest.setParameter(
					entry.getKey(), entry.getValue());
			}
		}

		return mockHttpServletRequest;
	}

	protected Portal mockPortal() throws Exception {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCDNHost(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			COMPANY_ID
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getPathModule()
		).thenReturn(
			Portal.PATH_MODULE
		);

		Mockito.when(
			portal.getPathProxy()
		).thenReturn(
			StringPool.BLANK
		);

		return portal;
	}

	protected void setUp() {
		FrontendJSWebUtil.clearCache();
	}

	protected static final long COMPANY_ID = 100;

	protected static final long LAST_MODIFIED = System.currentTimeMillis();

}