/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Kiana Suetani
 */
public class LiferayHeadlessClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		saxReaderUtil.setSAXReader(new SAXReaderImpl());
	}

	@Before
	public void setUp() {
		_httpUtilMockedStatic = Mockito.mockStatic(HttpUtil.class);
	}

	@After
	public void tearDown() {
		_httpUtilMockedStatic.close();
	}

	@Test
	public void testGetPageURLs() throws Exception {
		String virtualHostsJSON =
			"{\"items\":[{\"virtualHost\":\"example.com\"}],\"totalCount\":1}";

		String sitemapXML = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=",
			"\"http://www.sitemaps.org/schemas/sitemap/0.9\">",
			"<url><loc>https://example.com/home</loc></url>",
			"<url><loc>https://example.com/about</loc></url></urlset>");

		Http.Response httpResponse = Mockito.mock(Http.Response.class);

		Mockito.when(
			httpResponse.getResponseCode()
		).thenReturn(
			200
		);

		_httpUtilMockedStatic.when(
			() -> HttpUtil.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocation -> {
				Http.Options options = invocation.getArgument(0);

				options.setResponse(httpResponse);

				String location = options.getLocation();

				if (location.contains("portal-instances")) {
					return virtualHostsJSON;
				}

				return sitemapXML;
			}
		);

		LiferayHeadlessClient liferayHeadlessClient = new LiferayHeadlessClient(
			null, "https://portal.example.com");

		List<String> urls = liferayHeadlessClient.getPageURLs(0);

		Assert.assertEquals(urls.toString(), 2, urls.size());
		Assert.assertEquals("https://example.com/home", urls.get(0));
		Assert.assertEquals("https://example.com/about", urls.get(1));
	}

	private MockedStatic<HttpUtil> _httpUtilMockedStatic;

}