/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.servlet.taglib;

import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveWebKeys;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DynamicInclude.class)
public class OneDriveViewPostJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		_setRedirectingOAuth2RequestAttributes(httpServletRequest);

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.document.library.web#/document_library/view.jsp#post");
		dynamicIncludeRegistry.register(
			"com.liferay.document.library.web#/document_library" +
				"/view_file_entry.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/view_post.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private void _setRedirectingOAuth2RequestAttributes(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		JSONObject jsonObject = (JSONObject)httpSession.getAttribute(
			DLOpenerOneDriveWebKeys.
				DL_OPENER_ONE_DRIVE_REDIRECTING_OAUTH2_JSON_OBJECT);

		if (jsonObject != null) {
			httpSession.removeAttribute(
				DLOpenerOneDriveWebKeys.
					DL_OPENER_ONE_DRIVE_REDIRECTING_OAUTH2_JSON_OBJECT);

			for (String fieldName : jsonObject.keySet()) {
				httpServletRequest.setAttribute(
					fieldName, jsonObject.getString(fieldName));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OneDriveViewPostJSPDynamicInclude.class);

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.opener.onedrive.web)"
	)
	private ServletContext _servletContext;

}