/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCAssetLibraryTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/erc-asset-library-test-entity.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ERCAssetLibraryTestEntityResource.class
)
public class ERCAssetLibraryTestEntityResourceImpl
	extends BaseERCAssetLibraryTestEntityResourceImpl {

	@Override
	protected Page<ERCAssetLibraryTestEntity>
			doGetAssetLibraryERCAssetLibraryTestEntitiesPage(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCAssetLibraryTestEntity
			doGetAssetLibraryERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCAssetLibraryTestEntity
			doPostAssetLibraryERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCAssetLibraryTestEntity
			doPutAssetLibraryERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				String ercAssetLibraryTestEntityExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

}