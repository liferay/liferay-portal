/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.util;

import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.request.CommercePaymentRequestProvider;
import com.liferay.commerce.payment.request.CommercePaymentRequestProviderRegistry;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.util.CommercePaymentHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;

import java.util.Collections;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(service = CommercePaymentHelper.class)
public class CommercePaymentHelperImpl implements CommercePaymentHelper {

	@Override
	public CommercePaymentResult emptyResult(
		long commerceOrderId, String transactionId) {

		return new CommercePaymentResult(
			transactionId, commerceOrderId, -1, false, null, null,
			Collections.emptyList(), false);
	}

	@Override
	public CommercePaymentIntegration getCommercePaymentIntegration(
			long commerceChannelId, String paymentIntegrationKey)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannel.getGroupId(), paymentIntegrationKey);

		if ((commercePaymentMethodGroupRel == null) ||
			!commercePaymentMethodGroupRel.isActive()) {

			return null;
		}

		return _commercePaymentIntegrationRegistry.
			getCommercePaymentIntegration(
				commercePaymentMethodGroupRel.getPaymentIntegrationKey());
	}

	@Override
	public CommercePaymentMethod getCommercePaymentMethod(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		String commercePaymentMethodKey =
			commerceOrder.getCommercePaymentMethodKey();

		if (commercePaymentMethodKey.isEmpty()) {
			return null;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), commercePaymentMethodKey);

		if ((commercePaymentMethodGroupRel == null) ||
			!commercePaymentMethodGroupRel.isActive()) {

			return null;
		}

		return _commercePaymentMethodRegistry.getCommercePaymentMethod(
			commercePaymentMethodGroupRel.getPaymentIntegrationKey());
	}

	@Override
	public CommercePaymentRequest getCommercePaymentRequest(
			CommerceOrder commerceOrder, Locale locale, String transactionId,
			String checkoutStepUrl, HttpServletRequest httpServletRequest,
			CommercePaymentMethod commercePaymentMethod)
		throws Exception {

		String cancelUrl = null;
		String returnUrl = null;

		if (CommercePaymentMethodConstants.TYPE_ONLINE_REDIRECT ==
				commercePaymentMethod.getPaymentType()) {

			cancelUrl = _getCancelUrl(
				httpServletRequest, commerceOrder, checkoutStepUrl,
				commercePaymentMethod);
			returnUrl = _getReturnUrl(
				httpServletRequest, commerceOrder, checkoutStepUrl,
				commercePaymentMethod);
		}

		CommercePaymentRequestProvider commercePaymentRequestProvider =
			getCommercePaymentRequestProvider(commercePaymentMethod);

		return commercePaymentRequestProvider.getCommercePaymentRequest(
			cancelUrl, commerceOrder.getCommerceOrderId(), httpServletRequest,
			locale, returnUrl, transactionId);
	}

	@Override
	public CommercePaymentRequestProvider getCommercePaymentRequestProvider(
		CommercePaymentMethod commercePaymentMethod) {

		CommercePaymentRequestProvider commercePaymentRequestProvider =
			_commercePaymentRequestProviderRegistry.
				getCommercePaymentRequestProvider(
					commercePaymentMethod.getKey());

		if (commercePaymentRequestProvider == null) {
			commercePaymentRequestProvider =
				_commercePaymentRequestProviderRegistry.
					getCommercePaymentRequestProvider("default");
		}

		return commercePaymentRequestProvider;
	}

	@Override
	public boolean isDeliveryOnlySubscription(CommerceOrder commerceOrder) {
		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			if (Validator.isNotNull(commerceOrderItem.getSubscriptionType())) {
				return false;
			}
		}

		return true;
	}

	private StringBundler _getBaseUrl(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder,
			String redirect, CommercePaymentMethod commercePaymentMethod,
			int extraCapacity)
		throws Exception {

		StringBundler sb = new StringBundler(
			extraCapacity + (Validator.isNotNull(redirect) ? 12 : 10));

		sb.append(_portal.getPortalURL(httpServletRequest));
		sb.append(_portal.getPathModule());
		sb.append(CharPool.SLASH);
		sb.append(commercePaymentMethod.getServletPath());
		sb.append("?groupId=");
		sb.append(commerceOrder.getGroupId());
		sb.append("&uuid=");
		sb.append(URLCodec.encodeURL(commerceOrder.getUuid()));

		if (commerceOrder.isGuestOrder()) {
			Company company = _portal.getCompany(httpServletRequest);

			Key key = company.getKeyObj();

			String token = _encryptor.encrypt(
				key, String.valueOf(commerceOrder.getCommerceOrderId()));

			sb.append("&guestToken=");
			sb.append(token);
			sb.append(StringPool.AMPERSAND);
		}

		if (Validator.isNotNull(redirect)) {
			sb.append("&redirect=");
			sb.append(URLCodec.encodeURL(redirect));
		}

		return sb;
	}

	private String _getCancelUrl(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder,
			String redirect, CommercePaymentMethod commercePaymentMethod)
		throws Exception {

		StringBundler sb = _getBaseUrl(
			httpServletRequest, commerceOrder, redirect, commercePaymentMethod,
			1);

		sb.append("&cancel=true");

		return sb.toString();
	}

	private String _getReturnUrl(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder,
			String redirect, CommercePaymentMethod commercePaymentMethod)
		throws Exception {

		StringBundler sb = _getBaseUrl(
			httpServletRequest, commerceOrder, redirect, commercePaymentMethod,
			0);

		if (commerceOrder.isSubscriptionOrder() &&
			!isDeliveryOnlySubscription(commerceOrder)) {

			sb.append("&orderType=subscription");
		}
		else {
			sb.append("&orderType=normal");
		}

		return sb.toString();
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommercePaymentRequestProviderRegistry
		_commercePaymentRequestProviderRegistry;

	@Reference
	private Encryptor _encryptor;

	@Reference
	private Portal _portal;

}