/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.EntityModelResourceTestEntity1Resource;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/entity-model-resource-test-entity1.properties",
	scope = ServiceScope.PROTOTYPE,
	service = EntityModelResourceTestEntity1Resource.class
)
public class EntityModelResourceTestEntity1ResourceImpl
	extends BaseEntityModelResourceTestEntity1ResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return Collections::emptyMap;
	}

}