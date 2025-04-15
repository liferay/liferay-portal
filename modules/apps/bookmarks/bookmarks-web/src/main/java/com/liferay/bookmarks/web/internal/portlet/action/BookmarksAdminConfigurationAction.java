/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
	service = ConfigurationAction.class
)
public class BookmarksAdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/bookmarks_admin/configuration_browse.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateEmail(actionRequest, "emailMessageAdded");
		validateEmail(actionRequest, "emailMessageUpdated");

		_validateRootFolder(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _validateRootFolder(ActionRequest actionRequest)
		throws Exception {

		long rootFolderId = GetterUtil.getLong(
			getParameter(actionRequest, "rootFolderId"));

		if (rootFolderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			try {
				_bookmarksFolderLocalService.getFolder(rootFolderId);
			}
			catch (NoSuchFolderException noSuchFolderException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFolderException);
				}

				SessionErrors.add(actionRequest, "rootFolderIdInvalid");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BookmarksAdminConfigurationAction.class);

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

}