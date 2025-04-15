/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.struts;

import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.service.BookmarksEntryService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "path=/bookmarks/open_entry", service = StrutsAction.class
)
public class OpenEntryStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			long entryId = ParamUtil.getLong(httpServletRequest, "entryId");

			BookmarksEntry entry = _bookmarksEntryService.getEntry(entryId);

			if (entry.isInTrash()) {
				int status = ParamUtil.getInteger(
					httpServletRequest, "status",
					WorkflowConstants.STATUS_APPROVED);

				if (status != WorkflowConstants.STATUS_IN_TRASH) {
					throw new NoSuchEntryException("{entryId=" + entryId + "}");
				}
			}

			entry = _bookmarksEntryService.openEntry(entry);

			httpServletResponse.sendRedirect(entry.getUrl());

			return null;
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	@Reference
	private BookmarksEntryService _bookmarksEntryService;

	@Reference
	private Portal _portal;

}