/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.sharepoint.methods.Method;
import com.liferay.portal.sharepoint.methods.MethodFactory;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Bruno Farache
 */
public class SharepointServlet extends HttpServlet {

	@Override
	public void doGet(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					httpServletRequest.getHeader(HttpHeaders.USER_AGENT), " ",
					httpServletRequest.getMethod(), " ",
					httpServletRequest.getRequestURI()));
		}

		try {
			String uri = httpServletRequest.getRequestURI();

			if (uri.equals("/_vti_inf.html")) {
				vtiInfHtml(httpServletResponse);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String uri = httpServletRequest.getRequestURI();

			if (uri.equals("/_vti_bin/shtml.dll/_vti_rpc") ||
				uri.equals("/sharepoint/_vti_bin/_vti_aut/author.dll")) {

				HttpSession httpSession = httpServletRequest.getSession();

				User user = (User)httpSession.getAttribute(WebKeys.USER);

				SharepointRequest sharepointRequest = new SharepointRequest(
					httpServletRequest, httpServletResponse, user);

				Method method = MethodFactory.create(sharepointRequest);

				String rootPath = method.getRootPath(sharepointRequest);

				if (rootPath == null) {
					throw new SharepointException("Unabled to get root path");
				}

				// LPS-12922

				if (_log.isInfoEnabled()) {
					_log.info("Original root path " + rootPath);
				}

				rootPath = WebDAVUtil.stripManualCheckInRequiredPath(rootPath);
				rootPath = WebDAVUtil.stripOfficeExtension(rootPath);
				rootPath = SharepointUtil.stripService(rootPath, true);

				if (_log.isInfoEnabled()) {
					_log.info("Modified root path " + rootPath);
				}

				sharepointRequest.setRootPath(rootPath);
				sharepointRequest.setSharepointStorage(
					SharepointUtil.getStorage(rootPath));

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							httpServletRequest.getHeader(
								HttpHeaders.USER_AGENT),
							" ", method.getMethodName(), " ", uri, " ",
							rootPath));
				}

				method.process(sharepointRequest);
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							httpServletRequest.getHeader(
								HttpHeaders.USER_AGENT),
							" ", httpServletRequest.getMethod(), " ", uri));
				}
			}
		}
		catch (SharepointException sharepointException) {
			_log.error(sharepointException);
		}
	}

	protected void vtiInfHtml(HttpServletResponse httpServletResponse)
		throws Exception {

		ServletResponseUtil.write(
			httpServletResponse,
			StringBundler.concat(
				"<!-- FrontPage Configuration Information", StringPool.NEW_LINE,
				" FPVersion=\"6.0.2.9999\"", StringPool.NEW_LINE,
				"FPShtmlScriptUrl=\"_vti_bin/shtml.dll/_vti_rpc\"",
				StringPool.NEW_LINE,
				"FPAuthorScriptUrl=\"_vti_bin/_vti_aut/author.dll\"",
				StringPool.NEW_LINE,
				"FPAdminScriptUrl=\"_vti_bin/_vti_adm/admin.dll\"",
				StringPool.NEW_LINE, "TPScriptUrl=\"_vti_bin/owssvr.dll\"",
				StringPool.NEW_LINE, "-->"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharepointServlet.class);

}