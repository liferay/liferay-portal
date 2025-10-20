/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.events;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class CommerceCheckoutPreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					_portal.getScopeGroupId(httpServletRequest));

			if (commerceChannel == null) {
				return;
			}

			HttpServletRequest originalHttpServletRequest =
				PortalUtil.getOriginalServletRequest(httpServletRequest);

			HttpSession httpSession = originalHttpServletRequest.getSession();

			CommerceOrder commerceOrder =
				(CommerceOrder)httpSession.getAttribute(
					CommerceCheckoutWebKeys.
						COMMERCE_ORDER_ON_ACCOUNT_SELECTION);
			boolean immediateCheckout = GetterUtil.getBoolean(
				httpSession.getAttribute(
					CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT));

			if ((commerceOrder == null) && immediateCheckout) {
				PortletURL commerceCheckoutPortletURL =
					PortletProviderUtil.getPortletURL(
						httpServletRequest,
						_groupLocalService.getGroup(
							commerceChannel.getSiteGroupId()),
						CommercePortletKeys.COMMERCE_CHECKOUT,
						PortletProvider.Action.VIEW);

				if (commerceCheckoutPortletURL != null) {
					httpSession.removeAttribute(
						CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT);

					httpServletResponse.sendRedirect(
						PortletURLBuilder.create(
							commerceCheckoutPortletURL
						).setMVCRenderCommandName(
							"/commerce_checkout/checkout_redirect"
						).buildString());
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCheckoutPreAction.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}