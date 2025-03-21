/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet.action;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.payment.exception.DuplicateCommercePaymentMethodGroupRelQualifierException;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_PAYMENT_METHODS,
		"mvc.command.name=/commerce_payment_methods/edit_commerce_payment_method_group_rel_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCommercePaymentMethodGroupRelQualifiersMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommercePaymentMethodGroupRelQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommercePaymentMethodGroupRelQualifierException) {

				SessionErrors.add(actionRequest, exception.getClass());

				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	private void _updateCommercePaymentMethodGroupRelQualifiers(
			ActionRequest actionRequest)
		throws Exception {

		long commercePaymentMethodGroupRelId = ParamUtil.getLong(
			actionRequest, "commercePaymentMethodGroupRelId");

		String orderTypeQualifiers = ParamUtil.getString(
			actionRequest, "orderTypeQualifiers");

		if (Objects.equals(orderTypeQualifiers, "all")) {
			_commercePaymentMethodGroupRelQualifierService.
				deleteCommercePaymentMethodGroupRelQualifiers(
					CommerceOrderType.class.getName(),
					commercePaymentMethodGroupRelId);
		}

		String termEntryQualifiers = ParamUtil.getString(
			actionRequest, "termEntryQualifiers");

		if (Objects.equals(termEntryQualifiers, "none")) {
			_commercePaymentMethodGroupRelQualifierService.
				deleteCommercePaymentMethodGroupRelQualifiers(
					CommerceTermEntry.class.getName(),
					commercePaymentMethodGroupRelId);
		}
	}

	@Reference
	private CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

	@Reference
	private Portal _portal;

}