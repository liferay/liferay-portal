/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder;

import com.liferay.portal.url.builder.facet.BuildableAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.CDNAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.PathProxyAwareAbsolutePortalURLBuilder;

/**
 * Builds a URL to retrieve a JavaScript fileusing the new caching architecture
 * based on hashed file names.
 *
 * @author Iván Zaera Avellón
 */
public interface WebContextScriptAbsolutePortalURLBuilder
	extends BuildableAbsolutePortalURLBuilder,
			CDNAwareAbsolutePortalURLBuilder
				<WebContextScriptAbsolutePortalURLBuilder>,
			PathProxyAwareAbsolutePortalURLBuilder
				<WebContextScriptAbsolutePortalURLBuilder> {
}