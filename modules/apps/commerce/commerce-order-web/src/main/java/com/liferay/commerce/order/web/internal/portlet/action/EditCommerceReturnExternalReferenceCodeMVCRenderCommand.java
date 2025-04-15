/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.order.web.internal.display.context.CommerceReturnEditDisplayContext;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_RETURN,
		"mvc.command.name=/commerce_return/edit_commerce_return_external_reference_code"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceReturnExternalReferenceCodeMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceReturnEditDisplayContext commerceReturnEditDisplayContext =
				new CommerceReturnEditDisplayContext(
					_accountEntryLocalService, _classNameLocalService,
					_commerceOrderItemLocalService, _commerceOrderLocalService,
					_commercePaymentEntryService, _commercePriceFormatter,
					_language, _listTypeDefinitionService,
					_listTypeEntryService, _objectDefinitionLocalService,
					_objectEntryLocalService, _objectRelationshipLocalService,
					renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceReturnEditDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchObjectEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_return/external_reference_code.jsp";
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommercePaymentEntryService _commercePaymentEntryService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

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

}