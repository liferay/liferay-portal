/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.url.builder.WebContextStylesheetAbsolutePortalURLBuilder;

/**
 * @author Iván Zaera Avellón
 */
public class WebContextStylesheetAbsolutePortalURLBuilderImpl
	extends BaseWebContextResourceAbsolutePortalURLBuilderImpl
		<WebContextStylesheetAbsolutePortalURLBuilder>
	implements WebContextStylesheetAbsolutePortalURLBuilder {

	public WebContextStylesheetAbsolutePortalURLBuilderImpl(
		String cdnHost, HashedFilesRegistry hashedFilesRegistry,
		String pathModule, String pathProxy, String stylesheetPath,
		String webContextPath) {

		super(
			cdnHost, hashedFilesRegistry, pathModule, pathProxy, stylesheetPath,
			webContextPath);
	}

}