/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal.servlet.taglib;

import com.liferay.frontend.editor.ckeditor.web.internal.constants.CKEditorConstants;
import com.liferay.portal.kernel.frontend.source.map.FrontendSourceMapUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Chema Balsas
 */
@Component(service = DynamicInclude.class)
public class CKEditorCreoleOnEditorCreateDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		Bundle bundle = _bundleContext.getBundle();

		URL entryURL = bundle.getEntry(
			"/META-INF/resources/ckeditor/extension" +
				"/creole_dialog_definition.js");

		try (InputStream inputStream = entryURL.openStream()) {
			FrontendSourceMapUtil.transferJS(
				inputStream, httpServletResponse.getOutputStream());
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.println();

		String toolbarSet = (String)httpServletRequest.getAttribute(
			CKEditorConstants.ATTRIBUTE_NAMESPACE + ":toolbarSet");

		if (toolbarSet.equals("creole")) {
			entryURL = bundle.getEntry(
				"/META-INF/resources/ckeditor/extension/creole_dialog_show.js");

			try (InputStream inputStream = entryURL.openStream()) {
				FrontendSourceMapUtil.transferJS(
					inputStream, httpServletResponse.getOutputStream());
			}

			printWriter.println();
		}
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register(
			"com.liferay.frontend.editor.ckeditor.web#ckeditor_creole#" +
				"onEditorCreate");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private BundleContext _bundleContext;

}