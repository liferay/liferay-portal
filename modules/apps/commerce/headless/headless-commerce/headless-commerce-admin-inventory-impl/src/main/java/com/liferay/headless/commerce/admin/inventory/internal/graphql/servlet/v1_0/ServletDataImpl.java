/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.inventory.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.inventory.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.AccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.AccountResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.ChannelResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.OrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.ReplenishmentItemResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseAccountResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseChannelResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseItemResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0.WarehouseResourceImpl;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.AccountGroupResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.OrderTypeResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.ReplenishmentItemResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseAccountGroupResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseAccountResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseChannelResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseItemResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseOrderTypeResource;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseResource;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setReplenishmentItemResourceComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects);
		Mutation.setWarehouseResourceComponentServiceObjects(
			_warehouseResourceComponentServiceObjects);
		Mutation.setWarehouseAccountResourceComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects);
		Mutation.setWarehouseAccountGroupResourceComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects);
		Mutation.setWarehouseChannelResourceComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects);
		Mutation.setWarehouseItemResourceComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects);
		Mutation.setWarehouseOrderTypeResourceComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects);

		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setAccountGroupResourceComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects);
		Query.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Query.setOrderTypeResourceComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects);
		Query.setReplenishmentItemResourceComponentServiceObjects(
			_replenishmentItemResourceComponentServiceObjects);
		Query.setWarehouseResourceComponentServiceObjects(
			_warehouseResourceComponentServiceObjects);
		Query.setWarehouseAccountResourceComponentServiceObjects(
			_warehouseAccountResourceComponentServiceObjects);
		Query.setWarehouseAccountGroupResourceComponentServiceObjects(
			_warehouseAccountGroupResourceComponentServiceObjects);
		Query.setWarehouseChannelResourceComponentServiceObjects(
			_warehouseChannelResourceComponentServiceObjects);
		Query.setWarehouseItemResourceComponentServiceObjects(
			_warehouseItemResourceComponentServiceObjects);
		Query.setWarehouseOrderTypeResourceComponentServiceObjects(
			_warehouseOrderTypeResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Inventory";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-inventory-graphql/v1_0";
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
						"mutation#deleteReplenishmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"deleteReplenishmentItemByExternalReferenceCode"));
					put(
						"mutation#patchReplenishmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"patchReplenishmentItemByExternalReferenceCode"));
					put(
						"mutation#updateReplenishmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"putReplenishmentItemByExternalReferenceCode"));
					put(
						"mutation#deleteReplenishmentItem",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"deleteReplenishmentItem"));
					put(
						"mutation#deleteReplenishmentItemBatch",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"deleteReplenishmentItemBatch"));
					put(
						"mutation#patchReplenishmentItem",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"patchReplenishmentItem"));
					put(
						"mutation#createReplenishmentItemsPageExportBatch",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"postReplenishmentItemsPageExportBatch"));
					put(
						"mutation#createReplenishmentItem",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"postReplenishmentItem"));
					put(
						"mutation#createReplenishmentItemBatch",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"postReplenishmentItemBatch"));
					put(
						"mutation#createWarehousesPageExportBatch",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"postWarehousesPageExportBatch"));
					put(
						"mutation#createWarehouse",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "postWarehouse"));
					put(
						"mutation#createWarehouseBatch",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "postWarehouseBatch"));
					put(
						"mutation#deleteWarehouseByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"deleteWarehouseByExternalReferenceCode"));
					put(
						"mutation#patchWarehouseByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"patchWarehouseByExternalReferenceCode"));
					put(
						"mutation#updateWarehouseByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"putWarehouseByExternalReferenceCode"));
					put(
						"mutation#deleteWarehouseId",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "deleteWarehouseId"));
					put(
						"mutation#patchWarehouseId",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "patchWarehouseId"));
					put(
						"mutation#deleteWarehouseAccount",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"deleteWarehouseAccount"));
					put(
						"mutation#deleteWarehouseAccountBatch",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"deleteWarehouseAccountBatch"));
					put(
						"mutation#createWarehouseByExternalReferenceCodeWarehouseAccount",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"postWarehouseByExternalReferenceCodeWarehouseAccount"));
					put(
						"mutation#createWarehouseIdWarehouseAccount",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"postWarehouseIdWarehouseAccount"));
					put(
						"mutation#createWarehouseIdWarehouseAccountBatch",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"postWarehouseIdWarehouseAccountBatch"));
					put(
						"mutation#deleteWarehouseAccountGroup",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"deleteWarehouseAccountGroup"));
					put(
						"mutation#deleteWarehouseAccountGroupBatch",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"deleteWarehouseAccountGroupBatch"));
					put(
						"mutation#createWarehouseByExternalReferenceCodeWarehouseAccountGroup",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"postWarehouseByExternalReferenceCodeWarehouseAccountGroup"));
					put(
						"mutation#createWarehouseIdWarehouseAccountGroup",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"postWarehouseIdWarehouseAccountGroup"));
					put(
						"mutation#createWarehouseIdWarehouseAccountGroupBatch",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"postWarehouseIdWarehouseAccountGroupBatch"));
					put(
						"mutation#deleteWarehouseChannel",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"deleteWarehouseChannel"));
					put(
						"mutation#deleteWarehouseChannelBatch",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"deleteWarehouseChannelBatch"));
					put(
						"mutation#createWarehouseByExternalReferenceCodeWarehouseChannel",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"postWarehouseByExternalReferenceCodeWarehouseChannel"));
					put(
						"mutation#createWarehouseIdWarehouseChannel",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"postWarehouseIdWarehouseChannel"));
					put(
						"mutation#createWarehouseIdWarehouseChannelBatch",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"postWarehouseIdWarehouseChannelBatch"));
					put(
						"mutation#deleteWarehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"deleteWarehouseItemByExternalReferenceCode"));
					put(
						"mutation#patchWarehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"patchWarehouseItemByExternalReferenceCode"));
					put(
						"mutation#createWarehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"postWarehouseItemByExternalReferenceCode"));
					put(
						"mutation#updateWarehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"putWarehouseItemByExternalReferenceCode"));
					put(
						"mutation#deleteWarehouseItem",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"deleteWarehouseItem"));
					put(
						"mutation#deleteWarehouseItemBatch",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"deleteWarehouseItemBatch"));
					put(
						"mutation#patchWarehouseItem",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"patchWarehouseItem"));
					put(
						"mutation#createWarehouseByExternalReferenceCodeWarehouseItem",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"postWarehouseByExternalReferenceCodeWarehouseItem"));
					put(
						"mutation#createWarehouseIdWarehouseItem",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"postWarehouseIdWarehouseItem"));
					put(
						"mutation#createWarehouseIdWarehouseItemBatch",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"postWarehouseIdWarehouseItemBatch"));
					put(
						"mutation#deleteWarehouseOrderType",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"deleteWarehouseOrderType"));
					put(
						"mutation#deleteWarehouseOrderTypeBatch",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"deleteWarehouseOrderTypeBatch"));
					put(
						"mutation#createWarehouseByExternalReferenceCodeWarehouseOrderType",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"postWarehouseByExternalReferenceCodeWarehouseOrderType"));
					put(
						"mutation#createWarehouseIdWarehouseOrderType",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"postWarehouseIdWarehouseOrderType"));
					put(
						"mutation#createWarehouseIdWarehouseOrderTypeBatch",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"postWarehouseIdWarehouseOrderTypeBatch"));

					put(
						"query#warehouseAccountAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getWarehouseAccountAccount"));
					put(
						"query#warehouseAccountGroupAccountGroup",
						new ObjectValuePair<>(
							AccountGroupResourceImpl.class,
							"getWarehouseAccountGroupAccountGroup"));
					put(
						"query#warehouseChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getWarehouseChannelChannel"));
					put(
						"query#warehouseOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getWarehouseOrderTypeOrderType"));
					put(
						"query#replenishmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"getReplenishmentItemByExternalReferenceCode"));
					put(
						"query#replenishmentItem",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"getReplenishmentItem"));
					put(
						"query#replenishmentItems",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"getReplenishmentItemsPage"));
					put(
						"query#warehouseIdReplenishmentItems",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"getWarehouseIdReplenishmentItemsPage"));
					put(
						"query#warehouses",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "getWarehousesPage"));
					put(
						"query#warehouseByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"getWarehouseByExternalReferenceCode"));
					put(
						"query#warehouseId",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class, "getWarehouseId"));
					put(
						"query#warehouseByExternalReferenceCodeWarehouseAccounts",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseAccountsPage"));
					put(
						"query#warehouseIdWarehouseAccounts",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"getWarehouseIdWarehouseAccountsPage"));
					put(
						"query#warehouseByExternalReferenceCodeWarehouseAccountGroups",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage"));
					put(
						"query#warehouseIdWarehouseAccountGroups",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"getWarehouseIdWarehouseAccountGroupsPage"));
					put(
						"query#warehouseByExternalReferenceCodeWarehouseChannels",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseChannelsPage"));
					put(
						"query#warehouseIdWarehouseChannels",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"getWarehouseIdWarehouseChannelsPage"));
					put(
						"query#warehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseItemByExternalReferenceCode"));
					put(
						"query#warehouseItemsUpdated",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseItemsUpdatedPage"));
					put(
						"query#warehouseItem",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseItem"));
					put(
						"query#warehouseByExternalReferenceCodeWarehouseItems",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseItemsPage"));
					put(
						"query#warehouseIdWarehouseItems",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseIdWarehouseItemsPage"));
					put(
						"query#warehouseByExternalReferenceCodeWarehouseOrderTypes",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseOrderTypesPage"));
					put(
						"query#warehouseIdWarehouseOrderTypes",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"getWarehouseIdWarehouseOrderTypesPage"));

					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseResourceImpl.class,
							"getWarehouseByExternalReferenceCode"));
					put(
						"query#ReplenishmentItem.warehouseItemByExternalReferenceCode",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseItemByExternalReferenceCode"));
					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCodeWarehouseAccountGroups",
						new ObjectValuePair<>(
							WarehouseAccountGroupResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage"));
					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCodeWarehouseOrderTypes",
						new ObjectValuePair<>(
							WarehouseOrderTypeResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseOrderTypesPage"));
					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCodeWarehouseAccounts",
						new ObjectValuePair<>(
							WarehouseAccountResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseAccountsPage"));
					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCodeWarehouseItems",
						new ObjectValuePair<>(
							WarehouseItemResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseItemsPage"));
					put(
						"query#ReplenishmentItem.warehouseByExternalReferenceCodeWarehouseChannels",
						new ObjectValuePair<>(
							WarehouseChannelResourceImpl.class,
							"getWarehouseByExternalReferenceCodeWarehouseChannelsPage"));
					put(
						"query#Warehouse.replenishmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ReplenishmentItemResourceImpl.class,
							"getReplenishmentItemByExternalReferenceCode"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ReplenishmentItemResource>
		_replenishmentItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseResource>
		_warehouseResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseAccountResource>
		_warehouseAccountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseAccountGroupResource>
		_warehouseAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseChannelResource>
		_warehouseChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseItemResource>
		_warehouseItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WarehouseOrderTypeResource>
		_warehouseOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountGroupResource>
		_accountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;

}