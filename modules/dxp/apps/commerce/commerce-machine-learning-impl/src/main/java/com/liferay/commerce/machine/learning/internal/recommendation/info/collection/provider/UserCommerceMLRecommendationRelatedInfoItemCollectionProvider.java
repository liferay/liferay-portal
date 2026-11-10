/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.info.collection.provider;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Riccardo Ferrari
 */
@Component(
	configurationPid = "com.liferay.commerce.machine.learning.internal.recommendation.configuration.CommerceMLRecommendationsCollectionProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = RelatedInfoItemCollectionProvider.class
)
public class UserCommerceMLRecommendationRelatedInfoItemCollectionProvider
	extends UserCommerceMLRecommendationInfoItemCollectionProvider
	implements RelatedInfoItemCollectionProvider<CPDefinition, CPDefinition> {

	@Override
	public boolean isAvailable() {
		return commerceMLRecommendationsCollectionProviderConfiguration.
			userPersonalizedRecommendationsCollectionProviderEnabled();
	}

}