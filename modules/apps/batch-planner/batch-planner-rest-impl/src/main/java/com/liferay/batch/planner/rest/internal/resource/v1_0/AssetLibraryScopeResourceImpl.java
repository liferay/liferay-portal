/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.resource.v1_0;

import com.liferay.batch.planner.rest.resource.v1_0.AssetLibraryScopeResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Matija Petanjek
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-library-scope.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetLibraryScopeResource.class
)
public class AssetLibraryScopeResourceImpl
	extends BaseAssetLibraryScopeResourceImpl {
}