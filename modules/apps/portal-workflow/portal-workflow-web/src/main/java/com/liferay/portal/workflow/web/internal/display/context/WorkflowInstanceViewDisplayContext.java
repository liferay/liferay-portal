/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowLog;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.manager.WorkflowLogManager;
import com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil;
import com.liferay.portal.workflow.web.internal.search.WorkflowInstanceSearch;
import com.liferay.portal.workflow.web.internal.util.WorkflowInstancePortletUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Leonardo Barros
 */
public class WorkflowInstanceViewDisplayContext
	extends BaseWorkflowInstanceDisplayContext {

	public WorkflowInstanceViewDisplayContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			WorkflowComparatorFactory workflowComparatorFactory,
			WorkflowLogManager workflowLogManager)
		throws PortalException {

		super(liferayPortletRequest, liferayPortletResponse);

		_liferayPortletRequest = liferayPortletRequest;

		_workflowComparatorFactory = workflowComparatorFactory;
		_workflowLogManager = workflowLogManager;
	}

	public String getAssetIconCssClass(WorkflowInstance workflowInstance) {
		WorkflowHandler<?> workflowHandler = getWorkflowHandler(
			workflowInstance);

		return workflowHandler.getIconCssClass();
	}

	public String getAssetTitle(WorkflowInstance workflowInstance) {
		WorkflowHandler<?> workflowHandler = getWorkflowHandler(
			workflowInstance);

		String title = workflowHandler.getTitle(
			getWorkflowContextEntryClassPK(
				workflowInstance.getWorkflowContext()),
			workflowInstanceRequestHelper.getLocale());

		if (title != null) {
			return HtmlUtil.escape(title);
		}

		return getAssetType(workflowInstance);
	}

	public String getAssetType(WorkflowInstance workflowInstance) {
		WorkflowHandler<?> workflowHandler = getWorkflowHandler(
			workflowInstance);

		return workflowHandler.getType(
			workflowInstanceRequestHelper.getLocale());
	}

	public String getDefinition(WorkflowInstance workflowInstance)
		throws PortalException {

		WorkflowDefinition workflowDefinition =
			WorkflowDefinitionManagerUtil.liberalGetWorkflowDefinition(
				workflowInstanceRequestHelper.getCompanyId(),
				workflowInstance.getWorkflowDefinitionName(),
				workflowInstance.getWorkflowDefinitionVersion());

		return HtmlUtil.escape(
			workflowDefinition.getTitle(
				LanguageUtil.getLanguageId(
					workflowInstanceRequestHelper.getRequest())));
	}

	public String getDisplayStyle() {
		if (_displayStyle == null) {
			_displayStyle = WorkflowInstancePortletUtil.getDisplayStyle(
				liferayPortletRequest, new String[] {"descriptive", "list"});
		}

		return _displayStyle;
	}

	public Date getEndDate(WorkflowInstance workflowInstance) {
		return workflowInstance.getEndDate();
	}

	public String getHeaderTitle() {
		return "workflow-submissions";
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(liferayPortletRequest, "keywords");

		return _keywords;
	}

	public Date getLastActivityDate(WorkflowInstance workflowInstance)
		throws PortalException {

		WorkflowLog workflowLog = _getLatestWorkflowLog(workflowInstance);

		if (workflowLog == null) {
			return null;
		}

		return workflowLog.getCreateDate();
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, WorkflowPortletKeys.USER_WORKFLOW,
			"instance-order-by-col", "last-activity-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, WorkflowPortletKeys.USER_WORKFLOW,
			"instance-order-by-type", "asc");

		return _orderByType;
	}

	public WorkflowInstanceSearch getSearchContainer() throws PortalException {
		if (Objects.nonNull(_searchContainer)) {
			return _searchContainer;
		}

		_searchContainer = new WorkflowInstanceSearch(
			liferayPortletRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			_workflowComparatorFactory);

		WorkflowModelSearchResult<WorkflowInstance> workflowModelSearchResult =
			getWorkflowModelSearchResult(
				_searchContainer.getStart(), _searchContainer.getEnd(),
				_searchContainer.getOrderByComparator());

		setSearchContainerEmptyResultsMessage(_searchContainer);

		_searchContainer.setResultsAndTotal(
			workflowModelSearchResult::getWorkflowModels,
			workflowModelSearchResult.getLength());

		return _searchContainer;
	}

	public List<WorkflowHandler<?>> getSearchableAssetsWorkflowHandlers() {
		List<WorkflowHandler<?>> searchableAssetsWorkflowHandlers =
			new ArrayList<>();

		List<WorkflowHandler<?>> workflowHandlers =
			WorkflowHandlerRegistryUtil.getWorkflowHandlers();

		for (WorkflowHandler<?> workflowHandler : workflowHandlers) {
			if (workflowHandler.isAssetTypeSearchable()) {
				searchableAssetsWorkflowHandlers.add(workflowHandler);
			}
		}

		return searchableAssetsWorkflowHandlers;
	}

	public String getWorkflowContextEntryClassName(
		Map<String, Serializable> workflowContext) {

		return (String)workflowContext.get(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME);
	}

	public long getWorkflowContextEntryClassPK(
		Map<String, Serializable> workflowContext) {

		return GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));
	}

	public boolean isNavigationAll() {
		return Objects.equals(getNavigation(), "all");
	}

	public boolean isNavigationCompleted() {
		return Objects.equals(getNavigation(), "completed");
	}

	public boolean isNavigationPending() {
		return Objects.equals(getNavigation(), "pending");
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowExtraInfo() {
		if (_showExtraInfo != null) {
			return _showExtraInfo;
		}

		if (Objects.equals(
				ParamUtil.getString(_liferayPortletRequest, "type"),
				"document")) {

			_showExtraInfo = true;
		}
		else {
			_showExtraInfo = false;
		}

		return _showExtraInfo;
	}

	protected String getAssetType(String keywords) {
		for (WorkflowHandler<?> workflowHandler :
				getSearchableAssetsWorkflowHandlers()) {

			if (StringUtil.equalsIgnoreCase(
					keywords,
					workflowHandler.getType(
						workflowInstanceRequestHelper.getLocale()))) {

				return workflowHandler.getClassName();
			}
		}

		return StringPool.BLANK;
	}

	protected Boolean getCompleted() {
		if (isNavigationAll()) {
			return null;
		}

		if (isNavigationCompleted()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	protected WorkflowHandler<?> getWorkflowHandler(
		WorkflowInstance workflowInstance) {

		return WorkflowHandlerRegistryUtil.getWorkflowHandler(
			getWorkflowContextEntryClassName(
				workflowInstance.getWorkflowContext()));
	}

	protected WorkflowModelSearchResult<WorkflowInstance>
			getWorkflowModelSearchResult(
				int start, int end,
				OrderByComparator<WorkflowInstance> orderByComparator)
		throws PortalException {

		if (Objects.nonNull(workflowModelSearchResult)) {
			return workflowModelSearchResult;
		}

		workflowModelSearchResult =
			WorkflowInstanceManagerUtil.searchWorkflowInstances(
				workflowInstanceRequestHelper.getCompanyId(), null, true,
				getKeywords(), getKeywords(), getAssetType(getKeywords()),
				getKeywords(), getKeywords(), getCompleted(), true, start, end,
				orderByComparator);

		return workflowModelSearchResult;
	}

	protected void setSearchContainerEmptyResultsMessage(
		WorkflowInstanceSearch searchContainer) {

		DisplayTerms searchTerms = searchContainer.getDisplayTerms();

		if (isNavigationAll()) {
			searchContainer.setEmptyResultsMessage("there-are-no-instances");
		}
		else if (isNavigationPending()) {
			searchContainer.setEmptyResultsMessage(
				"there-are-no-pending-instances");
		}
		else {
			searchContainer.setEmptyResultsMessage(
				"there-are-no-completed-instances");
		}

		if (Validator.isNotNull(searchTerms.getKeywords())) {
			searchContainer.setEmptyResultsMessage(
				searchContainer.getEmptyResultsMessage() +
					"-with-the-specified-search-criteria");
		}
	}

	protected WorkflowModelSearchResult<WorkflowInstance>
		workflowModelSearchResult;

	private WorkflowLog _getLatestWorkflowLog(WorkflowInstance workflowInstance)
		throws PortalException {

		List<WorkflowLog> workflowLogs =
			_workflowLogManager.getWorkflowLogsByWorkflowInstance(
				workflowInstanceRequestHelper.getCompanyId(),
				workflowInstance.getWorkflowInstanceId(), null, 0, 1,
				_workflowComparatorFactory.getLogCreateDateComparator(false));

		if (workflowLogs.isEmpty()) {
			return null;
		}

		return workflowLogs.get(0);
	}

	private String _displayStyle;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private WorkflowInstanceSearch _searchContainer;
	private Boolean _showExtraInfo;
	private final WorkflowComparatorFactory _workflowComparatorFactory;
	private final WorkflowLogManager _workflowLogManager;

}