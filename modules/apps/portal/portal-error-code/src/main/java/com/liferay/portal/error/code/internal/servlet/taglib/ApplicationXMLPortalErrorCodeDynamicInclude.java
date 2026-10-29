/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.internal.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"dynamic.include.key=/errors/code.jsp#application/xml",
		"mime.type=application/xml"
	},
	service = DynamicInclude.class
)
public class ApplicationXMLPortalErrorCodeDynamicInclude
	extends BasePortalErrorCodeDynamicInclude {

	@Override
	protected void write(
		String message, PrintWriter printWriter, int statusCode) {

		Document document = _saxReader.createDocument(StringPool.UTF8);

		Element errorElement = document.addElement("error");

		Element messageElement = errorElement.addElement("message");

		messageElement.addText(message);

		Element statusCodeElement = errorElement.addElement("status-code");

		statusCodeElement.addText(String.valueOf(statusCode));

		printWriter.print(document.asXML());
	}

	@Override
	protected void write(
		String message, PrintWriter printWriter, String requestURI,
		int statusCode, Throwable throwable) {

		Document document = _saxReader.createDocument(StringPool.UTF8);

		Element errorElement = document.addElement("error");

		Element messageElement = errorElement.addElement("message");

		messageElement.addText(message);

		Element requestURIElement = errorElement.addElement("request-uri");

		requestURIElement.addText(requestURI);

		Element statusCodeElement = errorElement.addElement("status-code");

		statusCodeElement.addText(String.valueOf(statusCode));

		if (throwable != null) {
			Element throwableElement = errorElement.addElement("throwable");

			throwableElement.addCDATA(StackTraceUtil.getStackTrace(throwable));
		}

		printWriter.print(document.asXML());
	}

	@Reference
	private SAXReader _saxReader;

}