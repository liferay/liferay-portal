/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.portlet.toolbar.contributor;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.opener.constants.DLOpenerMimeTypes;
import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.portlet.toolbar.contributor.DLPortletToolbarContributorContext;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLPortletToolbarContributorContext.class)
public class DLOpenerGoogleDriveDLPortletToolbarContributorContext
	implements DLPortletToolbarContributorContext {

	@Override
	public void updatePortletTitleMenuItems(
		List<MenuItem> menuItems, Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			if (folder != null) {
				folderId = folder.getFolderId();
			}

			if (!_dlOpenerGoogleDriveManager.isConfigured(
					themeDisplay.getCompanyId()) ||
				!ModelResourcePermissionUtil.contains(
					_folderEntryModelResourcePermission,
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), folderId,
					ActionKeys.ADD_DOCUMENT)) {

				return;
			}

			menuItems.add(
				_createURLMenuItem(
					portletRequest, folder, "google-doc",
					DLOpenerMimeTypes.APPLICATION_VND_DOCX, _ICON_NAME_DOCUMENT,
					_ICON_COLOR_DOCUMENT));
			menuItems.add(
				_createURLMenuItem(
					portletRequest, folder, "google-slide",
					DLOpenerMimeTypes.APPLICATION_VND_PPTX,
					_ICON_NAME_PRESENTATION, _ICON_COLOR_PRESENTATION));
			menuItems.add(
				_createURLMenuItem(
					portletRequest, folder, "google-sheet",
					DLOpenerMimeTypes.APPLICATION_VND_XLSX,
					_ICON_NAME_SPREADSHEET, _ICON_COLOR_SPREADSHEET));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private URLMenuItem _createURLMenuItem(
		PortletRequest portletRequest, Folder folder, String key,
		String contentType, String icon, String iconColor) {

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon(icon);
		urlMenuItem.setLabel(_translate(portletRequest, key));
		urlMenuItem.setMethod(HttpMethods.POST);
		urlMenuItem.setURL(_getActionURL(portletRequest, folder, contentType));
		urlMenuItem.setData(
			Collections.singletonMap("file-icon-color", iconColor));

		return urlMenuItem;
	}

	private String _getActionURL(
		PortletRequest portletRequest, Folder folder, String contentType) {

		try {
			LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
				portletRequest, _portal.getPortletId(portletRequest),
				PortletRequest.ACTION_PHASE);

			liferayPortletURL.setParameter(
				ActionRequest.ACTION_NAME,
				"/document_library/edit_in_google_docs");
			liferayPortletURL.setParameter(Constants.CMD, Constants.ADD);

			long repositoryId = _beanProperties.getLong(
				folder, "repositoryId",
				_portal.getScopeGroupId(portletRequest));

			liferayPortletURL.setParameter(
				"repositoryId", String.valueOf(repositoryId));

			long folderId = _beanProperties.getLong(
				folder, "folderId", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			liferayPortletURL.setParameter(
				"folderId", String.valueOf(folderId));

			liferayPortletURL.setParameter("contentType", contentType);
			liferayPortletURL.setParameter(
				"googleDocsRedirect",
				_portal.getCurrentURL(
					_portal.getHttpServletRequest(portletRequest)));

			return liferayPortletURL.toString();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private String _translate(PortletRequest portletRequest, String key) {
		return _language.get(_portal.getLocale(portletRequest), key);
	}

	private static final String _ICON_COLOR_DOCUMENT = "6";

	private static final String _ICON_COLOR_PRESENTATION = "4";

	private static final String _ICON_COLOR_SPREADSHEET = "2";

	private static final String _ICON_NAME_DOCUMENT = "document-text";

	private static final String _ICON_NAME_PRESENTATION =
		"document-presentation";

	private static final String _ICON_NAME_SPREADSHEET = "document-table";

	private static final Log _log = LogFactoryUtil.getLog(
		DLOpenerGoogleDriveDLPortletToolbarContributorContext.class);

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private DLOpenerGoogleDriveManager _dlOpenerGoogleDriveManager;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}