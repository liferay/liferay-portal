/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.portlet;

import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributorRegistry;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.web.internal.display.context.CommerceSubscriptionEntryDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-product-subscription",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Subscriptions",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_SUBSCRIPTION_ENTRY,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceSubscriptionEntryPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CommerceSubscriptionEntryDisplayContext
			commerceSubscriptionEntryDisplayContext =
				new CommerceSubscriptionEntryDisplayContext(
					_commercePaymentMethodGroupRelLocalService,
					_commerceSubscriptionEntryLocalService,
					_commerceOrderItemLocalService,
					_cpSubscriptionTypeJSPContributorRegistry,
					_cpSubscriptionTypeRegistry,
					_portal.getHttpServletRequest(renderRequest),
					_portletResourcePermission);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceSubscriptionEntryDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private CPSubscriptionTypeJSPContributorRegistry
		_cpSubscriptionTypeJSPContributorRegistry;

	@Reference
	private CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SUBSCRIPTION + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}