/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.portlet.action;

import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.service.BatchPlannerPlanService;
import com.liferay.batch.planner.web.internal.display.BatchPlannerPlanDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joe Duffy
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
		"mvc.command.name=/batch_planner/view_batch_planner_plan"
	},
	service = MVCRenderCommand.class
)
public class ViewBatchPlannerPlanMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long batchPlannerPlanId = ParamUtil.getLong(
			renderRequest, "batchPlannerPlanId");

		try {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				_getBatchPlannerPlanDisplay(batchPlannerPlanId));

			return "/view_batch_planner_plan.jsp";
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());

			throw new PortletException(
				"Unable to render batch planner plan " + batchPlannerPlanId,
				portalException);
		}
	}

	private BatchPlannerPlanDisplay _getBatchPlannerPlanDisplay(
			long batchPlannerPlanId)
		throws PortalException {

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.getBatchPlannerPlan(batchPlannerPlanId);

		BatchPlannerPlanDisplay.Builder builder =
			new BatchPlannerPlanDisplay.Builder();

		builder.batchPlannerPlanId(
			batchPlannerPlanId
		).status(
			batchPlannerPlan.getStatus()
		).createDate(
			batchPlannerPlan.getCreateDate()
		).export(
			batchPlannerPlan.isExport()
		).processedItemsCount(
			0
		).totalItemsCount(
			batchPlannerPlan.getTotal()
		).title(
			batchPlannerPlan.getName()
		).modifiedDate(
			batchPlannerPlan.getModifiedDate()
		);

		return builder.build();
	}

	@Reference
	private BatchPlannerPlanService _batchPlannerPlanService;

}