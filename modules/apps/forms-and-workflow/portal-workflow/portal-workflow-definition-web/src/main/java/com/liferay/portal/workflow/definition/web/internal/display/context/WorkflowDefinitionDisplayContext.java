/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.definition.web.internal.display.context;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregatePredicateFilter;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PredicateFilter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.workflow.definition.web.internal.display.context.util.WorkflowDefinitionRequestHelper;
import com.liferay.portal.workflow.definition.web.internal.search.WorkflowDefinitionSearchTerms;
import com.liferay.portal.workflow.definition.web.internal.util.WorkflowDefinitionPortletUtil;
import com.liferay.portal.workflow.definition.web.internal.util.filter.WorkflowDefinitionNamePredicateFilter;
import com.liferay.portal.workflow.definition.web.internal.util.filter.WorkflowDefinitionTitlePredicateFilter;

import java.util.List;

import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionDisplayContext {

	public WorkflowDefinitionDisplayContext(RenderRequest renderRequest) {
		_workflowDefinitionRequestHelper = new WorkflowDefinitionRequestHelper(
			renderRequest);
	}

	public boolean canPublishWorkflowDefinition() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (_companyAdministratorCanPublish &&
			permissionChecker.isCompanyAdmin()) {

			return true;
		}

		if (permissionChecker.isOmniadmin()) {
			return true;
		}

		return false;
	}

	public String getActive(WorkflowDefinition workflowDefinition) {
		HttpServletRequest request =
			_workflowDefinitionRequestHelper.getRequest();

		if (workflowDefinition.isActive()) {
			return LanguageUtil.get(request, "yes");
		}

		return LanguageUtil.get(request, "no");
	}

	public String getName(WorkflowDefinition workflowDefinition) {
		return HtmlUtil.escape(workflowDefinition.getName());
	}

	public List<WorkflowDefinition> getSearchContainerResults(
			SearchContainer<WorkflowDefinition> searchContainer)
		throws PortalException {

		List<WorkflowDefinition> workflowDefinitions =
			WorkflowDefinitionManagerUtil.getWorkflowDefinitions(
				_workflowDefinitionRequestHelper.getCompanyId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getWorkflowDefinitionOrderByComparator());

		WorkflowDefinitionSearchTerms searchTerms =
			(WorkflowDefinitionSearchTerms)searchContainer.getSearchTerms();

		if (searchTerms.isAdvancedSearch()) {
			workflowDefinitions = filter(
				workflowDefinitions, searchTerms.getName(),
				searchTerms.getTitle(), searchTerms.isAndOperator());
		}
		else {
			workflowDefinitions = filter(
				workflowDefinitions, searchTerms.getKeywords(),
				searchTerms.getKeywords(), false);
		}

		searchContainer.setTotal(workflowDefinitions.size());

		if (workflowDefinitions.size() >
				(searchContainer.getEnd() - searchContainer.getStart())) {

			workflowDefinitions = ListUtil.subList(
				workflowDefinitions, searchContainer.getStart(),
				searchContainer.getEnd());
		}

		return workflowDefinitions;
	}

	public String getTitle(WorkflowDefinition workflowDefinition) {
		ThemeDisplay themeDisplay =
			_workflowDefinitionRequestHelper.getThemeDisplay();

		return HtmlUtil.escape(
			workflowDefinition.getTitle(themeDisplay.getLanguageId()));
	}

	public String getVersion(WorkflowDefinition workflowDefinition) {
		return String.valueOf(workflowDefinition.getVersion());
	}

	public void setCompanyAdministratorCanPublish(
		boolean companyAdministratorCanPublish) {

		_companyAdministratorCanPublish = companyAdministratorCanPublish;
	}

	protected PredicateFilter<WorkflowDefinition> createPredicateFilter(
		String name, String title, boolean andOperator) {

		AggregatePredicateFilter<WorkflowDefinition> aggregatePredicateFilter =
			new AggregatePredicateFilter<>(
				new WorkflowDefinitionNamePredicateFilter(name));

		if (andOperator) {
			aggregatePredicateFilter.and(
				new WorkflowDefinitionTitlePredicateFilter(title));
		}
		else {
			aggregatePredicateFilter.or(
				new WorkflowDefinitionTitlePredicateFilter(title));
		}

		return aggregatePredicateFilter;
	}

	protected List<WorkflowDefinition> filter(
		List<WorkflowDefinition> workflowDefinitions, String name, String title,
		boolean andOperator) {

		if (Validator.isNull(name) && Validator.isNull(title)) {
			return workflowDefinitions;
		}

		PredicateFilter<WorkflowDefinition> predicateFilter =
			createPredicateFilter(name, title, andOperator);

		return ListUtil.filter(workflowDefinitions, predicateFilter);
	}

	protected OrderByComparator<WorkflowDefinition>
		getWorkflowDefinitionOrderByComparator() {

		String orderByCol = ParamUtil.getString(
			_workflowDefinitionRequestHelper.getRequest(), "orderByCol",
			"name");

		String orderByType = ParamUtil.getString(
			_workflowDefinitionRequestHelper.getRequest(), "orderByType",
			"asc");

		return WorkflowDefinitionPortletUtil.
			getWorkflowDefitionOrderByComparator(
				orderByCol, orderByType,
				_workflowDefinitionRequestHelper.getLocale());
	}

	private boolean _companyAdministratorCanPublish;
	private final WorkflowDefinitionRequestHelper
		_workflowDefinitionRequestHelper;

}