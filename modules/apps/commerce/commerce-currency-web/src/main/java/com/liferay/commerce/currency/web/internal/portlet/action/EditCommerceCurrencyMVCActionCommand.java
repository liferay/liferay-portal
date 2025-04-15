/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.web.internal.portlet.action;

import com.liferay.commerce.currency.configuration.CommerceCurrencyConfiguration;
import com.liferay.commerce.currency.constants.CommerceCurrencyExchangeRateConstants;
import com.liferay.commerce.currency.constants.CommerceCurrencyPortletKeys;
import com.liferay.commerce.currency.exception.CommerceCurrencyCodeException;
import com.liferay.commerce.currency.exception.CommerceCurrencyFractionDigitsException;
import com.liferay.commerce.currency.exception.CommerceCurrencyNameException;
import com.liferay.commerce.currency.exception.DuplicateCommerceCurrencyException;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
		"mvc.command.name=/commerce_currency/edit_commerce_currency"
	},
	service = MVCActionCommand.class
)
public class EditCommerceCurrencyMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceCurrencies(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCommerceCurrency(actionRequest);
			}
			else if (cmd.equals("setActive")) {
				_setActive(actionRequest);
			}
			else if (cmd.equals("setPrimary")) {
				_setPrimary(actionRequest);
			}
			else if (cmd.equals("updateExchangeRates")) {
				updateExchangeRates(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCurrencyException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceCurrencyCodeException ||
					 exception instanceof
						 CommerceCurrencyFractionDigitsException ||
					 exception instanceof CommerceCurrencyNameException ||
					 exception instanceof DuplicateCommerceCurrencyException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/commerce_currency/edit_commerce_currency");
			}
			else {
				throw exception;
			}
		}
	}

	protected void updateExchangeRates(ActionRequest actionRequest)
		throws Exception {

		long[] updateCommerceCurrencyExchangeRateIds = null;

		long commerceCurrencyId = ParamUtil.getLong(
			actionRequest, "commerceCurrencyId");

		if (commerceCurrencyId > 0) {
			updateCommerceCurrencyExchangeRateIds = new long[] {
				commerceCurrencyId
			};
		}
		else {
			updateCommerceCurrencyExchangeRateIds = ParamUtil.getLongValues(
				actionRequest, "id");
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceCurrency.class.getName(), actionRequest);

		CommerceCurrencyConfiguration commerceCurrencyConfiguration =
			_configurationProvider.getConfiguration(
				CommerceCurrencyConfiguration.class,
				new CompanyServiceSettingsLocator(
					serviceContext.getCompanyId(),
					CommerceCurrencyExchangeRateConstants.SERVICE_NAME));

		String exchangeRateProviderKey =
			commerceCurrencyConfiguration.defaultExchangeRateProviderKey();

		for (long updateCommerceCurrencyExchangeRateId :
				updateCommerceCurrencyExchangeRateIds) {

			_commerceCurrencyService.updateExchangeRate(
				updateCommerceCurrencyExchangeRateId, exchangeRateProviderKey);
		}
	}

	private void _deleteCommerceCurrencies(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceCurrencyIds = null;

		long commerceCurrencyId = ParamUtil.getLong(
			actionRequest, "commerceCurrencyId");

		if (commerceCurrencyId > 0) {
			deleteCommerceCurrencyIds = new long[] {commerceCurrencyId};
		}
		else {
			deleteCommerceCurrencyIds = ParamUtil.getLongValues(
				actionRequest, "id");
		}

		for (long deleteCommerceCurrencyId : deleteCommerceCurrencyIds) {
			_commerceCurrencyService.deleteCommerceCurrency(
				deleteCommerceCurrencyId);
		}
	}

	private void _setActive(ActionRequest actionRequest)
		throws PortalException {

		long commerceCurrencyId = ParamUtil.getLong(
			actionRequest, "commerceCurrencyId");

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.getCommerceCurrency(commerceCurrencyId);

		_commerceCurrencyService.setActive(
			commerceCurrencyId, !commerceCurrency.isActive());
	}

	private void _setPrimary(ActionRequest actionRequest)
		throws PortalException {

		long commerceCurrencyId = ParamUtil.getLong(
			actionRequest, "commerceCurrencyId");

		boolean primary = ParamUtil.getBoolean(actionRequest, "primary");

		_commerceCurrencyService.setPrimary(commerceCurrencyId, primary);
	}

	private CommerceCurrency _updateCommerceCurrency(
			ActionRequest actionRequest)
		throws PortalException {

		int maxFractionDigits = ParamUtil.getInteger(
			actionRequest, "maxFractionDigits");
		int minFractionDigits = ParamUtil.getInteger(
			actionRequest, "minFractionDigits");

		if ((maxFractionDigits < 0) || (minFractionDigits < 0) ||
			(maxFractionDigits < minFractionDigits)) {

			throw new CommerceCurrencyFractionDigitsException();
		}

		long commerceCurrencyId = ParamUtil.getLong(
			actionRequest, "commerceCurrencyId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		String rate = ParamUtil.getString(actionRequest, "rate");
		Map<Locale, String> formatPatternMap = _localization.getLocalizationMap(
			actionRequest, "formatPattern");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");
		String roundingMode = ParamUtil.getString(
			actionRequest, "roundingMode");
		boolean primary = ParamUtil.getBoolean(actionRequest, "primary");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		String symbol = ParamUtil.getString(actionRequest, "symbol");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		CommerceCurrency commerceCurrency = null;

		if (commerceCurrencyId <= 0) {
			String code = ParamUtil.getString(actionRequest, "code");

			commerceCurrency = _commerceCurrencyService.addCommerceCurrency(
				externalReferenceCode, code, nameMap, symbol,
				new BigDecimal(rate), formatPatternMap, maxFractionDigits,
				minFractionDigits, roundingMode, primary, priority, active);
		}
		else {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceCurrency.class.getName(), actionRequest);

			commerceCurrency = _commerceCurrencyService.updateCommerceCurrency(
				externalReferenceCode, commerceCurrencyId, nameMap, symbol,
				new BigDecimal(rate), formatPatternMap, maxFractionDigits,
				minFractionDigits, roundingMode, primary, priority, active,
				serviceContext);
		}

		return commerceCurrency;
	}

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Localization _localization;

}