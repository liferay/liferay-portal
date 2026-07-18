/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.impl;

import com.liferay.commerce.currency.constants.CommerceCurrencyActionKeys;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.base.CommerceCurrencyServiceBaseImpl;
import com.liferay.commerce.currency.util.comparator.CommerceCurrencyPriorityComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceCurrency"
	},
	service = AopService.class
)
public class CommerceCurrencyServiceImpl
	extends CommerceCurrencyServiceBaseImpl {

	@Override
	public CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, String code,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyLocalService.addCommerceCurrency(
			externalReferenceCode, getUserId(), code, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active);
	}

	@Override
	public void deleteCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		commerceCurrencyLocalService.deleteCommerceCurrency(commerceCurrencyId);
	}

	@Override
	public CommerceCurrency fetchCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyLocalService.
			fetchCommerceCurrencyByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommerceCurrency fetchPrimaryCommerceCurrency(long companyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.fetchByC_P_A_First(
			companyId, true, true,
			CommerceCurrencyPriorityComparator.getInstance(false));
	}

	@Override
	public List<CommerceCurrency> getCommerceCurrencies(
			long companyId, boolean active, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.findByC_A(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommerceCurrency> getCommerceCurrencies(
			long companyId, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId, boolean active)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.countByC_A(companyId, active);
	}

	@Override
	public CommerceCurrency getCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.findByPrimaryKey(commerceCurrencyId);
	}

	@Override
	public CommerceCurrency getCommerceCurrency(long companyId, String code)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyPersistence.findByC_C(companyId, code);
	}

	@Override
	public BaseModelSearchResult<CommerceCurrency> searchCommerceCurrencies(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException {

		return commerceCurrencyLocalService.searchCommerceCurrencies(
			companyId, keywords, params, start, end, sort);
	}

	@Override
	public CommerceCurrency setActive(long commerceCurrencyId, boolean active)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyLocalService.setActive(
			commerceCurrencyId, active);
	}

	@Override
	public CommerceCurrency setPrimary(long commerceCurrencyId, boolean primary)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyLocalService.setPrimary(
			commerceCurrencyId, primary);
	}

	@Override
	public CommerceCurrency updateCommerceCurrency(
			String externalReferenceCode, long commerceCurrencyId,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		return commerceCurrencyLocalService.updateCommerceCurrency(
			externalReferenceCode, commerceCurrencyId, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active, serviceContext);
	}

	@Override
	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		commerceCurrencyLocalService.updateExchangeRate(
			commerceCurrencyId, exchangeRateProviderKey);
	}

	@Override
	public void updateExchangeRates() throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);

		commerceCurrencyLocalService.updateExchangeRates();
	}

	@Reference(
		target = "(resource.name=" + CommerceCurrencyConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}