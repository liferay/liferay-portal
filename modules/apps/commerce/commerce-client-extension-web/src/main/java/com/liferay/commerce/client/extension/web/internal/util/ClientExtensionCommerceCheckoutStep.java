/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.client.extension.web.internal.util;

import com.liferay.client.extension.type.CommerceCheckoutStepCET;
import com.liferay.commerce.client.extension.web.internal.type.deployer.Registrable;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.HttpURLConnection;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 */
public class ClientExtensionCommerceCheckoutStep
	implements CommerceCheckoutStep, Registrable {

	public ClientExtensionCommerceCheckoutStep(
		CommerceCheckoutStepCET commerceCheckoutStepCET,
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		Http http, JSONFactory jsonFactory, JSPRenderer jspRenderer,
		LocalOAuthClient localOAuthClient,
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService,
		PortalCatapult portalCatapult, ServletContext servletContext,
		UserService userService) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_http = http;
		_jsonFactory = jsonFactory;
		_jspRenderer = jspRenderer;
		_localOAuthClient = localOAuthClient;
		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
		_portalCatapult = portalCatapult;
		_servletContext = servletContext;
		_userService = userService;

		_active = commerceCheckoutStepCET.getActive();
		_baseURL = commerceCheckoutStepCET.getBaseURL();
		_commerceCheckoutStepOrder =
			commerceCheckoutStepCET.getCheckoutStepOrder();
		_label = commerceCheckoutStepCET.getCheckoutStepLabel();
		_name = commerceCheckoutStepCET.getCheckoutStepName();
		_oAuth2ApplicationExternalReferenceCode =
			commerceCheckoutStepCET.getOAuth2ApplicationExternalReferenceCode();
		_order = commerceCheckoutStepCET.getOrder();
		_paymentMethodKey = commerceCheckoutStepCET.getPaymentMethodKey();
		_sennaDisabled = commerceCheckoutStepCET.getSennaDisabled();
		_showControls = commerceCheckoutStepCET.getShowControls();
		_visible = commerceCheckoutStepCET.getVisible();

		_dictionary = HashMapDictionaryBuilder.<String, Object>put(
			"commerce.checkout.step.name", _name
		).put(
			"commerce.checkout.step.order", _commerceCheckoutStepOrder
		).build();
	}

	@Override
	public Dictionary<String, Object> getDictionary() {
		return _dictionary;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, _label);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean isActive(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_active) {
			return false;
		}

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (Validator.isNotNull(_paymentMethodKey) &&
			!_paymentMethodKey.equals(
				commerceOrder.getCommercePaymentMethodKey())) {

			return false;
		}

		JSONObject jsonObject = JSONUtil.put(
			"commerceOrderId", commerceOrder.getCommerceOrderId());

		User currentUser = _userService.getCurrentUser();

		try {
			Http.Response response = new Http.Response();

			String status = new String(
				_launch(
					commerceOrder, jsonObject, Http.Method.GET, "/ready",
					response, currentUser));

			if (!_isSuccess(response) || !Objects.equals(status, "READY")) {
				return false;
			}

			response = new Http.Response();

			_launch(
				commerceOrder, jsonObject, Http.Method.POST, "/active",
				response, currentUser);

			return _isSuccess(response);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	@Override
	public boolean isOrder() {
		return _order;
	}

	@Override
	public boolean isSennaDisabled() {
		return _sennaDisabled;
	}

	@Override
	public boolean isVisible(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		return _visible;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		JSONObject jsonObject = JSONUtil.put(
			"commerceOrderId", commerceOrder.getCommerceOrderId());

		ActionParameters actionParameters = actionRequest.getActionParameters();

		for (String name : actionParameters.getNames()) {
			jsonObject.put(name, actionParameters.getValue(name));
		}

		User currentUser = _userService.getCurrentUser();

		_portalCatapult.launch(
			commerceOrder.getCompanyId(), Http.Method.POST,
			_oAuth2ApplicationExternalReferenceCode, jsonObject, "/action",
			currentUser.getUserId()
		).get();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

			CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

			httpServletRequest.setAttribute(
			"orderId", commerceOrder.getCommerceOrderId());
			
		httpServletRequest.setAttribute(
			CommerceClientExtensionWebKeys.RENDER_URL, _baseURL + "/index.js");
			

		if (Validator.isNotNull(_paymentMethodKey)) {
			_renderPayment(httpServletRequest);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/checkout_step/client_extension.jsp");
	}

	@Override
	public boolean showControls(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _showControls;
	}

	private String _getLocation(
		OAuth2Application oAuth2Application, String resourcePath) {

		if (resourcePath.contains(Http.PROTOCOL_DELIMITER)) {
			return resourcePath;
		}

		String homePageURL = oAuth2Application.getHomePageURL();

		if (homePageURL.endsWith(StringPool.SLASH)) {
			homePageURL = homePageURL.substring(0, homePageURL.length() - 1);
		}

		if (resourcePath.startsWith(StringPool.SLASH)) {
			resourcePath = resourcePath.substring(1);
		}

		return StringBundler.concat(
			homePageURL, StringPool.SLASH, resourcePath);
	}

	private boolean _isSuccess(Http.Response response) {
		int responseCode = response.getResponseCode();

		if ((responseCode >= HttpURLConnection.HTTP_OK) &&
			(responseCode < HttpURLConnection.HTTP_MULT_CHOICE)) {

			return true;
		}

		return false;
	}

	private byte[] _launch(
			CommerceOrder commerceOrder, JSONObject jsonObject,
			Http.Method method, String resourcePath, Http.Response response,
			User user)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

		options.setBody(
			jsonObject.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				getOAuth2ApplicationByExternalReferenceCode(
					_oAuth2ApplicationExternalReferenceCode,
					commerceOrder.getCompanyId());

		options.setLocation(_getLocation(oAuth2Application, resourcePath));

		options.setMethod(method);
		options.setResponse(response);

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_localOAuthClient.consumeAccessToken(
						accessToken -> options.addHeader(
							HttpHeaders.AUTHORIZATION, "Bearer " + accessToken),
						oAuth2Application, user.getUserId());

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}

		return _http.URLtoByteArray(options);
	}

	private void _renderPayment(HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(),
					commerceOrder.getCommercePaymentMethodKey());

		if ((commercePaymentMethodGroupRel != null) &&
			commercePaymentMethodGroupRel.isActive()) {

			UnicodeProperties typeSettingsUnicodeProperties =
				commercePaymentMethodGroupRel.
					getTypeSettingsUnicodeProperties();

			String clientId = typeSettingsUnicodeProperties.get("clientId");

			if (Validator.isNotNull(clientId)) {
				String mode = typeSettingsUnicodeProperties.get("mode");

				if (mode.equals("live")) {
					httpServletRequest.setAttribute(
						"clientId", "li_" + clientId);
				}
				else {
					httpServletRequest.setAttribute(
						"clientId", "sb_" + clientId);
				}
			}
		}

		httpServletRequest.setAttribute(
			"orderId", commerceOrder.getCommerceOrderId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionCommerceCheckoutStep.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private final boolean _active;
	private final String _baseURL;
	private final int _commerceCheckoutStepOrder;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final Dictionary<String, Object> _dictionary;
	private final Http _http;
	private final JSONFactory _jsonFactory;
	private final JSPRenderer _jspRenderer;
	private final String _label;
	private final LocalOAuthClient _localOAuthClient;
	private final String _name;
	private final String _oAuth2ApplicationExternalReferenceCode;
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
	private final boolean _order;
	private final String _paymentMethodKey;
	private final PortalCatapult _portalCatapult;
	private final boolean _sennaDisabled;
	private final ServletContext _servletContext;
	private final boolean _showControls;
	private final UserService _userService;
	private final boolean _visible;

}
