/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecast;
import com.liferay.commerce.machine.learning.forecast.SkuCommerceMLForecastManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "batch.engine.task.item.delegate.name=" + SkuForecastBatchEngineTaskItemDelegate.KEY,
	scope = ServiceScope.PROTOTYPE, service = BatchEngineTaskItemDelegate.class
)
public class SkuForecastBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<SkuForecast> {

	public static final String KEY = "sku-monthly-quantity-forecast";

	@Override
	public SkuForecast createItem(
			SkuForecast skuForecast, Map<String, Serializable> parameters)
		throws Exception {

		SkuCommerceMLForecast skuCommerceMLForecast =
			_skuCommerceMLForecastManager.create();

		if (skuForecast.getActual() != null) {
			skuCommerceMLForecast.setActual(skuForecast.getActual());
		}

		skuCommerceMLForecast.setCompanyId(contextCompany.getCompanyId());

		if (skuForecast.getForecast() != null) {
			skuCommerceMLForecast.setForecast(skuForecast.getForecast());
			skuCommerceMLForecast.setForecastLowerBound(
				skuForecast.getForecastLowerBound());
			skuCommerceMLForecast.setForecastUpperBound(
				skuForecast.getForecastUpperBound());
		}

		skuCommerceMLForecast.setPeriod("month");
		skuCommerceMLForecast.setScope("sku");
		skuCommerceMLForecast.setSku(skuForecast.getSku());
		skuCommerceMLForecast.setTarget("quantity");
		skuCommerceMLForecast.setTimestamp(skuForecast.getTimestamp());

		_skuCommerceMLForecastManager.addSkuCommerceMLForecast(
			skuCommerceMLForecast);

		return null;
	}

	@Override
	public Class<SkuForecast> getItemClass() {
		return SkuForecast.class;
	}

	@Override
	public Page<SkuForecast> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private SkuCommerceMLForecastManager _skuCommerceMLForecastManager;

}