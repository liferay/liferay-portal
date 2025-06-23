/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.resource.v1_0.AssetLibraryTestEntityResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-library-test-entity.properties",
	scope = ServiceScope.PROTOTYPE,
	service = AssetLibraryTestEntityResource.class
)
public class AssetLibraryTestEntityResourceImpl
	extends BaseAssetLibraryTestEntityResourceImpl {
}