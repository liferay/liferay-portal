/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.client.extension.web.internal.util;

import com.liferay.client.extension.type.CommerceCheckoutStepCET;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.HttpURLConnection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class ClientExtensionCommerceCheckoutStepTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpCommerceCheckoutStepCET();
		_setUpOAuth2ApplicationLocalService();
		_setUpUserService();

		_clientExtensionCommerceCheckoutStep =
			new ClientExtensionCommerceCheckoutStep(
				_commerceCheckoutStepCET,
				_commercePaymentMethodGroupRelLocalService, _http, _jsonFactory,
				_jspRenderer, _localOAuthClient, _oAuth2ApplicationLocalService,
				_portalCatapult, _servletContext, _userService);
	}

	@After
	public void tearDown() {
		_transactionInvokerUtilMockedStatic.close();
	}

	@Test
	public void testIsActive() throws Exception {
		Mockito.when(
			_commerceCheckoutStepCET.getActive()
		).thenReturn(
			false
		);

		ClientExtensionCommerceCheckoutStep
			inactiveClientExtensionCommerceCheckoutStep =
				new ClientExtensionCommerceCheckoutStep(
					_commerceCheckoutStepCET,
					_commercePaymentMethodGroupRelLocalService, _http,
					_jsonFactory, _jspRenderer, _localOAuthClient,
					_oAuth2ApplicationLocalService, _portalCatapult,
					_servletContext, _userService);

		Assert.assertFalse(
			inactiveClientExtensionCommerceCheckoutStep.isActive(
				_getHttpServletRequest(),
				Mockito.mock(HttpServletResponse.class)));

		Mockito.verifyNoInteractions(_http);

		_setUpHttp(
			HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_OK);

		Assert.assertFalse(
			_clientExtensionCommerceCheckoutStep.isActive(
				_getHttpServletRequest(),
				Mockito.mock(HttpServletResponse.class)));

		_setUpHttp(
			HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_INTERNAL_ERROR);

		Assert.assertFalse(
			_clientExtensionCommerceCheckoutStep.isActive(
				_getHttpServletRequest(),
				Mockito.mock(HttpServletResponse.class)));

		_setUpHttp(HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_OK);

		Assert.assertTrue(
			_clientExtensionCommerceCheckoutStep.isActive(
				_getHttpServletRequest(),
				Mockito.mock(HttpServletResponse.class)));
	}

	@Test
	public void testIsContinueAfterPayment() {
		Assert.assertFalse(
			_clientExtensionCommerceCheckoutStep.isContinueAfterPayment());

		Mockito.when(
			_commerceCheckoutStepCET.getContinueAfterPayment()
		).thenReturn(
			true
		);

		ClientExtensionCommerceCheckoutStep
			clientExtensionCommerceCheckoutStep =
				new ClientExtensionCommerceCheckoutStep(
					_commerceCheckoutStepCET,
					_commercePaymentMethodGroupRelLocalService, _http,
					_jsonFactory, _jspRenderer, _localOAuthClient,
					_oAuth2ApplicationLocalService, _portalCatapult,
					_servletContext, _userService);

		Assert.assertTrue(
			clientExtensionCommerceCheckoutStep.isContinueAfterPayment());
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		CommerceOrder commerceOrder = Mockito.mock(CommerceOrder.class);

		Mockito.when(
			commerceOrder.getCommerceOrderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceOrder.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER)
		).thenReturn(
			commerceOrder
		);

		return httpServletRequest;
	}

	private void _setUpCommerceCheckoutStepCET() {
		Mockito.when(
			_commerceCheckoutStepCET.getActive()
		).thenReturn(
			true
		);

		Mockito.when(
			_commerceCheckoutStepCET.getOAuth2ApplicationExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpHttp(int activeResponseCode, int readyResponseCode)
		throws Exception {

		Mockito.when(
			_http.URLtoByteArray(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(0);

				String location = options.getLocation();
				Http.Response response = options.getResponse();

				if (location.endsWith("/ready")) {
					response.setResponseCode(readyResponseCode);

					return "READY".getBytes();
				}

				response.setResponseCode(activeResponseCode);

				return RandomTestUtil.randomBytes();
			}
		);
	}

	private void _setUpOAuth2ApplicationLocalService() throws Exception {
		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			oAuth2Application.getHomePageURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_oAuth2ApplicationLocalService.
				getOAuth2ApplicationByExternalReferenceCode(
					Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			oAuth2Application
		);
	}

	private void _setUpUserService() throws Exception {
		Mockito.when(
			_userService.getCurrentUser()
		).thenReturn(
			Mockito.mock(User.class)
		);
	}

	private ClientExtensionCommerceCheckoutStep
		_clientExtensionCommerceCheckoutStep;
	private final CommerceCheckoutStepCET _commerceCheckoutStepCET =
		Mockito.mock(CommerceCheckoutStepCET.class);
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService = Mockito.mock(
			CommercePaymentMethodGroupRelLocalService.class);
	private final Http _http = Mockito.mock(Http.class);
	private final JSONFactory _jsonFactory = Mockito.mock(JSONFactory.class);
	private final JSPRenderer _jspRenderer = Mockito.mock(JSPRenderer.class);
	private final LocalOAuthClient _localOAuthClient = Mockito.mock(
		LocalOAuthClient.class);
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService =
		Mockito.mock(OAuth2ApplicationLocalService.class);
	private final PortalCatapult _portalCatapult = Mockito.mock(
		PortalCatapult.class);
	private final ServletContext _servletContext = Mockito.mock(
		ServletContext.class);
	private final MockedStatic<TransactionInvokerUtil>
		_transactionInvokerUtilMockedStatic = Mockito.mockStatic(
			TransactionInvokerUtil.class);
	private final UserService _userService = Mockito.mock(UserService.class);

}