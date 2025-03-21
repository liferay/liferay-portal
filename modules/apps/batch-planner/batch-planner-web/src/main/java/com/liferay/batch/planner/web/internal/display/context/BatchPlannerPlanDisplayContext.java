/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.display.context;

import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalServiceUtil;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalServiceUtil;
import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.constants.BatchPlannerPlanConstants;
import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.service.BatchPlannerPlanServiceUtil;
import com.liferay.batch.planner.web.internal.display.BatchPlannerPlanDisplay;
import com.liferay.petra.function.transform.TransformUtil;
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
public class BatchPlannerPlanDisplayContext extends BaseDisplayContext {

	public BatchPlannerPlanDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(renderRequest, renderResponse);
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setNavigation(
			ParamUtil.getString(renderRequest, "navigation", "all")
		).setTabs1(
			"batch-planner-plans"
		).setParameter(
			"delta", () -> ParamUtil.getString(renderRequest, "delta")
		).buildPortletURL();
	}

	public SearchContainer<BatchPlannerPlanDisplay> getSearchContainer() {
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
			"plan-order-by-col", "modifiedDate");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, BatchPlannerPortletKeys.BATCH_PLANNER,
			"plan-order-by-type", "desc");

		return _orderByType;
	}

	private int _getProcessedItemsCount(
		BatchEngineImportTask batchEngineImportTask,
		int batchEngineImportTaskErrorsCount) {

		if (batchEngineImportTask.getImportStrategy() ==
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL) {

			return batchEngineImportTask.getProcessedItemsCount();
		}

		return batchEngineImportTask.getTotalItemsCount() -
			batchEngineImportTaskErrorsCount;
	}

	private SearchContainer<BatchPlannerPlanDisplay> _getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			renderRequest, getPortletURL(), null, "no-items-were-found");

		_searchContainer.setOrderByCol(_getOrderByCol());
		_searchContainer.setOrderByType(_getOrderByType());

		String navigation = ParamUtil.getString(
			renderRequest, "navigation", "all");

		long companyId = PortalUtil.getCompanyId(renderRequest);
		String searchByKeyword = ParamUtil.getString(
			renderRequest, "keywords", "");

		if (navigation.equals("all")) {
			_searchContainer.setResultsAndTotal(
				() -> TransformUtil.transform(
					BatchPlannerPlanServiceUtil.getBatchPlannerPlans(
						companyId, false, searchByKeyword,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						OrderByComparatorFactoryUtil.create(
							"BatchPlannerPlan",
							_searchContainer.getOrderByCol(),
							Objects.equals(
								_searchContainer.getOrderByType(), "asc"))),
					this::_toBatchPlannerPlanDisplay),
				BatchPlannerPlanServiceUtil.getBatchPlannerPlansCount(
					companyId, false, searchByKeyword));
		}
		else {
			_searchContainer.setResultsAndTotal(
				() -> TransformUtil.transform(
					BatchPlannerPlanServiceUtil.getBatchPlannerPlans(
						companyId, isExport(navigation), false, searchByKeyword,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						OrderByComparatorFactoryUtil.create(
							"BatchPlannerPlan",
							_searchContainer.getOrderByCol(),
							Objects.equals(
								_searchContainer.getOrderByType(), "asc"))),
					this::_toBatchPlannerPlanDisplay),
				BatchPlannerPlanServiceUtil.getBatchPlannerPlansCount(
					companyId, isExport(navigation), false, searchByKeyword));
		}

		return _searchContainer;
	}

	private BatchPlannerPlanDisplay _toBatchPlannerPlanDisplay(
			BatchPlannerPlan batchPlannerPlan)
		throws PortalException {

		BatchPlannerPlanDisplay.Builder builder =
			new BatchPlannerPlanDisplay.Builder();

		builder.action(
			_getAction(batchPlannerPlan.isExport())
		).batchPlannerPlanId(
			batchPlannerPlan.getBatchPlannerPlanId()
		).createDate(
			batchPlannerPlan.getCreateDate()
		).export(
			batchPlannerPlan.isExport()
		).internalClassNameKey(
			TaskItemUtil.getInternalClassNameKey(
				batchPlannerPlan.getInternalClassName(),
				batchPlannerPlan.getTaskItemDelegateName())
		).title(
			batchPlannerPlan.getName()
		).userId(
			batchPlannerPlan.getUserId()
		).status(
			BatchPlannerPlanConstants.STATUS_INACTIVE
		);

		if (!batchPlannerPlan.isActive()) {
			return builder.build();
		}

		builder.status(batchPlannerPlan.getStatus());

		if (batchPlannerPlan.isExport()) {
			BatchEngineExportTask batchEngineExportTask =
				BatchEngineExportTaskLocalServiceUtil.
					getBatchEngineExportTaskByExternalReferenceCode(
						String.valueOf(
							batchPlannerPlan.getBatchPlannerPlanId()),
						batchPlannerPlan.getCompanyId());

			builder.processedItemsCount(
				batchEngineExportTask.getProcessedItemsCount()
			).totalItemsCount(
				batchEngineExportTask.getTotalItemsCount()
			);
		}
		else {
			BatchEngineImportTask batchEngineImportTask =
				BatchEngineImportTaskLocalServiceUtil.
					getBatchEngineImportTaskByExternalReferenceCode(
						String.valueOf(
							batchPlannerPlan.getBatchPlannerPlanId()),
						batchPlannerPlan.getCompanyId());

			int batchEngineImportTaskErrorsCount =
				batchEngineImportTask.getBatchEngineImportTaskErrorsCount();

			builder.failedItemsCount(
				batchEngineImportTaskErrorsCount
			).processedItemsCount(
				_getProcessedItemsCount(
					batchEngineImportTask, batchEngineImportTaskErrorsCount)
			).totalItemsCount(
				batchEngineImportTask.getTotalItemsCount()
			);
		}

		return builder.build();
	}

	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<BatchPlannerPlanDisplay> _searchContainer;

}