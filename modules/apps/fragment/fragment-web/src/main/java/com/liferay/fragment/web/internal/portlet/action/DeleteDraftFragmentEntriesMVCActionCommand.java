/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/delete_draft_fragment_entries"
	},
	service = MVCActionCommand.class
)
public class DeleteDraftFragmentEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		long fragmentEntryId = ParamUtil.getLong(
			actionRequest, "fragmentEntryId");

		FragmentEntry fragmentEntry = _fragmentEntryService.fetchFragmentEntry(
			fragmentEntryId);

		if (fragmentEntry == null) {
			return;
		}

		if (fragmentEntry.isDraft()) {
			_fragmentEntryService.deleteFragmentEntry(
				fragmentEntry.getFragmentEntryId());
		}
		else {
			FragmentEntry draftFragmentEntry =
				fragmentEntry.fetchDraftFragmentEntry();

			if (draftFragmentEntry != null) {
				_fragmentEntryService.deleteFragmentEntry(
					draftFragmentEntry.getFragmentEntryId());
			}
		}
	}

	@Reference
	private FragmentEntryService _fragmentEntryService;

}