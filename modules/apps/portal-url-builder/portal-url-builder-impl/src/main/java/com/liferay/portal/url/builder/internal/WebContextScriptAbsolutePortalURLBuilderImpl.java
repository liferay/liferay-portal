/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.url.builder.WebContextScriptAbsolutePortalURLBuilder;

/**
 * @author Iván Zaera Avellón
 */
public class WebContextScriptAbsolutePortalURLBuilderImpl
	extends BaseWebContextResourceAbsolutePortalURLBuilderImpl
		<WebContextScriptAbsolutePortalURLBuilder>
	implements WebContextScriptAbsolutePortalURLBuilder {

	public WebContextScriptAbsolutePortalURLBuilderImpl(
		String cdnHost, HashedFilesRegistry hashedFilesRegistry,
		String pathModule, String pathProxy, String scriptPath,
		String webContextPath) {

		super(
			cdnHost, hashedFilesRegistry, pathModule, pathProxy, scriptPath,
			webContextPath);
	}

}