/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

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
	info = @Info(description = "Liferay Commerce Admin Catalog API. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.catalog.client', and version '4.0.74'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Catalog API", version = "v1.0")
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
			add(AttachmentResourceImpl.class);

			add(CatalogResourceImpl.class);

			add(CategoryResourceImpl.class);

			add(CurrencyResourceImpl.class);

			add(DiagramResourceImpl.class);

			add(GroupedProductResourceImpl.class);

			add(LinkedProductResourceImpl.class);

			add(ListTypeDefinitionResourceImpl.class);

			add(LowStockActionResourceImpl.class);

			add(MappedProductResourceImpl.class);

			add(OptionResourceImpl.class);

			add(OptionCategoryResourceImpl.class);

			add(OptionValueResourceImpl.class);

			add(PinResourceImpl.class);

			add(ProductResourceImpl.class);

			add(ProductAccountGroupResourceImpl.class);

			add(ProductChannelResourceImpl.class);

			add(ProductConfigurationResourceImpl.class);

			add(ProductConfigurationListResourceImpl.class);

			add(ProductConfigurationListAccountResourceImpl.class);

			add(ProductConfigurationListAccountGroupResourceImpl.class);

			add(ProductConfigurationListChannelResourceImpl.class);

			add(ProductConfigurationListOrderTypeResourceImpl.class);

			add(ProductGroupResourceImpl.class);

			add(ProductGroupProductResourceImpl.class);

			add(ProductOptionResourceImpl.class);

			add(ProductOptionValueResourceImpl.class);

			add(ProductShippingConfigurationResourceImpl.class);

			add(ProductSpecificationResourceImpl.class);

			add(ProductSubscriptionConfigurationResourceImpl.class);

			add(ProductTaxConfigurationResourceImpl.class);

			add(ProductVirtualSettingsResourceImpl.class);

			add(ProductVirtualSettingsFileEntryResourceImpl.class);

			add(RelatedProductResourceImpl.class);

			add(SkuResourceImpl.class);

			add(SkuSubscriptionConfigurationResourceImpl.class);

			add(SkuUnitOfMeasureResourceImpl.class);

			add(SkuVirtualSettingsResourceImpl.class);

			add(SkuVirtualSettingsFileEntryResourceImpl.class);

			add(SpecificationResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}