/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.struts;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = "path=/wiki/get_page_attachment", service = StrutsAction.class
)
public class GetPageAttachmentStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			long nodeId = ParamUtil.getLong(httpServletRequest, "nodeId");

			String title = ParamUtil.getString(httpServletRequest, "title");
			String fileName = ParamUtil.getString(
				httpServletRequest, "fileName");

			if (fileName.startsWith(
					WikiPageConstants.SHARED_IMAGES_TITLE + StringPool.SLASH)) {

				String[] fileNameParts = fileName.split(
					WikiPageConstants.SHARED_IMAGES_TITLE + StringPool.SLASH);

				fileName = fileNameParts[1];

				title = WikiPageConstants.SHARED_IMAGES_TITLE;
			}

			int status = ParamUtil.getInteger(
				httpServletRequest, "status",
				WorkflowConstants.STATUS_APPROVED);

			_getFile(
				nodeId, title, fileName, status, httpServletRequest,
				httpServletResponse);

			return null;
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFileException ||
				exception instanceof NoSuchPageException) {

				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
			else {
				_portal.sendError(
					exception, httpServletRequest, httpServletResponse);
			}

			return null;
		}
	}

	private void _getFile(
			long nodeId, String title, String fileName, int status,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		WikiPage wikiPage = _wikiPageService.getPage(nodeId, title);

		FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			wikiPage.getGroupId(), wikiPage.getAttachmentsFolderId(), fileName);

		if ((status != WorkflowConstants.STATUS_IN_TRASH) &&
			fileEntry.isInTrash()) {

			return;
		}

		if (fileEntry.isInTrash()) {
			fileName = _trashHelper.getOriginalTitle(fileEntry.getTitle());
		}

		ServletResponseUtil.sendFile(
			httpServletRequest, httpServletResponse, fileName,
			fileEntry.getContentStream(), fileEntry.getSize(),
			fileEntry.getMimeType());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetPageAttachmentStrutsAction.class);

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiPageService _wikiPageService;

}