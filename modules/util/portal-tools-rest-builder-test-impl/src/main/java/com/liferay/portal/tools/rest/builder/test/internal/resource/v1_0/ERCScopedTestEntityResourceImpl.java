/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCScopedTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/erc-scoped-test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = ERCScopedTestEntityResource.class
)
public class ERCScopedTestEntityResourceImpl
	extends BaseERCScopedTestEntityResourceImpl {

	@Override
	protected Page<ERCScopedTestEntity>
			doGetAssetLibraryERCScopedTestEntitiesPage(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doGetAssetLibraryERCScopedTestEntity(
			String assetLibraryExternalReferenceCode,
			String ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected Page<ERCScopedTestEntity> doGetERCScopedTestEntitiesPage(
			String roleNames)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected Page<ERCScopedTestEntity> doGetSiteERCScopedTestEntitiesPage(
			String siteExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doGetSiteERCScopedTestEntity(
			String siteExternalReferenceCode,
			String ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doPostAssetLibraryERCScopedTestEntity(
			String assetLibraryExternalReferenceCode,
			ERCScopedTestEntity ercScopedTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doPostSiteERCScopedTestEntity(
			String siteExternalReferenceCode,
			ERCScopedTestEntity ercScopedTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doPutAssetLibraryERCScopedTestEntity(
			String assetLibraryExternalReferenceCode,
			String ercScopedTestEntityExternalReferenceCode,
			ERCScopedTestEntity ercScopedTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCScopedTestEntity doPutSiteERCScopedTestEntity(
			String siteExternalReferenceCode,
			String ercScopedTestEntityExternalReferenceCode,
			ERCScopedTestEntity ercScopedTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

}