/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0;

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltán Takács
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Back-office administration of Liferay Commerce site-setting entities: warehouse definitions, tax categories, measurement units, and availability estimates. All four entities are company-scoped on the server, although warehouse, tax category, and availability estimate paths still accept a Liferay site identifier (`groupId`) as a compatibility wire field. Used by store-launch wizards and admin dashboards. Primary entities: AvailabilityEstimate (restock-time windows shown on out-of-stock items), TaxCategory (groupings for tax treatment), Warehouse (warehouse definition; stock levels and per-account/group/channel scoping live in the admin-inventory API), and MeasurementUnit (units of measure used by SKUs and shipping rules). Unimplemented endpoints: the list (GET) and create (POST) operations under /commerceAdminSiteSetting/<groupId>/taxCategory and /commerceAdminSiteSetting/<groupId>/warehouse, and the replace (PUT) operations on the TaxCategory and Warehouse by-id paths, are not implemented and reject every request with a 400 Bad Request without invoking the underlying service; to persist tax categories or warehouse records, drive the back-end services directly or use the sibling admin APIs. AvailabilityEstimate is fully implemented, including addressing by external reference code (`/availabilityEstimate/by-externalReferenceCode/<externalReferenceCode>`, where PUT is upsert). Common workflows: Set up measurement units -- (1) POST /measurement-units (or PUT /measurement-units/by-key/<key>) to create each unit, setting primary=true for the base unit of each type (Dimensions, Weight, Unit); (2) GET /measurement-units/by-type/<measurementUnitType> to confirm the units of a given type before enabling a channel. -- Manage warehouse definitions -- this API only fetches single records (GET /warehouse/<id>) and deletes (DELETE /warehouse/<id>) warehouse definitions; the list (GET), create (POST), and replace (PUT) operations here are not implemented and reject every request with a 400 Bad Request, so per-SKU stock, replenishment, and account/group/channel scoping are administered through the admin-inventory API. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.site.setting.client', and version '4.0.44'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Site Setting API", version = "v1.0")
)
@Path("/v1.0")
public class OpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(
			@Context HttpServletRequest httpServletRequest,
			@PathParam("type") String type, @Context UriInfo uriInfo)
		throws Exception {

		Class<? extends OpenAPIResource> clazz = _openAPIResource.getClass();

		try {
			Method method = clazz.getMethod(
				"getOpenAPI", HttpServletRequest.class, Set.class, String.class,
				UriInfo.class);

			return (Response)method.invoke(
				_openAPIResource, httpServletRequest, _resourceClasses, type,
				uriInfo);
		}
		catch (NoSuchMethodException noSuchMethodException1) {
			try {
				Method method = clazz.getMethod(
					"getOpenAPI", Set.class, String.class, UriInfo.class);

				return (Response)method.invoke(
					_openAPIResource, _resourceClasses, type, uriInfo);
			}
			catch (NoSuchMethodException noSuchMethodException2) {
				return _openAPIResource.getOpenAPI(_resourceClasses, type);
			}
		}
	}

	@Reference
	private OpenAPIResource _openAPIResource;

	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			add(AvailabilityEstimateResourceImpl.class);

			add(MeasurementUnitResourceImpl.class);

			add(TaxCategoryResourceImpl.class);

			add(WarehouseResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-469443266