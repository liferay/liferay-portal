/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.display.context;

import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.display.context.helper.CommercePaymentRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Marco Leo
 */
public class CommercePaymentMethodGroupRelsDisplayContext {

	public CommercePaymentMethodGroupRelsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommercePaymentMethodGroupRelService
			commercePaymentMethodGroupRelService,
		CommercePaymentMethodRegistry commercePaymentMethodRegistry,
		CommercePaymentIntegrationRegistry commercePaymentIntegrationRegistry,
		HttpServletRequest httpServletRequest) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commercePaymentMethodGroupRelService =
			commercePaymentMethodGroupRelService;
		_commercePaymentMethodRegistry = commercePaymentMethodRegistry;
		_commercePaymentIntegrationRegistry =
			commercePaymentIntegrationRegistry;

		commercePaymentRequestHelper = new CommercePaymentRequestHelper(
			httpServletRequest);
	}

	public long getCommerceChannelId() throws PortalException {
		if (_commercePaymentMethodGroupRel != null) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					_commercePaymentMethodGroupRel.getGroupId());

			return commerceChannel.getCommerceChannelId();
		}

		return ParamUtil.getLong(
			commercePaymentRequestHelper.getRequest(), "commerceChannelId");
	}

	public String getCommercePaymentIntegrationKey() {
		if (_commercePaymentMethodGroupRel != null) {
			return _commercePaymentMethodGroupRel.getPaymentIntegrationKey();
		}

		return ParamUtil.getString(
			commercePaymentRequestHelper.getRequest(),
			"commercePaymentIntegrationKey");
	}

	public String getCommercePaymentMethodEngineDescription(Locale locale) {
		CommercePaymentMethod commercePaymentMethod =
			_commercePaymentMethodRegistry.getCommercePaymentMethod(
				getCommercePaymentMethodEngineKey());

		if (commercePaymentMethod == null) {
			return StringPool.BLANK;
		}

		return commercePaymentMethod.getDescription(locale);
	}

	public String getCommercePaymentMethodEngineKey() {
		if (_commercePaymentMethodGroupRel != null) {
			return _commercePaymentMethodGroupRel.getPaymentIntegrationKey();
		}

		return ParamUtil.getString(
			commercePaymentRequestHelper.getRequest(),
			"commercePaymentMethodEngineKey");
	}

	public String getCommercePaymentMethodEngineName(Locale locale) {
		String commercePaymentMethodEngineKey =
			getCommercePaymentMethodEngineKey();

		CommercePaymentMethod commercePaymentMethod =
			_commercePaymentMethodRegistry.getCommercePaymentMethod(
				commercePaymentMethodEngineKey);

		if (commercePaymentMethod != null) {
			return commercePaymentMethod.getName(locale);
		}

		CommercePaymentIntegration commercePaymentIntegration = null;

		if (!Objects.equals(
				commercePaymentMethodEngineKey,
				"function.commerce.payment.integration.configuration")) {

			commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commercePaymentMethodEngineKey);
		}
		else {
			commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						getCommercePaymentIntegrationKey());
		}

		return commercePaymentIntegration.getName(locale);
	}

	public CommercePaymentMethodGroupRel getCommercePaymentMethodGroupRel()
		throws PortalException {

		if (_commercePaymentMethodGroupRel != null) {
			return _commercePaymentMethodGroupRel;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				getCommerceChannelId());

		String commercePaymentMethodEngineKey =
			getCommercePaymentMethodEngineKey();

		if (!Objects.equals(
				commercePaymentMethodEngineKey,
				"function.commerce.payment.integration.configuration")) {

			_commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelService.
					fetchCommercePaymentMethodGroupRel(
						commerceChannel.getGroupId(),
						commercePaymentMethodEngineKey);
		}
		else {
			_commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelService.
					fetchCommercePaymentMethodGroupRel(
						commerceChannel.getGroupId(),
						getCommercePaymentIntegrationKey());
		}

		return _commercePaymentMethodGroupRel;
	}

	public long getCommercePaymentMethodGroupRelId() {
		if (_commercePaymentMethodGroupRel != null) {
			return _commercePaymentMethodGroupRel.
				getCommercePaymentMethodGroupRelId();
		}

		return 0;
	}

	protected final CommercePaymentRequestHelper commercePaymentRequestHelper;

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;
	private CommercePaymentMethodGroupRel _commercePaymentMethodGroupRel;
	private final CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;
	private final CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

}