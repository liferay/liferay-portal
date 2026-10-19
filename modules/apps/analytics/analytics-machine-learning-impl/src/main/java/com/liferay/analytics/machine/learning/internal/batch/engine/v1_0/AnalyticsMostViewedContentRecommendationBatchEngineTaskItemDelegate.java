/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.batch.engine.v1_0;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.AnalyticsMostViewedContentRecommendation;
import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendation;
import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendationManager;
import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
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
public class AnalyticsMostViewedContentRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate
		<AnalyticsMostViewedContentRecommendation> {

	@Override
	public AnalyticsMostViewedContentRecommendation createItem(
			AnalyticsMostViewedContentRecommendation
				analyticsMostViewedContentRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		MostViewedContentRecommendation mostViewedContentRecommendation =
			new MostViewedContentRecommendation();

		mostViewedContentRecommendation.setAssetCategoryIds(
			mostViewedContentRecommendation.getAssetCategoryIds());
		mostViewedContentRecommendation.setCompanyId(
			contextCompany.getCompanyId());
		mostViewedContentRecommendation.setCreateDate(
			analyticsMostViewedContentRecommendation.getCreateDate());
		mostViewedContentRecommendation.setJobId(
			analyticsMostViewedContentRecommendation.getJobId());
		mostViewedContentRecommendation.setRecommendedEntryClassPK(
			analyticsMostViewedContentRecommendation.
				getRecommendedAssetEntryId());
		mostViewedContentRecommendation.setScore(
			analyticsMostViewedContentRecommendation.getScore());

		_mostViewedContentRecommendationManager.
			addMostViewedContentRecommendation(mostViewedContentRecommendation);

		return null;
	}

	@Override
	public Class<AnalyticsMostViewedContentRecommendation> getItemClass() {
		return AnalyticsMostViewedContentRecommendation.class;
	}

	@Override
	public Page<AnalyticsMostViewedContentRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private MostViewedContentRecommendationManager
		_mostViewedContentRecommendationManager;

}