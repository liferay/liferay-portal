/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v5_0_1;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Riccardo Alberti
 */
public class CommercePermissionUpgradeProcess extends UpgradeProcess {

	public CommercePermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, String> resourceActionNames = _getResourceActionNames();

		try (Statement statement = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			ResultSet resultSet = statement.executeQuery(
				StringBundler.concat(
					"select resourcePermissionId from ResourcePermission ",
					"where name in ('90', '", _PORTLET_NAME_COMMERCE_DISCOUNT,
					"', '", _PORTLET_NAME_COMMERCE_PRICE_LIST, "')"))) {

			while (resultSet.next()) {
				ResourcePermission resourcePermission =
					_resourcePermissionLocalService.getResourcePermission(
						resultSet.getLong(1));

				if (Objects.equals(
						resourcePermission.getName(),
						_PORTLET_NAME_COMMERCE_DISCOUNT)) {

					_setResourcePermissions(
						_PORTLET_NAME_COMMERCE_DISCOUNT_PRICING,
						_PORTLET_NAME_COMMERCE_DISCOUNT, resourcePermission);

					_resourcePermissionLocalService.deleteResourcePermission(
						resourcePermission);
				}
				else if (Objects.equals(
							resourcePermission.getName(),
							_PORTLET_NAME_COMMERCE_PRICE_LIST)) {

					_setResourcePermissions(
						_PORTLET_NAME_COMMERCE_PRICE_LIST_PRICING,
						_PORTLET_NAME_COMMERCE_PRICE_LIST, resourcePermission);
					_setResourcePermissions(
						_PORTLET_NAME_COMMERCE_PROMOTION_PRICING,
						_PORTLET_NAME_COMMERCE_PRICE_LIST, resourcePermission);

					_resourcePermissionLocalService.deleteResourcePermission(
						resourcePermission);
				}
				else if (Objects.equals(resourcePermission.getName(), "90")) {
					_setResourcePermissions(
						resourceActionNames, resourcePermission);
				}
			}
		}

		_deleteResourceActions(_PORTLET_NAME_COMMERCE_DISCOUNT);
		_deleteResourceActions(_PORTLET_NAME_COMMERCE_PRICE_LIST);
		_deleteResourceActions();
	}

	private void _addResourcePermission(
			String actionId, long companyId, String name, long roleId)
		throws Exception {

		if (!_resourcePermissionLocalService.hasResourcePermission(
				companyId, name, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), roleId, actionId)) {

			_resourcePermissionLocalService.addResourcePermission(
				companyId, name, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), roleId, actionId);
		}
	}

	private void _deleteResourceActions() {
		for (String actionId :
				ArrayUtil.append(_ACTION_IDS, _REMOVED_ACTION_IDS)) {

			ResourceAction resourceAction =
				_resourceActionLocalService.fetchResourceAction("90", actionId);

			if (resourceAction == null) {
				continue;
			}

			_resourceActionLocalService.deleteResourceAction(resourceAction);
		}
	}

	private void _deleteResourceActions(String name) {
		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(name);

		for (ResourceAction resourceAction : resourceActions) {
			_resourceActionLocalService.deleteResourceAction(resourceAction);
		}
	}

	private List<String> _getResourceActionIds(
		long actionIds, String resourcePermissionName) {

		List<String> resourceActionIds = new ArrayList<>();

		for (ResourceAction resourceAction :
				_resourceActionLocalService.getResourceActions(
					resourcePermissionName)) {

			long bitwiseValue = resourceAction.getBitwiseValue();

			if ((actionIds & bitwiseValue) != bitwiseValue) {
				continue;
			}

			resourceActionIds.add(resourceAction.getActionId());
		}

		return resourceActionIds;
	}

	private Map<String, String> _getResourceActionNames() throws Exception {
		Map<String, String> resourceActionNames = new HashMap<>();

		StringBundler sb = new StringBundler();

		sb.append("select name, actionId from ResourceAction where name != ");
		sb.append("'90' and actionId in (");

		for (int i = 0; i < _ACTION_IDS.length; i++) {
			sb.append(StringPool.APOSTROPHE);
			sb.append(_ACTION_IDS[i]);
			sb.append(StringPool.APOSTROPHE);

			if (i == (_ACTION_IDS.length - 1)) {
				continue;
			}

			sb.append(", ");
		}

		sb.append(")");

		try (Statement statement = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			ResultSet resultSet = statement.executeQuery(sb.toString())) {

			while (resultSet.next()) {
				resourceActionNames.put(
					resultSet.getString("actionId"),
					resultSet.getString("name"));
			}
		}

		return resourceActionNames;
	}

	private void _setResourcePermissions(
			Map<String, String> resourceActionNames,
			ResourcePermission resourcePermission)
		throws Exception {

		for (String actionId : _ACTION_IDS) {
			if (!resourcePermission.hasActionId(actionId)) {
				continue;
			}

			String resourceActionName = resourceActionNames.get(actionId);

			if (resourceActionName == null) {
				continue;
			}

			resourcePermission.removeResourceAction(actionId);

			_resourcePermissionLocalService.setResourcePermissions(
				resourcePermission.getCompanyId(), resourceActionName,
				resourcePermission.getScope(), resourcePermission.getPrimKey(),
				resourcePermission.getRoleId(), new String[] {actionId});

			if (Objects.equals(
					actionId, "ADD_COMMERCE_PRODUCT_MEASUREMENT_UNIT")) {

				_addResourcePermission(
					ActionKeys.DELETE, resourcePermission.getCompanyId(),
					CPMeasurementUnit.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.PERMISSIONS, resourcePermission.getCompanyId(),
					CPMeasurementUnit.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.UPDATE, resourcePermission.getCompanyId(),
					CPMeasurementUnit.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.VIEW, resourcePermission.getCompanyId(),
					CPMeasurementUnit.class.getName(),
					resourcePermission.getRoleId());
			}
			else if (Objects.equals(actionId, "VIEW_INVENTORIES")) {
				_addResourcePermission(
					ActionKeys.DELETE, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.PERMISSIONS, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.UPDATE, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					resourcePermission.getRoleId());
				_addResourcePermission(
					ActionKeys.VIEW, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					resourcePermission.getRoleId());
			}
		}
	}

	private void _setResourcePermissions(
			String newName, String oldName,
			ResourcePermission resourcePermission)
		throws Exception {

		String primKey = resourcePermission.getPrimKey();

		if (primKey.equals(oldName)) {
			primKey = newName;
		}

		List<String> resourceActionIds = _getResourceActionIds(
			resourcePermission.getActionIds(), oldName);

		_resourceActionLocalService.checkResourceActions(
			newName, resourceActionIds);

		_resourcePermissionLocalService.setResourcePermissions(
			resourcePermission.getCompanyId(), newName,
			resourcePermission.getScope(), primKey,
			resourcePermission.getRoleId(),
			resourceActionIds.toArray(new String[0]));
	}

	private static final String[] _ACTION_IDS = {
		"ADD_ACCOUNT", "ADD_ACCOUNT_GROUP",
		"ADD_COMMERCE_AVAILABILITY_ESTIMATE", "ADD_COMMERCE_BOM_DEFINITION",
		"ADD_COMMERCE_BOM_FOLDER", "ADD_COMMERCE_BRAND", "ADD_COMMERCE_CATALOG",
		"ADD_COMMERCE_CHANNEL", "ADD_COMMERCE_DATA_INTEGRATION_PROCESS",
		"ADD_COMMERCE_DISCOUNT", "ADD_COMMERCE_MODEL",
		"ADD_COMMERCE_PRICE_LIST", "ADD_COMMERCE_PRICING_CLASS",
		"ADD_COMMERCE_PRODUCT_MEASUREMENT_UNIT", "ADD_COMMERCE_PRODUCT_OPTION",
		"ADD_COMMERCE_PRODUCT_OPTION_CATEGORY",
		"ADD_COMMERCE_PRODUCT_SPECIFICATION_OPTION", "ADD_COMMERCE_SHIPMENT",
		"ADD_WAREHOUSE", "MANAGE_ALL_ACCOUNTS", "MANAGE_AVAILABLE_ACCOUNTS",
		"MANAGE_COMMERCE_CURRENCIES", "MANAGE_COMMERCE_HEALTH_STATUS",
		"MANAGE_COMMERCE_ORDER_PRICES",
		"MANAGE_COMMERCE_PRODUCT_TAX_CATEGORIES",
		"MANAGE_COMMERCE_SUBSCRIPTIONS", "VIEW_COMMERCE_ACCOUNT_GROUPS",
		"VIEW_COMMERCE_AVAILABILITY_ESTIMATES", "VIEW_COMMERCE_CATALOGS",
		"VIEW_COMMERCE_CHANNELS", "VIEW_COMMERCE_DISCOUNTS",
		"VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS", "VIEW_COMMERCE_SHIPMENTS",
		"VIEW_INVENTORIES"
	};

	private static final String _PORTLET_NAME_COMMERCE_DISCOUNT =
		"com_liferay_commerce_discount_web_internal_portlet_" +
			"CommerceDiscountPortlet";

	private static final String _PORTLET_NAME_COMMERCE_DISCOUNT_PRICING =
		"com_liferay_commerce_pricing_web_internal_portlet_" +
			"CommerceDiscountPortlet";

	private static final String _PORTLET_NAME_COMMERCE_PRICE_LIST =
		"com_liferay_commerce_price_list_web_internal_portlet_" +
			"CommercePriceListPortlet";

	private static final String _PORTLET_NAME_COMMERCE_PRICE_LIST_PRICING =
		"com_liferay_commerce_pricing_web_internal_portlet_" +
			"CommercePriceListPortlet";

	private static final String _PORTLET_NAME_COMMERCE_PROMOTION_PRICING =
		"com_liferay_commerce_pricing_web_internal_portlet_" +
			"CommercePromotionPortlet";

	private static final String[] _REMOVED_ACTION_IDS = {
		"MANAGE_COMMERCE_AVAILABILITY_ESTIMATES",
		"MANAGE_COMMERCE_PRODUCT_MEASUREMENT_UNITS", "MANAGE_INVENTORY"
	};

	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}