/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.data.source;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.machine.learning.recommendation.ProductInteractionCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.ProductInteractionCommerceMLRecommendationManager;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
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
	property = "commerce.product.data.source.name=" + ProductInteractionCommerceMLRecommendationCPDataSourceImpl.NAME,
	service = CPDataSource.class
)
public class ProductInteractionCommerceMLRecommendationCPDataSourceImpl
	extends BaseCommerceMLRecommendationCPDataSource {

	public static final String NAME =
		"productInteractionCommerceMLRecommendationDataSource";

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			getResourceBundle(locale),
			"product-interaction-based-recommendations");
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

		if (cpCatalogEntry == null) {
			return new CPDataSourceResult(Collections.emptyList(), 0);
		}

		List<ProductInteractionCommerceMLRecommendation>
			productInteractionCommerceMLRecommendations =
				_productInteractionCommerceMLRecommendationManager.
					getProductInteractionCommerceMLRecommendations(
						portal.getCompanyId(httpServletRequest),
						cpCatalogEntry.getCPDefinitionId());

		if (productInteractionCommerceMLRecommendations.isEmpty()) {
			return new CPDataSourceResult(Collections.emptyList(), 0);
		}

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		List<ProductInteractionCommerceMLRecommendation>
			productInteractionCommerceMLRecommendationList = ListUtil.subList(
				productInteractionCommerceMLRecommendations, start, end);

		for (ProductInteractionCommerceMLRecommendation
				productInteractionCommerceMLRecommendation :
					productInteractionCommerceMLRecommendationList) {

			long recommendedEntryClassPK =
				productInteractionCommerceMLRecommendation.
					getRecommendedEntryClassPK();

			if (_log.isTraceEnabled()) {
				_log.trace(
					StringBundler.concat(
						"Recommended item: ", recommendedEntryClassPK,
						" rank: ",
						productInteractionCommerceMLRecommendation.getRank(),
						" score: ",
						productInteractionCommerceMLRecommendation.getScore()));
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
			cpCatalogEntries,
			productInteractionCommerceMLRecommendations.size());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductInteractionCommerceMLRecommendationCPDataSourceImpl.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private Language _language;

	@Reference(unbind = "-")
	private ProductInteractionCommerceMLRecommendationManager
		_productInteractionCommerceMLRecommendationManager;

}