/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.task.web.internal.util.WorkflowTaskPortletUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class WorkflowTaskManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public WorkflowTaskManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getDisplayStyle() {
		if (_displayStyle == null) {
			_displayStyle = WorkflowTaskPortletUtil.getWorkflowTaskDisplayStyle(
				liferayPortletRequest, new String[] {"descriptive", "list"});
		}

		return _displayStyle;
	}

	@Override
	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
			"last-activity-date");

		return _orderByCol;
	}

	@Override
	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, PortletKeys.MY_WORKFLOW_TASK, "asc");

		return _orderByType;
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"descriptive", "list"};
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "pending", "completed"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"last-activity-date", "due-date"};
	}

	private String _displayStyle;
	private String _orderByCol;
	private String _orderByType;

}