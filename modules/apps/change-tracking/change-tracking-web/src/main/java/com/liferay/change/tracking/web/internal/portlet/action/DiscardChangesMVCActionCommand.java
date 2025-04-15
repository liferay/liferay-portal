/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/discard_changes"
	},
	service = MVCActionCommand.class
)
public class DiscardChangesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			actionRequest, "ctCollectionId");
		long[] ctEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "ctEntryIds"), 0L);
		long modelClassNameId = ParamUtil.getLong(
			actionRequest, "modelClassNameId");
		long modelClassPK = ParamUtil.getLong(actionRequest, "modelClassPK");

		List<CTEntry> ctEntries = new ArrayList<>();

		if ((modelClassNameId > 0) && (modelClassPK > 0)) {
			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
				ctCollectionId, modelClassNameId, modelClassPK);

			ctEntries.add(ctEntry);
		}

		for (long ctEntryId : ctEntryIds) {
			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(ctEntryId);

			ctEntries.add(ctEntry);
		}

		_ctCollectionService.discardCTEntry(ctCollectionId, ctEntries);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

}