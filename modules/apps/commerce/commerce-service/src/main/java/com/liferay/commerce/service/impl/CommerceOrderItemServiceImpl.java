/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.base.CommerceOrderItemServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Igor Beslic
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrderItem"
	},
	service = AopService.class
)
public class CommerceOrderItemServiceImpl
	extends CommerceOrderItemServiceBaseImpl {

	@Override
	public CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderItemLocalService.addCommerceOrderItem(
			getUserId(), commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrderItem addOrUpdateCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(),
			_commerceOrderService.getCommerceOrder(commerceOrderId),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
			getUserId(), commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	@Override
	public int countSubscriptionCommerceOrderItems(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.
			countSubscriptionCommerceOrderItems(commerceOrderId);
	}

	@Override
	public void deleteCommerceOrderItem(long commerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		commerceOrderItemLocalService.deleteCommerceOrderItem(
			getUserId(), commerceOrderItem);
	}

	@Override
	public void deleteCommerceOrderItem(
			long commerceOrderItemId, CommerceContext commerceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		commerceOrderItemLocalService.deleteCommerceOrderItem(
			getUserId(), commerceOrderItem, commerceContext);
	}

	@Override
	public void deleteCommerceOrderItems(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		commerceOrderItemLocalService.deleteCommerceOrderItems(
			getUserId(), commerceOrderId);
	}

	@Override
	public void deleteMissingCommerceOrderItems(
			long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		commerceOrderItemLocalService.deleteMissingCommerceOrderItems(
			getUserId(), commerceOrderId, commerceOrderItemIds,
			externalReferenceCodes);
	}

	@Override
	public CommerceOrderItem fetchCommerceOrderItem(long commerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.fetchCommerceOrderItem(
				commerceOrderItemId);

		if (commerceOrderItem != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceOrderItem;
	}

	@Override
	public CommerceOrderItem fetchCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceOrderItem != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceOrderItem;
	}

	@Override
	public List<CommerceOrderItem> getAvailableForShipmentCommerceOrderItems(
			long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.
			getAvailableForShipmentCommerceOrderItems(commerceOrderId);
	}

	@Override
	public List<CommerceOrderItem> getChildCommerceOrderItems(
			long parentCommerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.fetchCommerceOrderItem(
				parentCommerceOrderItemId);

		if (commerceOrderItem != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);

			return commerceOrderItemLocalService.getChildCommerceOrderItems(
				parentCommerceOrderItemId);
		}

		return Collections.emptyList();
	}

	@Override
	public BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.VIEW);

		return commerceOrderItemLocalService.
			getCommerceInventoryWarehouseItemQuantity(
				commerceOrderItemId, commerceInventoryWarehouseId);
	}

	@Override
	public CommerceOrderItem getCommerceOrderItem(long commerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.VIEW);

		return commerceOrderItem;
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
			long commerceOrderId, int start, int end)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItems(
			commerceOrderId, start, end);
	}

	@Override
	public List<CommerceOrderItem> getCommerceOrderItems(
			long groupId, long commerceAccountId, int[] orderStatuses,
			int start, int end)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), commerceAccountId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItems(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	@Override
	public int getCommerceOrderItemsCount(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItemsCount(
			commerceOrderId);
	}

	@Override
	public int getCommerceOrderItemsCount(
			long commerceOrderId, long cpInstanceId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItemsCount(
			commerceOrderId, cpInstanceId);
	}

	@Override
	public int getCommerceOrderItemsCount(
			long groupId, long commerceAccountId, int[] orderStatuses)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), commerceAccountId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItemsCount(
			groupId, commerceAccountId, orderStatuses);
	}

	@Override
	public BigDecimal getCommerceOrderItemsQuantity(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getCommerceOrderItemsQuantity(
			commerceOrderId);
	}

	@Override
	public List<CommerceOrderItem> getParentCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId, int start,
			int end, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getParentCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	@Override
	public int getParentCommerceOrderItemsCount(
			long commerceOrderId, long parentCommerceOrderItemId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.getParentCommerceOrderItemsCount(
			commerceOrderId, parentCommerceOrderItemId);
	}

	@Override
	public List<CommerceOrderItem> getSupplierCommerceOrderItems(
			long customerCommerceOrderItemId, int start, int end)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.fetchCommerceOrderItem(
				customerCommerceOrderItemId);

		if (commerceOrderItem != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);

			return commerceOrderItemLocalService.getSupplierCommerceOrderItems(
				customerCommerceOrderItemId, start, end);
		}

		return Collections.emptyList();
	}

	@Override
	public CommerceOrderItem importCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json, BigDecimal quantity,
			BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderItemLocalService.importCommerceOrderItem(
			getUserId(), externalReferenceCode, commerceOrderItemId,
			commerceOrderId, cpInstanceId, cpMeasurementUnitKey, json, quantity,
			shippedQuantity, unitOfMeasureIncrementalOrderQuantity,
			unitOfMeasureKey, serviceContext);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId,
			String keywords, int start, int end, Sort sort)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.searchCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, keywords, start, end,
			sort);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.searchCommerceOrderItems(
			commerceOrderId, keywords, start, end, sort);
	}

	@Override
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String name, String sku, boolean andOperator,
			int start, int end, Sort sort)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderItemLocalService.searchCommerceOrderItems(
			commerceOrderId, name, sku, andOperator, start, end, sort);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, BigDecimal quantity,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			getUserId(), commerceOrderItemId, quantity, commerceContext,
			serviceContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			getUserId(), commerceOrderItemId, cpMeasurementUnitId, quantity,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			getUserId(), commerceOrderItemId, cpMeasurementUnitId, quantity,
			serviceContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId, String json,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItem(
			externalReferenceCode, getUserId(),
			commerceOrderItem.getCommerceOrderItemId(), json, quantity,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItemDeliveryDate(
			long commerceOrderItemId, Date requestedDeliveryDate)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.
			updateCommerceOrderItemDeliveryDate(
				commerceOrderItemId, requestedDeliveryDate);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);
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

		return commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);
	}

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
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrderItem.getCommerceOrderId());

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItemId, discountAmount, discountPercentageLevel1,
			discountPercentageLevel2, discountPercentageLevel3,
			discountPercentageLevel4, finalPrice, promoPrice, unitPrice);
	}

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
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrderItem.getCommerceOrderId());

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderItemLocalService.updateCommerceOrderItemPrices(
			commerceOrderItemId, discountAmount, discountAmountWithTaxAmount,
			discountPercentageLevel1, discountPercentageLevel1WithTaxAmount,
			discountPercentageLevel2, discountPercentageLevel2WithTaxAmount,
			discountPercentageLevel3, discountPercentageLevel3WithTaxAmount,
			discountPercentageLevel4, discountPercentageLevel4WithTaxAmount,
			finalPrice, finalPriceWithTaxAmount, promoPrice,
			promoPriceWithTaxAmount, unitPrice, unitPriceWithTaxAmount);
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
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrderItem.getCommerceOrderId());

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderItemLocalService.updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, unitPrice);
	}

	@Override
	public CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, BigDecimal quantity, BigDecimal unitPrice)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrderItem.getCommerceOrderId());

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderItemLocalService.updateCommerceOrderItemUnitPrice(
			getUserId(), commerceOrderItemId, quantity, unitPrice);
	}

	@Override
	public CommerceOrderItem updateCustomFields(
			long commerceOrderItemId, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateCustomFields(
			commerceOrderItemId, serviceContext);
	}

	@Override
	public CommerceOrderItem updateExternalReferenceCode(
			long commerceOrderItemId, String externalReferenceCode)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderItemLocalService.updateExternalReferenceCode(
			commerceOrderItemId, externalReferenceCode);
	}

	@Reference
	protected CommerceProductViewPermission commerceProductViewPermission;

	@Reference
	protected CPInstanceLocalService cpInstanceLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}