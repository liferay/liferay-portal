/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport;

import java.util.Locale;

/**
 * @author Víctor Galán
 */
public class LayoutStaticSiteExportEntry implements StaticSiteExportEntry {

	public LayoutStaticSiteExportEntry(
		String externalReferenceCode, String html, Locale locale, String path) {

		_externalReferenceCode = externalReferenceCode;
		_html = html;
		_locale = locale;
		_path = path;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	@Override
	public String getHTML() {
		return _html;
	}

	@Override
	public String getKey() {
		return _externalReferenceCode;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public String getPath() {
		return _path;
	}

	private final String _externalReferenceCode;
	private final String _html;
	private final Locale _locale;
	private final String _path;

}