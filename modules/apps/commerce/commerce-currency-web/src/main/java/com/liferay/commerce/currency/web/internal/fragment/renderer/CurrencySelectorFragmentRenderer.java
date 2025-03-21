/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.web.internal.fragment.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(service = FragmentRenderer.class)
public class CurrencySelectorFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public String getIcon() {
		return "catalog";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "currency-selector");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPD-34908");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-34908")) {
			return;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			if (_isEditMode(httpServletRequest)) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-currency-selector-component-will-be-shown-here");
			}

			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/currency_selector/page.jsp");

			long commerceChannelId = commerceContext.getCommerceChannelId();

			httpServletRequest.setAttribute(
				"liferay-commerce:currency-selector:commerceChannelId",
				commerceChannelId);

			httpServletRequest.setAttribute(
				"liferay-commerce:currency-selector:commerceOrderDetailBaseURL",
				_commerceOrderHttpHelper.getCommerceCartBaseURL(
					httpServletRequest));

			CommerceOrder currentCommerceOrder =
				_commerceOrderHttpHelper.getCurrentCommerceOrder(
					httpServletRequest);

			long commerceOrderId = 0;

			if (currentCommerceOrder != null) {
				commerceOrderId = currentCommerceOrder.getCommerceOrderId();
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:currency-selector:commerceOrderId",
				commerceOrderId);

			httpServletRequest.setAttribute(
				"liferay-commerce:currency-selector:commerceOrderTypes",
				JSONUtil.toJSONArray(
					_commerceOrderTypeLocalService.getCommerceOrderTypes(
						_portal.getCompanyId(httpServletRequest),
						CommerceChannel.class.getName(), commerceChannelId,
						true, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					commerceOrderType -> JSONUtil.put(
						"name_i18n",
						commerceOrderType.getName(
							_portal.getLocale(httpServletRequest))
					).put(
						"orderTypeId",
						commerceOrderType.getCommerceOrderTypeId()
					)));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private void _printPortletMessageInfo(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String message) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			StringBundler sb = new StringBundler(3);

			sb.append("<div class=\"portlet-msg-info\">");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			sb.append(themeDisplay.translate(message));

			sb.append("</div>");

			printWriter.write(sb.toString());
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CurrencySelectorFragmentRenderer.class);

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.currency.web)"
	)
	private ServletContext _servletContext;

}