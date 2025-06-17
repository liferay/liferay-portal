/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ExportImportProcessDisplayContext {

	public ExportImportProcessDisplayContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		_httpServletRequest = httpServletRequest;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
	}

	public BackgroundTask getBackgroundTask() throws PortalException {
		if (_backgroundTaskId != null) {
			return _backgroundTask;
		}

		_backgroundTaskId = ParamUtil.getLong(
			_httpServletRequest, "backgroundTaskId");

		_backgroundTask = null;

		if (_backgroundTaskId > 0) {
			_backgroundTask = BackgroundTaskManagerUtil.getBackgroundTask(
				_backgroundTaskId);
		}

		return _backgroundTask;
	}

	public SearchContainer<BackgroundTask> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_portletRequest, _getIteratorURL(), null,
			"no-processes-were-found");

		BackgroundTask backgroundTask = getBackgroundTask();

		List<BackgroundTask> backgroundTasks = new ArrayList<>();

		if (backgroundTask != null) {
			backgroundTasks.add(backgroundTask);
		}

		_searchContainer.setResultsAndTotal(
			() -> backgroundTasks, backgroundTasks.size());

		return _searchContainer;
	}

	private PortletURL _getIteratorURL() {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		MimeResponse mimeResponse = (MimeResponse)portletResponse;

		return mimeResponse.createRenderURL();
	}

	private BackgroundTask _backgroundTask;
	private Long _backgroundTaskId;
	private final HttpServletRequest _httpServletRequest;
	private final PortletRequest _portletRequest;
	private SearchContainer<BackgroundTask> _searchContainer;

}