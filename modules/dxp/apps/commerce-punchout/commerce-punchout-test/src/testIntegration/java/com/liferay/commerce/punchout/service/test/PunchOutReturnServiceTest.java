/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.punchout.service.PunchOutReturnService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class PunchOutReturnServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_httpServer = HttpServer.create(
			new InetSocketAddress("127.0.0.1", 0), 0);

		_httpServer.createContext("/redirect", this::_redirect);
		_httpServer.createContext("/return", this::_writeRedirectURL);

		_httpServer.start();
	}

	@After
	public void tearDown() {
		_httpServer.stop(0);
	}

	@Test
	public void testReturnToPunchOutVendor() throws Exception {
		Assert.assertNull(
			_punchOutReturnService.returnToPunchOutVendor(
				_commerceOrder, _getURL("/return")));
		Assert.assertEquals(0, _requestsCount.get());

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					URLFetchSecurityCompanyConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"urlLocalNetworkAccessEnabled", true
					).build())) {

			Assert.assertNull(
				_punchOutReturnService.returnToPunchOutVendor(
					_commerceOrder, _getURL("/redirect")));
			Assert.assertEquals(1, _requestsCount.get());

			Assert.assertEquals(
				_REDIRECT_URL,
				_punchOutReturnService.returnToPunchOutVendor(
					_commerceOrder, _getURL("/return")));
			Assert.assertEquals(2, _requestsCount.get());
		}
	}

	private String _getURL(String path) {
		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		return "http://127.0.0.1:" + inetSocketAddress.getPort() + path;
	}

	private void _redirect(HttpExchange httpExchange) throws IOException {
		_requestsCount.incrementAndGet();

		Headers responseHeaders = httpExchange.getResponseHeaders();

		responseHeaders.set("Location", _getURL("/return"));

		httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_MOVED_TEMP, -1);

		httpExchange.close();
	}

	private void _writeRedirectURL(HttpExchange httpExchange)
		throws IOException {

		_requestsCount.incrementAndGet();

		String json = JSONUtil.put(
			"redirecturl", _REDIRECT_URL
		).toString();

		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		httpExchange.sendResponseHeaders(
			HttpURLConnection.HTTP_OK, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private static final String _REDIRECT_URL = "https://example.com/redirect";

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private Group _group;

	private HttpServer _httpServer;

	@Inject
	private PunchOutReturnService _punchOutReturnService;

	private final AtomicInteger _requestsCount = new AtomicInteger();

	@DeleteAfterTestRun
	private User _user;

}