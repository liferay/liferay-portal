/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v2_0;

import com.liferay.search.experiences.rest.resource.v2_0.SearchableAssetNameResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	properties = "OSGI-INF/liferay/rest/v2_0/searchable-asset-name.properties",
	scope = ServiceScope.PROTOTYPE, service = SearchableAssetNameResource.class
)
public class SearchableAssetNameResourceImpl
	extends BaseSearchableAssetNameResourceImpl {
}