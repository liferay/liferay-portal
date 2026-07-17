/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;

import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class LiferayDocumentFormatRegistry implements DocumentFormatRegistry {

	@Override
	public DocumentFormat getFormatByFileExtension(String extension) {
		DocumentFormat documentFormat = _documentFormatsByExtension.get(
			extension);

		if (documentFormat != null) {
			return documentFormat;
		}

		return _documentFormatRegistry.getFormatByFileExtension(extension);
	}

	@Override
	public DocumentFormat getFormatByMimeType(String mimeType) {
		DocumentFormat documentFormat = _documentFormatsByMimeType.get(
			mimeType);

		if (documentFormat != null) {
			return documentFormat;
		}

		return _documentFormatRegistry.getFormatByMimeType(mimeType);
	}

	private static final Map<String, DocumentFormat>
		_documentFormatsByExtension =
			HashMapBuilder.<String, DocumentFormat>put(
				"css",
				new DocumentFormat(
					"CSS", DocumentFamily.TEXT, ContentTypes.TEXT_CSS, "css")
			).put(
				"java",
				new DocumentFormat(
					"Java", DocumentFamily.TEXT,
					ContentTypes.TEXT_X_JAVA_SOURCE, "java")
			).put(
				"js",
				new DocumentFormat(
					"Javascript", DocumentFamily.TEXT,
					ContentTypes.APPLICATION_JAVASCRIPT, "js")
			).put(
				"jsp",
				new DocumentFormat(
					"JSP", DocumentFamily.TEXT, ContentTypes.TEXT_X_JSP, "jsp")
			).put(
				"jspf",
				new DocumentFormat(
					"JSPF", DocumentFamily.TEXT, ContentTypes.TEXT_X_JSP,
					"jspf")
			).put(
				"sh",
				new DocumentFormat(
					"Shell Script", DocumentFamily.TEXT,
					ContentTypes.APPLICATION_X_SH, "sh")
			).build();
	private static final Map<String, DocumentFormat>
		_documentFormatsByMimeType = HashMapBuilder.<String, DocumentFormat>put(
			ContentTypes.APPLICATION_JAVASCRIPT,
			new DocumentFormat(
				"Javascript", DocumentFamily.TEXT,
				ContentTypes.APPLICATION_JAVASCRIPT, "js")
		).put(
			ContentTypes.APPLICATION_X_SH,
			new DocumentFormat(
				"Shell Script", DocumentFamily.TEXT,
				ContentTypes.APPLICATION_X_SH, "sh")
		).put(
			ContentTypes.TEXT_CSS,
			new DocumentFormat(
				"CSS", DocumentFamily.TEXT, ContentTypes.TEXT_CSS, "css")
		).put(
			ContentTypes.TEXT_X_JAVA_SOURCE,
			new DocumentFormat(
				"Java", DocumentFamily.TEXT, ContentTypes.TEXT_X_JAVA_SOURCE,
				"java")
		).put(
			ContentTypes.TEXT_X_JSP,
			new DocumentFormat(
				"JSP", DocumentFamily.TEXT, ContentTypes.TEXT_X_JSP, "jsp")
		).build();

	private final DocumentFormatRegistry _documentFormatRegistry =
		new DefaultDocumentFormatRegistry();

}