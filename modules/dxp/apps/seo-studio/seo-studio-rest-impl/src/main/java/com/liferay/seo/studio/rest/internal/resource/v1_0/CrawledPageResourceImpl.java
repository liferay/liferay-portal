/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.seo.studio.rest.resource.v1_0.CrawledPageResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brooke Dalton
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/crawled-page.properties",
	scope = ServiceScope.PROTOTYPE, service = CrawledPageResource.class
)
public class CrawledPageResourceImpl extends BaseCrawledPageResourceImpl {
}