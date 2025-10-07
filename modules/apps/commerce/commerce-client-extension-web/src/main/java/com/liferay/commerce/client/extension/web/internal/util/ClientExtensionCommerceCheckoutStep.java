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
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
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
		JSONFactory jsonFactory, JSPRenderer jspRenderer,
		PortalCatapult portalCatapult, ServletContext servletContext,
		UserService userService) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_jsonFactory = jsonFactory;
		_jspRenderer = jspRenderer;
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

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		JSONObject jsonObject = JSONUtil.put(
			"commerceOrderId", commerceOrder.getCommerceOrderId());

		User currentUser = _userService.getCurrentUser();

		try {
			String status = new String(
				_portalCatapult.launch(
					commerceOrder.getCompanyId(), Http.Method.GET,
					_oAuth2ApplicationExternalReferenceCode, jsonObject,
					"/ready", currentUser.getUserId()
				).get());

			if (Objects.equals(status, "READY") && _active &&
				(Validator.isNull(_paymentMethodKey) ||
				 _paymentMethodKey.equals(
					 commerceOrder.getCommercePaymentMethodKey()))) {

				_portalCatapult.launch(
					commerceOrder.getCompanyId(), Http.Method.POST,
					_oAuth2ApplicationExternalReferenceCode, jsonObject,
					"/active", currentUser.getUserId()
				).get();

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return false;
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

	private final boolean _active;
	private final String _baseURL;
	private final int _commerceCheckoutStepOrder;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final Dictionary<String, Object> _dictionary;
	private final JSONFactory _jsonFactory;
	private final JSPRenderer _jspRenderer;
	private final String _label;
	private final String _name;
	private final String _oAuth2ApplicationExternalReferenceCode;
	private final boolean _order;
	private final String _paymentMethodKey;
	private final PortalCatapult _portalCatapult;
	private final boolean _sennaDisabled;
	private final ServletContext _servletContext;
	private final boolean _showControls;
	private final UserService _userService;
	private final boolean _visible;

}