/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.site.staticexport.StaticSiteExportResource;

import java.io.File;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceImpl implements StaticSiteExportResource {

	public StaticSiteExportResourceImpl(File file, String path, String url) {
		_file = file;
		_path = path;
		_url = url;
	}

	@Override
	public File getFile() {
		return _file;
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public String getURL() {
		return _url;
	}

	private final File _file;
	private final String _path;
	private final String _url;

}