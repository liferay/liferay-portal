/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.servlet.taglib;

import com.liferay.document.library.kernel.util.VideoConverter;
import com.liferay.document.library.video.internal.converter.DLVideoFFMPEGVideoConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DynamicInclude.class)
public class DLVideoFFMPEGEditConfigurationJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		httpServletRequest.setAttribute(
			DLVideoFFMPEGVideoConverter.class.getName(),
			_dlVideoFFMPEGVideoConverter);

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			StringBundler.concat(
				"com.liferay.configuration.admin.web#/edit_configuration.jsp#",
				"com.liferay.document.library.video.internal.configuration.",
				"DLVideoFFMPEGVideoConverterConfiguration#pre"));
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.configuration.admin.web" +
			"/edit_configuration.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLVideoFFMPEGEditConfigurationJSPDynamicInclude.class);

	@Reference(
		target = "(component.name=com.liferay.document.library.video.internal.converter.DLVideoFFMPEGVideoConverter)"
	)
	private VideoConverter _dlVideoFFMPEGVideoConverter;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.video)"
	)
	private ServletContext _servletContext;

}