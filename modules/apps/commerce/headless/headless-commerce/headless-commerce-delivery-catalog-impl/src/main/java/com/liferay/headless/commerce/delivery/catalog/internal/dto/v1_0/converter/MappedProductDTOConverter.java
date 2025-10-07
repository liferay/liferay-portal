/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.constants.CommerceInventoryAvailabilityConstants;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Availability;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Price;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0.SkuOptionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry",
	service = DTOConverter.class
)
public class MappedProductDTOConverter
	implements DTOConverter<CSDiagramEntry, MappedProduct> {

	@Override
	public String getContentType() {
		return MappedProduct.class.getSimpleName();
	}

	@Override
	public MappedProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		MappedProductDTOConverterContext mappedProductDTOConverterContext =
			(MappedProductDTOConverterContext)dtoConverterContext;

		CommerceContext commerceContext =
			mappedProductDTOConverterContext.getCommerceContext();

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryLocalService.getCSDiagramEntry(
				(Long)mappedProductDTOConverterContext.getId());

		long cpInstanceId = GetterUtil.getLong(
			mappedProductDTOConverterContext.getReplacementCPInstanceId(),
			csDiagramEntry.getCPInstanceId());

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		long cProductId = GetterUtil.getLong(
			mappedProductDTOConverterContext.getReplacementCProductId(),
			csDiagramEntry.getCProductId());

		if (cpInstance != null) {
			CProduct cProduct =
				_cProductLocalService.getCProductByCPInstanceUuid(
					cpInstance.getCPInstanceUuid());

			cProductId = cProduct.getCProductId();
		}

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(cProductId);

		if ((cpDefinition != null) &&
			!_commerceProductViewPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				CommerceUtil.getCommerceAccountId(commerceContext),
				cpDefinition.getCPDefinitionId())) {

			return null;
		}

		CPInstance firstAvailableReplacementCPInstance =
			_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
				accountEntry.getAccountEntryId(),
				commerceContext.getCommerceChannelGroupId(), 0, cpInstanceId);

		return new MappedProduct() {
			{
				setActions(mappedProductDTOConverterContext::getActions);
				setAvailability(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return _getAvailability(
							accountEntry.getAccountEntryId(),
							commerceContext.getCommerceChannelGroupId(),
							mappedProductDTOConverterContext.getCompanyId(),
							commerceContext.getCPConfigurationListId(
								cpInstance.getGroupId()),
							cpInstance,
							mappedProductDTOConverterContext.getLocale(),
							cpInstance.getSku(), StringPool.BLANK);
					});
				setFirstAvailableReplacementMappedProduct(
					() -> {
						MappedProduct firstAvailableReplacementMappedProduct =
							null;

						if ((cpInstance != null) &&
							cpInstance.isDiscontinued() &&
							(firstAvailableReplacementCPInstance != null)) {

							mappedProductDTOConverterContext.
								setReplacementCPInstanceId(
									firstAvailableReplacementCPInstance.
										getCPInstanceId());

							CPDefinition firstAvailableReplacementCPDefinition =
								firstAvailableReplacementCPInstance.
									getCPDefinition();

							mappedProductDTOConverterContext.
								setReplacementCProductId(
									firstAvailableReplacementCPDefinition.
										getCProductId());

							firstAvailableReplacementMappedProduct =
								MappedProductDTOConverter.this.toDTO(
									mappedProductDTOConverterContext);
						}

						mappedProductDTOConverterContext.
							setReplacementCPInstanceId(null);
						mappedProductDTOConverterContext.
							setReplacementCProductId(null);

						return firstAvailableReplacementMappedProduct;
					});
				setId(csDiagramEntry::getCSDiagramEntryId);
				setPrice(
					() -> _getPrice(
						commerceContext, cpInstance,
						mappedProductDTOConverterContext.getLocale(),
						BigDecimal.ONE, StringPool.BLANK));
				setProductConfiguration(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return _productConfigurationDTOConverter.toDTO(
							new ProductConfigurationDTOConverterContext(
								mappedProductDTOConverterContext.
									getCommerceContext(),
								cpDefinition.getCPDefinitionId(),
								mappedProductDTOConverterContext.getLocale()));
					});
				setProductExternalReferenceCode(
					() -> {
						if (cpDefinition == null) {
							return StringPool.BLANK;
						}

						CProduct cProduct = cpDefinition.getCProduct();

						return cProduct.getExternalReferenceCode();
					});
				setProductId(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return cpDefinition.getCProductId();
					});
				setProductName(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return LanguageUtils.getLanguageIdMap(
							cpDefinition.getNameMap());
					});
				setProductOptions(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						List<ProductOption> productOptions = new ArrayList<>();

						for (CPDefinitionOptionRel cpDefinitionOptionRel :
								_cpDefinitionOptionRelLocalService.
									getCPDefinitionOptionRels(
										cpDefinition.getCPDefinitionId())) {

							productOptions.add(
								_productOptionDTOConverter.toDTO(
									new DefaultDTOConverterContext(
										cpDefinitionOptionRel.
											getCPDefinitionOptionRelId(),
										mappedProductDTOConverterContext.
											getLocale())));
						}

						return productOptions.toArray(new ProductOption[0]);
					});
				setPurchasable(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.isPurchasable();
					});
				setQuantity(csDiagramEntry::getQuantity);
				setReplacementMappedProduct(
					() -> {
						MappedProduct replacementMappedProduct = null;

						if ((cpInstance != null) &&
							cpInstance.isDiscontinued()) {

							CPInstance replacementCPInstance =
								_cpInstanceHelper.fetchReplacementCPInstance(
									cpInstance.getReplacementCProductId(),
									cpInstance.getReplacementCPInstanceUuid());

							if (replacementCPInstance == null) {
								return null;
							}

							mappedProductDTOConverterContext.
								setReplacementCPInstanceId(
									replacementCPInstance.getCPInstanceId());
							mappedProductDTOConverterContext.
								setReplacementCProductId(
									cpInstance.getReplacementCProductId());

							replacementMappedProduct =
								MappedProductDTOConverter.this.toDTO(
									mappedProductDTOConverterContext);
						}

						mappedProductDTOConverterContext.
							setReplacementCPInstanceId(null);
						mappedProductDTOConverterContext.
							setReplacementCProductId(null);

						return replacementMappedProduct;
					});
				setReplacementMessage(
					() -> {
						if ((cpInstance == null) ||
							!cpInstance.isDiscontinued() ||
							(firstAvailableReplacementCPInstance == null) ||
							(cpInstance.getCPInstanceId() !=
								csDiagramEntry.getCPInstanceId())) {

							return null;
						}

						return _language.format(
							mappedProductDTOConverterContext.getLocale(),
							"x-has-been-replaced-by-x",
							new String[] {
								csDiagramEntry.getSku(),
								firstAvailableReplacementCPInstance.getSku()
							});
					});
				setSequence(csDiagramEntry::getSequence);
				setSku(
					() -> {
						if (cpInstance == null) {
							return csDiagramEntry.getSku();
						}

						return cpInstance.getSku();
					});
				setSkuExternalReferenceCode(
					() -> {
						if (cpInstance == null) {
							return StringPool.BLANK;
						}

						return cpInstance.getExternalReferenceCode();
					});
				setSkuId(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getCPInstanceId();
					});
				setSkuOptions(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						JSONArray jsonArray = CPJSONUtil.toJSONArray(
							_cpDefinitionOptionRelLocalService.
								getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
									cpInstance.getCPInstanceId()));

						return SkuOptionUtil.getSkuOptions(
							_cpInstanceHelper.getCPDefinitionOptionValueRelsMap(
								cpInstance.getCPDefinitionId(),
								jsonArray.toString()),
							_cpInstanceLocalService,
							mappedProductDTOConverterContext.getLocale());
					});
				setThumbnail(
					() -> {
						if (cpDefinition == null) {
							return StringPool.BLANK;
						}

						return cpDefinition.getDefaultImageThumbnailSrc(
							CommerceUtil.getCommerceAccountId(commerceContext));
					});
				setType(
					() -> {
						if (csDiagramEntry.isDiagram()) {
							return MappedProduct.Type.create(
								Type.DIAGRAM.getValue());
						}

						if (csDiagramEntry.getCPInstanceId() > 0) {
							return MappedProduct.Type.create(
								Type.SKU.getValue());
						}

						return MappedProduct.Type.create(
							Type.EXTERNAL.getValue());
					});
				setUrls(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return LanguageUtils.getLanguageIdMap(
							_cpDefinitionLocalService.getUrlTitleMap(
								cpDefinition.getCPDefinitionId()));
					});
			}
		};
	}

	private Availability _getAvailability(
			long accountEntryId, long commerceChannelGroupId, long companyId,
			long cpConfigurationListId, CPInstance cpInstance, Locale locale,
			String sku, String unitOfMeasureKey)
		throws Exception {

		Availability availability = new Availability();

		if (_cpDefinitionInventoryEngine.isDisplayAvailability(
				cpConfigurationListId, cpInstance)) {

			if (Objects.equals(
					_commerceInventoryEngine.getAvailabilityStatus(
						cpInstance.getCompanyId(), accountEntryId,
						cpInstance.getGroupId(), commerceChannelGroupId,
						_cpDefinitionInventoryEngine.getMinStockQuantity(
							cpConfigurationListId, cpInstance),
						cpInstance.getSku(), unitOfMeasureKey),
					CommerceInventoryAvailabilityConstants.AVAILABLE)) {

				availability.setLabel_i18n(
					() -> _language.get(locale, "available"));
				availability.setLabel(() -> "available");
			}
			else {
				availability.setLabel_i18n(
					() -> _language.get(locale, "unavailable"));
				availability.setLabel(() -> "unavailable");
			}
		}

		if (_cpDefinitionInventoryEngine.isDisplayStockQuantity(
				cpConfigurationListId, cpInstance)) {

			availability.setStockQuantity(
				() -> BigDecimalUtil.stripTrailingZeros(
					_commerceInventoryEngine.getStockQuantity(
						companyId, accountEntryId, cpInstance.getGroupId(),
						commerceChannelGroupId, sku, unitOfMeasureKey)));
		}

		return availability;
	}

	private String[] _getFormattedDiscountPercentages(
			BigDecimal[] discountPercentages, Locale locale)
		throws Exception {

		List<String> formattedDiscountPercentages = new ArrayList<>();

		for (BigDecimal percentage : discountPercentages) {
			formattedDiscountPercentages.add(
				_commercePriceFormatter.format(percentage, locale));
		}

		return formattedDiscountPercentages.toArray(new String[0]);
	}

	private Price _getPrice(
			CommerceContext commerceContext, CPInstance cpInstance,
			Locale locale, BigDecimal quantity, String unitOfMeasureKey)
		throws Exception {

		if (cpInstance == null) {
			return null;
		}

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, true, unitOfMeasureKey,
				commerceContext);

		if (commerceProductPrice == null) {
			return new Price();
		}

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		CommerceMoney unitPriceCommerceMoney =
			commerceProductPrice.getUnitPrice();

		Price price = new Price() {
			{
				setCurrency(() -> commerceCurrency.getName(locale));
				setPrice(
					() -> {
						BigDecimal unitPrice =
							unitPriceCommerceMoney.getPrice();

						return unitPrice.doubleValue();
					});
				setPriceFormatted(() -> unitPriceCommerceMoney.format(locale));
			}
		};

		CommerceMoney unitPromoPriceCommerceMoney =
			commerceProductPrice.getUnitPromoPrice();

		BigDecimal unitPromoPrice = unitPromoPriceCommerceMoney.getPrice();

		if ((unitPromoPrice != null) &&
			(unitPromoPrice.compareTo(BigDecimal.ZERO) > 0) &&
			(unitPromoPrice.compareTo(unitPriceCommerceMoney.getPrice()) < 0)) {

			price.setPromoPrice(unitPromoPrice::doubleValue);
			price.setPromoPriceFormatted(
				() -> unitPromoPriceCommerceMoney.format(locale));
		}

		CommerceDiscountValue discountValue =
			commerceProductPrice.getDiscountValue();

		if (discountValue != null) {
			CommerceMoney discountAmountCommerceMoney =
				discountValue.getDiscountAmount();

			price.setDiscount(() -> discountAmountCommerceMoney.format(locale));

			price.setDiscountPercentage(
				() -> _commercePriceFormatter.format(
					discountValue.getDiscountPercentage(), locale));
			price.setDiscountPercentages(
				() -> _getFormattedDiscountPercentages(
					discountValue.getPercentages(), locale));

			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			price.setFinalPrice(() -> finalPriceCommerceMoney.format(locale));
		}

		return price;
	}

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionInventoryEngine _cpDefinitionInventoryEngine;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinitionInventory, ProductConfiguration>
		_productConfigurationDTOConverter;

	@Reference(target = DTOConverterConstants.PRODUCT_OPTION_DTO_CONVERTER)
	private DTOConverter<CPDefinitionOptionRel, ProductOption>
		_productOptionDTOConverter;

}