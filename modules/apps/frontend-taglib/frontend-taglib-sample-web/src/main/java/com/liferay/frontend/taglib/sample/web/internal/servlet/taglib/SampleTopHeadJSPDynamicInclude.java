/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.sample.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Ortega
 */
@Component(service = DynamicInclude.class)
public class SampleTopHeadJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String currentURL = (String)httpServletRequest.getAttribute(
			WebKeys.CURRENT_URL);

		PrintWriter printWriter = httpServletResponse.getWriter();

		if (StringUtil.matchesIgnoreCase(currentURL, "LPD-49303-firstLayout")) {
			printWriter.print(
				"<style id='temporaryStyle' data-senna-track='temporary'>");

			printWriter.println(
				"body{background-color:rgb(0, 255, 0)!important;}</style>");

			printWriter.print("<style id='mainStyle'>");

			printWriter.println(
				"body{background-color:rgb(0, 0, 255)!important;}</style>");
		}
		else if (StringUtil.matchesIgnoreCase(
					currentURL, "LPD-49303-secondLayout")) {

			printWriter.print(
				"<style id='temporaryStyle' data-senna-track='temporary'>");

			printWriter.print(
				"body{background-color:rgb(255, 0, 0)!important;");

			printWriter.println("color:rgb(1, 2, 3)!important}</style>");
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		boolean singlePageApplicationEnabled = GetterUtil.getBoolean(
			_props.get(PropsKeys.JAVASCRIPT_SINGLE_PAGE_APPLICATION_ENABLED));

		if (singlePageApplicationEnabled) {
			dynamicIncludeRegistry.register(
				"/html/common/themes/top_head.jsp#pre");
		}
	}

	@Override
	protected String getJspPath() {
		return null;
	}

	@Override
	protected Log getLog() {
		return null;
	}

	@Override
	protected ServletContext getServletContext() {
		return null;
	}

	@Reference
	private Props _props;

}