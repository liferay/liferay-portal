/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.vulcan.batch.engine.util;

import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.yaml.YAMLUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

/**
 * @author Matija Petanjek
 */
public class OpenAPIYAMLUtil {

	public static OpenAPIYAML getOpenAPIYAML(
			long companyId, String internalClassNameKey,
			OpenAPIResource openAPIResource,
			VulcanBatchEngineTaskItemDelegateRegistry
				vulcanBatchEngineTaskItemDelegateRegistry)
		throws Exception {

		VulcanBatchEngineTaskItemDelegate vulcanBatchEngineTaskItemDelegate =
			vulcanBatchEngineTaskItemDelegateRegistry.
				getVulcanBatchEngineTaskItemDelegate(
					companyId, internalClassNameKey);

		if (vulcanBatchEngineTaskItemDelegate == null) {
			throw new NotFoundException();
		}

		Response response = openAPIResource.getOpenAPI(
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

}