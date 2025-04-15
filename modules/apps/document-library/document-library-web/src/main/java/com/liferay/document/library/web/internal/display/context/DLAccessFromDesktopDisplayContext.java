/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.search.ResultRow;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class DLAccessFromDesktopDisplayContext {

	public DLAccessFromDesktopDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_dlRequestHelper = new DLRequestHelper(httpServletRequest);
	}

	public Folder getFolder() throws PortalException {
		ResultRow row = (ResultRow)_httpServletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		if ((row != null) && (row.getObject() instanceof Folder)) {
			return (Folder)row.getObject();
		}

		Folder folder = (Folder)_httpServletRequest.getAttribute(
			"info_panel.jsp-folder");

		if (folder == null) {
			return ActionUtil.getFolder(_httpServletRequest);
		}

		return folder;
	}

	public String getRandomNamespace() {
		if (_randomNamespace != null) {
			return _randomNamespace;
		}

		String randomKey = PortalUtil.generateRandomKey(
			_httpServletRequest, _getRandomNamespaceKey());

		_randomNamespace = randomKey + StringPool.UNDERLINE;

		return _randomNamespace;
	}

	public String getWebDAVHelpMessage() {
		return LanguageUtil.get(_httpServletRequest, "webdav-help");
	}

	public String getWebDAVURL() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DLURLHelperUtil.getWebDavURL(themeDisplay, getFolder(), null);
	}

	private String _getRandomNamespaceKey() {
		String resourcePortletName = _dlRequestHelper.getResourcePortletName();

		if (resourcePortletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			resourcePortletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			return "portlet_document_library_folder_action";
		}

		return "portlet_image_gallery_display_folder_action";
	}

	private final DLRequestHelper _dlRequestHelper;
	private final HttpServletRequest _httpServletRequest;
	private String _randomNamespace;

}