/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.batch.engine.v1_0;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.AnalyticsUserContentRecommendation;
import com.liferay.analytics.machine.learning.content.UserContentRecommendation;
import com.liferay.analytics.machine.learning.content.UserContentRecommendationManager;
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
public class AnalyticsUserContentRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate
		<AnalyticsUserContentRecommendation> {

	@Override
	public AnalyticsUserContentRecommendation createItem(
			AnalyticsUserContentRecommendation
				analyticsUserContentRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		UserContentRecommendation userContentRecommendation =
			new UserContentRecommendation();

		userContentRecommendation.setAssetCategoryIds(
			userContentRecommendation.getAssetCategoryIds());
		userContentRecommendation.setCompanyId(contextCompany.getCompanyId());
		userContentRecommendation.setCreateDate(
			analyticsUserContentRecommendation.getCreateDate());
		userContentRecommendation.setEntryClassPK(
			analyticsUserContentRecommendation.getUserId());
		userContentRecommendation.setJobId(
			analyticsUserContentRecommendation.getJobId());
		userContentRecommendation.setRecommendedEntryClassPK(
			analyticsUserContentRecommendation.getRecommendedAssetEntryId());
		userContentRecommendation.setScore(
			analyticsUserContentRecommendation.getScore());

		_userContentRecommendationManager.addUserContentRecommendation(
			userContentRecommendation);

		return null;
	}

	@Override
	public Class<AnalyticsUserContentRecommendation> getItemClass() {
		return AnalyticsUserContentRecommendation.class;
	}

	@Override
	public Page<AnalyticsUserContentRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private UserContentRecommendationManager _userContentRecommendationManager;

}