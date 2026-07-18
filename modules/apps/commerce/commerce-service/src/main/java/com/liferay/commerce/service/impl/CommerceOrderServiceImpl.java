/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.internal.order.comparator.CommerceOrderModifiedDateComparator;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.base.CommerceOrderServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrder"
	},
	service = AopService.class
)
public class CommerceOrderServiceImpl extends CommerceOrderServiceBaseImpl {

	@Override
	public FileEntry addAttachmentFileEntry(
			String externalReferenceCode, long userId, long commerceOrderId,
			String fileName, InputStream inputStream)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.addAttachmentFileEntry(
			externalReferenceCode, userId, commerceOrderId, fileName,
			inputStream);
	}

	@Override
	public CommerceOrder addCommerceOrder(
			long groupId, long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId)
		throws PortalException {

		_checkPermissions(_getAccountEntry(commerceAccountId), groupId);

		return commerceOrderLocalService.addCommerceOrder(
			getUserId(), groupId, commerceAccountId, commerceCurrencyCode,
			commerceOrderTypeId);
	}

	@Override
	public CommerceOrder addOrUpdateCommerceOrder(
			String externalReferenceCode, long groupId, long billingAddressId,
			long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name, int orderDateMonth,
			int orderDateDay, int orderDateYear, int orderDateHour,
			int orderDateMinute, int orderStatus, int paymentStatus,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal shippingWithTaxAmount,
			BigDecimal subtotal, BigDecimal subtotalWithTaxAmount,
			BigDecimal taxAmount, BigDecimal total,
			BigDecimal totalWithTaxAmount, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.fetchByERC_C(
			externalReferenceCode, serviceContext.getCompanyId());

		if (commerceOrder == null) {
			_checkPermissions(_getAccountEntry(commerceAccountId), groupId);
		}
		else {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.UPDATE);
		}

		return commerceOrderLocalService.addOrUpdateCommerceOrder(
			externalReferenceCode, getUserId(), groupId, billingAddressId,
			commerceAccountId, commerceCurrencyCode, commerceOrderTypeId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, orderDateMonth, orderDateDay,
			orderDateYear, orderDateHour, orderDateMinute, orderStatus,
			paymentStatus, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrder addOrUpdateCommerceOrder(
			String externalReferenceCode, long groupId, long billingAddressId,
			long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name, int orderStatus,
			int paymentStatus, String purchaseOrderNumber,
			BigDecimal shippingAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalWithTaxAmount,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		return commerceOrderService.addOrUpdateCommerceOrder(
			externalReferenceCode, groupId, billingAddressId, commerceAccountId,
			commerceCurrencyCode, commerceOrderTypeId, commerceShippingMethodId,
			shippingAddressId, advanceStatus, commercePaymentMethodKey, name, 0,
			0, 0, 0, 0, orderStatus, paymentStatus, purchaseOrderNumber,
			shippingAmount, shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrder applyCouponCode(
			long commerceOrderId, String couponCode,
			CommerceContext commerceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.applyCouponCode(
			commerceOrderId, couponCode, commerceContext);
	}

	@Override
	public void deleteAttachmentFileEntry(
			long attachmentFileEntryId, long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		commerceOrderLocalService.deleteAttachmentFileEntry(
			attachmentFileEntryId, commerceOrderId);
	}

	@Override
	public void deleteCommerceOrder(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.DELETE);

		commerceOrderLocalService.deleteCommerceOrder(commerceOrderId);
	}

	@Override
	public CommerceOrder executeWorkflowTransition(
			long commerceOrderId, long workflowTaskId, String transitionName,
			String comment)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.executeWorkflowTransition(
			getUserId(), commerceOrderId, workflowTaskId, transitionName,
			comment);
	}

	@Override
	public CommerceOrder fetchCommerceOrder(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderPersistence.fetchByPrimaryKey(commerceOrderId);

		if (commerceOrder != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.VIEW);
		}

		return commerceOrder;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceOrder fetchCommerceOrder(
			long commerceAccountId, long groupId, int orderStatus)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderPersistence.fetchByG_C_O_First(
				groupId, commerceAccountId, orderStatus,
				CommerceOrderModifiedDateComparator.getInstance(false));

		if (commerceOrder != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.VIEW);
		}

		return commerceOrder;
	}

	@Override
	public CommerceOrder fetchCommerceOrder(
			long commerceAccountId, long groupId, long userId, int orderStatus)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderLocalService.fetchCommerceOrder(
				commerceAccountId, groupId, userId, orderStatus);

		if (commerceOrder != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.VIEW);
		}

		return commerceOrder;
	}

	@Override
	public CommerceOrder fetchCommerceOrder(String uuid, long groupId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.fetchByUUID_G(
			uuid, groupId);

		if (commerceOrder != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.VIEW);
		}

		return commerceOrder;
	}

	@Override
	public CommerceOrder fetchCommerceOrderByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceOrder != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrder, ActionKeys.VIEW);
		}

		return commerceOrder;
	}

	@Override
	public CommerceOrder getCommerceOrder(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrder, ActionKeys.VIEW);

		return commerceOrder;
	}

	@Override
	public CommerceOrder getCommerceOrderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByUUID_G(
			uuid, groupId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrder, ActionKeys.VIEW);

		return commerceOrder;
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long groupId, int start, int end,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderLocalService.getCommerceOrders(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long groupId, int[] orderStatuses)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderLocalService.getCommerceOrders(
			groupId, orderStatuses);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long groupId, int[] orderStatuses, int start, int end)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderLocalService.getCommerceOrders(
			groupId, orderStatuses, start, end);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long groupId, long commerceAccountId, int start, int end,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderPersistence.findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceOrdersCount(long groupId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCommerceOrdersCount(long groupId, long commerceAccountId)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		return commerceOrderPersistence.countByG_C(groupId, commerceAccountId);
	}

	@Override
	public List<CommerceOrder> getPendingCommerceOrders(
			long groupId, long commerceAccountId, String keywords, int start,
			int end)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.VIEW_OPEN_COMMERCE_ORDERS);

		Group group = _groupLocalService.getGroup(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			group.getCompanyId(), groupId, new long[] {commerceAccountId},
			keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
			false, start, end);
	}

	@Override
	public long getPendingCommerceOrdersCount(long companyId, long groupId)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, StringPool.BLANK,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, false);
	}

	@Override
	public int getPendingCommerceOrdersCount(
			long groupId, long commerceAccountId, String keywords)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.VIEW_OPEN_COMMERCE_ORDERS);

		Group group = _groupLocalService.getGroup(groupId);

		return (int)commerceOrderLocalService.getCommerceOrdersCount(
			group.getCompanyId(), groupId, new long[] {commerceAccountId},
			keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
			false);
	}

	@Override
	public List<CommerceOrder> getPlacedCommerceOrders(
			long companyId, long groupId, int start, int end)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, StringPool.BLANK,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, true, start,
			end);
	}

	@Override
	public List<CommerceOrder> getPlacedCommerceOrders(
			long groupId, long commerceAccountId, String keywords, int start,
			int end)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS);

		Group group = _groupLocalService.getGroup(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			group.getCompanyId(), groupId, new long[] {commerceAccountId},
			keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
			true, start, end);
	}

	@Override
	public long getPlacedCommerceOrdersCount(long companyId, long groupId)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, StringPool.BLANK,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, true);
	}

	@Override
	public int getPlacedCommerceOrdersCount(
			long groupId, long commerceAccountId, String keywords)
		throws PortalException {

		_checkAccountOrder(
			groupId, commerceAccountId,
			CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS);

		Group group = _groupLocalService.getGroup(groupId);

		return (int)commerceOrderLocalService.getCommerceOrdersCount(
			group.getCompanyId(), groupId, new long[] {commerceAccountId},
			keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
			true);
	}

	@Override
	public List<CommerceOrder> getUserCommerceOrders(
			long companyId, long groupId, String keywords, int start, int end)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_ANY}, true, start,
			end);
	}

	@Override
	public long getUserCommerceOrdersCount(
			long companyId, long groupId, String keywords)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_ANY}, true);
	}

	@Override
	public List<CommerceOrder> getUserOpenCommerceOrders(
			long companyId, long groupId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, false, start,
			end, sort);
	}

	@Override
	public List<CommerceOrder> getUserPendingCommerceOrders(
			long companyId, long groupId, String keywords, int start, int end)
		throws PortalException {

		return commerceOrderService.getUserOpenCommerceOrders(
			companyId, groupId, keywords, start, end, null);
	}

	@Override
	public long getUserPendingCommerceOrdersCount(
			long companyId, long groupId, String keywords)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, false);
	}

	@Override
	public List<CommerceOrder> getUserPlacedCommerceOrders(
			long companyId, long groupId, String keywords, int start, int end)
		throws PortalException {

		return getUserPlacedCommerceOrders(
			companyId, groupId, keywords, start, end, null);
	}

	@Override
	public List<CommerceOrder> getUserPlacedCommerceOrders(
			long companyId, long groupId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, true, start,
			end, sort);
	}

	@Override
	public long getUserPlacedCommerceOrdersCount(
			long companyId, long groupId, String keywords)
		throws PortalException {

		long[] commerceAccountIds = _getCommerceAccountIds(groupId);

		return commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, keywords,
			new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, true);
	}

	@Override
	public void mergeGuestCommerceOrder(
			long guestCommerceOrderId, long userCommerceOrderId,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), guestCommerceOrderId, ActionKeys.VIEW);
		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), userCommerceOrderId, ActionKeys.UPDATE);

		commerceOrderLocalService.mergeGuestCommerceOrder(
			getUserId(), guestCommerceOrderId, userCommerceOrderId,
			commerceContext, serviceContext);
	}

	@Override
	public CommerceOrder recalculatePrice(
			long commerceOrderId, CommerceContext commerceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.recalculatePrice(
			commerceOrderId, commerceContext);
	}

	@Override
	public CommerceOrder reorderCommerceOrder(
			long commerceOrderId, CommerceContext commerceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderLocalService.reorderCommerceOrder(
			getUserId(), commerceOrderId, commerceContext);
	}

	@Override
	public CommerceOrder resetCommerceOrderAddresses(
			long commerceOrderId, boolean billingAddress,
			boolean shippingAddress)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.resetCommerceOrderAddresses(
			commerceOrderId, billingAddress, shippingAddress);
	}

	@Override
	public CommerceOrder resetTermsAndConditions(
			long commerceOrderId, boolean deliveryCommerceTermEntry,
			boolean paymentCommerceTermEntry)
		throws PortalException {

		if (deliveryCommerceTermEntry) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_DELIVERY_TERMS);
		}

		if (paymentCommerceTermEntry) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS);
		}

		return commerceOrderLocalService.resetTermsAndConditions(
			commerceOrderId, deliveryCommerceTermEntry,
			paymentCommerceTermEntry);
	}

	@Override
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long billingAddressId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateBillingAddress(
			commerceOrderId, billingAddressId);
	}

	@Override
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String street1, String street2,
			String street3, String subtype, String phoneNumber, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateBillingAddress(
			commerceOrderId, countryId, regionId, city, description, name,
			phoneNumber, street1, street2, street3, subtype, zip,
			serviceContext);
	}

	@Override
	public CommerceOrder updateCommerceOrder(CommerceOrder commerceOrder)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrder.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	@Override
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal subtotal, BigDecimal total)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, subtotal, total);
	}

	@Override
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal shippingWithTaxAmount,
			BigDecimal subtotal, BigDecimal subtotalWithTaxAmount,
			BigDecimal taxAmount, BigDecimal total,
			BigDecimal totalDiscountAmount, BigDecimal totalWithTaxAmount)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			totalDiscountAmount);
	}

	@Override
	public CommerceOrder updateCommerceOrderExternalReferenceCode(
			String externalReferenceCode, long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.
			updateCommerceOrderExternalReferenceCode(
				externalReferenceCode, commerceOrderId);
	}

	@Override
	public CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, BigDecimal shippingAmount,
			BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4, BigDecimal subtotal,
			BigDecimal subtotalDiscountAmount,
			BigDecimal subtotalDiscountPercentageLevel1,
			BigDecimal subtotalDiscountPercentageLevel2,
			BigDecimal subtotalDiscountPercentageLevel3,
			BigDecimal subtotalDiscountPercentageLevel4, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalDiscountAmount,
			BigDecimal totalDiscountPercentageLevel1,
			BigDecimal totalDiscountPercentageLevel2,
			BigDecimal totalDiscountPercentageLevel3,
			BigDecimal totalDiscountPercentageLevel4)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderLocalService.updateCommerceOrderPrices(
			commerceOrderId, shippingAmount, shippingDiscountAmount,
			shippingDiscountPercentageLevel1, shippingDiscountPercentageLevel2,
			shippingDiscountPercentageLevel3, shippingDiscountPercentageLevel4,
			subtotal, subtotalDiscountAmount, subtotalDiscountPercentageLevel1,
			subtotalDiscountPercentageLevel2, subtotalDiscountPercentageLevel3,
			subtotalDiscountPercentageLevel4, taxAmount, total,
			totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4);
	}

	@Override
	public CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, BigDecimal shippingAmount,
			BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4,
			BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			BigDecimal shippingDiscountWithTaxAmount,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalDiscountAmount,
			BigDecimal subtotalDiscountPercentageLevel1,
			BigDecimal subtotalDiscountPercentageLevel2,
			BigDecimal subtotalDiscountPercentageLevel3,
			BigDecimal subtotalDiscountPercentageLevel4,
			BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal subtotalDiscountWithTaxAmount,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalDiscountAmount,
			BigDecimal totalDiscountPercentageLevel1,
			BigDecimal totalDiscountPercentageLevel2,
			BigDecimal totalDiscountPercentageLevel3,
			BigDecimal totalDiscountPercentageLevel4,
			BigDecimal totalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal totalDiscountWithTaxAmount,
			BigDecimal totalWithTaxAmount)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		_portletResourcePermission.check(
			getPermissionChecker(), commerceOrder.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES);

		return commerceOrderLocalService.updateCommerceOrderPrices(
			commerceOrderId, shippingAmount, shippingDiscountAmount,
			shippingDiscountPercentageLevel1, shippingDiscountPercentageLevel2,
			shippingDiscountPercentageLevel3, shippingDiscountPercentageLevel4,
			shippingDiscountPercentageLevel1WithTaxAmount,
			shippingDiscountPercentageLevel2WithTaxAmount,
			shippingDiscountPercentageLevel3WithTaxAmount,
			shippingDiscountPercentageLevel4WithTaxAmount,
			shippingDiscountWithTaxAmount, shippingWithTaxAmount, subtotal,
			subtotalDiscountAmount, subtotalDiscountPercentageLevel1,
			subtotalDiscountPercentageLevel2, subtotalDiscountPercentageLevel3,
			subtotalDiscountPercentageLevel4,
			subtotalDiscountPercentageLevel1WithTaxAmount,
			subtotalDiscountPercentageLevel2WithTaxAmount,
			subtotalDiscountPercentageLevel3WithTaxAmount,
			subtotalDiscountPercentageLevel4WithTaxAmount,
			subtotalDiscountWithTaxAmount, subtotalWithTaxAmount, taxAmount,
			total, totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4,
			totalDiscountPercentageLevel1WithTaxAmount,
			totalDiscountPercentageLevel2WithTaxAmount,
			totalDiscountPercentageLevel3WithTaxAmount,
			totalDiscountPercentageLevel4WithTaxAmount,
			totalDiscountWithTaxAmount, totalWithTaxAmount);
	}

	@Override
	public CommerceOrder updateCommercePaymentMethodKey(
			long commerceOrderId, String commercePaymentMethodKey)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId,
			CommerceOrderActionKeys.CHECKOUT_COMMERCE_ORDER);

		return commerceOrderLocalService.updateCommercePaymentMethodKey(
			commerceOrderId, commercePaymentMethodKey);
	}

	@Override
	public CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName, CommerceContext commerceContext,
			Locale locale)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateCommerceShippingMethod(
			commerceOrderId, commerceShippingMethodId,
			commerceShippingOptionName, commerceContext, locale);
	}

	@Override
	public CommerceOrder updateInfo(
			long commerceOrderId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateInfo(
			commerceOrderId, printedNote, requestedDeliveryDateMonth,
			requestedDeliveryDateDay, requestedDeliveryDateYear,
			requestedDeliveryDateHour, requestedDeliveryDateMinute,
			serviceContext);
	}

	@Override
	public CommerceOrder updateOrderDate(
			long commerceOrderId, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateOrderDate(
			commerceOrderId, orderDateMonth, orderDateDay, orderDateYear,
			orderDateHour, orderDateMinute, serviceContext);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement.
	 * See CommercePaymentEngine.updateOrderPaymentStatus.
	 */
	@Deprecated
	@Override
	public CommerceOrder updatePaymentStatus(
			long commerceOrderId, int paymentStatus)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement.
	 * See CommercePaymentEngine.updateOrderPaymentStatus.
	 */
	@Deprecated
	@Override
	public CommerceOrder updatePaymentStatusAndTransactionId(
			long commerceOrderId, int paymentStatus, String transactionId)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CommerceOrder updatePrintedNote(
			long commerceOrderId, String printedNote)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updatePrintedNote(
			commerceOrderId, printedNote);
	}

	@Override
	public CommerceOrder updatePurchaseOrderNumber(
			long commerceOrderId, String purchaseOrderNumber)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updatePurchaseOrderNumber(
			commerceOrderId, purchaseOrderNumber);
	}

	@Override
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long shippingAddressId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateShippingAddress(
			commerceOrderId, shippingAddressId);
	}

	@Override
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.UPDATE);

		return commerceOrderLocalService.updateShippingAddress(
			commerceOrderId, countryId, regionId, city, description, name,
			phoneNumber, street1, street2, street3, subtype, zip,
			serviceContext);
	}

	@Override
	public CommerceOrder updateTermsAndConditions(
			long commerceOrderId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, String languageId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		if (deliveryCommerceTermEntryId > 0) {
			_portletResourcePermission.check(
				getPermissionChecker(), commerceOrder.getGroupId(),
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_DELIVERY_TERMS);
		}

		if (paymentCommerceTermEntryId > 0) {
			_portletResourcePermission.check(
				getPermissionChecker(), commerceOrder.getGroupId(),
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS);
		}

		return commerceOrderLocalService.updateTermsAndConditions(
			commerceOrderId, deliveryCommerceTermEntryId,
			paymentCommerceTermEntryId, languageId);
	}

	private void _checkAccountOrder(
			long groupId, long accountEntryId, String action)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		if (accountEntry == null) {
			_portletResourcePermission.check(
				getPermissionChecker(), groupId, action);
		}
		else if (accountEntry.isBusinessAccount()) {
			_portletResourcePermission.check(
				getPermissionChecker(), accountEntry.getAccountEntryGroup(),
				action);
		}
	}

	private void _checkPermissions(AccountEntry accountEntry, long groupId)
		throws PortalException {

		if (accountEntry.isBusinessAccount()) {
			_portletResourcePermission.check(
				getPermissionChecker(), accountEntry.getAccountEntryGroupId(),
				CommerceOrderActionKeys.ADD_COMMERCE_ORDER);

			return;
		}

		if (accountEntry.isGuestAccount() ||
			_portletResourcePermission.contains(
				getPermissionChecker(), groupId,
				CommerceOrderActionKeys.MANAGE_ALL_ACCOUNTS)) {

			return;
		}

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntry.getAccountEntryId(),
			ActionKeys.UPDATE);
	}

	private AccountEntry _getAccountEntry(long accountEntryId)
		throws PortalException {

		User user = getUser();

		if ((user == null) || user.isGuestUser()) {
			return _accountEntryLocalService.getGuestAccountEntry(
				user.getCompanyId());
		}

		return _accountEntryLocalService.getAccountEntry(accountEntryId);
	}

	private long[] _getCommerceAccountIds(long groupId) throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				getPermissionChecker(), groupId,
				CommerceOrderActionKeys.MANAGE_ALL_ACCOUNTS)) {

			return null;
		}

		return _commerceAccountHelper.getUserCommerceAccountIds(
			getUserId(), groupId);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}