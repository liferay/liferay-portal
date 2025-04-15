/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.data.source;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.machine.learning.recommendation.ProductContentCommerceMLRecommendation;
import com.liferay.commerce.machine.learning.recommendation.ProductContentCommerceMLRecommendationManager;
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
	property = "commerce.product.data.source.name=" + ProductContentCommerceMLRecommendationCPDataSourceImpl.NAME,
	service = CPDataSource.class
)
public class ProductContentCommerceMLRecommendationCPDataSourceImpl
	extends BaseCommerceMLRecommendationCPDataSource {

	public static final String NAME =
		"productContentCommerceMLRecommendationDataSource";

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			getResourceBundle(locale), "product-content-based-recommendations");
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

		List<ProductContentCommerceMLRecommendation>
			productContentCommerceMLRecommendations =
				_productContentCommerceMLRecommendationManager.
					getProductContentCommerceMLRecommendations(
						portal.getCompanyId(httpServletRequest),
						cpCatalogEntry.getCPDefinitionId());

		if (productContentCommerceMLRecommendations.isEmpty()) {
			return new CPDataSourceResult(Collections.emptyList(), 0);
		}

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		List<ProductContentCommerceMLRecommendation>
			productContentCommerceMLRecommendationList = ListUtil.subList(
				productContentCommerceMLRecommendations, start, end);

		for (ProductContentCommerceMLRecommendation
				productContentCommerceMLRecommendation :
					productContentCommerceMLRecommendationList) {

			long recommendedEntryClassPK =
				productContentCommerceMLRecommendation.
					getRecommendedEntryClassPK();

			if (_log.isTraceEnabled()) {
				_log.trace(
					StringBundler.concat(
						"Recommended item: ", recommendedEntryClassPK,
						" rank: ",
						productContentCommerceMLRecommendation.getRank(),
						" score: ",
						productContentCommerceMLRecommendation.getScore()));
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
			cpCatalogEntries, productContentCommerceMLRecommendations.size());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductContentCommerceMLRecommendationCPDataSourceImpl.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private Language _language;

	@Reference(unbind = "-")
	private ProductContentCommerceMLRecommendationManager
		_productContentCommerceMLRecommendationManager;

}