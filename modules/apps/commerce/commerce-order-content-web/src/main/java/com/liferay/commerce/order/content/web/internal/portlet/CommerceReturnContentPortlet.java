/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.order.content.web.internal.display.context.CommerceReturnContentDisplayContext;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
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
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-return-content",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Returns",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/returns/view.jsp",
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_RETURN_CONTENT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceReturnContentPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(renderRequest), "LPD-10562")) {

			include("/returns/error.jsp", renderRequest, renderResponse);
		}
		else {
			try {
				CommerceReturnContentDisplayContext
					commerceReturnContentDisplayContext =
						new CommerceReturnContentDisplayContext(
							_accountEntryLocalService,
							_commerceOrderItemService, _commerceOrderService,
							_commercePaymentMethodGroupRelLocalService,
							_commercePriceFormatter, _commerceQuantityFormatter,
							_discussionPermission, _language,
							_listTypeDefinitionService, _listTypeEntryService,
							_objectDefinitionLocalService,
							_objectEntryLocalService,
							_objectRelationshipLocalService,
							_portal.getHttpServletRequest(renderRequest));

				renderRequest.setAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT,
					commerceReturnContentDisplayContext);

				super.render(renderRequest, renderResponse);
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private Language _language;

	@Reference
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Reference
	private ListTypeEntryService _listTypeEntryService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

}