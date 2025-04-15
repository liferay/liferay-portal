/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/bookmarks/edit_entry"
	},
	service = MVCRenderCommand.class
)
public class EditEntryMVCRenderCommand extends BaseEntryMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long entryId = ParamUtil.getLong(renderRequest, "entryId");

		CTTimelineUtil.setCTTimelineKeys(
			renderRequest, BookmarksEntry.class, entryId);

		return super.render(renderRequest, renderResponse);
	}

	@Override
	protected void checkPermissions(
			PermissionChecker permissionChecker, BookmarksEntry entry)
		throws PortalException {

		_bookmarksEntryModelResourcePermission.check(
			permissionChecker, entry, ActionKeys.UPDATE);
	}

	@Override
	protected String getPath() {
		return "/bookmarks/edit_entry.jsp";
	}

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksEntry)"
	)
	private volatile ModelResourcePermission<BookmarksEntry>
		_bookmarksEntryModelResourcePermission;

}