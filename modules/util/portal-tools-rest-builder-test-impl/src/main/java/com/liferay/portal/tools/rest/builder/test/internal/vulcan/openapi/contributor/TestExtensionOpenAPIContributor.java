/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.vulcan.openapi.contributor;

import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = OpenAPIContributor.class)
public class TestExtensionOpenAPIContributor implements OpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext) {
		if ((openAPIContext == null) ||
			!Objects.equals(
				openAPIContext.getPath(),
				"/o/portal-tools-rest-builder-test/")) {

			return;
		}

		Components components = openAPI.getComponents();

		Map<String, Schema> schemas = components.getSchemas();

		Schema testEntitySchema = schemas.get("TestEntity");

		if (testEntitySchema == null) {
			return;
		}

		testEntitySchema.addExtension("x-test", true);
	}

}