/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveWebKeys;
import com.liferay.document.library.opener.google.drive.web.internal.helper.GoogleDrivePortletRequestAuthorizationHelper;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2StateUtil;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/open_google_docs"
	},
	service = MVCRenderCommand.class
)
public class OpenGoogleDocsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			OAuth2State oAuth2State = OAuth2StateUtil.getOAuth2State(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(renderRequest)));

			if (oAuth2State == null) {
				_googleDrivePortletRequestAuthorizationHelper.
					performAuthorizationFlow(renderRequest, renderResponse);
			}
			else {
				renderRequest.setAttribute(
					DLOpenerGoogleDriveWebKeys.
						DL_OPENER_GOOGLE_DRIVE_FILE_REFERENCE,
					_googleDriveManager.requestEditAccess(
						_portal.getUserId(renderRequest),
						_dlAppService.getFileEntry(
							ParamUtil.getLong(renderRequest, "fileEntryId"))));

				RequestDispatcher requestDispatcher =
					_servletContext.getRequestDispatcher(
						"/open_google_docs.jsp");

				requestDispatcher.include(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getHttpServletResponse(renderResponse));
			}

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (IOException | PortalException | ServletException exception) {
			throw new PortletException(exception);
		}
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLOpenerGoogleDriveManager _googleDriveManager;

	@Reference
	private GoogleDrivePortletRequestAuthorizationHelper
		_googleDrivePortletRequestAuthorizationHelper;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.opener.google.drive.web)"
	)
	private ServletContext _servletContext;

}