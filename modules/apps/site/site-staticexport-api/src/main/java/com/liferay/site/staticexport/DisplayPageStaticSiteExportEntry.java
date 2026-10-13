/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.Locale;

/**
 * @author Víctor Galán
 */
public class DisplayPageStaticSiteExportEntry implements StaticSiteExportEntry {

	public DisplayPageStaticSiteExportEntry(
		String className, long classPK, String externalReferenceCode,
		String html, String layoutExternalReferenceCode, Locale locale,
		String path) {

		_className = className;
		_classPK = classPK;
		_externalReferenceCode = externalReferenceCode;
		_html = html;
		_layoutExternalReferenceCode = layoutExternalReferenceCode;
		_locale = locale;
		_path = path;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
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
		return StringBundler.concat(_className, StringPool.COLON, _classPK);
	}

	public String getLayoutExternalReferenceCode() {
		return _layoutExternalReferenceCode;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public String getPath() {
		return _path;
	}

	private final String _className;
	private final long _classPK;
	private final String _externalReferenceCode;
	private final String _html;
	private final String _layoutExternalReferenceCode;
	private final Locale _locale;
	private final String _path;

}