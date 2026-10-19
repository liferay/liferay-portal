/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.servlet.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.constants.CommercePaymentWebKeys;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.Servlet;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class MercanetServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(group.getCompanyId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_company.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser();

		_commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				_user.getUserId(), _commerceChannel.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, null, "mercanet",
				99, null);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				"com.liferay.commerce.payment.method.mercanet"));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue("environment", "test");
		modifiableSettings.setValue("keyVersion", "1");
		modifiableSettings.setValue("merchantId", _MERCHANT_ID);
		modifiableSettings.setValue("secretKey", _SECRET_KEY);

		modifiableSettings.store();
	}

	@Test
	public void testDoGet() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder(
			"mercanet", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			_user.getUserId());
		CommerceOrder guestCommerceOrder = _addGuestCommerceOrder();

		String redirectionData =
			RandomTestUtil.randomString() + "\"><img src=x onerror=alert(1)>";
		String redirectURL =
			"https://payment-webinit-mercanet.test.sips-services.com" +
				"/paymentInit";
		String seal = RandomTestUtil.randomString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.method.mercanet.internal." +
					"servlet.MercanetServlet",
				LoggerTestUtil.ERROR)) {

			MockHttpServletResponse mockHttpServletResponse = _service(
				_getMockHttpServletRequest(
					guestCommerceOrder, null,
					_getGuestToken(guestCommerceOrder), redirectionData,
					redirectURL, seal, null, null));

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(
				content.contains(
					"action=\"" + HtmlUtil.escapeHREF(redirectURL) + "\""));
			Assert.assertTrue(
				content.contains(
					"value=\"" + HtmlUtil.escapeAttribute(redirectionData) +
						"\""));
			Assert.assertTrue(
				content.contains(
					"value=\"" + HtmlUtil.escapeAttribute(seal) + "\""));
			Assert.assertFalse(content.contains("<img"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());

			mockHttpServletResponse = _service(
				_getMockHttpServletRequest(
					guestCommerceOrder, null, _getGuestToken(commerceOrder),
					redirectionData, redirectURL, seal, null, null));

			content = mockHttpServletResponse.getContentAsString();

			Assert.assertFalse(content.contains("formMercanet"));

			_assertPrincipalException(logCapture);

			logCapture.resetPriority(LoggerTestUtil.ERROR);

			mockHttpServletResponse = _service(
				_getMockHttpServletRequest(
					commerceOrder, null, null, redirectionData, redirectURL,
					seal, null, UserTestUtil.addUser()));

			content = mockHttpServletResponse.getContentAsString();

			Assert.assertFalse(content.contains("formMercanet"));

			_assertPrincipalException(logCapture);
		}
	}

	@Test
	public void testDoPost() throws Exception {
		_testDoPostWhenTypeIsAutomatic();
		_testDoPostWhenTypeIsNormal();
	}

	private CommerceOrder _addCommerceOrder(
			String commercePaymentMethodKey, int paymentStatus, long userId)
		throws Exception {

		return _updateCommerceOrder(
			CommerceTestUtil.addB2CCommerceOrder(
				userId, _commerceChannel.getGroupId(), _commerceCurrency),
			commercePaymentMethodKey, paymentStatus);
	}

	private CommerceOrder _addGuestCommerceOrder() throws Exception {
		User guestUser = _company.getGuestUser();

		return _updateCommerceOrder(
			_commerceOrderLocalService.addCommerceOrder(
				guestUser.getUserId(), _commerceChannel.getGroupId(),
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_commerceCurrency.getCode(), 0),
			"mercanet", CommerceOrderPaymentConstants.STATUS_AUTHORIZED);
	}

	private void _assertCommerceOrder(
			CommerceOrder commerceOrder, int paymentStatus)
		throws Exception {

		CommerceOrder currentCommerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			paymentStatus, currentCommerceOrder.getPaymentStatus());
		Assert.assertEquals(
			commerceOrder.getTransactionId(),
			currentCommerceOrder.getTransactionId());
	}

	private void _assertPrincipalException(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertTrue(
			logEntry.getThrowable() instanceof PrincipalException);
	}

	private String _getData(
		long customerId, String merchantId, long orderId, String responseCode,
		String transactionReference) {

		return StringBundler.concat(
			"customerId=", customerId, "|merchantId=", merchantId, "|orderId=",
			orderId, "|responseCode=", responseCode, "|transactionReference=",
			transactionReference);
	}

	private String _getGuestToken(CommerceOrder commerceOrder)
		throws Exception {

		return _encryptor.encrypt(
			_company.getKeyObj(),
			String.valueOf(commerceOrder.getCommerceOrderId()));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		CommerceOrder commerceOrder, String data, String guestToken,
		String redirectionData, String redirectURL, String seal, String type,
		User user) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "/mercanet-payment");

		if (commerceOrder != null) {
			mockHttpServletRequest.addParameter(
				"groupId", String.valueOf(commerceOrder.getGroupId()));
			mockHttpServletRequest.addParameter(
				"uuid", commerceOrder.getUuid());
		}

		if (Validator.isNotNull(guestToken)) {
			mockHttpServletRequest.addParameter("guestToken", guestToken);
		}

		if (Validator.isNull(type)) {
			mockHttpServletRequest.setAttribute(
				CommercePaymentWebKeys.REDIRECT_URL,
				URLCodec.encodeURL(redirectURL));
			mockHttpServletRequest.setAttribute(
				CommercePaymentWebKeys.REDIRECTION_DATA,
				URLCodec.encodeURL(redirectionData));
			mockHttpServletRequest.setAttribute(
				CommercePaymentWebKeys.SEAL, URLCodec.encodeURL(seal));
		}
		else {
			mockHttpServletRequest.addParameter("Data", data);
			mockHttpServletRequest.addParameter(
				"redirect", "http://localhost/web/guest");

			if (Validator.isNotNull(seal)) {
				mockHttpServletRequest.addParameter("Seal", seal);
			}

			mockHttpServletRequest.addParameter("type", type);
			mockHttpServletRequest.setMethod("POST");
		}

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());
		}

		return mockHttpServletRequest;
	}

	private Http.Options _getOptions(
		CommerceOrder commerceOrder, String data, String type) {

		Http.Options options = new Http.Options();

		options.addPart("Data", data);
		options.addPart("groupId", String.valueOf(commerceOrder.getGroupId()));
		options.addPart("Seal", _getSeal(data));
		options.addPart("type", type);
		options.addPart("uuid", commerceOrder.getUuid());
		options.setFollowRedirects(false);
		options.setLocation(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/o/mercanet-payment"));
		options.setPost(true);

		return options;
	}

	private String _getSeal(String data) {
		return DigesterUtil.digestHex(DigesterUtil.SHA_256, data + _SECRET_KEY);
	}

	private MockHttpServletResponse _service(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalImpl.class.getName(), LoggerTestUtil.OFF)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse;
	}

	private void _testDoPostWhenTypeIsAutomatic() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder(
			"mercanet", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			_user.getUserId());

		String data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "00",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, null, "automatic",
				null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null,
				DigesterUtil.digestHex(
					DigesterUtil.SHA_256, data + RandomTestUtil.randomString()),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			RandomTestUtil.randomLong(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "00",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), RandomTestUtil.randomString(),
			commerceOrder.getCommerceOrderId(), "00",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			RandomTestUtil.randomLong(), "00",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "00",
			RandomTestUtil.randomString());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		CommerceOrder moneyOrderCommerceOrder = _addCommerceOrder(
			"money-order", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			_user.getUserId());

		data = _getData(
			moneyOrderCommerceOrder.getUserId(), _MERCHANT_ID,
			moneyOrderCommerceOrder.getCommerceOrderId(), "00",
			moneyOrderCommerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				moneyOrderCommerceOrder, data, null, null, null, _getSeal(data),
				"automatic", null));

		_assertCommerceOrder(
			moneyOrderCommerceOrder,
			CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "00",
			commerceOrder.getTransactionId());

		_http.URLtoString(_getOptions(commerceOrder, data, "automatic"));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_COMPLETED);
	}

	private void _testDoPostWhenTypeIsNormal() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder(
			"mercanet", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			_user.getUserId());

		String data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "00",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, null, "normal", _user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null,
				DigesterUtil.digestHex(
					DigesterUtil.SHA_256, data + RandomTestUtil.randomString()),
				"normal", _user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			RandomTestUtil.randomLong(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), RandomTestUtil.randomString(),
			commerceOrder.getCommerceOrderId(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			RandomTestUtil.randomLong(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "05",
			RandomTestUtil.randomString());

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		CommerceOrder moneyOrderCommerceOrder = _addCommerceOrder(
			"money-order", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			_user.getUserId());

		data = _getData(
			moneyOrderCommerceOrder.getUserId(), _MERCHANT_ID,
			moneyOrderCommerceOrder.getCommerceOrderId(), "05",
			moneyOrderCommerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				moneyOrderCommerceOrder, data, null, null, null, _getSeal(data),
				"normal", _user));

		_assertCommerceOrder(
			moneyOrderCommerceOrder,
			CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		CommerceOrder completedCommerceOrder = _addCommerceOrder(
			"mercanet", CommerceOrderPaymentConstants.STATUS_COMPLETED,
			_user.getUserId());

		data = _getData(
			completedCommerceOrder.getUserId(), _MERCHANT_ID,
			completedCommerceOrder.getCommerceOrderId(), "05",
			completedCommerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				completedCommerceOrder, data, null, null, null, _getSeal(data),
				"normal", _user));

		_assertCommerceOrder(
			completedCommerceOrder,
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		data = _getData(
			commerceOrder.getUserId(), _MERCHANT_ID,
			commerceOrder.getCommerceOrderId(), "05",
			commerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				null, data, null, null, null, _getSeal(data), "normal",
				UserTestUtil.addUser()));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				UserTestUtil.addUser()));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user);

		mockHttpServletRequest.setParameter(
			"redirect", "https://www.google.com");

		MockHttpServletResponse mockHttpServletResponse = _service(
			mockHttpServletRequest);

		Assert.assertNull(mockHttpServletResponse.getRedirectedUrl());

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		User user = TestPropsValues.getUser();

		CommerceOrder adminCommerceOrder = _addCommerceOrder(
			"mercanet", CommerceOrderPaymentConstants.STATUS_AUTHORIZED,
			user.getUserId());

		String adminData = _getData(
			adminCommerceOrder.getUserId(), _MERCHANT_ID,
			adminCommerceOrder.getCommerceOrderId(), "05",
			adminCommerceOrder.getTransactionId());

		Http.Options options = _getOptions(
			adminCommerceOrder, adminData, "normal");

		String credentials =
			user.getEmailAddress() + ":" + TestPropsValues.USER_PASSWORD;

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(credentials.getBytes()));

		options.addPart(
			"redirect",
			"http://localhost:" + PortalUtil.getPortalServerPort(false));

		_http.URLtoString(options);

		_assertCommerceOrder(
			adminCommerceOrder,
			CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		_service(
			_getMockHttpServletRequest(
				commerceOrder, data, null, null, null, _getSeal(data), "normal",
				_user));

		_assertCommerceOrder(
			commerceOrder, CommerceOrderPaymentConstants.STATUS_CANCELLED);

		CommerceOrder guestCommerceOrder = _addGuestCommerceOrder();

		data = _getData(
			guestCommerceOrder.getUserId(), _MERCHANT_ID,
			guestCommerceOrder.getCommerceOrderId(), "05",
			guestCommerceOrder.getTransactionId());

		_service(
			_getMockHttpServletRequest(
				guestCommerceOrder, data, _getGuestToken(commerceOrder), null,
				null, _getSeal(data), "normal", null));

		_assertCommerceOrder(
			guestCommerceOrder,
			CommerceOrderPaymentConstants.STATUS_AUTHORIZED);

		_service(
			_getMockHttpServletRequest(
				guestCommerceOrder, data, _getGuestToken(guestCommerceOrder),
				null, null, _getSeal(data), "normal", null));

		_assertCommerceOrder(
			guestCommerceOrder, CommerceOrderPaymentConstants.STATUS_CANCELLED);
	}

	private CommerceOrder _updateCommerceOrder(
		CommerceOrder commerceOrder, String commercePaymentMethodKey,
		int paymentStatus) {

		commerceOrder.setCommercePaymentMethodKey(commercePaymentMethodKey);
		commerceOrder.setPaymentStatus(paymentStatus);
		commerceOrder.setTransactionId(RandomTestUtil.randomString());

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	private static final String _MERCHANT_ID = RandomTestUtil.randomString();

	private static final String _SECRET_KEY = RandomTestUtil.randomString();

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Encryptor _encryptor;

	@Inject
	private Http _http;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.method.mercanet.internal.servlet.MercanetServlet"
	)
	private Servlet _servlet;

	private User _user;

}