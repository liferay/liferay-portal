/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.payment.entry.CommercePaymentEntryRefundTypeRegistry;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.display.context.CommercePaymentEntryDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
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
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Payments",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_PAYMENT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommercePaymentPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			CommercePaymentEntryDisplayContext
				commercePaymentEntryDisplayContext =
					new CommercePaymentEntryDisplayContext(
						_classNameLocalService, _commerceChannelService,
						_commerceCurrencyService,
						_commercePaymentEntryModelResourcePermission,
						_commercePaymentEntryRefundTypeRegistry,
						_commercePaymentEntryService,
						_commercePaymentMethodGroupRelService,
						_commercePriceFormatter,
						_portal.getHttpServletRequest(renderRequest), _language,
						_portal);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commercePaymentEntryDisplayContext);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry)"
	)
	private ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;

	@Reference
	private CommercePaymentEntryRefundTypeRegistry
		_commercePaymentEntryRefundTypeRegistry;

	@Reference
	private CommercePaymentEntryService _commercePaymentEntryService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}