/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.internal.display.context;

import com.liferay.portal.background.task.util.comparator.BackgroundTaskComparatorFactoryUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ProcessListDisplayContext {

	public ProcessListDisplayContext(
		long groupId, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, Group liveGroup) {

		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_liveGroup = liveGroup;

		if (liveGroup == null) {
			_liveGroupId = 0L;
		}
		else {
			_liveGroupId = liveGroup.getGroupId();
		}
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle", "descriptive");

		return _displayStyle;
	}

	public String getProcessListListViewCss() {
		if (_processListListViewCss != null) {
			return _processListListViewCss;
		}

		_processListListViewCss = "process-list";

		if (Objects.equals(getDisplayStyle(), "list")) {
			_processListListViewCss += " process-list-list-view";
		}

		return _processListListViewCss;
	}

	public SearchContainer<BackgroundTask> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST),
			_getPortletURL(), null, "no-publish-processes-were-found");

		_searchContainer.setOrderByCol(_getOrderByCol());
		_searchContainer.setOrderByComparator(
			BackgroundTaskComparatorFactoryUtil.
				getBackgroundTaskOrderByComparator(
					_getOrderByCol(), _getOrderByType()));
		_searchContainer.setOrderByType(_getOrderByType());

		if (_isNavigationHome()) {
			if (Objects.equals(_getOrderByCol(), "duration")) {
				_searchContainer.setResultsAndTotal(
					() ->
						BackgroundTaskManagerUtil.getBackgroundTasksByDuration(
							new long[] {_groupId, _liveGroupId},
							new String[] {_getTaskExecutorClassName()},
							_searchContainer.getStart(),
							_searchContainer.getEnd(), _isOrderByAsc()),
					BackgroundTaskManagerUtil.getBackgroundTasksCount(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName()));
			}
			else {
				_searchContainer.setResultsAndTotal(
					() -> BackgroundTaskManagerUtil.getBackgroundTasks(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName(),
						_searchContainer.getStart(), _searchContainer.getEnd(),
						_searchContainer.getOrderByComparator()),
					BackgroundTaskManagerUtil.getBackgroundTasksCount(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName()));
			}
		}
		else {
			if (Objects.equals(_getOrderByCol(), "duration")) {
				_searchContainer.setResultsAndTotal(
					() ->
						BackgroundTaskManagerUtil.getBackgroundTasksByDuration(
							new long[] {_groupId, _liveGroupId},
							new String[] {_getTaskExecutorClassName()},
							_isNavigationCompleted(),
							_searchContainer.getStart(),
							_searchContainer.getEnd(), _isOrderByAsc()),
					BackgroundTaskManagerUtil.getBackgroundTasksCount(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName(), _isNavigationCompleted()));
			}
			else {
				_searchContainer.setResultsAndTotal(
					() -> BackgroundTaskManagerUtil.getBackgroundTasks(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName(), _isNavigationCompleted(),
						_searchContainer.getStart(), _searchContainer.getEnd(),
						_searchContainer.getOrderByComparator()),
					BackgroundTaskManagerUtil.getBackgroundTasksCount(
						new long[] {_groupId, _liveGroupId},
						_getTaskExecutorClassName(), _isNavigationCompleted()));
			}
		}

		_searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_liferayPortletResponse));

		return _searchContainer;
	}

	public boolean isLocalPublishing() {
		if (_localPublishing != null) {
			return _localPublishing;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		_localPublishing =
			(_liveGroup == null) ||
			stagingGroupHelper.isLocalStagingOrLocalLiveGroup(_liveGroup);

		return _localPublishing;
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

		_orderByCol = ParamUtil.getString(_httpServletRequest, "orderByCol");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(_httpServletRequest, "orderByType");

		return _orderByType;
	}

	private PortletURL _getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			GetterUtil.getString(
				_httpServletRequest.getAttribute(
					"liferay-staging:process-list:mvcRenderCommandName"))
		).setNavigation(
			_getNavigation()
		).setTabs1(
			"processes"
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"localPublishing", isLocalPublishing()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType", _getOrderByType()
		).setParameter(
			"searchContainerId",
			ParamUtil.getString(_httpServletRequest, "searchContainerId")
		).buildPortletURL();

		return _portletURL;
	}

	private String _getTaskExecutorClassName() {
		if (_taskExecutorClassName != null) {
			return _taskExecutorClassName;
		}

		if (isLocalPublishing()) {
			_taskExecutorClassName = GetterUtil.getString(
				_httpServletRequest.getAttribute(
					"liferay-staging:process-list:localTaskExecutorClassName"));
		}
		else {
			_taskExecutorClassName = GetterUtil.getString(
				_httpServletRequest.getAttribute(
					"liferay-staging:process-list:" +
						"remoteTaskExecutorClassName"));
		}

		if (Validator.isNull(_taskExecutorClassName)) {
			_taskExecutorClassName = GetterUtil.getString(
				_httpServletRequest.getAttribute(
					"liferay-staging:process-list:localTaskExecutorClassName"));
		}

		return _taskExecutorClassName;
	}

	private boolean _isNavigationCompleted() {
		return Objects.equals(_getNavigation(), "completed");
	}

	private boolean _isNavigationHome() {
		return Objects.equals(_getNavigation(), "all");
	}

	private boolean _isOrderByAsc() {
		if (_orderByAsc != null) {
			return _orderByAsc;
		}

		_orderByAsc = false;

		if (StringUtil.equalsIgnoreCase("asc", _getOrderByType())) {
			_orderByAsc = true;
		}

		return _orderByAsc;
	}

	private String _displayStyle;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final Group _liveGroup;
	private final long _liveGroupId;
	private Boolean _localPublishing;
	private String _navigation;
	private Boolean _orderByAsc;
	private String _orderByCol;
	private String _orderByType;
	private PortletURL _portletURL;
	private String _processListListViewCss;
	private SearchContainer<BackgroundTask> _searchContainer;
	private String _taskExecutorClassName;

}