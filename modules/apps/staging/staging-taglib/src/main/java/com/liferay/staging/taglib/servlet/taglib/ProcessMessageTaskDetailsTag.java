/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class ProcessMessageTaskDetailsTag extends IncludeTag {

	public long getBackgroundTaskId() {
		return _backgroundTaskId;
	}

	public String getBackgroundTaskStatusMessage() {
		return _backgroundTaskStatusMessage;
	}

	public String getLinkClass() {
		return _linkClass;
	}

	public void setBackgroundTaskId(long backgroundTaskId) {
		_backgroundTaskId = backgroundTaskId;
	}

	public void setBackgroundTaskStatusMessage(
		String backgroundTaskStatusMessage) {

		_backgroundTaskStatusMessage = backgroundTaskStatusMessage;
	}

	public void setLinkClass(String linkClass) {
		_linkClass = linkClass;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_backgroundTaskId = 0;
		_backgroundTaskStatusMessage = null;
		_linkClass = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:process-message-task-details:backgroundTaskId",
			_backgroundTaskId);
		httpServletRequest.setAttribute(
			"liferay-staging:" +
				"process-message-task-details:backgroundTaskStatusMessage",
			_backgroundTaskStatusMessage);
		httpServletRequest.setAttribute(
			"liferay-staging:process-message-task-details:linkClass",
			_linkClass);
	}

	private static final String _PAGE =
		"/process_message_task_details/page.jsp";

	private long _backgroundTaskId;
	private String _backgroundTaskStatusMessage;
	private String _linkClass = StringPool.BLANK;

}