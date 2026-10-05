/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.webcache.WebCacheException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration;

import com.rometools.rome.feed.synd.SyndFeed;

import java.io.ByteArrayInputStream;

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
	public void setUp() {
		_httpUtilMockedStatic = Mockito.mockStatic(HttpUtil.class);

		_httpUtilMockedStatic.when(
			() -> HttpUtil.URLtoInputStream(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(0);

				Assert.assertFalse(options.isFollowRedirects());

				if (Objects.equals(options.getLocation(), _PUBLIC_URL)) {
					Http.Response response = options.getResponse();

					response.setRedirect(_redirectURL);
				}

				return new ByteArrayInputStream(
					_getFeed().getBytes(StandardCharsets.UTF_8));
			}
		);

		_inetAddressUtilMockedStatic = Mockito.mockStatic(
			InetAddressUtil.class);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.getInetAddressByName("127.0.0.1")
		).thenReturn(
			InetAddress.getLoopbackAddress()
		);

		_inetAddressUtilMockedStatic.when(
			() -> InetAddressUtil.isLocalInetAddress(
				InetAddress.getLoopbackAddress())
		).thenReturn(
			true
		);
	}

	@After
	public void tearDown() {
		_httpUtilMockedStatic.close();
		_inetAddressUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-106200")
	public void testConvert() throws Exception {
		_testConvert("file:///" + RandomTestUtil.randomString(), null, false);
		_testConvert(_LOCAL_URL, null, false);
		_testConvert(
			_PUBLIC_URL, "https://" + RandomTestUtil.randomString() + ".com",
			true);
		_testConvert(_PUBLIC_URL, _LOCAL_URL, false);
		_testConvert(_PUBLIC_URL, _PUBLIC_URL, false);
		_testConvert(_PUBLIC_URL, null, true);
	}

	private String _getFeed() {
		return StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\">",
			"<channel><title>", _FEED_TITLE, "</title><link>", _PUBLIC_URL,
			"</link><description>", RandomTestUtil.randomString(),
			"</description></channel></rss>");
	}

	private void _testConvert(
			String url, String redirectURL, boolean expectedConversion)
		throws Exception {

		RSSWebCacheItem rssWebCacheItem = new RSSWebCacheItem(
			Mockito.mock(RSSWebCacheConfiguration.class), url);

		try {
			_redirectURL = redirectURL;

			SyndFeed syndFeed = (SyndFeed)rssWebCacheItem.convert(
				StringPool.BLANK);

			Assert.assertTrue(expectedConversion);
			Assert.assertEquals(_FEED_TITLE, syndFeed.getTitle());
		}
		catch (WebCacheException webCacheException) {
			Assert.assertFalse(expectedConversion);

			Throwable throwable = webCacheException.getCause();

			if (Objects.equals(redirectURL, _PUBLIC_URL)) {
				Assert.assertEquals(
					"Unable to exceed maximum number of allowed URL " +
						"redirects: " + url,
					throwable.getMessage());
			}
			else {
				Assert.assertEquals(
					"Only external HTTP or HTTPS URLs are allowed: " +
						GetterUtil.getString(redirectURL, url),
					throwable.getMessage());
			}
		}
	}

	private static final String _FEED_TITLE = RandomTestUtil.randomString();

	private static final String _LOCAL_URL = "http://127.0.0.1/rss.xml";

	private static final String _PUBLIC_URL =
		"http://" + RandomTestUtil.randomString() + ".com/rss.xml";

	private MockedStatic<HttpUtil> _httpUtilMockedStatic;
	private MockedStatic<InetAddressUtil> _inetAddressUtilMockedStatic;
	private String _redirectURL;

}