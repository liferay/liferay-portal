/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.type.grouped.constants.GroupedCPTypeWebKeys;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/cp_definition_grouped_entry_info_panel"
	},
	service = MVCResourceCommand.class
)
public class CPDefinitionGroupedEntryInfoPanelMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			GroupedCPTypeWebKeys.CP_DEFINITION_GROUPED_ENTRIES,
			_getCPDefinitionGroupedEntries(resourceRequest));

		include(
			resourceRequest, resourceResponse,
			"/cp_definition_grouped_entry_info_panel.jsp");
	}

	private List<CPDefinitionGroupedEntry> _getCPDefinitionGroupedEntries(
			ResourceRequest resourceRequest)
		throws Exception {

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			new ArrayList<>();

		long[] cpDefinitionGroupedEntryIds = ParamUtil.getLongValues(
			resourceRequest, "rowIds");

		for (long cpDefinitionGroupedEntryId : cpDefinitionGroupedEntryIds) {
			cpDefinitionGroupedEntries.add(
				_cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntry(
					cpDefinitionGroupedEntryId));
		}

		return cpDefinitionGroupedEntries;
	}

	@Reference
	private CPDefinitionGroupedEntryService _cpDefinitionGroupedEntryService;

}