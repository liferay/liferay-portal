/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.service.CPConfigurationListRelService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
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
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_CONFIGURATION_LISTS,
		"mvc.command.name=/cp_configuration_lists/edit_cp_configuration_list_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCPConfigurationListQualifiersMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCPConfigurationListQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	private void _updateCPConfigurationListQualifiers(
			ActionRequest actionRequest)
		throws PortalException {

		long cpConfigurationListId = ParamUtil.getLong(
			actionRequest, "cpConfigurationListId");

		String accountQualifiers = ParamUtil.getString(
			actionRequest, "accountQualifiers");

		if (Objects.equals(accountQualifiers, "all")) {
			_cpConfigurationListRelService.deleteCPConfigurationListRels(
				AccountEntry.class.getName(), cpConfigurationListId);
			_cpConfigurationListRelService.deleteCPConfigurationListRels(
				AccountGroup.class.getName(), cpConfigurationListId);
		}
		else if (Objects.equals(accountQualifiers, "accounts")) {
			_cpConfigurationListRelService.deleteCPConfigurationListRels(
				AccountGroup.class.getName(), cpConfigurationListId);
		}
		else {
			_cpConfigurationListRelService.deleteCPConfigurationListRels(
				AccountEntry.class.getName(), cpConfigurationListId);
		}

		String channelQualifiers = ParamUtil.getString(
			actionRequest, "channelQualifiers");

		if (Objects.equals(channelQualifiers, "all")) {
			_commerceChannelRelService.deleteCommerceChannelRels(
				CPConfigurationList.class.getName(), cpConfigurationListId);
		}
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CPConfigurationListRelService _cpConfigurationListRelService;

}