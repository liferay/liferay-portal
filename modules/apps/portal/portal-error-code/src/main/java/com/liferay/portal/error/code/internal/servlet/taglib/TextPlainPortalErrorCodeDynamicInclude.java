/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.internal.servlet.taglib;

import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"dynamic.include.key=/errors/code.jsp#text/plain",
		"mime.type=text/plain"
	},
	service = DynamicInclude.class
)
public class TextPlainPortalErrorCodeDynamicInclude
	extends BasePortalErrorCodeDynamicInclude {

	@Override
	protected void write(
		String message, PrintWriter printWriter, int statusCode) {

		printWriter.print("Message: ");
		printWriter.println(message);

		printWriter.print("Status Code: ");
		printWriter.println(String.valueOf(statusCode));
	}

	@Override
	protected void write(
		String message, PrintWriter printWriter, String requestURI,
		int statusCode, Throwable throwable) {

		printWriter.print("Message: ");
		printWriter.println(message);

		printWriter.print("Request URI: ");
		printWriter.println(requestURI);

		printWriter.print("Status Code: ");
		printWriter.println(String.valueOf(statusCode));

		if (throwable != null) {
			printWriter.print("Throwable: ");

			throwable.printStackTrace(printWriter);
		}
	}

}