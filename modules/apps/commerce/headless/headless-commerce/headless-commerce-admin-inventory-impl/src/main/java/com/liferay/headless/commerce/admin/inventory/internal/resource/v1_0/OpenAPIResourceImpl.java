/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Liferay Commerce Admin Inventory API exposes the inventory side of the Commerce administration surface -- warehouses, the items they stock, replenishment plans for those items, and the bindings that decide which warehouses can fulfill which accounts, account groups, channels, and order types. Primary entities -- Warehouse models a physical or logical fulfillment location with address, geo-coordinates, and an active flag. WarehouseItem tracks the on-hand and reserved quantity of a SKU at a single warehouse, scaled to a unit-of-measure key. ReplenishmentItem records expected restock quantities with an availability date. WarehouseAccount, WarehouseAccountGroup, WarehouseChannel, and WarehouseOrderType are the four binding entities that connect a warehouse to an AccountEntry, AccountGroup, CommerceChannel, and CommerceOrderType respectively, and decide which warehouse is eligible to source inventory for a given buyer or order type. Audience -- administrators of the Commerce inventory subsystem. The buyer-facing inventory view lives in the commerce-delivery-cart, commerce-delivery-catalog, and commerce-delivery-order modules; this API is the admin counterpart used to provision and maintain stock data. Addressing -- every primary resource is reachable either by the internal numeric id (/warehouses/<id>, /warehouseItems/<id>, /replenishment-items/<replenishmentItemId>) or by externalReferenceCode (/warehouses/by-externalReferenceCode/<externalReferenceCode>, /warehouseItems/by-externalReferenceCode/<externalReferenceCode>, /replenishment-items/by-externalReferenceCode/<externalReferenceCode>). PUT against the externalReferenceCode path is upsert -- the request body either replaces the existing entity or creates a new one carrying that externalReferenceCode. PATCH against either path is JSON Merge Patch -- only the fields supplied in the body are modified, every other field falls back to its persisted value. Binding entities -- WarehouseAccount, WarehouseAccountGroup, WarehouseChannel, and WarehouseOrderType live under both /warehouses/<id>/<rel> and /warehouses/by-externalReferenceCode/<externalReferenceCode>/<rel> for listing and creation, and under their own top-level path (/warehouse-accounts/<warehouseAccountId>, etc.) for deletion and sub-resource lookup. Bindings are not upserts -- creating a duplicate binding returns 409 for warehouse-channel bindings and 400 for the other three. Common workflows: Provision a warehouse -- POST /warehouses with externalReferenceCode, name, address, and geo-coordinates, then POST nested warehouse-channels, warehouse-accounts, warehouse-account-groups, and warehouse-order-types to make the warehouse eligible for that scope. Adjust stock -- PUT /warehouseItems/by-externalReferenceCode/<externalReferenceCode> to upsert the quantity and reservedQuantity of a SKU at a warehouse, or PATCH the same path to change only the quantity. Plan a restock -- PUT /replenishment-items/by-externalReferenceCode/<externalReferenceCode> with sku, warehouseId, quantity, and availabilityDate to record an expected delivery. Audit recent changes -- GET /warehouseItems/updated with start and end query parameters to retrieve every WarehouseItem modified in the requested timespan (defaulting to a 30-day window when only one bound is supplied). A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.inventory.client', and version '4.0.43'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Inventory API", version = "v1.0")
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
			add(AccountResourceImpl.class);

			add(AccountGroupResourceImpl.class);

			add(ChannelResourceImpl.class);

			add(OrderTypeResourceImpl.class);

			add(ReplenishmentItemResourceImpl.class);

			add(WarehouseResourceImpl.class);

			add(WarehouseAccountResourceImpl.class);

			add(WarehouseAccountGroupResourceImpl.class);

			add(WarehouseChannelResourceImpl.class);

			add(WarehouseItemResourceImpl.class);

			add(WarehouseOrderTypeResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:114630179