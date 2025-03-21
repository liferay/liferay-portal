/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.portlet.action;

import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.service.BatchPlannerPlanService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
		"mvc.command.name=/batch_planner/delete_batch_planner_plan_template"
	},
	service = MVCActionCommand.class
)
public class DeleteBatchPlannerPlanTemplateMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long batchPlannerPlanId = ParamUtil.getLong(
			actionRequest, "batchPlannerPlanId");

		if (batchPlannerPlanId != 0) {
			_batchPlannerPlanService.deleteBatchPlannerPlan(batchPlannerPlanId);
		}
		else {
			_deleteBatchPlannerPlans(
				ParamUtil.getLongValues(actionRequest, "batchPlannerPlanIds"));
		}
	}

	private void _deleteBatchPlannerPlans(long[] batchPlannerPlanIds)
		throws Exception {

		for (long batchPlannerPlanId : batchPlannerPlanIds) {
			_batchPlannerPlanService.deleteBatchPlannerPlan(batchPlannerPlanId);
		}
	}

	@Reference
	private BatchPlannerPlanService _batchPlannerPlanService;

}