/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter;

import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecast;
import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecastManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecast"
	},
	service = DTOConverter.class
)
public class SkuForecastDTOConverter
	implements DTOConverter<SkuCommerceMLForecast, SkuForecast> {

	@Override
	public String getContentType() {
		return SkuForecast.class.getSimpleName();
	}

	@Override
	public SkuCommerceMLForecast getObject(String externalReferenceCode)
		throws Exception {

		return _skuCommerceMLForecastManager.getSkuCommerceMLForecast(
			CompanyThreadLocal.getCompanyId(),
			GetterUtil.getLong(externalReferenceCode));
	}

	@Override
	public SkuForecast toDTO(
			DTOConverterContext dtoConverterContext,
			SkuCommerceMLForecast skuCommerceMLForecast)
		throws Exception {

		if (skuCommerceMLForecast == null) {
			return null;
		}

		return new SkuForecast() {
			{
				setActual(skuCommerceMLForecast::getActual);
				setForecast(skuCommerceMLForecast::getForecast);
				setForecastLowerBound(
					skuCommerceMLForecast::getForecastLowerBound);
				setForecastUpperBound(
					skuCommerceMLForecast::getForecastUpperBound);
				setSku(skuCommerceMLForecast::getSku);
				setTimestamp(skuCommerceMLForecast::getTimestamp);
				setUnit(skuCommerceMLForecast::getTarget);
			}
		};
	}

	@Reference
	private SkuCommerceMLForecastManager _skuCommerceMLForecastManager;

}