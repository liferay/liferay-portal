/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.AvailabilityEstimateResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.MeasurementUnitResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.WarehouseResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setAvailabilityEstimateResourceComponentServiceObjects(
		ComponentServiceObjects<AvailabilityEstimateResource>
			availabilityEstimateResourceComponentServiceObjects) {

		_availabilityEstimateResourceComponentServiceObjects =
			availabilityEstimateResourceComponentServiceObjects;
	}

	public static void setMeasurementUnitResourceComponentServiceObjects(
		ComponentServiceObjects<MeasurementUnitResource>
			measurementUnitResourceComponentServiceObjects) {

		_measurementUnitResourceComponentServiceObjects =
			measurementUnitResourceComponentServiceObjects;
	}

	public static void setTaxCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<TaxCategoryResource>
			taxCategoryResourceComponentServiceObjects) {

		_taxCategoryResourceComponentServiceObjects =
			taxCategoryResourceComponentServiceObjects;
	}

	public static void setWarehouseResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseResource>
			warehouseResourceComponentServiceObjects) {

		_warehouseResourceComponentServiceObjects =
			warehouseResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response deleteAvailabilityEstimate(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects,
			this::_populateResourceContext,
			availabilityEstimateResource ->
				availabilityEstimateResource.deleteAvailabilityEstimate(id));
	}

	@GraphQLField
	public Response deleteAvailabilityEstimateBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects,
			this::_populateResourceContext,
			availabilityEstimateResource ->
				availabilityEstimateResource.deleteAvailabilityEstimateBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response updateAvailabilityEstimate(
			@GraphQLName("id") Long id,
			@GraphQLName("availabilityEstimate") AvailabilityEstimate
				availabilityEstimate)
		throws Exception {

		return _applyComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects,
			this::_populateResourceContext,
			availabilityEstimateResource ->
				availabilityEstimateResource.putAvailabilityEstimate(
					id, availabilityEstimate));
	}

	@GraphQLField
	public Response updateAvailabilityEstimateBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects,
			this::_populateResourceContext,
			availabilityEstimateResource ->
				availabilityEstimateResource.putAvailabilityEstimateBatch(
					callbackURL, object));
	}

	@GraphQLField
	public AvailabilityEstimate
			createCommerceAdminSiteSettingGroupAvailabilityEstimate(
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("availabilityEstimate") AvailabilityEstimate
					availabilityEstimate)
		throws Exception {

		return _applyComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects,
			this::_populateResourceContext,
			availabilityEstimateResource ->
				availabilityEstimateResource.
					postCommerceAdminSiteSettingGroupAvailabilityEstimate(
						groupId, availabilityEstimate));
	}

	@GraphQLField
	public Response createMeasurementUnitsPageExportBatch(
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.postMeasurementUnitsPageExportBatch(
					_filterBiFunction.apply(
						measurementUnitResource, filterString),
					_sortsBiFunction.apply(
						measurementUnitResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public MeasurementUnit createMeasurementUnit(
			@GraphQLName("measurementUnit") MeasurementUnit measurementUnit)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.postMeasurementUnit(measurementUnit));
	}

	@GraphQLField
	public Response createMeasurementUnitBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.postMeasurementUnitBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteMeasurementUnitByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.
					deleteMeasurementUnitByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response patchMeasurementUnitByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("measurementUnit") MeasurementUnit measurementUnit)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.
					patchMeasurementUnitByExternalReferenceCode(
						externalReferenceCode, measurementUnit));
	}

	@GraphQLField
	public MeasurementUnit updateMeasurementUnitByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("measurementUnit") MeasurementUnit measurementUnit)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.
					putMeasurementUnitByExternalReferenceCode(
						externalReferenceCode, measurementUnit));
	}

	@GraphQLField
	public boolean deleteMeasurementUnitByKey(@GraphQLName("key") String key)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.deleteMeasurementUnitByKey(key));

		return true;
	}

	@GraphQLField
	public Response patchMeasurementUnitByKey(
			@GraphQLName("key") String key,
			@GraphQLName("measurementUnit") MeasurementUnit measurementUnit)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.patchMeasurementUnitByKey(
					key, measurementUnit));
	}

	@GraphQLField
	public boolean deleteMeasurementUnit(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.deleteMeasurementUnit(id));

		return true;
	}

	@GraphQLField
	public Response deleteMeasurementUnitBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.deleteMeasurementUnitBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response patchMeasurementUnit(
			@GraphQLName("id") Long id,
			@GraphQLName("measurementUnit") MeasurementUnit measurementUnit)
		throws Exception {

		return _applyComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects,
			this::_populateResourceContext,
			measurementUnitResource ->
				measurementUnitResource.patchMeasurementUnit(
					id, measurementUnit));
	}

	@GraphQLField
	public TaxCategory createCommerceAdminSiteSettingGroupTaxCategory(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("taxCategory") TaxCategory taxCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource ->
				taxCategoryResource.
					postCommerceAdminSiteSettingGroupTaxCategory(
						groupId, taxCategory));
	}

	@GraphQLField
	public Response deleteTaxCategory(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> taxCategoryResource.deleteTaxCategory(id));
	}

	@GraphQLField
	public Response deleteTaxCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> taxCategoryResource.deleteTaxCategoryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response updateTaxCategory(
			@GraphQLName("id") Long id,
			@GraphQLName("taxCategory") TaxCategory taxCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> taxCategoryResource.putTaxCategory(
				id, taxCategory));
	}

	@GraphQLField
	public Response updateTaxCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> taxCategoryResource.putTaxCategoryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Warehouse createCommerceAdminSiteSettingGroupWarehouse(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource ->
				warehouseResource.postCommerceAdminSiteSettingGroupWarehouse(
					groupId, warehouse));
	}

	@GraphQLField
	public Response deleteWarehouse(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.deleteWarehouse(id));
	}

	@GraphQLField
	public Response deleteWarehouseBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.deleteWarehouseBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response updateWarehouse(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.putWarehouse(id, warehouse));
	}

	@GraphQLField
	public Response updateWarehouseBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.putWarehouseBatch(
				callbackURL, object));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			AvailabilityEstimateResource availabilityEstimateResource)
		throws Exception {

		availabilityEstimateResource.setContextAcceptLanguage(_acceptLanguage);
		availabilityEstimateResource.setContextCompany(_company);
		availabilityEstimateResource.setContextHttpServletRequest(
			_httpServletRequest);
		availabilityEstimateResource.setContextHttpServletResponse(
			_httpServletResponse);
		availabilityEstimateResource.setContextUriInfo(_uriInfo);
		availabilityEstimateResource.setContextUser(_user);
		availabilityEstimateResource.setGroupLocalService(_groupLocalService);
		availabilityEstimateResource.setRoleLocalService(_roleLocalService);

		availabilityEstimateResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		availabilityEstimateResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			MeasurementUnitResource measurementUnitResource)
		throws Exception {

		measurementUnitResource.setContextAcceptLanguage(_acceptLanguage);
		measurementUnitResource.setContextCompany(_company);
		measurementUnitResource.setContextHttpServletRequest(
			_httpServletRequest);
		measurementUnitResource.setContextHttpServletResponse(
			_httpServletResponse);
		measurementUnitResource.setContextUriInfo(_uriInfo);
		measurementUnitResource.setContextUser(_user);
		measurementUnitResource.setGroupLocalService(_groupLocalService);
		measurementUnitResource.setRoleLocalService(_roleLocalService);

		measurementUnitResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		measurementUnitResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			TaxCategoryResource taxCategoryResource)
		throws Exception {

		taxCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		taxCategoryResource.setContextCompany(_company);
		taxCategoryResource.setContextHttpServletRequest(_httpServletRequest);
		taxCategoryResource.setContextHttpServletResponse(_httpServletResponse);
		taxCategoryResource.setContextUriInfo(_uriInfo);
		taxCategoryResource.setContextUser(_user);
		taxCategoryResource.setGroupLocalService(_groupLocalService);
		taxCategoryResource.setRoleLocalService(_roleLocalService);

		taxCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		taxCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(WarehouseResource warehouseResource)
		throws Exception {

		warehouseResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseResource.setContextCompany(_company);
		warehouseResource.setContextHttpServletRequest(_httpServletRequest);
		warehouseResource.setContextHttpServletResponse(_httpServletResponse);
		warehouseResource.setContextUriInfo(_uriInfo);
		warehouseResource.setContextUser(_user);
		warehouseResource.setGroupLocalService(_groupLocalService);
		warehouseResource.setRoleLocalService(_roleLocalService);

		warehouseResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<AvailabilityEstimateResource>
		_availabilityEstimateResourceComponentServiceObjects;
	private static ComponentServiceObjects<MeasurementUnitResource>
		_measurementUnitResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxCategoryResource>
		_taxCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseResource>
		_warehouseResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}