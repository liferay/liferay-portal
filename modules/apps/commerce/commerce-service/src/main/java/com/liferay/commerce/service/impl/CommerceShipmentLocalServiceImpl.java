/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.exception.CommerceShipmentExpectedDateException;
import com.liferay.commerce.exception.CommerceShipmentItemQuantityException;
import com.liferay.commerce.exception.CommerceShipmentShippingDateException;
import com.liferay.commerce.exception.CommerceShipmentStatusException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.attributes.provider.CommerceModelAttributesProvider;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.service.base.CommerceShipmentLocalServiceBaseImpl;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceShipment",
	service = AopService.class
)
public class CommerceShipmentLocalServiceImpl
	extends CommerceShipmentLocalServiceBaseImpl {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addDeliverySubscriptionCommerceShipment(long, long)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment addCommerceDeliverySubscriptionShipment(
			long userId, long commerceOrderId, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		long commerceShipmentId = counterLocalService.increment();

		CommerceShipment commerceShipment = commerceShipmentPersistence.create(
			commerceShipmentId);

		commerceShipment.setGroupId(commerceOrder.getGroupId());
		commerceShipment.setCompanyId(user.getCompanyId());
		commerceShipment.setUserId(user.getUserId());
		commerceShipment.setUserName(user.getFullName());
		commerceShipment.setCommerceAccountId(
			commerceOrder.getCommerceAccountId());
		commerceShipment.setCommerceAddressId(
			commerceOrder.getShippingAddressId());
		commerceShipment.setCommerceShippingMethodId(
			commerceOrder.getCommerceShippingMethodId());
		commerceShipment.setShippingOptionName(
			commerceOrder.getShippingOptionName());
		commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING);

		CommerceAddress commerceAddress = _updateCommerceShipmentAddress(
			null, commerceShipment, name, description, street1, street2,
			street3, city, zip, regionId, countryId, phoneNumber, null);

		commerceShipment.setCommerceAddressId(
			commerceAddress.getCommerceAddressId());

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Override
	public CommerceShipment addCommerceShipment(
			long commerceOrderId, ServiceContext serviceContext)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		return commerceShipmentLocalService.addCommerceShipment(
			null, commerceOrder.getGroupId(),
			commerceOrder.getCommerceAccountId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getCommerceShippingMethodId(),
			commerceOrder.getShippingOptionName(), serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long commerceShipmentId = counterLocalService.increment();

		CommerceShipment commerceShipment = commerceShipmentPersistence.create(
			commerceShipmentId);

		commerceShipment.setExternalReferenceCode(externalReferenceCode);
		commerceShipment.setGroupId(groupId);
		commerceShipment.setCompanyId(user.getCompanyId());
		commerceShipment.setUserId(user.getUserId());
		commerceShipment.setUserName(user.getFullName());
		commerceShipment.setCommerceAccountId(commerceAccountId);
		commerceShipment.setCommerceAddressId(commerceAddressId);

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceShippingMethodId);

		if (commerceShippingMethod != null) {
			commerceShipment.setCommerceShippingMethodId(
				commerceShippingMethodId);
			commerceShipment.setShippingOptionName(commerceShippingOptionName);
			commerceShipment.setTrackingURL(
				commerceShippingMethod.getTrackingURL());
		}

		commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING);
		commerceShipment.setExpandoBridgeAttributes(serviceContext);

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment addDeliverySubscriptionCommerceShipment(
			long userId, long commerceOrderItemId)
		throws PortalException {

		long commerceShipmentId = counterLocalService.increment();

		CommerceShipment commerceShipment = commerceShipmentPersistence.create(
			commerceShipmentId);

		User user = _userLocalService.getUser(userId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		commerceShipment.setGroupId(commerceOrder.getGroupId());

		commerceShipment.setCompanyId(user.getCompanyId());
		commerceShipment.setUserId(user.getUserId());
		commerceShipment.setUserName(user.getFullName());
		commerceShipment.setCommerceAccountId(
			commerceOrder.getCommerceAccountId());
		commerceShipment.setCommerceAddressId(
			commerceOrder.getShippingAddressId());
		commerceShipment.setCommerceShippingMethodId(
			commerceOrder.getCommerceShippingMethodId());
		commerceShipment.setShippingOptionName(
			commerceOrder.getShippingOptionName());
		commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING);

		commerceShipment = commerceShipmentPersistence.update(commerceShipment);

		_commerceShipmentItemLocalService.
			addDeliverySubscriptionCommerceShipmentItem(
				commerceOrder.getScopeGroupId(), userId, commerceShipmentId,
				commerceOrderItemId);

		return commerceShipment;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceShipment deleteCommerceShipment(
			CommerceShipment commerceShipment, boolean restoreStockQuantity)
		throws PortalException {

		commerceShipment = commerceShipmentPersistence.remove(commerceShipment);

		_commerceShipmentItemLocalService.deleteCommerceShipmentItems(
			commerceShipment.getCommerceShipmentId(), restoreStockQuantity);

		_expandoRowLocalService.deleteRows(
			commerceShipment.getCommerceShipmentId());

		return commerceShipment;
	}

	@Override
	public CommerceShipment deleteCommerceShipment(long commerceShipmentId)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		return commerceShipmentLocalService.deleteCommerceShipment(
			commerceShipment);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
		long commerceOrderId, int start, int end) {

		return commerceShipmentFinder.findByCommerceOrderId(
			commerceOrderId, start, end);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus, int start, int end)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, commerceAccountIds, keywords,
			excludeShipmentStatus, shipmentStatuses, start, end);

		BaseModelSearchResult<CommerceShipment> baseModelSearchResult =
			searchCommerceShipments(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
		long[] groupIds, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return commerceShipmentPersistence.findByG_S(
			groupIds, status, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
		long[] groupIds, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return commerceShipmentPersistence.findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
		long[] groupIds, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return commerceShipmentPersistence.findByG_C(
			groupIds, commerceAddressId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceShipmentsCount(long commerceOrderId) {
		return commerceShipmentFinder.countByCommerceOrderId(commerceOrderId);
	}

	@Override
	public int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, commerceAccountIds, keywords,
			excludeShipmentStatus, shipmentStatuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		BaseModelSearchResult<CommerceShipment> baseModelSearchResult =
			searchCommerceShipments(searchContext);

		return baseModelSearchResult.getLength();
	}

	@Override
	public int getCommerceShipmentsCount(long[] groupIds) {
		return commerceShipmentPersistence.countByGroupId(groupIds);
	}

	@Override
	public int getCommerceShipmentsCount(long[] groupIds, int status) {
		return commerceShipmentPersistence.countByG_S(groupIds, status);
	}

	@Override
	public int getCommerceShipmentsCount(
		long[] groupIds, long commerceAddressId) {

		return commerceShipmentPersistence.countByG_C(
			groupIds, commerceAddressId);
	}

	@Override
	public int[] getCommerceShipmentStatusesByCommerceOrderId(
		long commerceOrderId) {

		return commerceShipmentFinder.
			findCommerceShipmentStatusesByCommerceOrderId(commerceOrderId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment reprocessCommerceShipment(long commerceShipmentId)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		if (commerceShipment.getStatus() ==
				CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED) {

			throw new CommerceShipmentStatusException();
		}

		commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING);

		if (ArrayUtil.contains(
				messageShipmentStatuses,
				CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING)) {

			sendShipmentStatusMessage(commerceShipment);
		}

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Override
	public BaseModelSearchResult<CommerceShipment> searchCommerceShipments(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceShipment> indexer = _indexerRegistry.nullSafeGetIndexer(
			CommerceShipment.class.getName());

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<CommerceShipment> commerceShipments = _getCommerceShipments(
				hits);

			if (commerceShipments != null) {
				return new BaseModelSearchResult<>(
					commerceShipments, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Override
	public long searchCommerceShipmentsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceShipment> indexer = _indexerRegistry.nullSafeGetIndexer(
			CommerceShipment.class.getName());

		return indexer.searchCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber, ServiceContext serviceContext)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		CommerceAddress commerceAddress = _updateCommerceShipmentAddress(
			externalReferenceCode, commerceShipment, name, description, street1,
			street2, street3, city, zip, regionId, countryId, phoneNumber,
			serviceContext);

		commerceShipment.setCommerceAddressId(
			commerceAddress.getCommerceAddressId());

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		if (commerceShipment.getCommerceShippingMethodId() !=
				commerceShippingMethodId) {

			commerceShipment.setCommerceShippingMethodId(
				commerceShippingMethodId);
			commerceShipment.setShippingOptionName(null);
		}

		commerceShipment.setCarrier(carrier);
		commerceShipment.setTrackingNumber(trackingNumber);
		commerceShipment.setTrackingURL(trackingURL);

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Override
	public CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, ServiceContext serviceContext)
		throws PortalException {

		String name = null;
		String description = null;
		String street1 = null;
		String street2 = null;
		String street3 = null;
		String city = null;
		String zip = null;
		long regionId = 0;
		long countryId = 0;
		String phoneNumber = null;

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		CommerceAddress commerceAddress =
			commerceShipment.fetchCommerceAddress();

		if (commerceAddress != null) {
			name = commerceAddress.getName();
			description = commerceAddress.getDescription();
			street1 = commerceAddress.getStreet1();
			street2 = commerceAddress.getStreet2();
			street3 = commerceAddress.getStreet3();
			city = commerceAddress.getCity();
			zip = commerceAddress.getZip();
			regionId = commerceAddress.getRegionId();
			countryId = commerceAddress.getCountryId();
			phoneNumber = commerceAddress.getPhoneNumber();
		}

		return commerceShipmentLocalService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		User user = _userLocalService.getUser(serviceContext.getUserId());

		int oldStatus = commerceShipment.getStatus();

		_validateStatus(status, oldStatus);

		if (commerceShipment.getCommerceShippingMethodId() !=
				commerceShippingMethodId) {

			commerceShipment.setCommerceShippingMethodId(
				commerceShippingMethodId);
			commerceShipment.setShippingOptionName(null);
		}

		CommerceAddress commerceAddress = _updateCommerceShipmentAddress(
			null, commerceShipment, name, description, street1, street2,
			street3, city, zip, regionId, countryId, phoneNumber,
			serviceContext);

		commerceShipment.setCommerceAddressId(
			commerceAddress.getCommerceAddressId());

		commerceShipment.setCarrier(carrier);
		commerceShipment.setTrackingNumber(trackingNumber);
		commerceShipment.setTrackingURL(trackingURL);

		Date shippingDate = _getDate(
			shippingDateMonth, shippingDateDay, shippingDateYear,
			shippingDateHour, shippingDateMinute, user.getTimeZone(),
			CommerceShipmentShippingDateException.class);

		if (shippingDate != null) {
			commerceShipment.setShippingDate(shippingDate);
		}

		Date expectedDate = _getDate(
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, user.getTimeZone(),
			CommerceShipmentExpectedDateException.class);

		if (expectedDate != null) {
			commerceShipment.setExpectedDate(expectedDate);
		}

		commerceShipment.setStatus(status);
		commerceShipment.setExpandoBridgeAttributes(serviceContext);

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		User user = _userLocalService.getUser(commerceShipment.getUserId());

		commerceShipment.setExpectedDate(
			_getDate(
				expectedDateMonth, expectedDateDay, expectedDateYear,
				expectedDateHour, expectedDateMinute, user.getTimeZone(),
				CommerceShipmentShippingDateException.class));

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateExternalReferenceCode(
			long commerceShipmentId, String externalReferenceCode)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		if (Objects.equals(
				commerceShipment.getExternalReferenceCode(),
				externalReferenceCode)) {

			return commerceShipment;
		}

		commerceShipment.setExternalReferenceCode(externalReferenceCode);

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		User user = _userLocalService.getUser(commerceShipment.getUserId());

		commerceShipment.setShippingDate(
			_getDate(
				shippingDateMonth, shippingDateDay, shippingDateYear,
				shippingDateHour, shippingDateMinute, user.getTimeZone(),
				CommerceShipmentShippingDateException.class));

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipment updateStatus(long commerceShipmentId, int status)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);

		int commerceShipmentItemsCount =
			_commerceShipmentItemLocalService.getCommerceShipmentItemsCount(
				commerceShipmentId);

		if (commerceShipmentItemsCount == 0) {
			throw new CommerceShipmentItemQuantityException();
		}

		int validCommerceShipmentItemsCount =
			_commerceShipmentItemLocalService.
				getValidCommerceShipmentItemsCount(commerceShipmentId);

		if (validCommerceShipmentItemsCount != commerceShipmentItemsCount) {
			throw new CommerceShipmentStatusException();
		}

		commerceShipment.setStatus(status);

		if (ArrayUtil.contains(messageShipmentStatuses, status)) {
			sendShipmentStatusMessage(commerceShipment);
		}

		return commerceShipmentPersistence.update(commerceShipment);
	}

	@Transactional(
		propagation = Propagation.REQUIRED, rollbackFor = Exception.class
	)
	protected void sendShipmentStatusMessage(
		CommerceShipment commerceShipment) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Message message = new Message();

				DTOConverter<?, ?> commerceShipmentDTOConverter =
					_dtoConverterRegistry.getDTOConverter(
						CommerceShipment.class.getName());

				message.setPayload(
					JSONUtil.put(
						"classPK", commerceShipment.getCommerceShipmentId()
					).put(
						"commerceShipment",
						() -> {
							Object object = commerceShipmentDTOConverter.toDTO(
								new DefaultDTOConverterContext(
									_dtoConverterRegistry,
									commerceShipment.getCommerceShipmentId(),
									LocaleUtil.getSiteDefault(), null, null));

							return _jsonFactory.createJSONObject(
								_jsonFactory.looseSerializeDeep(object));
						}
					).put(
						"commerceShipmentId",
						commerceShipment.getCommerceShipmentId()
					).put(
						"model" + CommerceShipment.class.getName(),
						commerceShipment.getModelAttributes()
					).put(
						"modelDTO" +
							commerceShipmentDTOConverter.getContentType(),
						_commerceModelAttributesProvider.getModelAttributes(
							commerceShipment, commerceShipmentDTOConverter,
							commerceShipment.getUserId())
					));

				MessageBusUtil.sendMessage(
					DestinationNames.COMMERCE_SHIPMENT_STATUS, message);

				return null;
			});
	}

	protected int[] messageShipmentStatuses = {
		CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED,
		CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED
	};

	private SearchContext _buildSearchContext(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, boolean negated, int[] shipmentStatuses, int start,
			int end)
		throws PortalException {

		SearchContext searchContext = new SearchContext();

		if (shipmentStatuses != null) {
			searchContext.setAttribute("negateShipmentStatuses", negated);
			searchContext.setAttribute("shipmentStatues", shipmentStatuses);
		}

		if (commerceAccountIds != null) {
			searchContext.setAttribute(
				"commerceAccountIds", commerceAccountIds);
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setKeywords(keywords);
		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CommerceShipment> _getCommerceShipments(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceShipment> commerceShipments = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceShipmentId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceShipment commerceShipment = fetchCommerceShipment(
				commerceShipmentId);

			if (commerceShipment == null) {
				commerceShipments = null;

				Indexer<CommerceShipment> indexer = _indexerRegistry.getIndexer(
					CommerceShipment.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceShipments != null) {
				commerceShipments.add(commerceShipment);
			}
		}

		return commerceShipments;
	}

	private Date _getDate(
			int dateMonth, int dateDay, int dateYear, int dateHour,
			int dateMinute, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException {

		if ((dateMonth == 0) && (dateDay == 0) && (dateYear == 0) &&
			(dateHour == 0) && (dateMinute == 0)) {

			return null;
		}

		return _portal.getDate(
			dateMonth, dateDay, dateYear, dateHour, dateMinute, timeZone,
			clazz);
	}

	private CommerceAddress _updateCommerceShipmentAddress(
			String externalReferenceCode, CommerceShipment commerceShipment,
			String name, String description, String street1, String street2,
			String street3, String city, String zip, long regionId,
			long countryId, String phoneNumber, ServiceContext serviceContext)
		throws PortalException {

		CommerceAddress commerceAddress =
			commerceShipment.fetchCommerceAddress();

		if (Objects.equals(name, commerceAddress.getName()) &&
			Objects.equals(description, commerceAddress.getDescription()) &&
			Objects.equals(street1, commerceAddress.getStreet1()) &&
			Objects.equals(street2, commerceAddress.getStreet2()) &&
			Objects.equals(street3, commerceAddress.getStreet3()) &&
			Objects.equals(city, commerceAddress.getCity()) &&
			Objects.equals(zip, commerceAddress.getZip()) &&
			Objects.equals(regionId, commerceAddress.getRegionId()) &&
			Objects.equals(countryId, commerceAddress.getCountryId()) &&
			Objects.equals(phoneNumber, commerceAddress.getPhoneNumber())) {

			return commerceAddress;
		}

		return _commerceAddressLocalService.addCommerceAddress(
			externalReferenceCode, commerceShipment.getModelClassName(),
			commerceShipment.getCommerceShipmentId(), countryId, regionId, city,
			description, name, phoneNumber, street1, street2, street3,
			StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING, zip,
			serviceContext);
	}

	private void _validateStatus(int status, int oldStatus)
		throws PortalException {

		if (status < oldStatus) {
			throw new CommerceShipmentStatusException();
		}
	}

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceModelAttributesProvider _commerceModelAttributesProvider;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

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

}