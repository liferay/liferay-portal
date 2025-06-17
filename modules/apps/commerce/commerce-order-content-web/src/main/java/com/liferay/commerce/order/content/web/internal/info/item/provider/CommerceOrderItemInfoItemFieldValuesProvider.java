/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info.item.provider;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.content.web.internal.info.CommerceOrderItemInfoItemFields;
import com.liferay.commerce.order.content.web.internal.util.CommerceOrderItemUtil;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemFieldValuesProvider.class)
public class CommerceOrderItemInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<CommerceOrderItem> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
		CommerceOrderItem commerceOrderItem) {

		return InfoItemFieldValues.builder(
		).infoFieldValues(
			_getCommerceOrderItemInfoFieldValues(commerceOrderItem)
		).infoFieldValues(
			_expandoInfoItemFieldSetProvider.getInfoFieldValues(
				CommerceOrderItem.class.getName(), commerceOrderItem)
		).infoFieldValues(
			_templateInfoItemFieldSetProvider.getInfoFieldValues(
				CommerceOrderItem.class.getName(), commerceOrderItem)
		).infoFieldValues(
			_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
				CommerceOrderItem.class.getName(), commerceOrderItem)
		).infoItemReference(
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				commerceOrderItem.getCommerceOrderItemId())
		).build();
	}

	private List<InfoFieldValue<Object>> _getCommerceOrderItemInfoFieldValues(
		CommerceOrderItem commerceOrderItem) {

		List<InfoFieldValue<Object>> commerceOrderItemInfoFieldValues =
			new ArrayList<>();

		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.companyIdInfoField,
				commerceOrderItem.getCompanyId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.createDateInfoField,
				commerceOrderItem.getCreateDate()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.defaultLanguageIdInfoField,
				commerceOrderItem.getDefaultLanguageId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.groupIdInfoField,
				commerceOrderItem.getGroupId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.modifiedDateInfoField,
				commerceOrderItem.getModifiedDate()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.nameInfoField,
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						commerceOrderItem.getDefaultLanguageId())
				).values(
					commerceOrderItem.getNameMap()
				).build()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.orderIdInfoField,
				commerceOrderItem.getCommerceOrderId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.orderItemIdInfoField,
				commerceOrderItem.getCommerceOrderItemId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.quantityInfoField,
				commerceOrderItem.getQuantity()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.skuInfoField,
				commerceOrderItem.getSku()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.stagedModelTypeInfoField,
				commerceOrderItem.getStagedModelType()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.userIdInfoField,
				commerceOrderItem.getUserId()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.userNameInfoField,
				commerceOrderItem.getUserName()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.userUuidInfoField,
				commerceOrderItem.getUserUuid()));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.uuidInfoField,
				commerceOrderItem.getUuid()));

		String discountAmount = StringPool.BLANK;
		String options = StringPool.BLANK;
		String promoPrice = StringPool.BLANK;
		String thumbnailURL = StringPool.BLANK;
		String totalPrice = StringPool.BLANK;
		String unitPrice = StringPool.BLANK;
		String url = StringPool.BLANK;

		try {
			ThemeDisplay themeDisplay = _getThemeDisplay();

			Locale locale = themeDisplay.getLocale();

			if (commerceOrderItem.getParentCommerceOrderItemId() == 0) {
				options = CommerceOrderItemUtil.getOptions(
					commerceOrderItem, _cpInstanceHelper, locale);
			}

			commerceOrderItemInfoFieldValues.add(
				new InfoFieldValue<>(
					CommerceOrderItemInfoItemFields.optionsInfoField, options));

			CommerceOrderItemPrice commerceOrderItemPrice =
				_getCommerceOrderItemPrice(commerceOrderItem);

			discountAmount = CommerceOrderItemUtil.formatDiscountAmount(
				commerceOrderItemPrice, locale);
			promoPrice = CommerceOrderItemUtil.formatPromoPrice(
				commerceOrderItemPrice, locale);
			totalPrice = CommerceOrderItemUtil.formatTotalPrice(
				commerceOrderItemPrice, locale);
			unitPrice = CommerceOrderItemUtil.formatUnitPrice(
				commerceOrderItemPrice, _language, locale);

			CPInstance cpInstance = _cpInstanceService.fetchCPInstance(
				commerceOrderItem.getCPInstanceId());

			if (cpInstance != null) {
				thumbnailURL = _cpDefinitionHelper.getDefaultImageFileURL(
					CommerceUtil.getCommerceAccountId(
						CommerceContextThreadLocal.get()),
					cpInstance.getCPDefinitionId());
				url = _cpDefinitionHelper.getFriendlyURL(
					cpInstance.getCPDefinitionId(), themeDisplay);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.discountAmountInfoField,
				discountAmount));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.promoPriceInfoField,
				promoPrice));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.thumbnailURLInfoField,
				thumbnailURL));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.totalPriceInfoField,
				totalPrice));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.unitPriceInfoField, unitPrice));
		commerceOrderItemInfoFieldValues.add(
			new InfoFieldValue<>(
				CommerceOrderItemInfoItemFields.URLInfoField, url));

		return commerceOrderItemInfoFieldValues;
	}

	private CommerceOrderItemPrice _getCommerceOrderItemPrice(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		return _commerceOrderPriceCalculation.getCommerceOrderItemPrice(
			commerceOrder.getCommerceCurrency(), commerceOrderItem);
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemInfoItemFieldValuesProvider.class);

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private ExpandoInfoItemFieldSetProvider _expandoInfoItemFieldSetProvider;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private Language _language;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

}