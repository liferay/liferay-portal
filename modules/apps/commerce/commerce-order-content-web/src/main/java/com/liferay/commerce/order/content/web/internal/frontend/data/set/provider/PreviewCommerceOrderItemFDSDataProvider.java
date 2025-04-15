/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.configuration.CommerceOrderImporterDateFormatConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.model.PreviewOrderItem;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PREVIEW_ORDER_ITEMS,
	service = FDSDataProvider.class
)
public class PreviewCommerceOrderItemFDSDataProvider
	implements FDSDataProvider<PreviewOrderItem> {

	@Override
	public List<PreviewOrderItem> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		try {
			_commerceOrderImporterItems = _getCommerceOrderImporterItems(
				httpServletRequest, fdsPagination);
		}
		finally {
			IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
		}

		if (_commerceOrderImporterItems == null) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long groupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				themeDisplay.getSiteGroupId());

		CommerceOrderImporterDateFormatConfiguration
			commerceOrderImporterDateFormatConfiguration =
				_configurationProvider.getConfiguration(
					CommerceOrderImporterDateFormatConfiguration.class,
					new GroupServiceSettingsLocator(
						groupId,
						CommerceConstants.
							SERVICE_NAME_COMMERCE_ORDER_IMPORTER_DATE_FORMAT));

		IntegerWrapper integerWrapper = new IntegerWrapper();
		Locale locale = _portal.getLocale(httpServletRequest);

		return TransformUtil.transform(
			_commerceOrderImporterItems,
			commerceOrderImporterItem -> {
				String externalReferenceCode = StringPool.BLANK;

				CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
					commerceOrderImporterItem.getCPInstanceId());

				if (cpInstance != null) {
					externalReferenceCode =
						cpInstance.getExternalReferenceCode();
				}

				CommerceOrderItemPrice commerceOrderItemPrice =
					commerceOrderImporterItem.getCommerceOrderItemPrice();

				BigDecimal quantity = _commerceQuantityFormatter.format(
					commerceOrderImporterItem.getCPInstanceId(),
					commerceOrderImporterItem.getQuantity(),
					commerceOrderImporterItem.getUnitOfMeasureKey());

				return new PreviewOrderItem(
					externalReferenceCode,
					_getImportStatus(commerceOrderImporterItem, locale),
					_getCommerceOrderOptions(commerceOrderImporterItem, locale),
					commerceOrderImporterItem.getName(locale), quantity,
					commerceOrderImporterItem.getReplacingSKU(),
					_formatImportDate(
						commerceOrderImporterItem.
							getRequestedDeliveryDateString(),
						commerceOrderImporterDateFormatConfiguration.
							orderImporterDateFormat(),
						themeDisplay.getLocale()),
					integerWrapper.increment(),
					commerceOrderImporterItem.getSKU(),
					_formatFinalPrice(commerceOrderItemPrice, quantity, locale),
					commerceOrderImporterItem.getUnitOfMeasureKey(),
					_formatUnitPrice(commerceOrderItemPrice, locale));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		try {
			CommerceOrderImporterType commerceOrderImporterType =
				_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
					ParamUtil.getString(
						httpServletRequest, "commerceOrderImporterTypeKey"));

			if (commerceOrderImporterType == null) {
				return 0;
			}

			Object commerceOrderImporterItem =
				commerceOrderImporterType.getCommerceOrderImporterItem(
					httpServletRequest);

			if (commerceOrderImporterItem == null) {
				return 0;
			}

			return commerceOrderImporterType.getCommerceOrderImporterItemsCount(
				commerceOrderImporterItem);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return 0;
	}

	private String _formatFinalPrice(
		CommerceOrderItemPrice commerceOrderItemPrice, BigDecimal quantity,
		Locale locale) {

		if ((commerceOrderItemPrice == null) ||
			(commerceOrderItemPrice.getUnitPrice() == null)) {

			return StringPool.BLANK;
		}

		if (commerceOrderItemPrice.isPriceOnApplication()) {
			return StringPool.DASH;
		}

		CommerceMoney unitPriceCommerceMoney =
			commerceOrderItemPrice.getUnitPrice();

		BigDecimal unitPrice = unitPriceCommerceMoney.getPrice();

		BigDecimal finalPrice = unitPrice.multiply(quantity);

		try {
			return _commercePriceFormatter.format(
				unitPriceCommerceMoney.getCommerceCurrency(), finalPrice,
				locale);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	private String _formatImportDate(
		String requestedDeliveryDate, String requestedDeliveryDateFormat,
		Locale locale) {

		try {
			if (Validator.isNull(requestedDeliveryDate)) {
				return StringPool.BLANK;
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				requestedDeliveryDateFormat);

			Date formattedRequestedDeliveryDate = simpleDateFormat.parse(
				requestedDeliveryDate);

			return simpleDateFormat.format(formattedRequestedDeliveryDate);
		}
		catch (IllegalArgumentException | ParseException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return _language.get(locale, "request-delivery-date-invalid");
		}
	}

	private String _formatUnitPrice(
		CommerceOrderItemPrice commerceOrderItemPrice, Locale locale) {

		if ((commerceOrderItemPrice == null) ||
			(commerceOrderItemPrice.getUnitPrice() == null)) {

			return StringPool.BLANK;
		}

		if (commerceOrderItemPrice.isPriceOnApplication()) {
			return _language.get(
				locale,
				CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION);
		}

		CommerceMoney unitPriceCommerceMoney =
			commerceOrderItemPrice.getUnitPrice();

		try {
			return unitPriceCommerceMoney.format(locale);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	private long _getCommerceOptionValueCPDefinitionId(
		CommerceOrderImporterItem commerceOrderImporterItem) {

		if (!commerceOrderImporterItem.hasParentCommerceOrderItem()) {
			return commerceOrderImporterItem.getCPDefinitionId();
		}

		return commerceOrderImporterItem.
			getParentCommerceOrderItemCPDefinitionId();
	}

	private List<CommerceOrderImporterItem> _getCommerceOrderImporterItems(
		HttpServletRequest httpServletRequest, FDSPagination fdsPagination) {

		try {
			CommerceOrderImporterType commerceOrderImporterType =
				_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
					ParamUtil.getString(
						httpServletRequest, "commerceOrderImporterTypeKey"));

			if (commerceOrderImporterType == null) {
				return null;
			}

			Object commerceOrderImporterItem =
				commerceOrderImporterType.getCommerceOrderImporterItem(
					httpServletRequest);

			if (commerceOrderImporterItem == null) {
				return null;
			}

			return commerceOrderImporterType.getCommerceOrderImporterItems(
				_commerceOrderService.getCommerceOrder(
					ParamUtil.getLong(httpServletRequest, "commerceOrderId")),
				fdsPagination, commerceOrderImporterItem);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private String _getCommerceOrderOptions(
			CommerceOrderImporterItem commerceOrderImporterItem, Locale locale)
		throws PortalException {

		StringJoiner stringJoiner = new StringJoiner(
			StringPool.COMMA_AND_SPACE);

		List<KeyValuePair> commerceOptionValueKeyValuePairs =
			_cpInstanceHelper.getKeyValuePairs(
				_getCommerceOptionValueCPDefinitionId(
					commerceOrderImporterItem),
				commerceOrderImporterItem.getJSON(), locale);

		for (KeyValuePair keyValuePair : commerceOptionValueKeyValuePairs) {
			stringJoiner.add(keyValuePair.getValue());
		}

		return stringJoiner.toString();
	}

	private String _getImportStatus(
		CommerceOrderImporterItem commerceOrderImporterItem, Locale locale) {

		if (commerceOrderImporterItem.isValid()) {
			return _language.get(locale, "ok");
		}

		String[] errorMessages = commerceOrderImporterItem.getErrorMessages();

		return errorMessages[0];
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PreviewCommerceOrderItemFDSDataProvider.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	private List<CommerceOrderImporterItem> _commerceOrderImporterItems;

	@Reference
	private CommerceOrderImporterTypeRegistry
		_commerceOrderImporterTypeRegistry;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}