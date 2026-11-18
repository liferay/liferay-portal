/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.resource.v1_0;

import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecast;
import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecastManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast;
import com.liferay.headless.commerce.machine.learning.internal.constants.CommerceMLForecastConstants;
import com.liferay.headless.commerce.machine.learning.internal.security.permission.CommerceMLPermissionUtil;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.SkuForecastResource;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/sku-forecast.properties",
	scope = ServiceScope.PROTOTYPE, service = SkuForecastResource.class
)
public class SkuForecastResourceImpl extends BaseSkuForecastResourceImpl {

	@Override
	public Page<SkuForecast> getSkuForecastsByMonthlyRevenuePage(
			Integer forecastLength, Date forecastStartDate,
			Integer historyLength, String[] skus, Pagination pagination)
		throws Exception {

		CommerceMLPermissionUtil.check(
			contextCompany.getCompanyId(), roleLocalService);

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
				_skuCommerceMLForecastManager.
					getMonthlyQuantitySkuCommerceMLForecasts(
						contextCompany.getCompanyId(), skus, startDate,
						historyLength, forecastLength,
						pagination.getStartPosition(),
						pagination.getEndPosition()),
				skuCommerceMLForecast -> _skuForecastDTOConverter.toDTO(
					skuCommerceMLForecast)),
			pagination,
			_skuCommerceMLForecastManager.
				getMonthlyQuantitySkuCommerceMLForecastsCount(
					contextCompany.getCompanyId(), skus, startDate,
					historyLength, forecastLength));
	}

	@Reference
	private SkuCommerceMLForecastManager _skuCommerceMLForecastManager;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter.SkuForecastDTOConverter)"
	)
	private DTOConverter<SkuCommerceMLForecast, SkuForecast>
		_skuForecastDTOConverter;

}