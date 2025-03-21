/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.math.BigDecimal;

/**
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceShippingFixedOptionDisplayContext {

	public BaseCommerceShippingFixedOptionDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceShippingMethodService commerceShippingMethodService,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		this.commerceChannelLocalService = commerceChannelLocalService;
		this.commerceCurrencyLocalService = commerceCurrencyLocalService;
		this.commerceShippingMethodService = commerceShippingMethodService;
		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;
	}

	public String getCommerceCurrencyCode() throws PortalException {
		CommerceShippingMethod commerceShippingMethod =
			getCommerceShippingMethod();

		if (commerceShippingMethod == null) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceShippingMethod.getGroupId());

		if (commerceChannel == null) {
			return StringPool.BLANK;
		}

		return commerceChannel.getCommerceCurrencyCode();
	}

	public CommerceShippingMethod getCommerceShippingMethod()
		throws PortalException {

		if (_commerceShippingMethod != null) {
			return _commerceShippingMethod;
		}

		long commerceShippingMethodId = ParamUtil.getLong(
			renderRequest, "commerceShippingMethodId");

		if (commerceShippingMethodId > 0) {
			_commerceShippingMethod =
				commerceShippingMethodService.getCommerceShippingMethod(
					commerceShippingMethodId);
		}

		return _commerceShippingMethod;
	}

	public long getCommerceShippingMethodId() throws PortalException {
		CommerceShippingMethod commerceShippingMethod =
			getCommerceShippingMethod();

		if (commerceShippingMethod == null) {
			return 0;
		}

		return commerceShippingMethod.getCommerceShippingMethodId();
	}

	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/commerce_shipping_methods/edit_commerce_shipping_method"
		).setParameter(
			"commerceShippingMethodId",
			() -> {
				CommerceShippingMethod commerceShippingMethod =
					getCommerceShippingMethod();

				if (commerceShippingMethod != null) {
					return commerceShippingMethod.getCommerceShippingMethodId();
				}

				return null;
			}
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(renderRequest, "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).setParameter(
			"engineKey",
			() -> {
				String engineKey = ParamUtil.getString(
					renderRequest, "engineKey");

				if (Validator.isNotNull(engineKey)) {
					return engineKey;
				}

				return null;
			}
		).setParameter(
			"screenNavigationCategoryKey",
			_getSelectedScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	public String getScreenNavigationCategoryKey() {
		return "details";
	}

	public BigDecimal round(BigDecimal value) throws PortalException {
		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		if (commerceCurrency == null) {
			return value;
		}

		return commerceCurrency.round(value);
	}

	protected final CommerceChannelLocalService commerceChannelLocalService;
	protected final CommerceCurrencyLocalService commerceCurrencyLocalService;
	protected final CommerceShippingMethodService commerceShippingMethodService;
	protected final RenderRequest renderRequest;
	protected final RenderResponse renderResponse;

	private CommerceCurrency _getCommerceCurrency() throws PortalException {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String commerceCurrencyCode = getCommerceCurrencyCode();

		if (commerceCurrencyCode.isEmpty()) {
			return commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				themeDisplay.getCompanyId());
		}

		return commerceCurrencyLocalService.getCommerceCurrency(
			themeDisplay.getCompanyId(), commerceCurrencyCode);
	}

	private String _getSelectedScreenNavigationCategoryKey() {
		return ParamUtil.getString(
			renderRequest, "screenNavigationCategoryKey",
			getScreenNavigationCategoryKey());
	}

	private CommerceShippingMethod _commerceShippingMethod;

}