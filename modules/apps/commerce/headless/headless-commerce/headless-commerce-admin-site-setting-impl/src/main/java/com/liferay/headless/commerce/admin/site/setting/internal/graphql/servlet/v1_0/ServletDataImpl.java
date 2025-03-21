/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.site.setting.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.site.setting.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0.AvailabilityEstimateResourceImpl;
import com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0.MeasurementUnitResourceImpl;
import com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0.TaxCategoryResourceImpl;
import com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0.WarehouseResourceImpl;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.AvailabilityEstimateResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.MeasurementUnitResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.WarehouseResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Zoltán Takács
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAvailabilityEstimateResourceComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects);
		Mutation.setMeasurementUnitResourceComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects);
		Mutation.setTaxCategoryResourceComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects);
		Mutation.setWarehouseResourceComponentServiceObjects(
			_warehouseResourceComponentServiceObjects);

		Query.setAvailabilityEstimateResourceComponentServiceObjects(
			_availabilityEstimateResourceComponentServiceObjects);
		Query.setMeasurementUnitResourceComponentServiceObjects(
			_measurementUnitResourceComponentServiceObjects);
		Query.setTaxCategoryResourceComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects);
		Query.setWarehouseResourceComponentServiceObjects(
			_warehouseResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Site.Setting";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-site-setting-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteAvailabilityEstimate",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"deleteAvailabilityEstimate"));
					put(
						"mutation#deleteAvailabilityEstimateBatch",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"deleteAvailabilityEstimateBatch"));
					put(
						"mutation#updateAvailabilityEstimate",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"putAvailabilityEstimate"));
					put(
						"mutation#updateAvailabilityEstimateBatch",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"putAvailabilityEstimateBatch"));
					put(
						"mutation#createCommerceAdminSiteSettingGroupAvailabilityEstimate",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"postCommerceAdminSiteSettingGroupAvailabilityEstimate"));
					put(
						"mutation#createMeasurementUnitsPageExportBatch",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"postMeasurementUnitsPageExportBatch"));
					put(
						"mutation#createMeasurementUnit",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"postMeasurementUnit"));
					put(
						"mutation#createMeasurementUnitBatch",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"postMeasurementUnitBatch"));
					put(
						"mutation#deleteMeasurementUnitByExternalReferenceCode",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"deleteMeasurementUnitByExternalReferenceCode"));
					put(
						"mutation#patchMeasurementUnitByExternalReferenceCode",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"patchMeasurementUnitByExternalReferenceCode"));
					put(
						"mutation#updateMeasurementUnitByExternalReferenceCode",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"putMeasurementUnitByExternalReferenceCode"));
					put(
						"mutation#deleteMeasurementUnitByKey",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"deleteMeasurementUnitByKey"));
					put(
						"mutation#patchMeasurementUnitByKey",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"patchMeasurementUnitByKey"));
					put(
						"mutation#deleteMeasurementUnit",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"deleteMeasurementUnit"));
					put(
						"mutation#deleteMeasurementUnitBatch",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"deleteMeasurementUnitBatch"));
					put(
						"mutation#patchMeasurementUnit",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"patchMeasurementUnit"));
					put(
						"mutation#createCommerceAdminSiteSettingGroupTaxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"postCommerceAdminSiteSettingGroupTaxCategory"));
					put(
						"mutation#deleteTaxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"deleteTaxCategory"));
					put(
						"mutation#deleteTaxCategoryBatch",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"deleteTaxCategoryBatch"));
					put(
						"mutation#updateTaxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class, "putTaxCategory"));
					put(
						"mutation#updateTaxCategoryBatch",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"putTaxCategoryBatch"));
					put(
						"mutation#createCommerceAdminSiteSettingGroupWarehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"postCommerceAdminSiteSettingGroupWarehouse"));
					put(
						"mutation#deleteWarehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "deleteWarehouse"));
					put(
						"mutation#deleteWarehouseBatch",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"deleteWarehouseBatch"));
					put(
						"mutation#updateWarehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "putWarehouse"));
					put(
						"mutation#updateWarehouseBatch",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "putWarehouseBatch"));

					put(
						"query#availabilityEstimate",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"getAvailabilityEstimate"));
					put(
						"query#commerceAdminSettingGroupAvailabilityEstimate",
						new ObjectValuePair<>(
							AvailabilityEstimateResourceImpl.class,
							"getCommerceAdminSiteSettingGroupAvailabilityEstimatePage"));
					put(
						"query#measurementUnits",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"getMeasurementUnitsPage"));
					put(
						"query#measurementUnitByExternalReferenceCode",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"getMeasurementUnitByExternalReferenceCode"));
					put(
						"query#measurementUnitByKey",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"getMeasurementUnitByKey"));
					put(
						"query#measurementUnitsByType",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"getMeasurementUnitsByType"));
					put(
						"query#measurementUnit",
						new ObjectValuePair<>(
							MeasurementUnitResourceImpl.class,
							"getMeasurementUnit"));
					put(
						"query#commerceAdminSettingGroupTaxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"getCommerceAdminSiteSettingGroupTaxCategoryPage"));
					put(
						"query#taxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class, "getTaxCategory"));
					put(
						"query#commerceAdminSettingGroupWarehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"getCommerceAdminSiteSettingGroupWarehousePage"));
					put(
						"query#warehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "getWarehouse"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AvailabilityEstimateResource>
		_availabilityEstimateResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MeasurementUnitResource>
		_measurementUnitResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxCategoryResource>
		_taxCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseResource>
		_warehouseResourceComponentServiceObjects;

}