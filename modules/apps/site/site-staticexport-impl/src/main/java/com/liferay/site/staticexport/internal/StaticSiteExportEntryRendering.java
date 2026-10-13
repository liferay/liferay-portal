/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.site.staticexport.StaticSiteExportEntry;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportEntryRendering {

	public StaticSiteExportEntryRendering(
		Set<String> friendlyURLs, String key, Locale locale, String path,
		StaticSiteExportDocument staticSiteExportDocument,
		Function<String, StaticSiteExportEntry> staticSiteExportEntryFunction) {

		_friendlyURLs = friendlyURLs;
		_key = key;
		_locale = locale;
		_path = path;
		_staticSiteExportDocument = staticSiteExportDocument;
		_staticSiteExportEntryFunction = staticSiteExportEntryFunction;
	}

	public Set<String> getFriendlyURLs() {
		return _friendlyURLs;
	}

	public String getKey() {
		return _key;
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getPath() {
		return _path;
	}

	public StaticSiteExportDocument getStaticSiteExportDocument() {
		return _staticSiteExportDocument;
	}

	public StaticSiteExportEntry toStaticSiteExportEntry() {
		return _staticSiteExportEntryFunction.apply(
			_staticSiteExportDocument.getHTML());
	}

	private final Set<String> _friendlyURLs;
	private final String _key;
	private final Locale _locale;
	private final String _path;
	private final StaticSiteExportDocument _staticSiteExportDocument;
	private final Function<String, StaticSiteExportEntry>
		_staticSiteExportEntryFunction;

}