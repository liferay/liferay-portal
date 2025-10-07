/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderConfiguration;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.exception.GuestCartItemMaxAllowedException;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.exception.ProductBundleException;
import com.liferay.commerce.helper.CommerceShippingHelper;
import com.liferay.commerce.internal.util.CommercePriceConverterUtil;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemUnitOfMeasureKeyException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditTypeRegistry;
import com.liferay.commerce.inventory.type.constants.CommerceInventoryAuditTypeConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemTable;
import com.liferay.commerce.model.CommerceOrderTable;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceImpl;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPDefinitionOptionRelException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.base.CommerceOrderItemLocalServiceBaseImpl;
import com.liferay.commerce.tax.CommerceTaxCalculation;
import com.liferay.commerce.util.CommerceOrderItemThreadLocal;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Ethan Bustad
 * @author Igor Beslic
 */
@Component(
	configurationPid = "com.liferay.commerce.configuration.CommerceOrderConfiguration",
	property = "model.class.name=com.liferay.commerce.model.CommerceOrderItem",
	service = AopService.class
)
public class CommerceOrderItemLocalServiceImpl
	extends CommerceOrderItemLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem addCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isBlank(json)) {
			json = _getCPInstanceOptionValueRelsJSON(cpInstanceId);
		}

		CommerceOrderLocalService commerceOrderLocalService =
			_commerceOrderLocalServiceSnapshot.get();

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		json = _getSanitizedJSON(cpInstance, json, serviceContext.getLocale());

		User user = _userLocalService.getUser(userId);

		_updateWorkflow(user.getUserId(), commerceOrder);

		CommerceProductPrice commerceProductPrice = _getCommerceProductPrice(
			commerceOrder, cpInstance.getCPDefinitionId(),
			cpInstance.getCPInstanceId(), json, quantity, unitOfMeasureKey,
			commerceContext);

		BigDecimal unitOfMeasureIncrementalOrderQuantity =
			commerceProductPrice.getUnitOfMeasureIncrementalOrderQuantity();

		CommerceOrderItem commerceOrderItem = _createCommerceOrderItem(
			commerceOrder.getGroupId(), user, commerceOrder,
			commerceProductPrice, cpInstance, 0, json, quantity,
			shippedQuantity, unitOfMeasureIncrementalOrderQuantity,
			unitOfMeasureKey, false, serviceContext);

		commerceOrderItem.setReplacedCPInstanceId(replacedCPInstanceId);

		commerceOrderItem = commerceOrderItemPersistence.update(
			commerceOrderItem);

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
				cpInstance.getCPDefinitionId(), json);

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			if (Validator.isNull(commerceOptionValue.getPriceType()) ||
				(_isStaticPriceType(commerceOptionValue.getPriceType()) &&
				 (commerceOptionValue.getCPInstanceId() <= 0))) {

				continue;
			}

			CPInstance commerceOptionValueCPInstance =
				_cpInstanceLocalService.getCPInstance(
					commerceOptionValue.getCPInstanceId());

			BigDecimal currentQuantity = quantity.multiply(
				commerceOptionValue.getQuantity());

			currentQuantity = currentQuantity.divide(
				unitOfMeasureIncrementalOrderQuantity, RoundingMode.HALF_UP);

			commerceProductPrice = _getCommerceProductPrice(
				commerceOrder,
				commerceOptionValueCPInstance.getCPDefinitionId(),
				commerceOptionValueCPInstance.getCPInstanceId(),
				commerceOptionValue.toJSON(), currentQuantity,
				commerceOptionValue.getUnitOfMeasureKey(), commerceContext);

			CommerceOrderItem childCommerceOrderItem = _createCommerceOrderItem(
				commerceOrder.getGroupId(), user, commerceOrder,
				commerceProductPrice, commerceOptionValueCPInstance,
				commerceOrderItem.getCommerceOrderItemId(),
				commerceOptionValue.toJSON(), currentQuantity, BigDecimal.ZERO,
				commerceProductPrice.getUnitOfMeasureIncrementalOrderQuantity(),
				commerceOptionValue.getUnitOfMeasureKey(), true,
				serviceContext);

			if (!_isStaticPriceType(commerceOptionValue.getPriceType())) {
				childCommerceOrderItem = commerceOrderItemPersistence.update(
					childCommerceOrderItem);

				continue;
			}

			childCommerceOrderItem.setUnitOfMeasureIncrementalOrderQuantity(
				BigDecimal.ONE);

			commerceProductPrice = _getStaticCommerceProductPrice(
				commerceOptionValue.getCPInstanceId(),
				commerceOrder.getCommerceCurrency(),
				childCommerceOrderItem.getCommerceOrder(), currentQuantity,
				commerceOptionValue.getPrice(), BigDecimal.ONE,
				commerceOptionValue.getUnitOfMeasureKey());

			_setCommerceOrderItemPrice(
				childCommerceOrderItem, commerceProductPrice);

			_setCommerceOrderItemDiscountValue(
				childCommerceOrderItem, commerceProductPrice.getDiscountValue(),
				false);

			_setCommerceOrderItemDiscountValue(
				childCommerceOrderItem,
				commerceProductPrice.getDiscountValueWithTaxAmount(), true);

			commerceOrderItemPersistence.update(childCommerceOrderItem);
		}

		if (commerceOrder.isManuallyAdjusted() && commerceOrder.isOpen()) {
			commerceOrder.setManuallyAdjusted(false);
		}

		if (!commerceOrder.isShippable() && commerceOrderItem.isShippable()) {
			commerceOrder.setShippable(true);
		}

		commerceOrderLocalService.updateCommerceOrder(commerceOrder);

		commerceOrderLocalService.recalculatePrice(
			commerceOrderItem.getCommerceOrderId(), commerceContext);

		return commerceOrderItem;
	}

	@Override
	public CommerceOrderItem addOrUpdateCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		json = _getSanitizedJSON(
			_cpInstanceLocalService.fetchCPInstance(cpInstanceId), json,
			serviceContext.getLocale());

		try (SafeCloseable safeCloseable =
				CommerceOrderItemThreadLocal.setSanitizedWithSafeCloseable(
					true)) {

			List<CommerceOrderItem> commerceOrderItems = getCommerceOrderItems(
				commerceOrderId, cpInstanceId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
				if ((commerceOrderItem.getParentCommerceOrderItemId() == 0) &&
					_jsonMatches(json, commerceOrderItem.getJson()) &&
					Objects.equals(
						GetterUtil.getString(unitOfMeasureKey),
						GetterUtil.getString(
							commerceOrderItem.getUnitOfMeasureKey()))) {

					return commerceOrderItemLocalService.
						updateCommerceOrderItem(
							commerceOrderItem.getExternalReferenceCode(),
							userId, commerceOrderItem.getCommerceOrderItemId(),
							commerceOrderItem.getJson(),
							quantity.add(commerceOrderItem.getQuantity()),
							commerceContext, serviceContext);
				}
			}

			return commerceOrderItemLocalService.addCommerceOrderItem(
				userId, commerceOrderId, cpInstanceId, json, quantity,
				replacedCPInstanceId, BigDecimal.ZERO, unitOfMeasureKey,
				commerceContext, serviceContext);
		}
	}

	@Override
	public int countSubscriptionCommerceOrderItems(long commerceOrderId) {
		return commerceOrderItemPersistence.countByC_S(commerceOrderId, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		_validateParentCommerceOrderId(commerceOrderItem);

		return _deleteCommerceOrderItem(userId, commerceOrderItem);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem,
			CommerceContext commerceContext)
		throws PortalException {

		// Commerce order item

		commerceOrderItemLocalService.deleteCommerceOrderItem(
			userId, commerceOrderItem);

		CommerceOrderLocalService commerceOrderLocalService =
			_commerceOrderLocalServiceSnapshot.get();

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		if (_commerceShippingHelper.isFreeShipping(commerceOrder)) {
			commerceOrderLocalService.updateCommerceShippingMethod(
				commerceOrder.getCommerceOrderId(), 0, null, BigDecimal.ZERO,
				commerceContext);
		}

		commerceOrderLocalService.recalculatePrice(
			commerceOrder.getCommerceOrderId(), commerceContext);

		return commerceOrderItem;
	}

	@Override
	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, long commerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		return commerceOrderItemLocalService.deleteCommerceOrderItem(
			userId, commerceOrderItem);
	}

	@Override
	public void deleteCommerceOrderItems(long userId, long commerceOrderId)
		throws PortalException {

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrderItemPersistence.findByCommerceOrderId(
				commerceOrderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (commerceOrderItem.hasParentCommerceOrderItem()) {
				continue;
			}

			commerceOrderItemLocalService.deleteCommerceOrderItem(
				userId, commerceOrderItem);
		}
	}

	@Override
	public void deleteCommerceOrderItemsByCPInstanceId(
			long userId, long cpInstanceId)
		throws PortalException {

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrderItemPersistence.findByCPInstanceId(cpInstanceId);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			commerceOrderItemLocalService.deleteCommerceOrderItem(
				userId, commerceOrderItem);
		}
	}

	@Override
	public void deleteMissingCommerceOrderItems(
			long userId, long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException {

		List<Long> commerceOrderItemIdsList = dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				CommerceOrderItemTable.INSTANCE.commerceOrderItemId
			).from(
				CommerceOrderItemTable.INSTANCE
			).where(
				CommerceOrderItemTable.INSTANCE.commerceOrderId.eq(
					commerceOrderId
				).and(
					CommerceOrderItemTable.INSTANCE.commerceOrderItemId.notIn(
						DSLQueryFactoryUtil.selectDistinct(
							CommerceOrderItemTable.INSTANCE.commerceOrderItemId
						).from(
							CommerceOrderItemTable.INSTANCE
						).where(
							_getPredicate(
								commerceOrderId, commerceOrderItemIds,
								externalReferenceCodes)
						))
				)
			));

		for (long commerceOrderItemId : commerceOrderItemIdsList) {
			commerceOrderItemLocalService.deleteCommerceOrderItem(
				userId, commerceOrderItemId);
		}
	}

	@Override
	public CommerceOrderItem
		fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId) {

		return commerceOrderItemPersistence.
			fetchByCommerceInventoryBookedQuantityId(
				commerceInventoryBookedQuantityId);
	}

	@Override
	public List<CommerceOrderItem> getAvailableForShipmentCommerceOrderItems(
		long commerceOrderId) {

		return commerceOrderItemFinder.findByAvailableQuantity(commerceOrderId);
	}

	@Override
	public List<CommerceOrderItem> getChildCommerceOrderItems(
		long parentCommerceOrderItemId) {

		return commerceOrderItemPersistence.findByParentCommerceOrderItemId(
			parentCommerceOrderItemId);
	}

	@Override
	public BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseId, commerceOrderItem.getSku(),
					commerceOrderItem.getUnitOfMeasureKey());

		if (commerceInventoryWarehouseItem == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal commerceInventoryWarehouseItemQuantity =
			commerceInventoryWarehouseItem.getQuantity();

		if (commerceInventoryWarehouseItemQuantity == null) {
			return BigDecimal.ZERO;
		}

		return commerceInventoryWarehouseItemQuantity;
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end) {

		return commerceOrderItemPersistence.findByCommerceOrderId(
			commerceOrderId, start, end);
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return commerceOrderItemPersistence.findByCommerceOrderId(
			commerceOrderId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long cpInstanceId, int[] orderStatuses, String unitOfMeasureKey,
		int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommerceOrderItemTable.INSTANCE
			).from(
				CommerceOrderItemTable.INSTANCE
			).innerJoinON(
				CommerceOrderTable.INSTANCE,
				CommerceOrderTable.INSTANCE.commerceOrderId.eq(
					CommerceOrderItemTable.INSTANCE.commerceOrderId)
			).where(
				CommerceOrderItemTable.INSTANCE.CPInstanceId.eq(
					cpInstanceId
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceOrderItemTable.INSTANCE.unitOfMeasureKey.
							eq(unitOfMeasureKey);
					}
				).and(
					CommerceOrderTable.INSTANCE.orderStatus.in(
						TransformUtil.transform(
							orderStatuses, Integer::valueOf, Integer.class))
				)
			).limit(
				start, end
			));
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end) {

		return commerceOrderItemPersistence.findByC_CPI(
			commerceOrderId, cpInstanceId, start, end);
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return commerceOrderItemPersistence.findByC_CPI(
			commerceOrderId, cpInstanceId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
		long groupId, long commerceAccountId, int[] orderStatuses, int start,
		int end) {

		return commerceOrderItemFinder.findByG_A_O(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	@Override
	public int getCommerceOrderItemsCount(long commerceOrderId) {
		return commerceOrderItemPersistence.countByCommerceOrderId(
			commerceOrderId);
	}

	@Override
	public int getCommerceOrderItemsCount(
		long commerceOrderId, long cpInstanceId) {

		return commerceOrderItemPersistence.countByC_CPI(
			commerceOrderId, cpInstanceId);
	}

	@Override
	public int getCommerceOrderItemsCount(
		long groupId, long commerceAccountId, int[] orderStatuses) {

		return commerceOrderItemFinder.countByG_A_O(
			groupId, commerceAccountId, orderStatuses);
	}

	@Override
	public BigDecimal getCommerceOrderItemsQuantity(long commerceOrderId) {
		return commerceOrderItemFinder.getCommerceOrderItemsQuantity(
			commerceOrderId);
	}

	@Override
	public List<Long> getCustomerCommerceOrderIds(long commerceOrderId) {
		return dslQuery(
			_getCustomerCommerceOrdersGroupByStep(
				commerceOrderId,
				DSLQueryFactoryUtil.selectDistinct(
					CommerceOrderItemTable.INSTANCE.commerceOrderId)));
	}

	@Override
	public int getCustomerCommerceOrderIdsCount(long commerceOrderId) {
		return dslQueryCount(
			_getCustomerCommerceOrdersGroupByStep(
				commerceOrderId,
				DSLQueryFactoryUtil.countDistinct(
					CommerceOrderItemTable.INSTANCE.commerceOrderItemId)));
	}

	@Override
	public List<CommerceOrderItem> getParentCommerceOrderItems(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return commerceOrderItemPersistence.findByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	@Override
	public int getParentCommerceOrderItemsCount(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return commerceOrderItemPersistence.countByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId);
	}

	@Override
	public List<CommerceOrderItem> getSubscriptionCommerceOrderItems(
		long commerceOrderId) {

		return commerceOrderItemPersistence.findByC_S(commerceOrderId, true);
	}

	@Override
	public List<Long> getSupplierCommerceOrderIds(long commerceOrderId) {
		return dslQuery(
			_getSupplierCommerceOrdersGroupByStep(
				commerceOrderId,
				DSLQueryFactoryUtil.selectDistinct(
					CommerceOrderItemTable.INSTANCE.commerceOrderId)));
	}

	@Override
	public int getSupplierCommerceOrderIdsCount(long commerceOrderId) {
		return dslQueryCount(
			_getSupplierCommerceOrdersGroupByStep(
				commerceOrderId,
				DSLQueryFactoryUtil.countDistinct(
					CommerceOrderItemTable.INSTANCE.commerceOrderItemId)));
	}

	@Override
	public List<CommerceOrderItem> getSupplierCommerceOrderItems(
		long customerCommerceOrderItemId, int start, int end) {

		return commerceOrderItemPersistence.findByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId, start, end);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem importCommerceOrderItem(
			long userId, String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json, BigDecimal quantity,
			BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isBlank(json)) {
			json = _getCPInstanceOptionValueRelsJSON(cpInstanceId);
		}

		CommerceOrderLocalService commerceOrderLocalService =
			_commerceOrderLocalServiceSnapshot.get();

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		User user = _userLocalService.getUser(userId);

		_updateWorkflow(user.getUserId(), commerceOrder);

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.fetchByPrimaryKey(commerceOrderItemId);

		if ((commerceOrderItem == null) &&
			!Validator.isBlank(externalReferenceCode)) {

			commerceOrderItem = commerceOrderItemPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (commerceOrderItem == null) {
			commerceOrderItem = _createCommerceOrderItem(
				commerceOrder.getGroupId(), user, commerceOrder, null,
				cpInstance, 0, json, quantity, shippedQuantity,
				unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey, false,
				serviceContext);
		}
		else {
			commerceOrderItem = _updateCommerceOrderItem(
				commerceOrderItem, externalReferenceCode, user, commerceOrder,
				cpInstance, json, quantity, shippedQuantity,
				unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
				serviceContext);
		}

		if (!Validator.isBlank(cpMeasurementUnitKey)) {
			CPMeasurementUnit cpMeasurementUnit =
				_cpMeasurementUnitLocalService.getCPMeasurementUnitByKey(
					user.getCompanyId(), cpMeasurementUnitKey);

			commerceOrderItem.setCPMeasurementUnitId(
				cpMeasurementUnit.getCPMeasurementUnitId());
		}

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Override
	public CommerceOrderItem incrementShippedQuantity(
			long commerceOrderItemId, BigDecimal shippedQuantity)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		shippedQuantity = shippedQuantity.add(
			commerceOrderItem.getShippedQuantity());

		if (BigDecimalUtil.lt(shippedQuantity, BigDecimal.ZERO)) {
			shippedQuantity = BigDecimal.ZERO;
		}

		commerceOrderItem.setShippedQuantity(shippedQuantity);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId,
			String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			commerceOrderId, parentCommerceOrderItemId, start, end, sort);

		searchContext.setKeywords(keywords);

		return _searchCommerceOrderItems(searchContext);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			commerceOrderId, null, start, end, sort);

		searchContext.setKeywords(keywords);

		return _searchCommerceOrderItems(searchContext);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String name, String sku, boolean andOperator,
			int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			commerceOrderId, null, start, end, sort);

		searchContext.setAndSearch(andOperator);
		searchContext.setAttribute(Field.NAME, name);
		searchContext.setAttribute("sku", sku);

		return _searchCommerceOrderItems(searchContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long commerceInventoryBookedQuantityId)
		throws NoSuchOrderItemException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem.setCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);

		try {
			_reindexCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, BigDecimal quantity,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem.getExternalReferenceCode(), userId,
			commerceOrderItemId, commerceOrderItem.getJson(), quantity,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem =
			commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem.getExternalReferenceCode(), userId,
				commerceOrderItemId, commerceOrderItem.getJson(), quantity,
				commerceContext, serviceContext);

		commerceOrderItem.setCPMeasurementUnitId(cpMeasurementUnitId);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem =
			commerceOrderItemLocalService.updateCommerceOrderItem(
				userId, commerceOrderItemId, commerceOrderItem.getJson(),
				quantity, serviceContext);

		commerceOrderItem.setCPMeasurementUnitId(cpMeasurementUnitId);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, String json,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		_validateParentCommerceOrderId(commerceOrderItem);

		json = _getSanitizedJSON(
			commerceOrderItem.fetchCPInstance(), json,
			serviceContext.getLocale());

		List<CommerceOrderItem> childCommerceOrderItems =
			commerceOrderItemPersistence.findByParentCommerceOrderItemId(
				commerceOrderItemId);

		if (childCommerceOrderItems.isEmpty()) {
			return _updateCommerceOrderItem(
				userId, commerceOrderItemId, quantity, json, serviceContext);
		}

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.toCommerceOptionValues(json);

		for (CommerceOrderItem childCommerceOrderItem :
				childCommerceOrderItems) {

			CommerceOptionValue commerceOptionValue =
				_commerceOptionValueHelper.toCommerceOptionValue(
					childCommerceOrderItem.getJson());

			CommerceOptionValue matchedCommerceOptionValue =
				commerceOptionValue.getFirstMatch(commerceOptionValues);

			if (matchedCommerceOptionValue == null) {
				throw new NoSuchOrderItemException(
					"Child commerce order item does not match any JSON item");
			}

			BigDecimal currentQuantity = quantity.multiply(
				commerceOptionValue.getQuantity());

			currentQuantity = currentQuantity.divide(
				commerceOrderItem.getUnitOfMeasureIncrementalOrderQuantity(),
				RoundingMode.HALF_UP);

			_updateCommerceOrderItem(
				userId, childCommerceOrderItem.getCommerceOrderItemId(),
				currentQuantity, childCommerceOrderItem.getJson(),
				serviceContext);
		}

		return _updateCommerceOrderItem(
			userId, commerceOrderItemId, quantity, json, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long userId, long commerceOrderItemId,
			String json, BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		_validateParentCommerceOrderId(commerceOrderItem);

		json = _getSanitizedJSON(
			commerceOrderItem.fetchCPInstance(), json,
			serviceContext.getLocale());

		List<CommerceOrderItem> childCommerceOrderItems =
			commerceOrderItemPersistence.findByParentCommerceOrderItemId(
				commerceOrderItemId);

		if (childCommerceOrderItems.isEmpty()) {
			return _updateCommerceOrderItem(
				externalReferenceCode, userId, commerceOrderItemId, quantity,
				json, null, commerceContext, serviceContext);
		}

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.toCommerceOptionValues(json);

		for (CommerceOrderItem childCommerceOrderItem :
				childCommerceOrderItems) {

			CommerceOptionValue commerceOptionValue =
				_commerceOptionValueHelper.toCommerceOptionValue(
					childCommerceOrderItem.getJson());

			CommerceOptionValue matchedCommerceOptionValue =
				commerceOptionValue.getFirstMatch(commerceOptionValues);

			if (matchedCommerceOptionValue == null) {
				throw new NoSuchOrderItemException(
					"Child commerce order item does not match any JSON item");
			}

			BigDecimal currentQuantity = quantity.multiply(
				commerceOptionValue.getQuantity());

			currentQuantity = currentQuantity.divide(
				commerceOrderItem.getUnitOfMeasureIncrementalOrderQuantity(),
				RoundingMode.HALF_UP);

			if (!_isStaticPriceType(commerceOptionValue.getPriceType())) {
				_updateCommerceOrderItem(
					childCommerceOrderItem.getExternalReferenceCode(), userId,
					childCommerceOrderItem.getCommerceOrderItemId(),
					currentQuantity, childCommerceOrderItem.getJson(), null,
					commerceContext, serviceContext);

				continue;
			}

			CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

			CommerceProductPrice staticCommerceProductPrice =
				_getStaticCommerceProductPrice(
					commerceOptionValue.getCPInstanceId(),
					commerceOrder.getCommerceCurrency(),
					childCommerceOrderItem.getCommerceOrder(), currentQuantity,
					commerceOptionValue.getPrice(), BigDecimal.ONE,
					childCommerceOrderItem.getUnitOfMeasureKey());

			_updateCommerceOrderItem(
				childCommerceOrderItem.getExternalReferenceCode(), userId,
				childCommerceOrderItem.getCommerceOrderItemId(),
				currentQuantity, childCommerceOrderItem.getJson(),
				staticCommerceProductPrice, commerceContext, serviceContext);
		}

		return _updateCommerceOrderItem(
			externalReferenceCode, userId, commerceOrderItemId, quantity, json,
			null, commerceContext, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemDeliveryDate(
			long commerceOrderItemId, Date requestedDeliveryDate)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem.setRequestedDeliveryDate(requestedDeliveryDate);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem.setShippingAddressId(shippingAddressId);
		commerceOrderItem.setDeliveryGroupName(deliveryGroupName);
		commerceOrderItem.setPrintedNote(printedNote);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear)
		throws PortalException {

		Date requestedDeliveryDate = _portal.getDate(
			requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		commerceOrderItem.setShippingAddressId(shippingAddressId);
		commerceOrderItem.setDeliveryGroupName(deliveryGroupName);
		commerceOrderItem.setPrintedNote(printedNote);
		commerceOrderItem.setRequestedDeliveryDate(requestedDeliveryDate);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, String deliveryGroupName,
			long shippingAddressId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute, ServiceContext serviceContext)
		throws PortalException {

		return commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemPrice(
			long commerceOrderItemId, CommerceContext commerceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		if (commerceOrderItem.isManuallyAdjusted() ||
			(commerceOrderItem.getParentCommerceOrderItemId() != 0)) {

			return commerceOrderItem;
		}

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if (cpInstance == null) {
			return commerceOrderItem;
		}

		List<CommerceOrderItem> childCommerceOrderItems =
			commerceOrderItemPersistence.findByParentCommerceOrderItemId(
				commerceOrderItemId);

		for (CommerceOrderItem childCommerceOrderItem :
				childCommerceOrderItems) {

			CommerceOptionValue commerceOptionValue =
				_commerceOptionValueHelper.toCommerceOptionValue(
					childCommerceOrderItem.getJson());

			if (!_isStaticPriceType(commerceOptionValue.getPriceType())) {
				_setCommerceOrderItemPrice(
					childCommerceOrderItem, null, commerceContext);
			}
			else {
				CommerceOrder commerceOrder =
					commerceOrderItem.getCommerceOrder();

				_setCommerceOrderItemPrice(
					childCommerceOrderItem,
					_getStaticCommerceProductPrice(
						commerceOptionValue.getCPInstanceId(),
						commerceOrder.getCommerceCurrency(),
						childCommerceOrderItem.getCommerceOrder(),
						childCommerceOrderItem.getQuantity(),
						commerceOptionValue.getPrice(), BigDecimal.ONE,
						childCommerceOrderItem.getUnitOfMeasureKey()),
					commerceContext);
			}

			commerceOrderItemPersistence.update(childCommerceOrderItem);
		}

		commerceOrderItem = commerceOrderItemPersistence.findByPrimaryKey(
			commerceOrderItemId);

		_setCommerceOrderItemPrice(commerceOrderItem, null, commerceContext);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, BigDecimal discountAmount,
			BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel4, BigDecimal finalPrice,
			BigDecimal promoPrice, BigDecimal unitPrice)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		_validateParentCommerceOrderId(commerceOrderItem);

		boolean discountChanged = _isDiscountChanged(
			discountAmount, commerceOrderItem);
		boolean priceChanged = _isPriceChanged(
			finalPrice, promoPrice, unitPrice, commerceOrderItem);

		commerceOrderItem.setDiscountAmount(
			(BigDecimal)GetterUtil.get(discountAmount, BigDecimal.ZERO));

		if (!commerceOrderItem.isDiscountManuallyAdjusted() &&
			discountChanged) {

			commerceOrderItem.setDiscountManuallyAdjusted(true);
		}

		commerceOrderItem.setDiscountPercentageLevel1(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel1, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel2(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel2, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel3(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel3, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel4(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel4, BigDecimal.ZERO));
		commerceOrderItem.setFinalPrice(
			(BigDecimal)GetterUtil.get(finalPrice, BigDecimal.ZERO));
		commerceOrderItem.setManuallyAdjusted(true);

		if (!commerceOrderItem.isPriceManuallyAdjusted() && priceChanged) {
			commerceOrderItem.setPriceManuallyAdjusted(true);
		}

		commerceOrderItem.setPromoPrice(
			(BigDecimal)GetterUtil.get(promoPrice, BigDecimal.ZERO));
		commerceOrderItem.setUnitPrice(
			(BigDecimal)GetterUtil.get(unitPrice, BigDecimal.ZERO));

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, BigDecimal discountAmount,
			BigDecimal discountAmountWithTaxAmount,
			BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel1WithTaxAmount,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel2WithTaxAmount,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel3WithTaxAmount,
			BigDecimal discountPercentageLevel4,
			BigDecimal discountPercentageLevel4WithTaxAmount,
			BigDecimal finalPrice, BigDecimal finalPriceWithTaxAmount,
			BigDecimal promoPrice, BigDecimal promoPriceWithTaxAmount,
			BigDecimal unitPrice, BigDecimal unitPriceWithTaxAmount)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		boolean discountChanged = _isDiscountChanged(
			discountAmount, discountAmountWithTaxAmount, commerceOrderItem);
		boolean priceChanged = _isPriceChanged(
			finalPrice, finalPriceWithTaxAmount, promoPrice,
			promoPriceWithTaxAmount, unitPrice, unitPriceWithTaxAmount,
			commerceOrderItem);

		commerceOrderItem.setDiscountAmount(
			(BigDecimal)GetterUtil.get(discountAmount, BigDecimal.ZERO));

		if (!commerceOrderItem.isDiscountManuallyAdjusted() &&
			discountChanged) {

			commerceOrderItem.setDiscountManuallyAdjusted(true);
		}

		commerceOrderItem.setDiscountPercentageLevel1(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel1, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel2(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel2, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel3(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel3, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel4(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel4, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel1WithTaxAmount(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel1WithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel2WithTaxAmount(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel2WithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel3WithTaxAmount(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel3WithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setDiscountPercentageLevel4WithTaxAmount(
			(BigDecimal)GetterUtil.get(
				discountPercentageLevel4WithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setDiscountWithTaxAmount(
			(BigDecimal)GetterUtil.get(
				discountAmountWithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setFinalPrice(
			(BigDecimal)GetterUtil.get(finalPrice, BigDecimal.ZERO));
		commerceOrderItem.setFinalPriceWithTaxAmount(
			(BigDecimal)GetterUtil.get(
				finalPriceWithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setManuallyAdjusted(true);

		if (!commerceOrderItem.isPriceManuallyAdjusted() && priceChanged) {
			commerceOrderItem.setPriceManuallyAdjusted(true);
		}

		commerceOrderItem.setPromoPrice(
			(BigDecimal)GetterUtil.get(promoPrice, BigDecimal.ZERO));
		commerceOrderItem.setPromoPriceWithTaxAmount(
			(BigDecimal)GetterUtil.get(
				promoPriceWithTaxAmount, BigDecimal.ZERO));
		commerceOrderItem.setUnitPrice(
			(BigDecimal)GetterUtil.get(unitPrice, BigDecimal.ZERO));
		commerceOrderItem.setUnitPriceWithTaxAmount(
			(BigDecimal)GetterUtil.get(
				unitPriceWithTaxAmount, BigDecimal.ZERO));

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, BigDecimal unitPrice)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		if (!commerceOrderItem.isPriceManuallyAdjusted() &&
			_isPriceChanged(unitPrice, commerceOrderItem)) {

			commerceOrderItem.setPriceManuallyAdjusted(true);
		}

		commerceOrderItem.setUnitPrice(unitPrice);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long userId, long commerceOrderItemId, BigDecimal quantity,
			BigDecimal unitPrice)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		if (!commerceOrderItem.isPriceManuallyAdjusted() &&
			_isPriceChanged(unitPrice, commerceOrderItem)) {

			commerceOrderItem.setPriceManuallyAdjusted(true);
		}

		_updateCommerceInventoryBookedQuantity(
			userId, commerceOrderItem,
			commerceOrderItem.getCommerceInventoryBookedQuantityId(), quantity,
			commerceOrderItem.getQuantity());

		commerceOrderItem.setManuallyAdjusted(true);
		commerceOrderItem.setQuantity(quantity);
		commerceOrderItem.setUnitPrice(unitPrice);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderItem updateCustomFields(
			long commerceOrderItemId, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		commerceOrderItem.setExpandoBridgeAttributes(serviceContext);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);
	}

	@Override
	public CommerceOrderItem updateExternalReferenceCode(
			long commerceOrderItemId, String externalReferenceCode)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		commerceOrderItem.setExternalReferenceCode(externalReferenceCode);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_commerceOrderConfiguration = ConfigurableUtil.createConfigurable(
			CommerceOrderConfiguration.class, properties);
	}

	private SearchContext _buildSearchContext(
			long commerceOrderId, Long parentCommerceOrderItemId, int start,
			int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = new SearchContext();

		CommerceOrderLocalService commerceOrderLocalService =
			_commerceOrderLocalServiceSnapshot.get();

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		searchContext.setAttribute("commerceOrderId", commerceOrderId);

		if (parentCommerceOrderItemId != null) {
			searchContext.setAttribute(
				"parentCommerceOrderItemId", parentCommerceOrderItemId);
		}

		searchContext.setCompanyId(commerceOrder.getCompanyId());
		searchContext.setEnd(end);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private CommerceOrderItem _createCommerceOrderItem(
			long groupId, User user, CommerceOrder commerceOrder,
			CommerceProductPrice commerceProductPrice, CPInstance cpInstance,
			long parentCommerceOrderItemId, String json, BigDecimal quantity,
			BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, boolean child,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpInstance.getCPDefinitionId());

		_validate(
			serviceContext.getLocale(), commerceOrder, cpDefinition, cpInstance,
			json, quantity, unitOfMeasureKey, child,
			GetterUtil.getBoolean(
				serviceContext.getAttribute("validateOrder"), true));

		long commerceOrderItemId = counterLocalService.increment();

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.create(commerceOrderItemId);

		commerceOrderItem.setGroupId(groupId);
		commerceOrderItem.setCompanyId(user.getCompanyId());
		commerceOrderItem.setUserId(user.getUserId());
		commerceOrderItem.setUserName(user.getFullName());
		commerceOrderItem.setCommerceOrderId(
			commerceOrder.getCommerceOrderId());
		commerceOrderItem.setCPInstanceId(cpInstance.getCPInstanceId());
		commerceOrderItem.setCProductId(cpDefinition.getCProductId());
		commerceOrderItem.setParentCommerceOrderItemId(
			parentCommerceOrderItemId);
		commerceOrderItem.setFreeShipping(cpDefinition.isFreeShipping());
		commerceOrderItem.setJson(json);
		commerceOrderItem.setManuallyAdjusted(false);
		commerceOrderItem.setNameMap(cpDefinition.getNameMap());
		commerceOrderItem.setQuantity(quantity);
		commerceOrderItem.setShipSeparately(cpDefinition.isShipSeparately());
		commerceOrderItem.setShippable(cpDefinition.isShippable());
		commerceOrderItem.setShippedQuantity(shippedQuantity);
		commerceOrderItem.setShippingExtraPrice(
			cpDefinition.getShippingExtraPrice());
		commerceOrderItem.setSku(cpInstance.getSku());
		commerceOrderItem.setSubscription(_isSubscription(cpInstance));
		commerceOrderItem.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);
		commerceOrderItem.setUnitOfMeasureKey(unitOfMeasureKey);
		commerceOrderItem.setExpandoBridgeAttributes(serviceContext);

		_setDimensions(commerceOrderItem, cpInstance);
		_setSubscriptionInfo(commerceOrderItem, cpInstance);

		if (!ExportImportThreadLocal.isImportInProcess() &&
			(commerceProductPrice != null)) {

			_setCommerceOrderItemPrice(commerceOrderItem, commerceProductPrice);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem, commerceProductPrice.getDiscountValue(),
				false);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem,
				commerceProductPrice.getDiscountValueWithTaxAmount(), true);
		}

		_validate(serviceContext.getLocale(), commerceOrderItem, true);

		return commerceOrderItem;
	}

	private void _deleteBundleChildrenOrderItems(
			long userId, long commerceOrderItemId)
		throws PortalException {

		List<CommerceOrderItem> childCommerceOrderItems =
			commerceOrderItemPersistence.findByParentCommerceOrderItemId(
				commerceOrderItemId);

		for (CommerceOrderItem childCommerceOrderItem :
				childCommerceOrderItems) {

			childCommerceOrderItem.setParentCommerceOrderItemId(0);

			commerceOrderItemLocalService.deleteCommerceOrderItem(
				userId, childCommerceOrderItem);
		}
	}

	private CommerceOrderItem _deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		// Bundle order items

		_deleteBundleChildrenOrderItems(
			userId, commerceOrderItem.getCommerceOrderItemId());

		// Commerce order item

		commerceOrderItemPersistence.remove(commerceOrderItem);

		// Commerce Inventory Booked quantities

		if (commerceOrderItem.getCommerceInventoryBookedQuantityId() > 0) {
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
				_commerceInventoryBookedQuantityLocalService.
					fetchCommerceInventoryBookedQuantity(
						commerceOrderItem.
							getCommerceInventoryBookedQuantityId());

			if (commerceInventoryBookedQuantity != null) {
				_commerceInventoryBookedQuantityLocalService.
					deleteCommerceInventoryBookedQuantity(
						userId,
						commerceOrderItem.
							getCommerceInventoryBookedQuantityId(),
						HashMapBuilder.put(
							CommerceInventoryAuditTypeConstants.ORDER_ID,
							String.valueOf(
								commerceOrderItem.getCommerceOrderId())
						).build(),
						_commerceInventoryAuditTypeRegistry.
							getCommerceInventoryAuditType(
								CommerceInventoryConstants.
									AUDIT_TYPE_DELETE_BOOKED_QUANTITY));
			}
		}

		// Expando

		_expandoRowLocalService.deleteRows(
			commerceOrderItem.getCommerceOrderItemId());

		_updateWorkflow(userId, commerceOrderItem.getCommerceOrder());

		return commerceOrderItem;
	}

	private List<CommerceOrderItem> _getCommerceOrderItems(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceOrderItem> commerceOrderItems = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceOrderItemId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceOrderItem commerceOrderItem = fetchCommerceOrderItem(
				commerceOrderItemId);

			if (commerceOrderItem == null) {
				commerceOrderItems = null;
			}
			else if (commerceOrderItems != null) {
				commerceOrderItems.add(commerceOrderItem);
			}
		}

		return commerceOrderItems;
	}

	private CommerceProductPrice _getCommerceProductPrice(
			CommerceOrder commerceOrder, long cpDefinitionId, long cpInstanceId,
			String json, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCalculateTax(true);

		long accountEntryId = 0;

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			accountEntryId = accountEntry.getAccountEntryId();
		}

		CommerceContextFactory commerceContextFactory =
			_commerceContextFactorySnapshot.get();

		commerceProductPriceRequest.setCommerceContext(
			commerceContextFactory.create(
				accountEntryId, commerceContext.getCommerceChannelGroupId(),
				commerceOrder.getCommerceCurrencyCode(),
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getCompanyId()));

		commerceProductPriceRequest.setCommerceOptionValues(
			_getStaticOptionValuesNotLinkedToSku(cpDefinitionId, json));
		commerceProductPriceRequest.setCpInstanceId(cpInstanceId);
		commerceProductPriceRequest.setQuantity(quantity);
		commerceProductPriceRequest.setSecure(false);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		return _commerceProductPriceCalculation.getCommerceProductPrice(
			commerceProductPriceRequest);
	}

	private BigDecimal _getConvertedPrice(
			long cpInstanceId, BigDecimal price, CommerceOrder commerceOrder)
		throws PortalException {

		return CommercePriceConverterUtil.getConvertedPrice(
			commerceOrder.getGroupId(), cpInstanceId,
			commerceOrder.getBillingAddressId(),
			commerceOrder.getShippingAddressId(), price, false,
			_commerceTaxCalculation);
	}

	private JSONObject _getCPDefinitionOptionRelJSONObject(
		CPDefinitionOptionRel cpDefinitionOptionRel, JSONArray jsonArray) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject == null) {
				continue;
			}

			if (Objects.equals(
					cpDefinitionOptionRel.getKey(),
					jsonObject.getString("key")) ||
				Objects.equals(
					cpDefinitionOptionRel.getKey(),
					jsonObject.getString("skuOptionKey"))) {

				return jsonObject;
			}
		}

		return null;
	}

	private JSONArray _getCPDefinitionOptionRelValueJSONArray(
			CPDefinitionOptionRel cpDefinitionOptionRel, long cpInstanceId,
			JSONObject jsonObject)
		throws PortalException {

		JSONArray valueJSONArray = _jsonFactory.createJSONArray();

		if (cpDefinitionOptionRel.isSkuContributor()) {
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpInstanceOptionValueRelLocalService.
					fetchCPDefinitionOptionValueRel(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						cpInstanceId);

			if (cpDefinitionOptionValueRel == null) {
				throw new CPDefinitionOptionRelException();
			}

			valueJSONArray.put(cpDefinitionOptionValueRel.getKey());

			return valueJSONArray;
		}

		if (JSONUtil.isEmpty(jsonObject)) {
			return valueJSONArray;
		}

		String value = GetterUtil.getString(
			jsonObject.getString("skuOptionValueKey"));

		if (Validator.isBlank(value)) {
			value = GetterUtil.getString(jsonObject.getString("value"));
		}

		if (Validator.isBlank(value)) {
			if (cpDefinitionOptionRel.isRequired() ||
				cpDefinitionOptionRel.isSkuContributor()) {

				throw new CPDefinitionOptionRelException();
			}

			return valueJSONArray;
		}

		if (JSONUtil.isJSONArray(value)) {
			valueJSONArray = _jsonFactory.createJSONArray(value);
		}
		else {
			valueJSONArray.put(value);
		}

		CommerceOptionType commerceOptionType =
			_commerceOptionTypeRegistry.getCommerceOptionType(
				cpDefinitionOptionRel.getCommerceOptionTypeKey());

		if ((commerceOptionType != null) &&
			CPConstants.PRODUCT_OPTION_DOCUMENT_LIBRARY_KEY.equals(
				commerceOptionType.getKey()) &&
			JSONUtil.isJSONObject(valueJSONArray.getString(0))) {

			JSONObject valueJSONObject = _jsonFactory.createJSONObject(
				valueJSONArray.getString(0));

			if (JSONUtil.isEmpty(valueJSONObject)) {
				valueJSONArray = _jsonFactory.createJSONArray();
			}
		}

		if ((commerceOptionType != null) &&
			commerceOptionType.isValid(
				cpDefinitionOptionRel,
				JSONUtil.toStringArray(valueJSONArray))) {

			return valueJSONArray;
		}

		throw new CPDefinitionOptionRelException();
	}

	private String _getCPInstanceOptionValueRelsJSON(long cpInstanceId)
		throws PortalException {

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					cpInstanceId));

		return jsonArray.toString();
	}

	private GroupByStep _getCustomerCommerceOrdersGroupByStep(
		long commerceOrderId, FromStep fromStep) {

		return fromStep.from(
			CommerceOrderItemTable.INSTANCE
		).where(
			CommerceOrderItemTable.INSTANCE.commerceOrderItemId.in(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceOrderItemTable.INSTANCE.customerCommerceOrderItemId
				).from(
					CommerceOrderItemTable.INSTANCE
				).where(
					CommerceOrderItemTable.INSTANCE.commerceOrderId.eq(
						commerceOrderId)
				))
		);
	}

	private Predicate _getPredicate(
		long commerceOrderId, Long[] commerceOrderItemIds,
		String[] externalReferenceCodes) {

		return CommerceOrderItemTable.INSTANCE.commerceOrderId.eq(
			commerceOrderId
		).and(
			() -> {
				Predicate predicate = null;

				if (ArrayUtil.isNotEmpty(commerceOrderItemIds)) {
					predicate =
						CommerceOrderItemTable.INSTANCE.commerceOrderItemId.in(
							commerceOrderItemIds);
				}

				if (ArrayUtil.isNotEmpty(externalReferenceCodes)) {
					if (predicate != null) {
						return predicate.or(
							CommerceOrderItemTable.INSTANCE.
								externalReferenceCode.in(
									externalReferenceCodes));
					}

					predicate =
						CommerceOrderItemTable.INSTANCE.externalReferenceCode.
							in(externalReferenceCodes);
				}

				return predicate;
			}
		);
	}

	private String _getSanitizedJSON(
			CPInstance cpInstance, String json, Locale locale)
		throws PortalException {

		if (CommerceOrderItemThreadLocal.isSanitized() ||
			(cpInstance == null)) {

			return json;
		}

		JSONArray sanitizedJSONArray = _jsonFactory.createJSONArray();

		if (!JSONUtil.isJSONArray(json)) {
			json = "[]";
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpInstance.getCPDefinitionId());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			JSONObject jsonObject = _getCPDefinitionOptionRelJSONObject(
				cpDefinitionOptionRel, jsonArray);

			if (cpDefinitionOptionRel.isRequired() &&
				!cpDefinitionOptionRel.isSkuContributor() &&
				JSONUtil.isEmpty(jsonObject)) {

				throw new CPDefinitionOptionRelException();
			}

			JSONArray cpDefinitionOptionRelValueJSONArray =
				_getCPDefinitionOptionRelValueJSONArray(
					cpDefinitionOptionRel, cpInstance.getCPInstanceId(),
					jsonObject);

			JSONObject sanitizedJSONObject = _jsonFactory.createJSONObject(
			).put(
				"key", cpDefinitionOptionRel.getKey()
			).put(
				"skuOptionKey", cpDefinitionOptionRel.getKey()
			).put(
				"skuOptionName", cpDefinitionOptionRel.getName(locale)
			).put(
				"value", cpDefinitionOptionRelValueJSONArray
			);

			String commerceOptionTypeKey =
				cpDefinitionOptionRel.getCommerceOptionTypeKey();

			if (ArrayUtil.contains(
					CPConstants.PRODUCT_OPTION_SKU_CONTRIBUTOR_FIELD_TYPES,
					commerceOptionTypeKey) &&
				!JSONUtil.isEmpty(cpDefinitionOptionRelValueJSONArray)) {

				sanitizedJSONObject.put(
					"skuOptionValueKey",
					cpDefinitionOptionRelValueJSONArray.getString(0));
			}

			if (ArrayUtil.contains(
					CPConstants.PRODUCT_OPTION_MULTIPLE_VALUES_FIELD_TYPES,
					commerceOptionTypeKey)) {

				JSONArray skuOptionValueNamesJSONArray =
					_getSkuOptionValueNamesJSONArray(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						cpDefinitionOptionRelValueJSONArray, locale);

				if (!JSONUtil.isEmpty(skuOptionValueNamesJSONArray)) {
					sanitizedJSONObject.put(
						"skuOptionValueNames", skuOptionValueNamesJSONArray);
				}
			}

			if (!cpDefinitionOptionRel.isPriceContributor() ||
				JSONUtil.isEmpty(cpDefinitionOptionRelValueJSONArray)) {

				sanitizedJSONArray.put(sanitizedJSONObject);

				continue;
			}

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					fetchCPDefinitionOptionValueRel(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						cpDefinitionOptionRelValueJSONArray.getString(0));

			if (cpDefinitionOptionValueRel == null) {
				sanitizedJSONArray.put(sanitizedJSONObject);

				continue;
			}

			BigDecimal price = BigDecimal.ZERO;

			if (cpDefinitionOptionRel.isPriceTypeStatic()) {
				price = cpDefinitionOptionValueRel.getPrice();
			}

			sanitizedJSONObject.put(
				"price", String.valueOf(price)
			).put(
				"priceType", cpDefinitionOptionRel.getPriceType()
			);

			CPInstance cpDefinitionOptionValueRelCPInstance =
				cpDefinitionOptionValueRel.fetchCPInstance();

			if (cpDefinitionOptionValueRelCPInstance != null) {
				sanitizedJSONObject.put(
					"quantity",
					String.valueOf(cpDefinitionOptionValueRel.getQuantity())
				).put(
					"skuId",
					cpDefinitionOptionValueRelCPInstance.getCPInstanceId()
				);
			}

			sanitizedJSONArray.put(sanitizedJSONObject);
		}

		return sanitizedJSONArray.toString();
	}

	private JSONArray _getSkuOptionValueNamesJSONArray(
		long cpDefinitionOptionRelId, JSONArray jsonArray, Locale locale) {

		JSONArray skuOptionValueNamesJSONArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					fetchCPDefinitionOptionValueRel(
						cpDefinitionOptionRelId, jsonArray.getString(i));

			if (cpDefinitionOptionValueRel == null) {
				continue;
			}

			skuOptionValueNamesJSONArray.put(
				cpDefinitionOptionValueRel.getName(locale));
		}

		return skuOptionValueNamesJSONArray;
	}

	private CommerceProductPrice _getStaticCommerceProductPrice(
			long cpInstanceId, CommerceCurrency commerceCurrency,
			CommerceOrder commerceOrder, BigDecimal quantity,
			BigDecimal optionValuePrice,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey)
		throws PortalException {

		if (unitOfMeasureIncrementalOrderQuantity == null) {
			unitOfMeasureIncrementalOrderQuantity = BigDecimal.ONE;
		}

		CommerceProductPriceImpl commerceProductPriceImpl =
			new CommerceProductPriceImpl();

		if (optionValuePrice == null) {
			optionValuePrice = BigDecimal.ZERO;
		}

		commerceProductPriceImpl.setUnitPrice(
			_commerceMoneyFactory.create(commerceCurrency, optionValuePrice));
		commerceProductPriceImpl.setUnitPromoPrice(
			_commerceMoneyFactory.create(commerceCurrency, BigDecimal.ZERO));
		commerceProductPriceImpl.setUnitPromoPriceWithTaxAmount(
			_commerceMoneyFactory.create(commerceCurrency, BigDecimal.ZERO));

		BigDecimal unitPriceWithTaxAmount = optionValuePrice;

		BigDecimal finalPriceWithTaxAmount = optionValuePrice;

		if (cpInstanceId > 0) {
			unitPriceWithTaxAmount = _getConvertedPrice(
				cpInstanceId, optionValuePrice, commerceOrder);

			optionValuePrice = optionValuePrice.multiply(quantity);

			finalPriceWithTaxAmount = _getConvertedPrice(
				cpInstanceId, optionValuePrice, commerceOrder);
		}

		commerceProductPriceImpl.setUnitPriceWithTaxAmount(
			_commerceMoneyFactory.create(
				commerceCurrency, unitPriceWithTaxAmount));
		commerceProductPriceImpl.setFinalPrice(
			_commerceMoneyFactory.create(commerceCurrency, optionValuePrice));
		commerceProductPriceImpl.setFinalPriceWithTaxAmount(
			_commerceMoneyFactory.create(
				commerceCurrency, finalPriceWithTaxAmount));
		commerceProductPriceImpl.setCommerceDiscountValue(null);
		commerceProductPriceImpl.setQuantity(quantity);
		commerceProductPriceImpl.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);
		commerceProductPriceImpl.setUnitOfMeasureKey(unitOfMeasureKey);

		return commerceProductPriceImpl;
	}

	private List<CommerceOptionValue> _getStaticOptionValuesNotLinkedToSku(
			long cpDefinitionId, String jsonArrayString)
		throws PortalException {

		return ListUtil.filter(
			_commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
				cpDefinitionId, jsonArrayString),
			commerceOptionValue ->
				_isStaticPriceType(commerceOptionValue.getPriceType()) &&
				(commerceOptionValue.getCPInstanceId() == 0));
	}

	private GroupByStep _getSupplierCommerceOrdersGroupByStep(
		long commerceOrderId, FromStep fromStep) {

		return fromStep.from(
			CommerceOrderItemTable.INSTANCE
		).where(
			CommerceOrderItemTable.INSTANCE.customerCommerceOrderItemId.in(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceOrderItemTable.INSTANCE.commerceOrderItemId
				).from(
					CommerceOrderItemTable.INSTANCE
				).where(
					CommerceOrderItemTable.INSTANCE.commerceOrderId.eq(
						commerceOrderId)
				))
		);
	}

	private boolean _isDiscountChanged(
		BigDecimal discountAmount, BigDecimal discountAmountWithTaxAmount,
		CommerceOrderItem commerceOrderItem) {

		if (discountAmount == null) {
			discountAmount = BigDecimal.ZERO;
		}

		if (discountAmountWithTaxAmount == null) {
			discountAmountWithTaxAmount = BigDecimal.ZERO;
		}

		int discountAmountCompareTo = 0;

		if (commerceOrderItem.getDiscountAmount() != null) {
			discountAmountCompareTo = discountAmount.compareTo(
				commerceOrderItem.getDiscountAmount());
		}

		int discountAmountWithTaxAmountCompareTo = 0;

		if (commerceOrderItem.getDiscountWithTaxAmount() != null) {
			discountAmountWithTaxAmountCompareTo =
				discountAmountWithTaxAmount.compareTo(
					commerceOrderItem.getDiscountWithTaxAmount());
		}

		if ((discountAmountCompareTo != 0) ||
			(discountAmountWithTaxAmountCompareTo != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isDiscountChanged(
		BigDecimal discountAmount, CommerceOrderItem commerceOrderItem) {

		if (discountAmount == null) {
			discountAmount = BigDecimal.ZERO;
		}

		int discountAmountCompareTo = 0;

		if (commerceOrderItem.getDiscountAmount() != null) {
			discountAmountCompareTo = discountAmount.compareTo(
				commerceOrderItem.getDiscountAmount());
		}

		if (discountAmountCompareTo != 0) {
			return true;
		}

		return false;
	}

	private boolean _isPriceChanged(
		BigDecimal finalPrice, BigDecimal finalPriceWithTaxAmount,
		BigDecimal promoPrice, BigDecimal promoPriceWithTaxAmount,
		BigDecimal unitPrice, BigDecimal unitPriceWithTaxAmount,
		CommerceOrderItem commerceOrderItem) {

		if (finalPrice == null) {
			finalPrice = BigDecimal.ZERO;
		}

		if (promoPrice == null) {
			promoPrice = BigDecimal.ZERO;
		}

		if (unitPrice == null) {
			unitPrice = BigDecimal.ZERO;
		}

		if (promoPriceWithTaxAmount == null) {
			promoPriceWithTaxAmount = BigDecimal.ZERO;
		}

		if (finalPriceWithTaxAmount == null) {
			finalPriceWithTaxAmount = BigDecimal.ZERO;
		}

		if (unitPriceWithTaxAmount == null) {
			unitPriceWithTaxAmount = BigDecimal.ZERO;
		}

		int finalPriceCompareTo = 0;

		if (commerceOrderItem.getFinalPrice() != null) {
			finalPriceCompareTo = finalPrice.compareTo(
				commerceOrderItem.getFinalPrice());
		}

		int promoPriceCompareTo = 0;

		if (commerceOrderItem.getPromoPrice() != null) {
			promoPriceCompareTo = promoPrice.compareTo(
				commerceOrderItem.getPromoPrice());
		}

		int unitPriceCompareTo = 0;

		if (commerceOrderItem.getUnitPrice() != null) {
			unitPriceCompareTo = unitPrice.compareTo(
				commerceOrderItem.getUnitPrice());
		}

		int finalPriceWithTaxAmountCompareTo = 0;

		if (commerceOrderItem.getFinalPriceWithTaxAmount() != null) {
			finalPriceWithTaxAmountCompareTo =
				finalPriceWithTaxAmount.compareTo(
					commerceOrderItem.getFinalPriceWithTaxAmount());
		}

		int promoPriceWithTaxAmountCompareTo = 0;

		if (commerceOrderItem.getPromoPriceWithTaxAmount() != null) {
			promoPriceWithTaxAmountCompareTo =
				promoPriceWithTaxAmount.compareTo(
					commerceOrderItem.getPromoPriceWithTaxAmount());
		}

		int unitPriceWithTaxAmountCompareTo = 0;

		if (commerceOrderItem.getUnitPriceWithTaxAmount() != null) {
			unitPriceWithTaxAmountCompareTo = unitPriceWithTaxAmount.compareTo(
				commerceOrderItem.getUnitPriceWithTaxAmount());
		}

		if ((finalPriceCompareTo != 0) || (promoPriceCompareTo != 0) ||
			(unitPriceCompareTo != 0) ||
			(finalPriceWithTaxAmountCompareTo != 0) ||
			(promoPriceWithTaxAmountCompareTo != 0) ||
			(unitPriceWithTaxAmountCompareTo != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isPriceChanged(
		BigDecimal finalPrice, BigDecimal promoPrice, BigDecimal unitPrice,
		CommerceOrderItem commerceOrderItem) {

		if (finalPrice == null) {
			finalPrice = BigDecimal.ZERO;
		}

		if (promoPrice == null) {
			promoPrice = BigDecimal.ZERO;
		}

		if (unitPrice == null) {
			unitPrice = BigDecimal.ZERO;
		}

		int finalPriceCompareTo = 0;

		if (commerceOrderItem.getFinalPrice() != null) {
			finalPriceCompareTo = finalPrice.compareTo(
				commerceOrderItem.getFinalPrice());
		}

		int promoPriceCompareTo = 0;

		if (commerceOrderItem.getPromoPrice() != null) {
			promoPriceCompareTo = promoPrice.compareTo(
				commerceOrderItem.getPromoPrice());
		}

		int unitPriceCompareTo = 0;

		if (commerceOrderItem.getUnitPrice() != null) {
			unitPriceCompareTo = unitPrice.compareTo(
				commerceOrderItem.getUnitPrice());
		}

		if ((finalPriceCompareTo != 0) || (promoPriceCompareTo != 0) ||
			(unitPriceCompareTo != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isPriceChanged(
		BigDecimal unitPrice, CommerceOrderItem commerceOrderItem) {

		if (unitPrice == null) {
			unitPrice = BigDecimal.ZERO;
		}

		int unitPriceCompareTo = 0;

		if (commerceOrderItem.getUnitPrice() != null) {
			unitPriceCompareTo = unitPrice.compareTo(
				commerceOrderItem.getUnitPrice());
		}

		if (unitPriceCompareTo != 0) {
			return true;
		}

		return false;
	}

	private boolean _isStaticPriceType(Object value) {
		return Objects.equals(
			value, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	private boolean _isSubscription(CPInstance cpInstance)
		throws PortalException {

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		if (cpDefinition.isSubscriptionEnabled() ||
			cpDefinition.isDeliverySubscriptionEnabled()) {

			return true;
		}

		if (cpInstance.isOverrideSubscriptionInfo() &&
			(cpInstance.isSubscriptionEnabled() ||
			 cpInstance.isDeliverySubscriptionEnabled())) {

			return true;
		}

		return false;
	}

	private boolean _jsonMatches(String json1, String json2) {
		if (Objects.equals(json1, json2)) {
			return true;
		}

		try {
			JSONArray jsonArray1 = _jsonFactory.createJSONArray(json1);
			JSONArray jsonArray2 = _jsonFactory.createJSONArray(json2);

			if (jsonArray1.length() != jsonArray2.length()) {
				return false;
			}

			for (int i = 0; i < jsonArray1.length(); i++) {
				JSONObject existingJSONObject = null;
				JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

				for (int j = 0; j < jsonArray2.length(); j++) {
					JSONObject jsonObject2 = jsonArray2.getJSONObject(j);

					if (jsonObject1.has("skuOptionKey") &&
						Objects.equals(
							jsonObject1.get("skuOptionKey"),
							jsonObject2.get("skuOptionKey"))) {

						existingJSONObject = jsonObject2;

						break;
					}
				}

				if (existingJSONObject == null) {
					return false;
				}

				Object value = jsonObject1.get("value");

				if (value instanceof JSONArray) {
					JSONArray valueJSONArray = (JSONArray)value;

					if (valueJSONArray.length() != 0) {
						return false;
					}
				}

				if ((jsonObject1.has("value") &&
					 !Objects.equals(existingJSONObject.get("value"), value)) ||
					(jsonObject1.has("skuOptionValueKey") &&
					 !Objects.equals(
						 existingJSONObject.get("skuOptionValueKey"),
						 jsonObject1.get("skuOptionValueKey")))) {

					return false;
				}
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return false;
		}

		return true;
	}

	private void _reindexCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId)
		throws PortalException {

		Indexer<CommerceInventoryBookedQuantity> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CommerceInventoryBookedQuantity.class);

		indexer.reindex(
			CommerceInventoryBookedQuantity.class.getName(),
			commerceInventoryBookedQuantityId);
	}

	private BaseModelSearchResult<CommerceOrderItem> _searchCommerceOrderItems(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceOrderItem> indexer =
			_indexerRegistry.nullSafeGetIndexer(CommerceOrderItem.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceOrderItem> commerceOrderItems = _getCommerceOrderItems(
				hits);

			if (commerceOrderItems != null) {
				return new BaseModelSearchResult<>(
					commerceOrderItems, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _setCommerceOrderItemDiscountValue(
		CommerceOrderItem commerceOrderItem,
		CommerceDiscountValue commerceDiscountValue, boolean includeTax) {

		BigDecimal discountAmount = BigDecimal.ZERO;
		BigDecimal discountPercentageLevel1 = BigDecimal.ZERO;
		BigDecimal discountPercentageLevel2 = BigDecimal.ZERO;
		BigDecimal discountPercentageLevel3 = BigDecimal.ZERO;
		BigDecimal discountPercentageLevel4 = BigDecimal.ZERO;

		if (commerceDiscountValue != null) {
			CommerceMoney discountAmountCommerceMoney =
				commerceDiscountValue.getDiscountAmount();

			discountAmount = discountAmountCommerceMoney.getPrice();

			BigDecimal[] percentages = commerceDiscountValue.getPercentages();

			if ((percentages.length >= 1) && (percentages[0] != null)) {
				discountPercentageLevel1 = percentages[0];
			}

			if ((percentages.length >= 2) && (percentages[1] != null)) {
				discountPercentageLevel2 = percentages[1];
			}

			if ((percentages.length >= 3) && (percentages[2] != null)) {
				discountPercentageLevel3 = percentages[2];
			}

			if ((percentages.length >= 4) && (percentages[3] != null)) {
				discountPercentageLevel4 = percentages[3];
			}
		}

		if (includeTax) {
			commerceOrderItem.setDiscountPercentageLevel1WithTaxAmount(
				discountPercentageLevel1);
			commerceOrderItem.setDiscountPercentageLevel2WithTaxAmount(
				discountPercentageLevel2);
			commerceOrderItem.setDiscountPercentageLevel3WithTaxAmount(
				discountPercentageLevel3);
			commerceOrderItem.setDiscountPercentageLevel4WithTaxAmount(
				discountPercentageLevel4);
			commerceOrderItem.setDiscountWithTaxAmount(discountAmount);
		}
		else {
			commerceOrderItem.setDiscountAmount(discountAmount);
			commerceOrderItem.setDiscountPercentageLevel1(
				discountPercentageLevel1);
			commerceOrderItem.setDiscountPercentageLevel2(
				discountPercentageLevel2);
			commerceOrderItem.setDiscountPercentageLevel3(
				discountPercentageLevel3);
			commerceOrderItem.setDiscountPercentageLevel4(
				discountPercentageLevel4);
		}
	}

	private void _setCommerceOrderItemPrice(
		CommerceOrderItem commerceOrderItem,
		CommerceProductPrice commerceProductPrice) {

		commerceOrderItem.setPriceOnApplication(
			commerceProductPrice.isPriceOnApplication());

		CommerceMoney unitPriceCommerceMoney =
			commerceProductPrice.getUnitPrice();
		CommerceMoney unitPromoPriceCommerceMoney =
			commerceProductPrice.getUnitPromoPrice();

		BigDecimal unitPrice = BigDecimal.ZERO;

		if (!unitPriceCommerceMoney.isEmpty()) {
			unitPrice = unitPriceCommerceMoney.getPrice();
		}

		commerceOrderItem.setUnitPrice(unitPrice);

		BigDecimal promoPrice = BigDecimal.ZERO;

		if (!unitPromoPriceCommerceMoney.isEmpty()) {
			promoPrice = unitPromoPriceCommerceMoney.getPrice();
		}

		commerceOrderItem.setPromoPrice(promoPrice);

		CommerceMoney unitPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getUnitPriceWithTaxAmount();
		CommerceMoney unitPromoPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getUnitPromoPriceWithTaxAmount();

		BigDecimal unitPriceWithTaxAmount = BigDecimal.ZERO;

		if ((unitPriceWithTaxAmountCommerceMoney != null) &&
			!unitPriceWithTaxAmountCommerceMoney.isEmpty()) {

			unitPriceWithTaxAmount =
				unitPriceWithTaxAmountCommerceMoney.getPrice();
		}

		commerceOrderItem.setUnitPriceWithTaxAmount(unitPriceWithTaxAmount);

		BigDecimal promoPriceWithTaxAmount = BigDecimal.ZERO;

		if ((unitPromoPriceWithTaxAmountCommerceMoney != null) &&
			!unitPromoPriceWithTaxAmountCommerceMoney.isEmpty()) {

			promoPriceWithTaxAmount =
				unitPromoPriceWithTaxAmountCommerceMoney.getPrice();
		}

		commerceOrderItem.setPromoPriceWithTaxAmount(promoPriceWithTaxAmount);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		commerceOrderItem.setFinalPrice(finalPriceCommerceMoney.getPrice());

		CommerceMoney finalPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		if (finalPriceWithTaxAmountCommerceMoney != null) {
			BigDecimal finalPriceWithTaxAmount = BigDecimal.ZERO;

			if (!finalPriceWithTaxAmountCommerceMoney.isEmpty()) {
				finalPriceWithTaxAmount =
					finalPriceWithTaxAmountCommerceMoney.getPrice();
			}

			commerceOrderItem.setFinalPriceWithTaxAmount(
				finalPriceWithTaxAmount);
		}

		commerceOrderItem.setCommercePriceListId(
			commerceProductPrice.getCommercePriceListId());
	}

	private void _setCommerceOrderItemPrice(
			CommerceOrderItem commerceOrderItem,
			CommerceProductPrice commerceProductPrice,
			CommerceContext commerceContext)
		throws PortalException {

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if ((cpInstance == null) || commerceOrderItem.isManuallyAdjusted()) {
			return;
		}

		if (commerceProductPrice == null) {
			commerceProductPrice = _getCommerceProductPrice(
				commerceOrderItem.getCommerceOrder(),
				commerceOrderItem.getCPDefinitionId(),
				commerceOrderItem.getCPInstanceId(),
				commerceOrderItem.getJson(), commerceOrderItem.getQuantity(),
				commerceOrderItem.getUnitOfMeasureKey(), commerceContext);
		}

		_setCommerceOrderItemPrice(commerceOrderItem, commerceProductPrice);

		_setCommerceOrderItemDiscountValue(
			commerceOrderItem, commerceProductPrice.getDiscountValue(), false);

		_setCommerceOrderItemDiscountValue(
			commerceOrderItem,
			commerceProductPrice.getDiscountValueWithTaxAmount(), true);
	}

	private void _setDimensions(
			CommerceOrderItem commerceOrderItem, CPInstance cpInstance)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpInstance.getCPDefinitionId());

		commerceOrderItem.setWidth(cpInstance.getWidth());

		if (commerceOrderItem.getWidth() <= 0) {
			commerceOrderItem.setWidth(cpDefinition.getWidth());
		}

		commerceOrderItem.setHeight(cpInstance.getHeight());

		if (commerceOrderItem.getHeight() <= 0) {
			commerceOrderItem.setHeight(cpDefinition.getHeight());
		}

		commerceOrderItem.setDepth(cpInstance.getDepth());

		if (commerceOrderItem.getDepth() <= 0) {
			commerceOrderItem.setDepth(cpDefinition.getDepth());
		}

		commerceOrderItem.setWeight(cpInstance.getWeight());

		if (commerceOrderItem.getWeight() <= 0) {
			commerceOrderItem.setWeight(cpDefinition.getWeight());
		}
	}

	private void _setSubscriptionInfo(
			CommerceOrderItem commerceOrderItem, CPInstance cpInstance)
		throws PortalException {

		if (cpInstance.isOverrideSubscriptionInfo()) {
			if (cpInstance.isDeliverySubscriptionEnabled()) {
				commerceOrderItem.setDeliveryMaxSubscriptionCycles(
					cpInstance.getDeliveryMaxSubscriptionCycles());
				commerceOrderItem.setDeliverySubscriptionLength(
					cpInstance.getDeliverySubscriptionLength());
				commerceOrderItem.setDeliverySubscriptionType(
					cpInstance.getDeliverySubscriptionType());
				commerceOrderItem.setDeliverySubscriptionTypeSettings(
					cpInstance.getDeliverySubscriptionTypeSettings());
			}

			if (cpInstance.isSubscriptionEnabled()) {
				commerceOrderItem.setMaxSubscriptionCycles(
					cpInstance.getMaxSubscriptionCycles());
				commerceOrderItem.setSubscriptionLength(
					cpInstance.getSubscriptionLength());
				commerceOrderItem.setSubscriptionType(
					cpInstance.getSubscriptionType());
				commerceOrderItem.setSubscriptionTypeSettings(
					cpInstance.getSubscriptionTypeSettings());
			}
		}
		else {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					cpInstance.getCPDefinitionId());

			if (cpDefinition.isDeliverySubscriptionEnabled()) {
				commerceOrderItem.setDeliveryMaxSubscriptionCycles(
					cpDefinition.getDeliveryMaxSubscriptionCycles());
				commerceOrderItem.setDeliverySubscriptionLength(
					cpDefinition.getDeliverySubscriptionLength());
				commerceOrderItem.setDeliverySubscriptionType(
					cpDefinition.getDeliverySubscriptionType());
				commerceOrderItem.setDeliverySubscriptionTypeSettings(
					cpDefinition.getDeliverySubscriptionTypeSettings());
			}

			if (cpDefinition.isSubscriptionEnabled()) {
				commerceOrderItem.setMaxSubscriptionCycles(
					cpDefinition.getMaxSubscriptionCycles());
				commerceOrderItem.setSubscriptionLength(
					cpDefinition.getSubscriptionLength());
				commerceOrderItem.setSubscriptionType(
					cpDefinition.getSubscriptionType());
				commerceOrderItem.setSubscriptionTypeSettings(
					cpDefinition.getSubscriptionTypeSettings());
			}
		}
	}

	private void _updateCommerceInventoryBookedQuantity(
			long userId, CommerceOrderItem commerceOrderItem,
			long commerceInventoryBookedQuantityId, BigDecimal quantity,
			BigDecimal oldQuantity)
		throws PortalException {

		if ((oldQuantity.compareTo(quantity) != 0) &&
			(commerceInventoryBookedQuantityId > 0)) {

			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
				_commerceInventoryBookedQuantityLocalService.
					fetchCommerceInventoryBookedQuantity(
						commerceInventoryBookedQuantityId);

			if (commerceInventoryBookedQuantity != null) {
				_commerceInventoryBookedQuantityLocalService.
					updateCommerceInventoryBookedQuantity(
						userId,
						commerceInventoryBookedQuantity.
							getCommerceInventoryBookedQuantityId(),
						quantity,
						HashMapBuilder.put(
							CommerceInventoryAuditTypeConstants.ORDER_ID,
							String.valueOf(
								commerceOrderItem.getCommerceOrderId())
						).put(
							CommerceInventoryAuditTypeConstants.ORDER_ITEM_ID,
							String.valueOf(
								commerceOrderItem.getCommerceOrderItemId())
						).build(),
						commerceInventoryBookedQuantity.getMvccVersion());
			}
		}
	}

	private CommerceOrderItem _updateCommerceOrderItem(
			CommerceOrderItem commerceOrderItem, String externalReferenceCode,
			User user, CommerceOrder commerceOrder, CPInstance cpInstance,
			String json, BigDecimal quantity, BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpInstance.getCPDefinitionId());

		_validate(
			serviceContext.getLocale(), commerceOrder, cpDefinition, cpInstance,
			json, quantity, unitOfMeasureKey,
			commerceOrderItem.hasParentCommerceOrderItem(),
			GetterUtil.getBoolean(
				serviceContext.getAttribute("validateOrder"), true));

		commerceOrderItem.setExternalReferenceCode(externalReferenceCode);
		commerceOrderItem.setGroupId(commerceOrder.getGroupId());
		commerceOrderItem.setCompanyId(user.getCompanyId());
		commerceOrderItem.setUserId(user.getUserId());
		commerceOrderItem.setUserName(user.getFullName());
		commerceOrderItem.setCommerceOrderId(
			commerceOrder.getCommerceOrderId());
		commerceOrderItem.setCPInstanceId(cpInstance.getCPInstanceId());
		commerceOrderItem.setCProductId(cpDefinition.getCProductId());
		commerceOrderItem.setFreeShipping(cpDefinition.isFreeShipping());
		commerceOrderItem.setJson(json);
		commerceOrderItem.setManuallyAdjusted(false);
		commerceOrderItem.setNameMap(cpDefinition.getNameMap());
		commerceOrderItem.setQuantity(quantity);
		commerceOrderItem.setShipSeparately(cpDefinition.isShipSeparately());
		commerceOrderItem.setShippable(cpDefinition.isShippable());
		commerceOrderItem.setShippedQuantity(shippedQuantity);
		commerceOrderItem.setShippingExtraPrice(
			cpDefinition.getShippingExtraPrice());
		commerceOrderItem.setSku(cpInstance.getSku());
		commerceOrderItem.setSubscription(_isSubscription(cpInstance));
		commerceOrderItem.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);
		commerceOrderItem.setUnitOfMeasureKey(unitOfMeasureKey);
		commerceOrderItem.setExpandoBridgeAttributes(serviceContext);

		_setDimensions(commerceOrderItem, cpInstance);
		_setSubscriptionInfo(commerceOrderItem, cpInstance);

		if (!ExportImportThreadLocal.isImportInProcess()) {
			CommerceProductPrice commerceProductPrice =
				_getCommerceProductPrice(
					commerceOrder, cpInstance.getCPDefinitionId(),
					cpInstance.getCPInstanceId(), json, quantity,
					unitOfMeasureKey, null);

			_setCommerceOrderItemPrice(commerceOrderItem, commerceProductPrice);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem, commerceProductPrice.getDiscountValue(),
				false);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem,
				commerceProductPrice.getDiscountValueWithTaxAmount(), true);
		}

		_validate(serviceContext.getLocale(), commerceOrderItem, true);

		return commerceOrderItem;
	}

	private CommerceOrderItem _updateCommerceOrderItem(
			long userId, long commerceOrderItemId, BigDecimal quantity,
			String json, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		_validate(
			serviceContext.getLocale(), commerceOrder,
			commerceOrderItem.getCPDefinition(),
			commerceOrderItem.fetchCPInstance(), json, quantity,
			commerceOrderItem.getUnitOfMeasureKey(),
			commerceOrderItem.hasParentCommerceOrderItem(),
			GetterUtil.getBoolean(
				serviceContext.getAttribute("validateOrder"), true));

		_updateCommerceInventoryBookedQuantity(
			userId, commerceOrderItem,
			commerceOrderItem.getCommerceInventoryBookedQuantityId(), quantity,
			commerceOrderItem.getQuantity());

		_updateWorkflow(userId, commerceOrder);

		commerceOrderItem.setJson(json);
		commerceOrderItem.setQuantity(quantity);
		commerceOrderItem.setExpandoBridgeAttributes(serviceContext);

		_validate(serviceContext.getLocale(), commerceOrderItem, true);

		return commerceOrderItemPersistence.update(commerceOrderItem);
	}

	private CommerceOrderItem _updateCommerceOrderItem(
			String externalReferenceCode, long userId, long commerceOrderItemId,
			BigDecimal quantity, String json,
			CommerceProductPrice commerceProductPrice,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		_validate(
			serviceContext.getLocale(), commerceOrder,
			commerceOrderItem.getCPDefinition(),
			commerceOrderItem.fetchCPInstance(), json, quantity,
			commerceOrderItem.getUnitOfMeasureKey(),
			commerceOrderItem.hasParentCommerceOrderItem(),
			GetterUtil.getBoolean(
				serviceContext.getAttribute("validateOrder"), true));

		_updateCommerceInventoryBookedQuantity(
			userId, commerceOrderItem,
			commerceOrderItem.getCommerceInventoryBookedQuantityId(), quantity,
			commerceOrderItem.getQuantity());

		commerceOrder = _updateWorkflow(userId, commerceOrder);

		commerceOrderItem.setExternalReferenceCode(externalReferenceCode);
		commerceOrderItem.setJson(json);
		commerceOrderItem.setQuantity(quantity);

		if (commerceOrder.isOpen()) {
			if (commerceProductPrice == null) {
				commerceProductPrice = _getCommerceProductPrice(
					commerceOrderItem.getCommerceOrder(),
					commerceOrderItem.getCPDefinitionId(),
					commerceOrderItem.getCPInstanceId(),
					commerceOrderItem.getJson(), quantity,
					commerceOrderItem.getUnitOfMeasureKey(), commerceContext);
			}

			_setCommerceOrderItemPrice(commerceOrderItem, commerceProductPrice);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem, commerceProductPrice.getDiscountValue(),
				false);

			_setCommerceOrderItemDiscountValue(
				commerceOrderItem,
				commerceProductPrice.getDiscountValueWithTaxAmount(), true);
		}

		commerceOrderItem.setExpandoBridgeAttributes(serviceContext);

		_validate(serviceContext.getLocale(), commerceOrderItem, true);

		commerceOrderItem = commerceOrderItemPersistence.update(
			commerceOrderItem);

		if (commerceOrder.isOpen()) {
			CommerceOrderLocalService commerceOrderLocalService =
				_commerceOrderLocalServiceSnapshot.get();

			commerceOrderLocalService.recalculatePrice(
				commerceOrderItem.getCommerceOrderId(), commerceContext);
		}

		return commerceOrderItem;
	}

	private CommerceOrder _updateWorkflow(
			long userId, CommerceOrder commerceOrder)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				commerceOrder.getCompanyId(), commerceOrder.getGroupId(),
				CommerceOrder.class.getName(), 0,
				CommerceOrderConstants.TYPE_PK_APPROVAL, true);

		if ((workflowDefinitionLink != null) && commerceOrder.isApproved()) {
			CommerceOrderLocalService commerceOrderLocalService =
				_commerceOrderLocalServiceSnapshot.get();

			return commerceOrderLocalService.updateStatus(
				userId, commerceOrder.getCommerceOrderId(),
				WorkflowConstants.STATUS_DRAFT, Collections.emptyMap());
		}

		return commerceOrder;
	}

	private void _validate(
			Locale locale, CommerceOrder commerceOrder,
			CPDefinition cpDefinition, CPInstance cpInstance, String json,
			BigDecimal quantity, String unitOfMeasureKey, boolean child,
			boolean validateOrder)
		throws PortalException {

		if (commerceOrder.getUserId() == 0) {
			int count = commerceOrderItemPersistence.countByCommerceOrderId(
				commerceOrder.getCommerceOrderId());

			if (count >=
					_commerceOrderConfiguration.guestCartItemMaxAllowed()) {

				throw new GuestCartItemMaxAllowedException();
			}
		}

		if ((cpDefinition != null) && (cpInstance != null) &&
			(cpDefinition.getCPDefinitionId() !=
				cpInstance.getCPDefinitionId())) {

			throw new NoSuchCPInstanceException(
				StringBundler.concat(
					"CPInstance ", cpInstance.getCPInstanceId(),
					" belongs to a different CPDefinition than ",
					cpDefinition.getCPDefinitionId()));
		}

		if (cpInstance != null) {
			if (Validator.isNotNull(unitOfMeasureKey)) {
				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_cpInstanceUnitOfMeasureLocalService.
						fetchCPInstanceUnitOfMeasure(
							cpInstance.getCPInstanceId(), unitOfMeasureKey);

				if (cpInstanceUnitOfMeasure == null) {
					throw new NoSuchCPInstanceUnitOfMeasureException(
						"No commerce product instance unit of measure exists " +
							"with the primary key " + unitOfMeasureKey);
				}
			}
			else {
				int cpInstanceUnitOfMeasuresCount =
					_cpInstanceUnitOfMeasureLocalService.
						getCPInstanceUnitOfMeasuresCount(
							cpInstance.getCPInstanceId());

				if (cpInstanceUnitOfMeasuresCount > 0) {
					throw new CommerceInventoryWarehouseItemUnitOfMeasureKeyException(
						"Unit of measure key is mandatory");
				}
			}
		}

		if (!ExportImportThreadLocal.isImportInProcess() && validateOrder) {
			List<CommerceOrderValidatorResult> commerceCartValidatorResults =
				_commerceOrderValidatorRegistry.validate(
					locale, commerceOrder, cpInstance, json, quantity, child);

			if (!commerceCartValidatorResults.isEmpty()) {
				throw new CommerceOrderValidatorException(
					commerceCartValidatorResults);
			}
		}
	}

	private void _validate(
			Locale locale, CommerceOrderItem commerceOrderItem,
			boolean validateOrder)
		throws PortalException {

		if (!ExportImportThreadLocal.isImportInProcess() && validateOrder) {
			List<CommerceOrderValidatorResult> commerceCartValidatorResults =
				_commerceOrderValidatorRegistry.validate(
					locale, commerceOrderItem);

			if (!commerceCartValidatorResults.isEmpty()) {
				throw new CommerceOrderValidatorException(
					commerceCartValidatorResults);
			}
		}
	}

	private void _validateParentCommerceOrderId(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (commerceOrderItem.getParentCommerceOrderItemId() != 0) {
			throw new ProductBundleException(
				StringBundler.concat(
					"Operation not allowed on an item ",
					commerceOrderItem.getCommerceOrderItemId(),
					" because it is a child commerce order item ",
					commerceOrderItem.getParentCommerceOrderItemId()));
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemLocalServiceImpl.class);

	private static final Snapshot<CommerceContextFactory>
		_commerceContextFactorySnapshot = new Snapshot<>(
			CommerceOrderItemLocalServiceImpl.class,
			CommerceContextFactory.class);
	private static final Snapshot<CommerceOrderLocalService>
		_commerceOrderLocalServiceSnapshot = new Snapshot<>(
			CommerceOrderItemLocalServiceImpl.class,
			CommerceOrderLocalService.class);

	@Reference
	private CommerceInventoryAuditTypeRegistry
		_commerceInventoryAuditTypeRegistry;

	@Reference
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@Reference
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Reference
	private CommerceMoneyFactory _commerceMoneyFactory;

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	private CommerceOrderConfiguration _commerceOrderConfiguration;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

	@Reference
	private CommerceTaxCalculation _commerceTaxCalculation;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}