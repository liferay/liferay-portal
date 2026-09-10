/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportImpl implements StaticSiteExport {

	public StaticSiteExportImpl(
		List<StaticSiteExportLayout> staticSiteExportLayouts) {

		_staticSiteExportLayouts = staticSiteExportLayouts;
	}

	@Override
	public List<StaticSiteExportLayout> getStaticSiteExportLayouts() {
		return Collections.unmodifiableList(_staticSiteExportLayouts);
	}

	private final List<StaticSiteExportLayout> _staticSiteExportLayouts;

}