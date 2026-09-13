/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Víctor Galán
 */
@ProviderType
public interface StaticSiteExportReport {

	public List<Failure> getLayoutFailures();

	public List<Failure> getResourceFailures();

	@ProviderType
	public interface Failure {

		public String getMessage();

		public String getURL();

	}

}