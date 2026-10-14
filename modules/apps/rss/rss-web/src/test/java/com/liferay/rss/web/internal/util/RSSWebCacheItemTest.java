/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.webcache.WebCacheException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration;

import com.rometools.rome.feed.synd.SyndFeed;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.InetAddress;

import java.nio.charset.StandardCharsets;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Alberto Chaparro
 */
public class RSSWebCacheItemTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_httpUtilMockedStatic = Mockito.mockStatic(HttpUtil.class);

		_httpUtilMockedStatic.when(
			() -> HttpUtil.URLtoInputStream(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(0);

				Assert.assertFalse(options.isFollowRedirects());

				String location = options.getLocation();

				Http.Response response = options.getResponse();

				if (Objects.equals(location, _URL_NOT_REDIRECTED)) {
					response.setRedirect(_URL_LOCAL);
					response.setResponseCode(HttpServletResponse.SC_OK);
				}

				if (Objects.equals(location, _URL_PUBLIC)) {
					response.setRedirect(_redirectURL);
					response.setResponseCode(HttpServletResponse.SC_FOUND);
				}

				return new ByteArrayInputStream(
					_getFeedXML().getBytes(StandardCharsets.UTF_8));
			}
		);

		_inetAddressUtilMockedStatic = Mockito.mockStatic(
			InetAddressUtil.class);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.getInetAddressByName(Mockito.anyString())
		).thenReturn(
			InetAddress.getByName("203.0.113.1")
		);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.getInetAddressByName("10.0.0.1")
		).thenReturn(
			InetAddress.getByName("10.0.0.1")
		);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.getInetAddressByName("127.0.0.1")
		).thenReturn(
			InetAddress.getLoopbackAddress()
		);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.getInetAddressByName(_HOST_UNRESOLVED)
		).thenReturn(
			null
		);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.isLocalInetAddress(
				Mockito.any(InetAddress.class))
		).thenCallRealMethod();
	}

	@After
	public void tearDown() {
		_httpUtilMockedStatic.close();
		_inetAddressUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-106200")
	public void testConvert() {
		_testConvert(
			IOException.class,
			"Unable to exceed maximum number of allowed URL redirects: " +
				_URL_PUBLIC,
			_URL_PUBLIC, _URL_PUBLIC);
		_testConvert(
			SecurityException.class,
			"Local network URLs are not allowed: " + _URL_LOCAL, _URL_LOCAL,
			_URL_PUBLIC);
		_testConvert(
			SecurityException.class,
			"Local network URLs are not allowed: " + _URL_LOCAL, null,
			_URL_LOCAL);
		_testConvert(
			SecurityException.class,
			"Local network URLs are not allowed: " + _URL_SITE_LOCAL, null,
			_URL_SITE_LOCAL);
		_testConvert(
			SecurityException.class,
			"Only HTTP or HTTPS URLs are allowed: " + _URL_FILE, null,
			_URL_FILE);
		_testConvert(
			SecurityException.class,
			"Unable to resolve host: " + _HOST_UNRESOLVED, null,
			_URL_UNRESOLVED);
		_testConvert(
			null, null, "https://" + RandomTestUtil.randomString() + ".com",
			_URL_PUBLIC);
		_testConvert(null, null, null, _URL_NOT_REDIRECTED);
		_testConvert(null, null, null, _URL_PUBLIC);
	}

	private String _getFeedXML() {
		return StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\">",
			"<channel><title>", _TITLE, "</title><link>", _URL_PUBLIC,
			"</link><description>", RandomTestUtil.randomString(),
			"</description></channel></rss>");
	}

	private void _testConvert(
		Class<?> expectedExceptionClass, String expectedExceptionMessage,
		String redirectURL, String url) {

		_redirectURL = redirectURL;

		RSSWebCacheItem rssWebCacheItem = new RSSWebCacheItem(
			Mockito.mock(RSSWebCacheConfiguration.class), url);

		try {
			SyndFeed syndFeed = (SyndFeed)rssWebCacheItem.convert(
				StringPool.BLANK);

			Assert.assertNull(expectedExceptionClass);
			Assert.assertEquals(_TITLE, syndFeed.getTitle());
		}
		catch (WebCacheException webCacheException) {
			Throwable throwable = webCacheException.getCause();

			Assert.assertSame(expectedExceptionClass, throwable.getClass());
			Assert.assertEquals(
				expectedExceptionMessage, throwable.getMessage());
		}
	}

	private static final String _HOST_UNRESOLVED =
		RandomTestUtil.randomString() + ".net";

	private static final String _TITLE = RandomTestUtil.randomString();

	private static final String _URL_FILE =
		"file:///" + RandomTestUtil.randomString();

	private static final String _URL_LOCAL = "http://127.0.0.1/rss.xml";

	private static final String _URL_NOT_REDIRECTED =
		"http://" + RandomTestUtil.randomString() + ".com/not-redirected.xml";

	private static final String _URL_PUBLIC =
		"http://" + RandomTestUtil.randomString() + ".com/rss.xml";

	private static final String _URL_SITE_LOCAL = "http://10.0.0.1/rss.xml";

	private static final String _URL_UNRESOLVED =
		"http://" + _HOST_UNRESOLVED + "/rss.xml";

	private MockedStatic<HttpUtil> _httpUtilMockedStatic;
	private MockedStatic<InetAddressUtil> _inetAddressUtilMockedStatic;
	private String _redirectURL;

}