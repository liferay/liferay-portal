/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class JournalFolderDisplayContext {

	public JournalFolderDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public JournalFolder getFolder() {
		if (_folder != null) {
			return _folder;
		}

		_folder = (JournalFolder)_httpServletRequest.getAttribute(
			WebKeys.JOURNAL_FOLDER);

		if (_folder != null) {
			return _folder;
		}

		_folder = JournalFolderLocalServiceUtil.fetchFolder(
			ParamUtil.getLong(_httpServletRequest, "folderId"));

		return _folder;
	}

	public int getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_httpServletRequest, "status", _getDefaultStatus());

		return _status;
	}

	private int _getDefaultStatus() {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				_themeDisplay.getCompanyId(),
				_themeDisplay.getScopeGroupId()) ||
			_isNavigationMine()) {

			return WorkflowConstants.STATUS_ANY;
		}

		return WorkflowConstants.STATUS_APPROVED;
	}

	private boolean _isNavigationMine() {
		if (_navigationMine != null) {
			return _navigationMine;
		}

		_navigationMine = ParamUtil.getBoolean(
			_httpServletRequest, "navigationMine");

		return _navigationMine;
	}

	private JournalFolder _folder;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _navigationMine;
	private Integer _status;
	private final ThemeDisplay _themeDisplay;

}