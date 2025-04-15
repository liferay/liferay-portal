/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.aui.form.extension.sample.internal;

import com.liferay.portal.kernel.servlet.taglib.TagDynamicInclude;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = TagDynamicInclude.class)
public class SampleFormTagDynamicInclude implements TagDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String tagClassName,
			String tagDynamicId, String tagPoint)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.println(
			"<h2>Liferay Portal Taglib AUI Form Extension Sample</h2><br />");
	}

	@Override
	public void register(TagDynamicIncludeRegistry tagDynamicIncludeRegistry) {
		tagDynamicIncludeRegistry.register(
			"com.liferay.taglib.aui.FormTag", PortletKeys.LOGIN + "-loginForm",
			"doStartTag#before");

		tagDynamicIncludeRegistry.register(
			"com.liferay.taglib.aui.FormTag",
			PortletKeys.LOGIN + "-loginFormModal", "doStartTag#before");
	}

}