/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowLog;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.manager.WorkflowLogManager;
import com.liferay.portal.workflow.task.web.internal.display.context.helper.WorkflowTaskRequestHelper;
import com.liferay.portal.workflow.task.web.internal.search.WorkflowTaskSearch;
import com.liferay.portal.workflow.task.web.internal.util.WorkflowTaskPortletUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Leonardo Barros
 */
public class WorkflowTaskDisplayContext {

	public WorkflowTaskDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		WorkflowComparatorFactory workflowComparatorFactory,
		WorkflowLogManager workflowLogManager) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_workflowComparatorFactory = workflowComparatorFactory;
		_workflowLogManager = workflowLogManager;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), themeDisplay.getTimeZone());

		_workflowTaskRequestHelper = new WorkflowTaskRequestHelper(
			_httpServletRequest);
	}

	public List<DropdownItem> getActionDropdownItems(WorkflowTask workflowTask)
		throws PortalException {

		if (workflowTask.isCompleted()) {
			return Collections.emptyList();
		}

		PortletURL redirectURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view.jsp"
		).buildPortletURL();

		return DropdownItemListBuilder.addAll(
			_getEditWorkflowTaskDropdownItems(redirectURL, workflowTask)
		).add(
			() -> !isAssignedToUser(workflowTask),
			_getAssignToMeUnsafeConsumer(redirectURL, workflowTask)
		).add(
			dropdownItem -> {
				dropdownItem.put("symbolLeft", "assign-to-...");

				String label = LanguageUtil.get(
					_httpServletRequest, "assign-to-...");

				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"action", "taskAssign"
					).put(
						"assignURL",
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/workflow_task_assign.jsp"
						).setRedirect(
							redirectURL
						).setParameter(
							"workflowTaskId", workflowTask.getWorkflowTaskId()
						).setParameter(
							"workflowTaskURL", getCurrentURL()
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).put(
						"namespace", _liferayPortletResponse.getNamespace()
					).put(
						"title", label
					).build());
				dropdownItem.setLabel(label);
			}
		).add(
			dropdownItem -> {
				dropdownItem.put("symbolLeft", "update-due-date");

				String label = LanguageUtil.get(
					_httpServletRequest, "update-due-date");

				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"action", "updateDueDate"
					).put(
						"namespace", _liferayPortletResponse.getNamespace()
					).put(
						"title", label
					).put(
						"updateDueDateURL",
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/workflow_task_due_date.jsp"
						).setRedirect(
							getCurrentURL()
						).setParameter(
							"workflowTaskId", workflowTask.getWorkflowTaskId()
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					).build());
				dropdownItem.setLabel(label);
			}
		).build();
	}

	public AssetEntry getAssetEntry() throws PortalException {
		long assetEntryId = ParamUtil.getLong(
			_liferayPortletRequest, "assetEntryId");

		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getAssetEntry(assetEntryId);
	}

	public String getAssetIconCssClass(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		return workflowHandler.getIconCssClass();
	}

	public AssetRenderer<?> getAssetRenderer(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		return workflowHandler.getAssetRenderer(
			getWorkflowContextEntryClassPK(workflowHandler, workflowTask));
	}

	public AssetRendererFactory<?> getAssetRendererFactory() {
		return AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
			ParamUtil.getString(_liferayPortletRequest, "type"));
	}

	public String getAssetTitle(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		String title = workflowHandler.getTitle(
			getWorkflowContextEntryClassPK(workflowHandler, workflowTask),
			getTaskContentLocale());

		if (title != null) {
			return title;
		}

		return getAssetType(workflowTask);
	}

	public String getAssetType(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		return workflowHandler.getType(getTaskContentLocale());
	}

	public List<User> getAssignableUsers(WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowTaskManagerUtil.getAssignableUsers(
			workflowTask.getWorkflowTaskId());
	}

	public String getAssignedTheTaskMessageKey(WorkflowLog workflowLog)
		throws PortalException {

		User user = _getUser(workflowLog.getUserId());

		return "x-assigned-the-task-to-" +
			(user.isMale() ? "himself" : "herself");
	}

	public Object getAssignedTheTaskToMessageArguments(
		WorkflowLog workflowLog) {

		return new Object[] {
			HtmlUtil.escape(
				PortalUtil.getUserName(
					workflowLog.getAuditUserId(),
					String.valueOf(workflowLog.getAuditUserId()))),
			HtmlUtil.escape(_getActorName(workflowLog))
		};
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			_getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public String getCreateDateString(WorkflowLog workflowLog) {
		return _dateTimeFormat.format(workflowLog.getCreateDate());
	}

	public String getCreateDateString(WorkflowTask workflowTask) {
		return _dateTimeFormat.format(workflowTask.getCreateDate());
	}

	public String getCurrentURL() {
		PortletURL portletURL = PortletURLUtil.getCurrent(
			_liferayPortletRequest, _liferayPortletResponse);

		return portletURL.toString();
	}

	public String getDescription(WorkflowTask workflowTask) {
		return HtmlUtil.escape(workflowTask.getDescription());
	}

	public String getDisplayStyle() {
		if (_displayStyle == null) {
			_displayStyle = WorkflowTaskPortletUtil.getWorkflowTaskDisplayStyle(
				_liferayPortletRequest, _DISPLAY_VIEWS);
		}

		return _displayStyle;
	}

	public Date getDueDate(WorkflowTask workflowTask) {
		return workflowTask.getDueDate();
	}

	public String getDueDateString(WorkflowTask workflowTask) {
		if (workflowTask.getDueDate() == null) {
			return LanguageUtil.get(
				_workflowTaskRequestHelper.getRequest(), "never");
		}

		return _dateTimeFormat.format(workflowTask.getDueDate());
	}

	public DropdownItemList getFilterOptions() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getFilterNavigationDropdownItem("all")
					).add(
						_getFilterNavigationDropdownItem("pending")
					).add(
						_getFilterNavigationDropdownItem("completed")
					).build());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(
						_workflowTaskRequestHelper.getRequest(), "filter"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getOrderByDropdownItem("last-activity-date")
					).add(
						_getOrderByDropdownItem("due-date")
					).build());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(
						_workflowTaskRequestHelper.getRequest(), "order-by"));
			}
		).build();
	}

	public String getHeaderTitle(WorkflowTask workflowTask)
		throws PortalException {

		return workflowTask.getLabel(getTaskContentLocale()) + ": " +
			getAssetTitle(workflowTask);
	}

	public Date getLastActivityDate(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowLog workflowLog = _getWorkflowLog(workflowTask);

		if (workflowLog != null) {
			return workflowLog.getCreateDate();
		}

		return null;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, PortletKeys.MY_WORKFLOW_TASK, "asc");

		return _orderByType;
	}

	public String getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		ThemeDisplay themeDisplay =
			_workflowTaskRequestHelper.getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource", portletDisplay.getId());

		return _portletResource;
	}

	public String getPreviewOfTitle(WorkflowTask workflowTask)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			_workflowTaskRequestHelper.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return LanguageUtil.format(
			themeDisplay.getLocale(), "preview-of-x",
			ResourceActionsUtil.getModelResource(
				themeDisplay.getLocale(),
				_getWorkflowContextEntryClassName(workflowTask)),
			false);
	}

	public String getPreviousAssigneeMessageArguments(WorkflowLog workflowLog) {
		return HtmlUtil.escape(
			PortalUtil.getUserName(
				workflowLog.getPreviousUserId(),
				String.valueOf(workflowLog.getPreviousUserId())));
	}

	public String getSearchURL() {
		return PortletURLBuilder.create(
			_getPortletURL()
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					_workflowTaskRequestHelper.getThemeDisplay();

				return themeDisplay.getScopeGroupId();
			}
		).buildString();
	}

	public String getSortingURL() {
		return PortletURLBuilder.createRenderURL(
			_workflowTaskRequestHelper.getLiferayPortletResponse()
		).setNavigation(
			_getNavigation()
		).setTabs1(
			_getTabs1()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public String getTaglibEditURL(WorkflowTask workflowTask)
		throws PortalException, PortletException {

		ThemeDisplay themeDisplay =
			_workflowTaskRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			_getEditPortletURL(workflowTask)
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setPortletResource(
			getPortletResource()
		).setParameter(
			"hideDefaultSuccessMessage", true
		).setParameter(
			"refererPlid", themeDisplay.getPlid()
		).setParameter(
			"workflowTaskId", workflowTask.getWorkflowTaskId()
		).setPortletMode(
			PortletMode.VIEW
		).setWindowState(
			LiferayWindowState.NORMAL
		).buildString();
	}

	public String getTaglibViewDiffsURL(WorkflowTask workflowTask)
		throws PortalException, PortletException {

		StringBundler sb = new StringBundler(7);

		sb.append("javascript:Liferay.Util.openModal({id: '");
		sb.append(_liferayPortletResponse.getNamespace());
		sb.append("viewDiffs', title: '");
		sb.append(
			HtmlUtil.escapeJS(
				LanguageUtil.get(
					_workflowTaskRequestHelper.getRequest(), "diffs")));
		sb.append("', url:'");
		sb.append(
			HtmlUtil.escapeJS(
				PortletURLBuilder.create(
					_getViewDiffsPortletURL(workflowTask)
				).setRedirect(
					getCurrentURL()
				).setParameter(
					"hideControls", true
				).setPortletMode(
					PortletMode.VIEW
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString()));
		sb.append("'});");

		return sb.toString();
	}

	public Object getTaskCompletionMessageArguments(WorkflowLog workflowLog) {
		return new Object[] {
			HtmlUtil.escape(
				PortalUtil.getUserName(
					workflowLog.getAuditUserId(),
					String.valueOf(workflowLog.getAuditUserId()))),
			workflowLog.getCurrentWorkflowNodeLabel(
				_workflowTaskRequestHelper.getLocale())
		};
	}

	public Locale getTaskContentLocale() {
		String languageId = LanguageUtil.getLanguageId(_httpServletRequest);

		if (Validator.isNotNull(languageId)) {
			return LocaleUtil.fromLanguageId(languageId);
		}

		return _workflowTaskRequestHelper.getLocale();
	}

	public String getTaskInitiallyAssignedMessageArguments(
		WorkflowLog workflowLog) {

		return HtmlUtil.escape(_getActorName(workflowLog));
	}

	public String getTaskUpdateMessageArguments(WorkflowLog workflowLog) {
		return HtmlUtil.escape(
			PortalUtil.getUserName(
				workflowLog.getAuditUserId(),
				String.valueOf(workflowLog.getAuditUserId())));
	}

	public int getTotalItems() throws PortalException {
		WorkflowTaskSearch workflowTaskSearch = getWorkflowTaskSearch();

		return workflowTaskSearch.getTotal();
	}

	public Object getTransitionMessageArguments(WorkflowLog workflowLog) {
		return new Object[] {
			HtmlUtil.escape(
				PortalUtil.getUserName(
					workflowLog.getAuditUserId(),
					String.valueOf(workflowLog.getAuditUserId()))),
			workflowLog.getPreviousWorkflowNodeLabel(
				_workflowTaskRequestHelper.getLocale()),
			workflowLog.getCurrentWorkflowNodeLabel(
				_workflowTaskRequestHelper.getLocale())
		};
	}

	public List<String> getTransitionNames(WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowTaskManagerUtil.getNextTransitionNames(
			_workflowTaskRequestHelper.getCompanyId(),
			_workflowTaskRequestHelper.getUserId(),
			workflowTask.getWorkflowTaskId());
	}

	public String getUserFullName(WorkflowLog workflowLog) {
		User user = _getUser(workflowLog.getUserId());

		return HtmlUtil.escape(user.getFullName());
	}

	public ViewTypeItemList getViewTypes() {
		return new ViewTypeItemList(_getPortletURL(), getDisplayStyle()) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public long getWorkflowContextEntryClassPK(
			WorkflowHandler<?> workflowHandler, WorkflowTask workflowTask)
		throws PortalException {

		return workflowHandler.getEntryClassPK(
			_workflowTaskRequestHelper.getCompanyId(), _httpServletRequest,
			workflowTask);
	}

	public WorkflowHandler<?> getWorkflowHandler(WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowHandlerRegistryUtil.getWorkflowHandler(
			_getWorkflowContextEntryClassName(workflowTask));
	}

	public List<WorkflowLog> getWorkflowLogs(WorkflowTask workflowTask)
		throws PortalException {

		List<Integer> logTypes = new ArrayList<Integer>() {
			{
				add(WorkflowLog.TASK_ASSIGN);
				add(WorkflowLog.TASK_COMPLETION);
				add(WorkflowLog.TASK_UPDATE);
				add(WorkflowLog.TRANSITION);
			}
		};

		return _workflowLogManager.getWorkflowLogsByWorkflowTask(
			_workflowTaskRequestHelper.getCompanyId(),
			workflowTask.getWorkflowTaskId(), logTypes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			_workflowComparatorFactory.getLogCreateDateComparator(false));
	}

	public WorkflowTask getWorkflowTask() {
		ResultRow resultRow = (ResultRow)_liferayPortletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		if (resultRow != null) {
			return (WorkflowTask)resultRow.getParameter("workflowTask");
		}

		return (WorkflowTask)_liferayPortletRequest.getAttribute(
			WebKeys.WORKFLOW_TASK);
	}

	public Map<String, Object> getWorkflowTaskActionLinkData() {
		return new HashMap<>();
	}

	public String getWorkflowTaskAssigneeUserName(WorkflowTask workflowTask) {
		return PortalUtil.getUserName(
			workflowTask.getAssigneeUserId(),
			String.valueOf(workflowTask.getAssigneeUserId()));
	}

	public String getWorkflowTaskRandomId() {
		String randomId = StringPool.BLANK;

		ResultRow resultRow = (ResultRow)_liferayPortletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		if (resultRow != null) {
			randomId = StringUtil.randomId();
		}

		return randomId;
	}

	public WorkflowTaskSearch getWorkflowTaskSearch() throws PortalException {
		if (_workflowTaskSearch != null) {
			return _workflowTaskSearch;
		}

		boolean searchByUserRoles = _isAssignedToMyRolesTabSelected();

		_workflowTaskSearch = new WorkflowTaskSearch(
			_liferayPortletRequest, _getCurParam(searchByUserRoles),
			_getPortletURL(), _workflowComparatorFactory);

		WorkflowModelSearchResult<WorkflowTask> workflowModelSearchResult =
			_getWorkflowModelSearchResult(
				_workflowTaskSearch.getDisplayTerms(), searchByUserRoles);

		_workflowTaskSearch.setResultsAndTotal(
			workflowModelSearchResult::getWorkflowModels,
			workflowModelSearchResult.getLength());

		_setWorkflowTaskSearchEmptyResultsMessage(
			_workflowTaskSearch, searchByUserRoles, _getCompleted());

		return _workflowTaskSearch;
	}

	public String getWorkflowTaskUnassignedUserName() {
		return LanguageUtil.get(
			_workflowTaskRequestHelper.getRequest(), "nobody");
	}

	public List<WorkflowTransition> getWorkflowTaskWorkflowTransitions(
			WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowTaskManagerUtil.getWorkflowTaskWorkflowTransitions(
			workflowTask.getWorkflowTaskId());
	}

	public boolean hasAssignableUsers(WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowTaskManagerUtil.hasAssignableUsers(
			_workflowTaskRequestHelper.getCompanyId(),
			workflowTask.getWorkflowTaskId());
	}

	public boolean hasEditPortletURL(WorkflowTask workflowTask)
		throws PortalException {

		if (_getEditPortletURL(workflowTask) != null) {
			return true;
		}

		return false;
	}

	public boolean hasViewDiffsPortletURL(WorkflowTask workflowTask)
		throws PortalException {

		if (_getViewDiffsPortletURL(workflowTask) != null) {
			return true;
		}

		return false;
	}

	public boolean isAssignedToUser(WorkflowTask workflowTask) {
		if (workflowTask.getAssigneeUserId() ==
				_workflowTaskRequestHelper.getUserId()) {

			return true;
		}

		return false;
	}

	public boolean isAuditUser(WorkflowLog workflowLog) {
		if (workflowLog.getUserId() == 0) {
			return false;
		}

		if (workflowLog.getAuditUserId() == workflowLog.getUserId()) {
			return true;
		}

		return false;
	}

	public boolean isReadOnly() {
		return (boolean)_liferayPortletRequest.getAttribute(
			WebKeys.WORKFLOW_TASK_READ_ONLY);
	}

	public boolean isShowEditURL(WorkflowTask workflowTask) {
		boolean showEditURL = false;

		if ((workflowTask.getAssigneeUserId() ==
				_workflowTaskRequestHelper.getUserId()) &&
			!workflowTask.isCompleted()) {

			showEditURL = true;
		}

		return showEditURL;
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

	private String _getActorName(WorkflowLog workflowLog) {
		if (workflowLog.getRoleId() != 0) {
			Role role = _getRole(workflowLog.getRoleId());

			if (role == null) {
				return String.valueOf(workflowLog.getRoleId());
			}

			return role.getTitle(
				LanguageUtil.getLanguageId(_httpServletRequest));
		}
		else if (workflowLog.getUserId() != 0) {
			return PortalUtil.getUserName(
				workflowLog.getUserId(),
				String.valueOf(workflowLog.getUserId()));
		}

		return StringPool.BLANK;
	}

	private String[] _getAssetType(String keywords) {
		for (WorkflowHandler<?> workflowHandler :
				_getSearchableAssetsWorkflowHandlers()) {

			if (StringUtil.equalsIgnoreCase(
					keywords,
					workflowHandler.getType(getTaskContentLocale()))) {

				return new String[] {workflowHandler.getClassName()};
			}
		}

		return null;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAssignToMeUnsafeConsumer(
			PortletURL redirectURL, WorkflowTask workflowTask) {

		return dropdownItem -> {
			dropdownItem.put("symbolLeft", "assign-to-me");

			String label = LanguageUtil.get(
				_httpServletRequest, "assign-to-me");

			dropdownItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "taskAssignToMe"
				).put(
					"assignToMeURL",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/workflow_task_assign.jsp"
					).setRedirect(
						() -> {
							if (Validator.isNull(
									_httpServletRequest.getParameter(
										"workflowTaskId"))) {

								return redirectURL;
							}

							return getCurrentURL();
						}
					).setParameter(
						"assigneeUserId",
						() -> {
							ThemeDisplay themeDisplay =
								(ThemeDisplay)_httpServletRequest.getAttribute(
									WebKeys.THEME_DISPLAY);

							return themeDisplay.getUserId();
						}
					).setParameter(
						"assignMode", "assignToMe"
					).setParameter(
						"workflowTaskId", workflowTask.getWorkflowTaskId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString()
				).put(
					"namespace", _liferayPortletResponse.getNamespace()
				).put(
					"title", label
				).build());
			dropdownItem.setLabel(label);
		};
	}

	private Boolean _getCompleted() {
		if (_isNavigationAll()) {
			return null;
		}

		if (_isNavigationCompleted()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private String _getCurParam(boolean searchByUserRoles) {
		Boolean completedTasks = _getCompleted();

		if (!searchByUserRoles && (completedTasks == null)) {
			return SearchContainer.DEFAULT_CUR_PARAM;
		}
		else if (!searchByUserRoles && completedTasks) {
			return "cur1";
		}
		else if (!searchByUserRoles && !completedTasks) {
			return "cur2";
		}
		else if (searchByUserRoles && (completedTasks == null)) {
			return "cur3";
		}
		else if (searchByUserRoles && completedTasks) {
			return "cur4";
		}

		return "cur5";
	}

	private PortletURL _getEditPortletURL(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		return workflowHandler.getURLEdit(
			getWorkflowContextEntryClassPK(workflowHandler, workflowTask),
			_liferayPortletRequest, _liferayPortletResponse);
	}

	private List<DropdownItem> _getEditWorkflowTaskDropdownItems(
			PortletURL redirectURL, WorkflowTask workflowTask)
		throws PortalException {

		List<DropdownItem> dropdownItems = new ArrayList<>();

		if (!isAssignedToUser(workflowTask)) {
			return dropdownItems;
		}

		for (WorkflowTransition workflowTransition :
				getWorkflowTaskWorkflowTransitions(workflowTask)) {

			String label = workflowTransition.getLabel(getTaskContentLocale());

			dropdownItems.add(
				DropdownItemBuilder.putData(
					"action", "taskEditWorkflowTask"
				).putData(
					"formSubmitURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse, PortletKeys.MY_WORKFLOW_TASK
					).setActionName(
						"/portal_workflow_task/complete_task"
					).setMVCPath(
						"/edit_workflow_task.jsp"
					).setRedirect(
						redirectURL
					).setParameter(
						"assigneeUserId", workflowTask.getAssigneeUserId()
					).setParameter(
						"closeRedirect",
						ParamUtil.getString(
							_httpServletRequest, "closeRedirect")
					).setParameter(
						"transitionName",
						() -> {
							if (Validator.isNotNull(
									workflowTransition.getName())) {

								return workflowTransition.getName();
							}

							return null;
						}
					).setParameter(
						"workflowTaskId", workflowTask.getWorkflowTaskId()
					).buildString()
				).putData(
					"namespace", _liferayPortletResponse.getNamespace()
				).putData(
					"title", label
				).setLabel(
					label
				).build());
		}

		return dropdownItems;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getFilterNavigationDropdownItem(String navigation) {

		return dropdownItem -> {
			dropdownItem.setActive(
				Objects.equals(_getNavigation(), navigation));
			dropdownItem.setHref(
				_getPortletURL(), "navigation", navigation, "mvcPath",
				"/view.jsp", "tabs1", _getTabs1());
			dropdownItem.setLabel(
				LanguageUtil.get(
					_workflowTaskRequestHelper.getRequest(), navigation));
		};
	}

	private String _getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
			"last-activity-date");

		return _orderByCol;
	}

	private UnsafeConsumer<DropdownItem, Exception> _getOrderByDropdownItem(
		String orderByCol) {

		return dropdownItem -> {
			dropdownItem.setActive(
				Objects.equals(_getOrderByCol(), orderByCol));
			dropdownItem.setHref(_getPortletURL(), "orderByCol", orderByCol);
			dropdownItem.setLabel(
				LanguageUtil.get(
					_workflowTaskRequestHelper.getRequest(), orderByCol));
		};
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setNavigation(
			() -> {
				String navigation = ParamUtil.getString(
					_httpServletRequest, "navigation");

				if (Validator.isNotNull(navigation)) {
					return _getNavigation();
				}

				return null;
			}
		).setTabs1(
			_getTabs1()
		).buildPortletURL();
	}

	private Role _getRole(long roleId) {
		Role role = _roles.get(roleId);

		if (role == null) {
			role = RoleLocalServiceUtil.fetchRole(roleId);

			_roles.put(roleId, role);
		}

		return role;
	}

	private List<WorkflowHandler<?>> _getSearchableAssetsWorkflowHandlers() {
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

	private String _getTabs1() {
		return ParamUtil.getString(
			_liferayPortletRequest, "tabs1", "assigned-to-me");
	}

	private User _getUser(long userId) {
		User user = _users.get(userId);

		if (user == null) {
			user = UserLocalServiceUtil.fetchUser(userId);

			_users.put(userId, user);
		}

		return user;
	}

	private PortletURL _getViewDiffsPortletURL(WorkflowTask workflowTask)
		throws PortalException {

		WorkflowHandler<?> workflowHandler = getWorkflowHandler(workflowTask);

		return workflowHandler.getURLViewDiffs(
			getWorkflowContextEntryClassPK(workflowHandler, workflowTask),
			_liferayPortletRequest, _liferayPortletResponse);
	}

	private Map<String, Serializable> _getWorkflowContext(
			WorkflowTask workflowTask)
		throws PortalException {

		WorkflowInstance workflowInstance = _getWorkflowInstance(workflowTask);

		return workflowInstance.getWorkflowContext();
	}

	private String _getWorkflowContextEntryClassName(WorkflowTask workflowTask)
		throws PortalException {

		Map<String, Serializable> workflowContext = _getWorkflowContext(
			workflowTask);

		return (String)workflowContext.get(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME);
	}

	private WorkflowInstance _getWorkflowInstance(WorkflowTask workflowTask)
		throws PortalException {

		return WorkflowInstanceManagerUtil.getWorkflowInstance(
			_workflowTaskRequestHelper.getCompanyId(),
			_getWorkflowInstanceId(workflowTask));
	}

	private long _getWorkflowInstanceId(WorkflowTask workflowTask) {
		return workflowTask.getWorkflowInstanceId();
	}

	private WorkflowLog _getWorkflowLog(WorkflowTask workflowTask)
		throws PortalException {

		List<WorkflowLog> workflowLogs =
			_workflowLogManager.getWorkflowLogsByWorkflowTask(
				_workflowTaskRequestHelper.getCompanyId(),
				workflowTask.getWorkflowTaskId(), null, 0, 1,
				_workflowComparatorFactory.getLogCreateDateComparator(false));

		if (!workflowLogs.isEmpty()) {
			return workflowLogs.get(0);
		}

		return null;
	}

	private WorkflowModelSearchResult<WorkflowTask>
			_getWorkflowModelSearchResult(
				DisplayTerms displayTerms, boolean searchByUserRoles)
		throws WorkflowException {

		if (Objects.nonNull(_workflowModelSearchResult)) {
			return _workflowModelSearchResult;
		}

		_workflowModelSearchResult =
			WorkflowTaskManagerUtil.searchWorkflowTasks(
				_workflowTaskRequestHelper.getCompanyId(),
				_workflowTaskRequestHelper.getUserId(),
				displayTerms.getKeywords(),
				new String[] {displayTerms.getKeywords()},
				_getAssetType(displayTerms.getKeywords()), null, null, null,
				null, null, _getCompleted(), true, searchByUserRoles, null,
				null, false, _workflowTaskSearch.getStart(),
				_workflowTaskSearch.getEnd(),
				_workflowTaskSearch.getOrderByComparator());

		return _workflowModelSearchResult;
	}

	private boolean _isAssignedToMyRolesTabSelected() {
		return Objects.equals(_getTabs1(), "assigned-to-my-roles");
	}

	private boolean _isNavigationAll() {
		return Objects.equals(_getNavigation(), "all");
	}

	private boolean _isNavigationCompleted() {
		return Objects.equals(_getNavigation(), "completed");
	}

	private void _setWorkflowTaskSearchEmptyResultsMessage(
		WorkflowTaskSearch workflowTaskSearch, boolean searchByUserRoles,
		Boolean completedTasks) {

		DisplayTerms searchTerms = workflowTaskSearch.getDisplayTerms();

		if (!searchByUserRoles && (completedTasks == null)) {
			workflowTaskSearch.setEmptyResultsMessage(
				"there-are-no-tasks-assigned-to-you");
		}
		else if (!searchByUserRoles && !completedTasks) {
			workflowTaskSearch.setEmptyResultsMessage(
				"there-are-no-pending-tasks-assigned-to-you");
		}
		else if (searchByUserRoles && (completedTasks == null)) {
			workflowTaskSearch.setEmptyResultsMessage(
				"there-are-no-tasks-assigned-to-your-roles");
		}
		else if (searchByUserRoles && !completedTasks) {
			workflowTaskSearch.setEmptyResultsMessage(
				"there-are-no-pending-tasks-assigned-to-your-roles");
		}
		else {
			workflowTaskSearch.setEmptyResultsMessage(
				"there-are-no-completed-tasks");
		}

		if (Validator.isNotNull(searchTerms.getKeywords())) {
			workflowTaskSearch.setEmptyResultsMessage(
				workflowTaskSearch.getEmptyResultsMessage() +
					"-with-the-specified-search-criteria");
		}
	}

	private static final String[] _DISPLAY_VIEWS = {"descriptive", "list"};

	private final Format _dateTimeFormat;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private String _portletResource;
	private final Map<Long, Role> _roles = new HashMap<>();
	private Boolean _showExtraInfo;
	private final Map<Long, User> _users = new HashMap<>();
	private final WorkflowComparatorFactory _workflowComparatorFactory;
	private final WorkflowLogManager _workflowLogManager;
	private WorkflowModelSearchResult<WorkflowTask> _workflowModelSearchResult;
	private final WorkflowTaskRequestHelper _workflowTaskRequestHelper;
	private WorkflowTaskSearch _workflowTaskSearch;

}