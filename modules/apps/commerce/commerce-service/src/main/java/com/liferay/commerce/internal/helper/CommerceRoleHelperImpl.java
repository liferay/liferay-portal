/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.helper;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.constants.CommerceCurrencyActionKeys;
import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.commerce.notification.constants.CommerceNotificationActionKeys;
import com.liferay.commerce.payment.constants.CommercePaymentEntryActionKeys;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.price.list.constants.CommercePriceListActionKeys;
import com.liferay.commerce.pricing.constants.CommercePricingClassActionKeys;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceRoleHelper.class)
public class CommerceRoleHelperImpl implements CommerceRoleHelper {

	@Override
	public void checkCommerceAccountRoles(ServiceContext serviceContext)
		throws PortalException {

		_checkAccountRole(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			serviceContext);
		_checkRole(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			RoleConstants.TYPE_ORGANIZATION, serviceContext);
		_checkAccountRole(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER,
			serviceContext);
		_checkAccountRole(
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER, serviceContext);
		_checkAccountRole(
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			serviceContext);
		_checkAccountRole(
			AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER, serviceContext);

		if (FeatureFlagManagerUtil.isEnabled("LPD-10562")) {
			_checkRole(
				AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
				RoleConstants.TYPE_REGULAR, serviceContext);
			_checkRole(
				RoleConstants.USER, RoleConstants.TYPE_REGULAR, serviceContext);
		}

		_checkRole(
			AccountRoleConstants.ROLE_NAME_SUPPLIER, RoleConstants.TYPE_REGULAR,
			serviceContext);
	}

	@Override
	public void checkCommerceUserRoles(ServiceContext serviceContext)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled("LPD-10562")) {
			_checkRole(
				RoleConstants.USER, RoleConstants.TYPE_REGULAR, serviceContext);
		}
	}

	@Override
	public boolean hasCommerceUserPermissions(long companyId)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(companyId, RoleConstants.USER);

		for (String objectDefinitionName :
				_RETURNS_MANAGER_OBJECT_DEFINITION_NAMES) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					role.getCompanyId(), objectDefinitionName);

			if (objectDefinition == null) {
				continue;
			}

			if (!_resourcePermissionLocalService.hasResourcePermission(
					companyId, objectDefinition.getResourceName(),
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY)) {

				return false;
			}
		}

		for (String externalReferenceCode :
				_RETURNS_MANAGER_LIST_TYPE_DEFINITION_EXTERNAL_REFERENCE_CODES) {

			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, role.getCompanyId());

			if (listTypeDefinition == null) {
				continue;
			}

			if (!_resourcePermissionLocalService.hasResourcePermission(
					companyId, listTypeDefinition.getModelClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(
						listTypeDefinition.getListTypeDefinitionId()),
					role.getRoleId(), ActionKeys.VIEW)) {

				return false;
			}
		}

		return true;
	}

	private void _checkAccountRole(String name, ServiceContext serviceContext)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			serviceContext.getCompanyId(), name);

		if (role == null) {
			AccountRole accountRole = _accountRoleLocalService.addAccountRole(
				null, serviceContext.getUserId(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, name,
				Collections.singletonMap(serviceContext.getLocale(), name),
				Collections.emptyMap());

			role = accountRole.getRole();

			_setRolePermissions(role, serviceContext);
		}
		else if (AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR.
					equals(name) ||
				 GetterUtil.getBoolean(
					 serviceContext.getAttribute("forceReloadPermissions"))) {

			_setRolePermissions(role, serviceContext);
		}
	}

	private void _checkRole(
			String name, int type, ServiceContext serviceContext)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			serviceContext.getCompanyId(), name);

		if (role == null) {
			role = _roleLocalService.addRole(
				null, serviceContext.getUserId(), null, 0, name,
				Collections.singletonMap(serviceContext.getLocale(), name),
				Collections.emptyMap(), type, null, serviceContext);

			_setRolePermissions(role, serviceContext);
		}
		else if (GetterUtil.getBoolean(
					serviceContext.getAttribute("forceReloadPermissions"))) {

			_setRolePermissions(role, serviceContext);
		}
	}

	private void _setRolePermissions(
			long companyId, String primaryKey,
			Map<String, String[]> resourceActionIds, Role role, int scope)
		throws PortalException {

		for (Map.Entry<String, String[]> entry : resourceActionIds.entrySet()) {
			try {
				DBPartitionUtil.forEachCompanyId(
					company -> _resourceActionLocalService.checkResourceActions(
						entry.getKey(), Arrays.asList(entry.getValue())));
			}
			catch (Exception exception) {
				throw new PortalException(exception);
			}

			for (String actionId : entry.getValue()) {
				_resourcePermissionLocalService.addResourcePermission(
					companyId, entry.getKey(), scope, primaryKey,
					role.getRoleId(), actionId);
			}
		}
	}

	private void _setRolePermissions(Role role, ServiceContext serviceContext)
		throws PortalException {

		Map<String, String[]> companyResourceActionIds = new HashMap<>();
		Map<String, String[]> groupResourceActionIds = new HashMap<>();

		String name = role.getName();

		if (name.equals(
				AccountRoleConstants.
					REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR)) {

			companyResourceActionIds.put(
				"com.liferay.commerce.model.CommerceOrderType",
				new String[] {ActionKeys.VIEW});

			groupResourceActionIds.put(
				AccountEntry.class.getName(),
				new String[] {
					ActionKeys.UPDATE, ActionKeys.MANAGE_USERS, ActionKeys.VIEW,
					AccountActionKeys.ASSIGN_USERS,
					AccountActionKeys.MANAGE_ADDRESSES,
					AccountActionKeys.VIEW_ADDRESSES,
					AccountActionKeys.VIEW_ACCOUNT_ROLES,
					AccountActionKeys.VIEW_ORGANIZATIONS,
					AccountActionKeys.VIEW_USERS,
					CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS,
					CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS
				});
			groupResourceActionIds.put(
				AccountRole.class.getName(), new String[] {ActionKeys.VIEW});
			groupResourceActionIds.put(
				"com.liferay.commerce.order",
				new String[] {
					CommerceOrderActionKeys.ADD_COMMERCE_ORDER,
					CommerceOrderActionKeys.APPROVE_OPEN_COMMERCE_ORDERS,
					CommerceOrderActionKeys.CHECKOUT_OPEN_COMMERCE_ORDERS,
					CommerceOrderActionKeys.DELETE_COMMERCE_ORDERS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_DELIVERY_TERMS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
					CommerceOrderActionKeys.VIEW_BILLING_ADDRESS,
					CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS,
					CommerceOrderActionKeys.VIEW_OPEN_COMMERCE_ORDERS
				});
		}
		else if (name.equals(
					AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER)) {

			groupResourceActionIds.put(
				AccountEntry.class.getName(),
				new String[] {
					ActionKeys.UPDATE, ActionKeys.MANAGE_USERS, ActionKeys.VIEW,
					AccountActionKeys.MANAGE_ADDRESSES,
					AccountActionKeys.VIEW_ADDRESSES,
					AccountActionKeys.VIEW_ACCOUNT_ROLES,
					AccountActionKeys.VIEW_ORGANIZATIONS,
					AccountActionKeys.VIEW_USERS,
					CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS,
					CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS
				});
			groupResourceActionIds.put(
				AccountRole.class.getName(), new String[] {ActionKeys.VIEW});
		}
		else if (name.equals(
					AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER)) {

			groupResourceActionIds.put(
				AccountEntry.class.getName(), new String[] {ActionKeys.VIEW});
		}
		else if (name.equals(AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER)) {
			companyResourceActionIds.put(
				"com.liferay.commerce.model.CommerceOrderType",
				new String[] {ActionKeys.VIEW});

			groupResourceActionIds.put(
				AccountEntry.class.getName(),
				new String[] {
					AccountActionKeys.MANAGE_ADDRESSES,
					AccountActionKeys.VIEW_ADDRESSES
				});
			groupResourceActionIds.put(
				"com.liferay.commerce.order",
				new String[] {
					CommerceOrderActionKeys.ADD_COMMERCE_ORDER,
					CommerceOrderActionKeys.CHECKOUT_OPEN_COMMERCE_ORDERS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_DELIVERY_TERMS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_MULTISHIPPING,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
					CommerceOrderActionKeys.VIEW_BILLING_ADDRESS,
					CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS,
					CommerceOrderActionKeys.VIEW_OPEN_COMMERCE_ORDERS
				});
		}
		else if (name.equals(
					AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER)) {

			companyResourceActionIds.put(
				"com.liferay.commerce.model.CommerceOrderType",
				new String[] {ActionKeys.VIEW});

			groupResourceActionIds.put(
				"com.liferay.commerce.order",
				new String[] {
					CommerceOrderActionKeys.ADD_COMMERCE_ORDER,
					CommerceOrderActionKeys.APPROVE_OPEN_COMMERCE_ORDERS,
					CommerceOrderActionKeys.CHECKOUT_OPEN_COMMERCE_ORDERS,
					CommerceOrderActionKeys.DELETE_COMMERCE_ORDERS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_DELIVERY_TERMS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_MULTISHIPPING,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
					CommerceOrderActionKeys.VIEW_BILLING_ADDRESS,
					CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS,
					CommerceOrderActionKeys.VIEW_OPEN_COMMERCE_ORDERS
				});
		}
		else if (name.equals(AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER)) {
			groupResourceActionIds.put(
				AccountEntry.class.getName(),
				new String[] {AccountActionKeys.VIEW_ACCOUNT_GROUPS});
		}
		else if (name.equals(AccountRoleConstants.ROLE_NAME_SUPPLIER)) {
			for (String portletId : _SUPPLIER_CONTROL_PANEL_PORTLET_IDS) {
				companyResourceActionIds.put(
					portletId,
					new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL});
			}

			companyResourceActionIds.put(
				PortletKeys.PORTAL,
				new String[] {ActionKeys.VIEW_CONTROL_PANEL});
			companyResourceActionIds.put(
				"com.liferay.commerce.catalog",
				new String[] {CPActionKeys.ADD_COMMERCE_CATALOG});
			companyResourceActionIds.put(
				"com.liferay.commerce.channel",
				new String[] {
					CommerceNotificationActionKeys.
						VIEW_COMMERCE_NOTIFICATION_QUEUE_ENTRIES,
					CommerceNotificationActionKeys.
						ADD_COMMERCE_NOTIFICATION_TEMPLATE
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.model.CommerceOrderType",
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				"com.liferay.commerce.order",
				new String[] {
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_DELIVERY_TERMS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.price.list",
				new String[] {
					CommercePriceListActionKeys.ADD_COMMERCE_PRICE_LIST
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.pricing",
				new String[] {
					CommercePricingClassActionKeys.ADD_COMMERCE_PRICING_CLASS
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.pricing.model.CommercePricingClass",
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				"com.liferay.commerce.product",
				new String[] {
					CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION,
					CPActionKeys.ADD_COMMERCE_PRODUCT_SPECIFICATION_OPTION,
					CPActionKeys.MANAGE_COMMERCE_PRODUCT_ATTACHMENTS,
					CPActionKeys.MANAGE_COMMERCE_PRODUCT_IMAGES,
					CPActionKeys.MANAGE_COMMERCE_PRODUCT_MEASUREMENT_UNITS,
					CPActionKeys.VIEW_COMMERCE_PRODUCT_ATTACHMENTS,
					CPActionKeys.VIEW_COMMERCE_PRODUCT_IMAGES
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.product.model.CPOption",
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				"com.liferay.commerce.product.model.CPOptionCategory",
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				"com.liferay.commerce.product.model.CPSpecificationOption",
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				"com.liferay.commerce.shipment",
				new String[] {CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS});
			companyResourceActionIds.put(
				"com.liferay.commerce.tax",
				new String[] {
					CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES
				});
			companyResourceActionIds.put(
				"com.liferay.document.library",
				new String[] {ActionKeys.ADD_DOCUMENT});
			companyResourceActionIds.put(
				"com.liferay.expando.kernel.model.ExpandoColumn",
				new String[] {ActionKeys.VIEW});
		}
		else if (name.equals(AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)) {
			for (String portletId :
					_RETURNS_MANAGER_CONTROL_PANEL_PORTLET_IDS) {

				companyResourceActionIds.put(
					portletId,
					new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL});
			}

			companyResourceActionIds.put(
				CommerceChannel.class.getName(),
				new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});
			companyResourceActionIds.put(
				CommercePaymentEntry.class.getName(),
				new String[] {ActionKeys.VIEW});
			companyResourceActionIds.put(
				PortletKeys.PORTAL,
				new String[] {ActionKeys.VIEW_CONTROL_PANEL});
			companyResourceActionIds.put(
				"com.liferay.commerce.currency",
				new String[] {
					CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES
				});
			companyResourceActionIds.put(
				"com.liferay.commerce.order",
				new String[] {CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS});
			companyResourceActionIds.put(
				"com.liferay.commerce.payment",
				new String[] {CommercePaymentEntryActionKeys.ADD_REFUND});
			companyResourceActionIds.put(
				"com.liferay.commerce.return",
				new String[] {CommerceActionKeys.MANAGE_RETURNS});

			for (String objectDefinitionName :
					_RETURNS_MANAGER_OBJECT_DEFINITION_NAMES) {

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						role.getCompanyId(), objectDefinitionName);

				if (objectDefinition == null) {
					continue;
				}

				companyResourceActionIds.put(
					objectDefinition.getResourceName(),
					new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY});
				companyResourceActionIds.put(
					objectDefinition.getClassName(),
					new String[] {
						ActionKeys.DELETE, ActionKeys.PERMISSIONS,
						ActionKeys.UPDATE, ActionKeys.VIEW
					});
				companyResourceActionIds.put(
					objectDefinition.getPortletId(),
					new String[] {ActionKeys.VIEW});
			}
		}
		else if (name.equals(RoleConstants.USER)) {
			for (String objectDefinitionName :
					_RETURNS_MANAGER_OBJECT_DEFINITION_NAMES) {

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						role.getCompanyId(), objectDefinitionName);

				if (objectDefinition == null) {
					continue;
				}

				companyResourceActionIds.put(
					objectDefinition.getResourceName(),
					new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY});
			}

			for (String externalReferenceCode :
					_RETURNS_MANAGER_LIST_TYPE_DEFINITION_EXTERNAL_REFERENCE_CODES) {

				ListTypeDefinition listTypeDefinition =
					_listTypeDefinitionLocalService.
						fetchListTypeDefinitionByExternalReferenceCode(
							externalReferenceCode, role.getCompanyId());

				if (listTypeDefinition == null) {
					continue;
				}

				_resourcePermissionLocalService.setResourcePermissions(
					serviceContext.getCompanyId(),
					listTypeDefinition.getModelClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(
						listTypeDefinition.getListTypeDefinitionId()),
					role.getRoleId(), new String[] {ActionKeys.VIEW});
			}
		}

		_setRolePermissions(
			serviceContext.getCompanyId(),
			String.valueOf(serviceContext.getCompanyId()),
			companyResourceActionIds, role, ResourceConstants.SCOPE_COMPANY);
		_setRolePermissions(
			serviceContext.getCompanyId(),
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			groupResourceActionIds, role,
			ResourceConstants.SCOPE_GROUP_TEMPLATE);
	}

	private static final String[] _RETURNS_MANAGER_CONTROL_PANEL_PORTLET_IDS = {
		CommercePortletKeys.COMMERCE_PAYMENT,
		CommercePortletKeys.COMMERCE_RETURN
	};

	private static final String[]
		_RETURNS_MANAGER_LIST_TYPE_DEFINITION_EXTERNAL_REFERENCE_CODES = {
			"L_COMMERCE_RETURN_ITEM_STATUSES", "L_COMMERCE_RETURN_REASONS",
			"L_COMMERCE_RETURN_RESOLUTION_METHODS", "L_COMMERCE_RETURN_STATUSES"
		};

	private static final String[] _RETURNS_MANAGER_OBJECT_DEFINITION_NAMES = {
		"CommerceReturn", "CommerceReturnItem"
	};

	private static final String[] _SUPPLIER_CONTROL_PANEL_PORTLET_IDS = {
		CommercePortletKeys.COMMERCE_ORDER,
		CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		CommercePricingPortletKeys.COMMERCE_PROMOTION,
		CPPortletKeys.COMMERCE_CATALOGS, CPPortletKeys.COMMERCE_CHANNELS,
		CPPortletKeys.CP_DEFINITIONS
	};

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}