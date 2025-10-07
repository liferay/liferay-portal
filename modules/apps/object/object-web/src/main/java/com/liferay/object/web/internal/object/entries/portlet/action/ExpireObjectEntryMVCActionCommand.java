/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Jhosseph Gonzalez
 */
public class ExpireObjectEntryMVCActionCommand extends BaseMVCActionCommand {

	public ExpireObjectEntryMVCActionCommand(
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryService objectEntryService) {

		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryService = objectEntryService;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long objectEntryId = ParamUtil.getLong(actionRequest, "objectEntryId");

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		int status = objectEntry.getStatus();

		if ((status == WorkflowConstants.STATUS_DRAFT) ||
			(status == WorkflowConstants.STATUS_PENDING)) {

			return;
		}

		_objectEntryService.expireObjectEntry(
			objectEntryId,
			ServiceContextFactory.getInstance(
				ObjectEntry.class.getName(), actionRequest));
	}

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryService _objectEntryService;

}