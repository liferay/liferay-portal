/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.inventory.dto.v1_0.ReplenishmentItem;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccount;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccountGroup;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseChannel;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseItem;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseOrderType;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.ReplenishmentItemResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseAccountGroupResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseAccountResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseChannelResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseItemResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseOrderTypeResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setReplenishmentItemResourceComponentServiceObjects(
		ComponentServiceObjects<ReplenishmentItemResource>
			replenishmentItemResourceComponentServiceObjects) {

		_replenishmentItemResourceComponentServiceObjects =
			replenishmentItemResourceComponentServiceObjects;
	}

	public static void setWarehouseResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseResource>
			warehouseResourceComponentServiceObjects) {

		_warehouseResourceComponentServiceObjects =
			warehouseResourceComponentServiceObjects;
	}

	public static void setWarehouseAccountResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseAccountResource>
			warehouseAccountResourceComponentServiceObjects) {

		_warehouseAccountResourceComponentServiceObjects =
			warehouseAccountResourceComponentServiceObjects;
	}

	public static void setWarehouseAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseAccountGroupResource>
			warehouseAccountGroupResourceComponentServiceObjects) {

		_warehouseAccountGroupResourceComponentServiceObjects =
			warehouseAccountGroupResourceComponentServiceObjects;
	}

	public static void setWarehouseChannelResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseChannelResource>
			warehouseChannelResourceComponentServiceObjects) {

		_warehouseChannelResourceComponentServiceObjects =
			warehouseChannelResourceComponentServiceObjects;
	}

	public static void setWarehouseItemResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseItemResource>
			warehouseItemResourceComponentServiceObjects) {

		_warehouseItemResourceComponentServiceObjects =
			warehouseItemResourceComponentServiceObjects;
	}

	public static void setWarehouseOrderTypeResourceComponentServiceObjects(
		ComponentServiceObjects<WarehouseOrderTypeResource>
			warehouseOrderTypeResourceComponentServiceObjects) {

		_warehouseOrderTypeResourceComponentServiceObjects =
			warehouseOrderTypeResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean deleteReplenishmentItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.
					deleteReplenishmentItemByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public ReplenishmentItem patchReplenishmentItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("replenishmentItem") ReplenishmentItem
				replenishmentItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.
					patchReplenishmentItemByExternalReferenceCode(
						externalReferenceCode, replenishmentItem));
	}

	@GraphQLField
	public ReplenishmentItem updateReplenishmentItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("replenishmentItem") ReplenishmentItem
				replenishmentItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.
					putReplenishmentItemByExternalReferenceCode(
						externalReferenceCode, replenishmentItem));
	}

	@GraphQLField
	public boolean deleteReplenishmentItem(
			@GraphQLName("replenishmentItemId") Long replenishmentItemId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.deleteReplenishmentItem(
					replenishmentItemId));

		return true;
	}

	@GraphQLField
	public Response deleteReplenishmentItemBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.deleteReplenishmentItemBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ReplenishmentItem patchReplenishmentItem(
			@GraphQLName("replenishmentItemId") Long replenishmentItemId,
			@GraphQLName("replenishmentItem") ReplenishmentItem
				replenishmentItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.patchReplenishmentItem(
					replenishmentItemId, replenishmentItem));
	}

	@GraphQLField
	public Response createReplenishmentItemsPageExportBatch(
			@GraphQLName("sku") String sku,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.postReplenishmentItemsPageExportBatch(
					sku, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ReplenishmentItem createReplenishmentItem(
			@GraphQLName("warehouseId") Long warehouseId,
			@GraphQLName("sku") String sku,
			@GraphQLName("replenishmentItem") ReplenishmentItem
				replenishmentItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.postReplenishmentItem(
					warehouseId, sku, replenishmentItem));
	}

	@GraphQLField
	public Response createReplenishmentItemBatch(
			@GraphQLName("warehouseId") Long warehouseId,
			@GraphQLName("sku") String sku,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			replenishmentItemResource ->
				replenishmentItemResource.postReplenishmentItemBatch(
					warehouseId, sku, callbackURL, object));
	}

	@GraphQLField
	public Response createWarehousesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource ->
				warehouseResource.postWarehousesPageExportBatch(
					search,
					_filterBiFunction.apply(warehouseResource, filterString),
					_sortsBiFunction.apply(warehouseResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Warehouse createWarehouse(
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.postWarehouse(warehouse));
	}

	@GraphQLField
	public Response createWarehouseBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.postWarehouseBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteWarehouseByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource ->
				warehouseResource.deleteWarehouseByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response patchWarehouseByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource ->
				warehouseResource.patchWarehouseByExternalReferenceCode(
					externalReferenceCode, warehouse));
	}

	@GraphQLField
	public Warehouse updateWarehouseByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource ->
				warehouseResource.putWarehouseByExternalReferenceCode(
					externalReferenceCode, warehouse));
	}

	@GraphQLField
	public boolean deleteWarehouseId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.deleteWarehouseId(id));

		return true;
	}

	@GraphQLField
	public Response patchWarehouseId(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouse") Warehouse warehouse)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.patchWarehouseId(
				id, warehouse));
	}

	@GraphQLField
	public boolean deleteWarehouseAccount(
			@GraphQLName("warehouseAccountId") Long warehouseAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountResource ->
				warehouseAccountResource.deleteWarehouseAccount(
					warehouseAccountId));

		return true;
	}

	@GraphQLField
	public Response deleteWarehouseAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountResource ->
				warehouseAccountResource.deleteWarehouseAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public WarehouseAccount
			createWarehouseByExternalReferenceCodeWarehouseAccount(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("warehouseAccount") WarehouseAccount
					warehouseAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountResource ->
				warehouseAccountResource.
					postWarehouseByExternalReferenceCodeWarehouseAccount(
						externalReferenceCode, warehouseAccount));
	}

	@GraphQLField
	public WarehouseAccount createWarehouseIdWarehouseAccount(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseAccount") WarehouseAccount warehouseAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountResource ->
				warehouseAccountResource.postWarehouseIdWarehouseAccount(
					id, warehouseAccount));
	}

	@GraphQLField
	public Response createWarehouseIdWarehouseAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountResource ->
				warehouseAccountResource.postWarehouseIdWarehouseAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteWarehouseAccountGroup(
			@GraphQLName("warehouseAccountGroupId") Long
				warehouseAccountGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountGroupResource ->
				warehouseAccountGroupResource.deleteWarehouseAccountGroup(
					warehouseAccountGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteWarehouseAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountGroupResource ->
				warehouseAccountGroupResource.deleteWarehouseAccountGroupBatch(
					callbackURL, object));
	}

	@GraphQLField
	public WarehouseAccountGroup
			createWarehouseByExternalReferenceCodeWarehouseAccountGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("warehouseAccountGroup") WarehouseAccountGroup
					warehouseAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountGroupResource ->
				warehouseAccountGroupResource.
					postWarehouseByExternalReferenceCodeWarehouseAccountGroup(
						externalReferenceCode, warehouseAccountGroup));
	}

	@GraphQLField
	public WarehouseAccountGroup createWarehouseIdWarehouseAccountGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseAccountGroup") WarehouseAccountGroup
				warehouseAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountGroupResource ->
				warehouseAccountGroupResource.
					postWarehouseIdWarehouseAccountGroup(
						id, warehouseAccountGroup));
	}

	@GraphQLField
	public Response createWarehouseIdWarehouseAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseAccountGroupResource ->
				warehouseAccountGroupResource.
					postWarehouseIdWarehouseAccountGroupBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deleteWarehouseChannel(
			@GraphQLName("warehouseChannelId") Long warehouseChannelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseChannelResource ->
				warehouseChannelResource.deleteWarehouseChannel(
					warehouseChannelId));

		return true;
	}

	@GraphQLField
	public Response deleteWarehouseChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseChannelResource ->
				warehouseChannelResource.deleteWarehouseChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public WarehouseChannel
			createWarehouseByExternalReferenceCodeWarehouseChannel(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("warehouseChannel") WarehouseChannel
					warehouseChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseChannelResource ->
				warehouseChannelResource.
					postWarehouseByExternalReferenceCodeWarehouseChannel(
						externalReferenceCode, warehouseChannel));
	}

	@GraphQLField
	public WarehouseChannel createWarehouseIdWarehouseChannel(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseChannel") WarehouseChannel warehouseChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseChannelResource ->
				warehouseChannelResource.postWarehouseIdWarehouseChannel(
					id, warehouseChannel));
	}

	@GraphQLField
	public Response createWarehouseIdWarehouseChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseChannelResource ->
				warehouseChannelResource.postWarehouseIdWarehouseChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteWarehouseItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.
					deleteWarehouseItemByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response patchWarehouseItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.patchWarehouseItemByExternalReferenceCode(
					externalReferenceCode, warehouseItem));
	}

	@GraphQLField
	public WarehouseItem createWarehouseItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.postWarehouseItemByExternalReferenceCode(
					externalReferenceCode, warehouseItem));
	}

	@GraphQLField
	public WarehouseItem updateWarehouseItemByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.putWarehouseItemByExternalReferenceCode(
					externalReferenceCode, warehouseItem));
	}

	@GraphQLField
	public boolean deleteWarehouseItem(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource -> warehouseItemResource.deleteWarehouseItem(
				id));

		return true;
	}

	@GraphQLField
	public Response deleteWarehouseItemBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.deleteWarehouseItemBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response patchWarehouseItem(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource -> warehouseItemResource.patchWarehouseItem(
				id, warehouseItem));
	}

	@GraphQLField
	public WarehouseItem createWarehouseByExternalReferenceCodeWarehouseItem(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.
					postWarehouseByExternalReferenceCodeWarehouseItem(
						externalReferenceCode, warehouseItem));
	}

	@GraphQLField
	public WarehouseItem createWarehouseIdWarehouseItem(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseItem") WarehouseItem warehouseItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.postWarehouseIdWarehouseItem(
					id, warehouseItem));
	}

	@GraphQLField
	public Response createWarehouseIdWarehouseItemBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseItemResource ->
				warehouseItemResource.postWarehouseIdWarehouseItemBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteWarehouseOrderType(
			@GraphQLName("warehouseOrderTypeId") Long warehouseOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseOrderTypeResource ->
				warehouseOrderTypeResource.deleteWarehouseOrderType(
					warehouseOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deleteWarehouseOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseOrderTypeResource ->
				warehouseOrderTypeResource.deleteWarehouseOrderTypeBatch(
					callbackURL, object));
	}

	@GraphQLField
	public WarehouseOrderType
			createWarehouseByExternalReferenceCodeWarehouseOrderType(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("warehouseOrderType") WarehouseOrderType
					warehouseOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseOrderTypeResource ->
				warehouseOrderTypeResource.
					postWarehouseByExternalReferenceCodeWarehouseOrderType(
						externalReferenceCode, warehouseOrderType));
	}

	@GraphQLField
	public WarehouseOrderType createWarehouseIdWarehouseOrderType(
			@GraphQLName("id") Long id,
			@GraphQLName("warehouseOrderType") WarehouseOrderType
				warehouseOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseOrderTypeResource ->
				warehouseOrderTypeResource.postWarehouseIdWarehouseOrderType(
					id, warehouseOrderType));
	}

	@GraphQLField
	public Response createWarehouseIdWarehouseOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseOrderTypeResource ->
				warehouseOrderTypeResource.
					postWarehouseIdWarehouseOrderTypeBatch(
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
			ReplenishmentItemResource replenishmentItemResource)
		throws Exception {

		replenishmentItemResource.setContextAcceptLanguage(_acceptLanguage);
		replenishmentItemResource.setContextCompany(_company);
		replenishmentItemResource.setContextHttpServletRequest(
			_httpServletRequest);
		replenishmentItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		replenishmentItemResource.setContextUriInfo(_uriInfo);
		replenishmentItemResource.setContextUser(_user);
		replenishmentItemResource.setGroupLocalService(_groupLocalService);
		replenishmentItemResource.setRoleLocalService(_roleLocalService);

		replenishmentItemResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		replenishmentItemResource.setVulcanBatchEngineImportTaskResource(
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

	private void _populateResourceContext(
			WarehouseAccountResource warehouseAccountResource)
		throws Exception {

		warehouseAccountResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseAccountResource.setContextCompany(_company);
		warehouseAccountResource.setContextHttpServletRequest(
			_httpServletRequest);
		warehouseAccountResource.setContextHttpServletResponse(
			_httpServletResponse);
		warehouseAccountResource.setContextUriInfo(_uriInfo);
		warehouseAccountResource.setContextUser(_user);
		warehouseAccountResource.setGroupLocalService(_groupLocalService);
		warehouseAccountResource.setRoleLocalService(_roleLocalService);

		warehouseAccountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseAccountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WarehouseAccountGroupResource warehouseAccountGroupResource)
		throws Exception {

		warehouseAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseAccountGroupResource.setContextCompany(_company);
		warehouseAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		warehouseAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		warehouseAccountGroupResource.setContextUriInfo(_uriInfo);
		warehouseAccountGroupResource.setContextUser(_user);
		warehouseAccountGroupResource.setGroupLocalService(_groupLocalService);
		warehouseAccountGroupResource.setRoleLocalService(_roleLocalService);

		warehouseAccountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseAccountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WarehouseChannelResource warehouseChannelResource)
		throws Exception {

		warehouseChannelResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseChannelResource.setContextCompany(_company);
		warehouseChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		warehouseChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		warehouseChannelResource.setContextUriInfo(_uriInfo);
		warehouseChannelResource.setContextUser(_user);
		warehouseChannelResource.setGroupLocalService(_groupLocalService);
		warehouseChannelResource.setRoleLocalService(_roleLocalService);

		warehouseChannelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseChannelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WarehouseItemResource warehouseItemResource)
		throws Exception {

		warehouseItemResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseItemResource.setContextCompany(_company);
		warehouseItemResource.setContextHttpServletRequest(_httpServletRequest);
		warehouseItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		warehouseItemResource.setContextUriInfo(_uriInfo);
		warehouseItemResource.setContextUser(_user);
		warehouseItemResource.setGroupLocalService(_groupLocalService);
		warehouseItemResource.setRoleLocalService(_roleLocalService);

		warehouseItemResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseItemResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WarehouseOrderTypeResource warehouseOrderTypeResource)
		throws Exception {

		warehouseOrderTypeResource.setContextAcceptLanguage(_acceptLanguage);
		warehouseOrderTypeResource.setContextCompany(_company);
		warehouseOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		warehouseOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		warehouseOrderTypeResource.setContextUriInfo(_uriInfo);
		warehouseOrderTypeResource.setContextUser(_user);
		warehouseOrderTypeResource.setGroupLocalService(_groupLocalService);
		warehouseOrderTypeResource.setRoleLocalService(_roleLocalService);

		warehouseOrderTypeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		warehouseOrderTypeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<ReplenishmentItemResource>
		_replenishmentItemResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseResource>
		_warehouseResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseAccountResource>
		_warehouseAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseAccountGroupResource>
		_warehouseAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseChannelResource>
		_warehouseChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseItemResource>
		_warehouseItemResourceComponentServiceObjects;
	private static ComponentServiceObjects<WarehouseOrderTypeResource>
		_warehouseOrderTypeResourceComponentServiceObjects;

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