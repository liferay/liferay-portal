/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCSiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCSiteTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/erc-site-test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = ERCSiteTestEntityResource.class
)
public class ERCSiteTestEntityResourceImpl
	extends BaseERCSiteTestEntityResourceImpl {

	@Override
	protected Page<ERCSiteTestEntity> doGetSiteERCSiteTestEntitiesPage(
			String siteExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCSiteTestEntity doGetSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			String ercSiteTestEntityExternalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCSiteTestEntity doPostSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	protected ERCSiteTestEntity doPutSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			String ercSiteTestEntityExternalReferenceCode,
			ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

}