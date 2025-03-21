/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemException;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemFileEntryIdException;
import com.liferay.commerce.product.type.virtual.order.exception.NoSuchVirtualOrderItemException;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_virtual_order_item_file_entry"
	},
	service = MVCActionCommand.class
)
public class EditCommerceVirtualOrderItemFileEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceVirtualOrderItemFileEntry(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof CommerceVirtualOrderItemException ||
				exception instanceof
					CommerceVirtualOrderItemFileEntryIdException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (exception instanceof NoSuchVirtualOrderItemException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _updateCommerceVirtualOrderItemFileEntry(
			ActionRequest actionRequest)
		throws Exception {

		long commerceVirtualOrderItemFileEntryId = ParamUtil.getLong(
			actionRequest, "commerceVirtualOrderItemFileEntryId");

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");
		String url = ParamUtil.getString(actionRequest, "url");
		int usages = ParamUtil.getInteger(actionRequest, "usages");
		String version = ParamUtil.getString(actionRequest, "version");

		_commerceVirtualOrderItemFileEntryService.
			updateCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId, fileEntryId, url, usages,
				version);
	}

	@Reference
	private CommerceVirtualOrderItemFileEntryService
		_commerceVirtualOrderItemFileEntryService;

}