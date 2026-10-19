/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.machine.learning.recommendation.FrequentPatternCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.FrequentPatternCommerceMLRecommendationManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.FrequentPatternRecommendation;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;

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
public class FrequentPatternRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<FrequentPatternRecommendation> {

	@Override
	public FrequentPatternRecommendation createItem(
			FrequentPatternRecommendation frequentPatternRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		FrequentPatternCommerceMLRecommendation
			frequentPatternCommerceMLRecommendation =
				_frequentPatternCommerceMLRecommendationManager.create();

		frequentPatternCommerceMLRecommendation.setAntecedentIds(
			ArrayUtil.toArray(
				frequentPatternRecommendation.getAntecedentIds()));
		frequentPatternCommerceMLRecommendation.setAntecedentIdsLength(
			frequentPatternRecommendation.getAntecedentIdsLength());
		frequentPatternCommerceMLRecommendation.setCompanyId(
			contextCompany.getCompanyId());
		frequentPatternCommerceMLRecommendation.setCreateDate(
			frequentPatternRecommendation.getCreateDate());
		frequentPatternCommerceMLRecommendation.setJobId(
			frequentPatternRecommendation.getJobId());
		frequentPatternCommerceMLRecommendation.setRecommendedEntryClassPK(
			frequentPatternRecommendation.getRecommendedProductId());
		frequentPatternCommerceMLRecommendation.setScore(
			frequentPatternRecommendation.getScore());

		_frequentPatternCommerceMLRecommendationManager.
			addFrequentPatternCommerceMLRecommendation(
				frequentPatternCommerceMLRecommendation);

		return null;
	}

	@Override
	public Page<FrequentPatternRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private FrequentPatternCommerceMLRecommendationManager
		_frequentPatternCommerceMLRecommendationManager;

}