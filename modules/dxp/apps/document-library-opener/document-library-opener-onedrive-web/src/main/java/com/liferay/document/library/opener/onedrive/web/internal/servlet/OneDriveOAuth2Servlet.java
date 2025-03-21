/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.servlet;

import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;

import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2Manager;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2StateUtil;
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

import java.net.SocketException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.document.library.opener.onedrive.web.internal.servlet.OneDriveOAuth2Servlet",
		"osgi.http.whiteboard.servlet.pattern=/document_library/onedrive/oauth2",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class OneDriveOAuth2Servlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		OAuth2State oAuth2State = OAuth2StateUtil.getOAuth2State(
			_portal.getOriginalServletRequest(httpServletRequest));

		if (oAuth2State == null) {
			throw new IllegalStateException(
				"Authorization state is not initialized");
		}

		if (!OAuth2StateUtil.isValid(oAuth2State, httpServletRequest)) {
			OAuth2StateUtil.cleanUp(httpServletRequest);

			httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
		}
		else {
			_requestAccessToken(
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

	private void _requestAccessToken(
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
				_oAuth2Manager.createAccessToken(
					_portal.getCompanyId(httpServletRequest),
					oAuth2State.getUserId(), code,
					_portal.getPortalURL(httpServletRequest));

				OAuth2StateUtil.cleanUp(httpServletRequest);

				httpServletResponse.sendRedirect(oAuth2State.getSuccessURL());
			}
			catch (OAuth2AccessTokenErrorResponse | SocketException exception) {
				_log.error(exception);

				OAuth2StateUtil.cleanUp(httpServletRequest);

				SessionErrors.add(httpServletRequest, "externalServiceFailed");

				httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
			}
			catch (Exception exception) {
				_log.error(exception);

				OAuth2StateUtil.cleanUp(httpServletRequest);

				SessionErrors.add(httpServletRequest, exception.getClass());

				httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OneDriveOAuth2Servlet.class);

	private static final long serialVersionUID = 7759897747401129852L;

	@Reference
	private OAuth2Manager _oAuth2Manager;

	@Reference
	private Portal _portal;

}