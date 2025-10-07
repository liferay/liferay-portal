/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.dto.v1_0.VirtualItem;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Order",
		"dto.class.name=com.liferay.commerce.model.CommerceOrderItem",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class OrderItemDTOConverter
	implements DTOConverter<CommerceOrderItem, OrderItem> {

	@Override
	public String getContentType() {
		return OrderItem.class.getSimpleName();
	}

	@Override
	public OrderItem toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrderItem commerceOrderItem = _getCommerceOrderItem(
			dtoConverterContext);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();
		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.fetchCPInstanceUnitOfMeasure(
				commerceOrderItem.getCompanyId(),
				commerceOrderItem.getUnitOfMeasureKey(),
				commerceOrderItem.getSku());

		return new OrderItem() {
			{
				setBookedQuantityId(
					commerceOrderItem::getCommerceInventoryBookedQuantityId);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CommerceOrderItem.class.getName(),
						commerceOrderItem.getCommerceOrderItemId(),
						commerceOrderItem.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDecimalQuantity(commerceOrderItem::getQuantity);
				setDeliveryGroup(commerceOrderItem::getDeliveryGroupName);
				setDeliveryGroupName(commerceOrderItem::getDeliveryGroupName);
				setDiscountAmount(commerceOrderItem::getDiscountAmount);
				setDiscountManuallyAdjusted(
					commerceOrderItem::isDiscountManuallyAdjusted);
				setDiscountPercentageLevel1(
					commerceOrderItem::getDiscountPercentageLevel1);
				setDiscountPercentageLevel1WithTaxAmount(
					commerceOrderItem::
						getDiscountPercentageLevel1WithTaxAmount);
				setDiscountPercentageLevel2(
					commerceOrderItem::getDiscountPercentageLevel2);
				setDiscountPercentageLevel2WithTaxAmount(
					commerceOrderItem::
						getDiscountPercentageLevel2WithTaxAmount);
				setDiscountPercentageLevel3(
					commerceOrderItem::getDiscountPercentageLevel3);
				setDiscountPercentageLevel3WithTaxAmount(
					commerceOrderItem::
						getDiscountPercentageLevel3WithTaxAmount);
				setDiscountPercentageLevel4(
					commerceOrderItem::getDiscountPercentageLevel4);
				setDiscountPercentageLevel4WithTaxAmount(
					commerceOrderItem::
						getDiscountPercentageLevel4WithTaxAmount);
				setDiscountWithTaxAmount(
					commerceOrderItem::getDiscountWithTaxAmount);
				setExternalReferenceCode(
					commerceOrderItem::getExternalReferenceCode);
				setFinalPrice(commerceOrderItem::getFinalPrice);
				setFinalPriceWithTaxAmount(
					commerceOrderItem::getFinalPriceWithTaxAmount);
				setFormattedQuantity(
					() -> _commerceOrderItemQuantityFormatter.format(
						commerceOrderItem, cpInstanceUnitOfMeasure,
						dtoConverterContext.getLocale()));
				setId(commerceOrderItem::getCommerceOrderItemId);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceOrderItem.getNameMap()));
				setOptions(commerceOrderItem::getJson);
				setOrderExternalReferenceCode(
					commerceOrder::getExternalReferenceCode);
				setOrderId(commerceOrder::getCommerceOrderId);
				setPriceManuallyAdjusted(
					commerceOrderItem::isPriceManuallyAdjusted);
				setPrintedNote(commerceOrderItem::getPrintedNote);
				setProductId(commerceOrderItem::getCProductId);
				setPromoPrice(commerceOrderItem::getPromoPrice);
				setPromoPriceWithTaxAmount(
					commerceOrderItem::getPromoPriceWithTaxAmount);
				setQuantity(
					() -> _commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure,
						commerceOrderItem.getQuantity()));
				setReplacedSku(commerceOrderItem::getReplacedSku);
				setReplacedSkuExternalReferenceCode(
					() -> _getReplacedSkuExternalReferenceCode(
						commerceOrderItem.getReplacedCPInstanceId()));
				setReplacedSkuId(commerceOrderItem::getReplacedCPInstanceId);
				setRequestedDeliveryDate(
					commerceOrderItem::getRequestedDeliveryDate);
				setShippable(commerceOrder::isShippable);
				setShippedQuantity(
					() -> _commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure,
						commerceOrderItem.getShippedQuantity()));
				setShippingAddressExternalReferenceCode(
					() -> _getShippingAddressExternalReferenceCode(
						commerceOrderItem.getShippingAddressId()));
				setShippingAddressId(commerceOrderItem::getShippingAddressId);
				setSku(commerceOrderItem::getSku);
				setSkuExternalReferenceCode(
					() -> _getSkuExternalReferenceCode(cpInstance));
				setSkuId(() -> _getSkuId(cpInstance));
				setSubscription(commerceOrderItem::isSubscription);
				setUnitOfMeasure(
					() -> {
						if (commerceOrderItem.getCPMeasurementUnitId() <= 0) {
							return StringPool.BLANK;
						}

						CPMeasurementUnit cpMeasurementUnit =
							_cpMeasurementUnitService.getCPMeasurementUnit(
								commerceOrderItem.getCPMeasurementUnitId());

						return cpMeasurementUnit.getKey();
					});
				setUnitOfMeasureKey(commerceOrderItem::getUnitOfMeasureKey);
				setUnitPrice(commerceOrderItem::getUnitPrice);
				setUnitPriceWithTaxAmount(
					commerceOrderItem::getUnitPriceWithTaxAmount);
				setVirtualItems(
					() -> {
						try {
							CommerceVirtualOrderItem commerceVirtualOrderItem =
								_commerceVirtualOrderItemService.
									fetchCommerceVirtualOrderItemByCommerceOrderItemId(
										commerceOrderItem.
											getCommerceOrderItemId());

							if (commerceVirtualOrderItem == null) {
								return null;
							}

							return _toVirtualItems(
								commerceVirtualOrderItem.
									getCommerceVirtualOrderItemFileEntries(),
								commerceVirtualOrderItem);
						}
						catch (PortalException portalException) {
							if (_log.isDebugEnabled()) {
								_log.debug(portalException);
							}

							return null;
						}
					});
				setVirtualItemURLs(
					() -> {
						try {
							CommerceVirtualOrderItem commerceVirtualOrderItem =
								_commerceVirtualOrderItemService.
									fetchCommerceVirtualOrderItemByCommerceOrderItemId(
										commerceOrderItem.
											getCommerceOrderItemId());

							if (commerceVirtualOrderItem == null) {
								return null;
							}

							List<CommerceVirtualOrderItemFileEntry>
								commerceVirtualOrderItemFileEntries =
									commerceVirtualOrderItem.
										getCommerceVirtualOrderItemFileEntries();

							if (commerceVirtualOrderItemFileEntries.isEmpty()) {
								return null;
							}

							CommerceVirtualOrderItemFileEntry
								commerceVirtualOrderItemFileEntry =
									commerceVirtualOrderItemFileEntries.get(0);

							String url =
								commerceVirtualOrderItemFileEntry.getUrl();

							if (Validator.isBlank(url)) {
								url =
									_commerceMediaResolver.
										getDownloadVirtualOrderItemURL(
											commerceVirtualOrderItem.
												getCommerceVirtualOrderItemId(),
											commerceVirtualOrderItemFileEntry.
												getFileEntryId());
							}

							return new String[] {url};
						}
						catch (PortalException portalException) {
							if (_log.isDebugEnabled()) {
								_log.debug(portalException);
							}

							return null;
						}
					});
			}
		};
	}

	private CommerceOrderItem _getCommerceOrderItem(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrderItem commerceOrderItem = null;

		boolean secure = GetterUtil.getBoolean(
			dtoConverterContext.getAttribute("secure"), true);

		if (secure) {
			commerceOrderItem = _commerceOrderItemService.getCommerceOrderItem(
				(Long)dtoConverterContext.getId());
		}
		else {
			commerceOrderItem =
				_commerceOrderItemLocalService.getCommerceOrderItem(
					(Long)dtoConverterContext.getId());
		}

		return commerceOrderItem;
	}

	private String _getReplacedSkuExternalReferenceCode(
		long replacedCPInstanceId) {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			replacedCPInstanceId);

		if (cpInstance == null) {
			return null;
		}

		return cpInstance.getExternalReferenceCode();
	}

	private String _getShippingAddressExternalReferenceCode(
		long shippingAddressId) {

		CommerceAddress commerceAddress =
			_commerceAddressLocalService.fetchCommerceAddress(
				shippingAddressId);

		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getExternalReferenceCode();
	}

	private String _getSkuExternalReferenceCode(CPInstance cpInstance) {
		if (cpInstance == null) {
			return StringPool.BLANK;
		}

		return cpInstance.getExternalReferenceCode();
	}

	private Long _getSkuId(CPInstance cpInstance) {
		if (cpInstance == null) {
			return 0L;
		}

		return cpInstance.getCPInstanceId();
	}

	private VirtualItem[] _toVirtualItems(
		List<CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries,
		CommerceVirtualOrderItem commerceVirtualOrderItem) {

		return TransformUtil.transformToArray(
			commerceVirtualOrderItemFileEntries,
			commerceVirtualOrderItemFileEntry -> new VirtualItem() {
				{
					setUrl(
						() -> {
							if (Validator.isNull(
									commerceVirtualOrderItemFileEntry.
										getUrl())) {

								return _commerceMediaResolver.
									getDownloadVirtualOrderItemURL(
										commerceVirtualOrderItem.
											getCommerceVirtualOrderItemId(),
										commerceVirtualOrderItemFileEntry.
											getFileEntryId());
							}

							return commerceVirtualOrderItemFileEntry.getUrl();
						});
					setUsages(commerceVirtualOrderItemFileEntry::getUsages);
					setVersion(
						() -> {
							if (Validator.isNull(
									commerceVirtualOrderItemFileEntry.
										getVersion())) {

								return null;
							}

							return commerceVirtualOrderItemFileEntry.
								getVersion();
						});
				}
			},
			VirtualItem.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrderItemDTOConverter.class);

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceVirtualOrderItemService _commerceVirtualOrderItemService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private CPMeasurementUnitService _cpMeasurementUnitService;

}