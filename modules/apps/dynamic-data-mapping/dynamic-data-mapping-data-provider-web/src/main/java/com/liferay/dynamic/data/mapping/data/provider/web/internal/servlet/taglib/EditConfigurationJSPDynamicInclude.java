/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = DynamicInclude.class)
public class EditConfigurationJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			StringBundler.concat(
				"com.liferay.configuration.admin.web#/edit_configuration.jsp#",
				"com.liferay.dynamic.data.mapping.data.provider.configuration.",
				"DDMDataProviderConfiguration#pre"));
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
		EditConfigurationJSPDynamicInclude.class);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dynamic.data.mapping.data.provider.web)"
	)
	private ServletContext _servletContext;

}