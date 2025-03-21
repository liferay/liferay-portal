/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class ProcessStatusTag extends IncludeTag {

	public int getBackgroundTaskStatus() {
		return _backgroundTaskStatus;
	}

	public String getBackgroundTaskStatusLabel() {
		return _backgroundTaskStatusLabel;
	}

	public void setBackgroundTaskStatus(int backgroundTaskStatus) {
		_backgroundTaskStatus = backgroundTaskStatus;
	}

	public void setBackgroundTaskStatusLabel(String backgroundTaskStatusLabel) {
		_backgroundTaskStatusLabel = backgroundTaskStatusLabel;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_backgroundTaskStatus = 0;
		_backgroundTaskStatusLabel = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:process-status:backgroundTaskStatus",
			_backgroundTaskStatus);
		httpServletRequest.setAttribute(
			"liferay-staging:process-status:backgroundTaskStatusLabel",
			_backgroundTaskStatusLabel);
	}

	private static final String _PAGE = "/process_status/page.jsp";

	private int _backgroundTaskStatus;
	private String _backgroundTaskStatusLabel;

}