/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.openapi.contributor;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.action.ActionInfo;
import com.liferay.portal.vulcan.dto.action.DTOActionProvider;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;
import com.liferay.portal.vulcan.util.ActionUtil;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Carlos Correa
 */
@Component(service = OpenAPIContributor.class)
public class ActionsOpenAPIContributor implements OpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-180090")) {
			return;
		}

		Map<String, Schema> individualSchemas = _getIndividualSchemas(openAPI);

		for (Map.Entry<String, Schema> entry : individualSchemas.entrySet()) {
			DTOActionProvider dtoActionProvider = _serviceTrackerMap.getService(
				entry.getKey());

			if (dtoActionProvider == null) {
				continue;
			}

			Schema schema = entry.getValue();

			Map<String, Schema> properties = schema.getProperties();

			Schema actionsSchema = properties.get("actions");

			Map<String, Schema> actionSchemas = new HashMap<>();

			Map<String, ActionInfo> actionInfos =
				dtoActionProvider.getActionInfos();

			for (Map.Entry<String, ActionInfo> actionInfoEntry :
					actionInfos.entrySet()) {

				ActionInfo actionInfo = actionInfoEntry.getValue();

				if ((actionInfo == null) ||
					(actionInfo.getActionName() == null) ||
					(actionInfo.getResourceMethodName() == null)) {

					continue;
				}

				actionSchemas.put(
					actionInfoEntry.getKey(),
					_getActionSchema(
						actionInfoEntry.getKey(), actionInfoEntry.getValue(),
						openAPIContext.getUriInfo()));
			}

			properties.put(
				"actions",
				new MapSchema() {
					{
						setDescription(actionsSchema.getDescription());
						setProperties(actionSchemas);
						setReadOnly(actionsSchema.getReadOnly());
					}
				});
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DTOActionProvider.class, "dto.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Schema _getActionSchema(
		String actionName, ActionInfo actionInfo, UriInfo uriInfo) {

		Map<String, String> map = ActionUtil.addAction(
			actionName, actionInfo.getResourceClass(), null,
			actionInfo.getResourceMethodName(),
			(ModelResourcePermission<?>)null, (Long)null, uriInfo);

		return new MapSchema() {
			{
				setProperties(
					HashMapBuilder.put(
						"href", _getStringSchema(map.get("href"))
					).put(
						"method", _getStringSchema(map.get("method"))
					).build());
			}
		};
	}

	private Map<String, Schema> _getIndividualSchemas(OpenAPI openAPI) {
		Map<String, Schema> individualSchemas = new HashMap<>();

		Components components = openAPI.getComponents();

		if (components == null) {
			return individualSchemas;
		}

		Map<String, Schema> schemas = components.getSchemas();

		if (schemas == null) {
			return individualSchemas;
		}

		for (Schema schema : schemas.values()) {
			Map<String, Schema> properties = schema.getProperties();

			if ((properties == null) || !properties.containsKey("actions") ||
				!properties.containsKey("x-class-name")) {

				continue;
			}

			Schema xClassNameSchema = properties.get("x-class-name");

			individualSchemas.put(
				(String)xClassNameSchema.getDefault(), schema);
		}

		return individualSchemas;
	}

	private Schema _getStringSchema(String defaultValue) {
		Schema schema = new StringSchema();

		schema.setDefault(defaultValue);

		return schema;
	}

	private ServiceTrackerMap<String, DTOActionProvider> _serviceTrackerMap;

}