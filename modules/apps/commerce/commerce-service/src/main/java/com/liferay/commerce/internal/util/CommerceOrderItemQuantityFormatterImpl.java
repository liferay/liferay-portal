/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.util;

import com.liferay.commerce.configuration.CommerceOrderItemDecimalQuantityConfiguration;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.exception.CPDefinitionInventoryQuantityException;
import com.liferay.commerce.exception.CommerceOrderItemQuantityException;
import com.liferay.commerce.exception.CommerceShipmentItemQuantityException;
import com.liferay.commerce.inventory.exception.CommerceInventoryReplenishmentQuantityException;
import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryQuantityException;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.product.exception.CPConfigurationEntryQuantityException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelQuantityException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureQuantityException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.configuration.CommerceOrderItemDecimalQuantityConfiguration",
	service = CommerceOrderItemQuantityFormatter.class
)
public class CommerceOrderItemQuantityFormatterImpl
	implements CommerceOrderItemQuantityFormatter {

	@Override
	public String format(
			CommerceOrderItem commerceOrderItem,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure, Locale locale)
		throws PortalException {

		CPMeasurementUnit cpMeasurementUnit =
			commerceOrderItem.fetchCPMeasurementUnit();

		DecimalFormat decimalFormat = _getDecimalFormat(true, true, locale);
		BigDecimal quantity = commerceOrderItem.getQuantity();

		if (cpMeasurementUnit != null) {
			return StringBundler.concat(
				decimalFormat.format(quantity), StringPool.SPACE,
				cpMeasurementUnit.getName(locale));
		}

		if (cpInstanceUnitOfMeasure != null) {
			return decimalFormat.format(
				quantity.setScale(
					cpInstanceUnitOfMeasure.getPrecision(),
					RoundingMode.HALF_UP));
		}

		return decimalFormat.format(quantity);
	}

	@Override
	public String format(CommerceOrderItem commerceOrderItem, Locale locale)
		throws PortalException {

		DecimalFormat decimalFormat = _getDecimalFormat(true, false, locale);

		return decimalFormat.format(commerceOrderItem.getQuantity());
	}

	@Override
	public BigDecimal parse(
			ActionRequest actionRequest, String className, String param)
		throws Exception {

		String quantity = ParamUtil.getString(
			actionRequest, param, BigDecimal.ZERO.toString());

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return parse(className, quantity, themeDisplay.getLocale());
	}

	@Override
	public BigDecimal parse(String className, String quantity, Locale locale)
		throws Exception {

		if (Validator.isNull(quantity)) {
			quantity = BigDecimal.ZERO.toString();
		}

		_validateQuantity(className, quantity);

		DecimalFormatSymbols decimalFormatSymbols =
			DecimalFormatSymbols.getInstance(locale);

		if (Objects.equals(
				decimalFormatSymbols.getDecimalSeparator(), CharPool.PERIOD) &&
			_hasCommaDecimalPattern(quantity)) {

			quantity = StringUtil.replace(
				quantity, CharPool.PERIOD, StringPool.BLANK);

			quantity = StringUtil.replace(
				quantity, CharPool.COMMA, CharPool.PERIOD);
		}
		else if ((Objects.equals(
					decimalFormatSymbols.getDecimalSeparator(),
					CharPool.COMMA) ||
				  Objects.equals(
					  decimalFormatSymbols.getDecimalSeparator(),
					  CharPool.ARABIC_DECIMAL_SEPARATOR)) &&
				 _hasPeriodDecimalPattern(quantity)) {

			quantity = StringUtil.replace(
				quantity, CharPool.COMMA, StringPool.BLANK);

			quantity = StringUtil.replace(
				quantity, CharPool.PERIOD, CharPool.COMMA);
		}

		DecimalFormat decimalFormat = _getDecimalFormat(true, true, locale);

		return (BigDecimal)decimalFormat.parse(quantity);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_commerceOrderItemDecimalQuantityConfiguration =
			ConfigurableUtil.createConfigurable(
				CommerceOrderItemDecimalQuantityConfiguration.class,
				properties);
	}

	@Deactivate
	protected void deactivate() {
		_commerceOrderItemDecimalQuantityConfiguration = null;
	}

	private DecimalFormat _getDecimalFormat(
		boolean maximumFractionDigits, boolean minimumFractionDigits,
		Locale locale) {

		DecimalFormat decimalFormat = new DecimalFormat(
			CommerceOrderConstants.DECIMAL_FORMAT_PATTERN,
			DecimalFormatSymbols.getInstance(locale));

		if (maximumFractionDigits) {
			decimalFormat.setMaximumFractionDigits(
				_commerceOrderItemDecimalQuantityConfiguration.
					maximumFractionDigits());
		}

		if (minimumFractionDigits) {
			decimalFormat.setMinimumFractionDigits(
				_commerceOrderItemDecimalQuantityConfiguration.
					minimumFractionDigits());
		}

		decimalFormat.setParseBigDecimal(true);
		decimalFormat.setRoundingMode(
			_commerceOrderItemDecimalQuantityConfiguration.roundingMode());

		return decimalFormat;
	}

	private boolean _hasCommaDecimalPattern(String quantity) {
		Matcher matcher = _commaDecimalPattern.matcher(quantity);

		return matcher.find();
	}

	private boolean _hasPeriodDecimalPattern(String quantity) {
		Matcher matcher = _periodDecimalPattern.matcher(quantity);

		return matcher.find();
	}

	private void _validateQuantity(String className, String quantity)
		throws Exception {

		if (Validator.isNull(className) || _hasCommaDecimalPattern(quantity) ||
			_hasPeriodDecimalPattern(quantity)) {

			return;
		}

		if (Objects.equals(
				className,
				CommerceInventoryReplenishmentItem.class.getName())) {

			throw new CommerceInventoryReplenishmentQuantityException();
		}
		else if (Objects.equals(
					className,
					CommerceInventoryWarehouseItem.class.getName())) {

			throw new CommerceInventoryWarehouseItemQuantityException();
		}
		else if (Objects.equals(className, CommerceOrderItem.class.getName())) {
			throw new CommerceOrderItemQuantityException();
		}
		else if (Objects.equals(
					className, CommerceShipmentItem.class.getName())) {

			throw new CommerceShipmentItemQuantityException();
		}
		else if (Objects.equals(
					className, CommerceTierPriceEntry.class.getName())) {

			throw new CommerceTierPriceEntryQuantityException();
		}
		else if (Objects.equals(
					className, CPConfigurationEntry.class.getName())) {

			throw new CPConfigurationEntryQuantityException();
		}
		else if (Objects.equals(
					className, CPDefinitionInventory.class.getName())) {

			throw new CPDefinitionInventoryQuantityException();
		}
		else if (Objects.equals(
					className, CPDefinitionOptionValueRel.class.getName())) {

			throw new CPDefinitionOptionValueRelQuantityException();
		}
		else if (Objects.equals(
					className, CPInstanceUnitOfMeasure.class.getName())) {

			throw new CPInstanceUnitOfMeasureQuantityException();
		}
	}

	private static final Pattern _commaDecimalPattern = Pattern.compile(
		"^\\d{1,3}(?:\\.\\d{3})*(?:,\\d+)?$|^\\d+(?:,\\d+)?$");
	private static final Pattern _periodDecimalPattern = Pattern.compile(
		"((^\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?$)|(^\\d{1,2}(?:,\\d{2})*" +
			"(?:,\\d{3})(?:\\.\\d+)?$))|^\\d+(?:\\.\\d+)?$");

	private volatile CommerceOrderItemDecimalQuantityConfiguration
		_commerceOrderItemDecimalQuantityConfiguration;

}