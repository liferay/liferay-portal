/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockServletContext;

/**
 * @author László Csontos
 */
public class ServletContextUtilTest {

	@Test
	public void testGetResourceURIWithEmptyPath() throws Exception {
		testGetResourceURI(StringPool.BLANK);
	}

	@Test(expected = URISyntaxException.class)
	public void testGetResourceURIWithInvalidCharacters() throws Exception {
		ServletContextUtil.getResourceURI(
			new URL("file://" + _URI_WITH_INVALID_CHARACTERS + "/dummy"));
	}

	@Test
	public void testGetResourceURIWithReservedCharacters() throws Exception {
		testGetResourceURI(_URI_WITH_RESERVED_CHARACTERS);
	}

	@Test
	public void testGetResourceURIWithUnreservedCharacters() throws Exception {
		testGetResourceURI(_URI_WITH_UNRESERVED_CHARACTERS);
	}

	@Test
	public void testGetRootURIWithEmptyPath() throws Exception {
		testGetRootURI(StringPool.BLANK, getURI(StringPool.SLASH));
	}

	@Test(expected = MalformedURLException.class)
	public void testGetRootURIWithInvalidCharacters() throws Exception {
		testGetRootURI(_URI_WITH_INVALID_CHARACTERS, null);
	}

	@Test
	public void testGetRootURIWithReservedCharacters() throws Exception {
		String path = _URI_WITH_RESERVED_CHARACTERS;

		testGetRootURI(path, getURI(path));
	}

	@Test
	public void testGetRootURIWithUnreservedCharacters() throws Exception {
		String path = _URI_WITH_UNRESERVED_CHARACTERS;

		testGetRootURI(path, getURI(path));
	}

	protected ServletContext getServletContext(final String path) {
		return new MockServletContext() {

			@Override
			public URL getResource(String resourcePath)
				throws MalformedURLException {

				return new URL("file:" + path + resourcePath);
			}

		};
	}

	protected URI getURI(String path) {
		try {
			return new URI("file", path, null);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	protected void testGetResourceURI(String resourceURL) throws Exception {
		URL url = new URL("file://" + resourceURL + "/dummy");

		URI uri = ServletContextUtil.getResourceURI(url);

		Assert.assertEquals("file", uri.getScheme());
		Assert.assertEquals(url.getPath(), uri.getSchemeSpecificPart());
		Assert.assertNull(uri.getFragment());
	}

	protected void testGetRootURI(String path, URI uri) throws Exception {
		ServletContext servletContext = getServletContext(path);

		Assert.assertEquals(uri, ServletContextUtil.getRootURI(servletContext));

		Assert.assertEquals(
			uri, servletContext.getAttribute(ServletContextUtil.URI_ATTRIBUTE));
	}

	private static final String _URI_WITH_INVALID_CHARACTERS = ":?#[]/@";

	private static final String _URI_WITH_RESERVED_CHARACTERS =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";

	private static final String _URI_WITH_UNRESERVED_CHARACTERS =
		"/!$&'()*+,;= ";

	private static final Log _log = LogFactoryUtil.getLog(
		ServletContextUtilTest.class);

}