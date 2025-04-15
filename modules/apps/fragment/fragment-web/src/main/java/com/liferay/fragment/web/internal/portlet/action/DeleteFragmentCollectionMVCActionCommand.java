/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.RequiredFragmentEntryException;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/delete_fragment_collection"
	},
	service = MVCActionCommand.class
)
public class DeleteFragmentCollectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteFragmentCollectionIds = null;

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		if (fragmentCollectionId > 0) {
			deleteFragmentCollectionIds = new long[] {fragmentCollectionId};
		}
		else {
			deleteFragmentCollectionIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		try {
			_fragmentCollectionService.deleteFragmentCollections(
				deleteFragmentCollectionIds);
		}
		catch (RequiredFragmentEntryException requiredFragmentEntryException) {
			SessionErrors.add(
				actionRequest, requiredFragmentEntryException.getClass());

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

}