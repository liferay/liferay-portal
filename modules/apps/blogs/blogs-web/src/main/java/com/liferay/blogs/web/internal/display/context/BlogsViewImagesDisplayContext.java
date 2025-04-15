/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context;

import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class BlogsViewImagesDisplayContext {

	public BlogsViewImagesDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public long getFolderId() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Folder folder = _getAttachmentsFolder(themeDisplay);

		return folder.getFolderId();
	}

	public long getRepositoryId() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Folder folder = _getAttachmentsFolder(themeDisplay);

		return folder.getRepositoryId();
	}

	private Folder _getAttachmentsFolder(ThemeDisplay themeDisplay)
		throws PortalException {

		if (_folder != null) {
			return _folder;
		}

		_folder = BlogsEntryLocalServiceUtil.addAttachmentsFolder(
			themeDisplay.getUserId(), themeDisplay.getScopeGroupId());

		return _folder;
	}

	private Folder _folder;
	private final HttpServletRequest _httpServletRequest;

}