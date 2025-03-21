/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.info.collection.provider;

import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendationManager;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	configurationPid = "com.liferay.commerce.machine.learning.internal.recommendation.configuration.CommerceMLRecommendationsCollectionProviderConfiguration",
	service = InfoCollectionProvider.class
)
public class UserCommerceMLRecommendationInfoItemCollectionProvider
	extends BaseCommerceMLRecommendationCollectionProvider
	implements InfoCollectionProvider<CPDefinition> {

	@Override
	public InfoPage<CPDefinition> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		Object relatedItem = collectionQuery.getRelatedItem();

		Pagination pagination = collectionQuery.getPagination();

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			if (commerceContext == null) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry == null) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			long[] assetCategoryIds = null;

			if (relatedItem != null) {
				CPDefinition cpDefinition = (CPDefinition)relatedItem;

				AssetEntry assetEntry = assetEntryLocalService.getEntry(
					CPDefinition.class.getName(),
					cpDefinition.getCPDefinitionId());

				assetCategoryIds = assetEntry.getCategoryIds();
			}

			List<UserCommerceMLRecommendation> userCommerceMLRecommendations =
				userCommerceMLRecommendationManager.
					getUserCommerceMLRecommendations(
						accountEntry.getCompanyId(),
						accountEntry.getAccountEntryId(), assetCategoryIds);

			if (userCommerceMLRecommendations.isEmpty()) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			return InfoPage.of(
				TransformUtil.transform(
					ListUtil.subList(
						userCommerceMLRecommendations, pagination.getStart(),
						pagination.getEnd()),
					userCommerceMLRecommendation -> {
						try {
							return cpDefinitionService.fetchCPDefinition(
								userCommerceMLRecommendation.
									getRecommendedEntryClassPK());
						}
						catch (PortalException portalException) {
							_log.error(portalException);
						}

						return null;
					}),
				collectionQuery.getPagination(),
				userCommerceMLRecommendations.size());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return InfoPage.of(Collections.emptyList(), pagination, 0);
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "user-personalized-recommendations");
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference(unbind = "-")
	protected CPDefinitionService cpDefinitionService;

	@Reference
	protected Language language;

	@Reference(unbind = "-")
	protected UserCommerceMLRecommendationManager
		userCommerceMLRecommendationManager;

	private static final Log _log = LogFactoryUtil.getLog(
		UserCommerceMLRecommendationInfoItemCollectionProvider.class);

}