/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public abstract class BaseFilesSectionDisplayContextTestCase
	extends BaseSectionDisplayContextTestCase {

	@Override
	public HashMap<String, Object> getBaseAdditionalProps()
		throws PortalException {

		return new HashMapBuilder<>().putAll(
			super.getBaseAdditionalProps()
		).put(
			"fileMimeTypeCssClasses", _getFileMimeTypeCssClasses()
		).put(
			"fileMimeTypeIcons", _getFileMimeTypeIcons()
		).build();
	}

	private Map<String, String> _getFileMimeTypeCssClasses() {
		return HashMapBuilder.put(
			"application/pdf", "file-icon-color-5"
		).put(
			"default", "file-icon-color-0"
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/x-7z-compressed",
					"application/x-ace-compressed", "application/x-compressed",
					"application/x-rar-compressed",
					"application/x-zip-compressed", "application/zip"
				},
				"file-icon-color-1")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/excel", "application/vnd.ms-excel",
					"application/vnd.oasis.opendocument.spreadsheet",
					"application/vnd.openxmlformats-officedocument." +
						"spreadsheetml.sheet",
					"application/vnd.sun.xml.calc", "application/x-excel",
					"application/x-msexcel"
				},
				"file-icon-color-2")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/vnd+liferay.video.external.shortcut+html",
					"audio", "image", "video"
				},
				"file-icon-color-3")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/mspowerpoint", "application/powerpoint",
					"application/vnd.apple.keynote",
					"application/vnd.ms-powerpoint",
					"application/vnd.oasis.opendocument.presentation",
					"application/vnd.openxmlformats-officedocument." +
						"presentationml.presentation",
					"application/x-mspowerpoint"
				},
				"file-icon-color-4")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/msword",
					"application/vnd.oasis.opendocument.text",
					"application/vnd.openxmlformats-officedocument." +
						"wordprocessingml.document",
					"text/plain"
				},
				"file-icon-color-6")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/javascript", "text/asp", "text/css",
					"text/ecmascript", "text/html", "text/javascript",
					"text/x-c", "text/x-fortran", "text/x-java-source",
					"text/x-jsp", "text/x-pascal", "text/x-script.perl",
					"text/x-script.perl-module", "text/xml"
				},
				"file-icon-color-7")
		).build();
	}

	private Map<String, String> _getFileMimeTypeIcons() {
		return HashMapBuilder.put(
			"application/pdf", "document-vector"
		).put(
			"default", "document-default"
		).put(
			"image", "document-image"
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/javascript", "text/asp", "text/css",
					"text/ecmascript", "text/html", "text/javascript",
					"text/x-c", "text/x-fortran", "text/x-java-source",
					"text/x-jsp", "text/x-pascal", "text/x-script.perl",
					"text/x-script.perl-module", "text/xml"
				},
				"document-code")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/x-7z-compressed",
					"application/x-ace-compressed", "application/x-compressed",
					"application/x-rar-compressed",
					"application/x-zip-compressed", "application/zip"
				},
				"document-compressed")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/vnd+liferay.video.external.shortcut+html",
					"audio", "video"
				},
				"document-multimedia")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/mspowerpoint", "application/powerpoint",
					"application/vnd.apple.keynote",
					"application/vnd.ms-powerpoint",
					"application/vnd.oasis.opendocument.presentation",
					"application/vnd.openxmlformats-officedocument." +
						"presentationml.presentation",
					"application/x-mspowerpoint"
				},
				"document-presentation")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/excel", "application/vnd.ms-excel",
					"application/vnd.oasis.opendocument.spreadsheet",
					"application/vnd.openxmlformats-officedocument." +
						"spreadsheetml.sheet",
					"application/vnd.sun.xml.calc", "application/x-excel",
					"application/x-msexcel"
				},
				"document-table")
		).putAll(
			_getFileMimeTypeValues(
				new String[] {
					"application/msword",
					"application/vnd.oasis.opendocument.text",
					"application/vnd.openxmlformats-officedocument." +
						"wordprocessingml.document",
					"text/plain"
				},
				"document-text")
		).build();
	}

	private Map<String, String> _getFileMimeTypeValues(
		String[] mimeTypes, String value) {

		Map<String, String> fileMimeTypeValues = new HashMap<>();

		for (String mimeType : mimeTypes) {
			fileMimeTypeValues.put(mimeType, value);
		}

		return fileMimeTypeValues;
	}

}