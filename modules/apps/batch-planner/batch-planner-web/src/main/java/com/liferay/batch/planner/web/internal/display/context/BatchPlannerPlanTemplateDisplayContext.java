/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.display.context;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.service.BatchPlannerPlanServiceUtil;
import com.liferay.batch.planner.web.internal.display.BatchPlannerPlanTemplateDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

/**
 * @author Matija Petanjek
 */
public class BatchPlannerPlanTemplateDisplayContext extends BaseDisplayContext {

	public BatchPlannerPlanTemplateDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(renderRequest, renderResponse);
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/batch_planner/view_batch_planner_plan_templates"
		).setNavigation(
			ParamUtil.getString(renderRequest, "navigation", "all")
		).setTabs1(
			"batch-planner-plan-templates"
		).setParameter(
			"delta", () -> ParamUtil.getString(renderRequest, "delta")
		).buildPortletURL();
	}

	public SearchContainer<BatchPlannerPlanTemplateDisplay>
		getSearchContainer() {

		try {
			return _getSearchContainer();
		}
		catch (Exception exception) {
			Class<? extends Exception> clazz = exception.getClass();

			SessionErrors.add(renderRequest, clazz.getName());
		}

		return new SearchContainer<>(
			renderRequest, getPortletURL(), null, "no-items-were-found");
	}

	private String _getAction(boolean export) {
		if (export) {
			return "export";
		}

		return "import";
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, BatchPlannerPortletKeys.BATCH_PLANNER,
			"template-order-by-col", "modifiedDate");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, BatchPlannerPortletKeys.BATCH_PLANNER,
			"template-order-by-type", "desc");

		return _orderByType;
	}

	private SearchContainer<BatchPlannerPlanTemplateDisplay>
			_getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			renderRequest, getPortletURL(), null, "no-items-were-found");

		_searchContainer.setId(
			"batchPlannerPlanTemplateDisplaySearchContainer");
		_searchContainer.setOrderByCol(_getOrderByCol());
		_searchContainer.setOrderByType(_getOrderByType());

		String navigation = ParamUtil.getString(
			renderRequest, "navigation", "all");

		long companyId = PortalUtil.getCompanyId(renderRequest);

		String searchByKeyword = ParamUtil.getString(renderRequest, "keywords");

		if (navigation.equals("all")) {
			_searchContainer.setResultsAndTotal(
				() -> TransformUtil.transform(
					BatchPlannerPlanServiceUtil.getBatchPlannerPlans(
						companyId, true, searchByKeyword,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						OrderByComparatorFactoryUtil.create(
							"BatchPlannerPlan",
							_searchContainer.getOrderByCol(),
							Objects.equals(
								_searchContainer.getOrderByType(), "asc"))),
					this::_toBatchPlannerPlanTemplateDisplay),
				BatchPlannerPlanServiceUtil.getBatchPlannerPlansCount(
					companyId, true, searchByKeyword));
		}
		else {
			boolean export = isExport(navigation);

			_searchContainer.setResultsAndTotal(
				() -> TransformUtil.transform(
					BatchPlannerPlanServiceUtil.getBatchPlannerPlans(
						companyId, export, true, searchByKeyword,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						OrderByComparatorFactoryUtil.create(
							"BatchPlannerPlan",
							_searchContainer.getOrderByCol(),
							Objects.equals(
								_searchContainer.getOrderByType(), "asc"))),
					this::_toBatchPlannerPlanTemplateDisplay),
				BatchPlannerPlanServiceUtil.getBatchPlannerPlansCount(
					companyId, export, true, searchByKeyword));
		}

		_searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(renderResponse));

		return _searchContainer;
	}

	private BatchPlannerPlanTemplateDisplay _toBatchPlannerPlanTemplateDisplay(
			BatchPlannerPlan batchPlannerPlan)
		throws PortalException {

		return new BatchPlannerPlanTemplateDisplay.Builder(
		).action(
			_getAction(batchPlannerPlan.isExport())
		).batchPlannerPlanId(
			batchPlannerPlan.getBatchPlannerPlanId()
		).createDate(
			batchPlannerPlan.getCreateDate()
		).export(
			batchPlannerPlan.isExport()
		).externalType(
			batchPlannerPlan.getExternalType()
		).internalClassNameKey(
			TaskItemUtil.getInternalClassNameKey(
				batchPlannerPlan.getInternalClassName(),
				batchPlannerPlan.getTaskItemDelegateName())
		).title(
			batchPlannerPlan.getName()
		).userName(
			batchPlannerPlan.getUserName()
		).build();
	}

	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<BatchPlannerPlanTemplateDisplay> _searchContainer;

}