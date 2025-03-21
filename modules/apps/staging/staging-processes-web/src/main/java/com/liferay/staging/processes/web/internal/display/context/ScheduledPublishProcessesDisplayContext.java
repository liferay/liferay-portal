/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ScheduledPublishProcessesDisplayContext {

	public ScheduledPublishProcessesDisplayContext(
		Group group, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, long liveGroupId) {

		_group = group;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_liveGroupId = liveGroupId;
	}

	public String getCmd() {
		if (_cmd != null) {
			return _cmd;
		}

		_cmd = "unschedule_publish_to_live";

		if (_group.isStaged() && _group.isStagedRemotely()) {
			_cmd = "unschedule_publish_to_remote";
		}

		return _cmd;
	}

	public SearchContainer<SchedulerResponse> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST),
			_liferayPortletResponse.createRenderURL(), null,
			"no-scheduled-publish-processes-were-found");

		_searchContainer.setOrderByCol(_getOrderByCol());
		_searchContainer.setOrderByCol(_getOrderByType());
		_searchContainer.setResultsAndTotal(
			SchedulerEngineHelperUtil.getScheduledJobs(
				StagingUtil.getSchedulerGroupName(
					_getDestinationName(), _liveGroupId),
				StorageType.PERSISTED));

		return _searchContainer;
	}

	private String _getDestinationName() {
		if (_destinationName != null) {
			return _destinationName;
		}

		if (_isLocalPublishing()) {
			_destinationName = DestinationNames.LAYOUTS_LOCAL_PUBLISHER;
		}
		else {
			_destinationName = DestinationNames.LAYOUTS_REMOTE_PUBLISHER;
		}

		return _destinationName;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, PortletKeys.BACKGROUND_TASK,
			"entries-order-by-col", "create-date");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, PortletKeys.BACKGROUND_TASK,
			"entries-order-by-type", "desc");

		return _orderByType;
	}

	private boolean _isLocalPublishing() {
		if (_localPublishing != null) {
			return _localPublishing;
		}

		_localPublishing = true;

		if (_group.isStaged() && _group.isStagedRemotely()) {
			_localPublishing = false;
		}

		return _localPublishing;
	}

	private String _cmd;
	private String _destinationName;
	private final Group _group;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final long _liveGroupId;
	private Boolean _localPublishing;
	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<SchedulerResponse> _searchContainer;

}