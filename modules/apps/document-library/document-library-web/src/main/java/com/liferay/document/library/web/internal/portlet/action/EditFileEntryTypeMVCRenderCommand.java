/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.display.context.DLEditFileEntryTypeDataEngineDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_file_entry_type"
	},
	service = MVCRenderCommand.class
)
public class EditFileEntryTypeMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			renderRequest.setAttribute(
				DLWebKeys.
					DOCUMENT_LIBRARY_EDIT_FILE_ENTRY_TYPE_DATA_ENGINE_DISPLAY_CONTEXT,
				new DLEditFileEntryTypeDataEngineDisplayContext(
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse)));

			long fileEntryTypeId = ParamUtil.getLong(
				renderRequest, "fileEntryTypeId");

			if (fileEntryTypeId <= 0) {
				return "/document_library/edit_file_entry_type.jsp";
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeService.getFileEntryType(fileEntryTypeId);

			_dlFileEntryTypeModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), dlFileEntryType,
				ActionKeys.UPDATE);

			renderRequest.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY_TYPE, dlFileEntryType);

			return "/document_library/edit_file_entry_type.jsp";
		}
		catch (NoSuchFileEntryTypeException | PrincipalException exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			return "/document_library/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntryType)"
	)
	private volatile ModelResourcePermission<DLFileEntryType>
		_dlFileEntryTypeModelResourcePermission;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference
	private Portal _portal;

}