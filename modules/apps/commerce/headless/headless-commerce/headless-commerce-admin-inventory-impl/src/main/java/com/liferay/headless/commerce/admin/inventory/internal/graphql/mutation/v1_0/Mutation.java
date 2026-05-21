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

	@GraphQLField(
		description = "Deletes the replenishment item identified by replenishmentItemId. Calls CommerceInventoryReplenishmentItemService.deleteCommerceInventoryReplenishmentItem. Validation -- NoSuchInventoryReplenishmentItemException -> 404 when the id does not resolve."
	)
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

	@GraphQLField(
		description = "Deletes the replenishment item identified by externalReferenceCode. Calls CommerceInventoryReplenishmentItemService.deleteCommerceInventoryReplenishmentItem after looking the row up by externalReferenceCode within the request company. Validation -- NoSuchInventoryReplenishmentItemException -> 404 when the externalReferenceCode does not resolve."
	)
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

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the replenishment item identified by replenishmentItemId. Only the fields supplied in the body are modified -- availabilityDate and quantity fall back to the persisted value when omitted; externalReferenceCode in the body, when present, is written to the row. Validation -- CommerceInventoryReplenishmentQuantityException -> 400 when quantity is non-positive."
	)
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

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the replenishment item identified by externalReferenceCode. Only the fields supplied in the body are modified -- availabilityDate, quantity, and unitOfMeasureKey fall back to the persisted value when omitted. Validation -- NoSuchInventoryReplenishmentItemException -> 404 when the externalReferenceCode does not resolve; CommerceInventoryReplenishmentQuantityException -> 400 when quantity is non-positive."
	)
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

	@GraphQLField(
		description = "Creates a replenishment item for the SKU at the warehouse identified by warehouseId. Validation -- the underlying warehouse item (warehouseId, sku, default unit of measure) must exist (NoSuchInventoryWarehouseItemException -> 404 otherwise); CommerceInventoryReplenishmentQuantityException -> 400 when quantity is non-positive; DuplicateCommerceInventoryReplenishmentItemException -> 400 when externalReferenceCode collides with an existing entry."
	)
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

	@GraphQLField(
		description = "Upserts the replenishment item identified by externalReferenceCode. When a row with that externalReferenceCode already exists, every field is replaced (nulls are coerced to defaults). When it does not, a new replenishment item is created. Validation -- the underlying warehouse item identified by warehouseId and sku must exist (NoSuchInventoryWarehouseItemException -> 404 otherwise); duplicate externalReferenceCode raises DuplicateCommerceInventoryReplenishmentItemException -> 400."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse identified by externalReferenceCode. Validation -- NoSuchInventoryWarehouseException -> 404 when the externalReferenceCode does not resolve. Side effects -- cascades through all warehouse-item rows, all warehouse-account, warehouse-account-group, and warehouse-order-type bindings, and all warehouse-channel bindings that reference the warehouse."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse identified by id. Validation -- NoSuchInventoryWarehouseException -> 404 when the id does not resolve. Side effects -- cascades through all warehouse-item rows, all warehouse-account, warehouse-account-group, and warehouse-order-type bindings, and all warehouse-channel bindings that reference the warehouse."
	)
	public boolean deleteWarehouseId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_warehouseResourceComponentServiceObjects,
			this::_populateResourceContext,
			warehouseResource -> warehouseResource.deleteWarehouseId(id));

		return true;
	}

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the warehouse identified by externalReferenceCode. Each null field in the body falls back to the persisted value. Side effects -- optional nested warehouseItems are cascaded through addOrUpdateCommerceInventoryWarehouseItem. Returns 200 OK on success."
	)
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

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the warehouse identified by id. Each null field in the body falls back to the persisted value. Side effects -- optional nested warehouseItems are cascaded through addOrUpdateCommerceInventoryWarehouseItem. Returns 204 No Content on success."
	)
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

	@GraphQLField(
		description = "Creates a warehouse, or updates an existing one with the same externalReferenceCode supplied in the body (upsert by externalReferenceCode). Side effects -- on create, active defaults to true and latitude/longitude default to 0.0; optional nested warehouseItems are cascaded through addOrUpdateCommerceInventoryWarehouseItem. Validation -- CommerceInventoryWarehouseNameException -> 400 when name is blank."
	)
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

	@GraphQLField(
		description = "Upserts the warehouse identified by externalReferenceCode. When a warehouse with that externalReferenceCode already exists, every field is replaced (nulls become defaults -- empty strings, 0.0 for coordinates, false for active). When it does not, a new warehouse is created. Validation -- CommerceInventoryWarehouseNameException -> 400 when name is blank. Side effects -- optional nested warehouseItems cascade through addOrUpdateCommerceInventoryWarehouseItem."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse-account binding identified by warehouseAccountId. Calls CommerceInventoryWarehouseRelService.deleteCommerceInventoryWarehouseRel. Side effects -- removes the row that scopes the warehouse to the bound account; the account itself is not affected."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-account binding between the warehouse identified by externalReferenceCode and the account resolved by accountId or accountExternalReferenceCode in the body. Not an upsert. Validation -- NoSuchEntryException -> 404 when the account cannot be resolved; a duplicate binding raises a duplicate exception -> 400."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-account binding between the warehouse identified by id and the account resolved by accountId or accountExternalReferenceCode in the body. Not an upsert. Validation -- NoSuchInventoryWarehouseException -> 404 when the warehouse id does not resolve; NoSuchEntryException -> 404 when the account cannot be resolved."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse-account-group binding identified by warehouseAccountGroupId. Calls CommerceInventoryWarehouseRelService.deleteCommerceInventoryWarehouseRel. Side effects -- removes the row that scopes the warehouse to the bound account group; the account group itself is not affected."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-account-group binding between the warehouse identified by externalReferenceCode and the account group resolved by accountGroupId or accountGroupExternalReferenceCode in the body. Not an upsert. Validation -- NoSuchEntryException -> 404 when the account group cannot be resolved; a duplicate binding raises a duplicate exception -> 400."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-account-group binding between the warehouse identified by id and the account group resolved by accountGroupId or accountGroupExternalReferenceCode in the body. Not an upsert. Validation -- NoSuchEntryException -> 404 when the account group cannot be resolved."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse-channel binding identified by warehouseChannelId. Calls CommerceChannelRelService.deleteCommerceChannelRel. Side effects -- removes the row that scopes the warehouse to the bound channel; the channel itself is not affected."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-channel binding between the warehouse identified by externalReferenceCode and the channel resolved by channelId or channelExternalReferenceCode in the body. Not an upsert. Validation -- NoSuchChannelException -> 404 when the channel cannot be resolved; DuplicateCommerceChannelRelException -> 409 when the binding already exists."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-channel binding between the warehouse identified by id and the channel resolved by channelId or channelExternalReferenceCode in the body. Validation -- NoSuchChannelException -> 404 when the channel cannot be resolved; DuplicateCommerceChannelRelException -> 409 when the binding already exists."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse item identified by id. Calls CommerceInventoryWarehouseItemService.deleteCommerceInventoryWarehouseItem. Validation -- NoSuchInventoryWarehouseItemException -> 404 when the id does not resolve."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse item identified by externalReferenceCode. Calls CommerceInventoryWarehouseItemService.deleteCommerceInventoryWarehouseItem after looking the row up within the request company. Validation -- NoSuchInventoryWarehouseItemException -> 404 when the externalReferenceCode does not resolve."
	)
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

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the warehouse item identified by id. Only the supplied fields among quantity, reservedQuantity, and unitOfMeasureKey are modified; others fall back to the persisted value. Validation -- CPInstanceUnitOfMeasureKeyException -> 400 when unitOfMeasureKey is rejected. Returns 200 OK with an empty body on success; the 202 response declared by the spec is reserved for future async use."
	)
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

	@GraphQLField(
		description = "Applies a JSON Merge Patch to the warehouse item identified by externalReferenceCode. Only the supplied fields among quantity, reservedQuantity, and unitOfMeasureKey are modified; others fall back to the persisted value. Validation -- NoSuchInventoryWarehouseItemException -> 404 when the externalReferenceCode does not resolve; CPInstanceUnitOfMeasureKeyException -> 400 when unitOfMeasureKey is rejected. The 202 response declared by the spec is reserved for future async use and is not currently emitted."
	)
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

	@GraphQLField(
		description = "Creates a warehouse item under the warehouse identified by externalReferenceCode. Validation -- NoSuchInventoryWarehouseException -> 404 when the externalReferenceCode does not resolve; DuplicateCommerceInventoryWarehouseItemException -> 400 when a warehouse item with the supplied externalReferenceCode already exists."
	)
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

	@GraphQLField(
		description = "Creates a warehouse item under the warehouse identified by id. Validation -- NoSuchInventoryWarehouseException -> 404 when the id does not resolve; DuplicateCommerceInventoryWarehouseItemException -> 400 when a warehouse item with the supplied externalReferenceCode already exists."
	)
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

	@GraphQLField(
		description = "Creates a warehouse item with the supplied externalReferenceCode. The body must resolve a warehouse through either warehouseId or warehouseExternalReferenceCode. Validation -- NoSuchInventoryWarehouseException -> 404 when the warehouse cannot be resolved; DuplicateCommerceInventoryWarehouseItemException -> 400 when a warehouse item with the same externalReferenceCode already exists. For upsert semantics use PUT against the same path instead."
	)
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

	@GraphQLField(
		description = "Upserts the warehouse item identified by externalReferenceCode. When a warehouse item with that externalReferenceCode already exists, quantity, reservedQuantity, and unitOfMeasureKey are updated. When it does not, a new warehouse item is created against the warehouse resolved by warehouseExternalReferenceCode in the body. Validation -- NoSuchInventoryWarehouseException -> 404 when the warehouse cannot be resolved. Side effects -- quantity defaults to one and reservedQuantity defaults to zero on create."
	)
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

	@GraphQLField(
		description = "Deletes the warehouse-order-type binding identified by warehouseOrderTypeId. Calls CommerceInventoryWarehouseRelService.deleteCommerceInventoryWarehouseRel. Side effects -- removes the row that scopes the warehouse to the bound order type; the order type itself is not affected."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-order-type binding between the warehouse identified by externalReferenceCode and the order type resolved by orderTypeId or orderTypeExternalReferenceCode in the body. Not an upsert. Validation -- 404 when the order type cannot be resolved."
	)
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

	@GraphQLField(
		description = "Creates a warehouse-order-type binding between the warehouse identified by id and the order type resolved by orderTypeId or orderTypeExternalReferenceCode in the body. Not an upsert. Validation -- 404 when the order type cannot be resolved."
	)
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
// LIFERAY-REST-BUILDER-HASH:1603336571