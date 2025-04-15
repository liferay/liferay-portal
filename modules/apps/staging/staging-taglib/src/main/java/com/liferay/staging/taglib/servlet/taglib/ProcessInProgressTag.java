/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class ProcessInProgressTag extends IncludeTag {

	public BackgroundTask getBackgroundTask() {
		return _backgroundTask;
	}

	public boolean isListView() {
		return _listView;
	}

	public void setBackgroundTask(BackgroundTask backgroundTask) {
		_backgroundTask = backgroundTask;
	}

	public void setListView(boolean listView) {
		_listView = listView;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_backgroundTask = null;
		_listView = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:process-in-progress:backgroundTask",
			_backgroundTask);
		httpServletRequest.setAttribute(
			"liferay-staging:process-in-progress:listView", _listView);
	}

	private static final String _PAGE = "/process_in_progress/page.jsp";

	private BackgroundTask _backgroundTask;
	private boolean _listView;

}