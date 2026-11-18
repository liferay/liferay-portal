/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.resource.v1_0;

import com.liferay.commerce.machine.learning.forecast.AssetCategoryCommerceMLForecast;
import com.liferay.commerce.machine.learning.forecast.AssetCategoryCommerceMLForecastManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.AccountCategoryForecast;
import com.liferay.headless.commerce.machine.learning.internal.constants.CommerceMLForecastConstants;
import com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter.CommerceMLForecastCompositeResourcePrimaryKey;
import com.liferay.headless.commerce.machine.learning.internal.helper.v1_0.CommerceAccountPermissionHelper;
import com.liferay.headless.commerce.machine.learning.internal.security.permission.CommerceMLPermissionUtil;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.AccountCategoryForecastResource;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-category-forecast.properties",
	scope = ServiceScope.PROTOTYPE,
	service = AccountCategoryForecastResource.class
)
public class AccountCategoryForecastResourceImpl
	extends BaseAccountCategoryForecastResourceImpl {

	@Override
	public void create(
			Collection<AccountCategoryForecast> accountCategoryForecasts,
			Map<String, Serializable> parameters)
		throws Exception {

		CommerceMLPermissionUtil.check(
			contextCompany.getCompanyId(), roleLocalService);

		contextBatchUnsafeBiConsumer.accept(
			accountCategoryForecasts,
			accountCategoryForecast -> {
				AssetCategoryCommerceMLForecast
					assetCategoryCommerceMLForecast =
						_assetCategoryCommerceMLForecastManager.create();

				if (accountCategoryForecast.getActual() != null) {
					assetCategoryCommerceMLForecast.setActual(
						accountCategoryForecast.getActual());
				}

				assetCategoryCommerceMLForecast.setAssetCategoryId(
					accountCategoryForecast.getCategory());
				assetCategoryCommerceMLForecast.setCommerceAccountId(
					accountCategoryForecast.getAccount());
				assetCategoryCommerceMLForecast.setCompanyId(
					contextCompany.getCompanyId());
				assetCategoryCommerceMLForecast.setForecast(
					accountCategoryForecast.getForecast());
				assetCategoryCommerceMLForecast.setForecastLowerBound(
					accountCategoryForecast.getForecastLowerBound());
				assetCategoryCommerceMLForecast.setForecastUpperBound(
					accountCategoryForecast.getForecastUpperBound());
				assetCategoryCommerceMLForecast.setPeriod("month");
				assetCategoryCommerceMLForecast.setScope("asset-category");
				assetCategoryCommerceMLForecast.setTarget("revenue");
				assetCategoryCommerceMLForecast.setTimestamp(
					accountCategoryForecast.getTimestamp());

				_assetCategoryCommerceMLForecastManager.
					addAssetCategoryCommerceMLForecast(
						assetCategoryCommerceMLForecast);

				return null;
			});
	}

	@Override
	public Page<AccountCategoryForecast>
			getAccountCategoryForecastsByMonthlyRevenuePage(
				Long[] accountIds, Long[] categoryIds, Integer forecastLength,
				Date forecastStartDate, Integer historyLength,
				Pagination pagination)
		throws Exception {

		List<Long> commerceAccountIds =
			_commerceAccountPermissionHelper.filterCommerceAccountIds(
				Arrays.asList(accountIds));

		if (commerceAccountIds.isEmpty()) {
			return Page.of(Collections.emptyList());
		}

		Date startDate = forecastStartDate;

		if (startDate == null) {
			startDate = new Date();
		}

		if (historyLength == null) {
			historyLength = CommerceMLForecastConstants.HISTORY_LENGTH_DEFAULT;
		}

		if (forecastLength == null) {
			forecastLength =
				CommerceMLForecastConstants.FORECAST_LENGTH_DEFAULT;
		}

		return Page.of(
			transform(
				_assetCategoryCommerceMLForecastManager.
					getMonthlyRevenueAssetCategoryCommerceMLForecasts(
						contextCompany.getCompanyId(),
						ArrayUtil.toArray(categoryIds),
						ArrayUtil.toLongArray(commerceAccountIds), startDate,
						historyLength, forecastLength,
						pagination.getStartPosition(),
						pagination.getEndPosition()),
				assetCategoryCommerceMLForecast ->
					_accountCategoryForecastDTOConverter.toDTO(
						new DefaultDTOConverterContext(
							new CommerceMLForecastCompositeResourcePrimaryKey(
								assetCategoryCommerceMLForecast.getCompanyId(),
								assetCategoryCommerceMLForecast.
									getForecastId()),
							contextAcceptLanguage.getPreferredLocale()))),
			pagination,
			_assetCategoryCommerceMLForecastManager.
				getMonthlyRevenueAssetCategoryCommerceMLForecastsCount(
					contextCompany.getCompanyId(),
					ArrayUtil.toArray(categoryIds),
					ArrayUtil.toLongArray(commerceAccountIds), startDate,
					historyLength, forecastLength));
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter.AccountCategoryForecastDTOConverter)"
	)
	private DTOConverter
		<AssetCategoryCommerceMLForecast, AccountCategoryForecast>
			_accountCategoryForecastDTOConverter;

	@Reference
	private AssetCategoryCommerceMLForecastManager
		_assetCategoryCommerceMLForecastManager;

	@Reference
	private CommerceAccountPermissionHelper _commerceAccountPermissionHelper;

}