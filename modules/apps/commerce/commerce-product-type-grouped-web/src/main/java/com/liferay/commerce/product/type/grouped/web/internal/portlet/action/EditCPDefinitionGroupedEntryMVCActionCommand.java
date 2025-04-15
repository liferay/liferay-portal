/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_grouped_entry"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionGroupedEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD)) {
			_addCPDefinitionGroupedEntries(actionRequest);
		}
		else if (cmd.equals(Constants.DELETE)) {
			_deleteCPDefinitionGroupedEntries(actionRequest);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			_updateCPDefinitionGroupedEntry(actionRequest);
		}
	}

	private void _addCPDefinitionGroupedEntries(ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		long[] entryCPDefinitionIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "entryCPDefinitionIds"), 0L);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionGroupedEntry.class.getName(), actionRequest);

		_cpDefinitionGroupedEntryService.addCPDefinitionGroupedEntries(
			cpDefinitionId, entryCPDefinitionIds, serviceContext);
	}

	private void _deleteCPDefinitionGroupedEntries(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPDefinitionGroupedEntryIds = null;

		long cpDefinitionGroupedEntryId = ParamUtil.getLong(
			actionRequest, "cpDefinitionGroupedEntryId");

		if (cpDefinitionGroupedEntryId > 0) {
			deleteCPDefinitionGroupedEntryIds = new long[] {
				cpDefinitionGroupedEntryId
			};
		}
		else {
			deleteCPDefinitionGroupedEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCPDefinitionGroupedEntryId :
				deleteCPDefinitionGroupedEntryIds) {

			_cpDefinitionGroupedEntryService.deleteCPDefinitionGroupedEntry(
				deleteCPDefinitionGroupedEntryId);
		}
	}

	private CPDefinitionGroupedEntry _updateCPDefinitionGroupedEntry(
			ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionGroupedEntryId = ParamUtil.getLong(
			actionRequest, "cpDefinitionGroupedEntryId");

		double priority = ParamUtil.getDouble(actionRequest, "priority");
		int quantity = ParamUtil.getInteger(actionRequest, "quantity");

		return _cpDefinitionGroupedEntryService.updateCPDefinitionGroupedEntry(
			cpDefinitionGroupedEntryId, priority, quantity);
	}

	@Reference
	private CPDefinitionGroupedEntryService _cpDefinitionGroupedEntryService;

}