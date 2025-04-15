/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.RequiredFragmentEntryException;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/delete_fragment_compositions_and_fragment_entries"
	},
	service = MVCActionCommand.class
)
public class DeleteFragmentCompositionsAndFragmentEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteFragmentCompositionIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsFragmentComposition");

		for (long fragmentCompositionId : deleteFragmentCompositionIds) {
			_fragmentCompositionService.deleteFragmentComposition(
				fragmentCompositionId);
		}

		long[] deleteFragmentEntryIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsFragmentEntry");

		try {
			_fragmentEntryService.deleteFragmentEntries(deleteFragmentEntryIds);
		}
		catch (RequiredFragmentEntryException requiredFragmentEntryException) {
			SessionErrors.add(
				actionRequest, requiredFragmentEntryException.getClass());

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

}