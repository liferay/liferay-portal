/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.machine.learning.recommendation.ProductContentCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.ProductContentCommerceMLRecommendationManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.ProductContentRecommendation;
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
	scope = ServiceScope.PROTOTYPE, service = BatchEngineTaskItemDelegate.class
)
public class ProductContentRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<ProductContentRecommendation> {

	@Override
	public ProductContentRecommendation createItem(
			ProductContentRecommendation productContentRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		ProductContentCommerceMLRecommendation
			productContentCommerceMLRecommendation =
				_productContentCommerceMLRecommendationManager.create();

		productContentCommerceMLRecommendation.setCompanyId(
			contextCompany.getCompanyId());
		productContentCommerceMLRecommendation.setCreateDate(
			productContentRecommendation.getCreateDate());
		productContentCommerceMLRecommendation.setEntryClassPK(
			productContentRecommendation.getProductId());
		productContentCommerceMLRecommendation.setJobId(
			productContentRecommendation.getJobId());
		productContentCommerceMLRecommendation.setRank(
			productContentRecommendation.getRank());
		productContentCommerceMLRecommendation.setRecommendedEntryClassPK(
			productContentRecommendation.getRecommendedProductId());
		productContentCommerceMLRecommendation.setScore(
			productContentRecommendation.getScore());

		_productContentCommerceMLRecommendationManager.
			addProductContentCommerceMLRecommendation(
				productContentCommerceMLRecommendation);

		return null;
	}

	@Override
	public Page<ProductContentRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private ProductContentCommerceMLRecommendationManager
		_productContentCommerceMLRecommendationManager;

}