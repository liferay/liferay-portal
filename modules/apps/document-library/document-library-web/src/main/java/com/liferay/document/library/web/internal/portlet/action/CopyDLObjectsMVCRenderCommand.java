/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.web.internal.display.context.CopyDLObjectsDisplayContext;
import com.liferay.document.library.web.internal.exception.DLObjectSizeLimitExceededException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/copy_dl_objects"
	},
	service = MVCRenderCommand.class
)
public class CopyDLObjectsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long size = _getDLObjectsSize(
				ParamUtil.getLongValues(renderRequest, "dlObjectIds"));

			_validateDLObjectsSize(renderRequest, size);

			CopyDLObjectsDisplayContext copyDLObjectsDisplayContext =
				new CopyDLObjectsDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse), size,
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY));

			renderRequest.setAttribute(
				CopyDLObjectsDisplayContext.class.getName(),
				copyDLObjectsDisplayContext);

			return "/document_library/copy_dl_objects.jsp";
		}
		catch (DLObjectSizeLimitExceededException
					dlObjectSizeLimitExceededException) {

			HttpServletRequest originalHttpServletRequest =
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(renderRequest));

			SessionErrors.add(
				originalHttpServletRequest.getSession(),
				DLObjectSizeLimitExceededException.class,
				dlObjectSizeLimitExceededException);

			_sendRedirect(renderRequest, renderResponse);

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private long _getDLObjectsSize(long[] dlObjectIds) throws PortalException {
		long size = 0;

		for (long dlObjectId : dlObjectIds) {
			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				dlObjectId);

			if (dlFileEntry != null) {
				size += dlFileEntry.getSize();

				continue;
			}

			DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(dlObjectId);

			if (dlFolder != null) {
				size += _dlFolderLocalService.getFolderSize(
					dlFolder.getCompanyId(), dlFolder.getGroupId(),
					dlFolder.getTreePath());

				continue;
			}

			DLFileShortcut dlFileShortcut =
				_dlFileShortcutLocalService.getDLFileShortcut(dlObjectId);

			DLFileEntry toDLFileEntry = _dlFileEntryLocalService.getDLFileEntry(
				dlFileShortcut.getToFileEntryId());

			size += toDLFileEntry.getSize();
		}

		return size;
	}

	private void _sendRedirect(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				ParamUtil.getString(renderRequest, "redirect"));
		}
		catch (IOException ioException) {
			throw new PortletException(ioException);
		}
	}

	private void _validateDLObjectsSize(
			PortletRequest portletRequest, long size)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!DLCopyValidationUtil.isCopyToAllowed(
				_dlSizeLimitConfigurationProvider.getCompanyMaxSizeToCopy(
					themeDisplay.getCompanyId()),
				_dlSizeLimitConfigurationProvider.getGroupMaxSizeToCopy(
					themeDisplay.getScopeGroupId()),
				_dlSizeLimitConfigurationProvider.getSystemMaxSizeToCopy(),
				size)) {

			throw new DLObjectSizeLimitExceededException(
				_language.get(
					themeDisplay.getLocale(),
					DLCopyValidationUtil.getCopyToValidationMessage(
						_dlSizeLimitConfigurationProvider.
							getCompanyMaxSizeToCopy(
								themeDisplay.getCompanyId()),
						_dlSizeLimitConfigurationProvider.getGroupMaxSizeToCopy(
							themeDisplay.getScopeGroupId()),
						_dlSizeLimitConfigurationProvider.
							getSystemMaxSizeToCopy(),
						size, themeDisplay.getLocale(), false)));
		}
	}

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}