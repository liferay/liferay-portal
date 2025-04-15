/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 */
@Component(service = FragmentRenderer.class)
public class OrderSummaryFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", getClass());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"order_summary/dependencies/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject, resourceBundle);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "order-summary");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPD-20379");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/order_summary/page.jsp");

			long commerceOrderId = 0;

			CommerceOrder commerceOrder =
				CommerceOrderInfoItemUtil.getCommerceOrder(
					_commerceOrderService, httpServletRequest);

			if (commerceOrder != null) {
				commerceOrderId = commerceOrder.getCommerceOrderId();
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:order-summary:commerceOrderId",
				commerceOrderId);

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			String field = _getConfigurationValue(
				fragmentRendererContext, fragmentEntryLink, "field");

			httpServletRequest.setAttribute(
				"liferay-commerce:order-summary:field", field);

			httpServletRequest.setAttribute(
				"liferay-commerce:order-summary:fieldLabel",
				_getFieldLabel(fragmentEntryLink));

			if (commerceOrder != null) {
				httpServletRequest.setAttribute(
					"liferay-commerce:order-summary:fieldValue",
					_getFieldValue(
						commerceOrder, field, themeDisplay.getLocale()));
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:order-summary:label",
				_language.get(
					themeDisplay.getLocale(),
					_getConfigurationValue(
						fragmentRendererContext,
						fragmentRendererContext.getFragmentEntryLink(),
						"label")));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-summary:open",
				(commerceOrder != null) ? commerceOrder.isOpen() : false);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getConfigurationValue(
		FragmentRendererContext fragmentRendererContext,
		FragmentEntryLink fragmentEntryLink, String name) {

		return GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				getConfiguration(fragmentRendererContext),
				fragmentEntryLink.getEditableValues(),
				fragmentRendererContext.getLocale(), name));
	}

	private String _getFieldLabel(FragmentEntryLink fragmentEntryLink) {
		try {
			JSONObject configurationJSONObject = _jsonFactory.createJSONObject(
				fragmentEntryLink.getEditableValues());

			Iterator<String> configurationJSONObjectKeysIterator =
				configurationJSONObject.keys();

			JSONObject jsonObject = (JSONObject)configurationJSONObject.get(
				configurationJSONObjectKeysIterator.next());

			return jsonObject.getString("label");
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getFieldValue(
			CommerceOrder commerceOrder, String field, Locale locale)
		throws PortalException {

		if (field.equals("couponCode")) {
			return commerceOrder.getCouponCode();
		}
		else if (field.equals("shippingDiscountValueFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getShippingDiscountAmount(), locale);
		}
		else if (field.equals("shippingValueFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getShippingAmount(), locale);
		}
		else if (field.equals("subtotalDiscountValueFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getSubtotalDiscountAmount(), locale);
		}
		else if (field.equals("subtotalFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getSubtotal(), locale);
		}
		else if (field.equals("taxValueFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getTaxAmount(), locale);
		}
		else if (field.equals("totalDiscountValueFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getTotalDiscountAmount(), locale);
		}
		else if (field.equals("totalFormatted")) {
			return _commercePriceFormatter.format(
				commerceOrder.getCommerceCurrency(), commerceOrder.getTotal(),
				locale);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrderSummaryFragmentRenderer.class);

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.content.web)"
	)
	private ServletContext _servletContext;

}