/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.vulcan.yaml.openapi;

import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.yaml.YAMLUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = OpenAPIYAMLProvider.class)
public class OpenAPIYAMLProvider {

	public OpenAPIYAML getOpenAPIYAML(
			long companyId, String internalClassNameKey)
		throws Exception {

		VulcanBatchEngineTaskItemDelegate vulcanBatchEngineTaskItemDelegate =
			_vulcanBatchEngineTaskItemDelegateRegistry.
				getVulcanBatchEngineTaskItemDelegate(
					companyId, internalClassNameKey);

		if (vulcanBatchEngineTaskItemDelegate == null) {
			throw new NotFoundException();
		}

		Response response = _openAPIResource.getOpenAPI(
			Collections.singleton(
				vulcanBatchEngineTaskItemDelegate.getResourceClass()),
			"yaml");

		if (response.getStatus() != 200) {
			throw new IllegalArgumentException(
				"Unable to find OpenAPI specification for " +
					internalClassNameKey);
		}

		return YAMLUtil.loadOpenAPIYAML((String)response.getEntity());
	}

	@Reference
	private OpenAPIResource _openAPIResource;

	@Reference
	private VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

}