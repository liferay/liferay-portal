/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.content.web.internal.display.context.CommerceOrderContentDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
		"mvc.command.name=/commerce_open_order_content/edit_commerce_order"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceOrderMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_populatePortletDisplay(renderRequest);

			CommerceOrderContentDisplayContext
				commerceOrderContentDisplayContext =
					(CommerceOrderContentDisplayContext)
						renderRequest.getAttribute(
							WebKeys.PORTLET_DISPLAY_CONTEXT);

			CommerceOrder commerceOrder =
				commerceOrderContentDisplayContext.getCommerceOrder();

			if ((commerceOrder != null) && commerceOrder.isOpen()) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				long commerceChannelGroupId =
					_commerceChannelLocalService.
						getCommerceChannelGroupIdBySiteGroupId(
							themeDisplay.getScopeGroupId());

				AccountEntry accountEntry =
					_commerceAccountHelper.getCurrentAccountEntry(
						commerceChannelGroupId,
						_portal.getHttpServletRequest(renderRequest));

				if (accountEntry.getAccountEntryId() !=
						commerceOrder.getCommerceAccountId()) {

					HttpServletResponse httpServletResponse =
						_portal.getHttpServletResponse(renderResponse);

					httpServletResponse.sendRedirect(
						_portletURLFactory.create(
							_portal.getHttpServletRequest(renderRequest),
							CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
							PortletRequest.RENDER_PHASE
						).toString());

					return "/pending_commerce_orders/view.jsp";
				}

				CommerceOrder currentCommerceOrder =
					_commerceOrderHttpHelper.getCurrentCommerceOrder(
						_portal.getHttpServletRequest(renderRequest));

				if ((currentCommerceOrder == null) ||
					(commerceOrder.getCommerceOrderId() !=
						currentCommerceOrder.getCommerceOrderId())) {

					_commerceOrderHttpHelper.setCurrentCommerceOrder(
						_portal.getHttpServletRequest(renderRequest),
						commerceOrder);
				}
			}

			if (FeatureFlagManagerUtil.isEnabled("COMMERCE-8949")) {
				return "/pending_commerce_orders/new_view.jsp";
			}

			return "/pending_commerce_orders/edit_commerce_order.jsp";
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					themeDisplay.getRequest(),
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
					themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
			).buildString());
	}

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}