/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.url.parameters;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.util.URLParameterUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteHelper;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class EntryIdSimilarResultsContributor
	implements SimilarResultsContributor {

	public static final String ENTRY_ID = "entryId";

	public EntryIdSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		routeBuilder.addAttribute(
			ENTRY_ID,
			Long.valueOf(
				URLParameterUtil.getPortletIdParameter(
					HttpComponentsUtil.decodePath(routeHelper.getURLString()),
					ENTRY_ID)));
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder criteriaBuilder, CriteriaHelper criteriaHelper) {

		Long entryId = (Long)criteriaHelper.getRouteParameter(ENTRY_ID);

		AssetEntry assetEntry = _assetEntryLocalService.fetchAssetEntry(
			entryId);

		criteriaBuilder.uid(
			Field.getUID(
				assetEntry.getClassName(),
				String.valueOf(assetEntry.getClassPK())));
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		AssetEntry assetEntry = destinationHelper.getAssetEntry();

		destinationBuilder.replaceParameter(
			ENTRY_ID, String.valueOf(assetEntry.getEntryId()));
	}

	private final AssetEntryLocalService _assetEntryLocalService;

}