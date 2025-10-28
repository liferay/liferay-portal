/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.openapi.contributor;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(service = OpenAPIContributor.class)
public class ObjectDefinitionOpenAPIContributor implements OpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-51345") ||
			!StringUtil.equals(openAPIContext.getPath(), "/o/object-admin/")) {

			return;
		}

		Paths paths = openAPI.getPaths();

		for (String key : ListUtil.fromMapKeys(paths)) {
			PathItem pathItem = paths.get(key);

			if (key.endsWith("/object-definitions")) {
				_addAccumulateErrorParameter(pathItem.getPost());
			}
			else if (key.endsWith(
						"/object-definitions/by-external-reference-" +
							"code/{externalReferenceCode}")) {

				_addAccumulateErrorParameter(pathItem.getPut());
			}
		}
	}

	private void _addAccumulateErrorParameter(Operation operation) {
		List<Parameter> parameters = operation.getParameters();

		if (parameters == null) {
			parameters = new ArrayList<>();

			operation.setParameters(parameters);
		}

		parameters.add(
			new Parameter() {
				{
					in("query");
					name("accumulateError");
					schema(
						new BooleanSchema() {
							{
								setDefault(false);
							}
						});
				}
			});
	}

}