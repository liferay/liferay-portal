/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportImpl implements StaticSiteExport {

	public StaticSiteExportImpl(
		List<StaticSiteExportEntry> staticSiteExportEntries,
		StaticSiteExportReport staticSiteExportReport,
		List<StaticSiteExportResource> staticSiteExportResources) {

		_staticSiteExportEntries = staticSiteExportEntries;
		_staticSiteExportReport = staticSiteExportReport;
		_staticSiteExportResources = staticSiteExportResources;
	}

	@Override
	public void close() {
		for (StaticSiteExportResource staticSiteExportResource :
				_staticSiteExportResources) {

			FileUtil.delete(staticSiteExportResource.getFile());
		}
	}

	@Override
	public List<StaticSiteExportEntry> getStaticSiteExportEntries() {
		return Collections.unmodifiableList(_staticSiteExportEntries);
	}

	@Override
	public StaticSiteExportReport getStaticSiteExportReport() {
		return _staticSiteExportReport;
	}

	@Override
	public List<StaticSiteExportResource> getStaticSiteExportResources() {
		return Collections.unmodifiableList(_staticSiteExportResources);
	}

	private final List<StaticSiteExportEntry> _staticSiteExportEntries;
	private final StaticSiteExportReport _staticSiteExportReport;
	private final List<StaticSiteExportResource> _staticSiteExportResources;

}