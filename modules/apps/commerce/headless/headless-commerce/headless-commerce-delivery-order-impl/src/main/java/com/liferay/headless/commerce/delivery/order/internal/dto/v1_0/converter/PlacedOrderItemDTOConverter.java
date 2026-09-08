/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.dto.v1_0.converter;

import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemService;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItem;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Price;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Settings;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.VirtualItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItem"
	},
	service = DTOConverter.class
)
public class PlacedOrderItemDTOConverter
	implements DTOConverter<CommerceOrderItem, PlacedOrderItem> {

	@Override
	public String getContentType() {
		return PlacedOrderItem.class.getSimpleName();
	}

	@Override
	public PlacedOrderItem toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		PlacedOrderItemDTOConverterContext placedOrderItemDTOConverterContext =
			(PlacedOrderItemDTOConverterContext)dtoConverterContext;

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				(Long)placedOrderItemDTOConverterContext.getId());

		Locale locale = placedOrderItemDTOConverterContext.getLocale();

		return new PlacedOrderItem() {
			{
				setAdaptiveMediaImageHTMLTag(
					() ->
						_cpInstanceHelper.
							getCPInstanceAdaptiveMediaImageHTMLTag(
								placedOrderItemDTOConverterContext.
									getAccountId(),
								commerceOrderItem.getCompanyId(),
								commerceOrderItem.getCPInstanceId()));
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceOrderItem.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDeliveryGroup(commerceOrderItem::getDeliveryGroupName);
				setDeliveryGroupName(commerceOrderItem::getDeliveryGroupName);
				setErrorMessages(
					() -> _getErrorMessages(commerceOrderItem, locale));
				setExternalReferenceCode(
					commerceOrderItem::getExternalReferenceCode);
				setId(commerceOrderItem::getCommerceOrderItemId);
				setName(
					() -> commerceOrderItem.getName(
						_language.getLanguageId(locale)));
				setOptions(commerceOrderItem::getJson);
				setParentOrderItemId(
					commerceOrderItem::getParentCommerceOrderItemId);
				setPlacedOrderItems(
					() -> {
						PlacedOrderItem[] placedOrderItems =
							(PlacedOrderItem[])
								placedOrderItemDTOConverterContext.getAttribute(
									"placedOrderItems");

						if (ArrayUtil.isEmpty(placedOrderItems)) {
							return null;
						}

						return placedOrderItems;
					});
				setPrice(() -> _getPrice(commerceOrderItem, locale));
				setProductId(commerceOrderItem::getCProductId);
				setProductURLs(
					() -> LanguageUtils.getLanguageIdMap(
						_cpDefinitionLocalService.getUrlTitleMap(
							commerceOrderItem.getCPDefinitionId())));
				setQuantity(
					() -> _commerceQuantityFormatter.format(
						commerceOrderItem.getCPInstanceId(),
						commerceOrderItem.getQuantity(),
						commerceOrderItem.getUnitOfMeasureKey()));
				setReplacedSku(commerceOrderItem::getReplacedSku);
				setRequestedDeliveryDate(
					commerceOrderItem::getRequestedDeliveryDate);
				setSettings(
					() -> _getSettings(commerceOrderItem.getCPInstanceId()));
				setShippingAddressExternalReferenceCode(
					() -> {
						CommerceAddress commerceAddress =
							_commerceAddressService.fetchCommerceAddress(
								commerceOrderItem.getShippingAddressId());

						if (commerceAddress == null) {
							return null;
						}

						return commerceAddress.getExternalReferenceCode();
					});
				setShippingAddressId(commerceOrderItem::getShippingAddressId);
				setSku(commerceOrderItem::getSku);
				setSkuId(commerceOrderItem::getCPInstanceId);
				setSubscription(commerceOrderItem::isSubscription);
				setThumbnail(
					() -> _cpInstanceHelper.getCPInstanceThumbnailSrc(
						placedOrderItemDTOConverterContext.getAccountId(),
						commerceOrderItem.getCPInstanceId()));
				setUnitOfMeasure(
					() -> {
						String unitOfMeasureKey =
							commerceOrderItem.getUnitOfMeasureKey();

						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
							_cpInstanceUnitOfMeasureLocalService.
								fetchCPInstanceUnitOfMeasure(
									commerceOrderItem.getCPInstanceId(),
									unitOfMeasureKey);

						if (cpInstanceUnitOfMeasure == null) {
							return null;
						}

						return cpInstanceUnitOfMeasure.getName(locale);
					});
				setUnitOfMeasureKey(commerceOrderItem::getUnitOfMeasureKey);

				setVirtualItems(
					() -> {
						try {
							CommerceVirtualOrderItem commerceVirtualOrderItem =
								_commerceVirtualOrderItemService.
									fetchCommerceVirtualOrderItemByCommerceOrderItemId(
										commerceOrderItem.
											getCommerceOrderItemId());

							if ((commerceVirtualOrderItem == null) ||
								!commerceVirtualOrderItem.isActive()) {

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

							if ((commerceVirtualOrderItem == null) ||
								!commerceVirtualOrderItem.isActive()) {

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

	private String[] _getErrorMessages(
		CommerceOrderItem commerceOrderItem, Locale locale) {

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if (cpInstance != null) {
			return null;
		}

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			locale);

		return new String[] {
			_language.get(resourceBundle, "the-product-is-no-longer-available")
		};
	}

	private Price _getPrice(CommerceOrderItem commerceOrderItem, Locale locale)
		throws Exception {

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPrice(
				commerceCurrency, commerceOrderItem);

		CommerceMoney unitPriceCommerceMoney =
			commerceOrderItemPrice.getUnitPrice();

		BigDecimal unitPrice = unitPriceCommerceMoney.getPrice();

		Price price = new Price() {
			{
				setCurrency(() -> commerceCurrency.getName(locale));
				setPrice(unitPrice::doubleValue);
				setPriceFormatted(() -> unitPriceCommerceMoney.format(locale));
			}
		};

		CommerceMoney promoPriceCommerceMoney =
			commerceOrderItemPrice.getPromoPrice();

		if (promoPriceCommerceMoney != null) {
			BigDecimal unitPromoPrice = promoPriceCommerceMoney.getPrice();

			if (unitPromoPrice != null) {
				price.setPromoPrice(unitPromoPrice::doubleValue);
				price.setPromoPriceFormatted(
					() -> promoPriceCommerceMoney.format(locale));
			}
		}

		CommerceMoney discountAmountCommerceMoney =
			commerceOrderItemPrice.getDiscountAmount();

		if (discountAmountCommerceMoney != null) {
			BigDecimal discountAmount = discountAmountCommerceMoney.getPrice();

			if (discountAmount != null) {
				price.setDiscount(discountAmount::doubleValue);
				price.setDiscountFormatted(
					() -> discountAmountCommerceMoney.format(locale));
				price.setDiscountPercentage(
					() -> _commercePriceFormatter.format(
						commerceOrderItemPrice.getDiscountPercentage(),
						locale));

				BigDecimal discountPercentageLevel1 =
					commerceOrderItemPrice.getDiscountPercentageLevel1();
				BigDecimal discountPercentageLevel2 =
					commerceOrderItemPrice.getDiscountPercentageLevel2();
				BigDecimal discountPercentageLevel3 =
					commerceOrderItemPrice.getDiscountPercentageLevel3();
				BigDecimal discountPercentageLevel4 =
					commerceOrderItemPrice.getDiscountPercentageLevel4();

				price.setDiscountPercentageLevel1(
					discountPercentageLevel1::doubleValue);
				price.setDiscountPercentageLevel2(
					discountPercentageLevel2::doubleValue);
				price.setDiscountPercentageLevel3(
					discountPercentageLevel3::doubleValue);
				price.setDiscountPercentageLevel4(
					discountPercentageLevel4::doubleValue);
			}
		}

		CommerceMoney finalPriceCommerceMoney =
			commerceOrderItemPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		if (finalPrice != null) {
			price.setFinalPriceFormatted(
				() -> finalPriceCommerceMoney.format(locale));
			price.setFinalPrice(finalPrice::doubleValue);
		}

		return price;
	}

	private Settings _getSettings(long cpInstanceId) {
		Settings settings = new Settings();

		BigDecimal minOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY;
		BigDecimal maxOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY;
		BigDecimal multipleQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY;

		CPDefinitionInventory cpDefinitionInventory = null;

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance != null) {
			cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());
		}

		if (cpDefinitionInventory != null) {
			minOrderQuantity = cpDefinitionInventory.getMinOrderQuantity();
			maxOrderQuantity = cpDefinitionInventory.getMaxOrderQuantity();
			multipleQuantity = cpDefinitionInventory.getMultipleOrderQuantity();

			BigDecimal[] allowedOrderQuantitiesArray =
				cpDefinitionInventory.getAllowedOrderQuantitiesArray();

			if ((allowedOrderQuantitiesArray != null) &&
				(allowedOrderQuantitiesArray.length > 0)) {

				settings.setAllowedQuantities(
					() -> allowedOrderQuantitiesArray);
			}
		}

		if (minOrderQuantity != null) {
			BigDecimal finalMinOrderQuantity = minOrderQuantity;

			settings.setMinQuantity(
				() -> BigDecimalUtil.stripTrailingZeros(finalMinOrderQuantity));
		}

		if (maxOrderQuantity != null) {
			BigDecimal finalMaxOrderQuantity = maxOrderQuantity;

			settings.setMaxQuantity(
				() -> BigDecimalUtil.stripTrailingZeros(finalMaxOrderQuantity));
		}

		if (multipleQuantity != null) {
			BigDecimal finalMultipleQuantity = multipleQuantity;

			settings.setMultipleQuantity(
				() -> BigDecimalUtil.stripTrailingZeros(finalMultipleQuantity));
		}

		return settings;
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
		PlacedOrderItemDTOConverter.class);

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceVirtualOrderItemService _commerceVirtualOrderItemService;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private Language _language;

}