/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0;

import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendationManager;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.UserRecommendation;
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
public class UserRecommendationBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<UserRecommendation> {

	@Override
	public UserRecommendation createItem(
			UserRecommendation userRecommendation,
			Map<String, Serializable> parameters)
		throws Exception {

		UserCommerceMLRecommendation userCommerceMLRecommendation =
			_userCommerceMLRecommendationManager.create();

		userCommerceMLRecommendation.setAssetCategoryIds(
			ArrayUtil.toArray(userRecommendation.getAssetCategoryIds()));
		userCommerceMLRecommendation.setCompanyId(
			contextCompany.getCompanyId());
		userCommerceMLRecommendation.setCreateDate(
			userRecommendation.getCreateDate());
		userCommerceMLRecommendation.setEntryClassPK(
			userRecommendation.getProductId());
		userCommerceMLRecommendation.setJobId(userRecommendation.getJobId());
		userCommerceMLRecommendation.setRecommendedEntryClassPK(
			userRecommendation.getRecommendedProductId());
		userCommerceMLRecommendation.setScore(userRecommendation.getScore());

		_userCommerceMLRecommendationManager.addUserCommerceMLRecommendation(
			userCommerceMLRecommendation);

		return null;
	}

	@Override
	public Page<UserRecommendation> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private UserCommerceMLRecommendationManager
		_userCommerceMLRecommendationManager;

}