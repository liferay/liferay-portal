/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.web.internal.display.context.DLMimeTypeDisplayContextUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class WikiPageAttachmentHorizontalCard implements HorizontalCard {

	public WikiPageAttachmentHorizontalCard(
			FileEntry fileEntry, HttpServletRequest httpServletRequest)
		throws PortalException {

		_fileEntry = fileEntry;

		_fileVersion = fileEntry.getFileVersion();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getCssClass() {
		return DLMimeTypeDisplayContextUtil.getCssClassFileMimeType(
			_fileVersion);
	}

	@Override
	public String getHref() {
		return PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(
			_themeDisplay, _fileEntry,
			"status=" + WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getIcon() {
		return DLMimeTypeDisplayContextUtil.getIconFileMimeType(_fileVersion);
	}

	@Override
	public String getTitle() {
		return _fileEntry.getTitle();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final FileEntry _fileEntry;
	private final FileVersion _fileVersion;
	private final ThemeDisplay _themeDisplay;

}