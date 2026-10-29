/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.internal.servlet.taglib;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.StackTraceUtil;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"dynamic.include.key=/errors/code.jsp#application/json",
		"mime.type=application/json"
	},
	service = DynamicInclude.class
)
public class ApplicationJSONPortalErrorCodeDynamicInclude
	extends BasePortalErrorCodeDynamicInclude {

	@Override
	protected void write(
		String message, PrintWriter printWriter, int statusCode) {

		printWriter.write(
			JSONUtil.put(
				"message", message
			).put(
				"statusCode", statusCode
			).toString());
	}

	@Override
	protected void write(
		String message, PrintWriter printWriter, String requestURI,
		int statusCode, Throwable throwable) {

		printWriter.write(
			JSONUtil.put(
				"message", message
			).put(
				"requestURI", requestURI
			).put(
				"statusCode", statusCode
			).put(
				"throwable",
				() -> {
					if (throwable != null) {
						return StackTraceUtil.getStackTrace(throwable);
					}

					return null;
				}
			).toString());
	}

}