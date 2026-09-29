/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.analytics.internal.servlet;

import com.liferay.document.library.analytics.internal.constants.DocumentLibraryAnalyticsConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.document.library.analytics.internal.servlet.ResolveFileEntryUUIDServlet",
		"osgi.http.whiteboard.servlet.pattern=" + DocumentLibraryAnalyticsConstants.PATH_RESOLVE_FILE_ENTRY,
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class ResolveFileEntryUUIDServlet extends HttpServlet {

	@Override
	protected void doGet(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			_sendSuccess(
				httpServletResponse,
				_getFileEntryByUuidAndGroupId(httpServletRequest));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private FileEntry _getFileEntryByUuidAndGroupId(
			HttpServletRequest httpServletRequest)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			user = _userLocalService.getGuestUser(
				_portal.getCompanyId(httpServletRequest));
		}

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(user));

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
		String uuid = ParamUtil.getString(httpServletRequest, "uuid");

		return _dlAppService.getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	private void _sendSuccess(
			HttpServletResponse httpServletResponse, FileEntry fileEntry)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		JSONObject jsonObject = JSONUtil.put(
			"fileEntryId", fileEntry.getFileEntryId());

		printWriter.write(jsonObject.toString());

		httpServletResponse.setStatus(200);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResolveFileEntryUUIDServlet.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}