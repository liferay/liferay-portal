/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.portlet.action;

import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.web.internal.helper.BatchPlannerPlanHelper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Beslic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
		"mvc.command.name=/batch_planner/edit_export_batch_planner_plan_template"
	},
	service = MVCActionCommand.class
)
public class EditExportBatchPlannerPlanTemplateMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD)) {
			_addBatchPlannerPlan(actionRequest, actionResponse);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			_batchPlannerPlanHelper.updateExportBatchPlannerPlan(actionRequest);
		}
	}

	private void _addBatchPlannerPlan(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			BatchPlannerPlan batchPlannerPlan =
				_batchPlannerPlanHelper.addExportBatchPlannerPlan(
					actionRequest,
					ParamUtil.getString(actionRequest, "templateName"));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"batchPlannerPlanId",
					batchPlannerPlan.getBatchPlannerPlanId()
				).put(
					"name", batchPlannerPlan.getName()
				).put(
					"success", Boolean.TRUE
				));
		}
		catch (Exception exception) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("error", exception.getMessage()));
		}
	}

	@Reference
	private BatchPlannerPlanHelper _batchPlannerPlanHelper;

}