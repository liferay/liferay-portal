/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.data.source;

import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.UserCommerceMLRecommendationManager;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "commerce.product.data.source.name=" + UserCommerceMLRecommendationCPDataSourceImpl.NAME,
	service = CPDataSource.class
)
public class UserCommerceMLRecommendationCPDataSourceImpl
	extends BaseCommerceMLRecommendationCPDataSource {

	public static final String NAME = "userCommerceMLRecommendationDataSource";

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			getResourceBundle(locale),
			"user-interaction-based-product-recommendations");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {

		long groupId = portal.getScopeGroupId(httpServletRequest);

		AccountEntry accountEntry =
			commerceAccountHelper.getCurrentAccountEntry(
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				httpServletRequest);

		if (accountEntry == null) {
			return new CPDataSourceResult(Collections.emptyList(), 0);
		}

		CPCatalogEntry cpCatalogEntry =
			(CPCatalogEntry)httpServletRequest.getAttribute(
				CPWebKeys.CP_CATALOG_ENTRY);

		long[] categoryIds = null;

		if (cpCatalogEntry != null) {
			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				CPDefinition.class.getName(),
				cpCatalogEntry.getCPDefinitionId());

			categoryIds = assetEntry.getCategoryIds();
		}

		List<UserCommerceMLRecommendation> userCommerceMLRecommendations =
			_userCommerceMLRecommendationManager.
				getUserCommerceMLRecommendations(
					portal.getCompanyId(httpServletRequest),
					accountEntry.getAccountEntryId(), categoryIds);

		if (userCommerceMLRecommendations.isEmpty()) {
			return new CPDataSourceResult(Collections.emptyList(), 0);
		}

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		List<UserCommerceMLRecommendation> userCommerceMLRecommendationList =
			ListUtil.subList(userCommerceMLRecommendations, start, end);

		for (UserCommerceMLRecommendation userCommerceMLRecommendation :
				userCommerceMLRecommendationList) {

			long recommendedEntryClassPK =
				userCommerceMLRecommendation.getRecommendedEntryClassPK();

			if (_log.isTraceEnabled()) {
				_log.trace(
					StringBundler.concat(
						"Recommended item: ", recommendedEntryClassPK,
						" score: ", userCommerceMLRecommendation.getScore()));
			}

			try {
				CPCatalogEntry recommendedCPCatalogEntry =
					cpDefinitionHelper.getCPCatalogEntry(
						accountEntry.getAccountEntryId(), groupId,
						recommendedEntryClassPK,
						portal.getLocale(httpServletRequest));

				cpCatalogEntries.add(recommendedCPCatalogEntry);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return new CPDataSourceResult(
			cpCatalogEntries, userCommerceMLRecommendations.size());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserCommerceMLRecommendationCPDataSourceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private Language _language;

	@Reference
	private UserCommerceMLRecommendationManager
		_userCommerceMLRecommendationManager;

}