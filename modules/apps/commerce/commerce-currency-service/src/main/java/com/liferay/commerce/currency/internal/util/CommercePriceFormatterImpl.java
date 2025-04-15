/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.util;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;

import com.liferay.commerce.currency.configuration.RoundingTypeConfiguration;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.exception.CommerceDiscountAmountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.exception.CommerceOrderItemPriceException;
import com.liferay.commerce.exception.CommerceOrderPriceException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.payment.exception.CommercePaymentEntryAmountException;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.price.list.exception.CommercePriceEntryPriceException;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryPriceException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.pricing.exception.CommercePriceModifierAmountException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelPriceException;
import com.liferay.commerce.product.exception.CPInstancePriceException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasurePriceException;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionAmountException;
import com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionRelPriceException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.petra.string.CharPool;
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
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
@Component(
	configurationPid = "com.liferay.commerce.currency.configuration.RoundingTypeConfiguration",
	service = CommercePriceFormatter.class
)
public class CommercePriceFormatterImpl implements CommercePriceFormatter {

	@Override
	public String format(BigDecimal price, Locale locale)
		throws PortalException {

		if (price == null) {
			return StringPool.BLANK;
		}

		DecimalFormat decimalFormat = _getDecimalFormat(null, locale);

		return decimalFormat.format(price);
	}

	@Override
	public String format(
			CommerceCurrency commerceCurrency, BigDecimal price, Locale locale)
		throws PortalException {

		DecimalFormat decimalFormat = _getDecimalFormat(
			commerceCurrency, locale);

		return decimalFormat.format(price);
	}

	@Override
	public String formatAsRelative(
		CommerceCurrency commerceCurrency, BigDecimal relativePrice,
		Locale locale) {

		if (relativePrice.signum() == 0) {
			return StringPool.BLANK;
		}

		DecimalFormat decimalFormat = _getDecimalFormat(
			commerceCurrency, locale);

		if (relativePrice.signum() == -1) {
			return String.format(
				"%2s %s", StringPool.MINUS,
				decimalFormat.format(relativePrice.negate()));
		}

		return String.format(
			"%2s %s", StringPool.PLUS, decimalFormat.format(relativePrice));
	}

	@Override
	public BigDecimal parse(
			ActionRequest actionRequest, boolean allowNegativeValue,
			String className, String param)
		throws Exception {

		String price = ParamUtil.getString(
			actionRequest, param, BigDecimal.ZERO.toString());

		if (param.equals("rate")) {
			price = ParamUtil.getString(actionRequest, "rate");

			if (Validator.isNull(price)) {
				price = BigDecimal.ONE.toString();
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return new BigDecimal(
			parse(
				allowNegativeValue, className, price,
				themeDisplay.getLocale()));
	}

	@Override
	public String parse(
			boolean allowNegativeValue, String className, String price,
			Locale locale)
		throws Exception {

		if (Validator.isNull(price)) {
			price = BigDecimal.ZERO.toString();
		}

		_validatePrice(allowNegativeValue, className, price);

		DecimalFormatSymbols decimalFormatSymbols =
			DecimalFormatSymbols.getInstance(locale);

		if (Objects.equals(
				decimalFormatSymbols.getDecimalSeparator(), CharPool.PERIOD) &&
			_hasCommaDecimalPattern(allowNegativeValue, price)) {

			price = StringUtil.replace(
				price, CharPool.PERIOD, StringPool.BLANK);

			price = StringUtil.replace(price, CharPool.COMMA, CharPool.PERIOD);
		}
		else if ((Objects.equals(
					decimalFormatSymbols.getDecimalSeparator(),
					CharPool.COMMA) ||
				  Objects.equals(
					  decimalFormatSymbols.getDecimalSeparator(),
					  CharPool.ARABIC_DECIMAL_SEPARATOR)) &&
				 _hasPeriodDecimalPattern(allowNegativeValue, price)) {

			price = StringUtil.replace(price, CharPool.COMMA, StringPool.BLANK);

			price = StringUtil.replace(price, CharPool.PERIOD, CharPool.COMMA);
		}

		DecimalFormat decimalFormat = _getDecimalFormat(null, locale);

		return decimalFormat.parse(
			price
		).toString();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_roundingTypeConfiguration = ConfigurableUtil.createConfigurable(
			RoundingTypeConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		_roundingTypeConfiguration = null;
	}

	private DecimalFormat _getDecimalFormat(
		CommerceCurrency commerceCurrency, Locale locale) {

		String formatPattern = CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN;
		int maxFractionDigits =
			_roundingTypeConfiguration.maximumFractionDigits();
		int minFractionDigits =
			_roundingTypeConfiguration.minimumFractionDigits();
		RoundingMode roundingMode = _roundingTypeConfiguration.roundingMode();

		if (commerceCurrency != null) {
			formatPattern = commerceCurrency.getFormatPattern(locale);

			if (Validator.isNull(formatPattern)) {
				formatPattern = commerceCurrency.getFormatPattern(
					commerceCurrency.getDefaultLanguageId());
			}

			maxFractionDigits = commerceCurrency.getMaxFractionDigits();
			minFractionDigits = commerceCurrency.getMinFractionDigits();
			roundingMode = RoundingMode.valueOf(
				commerceCurrency.getRoundingMode());
		}

		DecimalFormat decimalFormat = new DecimalFormat(
			formatPattern, DecimalFormatSymbols.getInstance(locale));

		decimalFormat.setMaximumFractionDigits(maxFractionDigits);
		decimalFormat.setMinimumFractionDigits(minFractionDigits);
		decimalFormat.setParseBigDecimal(true);
		decimalFormat.setRoundingMode(roundingMode.ordinal());

		return decimalFormat;
	}

	private boolean _hasCommaDecimalPattern(
		boolean allowNegativeValue, String price) {

		Matcher matcher = null;

		if (allowNegativeValue) {
			matcher = _negativeCommaDecimalPattern.matcher(price);
		}
		else {
			matcher = _commaDecimalPattern.matcher(price);
		}

		return matcher.find();
	}

	private boolean _hasPeriodDecimalPattern(
		boolean allowNegativeValue, String price) {

		Matcher matcher = null;

		if (allowNegativeValue) {
			matcher = _negativePeriodDecimalPattern.matcher(price);
		}
		else {
			matcher = _periodDecimalPattern.matcher(price);
		}

		return matcher.find();
	}

	private void _validatePrice(
			boolean allowNegativeValue, String className, String price)
		throws Exception {

		if (Validator.isNull(className) ||
			_hasCommaDecimalPattern(allowNegativeValue, price) ||
			_hasPeriodDecimalPattern(allowNegativeValue, price)) {

			return;
		}

		if (Objects.equals(className, CommerceDiscount.class.getName())) {
			throw new CommerceDiscountAmountException();
		}
		else if (Objects.equals(className, CommerceOrder.class.getName())) {
			throw new CommerceOrderPriceException();
		}
		else if (Objects.equals(className, CommerceOrderItem.class.getName())) {
			throw new CommerceOrderItemPriceException();
		}
		else if (Objects.equals(
					className, CommercePaymentEntry.class.getName())) {

			throw new CommercePaymentEntryAmountException();
		}
		else if (Objects.equals(
					className, CommercePriceEntry.class.getName())) {

			throw new CommercePriceEntryPriceException();
		}
		else if (Objects.equals(
					className, CommercePriceModifier.class.getName())) {

			throw new CommercePriceModifierAmountException();
		}
		else if (Objects.equals(
					className, CommerceShippingFixedOption.class.getName())) {

			throw new CommerceShippingFixedOptionAmountException();
		}
		else if (Objects.equals(
					className,
					CommerceShippingFixedOptionRel.class.getName())) {

			throw new CommerceShippingFixedOptionRelPriceException();
		}
		else if (Objects.equals(
					className, CommerceTierPriceEntry.class.getName())) {

			throw new CommerceTierPriceEntryPriceException();
		}
		else if (Objects.equals(
					className, CPDefinitionOptionValueRel.class.getName())) {

			throw new CPDefinitionOptionValueRelPriceException();
		}
		else if (Objects.equals(className, CPInstance.class.getName())) {
			throw new CPInstancePriceException();
		}
		else if (Objects.equals(
					className, CPInstanceUnitOfMeasure.class.getName())) {

			throw new CPInstanceUnitOfMeasurePriceException();
		}
	}

	private static final Pattern _commaDecimalPattern = Pattern.compile(
		"^\\d{1,3}(?:\\.\\d{3})*(?:,\\d+)?$|^\\d+(?:,\\d+)?$");
	private static final Pattern _negativeCommaDecimalPattern = Pattern.compile(
		"^-?\\d{1,3}(?:\\.\\d{3})*(?:,\\d+)?$|^-?\\d+(?:,\\d+)?$");
	private static final Pattern _negativePeriodDecimalPattern =
		Pattern.compile(
			"((^-?\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?$)|(^-?\\d{1,2}(?:,\\d{2})*" +
				"(?:,\\d{3})(?:\\.\\d+)?$))|^-?\\d+(?:\\.\\d+)?$");
	private static final Pattern _periodDecimalPattern = Pattern.compile(
		"((^\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?$)|(^\\d{1,2}(?:,\\d{2})*" +
			"(?:,\\d{3})(?:\\.\\d+)?$))|^\\d+(?:\\.\\d+)?$");

	private volatile RoundingTypeConfiguration _roundingTypeConfiguration;

}