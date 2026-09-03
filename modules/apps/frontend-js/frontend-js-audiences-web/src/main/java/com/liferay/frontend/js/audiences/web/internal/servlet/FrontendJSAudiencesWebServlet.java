/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.servlet;

import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.frontend.js.audiences.web.internal.util.BootstrapJavaScriptUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.frontend.js.audiences.web.internal.servlet.FrontendJSAudiencesWebServlet",
		"osgi.http.whiteboard.servlet.pattern=/audiences/*"
	},
	service = Servlet.class
)
public class FrontendJSAudiencesWebServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String[] parts = StringUtil.split(
			httpServletRequest.getPathInfo(), CharPool.SLASH);

		String cacheControl = "immutable, max-age=31536000, public";
		String content = null;
		String contentType = null;
		String hash = null;

		if ((parts.length == 2) && parts[1].startsWith("bootstrap.")) {
			content = BootstrapJavaScriptUtil.getContent(
				httpServletRequest.getParameter("audiencesDefinitionHash"),
				GetterUtil.getInteger(
					httpServletRequest.getParameter("detectionTimeout")),
				Boolean.parseBoolean(
					httpServletRequest.getParameter("enableLog")));
			contentType = ContentTypes.APPLICATION_JAVASCRIPT;
			hash = BootstrapJavaScriptUtil.getHash();
		}
		else if ((parts.length == 2) && parts[1].startsWith("definition.")) {
			AudiencesDefinition audiencesDefinition =
				_audiencesDefinitionProvider.getAudiencesDefinition(
					_portal.getCompanyId(httpServletRequest));

			if (audiencesDefinition == null) {
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			content = audiencesDefinition.getContent();
			contentType = ContentTypes.APPLICATION_JSON;
			hash = audiencesDefinition.getHash();
		}
		else if ((parts.length == 4) && parts[3].startsWith("variations.")) {
			User user = _getUser(httpServletRequest);

			if (user == null) {
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			ElementVariations elementVariations = _getElementVariations(parts);

			if (elementVariations == null) {
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			if (!user.isGuestUser()) {
				cacheControl = "immutable, max-age=31536000, private";
			}

			content = elementVariations.getContent();
			contentType = ContentTypes.APPLICATION_JAVASCRIPT;
			hash = elementVariations.getHash();
		}
		else {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String requestHash = HashedFilesUtil.getHash(
			httpServletRequest.getPathInfo());

		if (!Objects.equals(hash, requestHash)) {
			StringBundler sb = new StringBundler(3);

			sb.append(
				HashedFilesUtil.replaceHash(
					httpServletRequest.getRequestURI(), hash));

			String queryString = httpServletRequest.getQueryString();

			if (!Validator.isBlank(queryString)) {
				sb.append(StringPool.QUESTION);
				sb.append(queryString);
			}

			httpServletResponse.sendRedirect(sb.toString());

			return;
		}

		httpServletResponse.setContentType(contentType);
		httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print(content);
	}

	private ElementVariations _getElementVariations(String[] parts) {
		return _elementVariationsProvider.getElementVariations(
			GetterUtil.getLong(parts[1]), GetterUtil.getLong(parts[2]));
	}

	private User _getSessionUser(HttpServletRequest httpServletRequest)
		throws PortalException {

		HttpSession httpSession = httpServletRequest.getSession();

		if (PortalSessionThreadLocal.getHttpSession() == null) {
			PortalSessionThreadLocal.setHttpSession(httpSession);
		}

		String userIdString = (String)httpSession.getAttribute("j_username");
		String password = (String)httpSession.getAttribute("j_password");

		if ((userIdString == null) || (password == null)) {
			return null;
		}

		return _userLocalService.getUser(GetterUtil.getLong(userIdString));
	}

	private User _getUser(HttpServletRequest httpServletRequest) {
		try {
			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				user = _getSessionUser(httpServletRequest);
			}

			if (user == null) {
				return _userLocalService.getGuestUser(
					_portal.getCompanyId(httpServletRequest));
			}

			PrincipalThreadLocal.setName(user.getUserId());
			PrincipalThreadLocal.setPassword(
				_portal.getUserPassword(httpServletRequest));

			return user;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendJSAudiencesWebServlet.class);

	@Reference
	private AudiencesDefinitionProvider _audiencesDefinitionProvider;

	@Reference
	private ElementVariationsProvider _elementVariationsProvider;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}