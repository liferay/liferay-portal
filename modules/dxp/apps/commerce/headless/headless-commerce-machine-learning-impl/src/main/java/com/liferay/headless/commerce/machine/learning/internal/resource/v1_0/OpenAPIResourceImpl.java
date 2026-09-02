/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.resource.v1_0;

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
 * @author Riccardo Ferrari
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Liferay Commerce machine-learning artefacts surfaced as a REST API. Three read endpoints expose monthly forecasts produced by the Analytics Cloud training pipeline -- demand at SKU granularity and revenue at account and account+category granularity. The same schema set also defines the wire shapes the Analytics Cloud dispatch executors use to upload commerce data (orders, products, channels, categories, SKUs) and to download trained models that materialise into product, user, and frequent-pattern recommendation rows consumed by in-portal recommendation widgets. -- Perspective -- the read endpoints are admin-facing analytics consumed by back-office dashboards; the upload and download schemas are cross-cutting and not exposed as paths. -- DXP-only -- this is the only headless commerce API that ships exclusively in DXP and not in CE. Primary entities -- AccountCategoryForecast (per-account, per-category monthly revenue forecast point), AccountForecast (per-account monthly revenue forecast point), SkuForecast (per-SKU monthly demand forecast point), Order/OrderItem and Product/ProductChannel/Sku/Category (ingestion-side wire shapes), and FrequentPatternRecommendation / ProductContentRecommendation / ProductInteractionRecommendation / UserRecommendation (download-side wire shapes). -- Common workflows: SKU demand forecasting -- GET /skuForecasts/by-monthlyDemand with optional skus, forecastLength (default 3), historyLength (default 8), and forecastStartDate; consume forecast together with forecastLowerBound and forecastUpperBound to bracket inventory replanning. -- Account revenue forecasting -- GET /accountForecasts/by-monthlyRevenue with optional accountIds, forecastLength, historyLength, and forecastStartDate; the response is filtered to the accountIds the caller has VIEW permission on through CommerceAccountPermissionHelper. -- Account x category revenue forecasting -- GET /accountCategoryForecasts/by-monthlyRevenue with the same dimensions plus categoryIds to drill into per-segment revenue. -- Analytics Cloud round-trip -- the AnalyticsUpload*DispatchTaskExecutor jobs in this module push Order/Product/ProductChannel/Category/Sku payloads shaped by the schemas here up to Analytics Cloud; the AnalyticsDownload*ForecastDispatchTaskExecutor and AnalyticsDownload*RecommendDispatchTaskExecutor jobs then materialise the trained models back into the forecast and recommendation rows surfaced by these endpoints and by the storefront recommendation widgets. -- Naming caveat -- the SKU operation is published as getSkuForecastsByMonthlyRevenuePage for backward client compatibility but the path is /by-monthlyDemand and the underlying call is getMonthlyQuantitySkuCommerceMLForecasts -- the operationId name is historical and does not describe the response (which carries demand quantity, not revenue). A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.machine.learning.client', and version '1.0.35'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Machine Learning API", version = "v1.0")
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
			add(AccountCategoryForecastResourceImpl.class);

			add(AccountForecastResourceImpl.class);

			add(SkuForecastResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-2009671479