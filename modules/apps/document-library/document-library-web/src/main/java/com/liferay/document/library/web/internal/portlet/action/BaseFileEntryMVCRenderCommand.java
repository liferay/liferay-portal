/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Sergio González
 */
public abstract class BaseFileEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			FileEntry fileEntry = ActionUtil.getFileEntry(renderRequest);

			if (fileEntry != null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				checkPermissions(
					themeDisplay.getPermissionChecker(), fileEntry);

				CTTimelineUtil.setCTTimelineKeys(
					renderRequest, DLFileEntry.class,
					fileEntry.getFileEntryId());
			}

			renderRequest.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY, fileEntry);

			String version = ParamUtil.getString(renderRequest, "version");

			if (Validator.isNotNull(version)) {
				renderRequest.setAttribute(
					WebKeys.DOCUMENT_LIBRARY_FILE_VERSION,
					ActionUtil.getFileVersion(renderRequest, fileEntry));
			}

			setAttributes(renderRequest, renderResponse);

			return getPath();
		}
		catch (NoSuchFileEntryException | NoSuchFileVersionException |
			   NoSuchRepositoryEntryException | PrincipalException exception) {

			SessionErrors.add(renderRequest, exception.getClass());

			return "/document_library/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	protected void checkPermissions(
			PermissionChecker permissionChecker, FileEntry fileEntry)
		throws PortalException {
	}

	protected abstract String getPath();

	protected void setAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {
	}

}