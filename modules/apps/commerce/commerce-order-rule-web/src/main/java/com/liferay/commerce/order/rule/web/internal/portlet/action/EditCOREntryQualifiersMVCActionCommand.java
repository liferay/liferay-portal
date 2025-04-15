/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.constants.COREntryPortletKeys;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.product.model.CommerceChannel;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + COREntryPortletKeys.COR_ENTRY,
		"mvc.command.name=/cor_entry/edit_cor_entry_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCOREntryQualifiersMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCOREntryQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	private void _deleteAccountEntryCOREntryRels(long corEntryId)
		throws Exception {

		int accountEntryCOREntryRelsCount =
			_corEntryRelService.getAccountEntryCOREntryRelsCount(
				corEntryId, null);

		if (accountEntryCOREntryRelsCount == 0) {
			return;
		}

		_corEntryRelService.deleteCOREntryRels(
			AccountEntry.class.getName(), corEntryId);
	}

	private void _deleteAccountGroupCOREntryRels(long corEntryId)
		throws Exception {

		int accountGroupCOREntryRelsCount =
			_corEntryRelService.getAccountGroupCOREntryRelsCount(
				corEntryId, null);

		if (accountGroupCOREntryRelsCount == 0) {
			return;
		}

		_corEntryRelService.deleteCOREntryRels(
			AccountGroup.class.getName(), corEntryId);
	}

	private void _updateCOREntryQualifiers(ActionRequest actionRequest)
		throws Exception {

		long corEntryId = ParamUtil.getLong(actionRequest, "corEntryId");

		String accountQualifiers = ParamUtil.getString(
			actionRequest, "accountQualifiers");

		if (Objects.equals(accountQualifiers, "all")) {
			_deleteAccountEntryCOREntryRels(corEntryId);
			_deleteAccountGroupCOREntryRels(corEntryId);
		}
		else if (Objects.equals(accountQualifiers, "accounts")) {
			_deleteAccountGroupCOREntryRels(corEntryId);
		}
		else {
			_deleteAccountEntryCOREntryRels(corEntryId);
		}

		String channelQualifiers = ParamUtil.getString(
			actionRequest, "channelQualifiers");

		if (Objects.equals(channelQualifiers, "all")) {
			_corEntryRelService.deleteCOREntryRels(
				CommerceChannel.class.getName(), corEntryId);
		}

		String orderTypeQualifiers = ParamUtil.getString(
			actionRequest, "orderTypeQualifiers");

		if (Objects.equals(orderTypeQualifiers, "all")) {
			_corEntryRelService.deleteCOREntryRels(
				CommerceOrderType.class.getName(), corEntryId);
		}
	}

	@Reference
	private COREntryRelService _corEntryRelService;

}