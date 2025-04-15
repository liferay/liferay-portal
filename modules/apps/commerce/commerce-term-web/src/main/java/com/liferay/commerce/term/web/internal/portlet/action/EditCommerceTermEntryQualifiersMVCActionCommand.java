/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.portlet.action;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.term.constants.CommerceTermEntryPortletKeys;
import com.liferay.commerce.term.exception.DuplicateCommerceTermEntryRelException;
import com.liferay.commerce.term.service.CommerceTermEntryRelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceTermEntryPortletKeys.COMMERCE_TERM_ENTRY,
		"mvc.command.name=/commerce_term_entry/edit_commerce_term_entry_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTermEntryQualifiersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceTermEntryQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof DuplicateCommerceTermEntryRelException) {
				SessionErrors.add(actionRequest, exception.getClass());

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	private void _updateCommerceTermEntryQualifiers(ActionRequest actionRequest)
		throws Exception {

		String orderTypeQualifiers = ParamUtil.getString(
			actionRequest, "orderTypeQualifiers");

		if (Objects.equals(orderTypeQualifiers, "all")) {
			_commerceTermEntryRelService.deleteCommerceTermEntryRels(
				CommerceOrderType.class.getName(),
				ParamUtil.getLong(actionRequest, "commerceTermEntryId"));
		}
	}

	@Reference
	private CommerceTermEntryRelService _commerceTermEntryRelService;

}