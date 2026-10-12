/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.info.collection.provider;

import com.liferay.commerce.machine.learning.recommendation.FrequentPatternCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.FrequentPatternCommerceMLRecommendationManager;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	configurationPid = "com.liferay.commerce.machine.learning.internal.recommendation.configuration.CommerceMLRecommendationsCollectionProviderConfiguration",
	service = RelatedInfoItemCollectionProvider.class
)
public class
	FrequentPatternCommerceMLRecommendationRelatedInfoItemCollectionProvider
		extends BaseCommerceMLRecommendationCollectionProvider
		implements RelatedInfoItemCollectionProvider
			<CPDefinition, CPDefinition> {

	@Override
	public InfoPage<CPDefinition> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		Object relatedItem = collectionQuery.getRelatedItem();

		Pagination pagination = collectionQuery.getPagination();

		if (!(relatedItem instanceof CPDefinition)) {
			return InfoPage.of(Collections.emptyList(), pagination, 0);
		}

		CPDefinition cpDefinition = (CPDefinition)relatedItem;

		try {
			if (!isGuestBrowsingEnabled()) {
				return InfoPage.of(Collections.emptyList(), pagination, 0);
			}

			List<FrequentPatternCommerceMLRecommendation>
				frequentPatternCommerceMLRecommendations =
					_frequentPatternCommerceMLRecommendationManager.
						getFrequentPatternCommerceMLRecommendations(
							cpDefinition.getCompanyId(),
							new long[] {cpDefinition.getCPDefinitionId()});

			if (frequentPatternCommerceMLRecommendations.isEmpty()) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			return InfoPage.of(
				TransformUtil.transform(
					ListUtil.subList(
						frequentPatternCommerceMLRecommendations,
						pagination.getStart(), pagination.getEnd()),
					frequentPatternCommerceMLRecommendation -> {
						try {
							return _cpDefinitionService.fetchCPDefinition(
								frequentPatternCommerceMLRecommendation.
									getRecommendedEntryClassPK());
						}
						catch (PortalException portalException) {
							_log.error(portalException);
						}

						return null;
					}),
				collectionQuery.getPagination(),
				frequentPatternCommerceMLRecommendations.size());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return InfoPage.of(Collections.emptyList(), pagination, 0);
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "also-bought-product-recommendations");
	}

	@Override
	public boolean isAvailable() {
		return commerceMLRecommendationsCollectionProviderConfiguration.
			alsoBoughtProductRecommendationsCollectionProviderEnabled();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrequentPatternCommerceMLRecommendationRelatedInfoItemCollectionProvider.class);

	@Reference(unbind = "-")
	private CPDefinitionService _cpDefinitionService;

	@Reference(unbind = "-")
	private FrequentPatternCommerceMLRecommendationManager
		_frequentPatternCommerceMLRecommendationManager;

	@Reference
	private Language _language;

}