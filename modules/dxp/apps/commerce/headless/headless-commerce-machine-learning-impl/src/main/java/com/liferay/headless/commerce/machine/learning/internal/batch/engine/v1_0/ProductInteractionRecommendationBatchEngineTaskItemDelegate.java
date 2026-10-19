/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.machine.learning.recommendation.ProductInteractionCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.ProductInteractionCommerceMLRecommendationManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.ProductInteractionRecommendation;
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
public class ProductInteractionRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<ProductInteractionRecommendation> {

	@Override
	public ProductInteractionRecommendation createItem(
			ProductInteractionRecommendation productInteractionRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		ProductInteractionCommerceMLRecommendation
			productInteractionCommerceMLRecommendation =
				_productInteractionCommerceMLRecommendationManager.create();

		productInteractionCommerceMLRecommendation.setCompanyId(
			contextCompany.getCompanyId());
		productInteractionCommerceMLRecommendation.setCreateDate(
			productInteractionRecommendation.getCreateDate());
		productInteractionCommerceMLRecommendation.setEntryClassPK(
			productInteractionRecommendation.getProductId());
		productInteractionCommerceMLRecommendation.setJobId(
			productInteractionRecommendation.getJobId());
		productInteractionCommerceMLRecommendation.setRank(
			productInteractionRecommendation.getRank());
		productInteractionCommerceMLRecommendation.setRecommendedEntryClassPK(
			productInteractionRecommendation.getRecommendedProductId());
		productInteractionCommerceMLRecommendation.setScore(
			productInteractionRecommendation.getScore());

		_productInteractionCommerceMLRecommendationManager.
			addProductInteractionCommerceMLRecommendation(
				productInteractionCommerceMLRecommendation);

		return null;
	}

	@Override
	public Page<ProductInteractionRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private ProductInteractionCommerceMLRecommendationManager
		_productInteractionCommerceMLRecommendationManager;

}