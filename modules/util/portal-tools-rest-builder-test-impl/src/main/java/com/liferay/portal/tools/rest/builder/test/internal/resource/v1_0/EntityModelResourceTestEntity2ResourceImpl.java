/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.tools.rest.builder.test.internal.entity.v1_0.EntityModelResourceTestEntity2EntityModel;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.EntityModelResourceTestEntity2Resource;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/entity-model-resource-test-entity2.properties",
	scope = ServiceScope.PROTOTYPE,
	service = EntityModelResourceTestEntity2Resource.class
)
public class EntityModelResourceTestEntity2ResourceImpl
	extends BaseEntityModelResourceTestEntity2ResourceImpl
	implements EntityModelResource {

	@Override
	public EntityModel getEntityModel(MultivaluedMap<?, ?> multivaluedMap)
		throws Exception {

		return new EntityModelResourceTestEntity2EntityModel();
	}

}