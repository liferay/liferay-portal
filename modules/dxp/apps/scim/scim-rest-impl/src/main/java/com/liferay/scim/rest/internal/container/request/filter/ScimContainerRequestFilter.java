/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.container.request.filter;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;

import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Scim.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=ScimContainerRequestFilter"
	},
	service = ContainerRequestFilter.class
)
public class ScimContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		if (_setEndpointURLMap.getAndSet(false)) {
			String basePath = UriInfoUtil.getBasePath(
				containerRequestContext.getUriInfo());

			AbstractResourceManager.setEndpointURLMap(
				HashMapBuilder.put(
					SCIMConstants.GROUP_ENDPOINT, basePath + "v1.0/v2/Groups"
				).put(
					SCIMConstants.RESOURCE_TYPE_ENDPOINT,
					basePath + "v1.0/v2/ResourceTypes"
				).put(
					SCIMConstants.SCHEMAS_ENDPOINT, basePath + "v1.0/v2/Schemas"
				).put(
					SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT,
					basePath + "v1.0/v2/ServiceProviderConfig"
				).put(
					SCIMConstants.USER_ENDPOINT, basePath + "v1.0/v2/Users"
				).build());
		}
	}

	private static final AtomicBoolean _setEndpointURLMap = new AtomicBoolean(
		true);

}