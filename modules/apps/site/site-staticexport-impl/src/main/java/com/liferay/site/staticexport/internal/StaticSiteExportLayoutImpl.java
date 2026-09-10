/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.site.staticexport.StaticSiteExportLayout;

import java.util.Locale;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportLayoutImpl implements StaticSiteExportLayout {

	public StaticSiteExportLayoutImpl(
		String html, Locale locale, String path, long plid) {

		_html = html;
		_locale = locale;
		_path = path;
		_plid = plid;
	}

	@Override
	public String getHTML() {
		return _html;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public long getPlid() {
		return _plid;
	}

	private final String _html;
	private final Locale _locale;
	private final String _path;
	private final long _plid;

}