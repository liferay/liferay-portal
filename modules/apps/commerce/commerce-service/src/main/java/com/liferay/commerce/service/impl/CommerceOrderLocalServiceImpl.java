/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderConfiguration;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.exception.CommerceDiscountCouponCodeException;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountUsageEntryLocalService;
import com.liferay.commerce.discount.validator.helper.CommerceDiscountValidatorHelper;
import com.liferay.commerce.exception.CommerceOrderAccountLimitException;
import com.liferay.commerce.exception.CommerceOrderDateException;
import com.liferay.commerce.exception.CommerceOrderPurchaseOrderNumberException;
import com.liferay.commerce.exception.CommerceOrderRequestedDeliveryDateException;
import com.liferay.commerce.exception.GuestCartMaxAllowedException;
import com.liferay.commerce.internal.order.comparator.CommerceOrderModifiedDateComparator;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.model.attributes.provider.CommerceModelAttributesProvider;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierLocalService;
import com.liferay.commerce.payment.util.comparator.CommercePaymentMethodPriorityComparator;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.exception.NoSuchChannelAccountEntryRelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderNoteLocalService;
import com.liferay.commerce.service.CommerceOrderPaymentLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelService;
import com.liferay.commerce.service.base.CommerceOrderLocalServiceBaseImpl;
import com.liferay.commerce.service.persistence.CommerceOrderItemPersistence;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceOrderThreadLocal;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.util.comparator.CommerceShippingMethodPriorityComparator;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Marco Leo
 */
@Component(
	configurationPid = "com.liferay.commerce.configuration.CommerceOrderConfiguration",
	property = "model.class.name=com.liferay.commerce.model.CommerceOrder",
	service = AopService.class
)
public class CommerceOrderLocalServiceImpl
	extends CommerceOrderLocalServiceBaseImpl {

	@Override
	public FileEntry addAttachmentFileEntry(
			String externalReferenceCode, long userId, long commerceOrderId,
			String fileName, InputStream inputStream)
		throws PortalException {

		if (Validator.isNull(fileName)) {
			return null;
		}

		File file = null;

		try {
			CommerceOrder commerceOrder =
				commerceOrderLocalService.getCommerceOrder(commerceOrderId);

			DLAppHelperThreadLocal.setEnabled(false);

			LocalRepository localRepository =
				commerceOrder.getLocalRepository();

			Folder folder = commerceOrder.getFolder(localRepository);

			if (folder == null) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);
				serviceContext.setCompanyId(commerceOrder.getCompanyId());
				serviceContext.setUserId(commerceOrder.getUserId());

				folder = localRepository.addFolder(
					"order-" + commerceOrderId, commerceOrder.getUserId(), 0,
					String.valueOf(commerceOrderId), StringPool.BLANK,
					serviceContext);
			}

			file = FileUtil.createTempFile(inputStream);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setAttribute(
				"className", CommerceOrder.class.getName());
			serviceContext.setAttribute(
				"classPK", String.valueOf(commerceOrderId));
			serviceContext.setIndexingEnabled(false);

			return localRepository.addFileEntry(
				externalReferenceCode, userId, folder.getFolderId(), fileName,
				MimeTypesUtil.getContentType(file, fileName), fileName,
				fileName, StringPool.BLANK, StringPool.BLANK, file, null, null,
				null, serviceContext);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(true);
			FileUtil.delete(file);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder addCommerceOrder(
			long userId, long groupId, long billingAddressId,
			long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId, long commerceShippingMethodId,
			long shippingAddressId, String commercePaymentMethodKey,
			String name, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			int orderStatus, int paymentStatus, String purchaseOrderNumber,
			BigDecimal shippingAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalWithTaxAmount,
			ServiceContext serviceContext)
		throws PortalException {

		// Check guest user

		if (userId == 0) {
			Group group = _groupLocalService.getGroup(groupId);

			User guestUser = _userLocalService.getGuestUser(
				group.getCompanyId());

			userId = guestUser.getUserId();
		}

		serviceContext.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		// Check approval workflow

		if (_hasWorkflowDefinition(
				groupId, CommerceOrderConstants.TYPE_PK_APPROVAL)) {

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		serviceContext.setScopeGroupId(groupId);

		// Commerce order

		_validateAccountLimit(groupId, commerceAccountId);
		_validateCommerceChannelAccount(groupId, commerceAccountId);
		_validateGuestOrders();

		if (Validator.isNull(commerceCurrencyCode)) {
			CommerceCurrency commerceCurrency =
				_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
					serviceContext.getCompanyId());

			if (commerceCurrency != null) {
				commerceCurrencyCode = commerceCurrency.getCode();
			}
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(groupId);

		if (commerceOrderTypeId <= 0) {
			List<CommerceOrderType> commerceOrderTypes =
				_commerceOrderTypeLocalService.getCommerceOrderTypes(
					user.getCompanyId(), CommerceChannel.class.getName(),
					commerceChannel.getCommerceChannelId(), true, 0, 1);

			if (!commerceOrderTypes.isEmpty()) {
				CommerceOrderType commerceOrderType = commerceOrderTypes.get(0);

				commerceOrderTypeId =
					commerceOrderType.getCommerceOrderTypeId();
			}
		}

		long commerceOrderId = counterLocalService.increment();

		CommerceOrder commerceOrder = commerceOrderPersistence.create(
			commerceOrderId);

		commerceOrder.setGroupId(groupId);
		commerceOrder.setCompanyId(user.getCompanyId());
		commerceOrder.setUserId(userId);
		commerceOrder.setUserName(user.getFullName());
		commerceOrder.setCommerceAccountId(commerceAccountId);
		commerceOrder.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceOrder.setCommerceOrderTypeId(commerceOrderTypeId);
		commerceOrder.setCommerceShippingMethodId(commerceShippingMethodId);

		if (billingAddressId > 0) {
			commerceOrder.setBillingAddressId(billingAddressId);
		}
		else {
			_setAccountDefaultBillingAddress(commerceChannel, commerceOrder);
		}

		if (shippingAddressId > 0) {
			commerceOrder.setShippingAddressId(shippingAddressId);
		}
		else {
			_setAccountDefaultShippingAddress(commerceChannel, commerceOrder);
		}

		if (Validator.isNotNull(commercePaymentMethodKey)) {
			commerceOrder.setCommercePaymentMethodKey(commercePaymentMethodKey);
		}
		else {
			_setAccountDefaultPaymentIntegrationKey(
				commerceChannel, commerceOrder);
		}

		_setAccountDefaultPaymentTerm(commerceChannel, commerceOrder, user);

		commerceOrder.setName(name);
		commerceOrder.setPurchaseOrderNumber(purchaseOrderNumber);
		commerceOrder.setShippingOptionName(shippingOptionName);

		_setCommerceOrderPrices(
			commerceOrder, shippingAmount, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount);

		_setCommerceOrderShippingDiscountValue(commerceOrder, null, true);
		_setCommerceOrderShippingDiscountValue(commerceOrder, null, false);
		_setCommerceOrderSubtotalDiscountValue(commerceOrder, null, true);
		_setCommerceOrderSubtotalDiscountValue(commerceOrder, null, false);
		_setCommerceOrderTotalDiscountValue(commerceOrder, null, true);
		_setCommerceOrderTotalDiscountValue(commerceOrder, null, false);

		commerceOrder.setManuallyAdjusted(false);

		Date orderDate = _portal.getDate(
			orderDateMonth, orderDateDay, orderDateYear, orderDateHour,
			orderDateMinute, user.getTimeZone(), null);

		if (orderDate != null) {
			commerceOrder.setOrderDate(orderDate);
		}
		else {
			commerceOrder.setOrderDate(new Date());
		}

		commerceOrder.setOrderStatus(orderStatus);
		commerceOrder.setPaymentStatus(paymentStatus);
		commerceOrder.setStatus(WorkflowConstants.STATUS_DRAFT);
		commerceOrder.setStatusByUserId(user.getUserId());
		commerceOrder.setStatusByUserName(user.getFullName());
		commerceOrder.setStatusDate(serviceContext.getModifiedDate(null));
		commerceOrder.setExpandoBridgeAttributes(serviceContext);

		commerceOrder = commerceOrderPersistence.update(commerceOrder);

		// Repository

		LocalRepository localRepository = commerceOrder.getLocalRepository();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		localRepository.addFolder(
			"order-" + commerceOrderId, user.getUserId(), 0,
			String.valueOf(commerceOrderId), StringPool.BLANK, serviceContext);

		// Workflow

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commerceOrder.getCompanyId(), commerceOrder.getScopeGroupId(),
			userId, CommerceOrder.class.getName(),
			commerceOrder.getCommerceOrderId(), commerceOrder, serviceContext,
			new HashMap<>());
	}

	@Override
	public CommerceOrder addCommerceOrder(
			long userId, long groupId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId)
		throws PortalException {

		return commerceOrderLocalService.addCommerceOrder(
			userId, groupId, 0, commerceAccountId, commerceCurrencyCode,
			commerceOrderTypeId, 0, 0, null, null, 0, 0, 0, 0, 0,
			CommerceOrderConstants.ORDER_STATUS_OPEN,
			CommerceOrderPaymentConstants.STATUS_PENDING, null, BigDecimal.ZERO,
			null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			new ServiceContext());
	}

	@Override
	public CommerceOrder addOrUpdateCommerceOrder(
			String externalReferenceCode, long userId, long groupId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey, String name,
			int orderDateMonth, int orderDateDay, int orderDateYear,
			int orderDateHour, int orderDateMinute, int orderStatus,
			int paymentStatus, String purchaseOrderNumber,
			BigDecimal shippingAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalWithTaxAmount,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		// Update

		CommerceOrder commerceOrder = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			commerceOrder = commerceOrderPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (commerceOrder != null) {
			commerceOrder = commerceOrderLocalService.updateCommerceOrder(
				externalReferenceCode, commerceOrder.getCommerceOrderId(),
				billingAddressId, commerceShippingMethodId, shippingAddressId,
				advanceStatus, commercePaymentMethodKey, null,
				purchaseOrderNumber, shippingAmount, shippingOptionName,
				shippingWithTaxAmount, subtotal, subtotalWithTaxAmount,
				taxAmount, total, commerceOrder.getTotalDiscountAmount(),
				totalWithTaxAmount);

			commerceOrder = commerceOrderLocalService.updatePaymentStatus(
				userId, commerceOrder.getCommerceOrderId(), paymentStatus);

			User user = _userLocalService.getUser(serviceContext.getUserId());

			Date orderDate = _portal.getDate(
				orderDateMonth, orderDateDay, orderDateYear, orderDateHour,
				orderDateMinute, user.getTimeZone(), null);

			if (orderDate != null) {
				commerceOrder.setOrderDate(orderDate);
			}

			commerceOrder.setOrderStatus(orderStatus);

			return commerceOrderPersistence.update(commerceOrder);
		}

		// Add

		commerceOrder = commerceOrderLocalService.addCommerceOrder(
			userId, groupId, billingAddressId, commerceAccountId,
			commerceCurrencyCode, commerceOrderTypeId, commerceShippingMethodId,
			shippingAddressId, commercePaymentMethodKey, name, orderDateMonth,
			orderDateDay, orderDateYear, orderDateHour, orderDateMinute,
			orderStatus, paymentStatus, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			serviceContext);

		commerceOrder.setExternalReferenceCode(externalReferenceCode);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public CommerceOrder applyCouponCode(
			long commerceOrderId, String couponCode,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		boolean hasDiscounts = false;

		int count =
			_commerceDiscountLocalService.getActiveCommerceDiscountsCount(
				commerceOrder.getCompanyId(), couponCode, true);

		if (count == 0) {
			hasDiscounts = true;
		}

		if (hasDiscounts && Validator.isNotNull(couponCode)) {
			throw new CommerceDiscountCouponCodeException();
		}

		if (Validator.isNotNull(couponCode)) {
			CommerceDiscount commerceDiscount =
				_commerceDiscountLocalService.getActiveCommerceDiscount(
					commerceOrder.getCompanyId(), couponCode, true);

			_commerceDiscountValidatorHelper.checkValid(
				commerceContext, commerceDiscount);

			if (!_commerceDiscountUsageEntryLocalService.
					validateDiscountLimitationUsage(
						CommerceUtil.getCommerceAccountId(commerceContext),
						commerceDiscount.getCommerceDiscountId())) {

				throw new CommerceDiscountLimitationTimesException();
			}
		}

		commerceOrder.setCouponCode(couponCode);

		commerceOrderPersistence.update(commerceOrder);

		return commerceOrderLocalService.recalculatePrice(
			commerceOrderId, commerceContext);
	}

	@Override
	public void deleteAttachmentFileEntry(
			long attachmentFileEntryId, long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		LocalRepository localRepository = commerceOrder.getLocalRepository();

		localRepository.deleteFileEntry(attachmentFileEntryId);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceOrder deleteCommerceOrder(CommerceOrder commerceOrder)
		throws PortalException {

		boolean deleteInProcess = CommerceOrderThreadLocal.isDeleteInProcess();

		try {
			CommerceOrderThreadLocal.setDeleteInProcess(true);

			// Commerce order items

			_commerceOrderItemLocalService.deleteCommerceOrderItems(
				_getUserId(commerceOrder), commerceOrder.getCommerceOrderId());

			// Commerce order notes

			_commerceOrderNoteLocalService.deleteCommerceOrderNotes(
				commerceOrder.getCommerceOrderId());

			// Commerce order payments

			_commerceOrderPaymentLocalService.deleteCommerceOrderPayments(
				commerceOrder.getCommerceOrderId());

			// Commerce addresses

			_commerceAddressLocalService.deleteCommerceAddresses(
				commerceOrder.getModelClassName(),
				commerceOrder.getCommerceOrderId());

			// Commerce order

			commerceOrderPersistence.remove(commerceOrder);

			// Expando

			_expandoRowLocalService.deleteRows(
				commerceOrder.getCommerceOrderId());

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				commerceOrder.getCompanyId(), commerceOrder.getScopeGroupId(),
				CommerceOrder.class.getName(),
				commerceOrder.getCommerceOrderId());

			return commerceOrder;
		}
		finally {
			CommerceOrderThreadLocal.setDeleteInProcess(deleteInProcess);
		}
	}

	@Override
	public CommerceOrder deleteCommerceOrder(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		return commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
	}

	@Override
	public void deleteCommerceOrders(long groupId) throws PortalException {
		List<CommerceOrder> commerceOrders =
			commerceOrderPersistence.findByGroupId(groupId);

		for (CommerceOrder commerceOrder : commerceOrders) {
			commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), delete by commerceAccountId
	 */
	@Deprecated
	@Override
	public void deleteCommerceOrders(long userId, Date date, int status) {
		commerceOrderPersistence.removeByU_LtC_O(userId, date, status);
	}

	@Override
	public void deleteCommerceOrdersByAccountId(
			long commerceAccountId, Date date, int status)
		throws PortalException {

		List<CommerceOrder> commerceOrderList =
			commerceOrderPersistence.findByC_LtC_O(
				date, commerceAccountId, status, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CommerceOrder commerceOrder : commerceOrderList) {
			commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	@Override
	public CommerceOrder executeWorkflowTransition(
			long userId, long commerceOrderId, long workflowTaskId,
			String transitionName, String comment)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		long companyId = commerceOrder.getCompanyId();

		WorkflowTask workflowTask = _workflowTaskManager.getWorkflowTask(
			workflowTaskId);

		if (!workflowTask.isAssignedToSingleUser()) {
			workflowTask = _workflowTaskManager.assignWorkflowTaskToUser(
				companyId, userId, workflowTask.getWorkflowTaskId(), userId,
				comment, null, null);
		}

		_workflowTaskManager.completeWorkflowTask(
			companyId, userId, workflowTask.getWorkflowTaskId(), transitionName,
			comment, null, true);

		return commerceOrder;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, int orderStatus) {

		return commerceOrderPersistence.fetchByG_C_O_First(
			groupId, commerceAccountId, orderStatus,
			CommerceOrderModifiedDateComparator.getInstance(false));
	}

	@Override
	public CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, long userId, int orderStatus) {

		return commerceOrderFinder.fetchByG_U_C_O_S_First(
			groupId, userId, commerceAccountId, orderStatus);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return commerceOrderPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses) {

		return commerceOrderFinder.findByG_O(groupId, orderStatuses);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses, int start, int end) {

		return commerceOrderFinder.findByG_O(
			groupId, orderStatuses, start, end);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return commerceOrderPersistence.findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, commerceAccountIds, keywords,
			excludeOrderStatus, orderStatuses, start, end, null);

		BaseModelSearchResult<CommerceOrder> baseModelSearchResult =
			commerceOrderLocalService.searchCommerceOrders(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, commerceAccountIds, keywords,
			excludeOrderStatus, orderStatuses, start, end, sort);

		BaseModelSearchResult<CommerceOrder> baseModelSearchResult =
			commerceOrderLocalService.searchCommerceOrders(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public List<CommerceOrder> getCommerceOrders(
		long groupId, String commercePaymentMethodKey) {

		return commerceOrderPersistence.findByG_CP(
			groupId, commercePaymentMethodKey);
	}

	@Override
	public List<CommerceOrder> getCommerceOrdersByBillingAddress(
		long billingAddressId) {

		return commerceOrderPersistence.findByBillingAddressId(
			billingAddressId);
	}

	@Override
	public List<CommerceOrder> getCommerceOrdersByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return commerceOrderPersistence.findByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrder> getCommerceOrdersByShippingAddress(
		long shippingAddressId) {

		return commerceOrderPersistence.findByShippingAddressId(
			shippingAddressId);
	}

	@Override
	public int getCommerceOrdersCount(long groupId) {
		return commerceOrderPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCommerceOrdersCount(long groupId, long commerceAccountId) {
		return commerceOrderPersistence.countByG_C(groupId, commerceAccountId);
	}

	@Override
	public long getCommerceOrdersCount(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupId, commerceAccountIds, keywords,
			excludeOrderStatus, orderStatuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return commerceOrderLocalService.searchCommerceOrdersCount(
			searchContext);
	}

	@Override
	public int getCommerceOrdersCountByCommerceAccountId(
		long commerceAccountId) {

		return commerceOrderPersistence.countByCommerceAccountId(
			commerceAccountId);
	}

	@Override
	public List<CommerceOrder> getShippedCommerceOrdersByCommerceShipmentId(
		long commerceShipmentId, int start, int end) {

		return commerceOrderFinder.getShippedCommerceOrdersByCommerceShipmentId(
			commerceShipmentId, start, end);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public List<CommerceOrder> getUserCommerceOrders(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords, int start, int end) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			return commerceOrderLocalService.getCommerceOrders(
				group.getCompanyId(), groupId, new long[] {commerceAccountId},
				keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
				false, start, end);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return Collections.emptyList();
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public int getUserCommerceOrdersCount(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			return (int)commerceOrderLocalService.getCommerceOrdersCount(
				group.getCompanyId(), groupId, new long[] {commerceAccountId},
				keywords, new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
				false);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return 0;
	}

	@Override
	public void mergeGuestCommerceOrder(
			long userId, long guestCommerceOrderId, long userCommerceOrderId,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException {

		List<CommerceOrderItem> guestCommerceOrderItems =
			_commerceOrderItemPersistence.findByCommerceOrderId(
				guestCommerceOrderId);

		for (CommerceOrderItem guestCommerceOrderItem :
				guestCommerceOrderItems) {

			List<CommerceOrderItem> userCommerceOrderItems =
				_commerceOrderItemPersistence.findByC_CPI(
					userCommerceOrderId,
					guestCommerceOrderItem.getCPInstanceId());

			if (!userCommerceOrderItems.isEmpty()) {
				boolean found = false;

				for (CommerceOrderItem userCommerceOrderItem :
						userCommerceOrderItems) {

					if (Objects.equals(
							guestCommerceOrderItem.getJson(),
							userCommerceOrderItem.getJson())) {

						found = true;

						break;
					}
				}

				if (found) {
					continue;
				}
			}

			_commerceOrderItemLocalService.addCommerceOrderItem(
				userId, userCommerceOrderId,
				guestCommerceOrderItem.getCPInstanceId(),
				guestCommerceOrderItem.getJson(),
				guestCommerceOrderItem.getQuantity(),
				guestCommerceOrderItem.getReplacedCPInstanceId(),
				guestCommerceOrderItem.getShippedQuantity(),
				guestCommerceOrderItem.getUnitOfMeasureKey(), commerceContext,
				serviceContext);
		}

		commerceOrderLocalService.deleteCommerceOrder(guestCommerceOrderId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder recalculatePrice(
			long commerceOrderId, CommerceContext commerceContext)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		if ((commerceOrder.getOrderStatus() !=
				CommerceOrderConstants.ORDER_STATUS_OPEN) ||
			commerceOrder.isManuallyAdjusted()) {

			return commerceOrder;
		}

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			_commerceOrderItemLocalService.updateCommerceOrderItemPrice(
				commerceOrderItem.getCommerceOrderItemId(), commerceContext);
		}

		commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, false, commerceContext);

		CommerceMoney subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
		CommerceMoney shippingValueCommerceMoney =
			commerceOrderPrice.getShippingValue();
		CommerceMoney taxValueCommerceMoney = commerceOrderPrice.getTaxValue();
		CommerceMoney totalCommerceMoney = commerceOrderPrice.getTotal();
		CommerceMoney subtotalWithTaxAmountCommerceMoney =
			commerceOrderPrice.getSubtotalWithTaxAmount();
		CommerceMoney shippingValueWithTaxAmountCommerceMoney =
			commerceOrderPrice.getShippingValueWithTaxAmount();
		CommerceMoney totalWithTaxAmountCommerceMoney =
			commerceOrderPrice.getTotalWithTaxAmount();

		commerceOrder.setShippingAmount(shippingValueCommerceMoney.getPrice());
		commerceOrder.setSubtotal(subtotalCommerceMoney.getPrice());
		commerceOrder.setTaxAmount(taxValueCommerceMoney.getPrice());
		commerceOrder.setTotal(totalCommerceMoney.getPrice());

		if (subtotalWithTaxAmountCommerceMoney != null) {
			commerceOrder.setSubtotalWithTaxAmount(
				subtotalWithTaxAmountCommerceMoney.getPrice());
		}

		if (shippingValueWithTaxAmountCommerceMoney != null) {
			commerceOrder.setShippingWithTaxAmount(
				shippingValueWithTaxAmountCommerceMoney.getPrice());
		}

		if (totalWithTaxAmountCommerceMoney != null) {
			commerceOrder.setTotalWithTaxAmount(
				totalWithTaxAmountCommerceMoney.getPrice());
		}

		_setCommerceOrderSubtotalDiscountValue(
			commerceOrder, commerceOrderPrice.getSubtotalDiscountValue(),
			false);
		_setCommerceOrderShippingDiscountValue(
			commerceOrder, commerceOrderPrice.getShippingDiscountValue(),
			false);
		_setCommerceOrderTotalDiscountValue(
			commerceOrder, commerceOrderPrice.getTotalDiscountValue(), false);
		_setCommerceOrderSubtotalDiscountValue(
			commerceOrder,
			commerceOrderPrice.getSubtotalDiscountValueWithTaxAmount(), true);
		_setCommerceOrderShippingDiscountValue(
			commerceOrder,
			commerceOrderPrice.getShippingDiscountValueWithTaxAmount(), true);
		_setCommerceOrderTotalDiscountValue(
			commerceOrder,
			commerceOrderPrice.getTotalDiscountValueWithTaxAmount(), true);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public CommerceOrder reorderCommerceOrder(
			long userId, long commerceOrderId, CommerceContext commerceContext)
		throws PortalException {

		// Commerce order

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(commerceOrder.getGroupId());
		serviceContext.setUserId(userId);

		long billingAddressId = 0;
		long shippingAddressId = 0;

		CommerceAddress billingAddress = _getNewCommerceAddress(
			"billing", commerceOrder, commerceOrder.getBillingAddress(),
			serviceContext);

		CommerceAddress shippingAddress = billingAddress;

		if (commerceOrder.getBillingAddressId() !=
				commerceOrder.getShippingAddressId()) {

			shippingAddress = _getNewCommerceAddress(
				"shipping", commerceOrder, commerceOrder.getShippingAddress(),
				serviceContext);
		}

		if (billingAddress != null) {
			billingAddressId = billingAddress.getCommerceAddressId();
		}

		if (shippingAddress != null) {
			shippingAddressId = shippingAddress.getCommerceAddressId();
		}

		String commerceCurrencyCode = commerceOrder.getCommerceCurrencyCode();
		boolean recalculate = false;

		CommerceCurrency commerceContextCommerceCurrency =
			commerceContext.getCommerceCurrency();

		if ((commerceContextCommerceCurrency != null) &&
			!Objects.equals(
				commerceContextCommerceCurrency.getCode(),
				commerceCurrencyCode)) {

			commerceCurrencyCode = commerceContextCommerceCurrency.getCode();
			recalculate = true;
		}

		CommerceOrder newCommerceOrder =
			commerceOrderLocalService.addCommerceOrder(
				userId, commerceOrder.getGroupId(), billingAddressId,
				commerceOrder.getCommerceAccountId(), commerceCurrencyCode,
				commerceOrder.getCommerceOrderTypeId(),
				commerceOrder.getCommerceShippingMethodId(), shippingAddressId,
				commerceOrder.getCommercePaymentMethodKey(),
				commerceOrder.getName(), 0, 0, 0, 0, 0,
				CommerceOrderConstants.ORDER_STATUS_OPEN,
				CommerceOrderPaymentConstants.STATUS_PENDING, StringPool.BLANK,
				commerceOrder.getShippingAmount(),
				commerceOrder.getShippingOptionName(),
				commerceOrder.getShippingWithTaxAmount(),
				commerceOrder.getSubtotal(),
				commerceOrder.getSubtotalWithTaxAmount(),
				commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
				commerceOrder.getTotalWithTaxAmount(), serviceContext);

		// Commerce order items

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemLocalService.getCommerceOrderItems(
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (commerceOrderItem.getParentCommerceOrderItemId() != 0) {
				continue;
			}

			_commerceOrderItemLocalService.addCommerceOrderItem(
				userId, newCommerceOrder.getCommerceOrderId(),
				commerceOrderItem.getCPInstanceId(),
				commerceOrderItem.getJson(), commerceOrderItem.getQuantity(),
				commerceOrderItem.getReplacedCPInstanceId(), BigDecimal.ZERO,
				commerceOrderItem.getUnitOfMeasureKey(), commerceContext,
				serviceContext);
		}

		if (recalculate) {
			newCommerceOrder =
				commerceOrderLocalService.updateCommerceShippingMethod(
					newCommerceOrder.getCommerceOrderId(),
					commerceOrder.getCommerceShippingMethodId(),
					commerceOrder.getShippingOptionName(), commerceContext,
					serviceContext.getLocale());

			newCommerceOrder = commerceOrderLocalService.recalculatePrice(
				newCommerceOrder.getCommerceOrderId(), commerceContext);
		}

		return newCommerceOrder;
	}

	@Override
	public CommerceOrder resetCommerceOrderAddresses(
			long commerceOrderId, boolean billingAddress,
			boolean shippingAddress)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		if (billingAddress) {
			commerceOrder.setBillingAddressId(0);
		}

		if (shippingAddress) {
			commerceOrder.setShippingAddressId(0);
		}

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder resetCommerceOrderShipping(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		commerceOrder.setCommerceShippingMethodId(0);
		commerceOrder.setShippingAmount(BigDecimal.ZERO);
		commerceOrder.setShippingOptionName(null);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public void resetCommerceOrderShippingByAddressId(long addressId)
		throws PortalException {

		List<CommerceOrder> commerceOrders =
			commerceOrderLocalService.getCommerceOrdersByShippingAddress(
				addressId);

		for (CommerceOrder commerceOrder : commerceOrders) {
			if (!commerceOrder.isDraft() && !commerceOrder.isOpen()) {
				continue;
			}

			commerceOrderLocalService.resetCommerceOrderShipping(
				commerceOrder.getCommerceOrderId());
		}
	}

	@Override
	public CommerceOrder resetTermsAndConditions(
			long commerceOrderId, boolean resetDeliveryCommerceTerm,
			boolean resetPaymentCommerceTermEntry)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		if (resetDeliveryCommerceTerm) {
			commerceOrder.setDeliveryCommerceTermEntryId(0);
			commerceOrder.setDeliveryCommerceTermEntryDescription(null);
			commerceOrder.setDeliveryCommerceTermEntryName(null);
		}

		if (resetPaymentCommerceTermEntry) {
			commerceOrder.setPaymentCommerceTermEntryId(0);
			commerceOrder.setPaymentCommerceTermEntryDescription(null);
			commerceOrder.setPaymentCommerceTermEntryName(null);
		}

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public BaseModelSearchResult<CommerceOrder> searchCommerceOrders(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceOrder> indexer = _indexerRegistry.nullSafeGetIndexer(
			CommerceOrder.class.getName());

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<CommerceOrder> commerceOrders = _getCommerceOrders(hits);

			if (commerceOrders != null) {
				return new BaseModelSearchResult<>(
					commerceOrders, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Override
	public long searchCommerceOrdersCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceOrder> indexer = _indexerRegistry.nullSafeGetIndexer(
			CommerceOrder.class.getName());

		return indexer.searchCount(searchContext);
	}

	@Override
	public CommerceOrder updateAccount(
			long commerceOrderId, long userId, long commerceAccountId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		User user = _userLocalService.getUser(userId);

		commerceOrder.setUserId(user.getUserId());
		commerceOrder.setUserName(user.getFullName());

		commerceOrder.setCommerceAccountId(commerceAccountId);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long billingAddressId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setBillingAddressId(billingAddressId);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		return _updateAddress(
			city, CommerceOrder::getBillingAddressId,
			CommerceOrder::setBillingAddressId, commerceOrderId, countryId,
			description, name, phoneNumber, regionId, street1, street2, street3,
			serviceContext, subtype, zip);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceOrder(CommerceOrder commerceOrder) {
		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					commerceOrder.getGroupId());

			User user = _userLocalService.getUser(commerceOrder.getUserId());

			_setAccountDefaultBillingAddress(commerceChannel, commerceOrder);
			_setAccountDefaultShippingAddress(commerceChannel, commerceOrder);
			_setAccountDefaultPaymentIntegrationKey(
				commerceChannel, commerceOrder);
			_setAccountDefaultShippingOption(
				commerceChannel, commerceOrder, user);
			_setAccountDefaultDeliveryTerm(
				commerceChannel, commerceOrder, user);
			_setAccountDefaultPaymentTerm(commerceChannel, commerceOrder, user);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return super.updateCommerceOrder(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceOrder(
			long userId, String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey,
			String couponCode, String deliveryCommerceTermEntryDescription,
			String deliveryCommerceTermEntryName, Date lastPriceUpdateDate,
			boolean manuallyAdjusted, String name, Date orderDate,
			int orderStatus, String paymentCommerceTermEntryDescription,
			String paymentCommerceTermEntryName, int paymentStatus,
			String printedNote, String purchaseOrderNumber,
			Date requestedDeliveryDate, boolean shippable,
			BigDecimal shippingAmount, BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4,
			BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			BigDecimal shippingDiscountWithTaxAmount, String shippingOptionName,
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
			BigDecimal totalWithTaxAmount, String transactionId, int status,
			long statusByUserId, String statusByUserName, Date statusDate,
			boolean recalculate, CommerceContext commerceContext)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		long currentUserId = commerceOrder.getUserId();

		if (currentUserId != userId) {
			User user = _userLocalService.getUser(userId);

			commerceOrder.setUserId(user.getUserId());
			commerceOrder.setUserName(user.getFullName());
		}

		commerceOrder.setExternalReferenceCode(externalReferenceCode);
		commerceOrder.setBillingAddressId(billingAddressId);
		commerceOrder.setCommerceAccountId(commerceAccountId);
		commerceOrder.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceOrder.setCommerceOrderTypeId(commerceOrderTypeId);
		commerceOrder.setCommerceShippingMethodId(commerceShippingMethodId);
		commerceOrder.setDeliveryCommerceTermEntryId(
			deliveryCommerceTermEntryId);
		commerceOrder.setPaymentCommerceTermEntryId(paymentCommerceTermEntryId);
		commerceOrder.setShippingAddressId(shippingAddressId);
		commerceOrder.setAdvanceStatus(advanceStatus);

		commerceOrder.setCommercePaymentMethodKey(commercePaymentMethodKey);
		commerceOrder.setCouponCode(couponCode);
		commerceOrder.setDeliveryCommerceTermEntryDescription(
			deliveryCommerceTermEntryDescription);
		commerceOrder.setDeliveryCommerceTermEntryName(
			deliveryCommerceTermEntryName);
		commerceOrder.setLastPriceUpdateDate(lastPriceUpdateDate);
		commerceOrder.setManuallyAdjusted(manuallyAdjusted);

		if (name != null) {
			commerceOrder.setName(name);
		}

		commerceOrder.setOrderDate(orderDate);
		commerceOrder.setOrderStatus(orderStatus);
		commerceOrder.setPaymentCommerceTermEntryDescription(
			paymentCommerceTermEntryDescription);
		commerceOrder.setPaymentCommerceTermEntryName(
			paymentCommerceTermEntryName);
		commerceOrder.setPaymentStatus(paymentStatus);
		commerceOrder.setPrintedNote(printedNote);
		commerceOrder.setPurchaseOrderNumber(purchaseOrderNumber);
		commerceOrder.setRequestedDeliveryDate(requestedDeliveryDate);
		commerceOrder.setShippable(shippable);

		if (shippingAmount == null) {
			shippingAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingAmount(shippingAmount);

		if (shippingDiscountAmount == null) {
			shippingDiscountAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountAmount(shippingDiscountAmount);

		if (shippingDiscountPercentageLevel1 == null) {
			shippingDiscountPercentageLevel1 = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel1(
			shippingDiscountPercentageLevel1);

		if (shippingDiscountPercentageLevel2 == null) {
			shippingDiscountPercentageLevel2 = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel2(
			shippingDiscountPercentageLevel2);

		if (shippingDiscountPercentageLevel3 == null) {
			shippingDiscountPercentageLevel3 = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel3(
			shippingDiscountPercentageLevel3);

		if (shippingDiscountPercentageLevel4 == null) {
			shippingDiscountPercentageLevel4 = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel4(
			shippingDiscountPercentageLevel4);

		if (shippingDiscountPercentageLevel1WithTaxAmount == null) {
			shippingDiscountPercentageLevel1WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
			shippingDiscountPercentageLevel1WithTaxAmount);

		if (shippingDiscountPercentageLevel2WithTaxAmount == null) {
			shippingDiscountPercentageLevel2WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
			shippingDiscountPercentageLevel2WithTaxAmount);

		if (shippingDiscountPercentageLevel3WithTaxAmount == null) {
			shippingDiscountPercentageLevel3WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
			shippingDiscountPercentageLevel3WithTaxAmount);

		if (shippingDiscountPercentageLevel4WithTaxAmount == null) {
			shippingDiscountPercentageLevel4WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
			shippingDiscountPercentageLevel4WithTaxAmount);

		if (shippingDiscountWithTaxAmount == null) {
			shippingDiscountWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingDiscountWithTaxAmount(
			shippingDiscountWithTaxAmount);
		commerceOrder.setShippingOptionName(shippingOptionName);

		if (shippingWithTaxAmount == null) {
			shippingWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingWithTaxAmount(shippingWithTaxAmount);

		if (subtotal == null) {
			subtotal = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotal(subtotal);

		if (subtotalDiscountAmount == null) {
			subtotalDiscountAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountAmount(subtotalDiscountAmount);

		if (subtotalDiscountPercentageLevel1 == null) {
			subtotalDiscountPercentageLevel1 = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel1(
			subtotalDiscountPercentageLevel1);

		if (subtotalDiscountPercentageLevel2 == null) {
			subtotalDiscountPercentageLevel2 = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel2(
			subtotalDiscountPercentageLevel2);

		if (subtotalDiscountPercentageLevel3 == null) {
			subtotalDiscountPercentageLevel3 = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel3(
			subtotalDiscountPercentageLevel3);

		if (subtotalDiscountPercentageLevel4 == null) {
			subtotalDiscountPercentageLevel4 = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel4(
			subtotalDiscountPercentageLevel4);

		if (subtotalDiscountPercentageLevel1WithTaxAmount == null) {
			subtotalDiscountPercentageLevel1WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
			subtotalDiscountPercentageLevel1WithTaxAmount);

		if (subtotalDiscountPercentageLevel2WithTaxAmount == null) {
			subtotalDiscountPercentageLevel2WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
			subtotalDiscountPercentageLevel2WithTaxAmount);

		if (subtotalDiscountPercentageLevel3WithTaxAmount == null) {
			subtotalDiscountPercentageLevel3WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
			subtotalDiscountPercentageLevel3WithTaxAmount);

		if (subtotalDiscountPercentageLevel4WithTaxAmount == null) {
			subtotalDiscountPercentageLevel4WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
			subtotalDiscountPercentageLevel4WithTaxAmount);

		if (subtotalDiscountWithTaxAmount == null) {
			subtotalDiscountWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalDiscountWithTaxAmount(
			subtotalDiscountWithTaxAmount);

		if (subtotalWithTaxAmount == null) {
			subtotalWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setSubtotalWithTaxAmount(subtotalWithTaxAmount);

		if (taxAmount == null) {
			taxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTaxAmount(taxAmount);

		if (total == null) {
			total = BigDecimal.ZERO;
		}

		commerceOrder.setTotal(total);

		if (totalDiscountAmount == null) {
			totalDiscountAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountAmount(totalDiscountAmount);

		if (totalDiscountPercentageLevel1 == null) {
			totalDiscountPercentageLevel1 = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel1(
			totalDiscountPercentageLevel1);

		if (totalDiscountPercentageLevel2 == null) {
			totalDiscountPercentageLevel2 = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel2(
			totalDiscountPercentageLevel2);

		if (totalDiscountPercentageLevel3 == null) {
			totalDiscountPercentageLevel3 = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel3(
			totalDiscountPercentageLevel3);

		if (totalDiscountPercentageLevel4 == null) {
			totalDiscountPercentageLevel4 = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel4(
			totalDiscountPercentageLevel4);

		if (totalDiscountPercentageLevel1WithTaxAmount == null) {
			totalDiscountPercentageLevel1WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
			totalDiscountPercentageLevel1WithTaxAmount);

		if (totalDiscountPercentageLevel2WithTaxAmount == null) {
			totalDiscountPercentageLevel2WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
			totalDiscountPercentageLevel2WithTaxAmount);

		if (totalDiscountPercentageLevel3WithTaxAmount == null) {
			totalDiscountPercentageLevel3WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
			totalDiscountPercentageLevel3WithTaxAmount);

		if (totalDiscountPercentageLevel4WithTaxAmount == null) {
			totalDiscountPercentageLevel4WithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
			totalDiscountPercentageLevel4WithTaxAmount);

		if (totalDiscountWithTaxAmount == null) {
			totalDiscountWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountWithTaxAmount(totalDiscountWithTaxAmount);

		if (totalWithTaxAmount == null) {
			totalWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalWithTaxAmount(totalWithTaxAmount);
		commerceOrder.setTransactionId(transactionId);
		commerceOrder.setStatus(status);
		commerceOrder.setStatusByUserId(statusByUserId);
		commerceOrder.setStatusByUserName(statusByUserName);
		commerceOrder.setStatusDate(statusDate);

		commerceOrder = commerceOrderPersistence.update(commerceOrder);

		if (recalculate) {
			commerceOrder = _recalculateOrder(commerceOrder, commerceContext);
		}

		return commerceOrder;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal subtotal, BigDecimal total)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		return commerceOrderLocalService.updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, commerceOrder.getShippingWithTaxAmount(),
			subtotal, commerceOrder.getSubtotalWithTaxAmount(),
			commerceOrder.getTaxAmount(), total,
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalWithTaxAmount());
	}

	@Indexable(type = IndexableType.REINDEX)
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

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setExternalReferenceCode(externalReferenceCode);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceOrder.getGroupId());

		commerceOrder.setBillingAddressId(billingAddressId);

		_setAccountDefaultBillingAddress(commerceChannel, commerceOrder);

		commerceOrder.setShippingAddressId(shippingAddressId);

		_setAccountDefaultShippingAddress(commerceChannel, commerceOrder);

		User user = _userLocalService.getUser(commerceOrder.getUserId());

		commerceOrder.setCommerceShippingMethodId(commerceShippingMethodId);
		commerceOrder.setShippingOptionName(shippingOptionName);

		_setAccountDefaultShippingOption(commerceChannel, commerceOrder, user);

		commerceOrder.setAdvanceStatus(advanceStatus);

		commerceOrder.setCommercePaymentMethodKey(commercePaymentMethodKey);

		_setAccountDefaultPaymentIntegrationKey(commerceChannel, commerceOrder);
		_setAccountDefaultDeliveryTerm(commerceChannel, commerceOrder, user);
		_setAccountDefaultPaymentTerm(commerceChannel, commerceOrder, user);

		if (name != null) {
			commerceOrder.setName(name);
		}

		commerceOrder.setPurchaseOrderNumber(purchaseOrderNumber);

		if (totalDiscountAmount == null) {
			totalDiscountAmount = BigDecimal.ZERO;
		}

		commerceOrder.setTotalDiscountAmount(totalDiscountAmount);

		_setCommerceOrderPrices(
			commerceOrder, shippingAmount, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public void updateCommerceOrderAddresses(long addressId)
		throws PortalException {

		List<CommerceOrder> commerceOrders =
			commerceOrderLocalService.getCommerceOrdersByBillingAddress(
				addressId);

		_updateCommerceOrderAddresses(commerceOrders, addressId);

		commerceOrders =
			commerceOrderLocalService.getCommerceOrdersByShippingAddress(
				addressId);

		_updateCommerceOrderAddresses(commerceOrders, addressId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceOrderExternalReferenceCode(
			String externalReferenceCode, long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setExternalReferenceCode(externalReferenceCode);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
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

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		return commerceOrderLocalService.updateCommerceOrderPrices(
			commerceOrderId, shippingAmount, shippingDiscountAmount,
			shippingDiscountPercentageLevel1, shippingDiscountPercentageLevel2,
			shippingDiscountPercentageLevel3, shippingDiscountPercentageLevel4,
			commerceOrder.getShippingDiscountPercentageLevel1WithTaxAmount(),
			commerceOrder.getShippingDiscountPercentageLevel2WithTaxAmount(),
			commerceOrder.getShippingDiscountPercentageLevel3WithTaxAmount(),
			commerceOrder.getShippingDiscountPercentageLevel4WithTaxAmount(),
			commerceOrder.getShippingDiscountWithTaxAmount(),
			commerceOrder.getShippingWithTaxAmount(), subtotal,
			subtotalDiscountAmount, subtotalDiscountPercentageLevel1,
			subtotalDiscountPercentageLevel2, subtotalDiscountPercentageLevel3,
			subtotalDiscountPercentageLevel4,
			commerceOrder.getSubtotalDiscountPercentageLevel1WithTaxAmount(),
			commerceOrder.getSubtotalDiscountPercentageLevel2WithTaxAmount(),
			commerceOrder.getSubtotalDiscountPercentageLevel3WithTaxAmount(),
			commerceOrder.getSubtotalDiscountPercentageLevel4WithTaxAmount(),
			commerceOrder.getSubtotalDiscountWithTaxAmount(),
			commerceOrder.getSubtotalWithTaxAmount(), taxAmount, total,
			totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4,
			commerceOrder.getTotalDiscountPercentageLevel1WithTaxAmount(),
			commerceOrder.getTotalDiscountPercentageLevel2WithTaxAmount(),
			commerceOrder.getTotalDiscountPercentageLevel3WithTaxAmount(),
			commerceOrder.getTotalDiscountPercentageLevel4WithTaxAmount(),
			commerceOrder.getTotalDiscountWithTaxAmount(),
			commerceOrder.getTotalWithTaxAmount());
	}

	@Indexable(type = IndexableType.REINDEX)
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

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setLastPriceUpdateDate(new Date());
		commerceOrder.setManuallyAdjusted(true);
		commerceOrder.setShippingAmount(shippingAmount);
		commerceOrder.setShippingDiscountAmount(shippingDiscountAmount);
		commerceOrder.setShippingDiscountPercentageLevel1(
			shippingDiscountPercentageLevel1);
		commerceOrder.setShippingDiscountPercentageLevel2(
			shippingDiscountPercentageLevel2);
		commerceOrder.setShippingDiscountPercentageLevel3(
			shippingDiscountPercentageLevel3);
		commerceOrder.setShippingDiscountPercentageLevel4(
			shippingDiscountPercentageLevel4);
		commerceOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
			shippingDiscountPercentageLevel1WithTaxAmount);
		commerceOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
			shippingDiscountPercentageLevel2WithTaxAmount);
		commerceOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
			shippingDiscountPercentageLevel3WithTaxAmount);
		commerceOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
			shippingDiscountPercentageLevel4WithTaxAmount);
		commerceOrder.setShippingDiscountWithTaxAmount(
			shippingDiscountWithTaxAmount);
		commerceOrder.setShippingWithTaxAmount(shippingWithTaxAmount);
		commerceOrder.setSubtotal(subtotal);
		commerceOrder.setSubtotalDiscountAmount(subtotalDiscountAmount);
		commerceOrder.setSubtotalDiscountPercentageLevel1(
			subtotalDiscountPercentageLevel1);
		commerceOrder.setSubtotalDiscountPercentageLevel2(
			subtotalDiscountPercentageLevel2);
		commerceOrder.setSubtotalDiscountPercentageLevel3(
			subtotalDiscountPercentageLevel3);
		commerceOrder.setSubtotalDiscountPercentageLevel4(
			subtotalDiscountPercentageLevel4);
		commerceOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
			subtotalDiscountPercentageLevel1WithTaxAmount);
		commerceOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
			subtotalDiscountPercentageLevel2WithTaxAmount);
		commerceOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
			subtotalDiscountPercentageLevel3WithTaxAmount);
		commerceOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
			subtotalDiscountPercentageLevel4WithTaxAmount);
		commerceOrder.setSubtotalDiscountWithTaxAmount(
			subtotalDiscountWithTaxAmount);
		commerceOrder.setSubtotalWithTaxAmount(subtotalWithTaxAmount);
		commerceOrder.setTaxAmount(taxAmount);
		commerceOrder.setTotal(total);
		commerceOrder.setTotalDiscountAmount(totalDiscountAmount);
		commerceOrder.setTotalDiscountPercentageLevel1(
			totalDiscountPercentageLevel1);
		commerceOrder.setTotalDiscountPercentageLevel2(
			totalDiscountPercentageLevel2);
		commerceOrder.setTotalDiscountPercentageLevel3(
			totalDiscountPercentageLevel3);
		commerceOrder.setTotalDiscountPercentageLevel4(
			totalDiscountPercentageLevel4);
		commerceOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
			totalDiscountPercentageLevel1WithTaxAmount);
		commerceOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
			totalDiscountPercentageLevel2WithTaxAmount);
		commerceOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
			totalDiscountPercentageLevel3WithTaxAmount);
		commerceOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
			totalDiscountPercentageLevel4WithTaxAmount);
		commerceOrder.setTotalDiscountWithTaxAmount(totalDiscountWithTaxAmount);
		commerceOrder.setTotalWithTaxAmount(totalWithTaxAmount);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public CommerceOrder updateCommercePaymentMethodKey(
			long commerceOrderId, String commercePaymentMethodKey)
		throws PortalException {

		CommerceOrder commerceOrder =
			commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		commerceOrder.setCommercePaymentMethodKey(commercePaymentMethodKey);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName, BigDecimal shippingAmount,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setCommerceShippingMethodId(commerceShippingMethodId);
		commerceOrder.setShippingAmount(shippingAmount);
		commerceOrder.setShippingOptionName(commerceShippingOptionName);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName, CommerceContext commerceContext,
			Locale locale)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		if (commerceShippingMethodId > 0) {
			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodLocalService.getCommerceShippingMethod(
					commerceShippingMethodId);

			commerceOrder.setCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId());

			commerceOrder.setShippingOptionName(commerceShippingOptionName);

			CommerceShippingEngine commerceShippingEngine =
				_commerceShippingEngineRegistry.getCommerceShippingEngine(
					commerceShippingMethod.getEngineKey());

			List<CommerceShippingOption> commerceShippingOptions =
				commerceShippingEngine.getCommerceShippingOptions(
					commerceContext, commerceOrder, locale);

			for (CommerceShippingOption commerceShippingOption :
					commerceShippingOptions) {

				if (Validator.isNotNull(commerceShippingOptionName) &&
					commerceShippingOptionName.equals(
						commerceShippingOption.getKey())) {

					commerceOrder.setShippingAmount(
						commerceShippingOption.getAmount());

					break;
				}
			}
		}
		else {
			commerceOrder.setCommerceShippingMethodId(commerceShippingMethodId);
			commerceOrder.setShippingAmount(BigDecimal.ZERO);
			commerceOrder.setShippingOptionName(commerceShippingOptionName);
		}

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateInfo(
			long commerceOrderId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		Date requestedDeliveryDate = _portal.getDate(
			requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear, requestedDeliveryDateHour,
			requestedDeliveryDateMinute, user.getTimeZone(),
			CommerceOrderRequestedDeliveryDateException.class);

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setPrintedNote(printedNote);
		commerceOrder.setRequestedDeliveryDate(requestedDeliveryDate);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateOrderDate(
			long commerceOrderId, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setOrderDate(
			_portal.getDate(
				orderDateMonth, orderDateDay, orderDateYear, orderDateHour,
				orderDateMinute, user.getTimeZone(),
				CommerceOrderDateException.class));

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updatePaymentStatus(
			long userId, long commerceOrderId, int paymentStatus)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		return commerceOrderLocalService.updatePaymentStatusAndTransactionId(
			userId, commerceOrderId, paymentStatus,
			commerceOrder.getTransactionId());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updatePaymentStatusAndTransactionId(
			long userId, long commerceOrderId, int paymentStatus,
			String transactionId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		CommerceOrder originalCommerceOrder =
			commerceOrder.cloneWithOriginalValues();

		int previousPaymentStatus = commerceOrder.getPaymentStatus();

		commerceOrder.setPaymentStatus(paymentStatus);
		commerceOrder.setTransactionId(transactionId);

		commerceOrder = commerceOrderPersistence.update(commerceOrder);

		// Messaging

		_sendPaymentStatusMessage(
			commerceOrder, originalCommerceOrder, previousPaymentStatus);

		return commerceOrder;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updatePrintedNote(
			long commerceOrderId, String printedNote)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setPrintedNote(printedNote);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updatePurchaseOrderNumber(
			long commerceOrderId, String purchaseOrderNumber)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		_validatePurchaseOrderNumber(purchaseOrderNumber);

		commerceOrder.setPurchaseOrderNumber(purchaseOrderNumber);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long shippingAddressId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setShippingAddressId(shippingAddressId);

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		return _updateAddress(
			city, CommerceOrder::getShippingAddressId,
			CommerceOrder::setShippingAddressId, commerceOrderId, countryId,
			description, name, phoneNumber, regionId, street1, street2, street3,
			serviceContext, subtype, zip);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrder updateStatus(
			long userId, long commerceOrderId, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		commerceOrder.setStatus(status);
		commerceOrder.setStatusByUserId(user.getUserId());
		commerceOrder.setStatusByUserName(user.getFullName());
		commerceOrder.setStatusDate(new Date());

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Override
	public CommerceOrder updateTermsAndConditions(
			long commerceOrderId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, String languageId)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		CommerceTermEntry deliveryCommerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				deliveryCommerceTermEntryId);

		CommerceTermEntry paymentCommerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				paymentCommerceTermEntryId);

		if ((deliveryCommerceTermEntry == null) &&
			(paymentCommerceTermEntry == null)) {

			return commerceOrder;
		}

		if (deliveryCommerceTermEntry != null) {
			commerceOrder.setDeliveryCommerceTermEntryId(
				deliveryCommerceTermEntry.getCommerceTermEntryId());
			commerceOrder.setDeliveryCommerceTermEntryDescription(
				deliveryCommerceTermEntry.getDescription(languageId, true));
			commerceOrder.setDeliveryCommerceTermEntryName(
				deliveryCommerceTermEntry.getLabel(languageId, true));
		}

		if (paymentCommerceTermEntry != null) {
			commerceOrder.setPaymentCommerceTermEntryId(
				paymentCommerceTermEntry.getCommerceTermEntryId());
			commerceOrder.setPaymentCommerceTermEntryDescription(
				paymentCommerceTermEntry.getDescription(languageId, true));
			commerceOrder.setPaymentCommerceTermEntryName(
				paymentCommerceTermEntry.getLabel(languageId, true));
		}

		return commerceOrderPersistence.update(commerceOrder);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_commerceOrderConfiguration = ConfigurableUtil.createConfigurable(
			CommerceOrderConfiguration.class, properties);
	}

	private SearchContext _buildSearchContext(
			long companyId, long commerceChannelGroupId,
			long[] commerceAccountIds, String keywords, boolean negated,
			int[] orderStatuses, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = new SearchContext();

		if (orderStatuses != null) {
			searchContext.setAttribute("negateOrderStatuses", negated);
			searchContext.setAttribute("orderStatuses", orderStatuses);
		}

		if (commerceAccountIds != null) {
			searchContext.setAttribute(
				"commerceAccountIds", commerceAccountIds);
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {commerceChannelGroupId});
		searchContext.setKeywords(keywords);

		if (sort == null) {
			sort = SortFactoryUtil.getSort(
				CommerceOrder.class, Sort.LONG_TYPE, Field.CREATE_DATE, "DESC");
		}
		else {
			sort.setFieldName(Field.CREATE_DATE);
			sort.setType(Sort.LONG_TYPE);
		}

		searchContext.setSorts(sort);

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CommercePaymentMethodGroupRel>
		_filterCommercePaymentMethodGroupRels(
			long commerceOrderTypeId,
			List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels,
			boolean subscriptionOrder) {

		if (ListUtil.isEmpty(commercePaymentMethodGroupRels)) {
			return Collections.emptyList();
		}

		List<CommercePaymentMethodGroupRel>
			filteredCommercePaymentMethodGroupRels = new LinkedList<>();

		ListUtil.sort(
			commercePaymentMethodGroupRels,
			new CommercePaymentMethodPriorityComparator());

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			List<CommercePaymentMethodGroupRelQualifier>
				commercePaymentMethodGroupRelQualifiers =
					_commercePaymentMethodGroupRelQualifierLocalService.
						getCommercePaymentMethodGroupRelQualifiers(
							CommerceOrderType.class.getName(),
							commercePaymentMethodGroupRel.
								getCommercePaymentMethodGroupRelId());

			if ((commerceOrderTypeId > 0) &&
				ListUtil.isNotEmpty(commercePaymentMethodGroupRelQualifiers) &&
				!ListUtil.exists(
					commercePaymentMethodGroupRelQualifiers,
					commercePaymentMethodGroupRelQualifier -> {
						long classPK =
							commercePaymentMethodGroupRelQualifier.getClassPK();

						return classPK == commerceOrderTypeId;
					})) {

				continue;
			}

			CommercePaymentIntegration commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commercePaymentMethodGroupRel.
							getPaymentIntegrationKey());
			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentMethodRegistry.getCommercePaymentMethod(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey());
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (((commercePaymentIntegration == null) &&
				 (commercePaymentMethod == null)) ||
				!permissionChecker.hasPermission(
					commercePaymentMethodGroupRel.getGroupId(),
					CommercePaymentMethodGroupRel.class.getName(),
					commercePaymentMethodGroupRel.
						getCommercePaymentMethodGroupRelId(),
					ActionKeys.VIEW) ||
				((commercePaymentMethod == null) && subscriptionOrder) ||
				((commercePaymentMethod != null) && subscriptionOrder &&
				 !commercePaymentMethod.isProcessRecurringEnabled()) ||
				((commercePaymentMethod != null) && !subscriptionOrder &&
				 !commercePaymentMethod.isProcessPaymentEnabled())) {

				continue;
			}

			filteredCommercePaymentMethodGroupRels.add(
				commercePaymentMethodGroupRel);
		}

		return filteredCommercePaymentMethodGroupRels;
	}

	private JSONObject _getCommerceOrderJSONObject(
			CommerceOrder commerceOrder,
			DTOConverter<?, ?> commerceOrderDTOConverter)
		throws Exception {

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, commerceOrder.getCommerceOrderId(),
				LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceOrderJSONObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerializeDeep(
				commerceOrderDTOConverter.toDTO(dtoConverterContext)));

		JSONArray commerceOrderItemsJSONArray = _jsonFactory.createJSONArray();

		DTOConverter<?, ?> commerceOrderItemDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				CommerceOrderItem.class.getName());

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			dtoConverterContext = new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				commerceOrderItem.getCommerceOrderItemId(),
				LocaleUtil.getSiteDefault(), null, null);

			dtoConverterContext.setAttribute("secure", Boolean.FALSE);

			JSONObject commerceOrderItemJSONObject =
				_jsonFactory.createJSONObject(
					_jsonFactory.looseSerializeDeep(
						commerceOrderItemDTOConverter.toDTO(
							dtoConverterContext)));

			commerceOrderItemsJSONArray.put(commerceOrderItemJSONObject);
		}

		commerceOrderJSONObject.put("orderItems", commerceOrderItemsJSONArray);

		return commerceOrderJSONObject;
	}

	private List<CommerceOrder> _getCommerceOrders(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceOrder> commerceOrders = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long commerceOrderId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceOrder commerceOrder = fetchCommerceOrder(commerceOrderId);

			if (commerceOrder == null) {
				commerceOrders = null;

				Indexer<CommerceOrder> indexer = _indexerRegistry.getIndexer(
					CommerceOrder.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceOrders != null) {
				commerceOrders.add(commerceOrder);
			}
		}

		return commerceOrders;
	}

	private CommerceAddress _getNewCommerceAddress(
			String addressType, CommerceOrder commerceOrder,
			CommerceAddress commerceAddress, ServiceContext serviceContext)
		throws PortalException {

		if (commerceAddress == null) {
			return commerceAddress;
		}

		List<CommerceAddress> commerceAddresses = Collections.emptyList();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceOrder.getGroupId());

		if (Objects.equals(addressType, "billing")) {
			commerceAddresses =
				_commerceAddressLocalService.getBillingCommerceAddresses(
					commerceChannel.getCommerceChannelId(),
					AccountEntry.class.getName(),
					commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
		}

		if (Objects.equals(addressType, "shipping")) {
			commerceAddresses =
				_commerceAddressLocalService.getShippingCommerceAddresses(
					commerceChannel.getCommerceChannelId(),
					AccountEntry.class.getName(),
					commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
		}

		for (CommerceAddress newCommerceAddress : commerceAddresses) {
			if (commerceAddress.isSameAddress(newCommerceAddress)) {
				return newCommerceAddress;
			}
		}

		return _commerceAddressLocalService.copyCommerceAddress(
			commerceAddress.getCommerceAddressId(),
			CommerceOrder.class.getName(), commerceOrder.getCommerceOrderId(),
			serviceContext);
	}

	private long _getUserId(CommerceOrder commerceOrder) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			User user = _userLocalService.fetchUser(serviceContext.getUserId());

			if (user != null) {
				return user.getUserId();
			}
		}

		return commerceOrder.getUserId();
	}

	private boolean _hasWorkflowDefinition(long groupId, long typePK)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		return _workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
			group.getCompanyId(), group.getGroupId(),
			CommerceOrder.class.getName(), 0, typePK);
	}

	private CommerceOrder _recalculateOrder(
			CommerceOrder commerceOrder, CommerceContext commerceContext)
		throws PortalException {

		if ((commerceOrder.getOrderStatus() !=
				CommerceOrderConstants.ORDER_STATUS_OPEN) ||
			commerceOrder.isManuallyAdjusted()) {

			return commerceOrder;
		}

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			_commerceOrderItemLocalService.updateCommerceOrderItemPrice(
				commerceOrderItem.getCommerceOrderItemId(), commerceContext);
		}

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, false, commerceContext);

		CommerceMoney subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
		CommerceMoney shippingValueCommerceMoney =
			commerceOrderPrice.getShippingValue();
		CommerceMoney taxValueCommerceMoney = commerceOrderPrice.getTaxValue();
		CommerceMoney totalCommerceMoney = commerceOrderPrice.getTotal();
		CommerceMoney subtotalWithTaxAmountCommerceMoney =
			commerceOrderPrice.getSubtotalWithTaxAmount();
		CommerceMoney shippingValueWithTaxAmountCommerceMoney =
			commerceOrderPrice.getShippingValueWithTaxAmount();
		CommerceMoney totalWithTaxAmountCommerceMoney =
			commerceOrderPrice.getTotalWithTaxAmount();

		commerceOrder.setShippingAmount(shippingValueCommerceMoney.getPrice());
		commerceOrder.setSubtotal(subtotalCommerceMoney.getPrice());
		commerceOrder.setTaxAmount(taxValueCommerceMoney.getPrice());
		commerceOrder.setTotal(totalCommerceMoney.getPrice());

		if (subtotalWithTaxAmountCommerceMoney != null) {
			commerceOrder.setSubtotalWithTaxAmount(
				subtotalWithTaxAmountCommerceMoney.getPrice());
		}

		if (shippingValueWithTaxAmountCommerceMoney != null) {
			commerceOrder.setShippingWithTaxAmount(
				shippingValueWithTaxAmountCommerceMoney.getPrice());
		}

		if (totalWithTaxAmountCommerceMoney != null) {
			commerceOrder.setTotalWithTaxAmount(
				totalWithTaxAmountCommerceMoney.getPrice());
		}

		_setCommerceOrderSubtotalDiscountValue(
			commerceOrder, commerceOrderPrice.getSubtotalDiscountValue(),
			false);
		_setCommerceOrderShippingDiscountValue(
			commerceOrder, commerceOrderPrice.getShippingDiscountValue(),
			false);
		_setCommerceOrderTotalDiscountValue(
			commerceOrder, commerceOrderPrice.getTotalDiscountValue(), false);
		_setCommerceOrderSubtotalDiscountValue(
			commerceOrder,
			commerceOrderPrice.getSubtotalDiscountValueWithTaxAmount(), true);
		_setCommerceOrderShippingDiscountValue(
			commerceOrder,
			commerceOrderPrice.getShippingDiscountValueWithTaxAmount(), true);
		_setCommerceOrderTotalDiscountValue(
			commerceOrder,
			commerceOrderPrice.getTotalDiscountValueWithTaxAmount(), true);

		return commerceOrderPersistence.update(commerceOrder);
	}

	private void _sendPaymentStatusMessage(
		CommerceOrder commerceOrder, CommerceOrder originalCommerceOrder,
		int previousPaymentStatus) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Message message = new Message();

				DTOConverter<?, ?> commerceOrderDTOConverter =
					_dtoConverterRegistry.getDTOConverter(
						"Liferay.Headless.Commerce.Admin.Order",
						CommerceOrder.class.getName(), "v1.0");

				message.setPayload(
					JSONUtil.put(
						"classPK", commerceOrder.getCommerceOrderId()
					).put(
						"commerceOrder",
						_getCommerceOrderJSONObject(
							commerceOrder, commerceOrderDTOConverter)
					).put(
						"commerceOrderId", commerceOrder.getCommerceOrderId()
					).put(
						"model" + CommerceOrder.class.getSimpleName(),
						commerceOrder.getModelAttributes()
					).put(
						"modelDTO" + commerceOrderDTOConverter.getContentType(),
						_commerceModelAttributesProvider.getModelAttributes(
							commerceOrder, commerceOrderDTOConverter,
							commerceOrder.getUserId())
					).put(
						"originalCommerceOrder",
						originalCommerceOrder.getModelAttributes()
					).put(
						"paymentStatus", commerceOrder.getPaymentStatus()
					).put(
						"previousPaymentStatus", previousPaymentStatus
					));

				MessageBusUtil.sendMessage(
					DestinationNames.COMMERCE_PAYMENT_STATUS, message);

				return null;
			});
	}

	private void _setAccountDefaultBillingAddress(
			CommerceChannel commerceChannel, CommerceOrder commerceOrder)
		throws PortalException {

		if (commerceOrder.getBillingAddressId() > 0) {
			return;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					commerceOrder.getCommerceAccountId(),
					commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.
						TYPE_BILLING_ADDRESS);

		if (commerceChannelAccountEntryRel == null) {
			return;
		}

		CommerceAddress commerceAddress =
			_commerceAddressLocalService.getCommerceAddress(
				commerceChannelAccountEntryRel.getClassPK());

		if (commerceAddress == null) {
			return;
		}

		List<CommerceAddress> billingCommerceAddresses =
			_commerceAddressLocalService.getBillingCommerceAddresses(
				commerceChannel.getCommerceChannelId(),
				AccountEntry.class.getName(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		if (billingCommerceAddresses.contains(commerceAddress)) {
			commerceOrder.setBillingAddressId(
				commerceAddress.getCommerceAddressId());
		}
	}

	private void _setAccountDefaultDeliveryTerm(
		CommerceChannel commerceChannel, CommerceOrder commerceOrder,
		User user) {

		if (commerceOrder.getDeliveryCommerceTermEntryId() > 0) {
			return;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					commerceOrder.getCommerceAccountId(),
					commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_DELIVERY_TERM);

		if (commerceChannelAccountEntryRel == null) {
			return;
		}

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				commerceChannelAccountEntryRel.getClassPK());

		if (commerceChannelAccountEntryRel.isOverrideEligibility() &&
			(commerceTermEntry != null)) {

			commerceOrder.setDeliveryCommerceTermEntryId(
				commerceTermEntry.getCommerceTermEntryId());
			commerceOrder.setDeliveryCommerceTermEntryDescription(
				commerceTermEntry.getDescription(user.getLanguageId(), true));
			commerceOrder.setDeliveryCommerceTermEntryName(
				commerceTermEntry.getLabel(user.getLanguageId(), true));

			return;
		}

		CommerceShippingFixedOption commerceShippingFixedOption =
			_commerceShippingFixedOptionLocalService.
				fetchCommerceShippingFixedOption(
					commerceOrder.getCompanyId(),
					commerceOrder.getShippingOptionName());

		if ((commerceShippingFixedOption == null) ||
			(commerceTermEntry == null)) {

			return;
		}

		List<CommerceTermEntry> deliveryCommerceTermEntries =
			_commerceTermEntryLocalService.getDeliveryCommerceTermEntries(
				commerceOrder.getCompanyId(),
				commerceOrder.getCommerceOrderTypeId(),
				commerceShippingFixedOption.getCommerceShippingFixedOptionId());

		if (!commerceTermEntry.isActive() ||
			!deliveryCommerceTermEntries.contains(commerceTermEntry)) {

			return;
		}

		commerceOrder.setDeliveryCommerceTermEntryId(
			commerceTermEntry.getCommerceTermEntryId());
		commerceOrder.setDeliveryCommerceTermEntryDescription(
			commerceTermEntry.getDescription(user.getLanguageId(), true));
		commerceOrder.setDeliveryCommerceTermEntryName(
			commerceTermEntry.getLabel(user.getLanguageId(), true));
	}

	private void _setAccountDefaultPaymentIntegrationKey(
			CommerceChannel commerceChannel, CommerceOrder commerceOrder)
		throws PortalException {

		if (Validator.isNotNull(commerceOrder.getCommercePaymentMethodKey())) {
			return;
		}

		CommerceAddress commerceAddress = commerceOrder.getBillingAddress();

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getShippingAddress();

			if (commerceAddress == null) {
				return;
			}
		}

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			_filterCommercePaymentMethodGroupRels(
				commerceOrder.getCommerceOrderTypeId(),
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRels(
						commerceOrder.getGroupId(),
						commerceAddress.getCountryId(), true),
				commerceOrder.isSubscription());

		if (ListUtil.isEmpty(commercePaymentMethodGroupRels)) {
			return;
		}

		if (commercePaymentMethodGroupRels.size() == 1) {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				commercePaymentMethodGroupRels.get(0);

			commerceOrder.setCommercePaymentMethodKey(
				commercePaymentMethodGroupRel.getPaymentIntegrationKey());
		}

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		if ((accountEntry == null) || accountEntry.isGuestAccount() ||
			accountEntry.isPersonalAccount()) {

			return;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					accountEntry.getAccountEntryId(),
					commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT);

		if (commerceChannelAccountEntryRel == null) {
			return;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannelAccountEntryRel.getClassPK());

		if ((commercePaymentMethodGroupRel != null) &&
			commercePaymentMethodGroupRel.isActive() &&
			commercePaymentMethodGroupRels.contains(
				commercePaymentMethodGroupRel) &&
			Validator.isNull(commerceOrder.getCommercePaymentMethodKey())) {

			commerceOrder.setCommercePaymentMethodKey(
				commercePaymentMethodGroupRel.getPaymentIntegrationKey());
		}
	}

	private void _setAccountDefaultPaymentTerm(
		CommerceChannel commerceChannel, CommerceOrder commerceOrder,
		User user) {

		if (commerceOrder.getPaymentCommerceTermEntryId() > 0) {
			return;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					commerceOrder.getCommerceAccountId(),
					commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT_TERM);

		if (commerceChannelAccountEntryRel == null) {
			return;
		}

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				commerceChannelAccountEntryRel.getClassPK());

		if (commerceChannelAccountEntryRel.isOverrideEligibility() &&
			(commerceTermEntry != null)) {

			commerceOrder.setPaymentCommerceTermEntryId(
				commerceTermEntry.getCommerceTermEntryId());
			commerceOrder.setPaymentCommerceTermEntryDescription(
				commerceTermEntry.getDescription(user.getLanguageId(), true));
			commerceOrder.setPaymentCommerceTermEntryName(
				commerceTermEntry.getLabel(user.getLanguageId(), true));

			return;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(),
					commerceOrder.getCommercePaymentMethodKey());

		if ((commercePaymentMethodGroupRel == null) ||
			(commerceTermEntry == null)) {

			return;
		}

		List<CommerceTermEntry> paymentCommerceTermEntries =
			_commerceTermEntryLocalService.getPaymentCommerceTermEntries(
				commerceOrder.getCompanyId(),
				commerceOrder.getCommerceOrderTypeId(),
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId());

		if (!commerceTermEntry.isActive() ||
			!paymentCommerceTermEntries.contains(commerceTermEntry)) {

			return;
		}

		commerceOrder.setPaymentCommerceTermEntryId(
			commerceTermEntry.getCommerceTermEntryId());
		commerceOrder.setPaymentCommerceTermEntryDescription(
			commerceTermEntry.getDescription(user.getLanguageId(), true));
		commerceOrder.setPaymentCommerceTermEntryName(
			commerceTermEntry.getLabel(user.getLanguageId(), true));
	}

	private void _setAccountDefaultShippingAddress(
			CommerceChannel commerceChannel, CommerceOrder commerceOrder)
		throws PortalException {

		if (commerceOrder.getShippingAddressId() > 0) {
			return;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					commerceOrder.getCommerceAccountId(),
					commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.
						TYPE_SHIPPING_ADDRESS);

		if (commerceChannelAccountEntryRel == null) {
			return;
		}

		CommerceAddress commerceAddress =
			_commerceAddressLocalService.getCommerceAddress(
				commerceChannelAccountEntryRel.getClassPK());

		if (commerceAddress == null) {
			return;
		}

		List<CommerceAddress> shippingCommerceAddresses =
			_commerceAddressLocalService.getShippingCommerceAddresses(
				commerceChannel.getCommerceChannelId(),
				AccountEntry.class.getName(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		if (shippingCommerceAddresses.contains(commerceAddress)) {
			commerceOrder.setShippingAddressId(
				commerceAddress.getCommerceAddressId());
		}
	}

	private void _setAccountDefaultShippingOption(
			CommerceChannel commerceChannel, CommerceOrder commerceOrder,
			User user)
		throws PortalException {

		if (((commerceOrder.getCommerceShippingMethodId() > 0) &&
			 Validator.isNotNull(commerceOrder.getShippingOptionName())) ||
			(commerceOrder.getShippingAddressId() <= 0) ||
			!commerceOrder.isShippable()) {

			return;
		}

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		if ((accountEntry == null) || accountEntry.isGuestAccount() ||
			accountEntry.isPersonalAccount()) {

			return;
		}

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				_commerceShippingOptionAccountEntryRelService.
					fetchCommerceShippingOptionAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceChannel.getCommerceChannelId());

		if (commerceShippingOptionAccountEntryRel == null) {
			return;
		}

		List<CommerceShippingMethod> commerceShippingMethods =
			_commerceShippingMethodLocalService.getCommerceShippingMethods(
				commerceOrder.getGroupId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommerceShippingMethodPriorityComparator.getInstance(false));

		if (ListUtil.isEmpty(commerceShippingMethods)) {
			return;
		}

		for (CommerceShippingMethod commerceShippingMethod :
				commerceShippingMethods) {

			CommerceShippingEngine commerceShippingEngine =
				_commerceShippingEngineRegistry.getCommerceShippingEngine(
					commerceShippingMethod.getEngineKey());

			if (commerceShippingEngine == null) {
				continue;
			}

			List<CommerceShippingOption> commerceShippingOptions =
				commerceShippingEngine.getEnabledCommerceShippingOptions(
					null, commerceOrder, user.getLocale());

			if (ListUtil.isEmpty(commerceShippingOptions)) {
				continue;
			}

			CommerceShippingOption defaultCommerceShippingOption = null;

			for (CommerceShippingOption commerceShippingOption :
					commerceShippingOptions) {

				String key = commerceShippingOption.getKey();

				if (key.equals(
						commerceShippingOptionAccountEntryRel.
							getCommerceShippingOptionKey())) {

					defaultCommerceShippingOption = commerceShippingOption;

					break;
				}
			}

			if (defaultCommerceShippingOption != null) {
				commerceOrder.setCommerceShippingMethodId(
					commerceShippingMethod.getCommerceShippingMethodId());
				commerceOrder.setShippingAmount(
					defaultCommerceShippingOption.getAmount());
				commerceOrder.setShippingOptionName(
					defaultCommerceShippingOption.getKey());
			}
		}
	}

	private void _setCommerceOrderPrices(
		CommerceOrder commerceOrder, BigDecimal shippingAmount,
		BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
		BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
		BigDecimal total, BigDecimal totalWithTaxAmount) {

		if (shippingAmount == null) {
			shippingAmount = BigDecimal.ZERO;
		}

		if (shippingWithTaxAmount == null) {
			shippingWithTaxAmount = BigDecimal.ZERO;
		}

		if (subtotal == null) {
			subtotal = BigDecimal.ZERO;
		}

		if (subtotalWithTaxAmount == null) {
			subtotalWithTaxAmount = BigDecimal.ZERO;
		}

		if (taxAmount == null) {
			taxAmount = BigDecimal.ZERO;
		}

		if (total == null) {
			total = BigDecimal.ZERO;
		}

		if (totalWithTaxAmount == null) {
			totalWithTaxAmount = BigDecimal.ZERO;
		}

		commerceOrder.setShippingAmount(shippingAmount);
		commerceOrder.setShippingWithTaxAmount(shippingWithTaxAmount);
		commerceOrder.setSubtotal(subtotal);
		commerceOrder.setSubtotalWithTaxAmount(subtotalWithTaxAmount);
		commerceOrder.setTaxAmount(taxAmount);
		commerceOrder.setTotal(total);
		commerceOrder.setTotalWithTaxAmount(totalWithTaxAmount);
	}

	private void _setCommerceOrderShippingDiscountValue(
		CommerceOrder commerceOrder,
		CommerceDiscountValue commerceDiscountValue, boolean withTaxAmount) {

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

			if (percentages.length >= 1) {
				discountPercentageLevel1 = percentages[0];
			}

			if (percentages.length >= 2) {
				discountPercentageLevel2 = percentages[1];
			}

			if (percentages.length >= 3) {
				discountPercentageLevel3 = percentages[2];
			}

			if (percentages.length >= 4) {
				discountPercentageLevel4 = percentages[3];
			}
		}

		if (withTaxAmount) {
			commerceOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
				discountPercentageLevel1);
			commerceOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
				discountPercentageLevel2);
			commerceOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
				discountPercentageLevel3);
			commerceOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
				discountPercentageLevel4);
			commerceOrder.setShippingDiscountWithTaxAmount(discountAmount);
		}
		else {
			commerceOrder.setShippingDiscountAmount(discountAmount);
			commerceOrder.setShippingDiscountPercentageLevel1(
				discountPercentageLevel1);
			commerceOrder.setShippingDiscountPercentageLevel2(
				discountPercentageLevel2);
			commerceOrder.setShippingDiscountPercentageLevel3(
				discountPercentageLevel3);
			commerceOrder.setShippingDiscountPercentageLevel4(
				discountPercentageLevel4);
		}
	}

	private void _setCommerceOrderSubtotalDiscountValue(
		CommerceOrder commerceOrder,
		CommerceDiscountValue commerceDiscountValue, boolean withTaxAmount) {

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

		if (withTaxAmount) {
			commerceOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
				discountPercentageLevel1);
			commerceOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
				discountPercentageLevel2);
			commerceOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
				discountPercentageLevel3);
			commerceOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
				discountPercentageLevel4);
			commerceOrder.setSubtotalDiscountWithTaxAmount(discountAmount);
		}
		else {
			commerceOrder.setSubtotalDiscountAmount(discountAmount);
			commerceOrder.setSubtotalDiscountPercentageLevel1(
				discountPercentageLevel1);
			commerceOrder.setSubtotalDiscountPercentageLevel2(
				discountPercentageLevel2);
			commerceOrder.setSubtotalDiscountPercentageLevel3(
				discountPercentageLevel3);
			commerceOrder.setSubtotalDiscountPercentageLevel4(
				discountPercentageLevel4);
		}
	}

	private void _setCommerceOrderTotalDiscountValue(
		CommerceOrder commerceOrder,
		CommerceDiscountValue commerceDiscountValue, boolean withTaxAmount) {

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

			if (percentages.length >= 1) {
				discountPercentageLevel1 = percentages[0];
			}

			if (percentages.length >= 2) {
				discountPercentageLevel2 = percentages[1];
			}

			if (percentages.length >= 3) {
				discountPercentageLevel3 = percentages[2];
			}

			if (percentages.length >= 4) {
				discountPercentageLevel4 = percentages[3];
			}
		}

		if (withTaxAmount) {
			commerceOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
				discountPercentageLevel1);
			commerceOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
				discountPercentageLevel2);
			commerceOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
				discountPercentageLevel3);
			commerceOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
				discountPercentageLevel4);
			commerceOrder.setTotalDiscountWithTaxAmount(discountAmount);
		}
		else {
			commerceOrder.setTotalDiscountAmount(discountAmount);
			commerceOrder.setTotalDiscountPercentageLevel1(
				discountPercentageLevel1);
			commerceOrder.setTotalDiscountPercentageLevel2(
				discountPercentageLevel2);
			commerceOrder.setTotalDiscountPercentageLevel3(
				discountPercentageLevel3);
			commerceOrder.setTotalDiscountPercentageLevel4(
				discountPercentageLevel4);
		}
	}

	private CommerceOrder _updateAddress(
			String city, Function<CommerceOrder, Long> commerceAddressIdGetter,
			BiConsumer<CommerceOrder, Long> commerceAddressIdSetter,
			long commerceOrderId, long countryId, String description,
			String name, String phoneNumber, long regionId, String street1,
			String street2, String street3, ServiceContext serviceContext,
			String subtype, String zip)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderPersistence.findByPrimaryKey(
			commerceOrderId);

		CommerceAddress commerceAddress = null;

		long commerceAddressId = commerceAddressIdGetter.apply(commerceOrder);

		if (commerceAddressId > 0) {
			commerceAddress = _commerceAddressLocalService.getCommerceAddress(
				commerceAddressId);

			commerceAddress =
				_commerceAddressLocalService.updateCommerceAddress(
					commerceAddress.getExternalReferenceCode(),
					commerceAddressId, countryId, regionId, city, description,
					name, phoneNumber, street1, street2, street3, subtype,
					commerceAddress.getType(), zip, serviceContext);
		}
		else {
			commerceAddress = _commerceAddressLocalService.addCommerceAddress(
				StringPool.BLANK, commerceOrder.getModelClassName(),
				commerceOrder.getCommerceOrderId(), countryId, regionId, city,
				description, name, phoneNumber, street1, street2, street3,
				subtype,
				CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING, zip,
				serviceContext);
		}

		commerceAddressIdSetter.accept(
			commerceOrder, commerceAddress.getCommerceAddressId());

		return commerceOrderPersistence.update(commerceOrder);
	}

	private void _updateCommerceOrderAddresses(
			List<CommerceOrder> commerceOrders, long addressId)
		throws PortalException {

		for (CommerceOrder commerceOrder : commerceOrders) {
			long billingAddressId = commerceOrder.getBillingAddressId();
			long shippingAddressId = commerceOrder.getShippingAddressId();

			long commerceShippingMethodId =
				commerceOrder.getCommerceShippingMethodId();
			String shippingOptionName = commerceOrder.getShippingOptionName();
			BigDecimal shippingPrice = commerceOrder.getShippingAmount();

			if (billingAddressId == addressId) {
				billingAddressId = 0;
			}

			if (shippingAddressId == addressId) {
				shippingAddressId = 0;

				commerceShippingMethodId = 0;
				shippingOptionName = null;
				shippingPrice = BigDecimal.ZERO;
			}

			commerceOrderLocalService.updateCommerceOrder(
				null, commerceOrder.getCommerceOrderId(), billingAddressId,
				commerceShippingMethodId, shippingAddressId,
				commerceOrder.getAdvanceStatus(),
				commerceOrder.getCommercePaymentMethodKey(), null,
				commerceOrder.getPurchaseOrderNumber(), shippingPrice,
				shippingOptionName, commerceOrder.getSubtotal(),
				commerceOrder.getTotal());
		}
	}

	private void _validateAccountLimit(
			long commerceChannelGroupId, long commerceAccountId)
		throws PortalException {

		if (CommerceOrderThreadLocal.isSkipValidateAccountLimit()) {
			return;
		}

		Group group = _groupLocalService.getGroup(commerceChannelGroupId);

		int pendingCommerceOrdersCount =
			(int)commerceOrderLocalService.getCommerceOrdersCount(
				group.getCompanyId(), commerceChannelGroupId,
				new long[] {commerceAccountId}, StringPool.BLANK,
				new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN}, false);

		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderFieldsConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannelGroupId,
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		if ((commerceOrderFieldsConfiguration.accountCartMaxAllowed() > 0) &&
			(pendingCommerceOrdersCount >=
				commerceOrderFieldsConfiguration.accountCartMaxAllowed())) {

			throw new CommerceOrderAccountLimitException(
				"The commerce account carts limit was reached");
		}
	}

	private void _validateCommerceChannelAccount(
			long commerceChannelGroupId, long accountEntryId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceChannelGroupId);

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					accountEntryId, commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		int count =
			_commerceChannelAccountEntryRelLocalService.
				getCommerceChannelAccountEntryRelsCount(
					commerceChannel.getCommerceChannelId(), null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if ((commerceChannelAccountEntryRel == null) && (count != 0)) {
			throw new NoSuchChannelAccountEntryRelException(
				"This commerce account is not eligible for this commerce " +
					"channel");
		}
	}

	private void _validateGuestOrders() throws PortalException {
		int count = commerceOrderPersistence.countByUserId(
			UserConstants.USER_ID_DEFAULT);

		if (count >= _commerceOrderConfiguration.guestCartMaxAllowed()) {
			throw new GuestCartMaxAllowedException();
		}
	}

	private void _validatePurchaseOrderNumber(String purchaseOrderNumber)
		throws PortalException {

		if (Validator.isNull(purchaseOrderNumber)) {
			throw new CommerceOrderPurchaseOrderNumberException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderLocalServiceImpl.class);

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Reference
	private CommerceDiscountUsageEntryLocalService
		_commerceDiscountUsageEntryLocalService;

	@Reference
	private CommerceDiscountValidatorHelper _commerceDiscountValidatorHelper;

	@Reference
	private CommerceModelAttributesProvider _commerceModelAttributesProvider;

	private CommerceOrderConfiguration _commerceOrderConfiguration;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderItemPersistence _commerceOrderItemPersistence;

	@Reference
	private CommerceOrderNoteLocalService _commerceOrderNoteLocalService;

	@Reference
	private CommerceOrderPaymentLocalService _commerceOrderPaymentLocalService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceShippingOptionAccountEntryRelService
		_commerceShippingOptionAccountEntryRelService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

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

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}