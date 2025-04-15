/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.servlet;

import com.google.api.client.auth.oauth2.TokenResponseException;

import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveWebConstants;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2StateUtil;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.document.library.opener.google.drive.web.internal.servlet.GoogleDriveOAuth2Servlet",
		"osgi.http.whiteboard.servlet.pattern=" + DLOpenerGoogleDriveWebConstants.GOOGLE_DRIVE_SERVLET_PATH,
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class GoogleDriveOAuth2Servlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		OAuth2State oAuth2State = OAuth2StateUtil.getOAuth2State(
			_portal.getOriginalServletRequest(httpServletRequest));

		if (oAuth2State == null) {
			throw new IllegalStateException(
				"Authorization oAuth2State not initialized");
		}

		if (!OAuth2StateUtil.isValid(oAuth2State, httpServletRequest)) {
			OAuth2StateUtil.cleanUp(httpServletRequest);

			httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
		}
		else {
			_requestAuthorizationToken(
				httpServletRequest, httpServletResponse, oAuth2State);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		doGet(httpServletRequest, httpServletResponse);
	}

	private void _requestAuthorizationToken(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, OAuth2State oAuth2State)
		throws IOException {

		String code = ParamUtil.getString(httpServletRequest, "code");

		if (Validator.isNull(code)) {
			OAuth2StateUtil.cleanUp(httpServletRequest);
			httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
		}
		else {
			try {
				_dlOpenerGoogleDriveManager.requestAuthorizationToken(
					_portal.getCompanyId(httpServletRequest),
					oAuth2State.getUserId(), code,
					OAuth2StateUtil.getRedirectURI(
						_portal.getPortalURL(httpServletRequest)));

				OAuth2StateUtil.cleanUp(httpServletRequest);

				httpServletResponse.sendRedirect(oAuth2State.getSuccessURL());
			}
			catch (TokenResponseException tokenResponseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(tokenResponseException);
				}

				OAuth2StateUtil.cleanUp(httpServletRequest);

				SessionErrors.add(httpServletRequest, "externalServiceFailed");

				httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
			}
			catch (PortalException portalException) {
				throw new IOException(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleDriveOAuth2Servlet.class);

	private static final long serialVersionUID = 7759897747401129852L;

	@Reference
	private DLOpenerGoogleDriveManager _dlOpenerGoogleDriveManager;

	@Reference
	private Portal _portal;

}