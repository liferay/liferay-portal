/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.exception.IncompleteWorkflowInstancesException;
import com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil;
import com.liferay.portal.workflow.web.internal.display.context.helper.WorkflowDefinitionRequestHelper;
import com.liferay.portal.workflow.web.internal.search.WorkflowDefinitionSearch;
import com.liferay.portal.workflow.web.internal.search.WorkflowDefinitionSearchTerms;
import com.liferay.portal.workflow.web.internal.util.WorkflowDefinitionPortletUtil;
import com.liferay.portal.workflow.web.internal.util.filter.WorkflowDefinitionActivePredicate;
import com.liferay.portal.workflow.web.internal.util.filter.WorkflowDefinitionDescriptionPredicate;
import com.liferay.portal.workflow.web.internal.util.filter.WorkflowDefinitionScopePredicate;
import com.liferay.portal.workflow.web.internal.util.filter.WorkflowDefinitionTitlePredicate;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionDisplayContext {

	public WorkflowDefinitionDisplayContext(
		CTEntryLocalService ctEntryLocalService, Portal portal,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest, ResourceBundleLoader resourceBundleLoader,
		UserLocalService userLocalService) {

		_ctEntryLocalService = ctEntryLocalService;
		_portal = portal;
		_portletResourcePermission = portletResourcePermission;
		_resourceBundleLoader = resourceBundleLoader;
		_userLocalService = userLocalService;

		_workflowDefinitionRequestHelper = new WorkflowDefinitionRequestHelper(
			renderRequest);
	}

	public boolean canPublishWorkflowDefinition() {
		ThemeDisplay themeDisplay =
			_workflowDefinitionRequestHelper.getThemeDisplay();

		return _portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			themeDisplay.getCompanyGroupId(), ActionKeys.ADD_DEFINITION);
	}

	public Date getCreatedDate(WorkflowDefinition workflowDefinition)
		throws PortalException {

		List<WorkflowDefinition> workflowDefinitions = getWorkflowDefinitions(
			workflowDefinition.getName());

		WorkflowDefinition firstWorkflowDefinition = workflowDefinitions.get(0);

		return firstWorkflowDefinition.getModifiedDate();
	}

	public String getCreatorUserName(WorkflowDefinition workflowDefinition)
		throws PortalException {

		List<WorkflowDefinition> workflowDefinitions = getWorkflowDefinitions(
			workflowDefinition.getName());

		return getUserName(workflowDefinitions.get(0));
	}

	public String getDescription(WorkflowDefinition workflowDefinition) {
		return HtmlUtil.escape(workflowDefinition.getDescription());
	}

	public String getDuplicateTitle(WorkflowDefinition workflowDefinition) {
		if (workflowDefinition == null) {
			return StringPool.BLANK;
		}

		String defaultLanguageId = LocalizationUtil.getDefaultLanguageId(
			workflowDefinition.getTitle());

		return LocalizationUtil.updateLocalization(
			workflowDefinition.getTitle(), "title",
			LanguageUtil.format(
				getResourceBundle(), "copy-of-x",
				workflowDefinition.getTitle(defaultLanguageId)),
			defaultLanguageId);
	}

	public String getManageSubmissionsLink() {
		return _buildErrorLink(
			"configure-submissions", _getWorkflowInstancesPortletURL());
	}

	public Object[] getMessageArguments(
			IncompleteWorkflowInstancesException
				incompleteWorkflowInstancesException)
		throws PortletException {

		return new Object[] {
			String.valueOf(
				incompleteWorkflowInstancesException.
					getWorkflowInstancesCount()),
			getManageSubmissionsLink()
		};
	}

	public Object[] getMessageArguments(
			RequiredWorkflowDefinitionException
				requiredWorkflowDefinitionException)
		throws PortletException {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return new Object[0];
		}
		else if (workflowDefinitionLinks.size() == 1) {
			WorkflowDefinitionLink workflowDefinitionLink =
				workflowDefinitionLinks.get(0);

			return new Object[] {
				_getLocalizedAssetName(workflowDefinitionLink.getClassName())
			};
		}
		else if (workflowDefinitionLinks.size() == 2) {
			WorkflowDefinitionLink workflowDefinitionLink1 =
				workflowDefinitionLinks.get(0);
			WorkflowDefinitionLink workflowDefinitionLink2 =
				workflowDefinitionLinks.get(1);

			return new Object[] {
				_getLocalizedAssetName(workflowDefinitionLink1.getClassName()),
				_getLocalizedAssetName(workflowDefinitionLink2.getClassName())
			};
		}

		WorkflowDefinitionLink workflowDefinitionLink1 =
			workflowDefinitionLinks.get(0);
		WorkflowDefinitionLink workflowDefinitionLink2 =
			workflowDefinitionLinks.get(1);

		return new Object[] {
			_getLocalizedAssetName(workflowDefinitionLink1.getClassName()),
			_getLocalizedAssetName(workflowDefinitionLink2.getClassName()),
			workflowDefinitionLinks.size() - 2
		};
	}

	public String getMessageKey(
		IncompleteWorkflowInstancesException
			incompleteWorkflowInstancesException) {

		if (incompleteWorkflowInstancesException.getWorkflowInstancesCount() ==
				1) {

			return "there-is-x-unresolved-workflow-submission-x";
		}

		return "there-are-x-unresolved-workflow-submissions-x";
	}

	public String getMessageKey(
		RequiredWorkflowDefinitionException
			requiredWorkflowDefinitionException) {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return StringPool.BLANK;
		}
		else if (workflowDefinitionLinks.size() == 1) {
			return "workflow-is-in-use.-remove-its-assignment-to-x";
		}
		else if (workflowDefinitionLinks.size() == 2) {
			return "workflow-is-in-use.-remove-its-assignment-to-x-and-x";
		}

		return "workflow-is-in-use.-remove-its-assignment-to-x-x-and-x-more";
	}

	public String getName(WorkflowDefinition workflowDefinition) {
		return HtmlUtil.escape(workflowDefinition.getName());
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_workflowDefinitionRequestHelper.getRequest(),
			WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
			"definition-order-by-col", "last-modified");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_workflowDefinitionRequestHelper.getRequest(),
			WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
			"definition-link-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<WorkflowDefinition> getSearch(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			int status)
		throws PortalException {

		if (Objects.nonNull(_workflowDefinitionSearch)) {
			return _workflowDefinitionSearch;
		}

		_workflowDefinitionSearch = new WorkflowDefinitionSearch(
			renderRequest, _getPortletURL(httpServletRequest));

		_workflowDefinitionSearch.setEmptyResultsMessage(
			"no-workflow-definitions-are-defined");

		List<WorkflowDefinition> workflowDefinitions =
			WorkflowDefinitionManagerUtil.liberalGetLatestWorkflowDefinitions(
				_workflowDefinitionRequestHelper.getCompanyId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_getWorkflowDefinitionOrderByComparator());

		if (!CTCollectionThreadLocal.isProductionMode() &&
			_ctEntryLocalService.hasCTEntries(
				CTCollectionThreadLocal.getCTCollectionId(),
				_portal.getClassNameId(WorkflowDefinition.class.getName()))) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				workflowDefinitions.addAll(
					WorkflowDefinitionManagerUtil.
						liberalGetLatestWorkflowDefinitions(
							_workflowDefinitionRequestHelper.getCompanyId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							_getWorkflowDefinitionOrderByComparator()));
			}
		}

		WorkflowDefinitionSearchTerms searchTerms =
			new WorkflowDefinitionSearchTerms(renderRequest);

		if (searchTerms.isAdvancedSearch()) {
			workflowDefinitions = filter(
				workflowDefinitions, searchTerms.getDescription(),
				searchTerms.getTitle(), status, searchTerms.isAndOperator());
		}
		else {
			workflowDefinitions = filter(
				workflowDefinitions, searchTerms.getKeywords(),
				searchTerms.getKeywords(), status, false);
		}

		List<WorkflowDefinition> filteredWorkflowDefinitions =
			workflowDefinitions;

		_workflowDefinitionSearch.setResultsAndTotal(
			() -> {
				if (filteredWorkflowDefinitions.size() >
						(_workflowDefinitionSearch.getEnd() -
							_workflowDefinitionSearch.getStart())) {

					return ListUtil.subList(
						filteredWorkflowDefinitions,
						_workflowDefinitionSearch.getStart(),
						_workflowDefinitionSearch.getEnd());
				}

				return filteredWorkflowDefinitions;
			},
			filteredWorkflowDefinitions.size());

		return _workflowDefinitionSearch;
	}

	public String getTitle(WorkflowDefinition workflowDefinition) {
		if (workflowDefinition == null) {
			return _getLanguage("new-workflow");
		}

		if (Validator.isNull(workflowDefinition.getTitle())) {
			return _getLanguage("untitled-workflow");
		}

		ThemeDisplay themeDisplay =
			_workflowDefinitionRequestHelper.getThemeDisplay();

		return HtmlUtil.escape(
			workflowDefinition.getTitle(themeDisplay.getLanguageId()));
	}

	public String getUserName(WorkflowDefinition workflowDefinition) {
		User user = _userLocalService.fetchUser(workflowDefinition.getUserId());

		if ((user == null) || user.isGuestUser() ||
			Validator.isNull(user.getFullName())) {

			return null;
		}

		return user.getFullName();
	}

	public String getUserNameOrBlank(WorkflowDefinition workflowDefinition) {
		String userName = getUserName(workflowDefinition);

		if (userName == null) {
			userName = StringPool.BLANK;
		}

		return userName;
	}

	public List<WorkflowDefinition> getWorkflowDefinitions(String name)
		throws PortalException {

		return WorkflowDefinitionManagerUtil.liberalGetWorkflowDefinitions(
			_workflowDefinitionRequestHelper.getCompanyId(), name,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public int getWorkflowDefinitionsCount(
			WorkflowDefinition workflowDefinition)
		throws PortalException {

		return WorkflowDefinitionManagerUtil.getWorkflowDefinitionsCount(
			_workflowDefinitionRequestHelper.getCompanyId(),
			workflowDefinition.getName());
	}

	public List<WorkflowDefinition> getWorkflowDefinitionsOrderByDesc(
			String name)
		throws PortalException {

		List<WorkflowDefinition> workflowDefinitions = getWorkflowDefinitions(
			name);

		if (workflowDefinitions.size() <= 1) {
			return workflowDefinitions;
		}

		Collections.reverse(workflowDefinitions);

		return workflowDefinitions;
	}

	protected Predicate<WorkflowDefinition> createPredicate(
		String description, String title, int status, boolean andOperator) {

		Predicate<WorkflowDefinition> predicate =
			new WorkflowDefinitionScopePredicate(
				WorkflowDefinitionConstants.SCOPE_ALL);

		if ((status == WorkflowConstants.STATUS_ANY) &&
			Validator.isNull(description) && Validator.isNull(title)) {

			return predicate;
		}

		predicate = predicate.and(new WorkflowDefinitionTitlePredicate(title));

		if (andOperator) {
			predicate = predicate.and(
				new WorkflowDefinitionDescriptionPredicate(description));
		}
		else {
			predicate = predicate.or(
				new WorkflowDefinitionDescriptionPredicate(description));
		}

		return predicate.and(new WorkflowDefinitionActivePredicate(status));
	}

	protected List<WorkflowDefinition> filter(
		List<WorkflowDefinition> workflowDefinitions, String description,
		String title, int status, boolean andOperator) {

		return ListUtil.filter(
			workflowDefinitions,
			createPredicate(description, title, status, andOperator));
	}

	protected ResourceBundle getResourceBundle() {
		return _resourceBundleLoader.loadResourceBundle(
			_workflowDefinitionRequestHelper.getLocale());
	}

	private String _buildErrorLink(String messageKey, PortletURL portletURL) {
		return StringUtil.replace(
			_HTML, new String[] {"[$RENDER_URL$]", "[$MESSAGE$]"},
			new String[] {
				portletURL.toString(),
				LanguageUtil.get(getResourceBundle(), messageKey)
			});
	}

	private String _getLanguage(String key) {
		return LanguageUtil.get(getResourceBundle(), key);
	}

	private String _getLocalizedAssetName(String className) {
		return ResourceActionsUtil.getModelResource(
			_workflowDefinitionRequestHelper.getLocale(), className);
	}

	private PortletURL _getPortletURL(HttpServletRequest httpServletRequest) {
		LiferayPortletResponse liferayPortletResponse =
			_workflowDefinitionRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		if (httpServletRequest == null) {
			return portletURL;
		}

		String definitionsNavigation = ParamUtil.getString(
			httpServletRequest, "definitionsNavigation");

		if (Validator.isNotNull(definitionsNavigation)) {
			portletURL.setParameter(
				"definitionsNavigation", definitionsNavigation);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	private OrderByComparator<WorkflowDefinition>
		_getWorkflowDefinitionOrderByComparator() {

		return WorkflowDefinitionPortletUtil.
			getWorkflowDefitionOrderByComparator(
				ParamUtil.getString(
					_workflowDefinitionRequestHelper.getRequest(), "orderByCol",
					"name"),
				getOrderByType(), _workflowDefinitionRequestHelper.getLocale());
	}

	private PortletURL _getWorkflowInstancesPortletURL() {
		return PortletURLBuilder.createLiferayPortletURL(
			_workflowDefinitionRequestHelper.getLiferayPortletResponse(),
			WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE,
			PortletRequest.RENDER_PHASE
		).setMVCPath(
			"/view.jsp"
		).buildPortletURL();
	}

	private static final String _HTML =
		"<a class='alert-link' href='[$RENDER_URL$]'>[$MESSAGE$]</a>";

	private final CTEntryLocalService _ctEntryLocalService;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private final PortletResourcePermission _portletResourcePermission;
	private final ResourceBundleLoader _resourceBundleLoader;
	private final UserLocalService _userLocalService;
	private final WorkflowDefinitionRequestHelper
		_workflowDefinitionRequestHelper;
	private WorkflowDefinitionSearch _workflowDefinitionSearch;

}