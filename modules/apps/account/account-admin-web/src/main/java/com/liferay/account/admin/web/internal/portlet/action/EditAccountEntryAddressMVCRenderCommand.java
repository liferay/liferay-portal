/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.account.admin.web.internal.display.AccountEntryDisplayFactoryUtil;
import com.liferay.account.admin.web.internal.display.AddressDisplay;
import com.liferay.account.admin.web.internal.display.context.AccountEntryAddressDisplayContext;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_entry_address"
	},
	service = MVCRenderCommand.class
)
public class EditAccountEntryAddressMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long accountEntryId = ParamUtil.getLong(
			renderRequest, "accountEntryId");

		renderRequest.setAttribute(
			AccountWebKeys.ACCOUNT_ENTRY_DISPLAY,
			AccountEntryDisplayFactoryUtil.create(
				accountEntryId, renderRequest));

		long accountEntryAddressId = ParamUtil.getLong(
			renderRequest, "accountEntryAddressId");

		AddressDisplay addressDisplay = AddressDisplay.of(
			accountEntryAddressId);

		renderRequest.setAttribute(
			AccountWebKeys.ADDRESS_DISPLAY, addressDisplay);
		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new AccountEntryAddressDisplayContext(
				addressDisplay, _portal, renderResponse));

		return "/account_entries_admin/edit_account_entry_address.jsp";
	}

	@Reference
	private Portal _portal;

}