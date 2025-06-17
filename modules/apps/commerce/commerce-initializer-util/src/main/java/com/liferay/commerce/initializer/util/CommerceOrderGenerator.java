/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.commerce.util.comparator.CommerceShippingMethodPriorityComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(service = CommerceOrderGenerator.class)
public class CommerceOrderGenerator {

	public void generate(long groupId, int ordersCount) {
		Callable<Object> generateOrdersCallable = new GenerateOrdersCallable(
			groupId, ordersCount);

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig, generateOrdersCallable);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	private void _generateCommerceOrder(
			long groupId, AccountEntry accountEntry,
			List<CPCatalogEntry> cpCatalogEntries,
			long commerceShippingMethodId,
			CommerceShippingEngine commerceShippingEngine)
		throws PortalException {

		// Commerce account users

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					accountEntry.getAccountEntryId(), 0, 1);

		if (accountEntryUserRels.isEmpty()) {
			String message =
				"There are no users related to the account " +
					accountEntry.getAccountEntryId();

			if (_log.isInfoEnabled()) {
				_log.info(message);
			}

			throw new PortalException(message);
		}

		// Commerce addresses

		List<CommerceAddress> commerceAddresses =
			_commerceAddressLocalService.getCommerceAddressesByCompanyId(
				accountEntry.getCompanyId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId(), 0, 1, null);

		if (commerceAddresses.isEmpty()) {
			String message =
				"There are no addresses related to the account " +
					accountEntry.getAccountEntryId();

			if (_log.isInfoEnabled()) {
				_log.info(message);
			}

			throw new PortalException(message);
		}

		AccountEntryUserRel accountEntryUserRel = accountEntryUserRels.get(0);

		// Add commerce order

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				accountEntry.getCompanyId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				accountEntryUserRel.getAccountUserId(),
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				accountEntryUserRel.getAccountEntryId(),
				commerceCurrency.getCode(), 0);

		// Commerce order items

		CommerceContext commerceContext = _commerceContextFactory.create(
			accountEntryUserRel.getAccountEntryId(), commerceOrder.getGroupId(),
			null, commerceOrder.getCommerceOrderId(),
			commerceOrder.getCompanyId());

		CommerceContextThreadLocal.set(commerceContext);

		ServiceContext serviceContext = _getServiceContext(commerceOrder);

		_generateCommerceOrderItems(
			commerceOrder, cpCatalogEntries, commerceContext, serviceContext);

		// Recalculate Price

		commerceOrder = _commerceOrderLocalService.recalculatePrice(
			commerceOrder.getCommerceOrderId(), commerceContext);

		CommerceAddress commerceAddress = commerceAddresses.get(0);

		commerceOrder.setShippingAddressId(
			commerceAddress.getCommerceAddressId());

		// Commerce shipping options

		String commerceShippingOptionKey =
			commerceOrder.getShippingOptionName();

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getCommerceShippingOptions(
				commerceContext, commerceOrder, serviceContext.getLocale());

		if (!commerceShippingOptions.isEmpty()) {
			CommerceShippingOption commerceShippingOption =
				commerceShippingOptions.get(0);

			commerceShippingOptionKey = commerceShippingOption.getKey();
		}

		// Update commerce order

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			null, commerceOrder.getCommerceOrderId(),
			commerceAddress.getCommerceAddressId(), commerceShippingMethodId,
			commerceAddress.getCommerceAddressId(),
			commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(), null,
			commerceOrder.getPurchaseOrderNumber(),
			commerceOrder.getShippingAmount(), commerceShippingOptionKey,
			commerceOrder.getSubtotal(), commerceOrder.getTotal());

		// Checkout commerce order

		_commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS,
			serviceContext.getUserId(), true);

		// Update payment status

		_commerceOrderLocalService.updatePaymentStatus(
			commerceOrder.getUserId(), commerceOrder.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_COMPLETED);
	}

	private void _generateCommerceOrderItems(
			CommerceOrder commerceOrder, List<CPCatalogEntry> cpCatalogEntries,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		for (CPCatalogEntry cpCatalogEntry : cpCatalogEntries) {

			// Commerce product SKUs

			List<CPSku> cpSkus = cpCatalogEntry.getCPSkus();

			if (cpSkus.isEmpty()) {
				continue;
			}

			CPSku cpSku = cpSkus.get(0);

			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				cpSku.getCPInstanceId());

			// Commerce product inventory

			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
				_cpDefinitionInventoryEngineRegistry.
					getCPDefinitionInventoryEngine(cpDefinitionInventory);

			BigDecimal maxOrderQuantity = _getMaxOrderQuantity(
				commerceContext.getCPConfigurationListId(
					cpInstance.getGroupId()),
				cpInstance, cpDefinitionInventoryEngine);

			if (BigDecimalUtil.lt(maxOrderQuantity, BigDecimal.ZERO)) {
				continue;
			}

			// Add commerce order item

			try {
				BigDecimal minOrderQuantity =
					cpDefinitionInventoryEngine.getMinOrderQuantity(
						commerceContext.getCPConfigurationListId(
							cpInstance.getGroupId()),
						cpInstance);

				if (BigDecimalUtil.lt(maxOrderQuantity, minOrderQuantity)) {
					continue;
				}

				List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
					_cpInstanceUnitOfMeasureLocalService.
						getCPInstanceUnitOfMeasures(
							cpInstance.getCompanyId(), cpInstance.getSku());

				BigDecimal quantity = _randomQuantity(
					minOrderQuantity, maxOrderQuantity,
					ListUtil.isEmpty(cpInstanceUnitOfMeasures) ? null :
						cpInstanceUnitOfMeasures.get(0));

				_commerceOrderItemLocalService.addCommerceOrderItem(
					commerceOrder.getUserId(),
					commerceOrder.getCommerceOrderId(),
					cpInstance.getCPInstanceId(), null, quantity, 0,
					BigDecimal.ZERO, StringPool.BLANK, commerceContext,
					serviceContext);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private void _generateCommerceOrders(long groupId, int ordersCount)
		throws Exception {

		// Initialize permission checker

		Group group = _groupLocalService.getGroup(groupId);

		_setPermissionChecker(group);

		// Commerce accounts

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryLocalService.searchAccountEntries(
				group.getCompanyId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"parentAccountEntryId",
					AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT
				).put(
					"status", () -> WorkflowConstants.STATUS_APPROVED
				).put(
					"types",
					_commerceAccountHelper.getAccountEntryTypes(
						_commerceChannelLocalService.
							getCommerceChannelGroupIdBySiteGroupId(groupId))
				).build(),
				QueryUtil.ALL_POS, 0, null, false);

		List<AccountEntry> accountEntries =
			baseModelSearchResult.getBaseModels();

		if (accountEntries.isEmpty()) {
			_log.error("There are no accounts");

			return;
		}

		// Commerce shipping methods

		long commerceShippingMethodId = _getCommerceShippingMethodId(groupId);

		// Search context

		SearchContext searchContext = _getSearchContext(groupId);

		// Commerce products

		CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
			groupId, searchContext, new CPQuery(), 0, 1);

		if (cpDataSourceResult.getLength() <= 0) {
			_log.error("There are no products");

			return;
		}

		// Commerce orders

		int retryNumber = 0;

		for (int i = 0; i < ordersCount; i++) {
			int min = _randomInt(0, cpDataSourceResult.getLength() - 1);

			int max = min + _randomInt(1, 20);

			if (max >= cpDataSourceResult.getLength()) {
				max = cpDataSourceResult.getLength() - 1;
			}

			if (min == max) {
				min = 0;
				max = _randomInt(1, 20);
			}

			cpDataSourceResult = _cpDefinitionHelper.search(
				groupId, searchContext, new CPQuery(), min, max);

			try {
				_generateCommerceOrder(
					groupId,
					accountEntries.get(
						_randomInt(0, accountEntries.size() - 1)),
					cpDataSourceResult.getCPCatalogEntries(),
					commerceShippingMethodId,
					_getCommerceShippingEngine(commerceShippingMethodId));

				retryNumber = 0;
			}
			catch (PortalException portalException) {
				if (_log.isInfoEnabled()) {
					_log.info(portalException);
				}

				// Order not generated, retry

				if (retryNumber < 5) {
					i--;
					retryNumber++;
				}
				else {
					_log.error(portalException);
				}
			}
		}
	}

	private CommerceShippingEngine _getCommerceShippingEngine(
		long commerceShippingMethodId) {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceShippingMethodId);

		if (commerceShippingMethod == null) {
			return null;
		}

		return _commerceShippingEngineRegistry.getCommerceShippingEngine(
			commerceShippingMethod.getEngineKey());
	}

	private long _getCommerceShippingMethodId(long groupId) throws Exception {
		List<CommerceShippingMethod> commerceShippingMethods =
			_commerceShippingMethodLocalService.getCommerceShippingMethods(
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CommerceShippingMethodPriorityComparator.getInstance(false));

		if (commerceShippingMethods.isEmpty()) {
			return 0;
		}

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethods.get(0);

		return commerceShippingMethod.getCommerceShippingMethodId();
	}

	private BigDecimal _getMaxOrderQuantity(
			long cpConfigurationListId, CPInstance cpInstance,
			CPDefinitionInventoryEngine cpDefinitionInventoryEngine)
		throws PortalException {

		BigDecimal stockQuantity = _commerceInventoryEngine.getStockQuantity(
			cpInstance.getCompanyId(), cpInstance.getGroupId(),
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal maxOrderQuantity =
			cpDefinitionInventoryEngine.getMaxOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (BigDecimalUtil.lt(stockQuantity, maxOrderQuantity)) {
			return stockQuantity;
		}

		return maxOrderQuantity;
	}

	private SearchContext _getSearchContext(long groupId) throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).build());

		Group group = _groupLocalService.getGroup(groupId);

		searchContext.setCompanyId(group.getCompanyId());

		searchContext.setAttribute("commerceChannelGroupId", groupId);

		return searchContext;
	}

	private ServiceContext _getServiceContext(CommerceOrder commerceOrder)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		User user = _userLocalService.getUser(commerceOrder.getUserId());

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setLanguageId(user.getLanguageId());
		serviceContext.setScopeGroupId(user.getGroupId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private int _randomInt(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException(
				"Max value must be greater than or equal to the min value");
		}

		int value = _random.nextInt();

		int range = max + 1 - min;

		if (range == 0) {
			return value;
		}

		return Math.floorMod(value, range) + min;
	}

	private BigDecimal _randomQuantity(
		BigDecimal min, BigDecimal max,
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {

		if (BigDecimalUtil.lt(max, min)) {
			throw new IllegalArgumentException(
				"Max value must be greater than or equal to the min value");
		}

		int randomInt = _random.nextInt();

		if (cpInstanceUnitOfMeasure == null) {
			int range = max.intValue() + 1 - min.intValue();

			return BigDecimal.valueOf(
				Math.floorMod(randomInt, range) + min.intValue());
		}

		BigDecimal incrementalOrderQuantity =
			cpInstanceUnitOfMeasure.getIncrementalOrderQuantity();

		return max.min(
			incrementalOrderQuantity.multiply(BigDecimal.valueOf(randomInt)));
	}

	private void _setPermissionChecker(Group group) throws Exception {
		Company company = _companyLocalService.getCompanyById(
			group.getCompanyId());

		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(), RoleConstants.ADMINISTRATOR);

		List<User> roleUsers = _userLocalService.getRoleUsers(role.getRoleId());

		User user = roleUsers.get(0);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderGenerator.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	private final Random _random = new Random();

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class GenerateOrdersCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_generateCommerceOrders(_groupId, _ordersCount);

			return null;
		}

		private GenerateOrdersCallable(long groupId, int ordersCount) {
			_groupId = groupId;
			_ordersCount = ordersCount;
		}

		private final long _groupId;
		private final int _ordersCount;

	}

}